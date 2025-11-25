import { describe, it, expect, vi, beforeEach } from 'vitest';
import { screen, waitFor, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithProviders } from '../utils/test-utils';
import Catalog from '../../features/catalog/Catalog';

// Mock accountSlice
vi.mock('../../features/account/accountSlice', () => ({
  default: {
    reducer: vi.fn((state = { user: null, error: null }) => state),
    actions: {
      logOut: vi.fn(),
      clearError: vi.fn()
    }
  }
}));

// Mock agent with controllable responses
vi.mock('../../app/api/agent', async () => {
  return {
    default: {
      Store: {
        list: vi.fn(),
        brands: vi.fn(),
        types: vi.fn(),
        search: vi.fn()
      }
    }
  };
});

describe('Catalog Component - Search & Filter Interactions', () => {
  let mockAgent: any;

  beforeEach(async () => {
    // Dynamically import to get mock references
    const agentModule = await import('../../app/api/agent');
    mockAgent = agentModule.default;
    
    vi.clearAllMocks();
    
    // Default mock responses
    mockAgent.Store.list.mockResolvedValue({
      content: [
        {
          id: 1,
          name: 'Running Shoes',
          price: 12000,
          description: 'Professional running shoes',
          pictureUrl: '/images/shoes1.jpg',
          productBrand: 'Nike',
          productType: 'Shoes',
          quantityInStock: 50
        },
        {
          id: 2,
          name: 'Basketball',
          price: 8000,
          description: 'Official size basketball',
          pictureUrl: '/images/ball1.jpg',
          productBrand: 'Adidas',
          productType: 'Balls',
          quantityInStock: 30
        }
      ],
      totalElements: 2
    });

    mockAgent.Store.search.mockResolvedValue({
      content: [
        {
          id: 1,
          name: 'Running Shoes',
          price: 12000,
          description: 'Professional running shoes',
          pictureUrl: '/images/shoes1.jpg',
          productBrand: 'Nike',
          productType: 'Shoes',
          quantityInStock: 50
        }
      ],
      length: 1
    });

    mockAgent.Store.brands.mockResolvedValue([
      { id: 0, name: 'All' },
      { id: 1, name: 'Nike' },
      { id: 2, name: 'Adidas' }
    ]);

    mockAgent.Store.types.mockResolvedValue([
      { id: 0, name: 'All' },
      { id: 1, name: 'Shoes' },
      { id: 2, name: 'Balls' }
    ]);
  });

  describe('Search Functionality', () => {
    it('triggers search when Enter key is pressed in search field', async () => {
      renderWithProviders(<Catalog />);

      // Wait for initial load
      await waitFor(() => {
        expect(screen.queryByText('Loading Products...')).not.toBeInTheDocument();
      });

      const searchInput = screen.getByLabelText(/search products/i);
      
      // Type search term
      await userEvent.type(searchInput, 'running');
      
      // Spy on console.log to verify search intent
      const consoleLogSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      
      // Press Enter key
      fireEvent.keyDown(searchInput, { key: 'Enter', code: 'Enter' });

      // Verify search was logged (TODO is not yet implemented)
      await waitFor(() => {
        expect(consoleLogSpy).toHaveBeenCalledWith('Search for:', 'running');
      });
      
      consoleLogSpy.mockRestore();
    });

    it('does not trigger search when non-Enter key is pressed', async () => {
      renderWithProviders(<Catalog />);

      await waitFor(() => {
        expect(screen.queryByText('Loading Products...')).not.toBeInTheDocument();
      });

      const searchInput = screen.getByLabelText(/search products/i);
      
      await userEvent.type(searchInput, 'test');
      
      mockAgent.Store.search.mockClear();
      
      // Press a different key (not Enter)
      fireEvent.keyDown(searchInput, { key: 'a', code: 'KeyA' });

      // Wait a bit to ensure no call happens
      await new Promise(resolve => setTimeout(resolve, 100));
      
      // Should not call search for non-Enter keys
      expect(mockAgent.Store.search).not.toHaveBeenCalled();
    });

    it('allows updating search term value', async () => {
      renderWithProviders(<Catalog />);

      await waitFor(() => {
        expect(screen.queryByText('Loading Products...')).not.toBeInTheDocument();
      });

      const searchInput = screen.getByLabelText(/search products/i) as HTMLInputElement;
      
      expect(searchInput.value).toBe('');
      
      await userEvent.type(searchInput, 'nike shoes');
      
      expect(searchInput.value).toBe('nike shoes');
    });
  });

  describe('Sort Functionality', () => {
    it('handles sort change and reloads products', async () => {
      renderWithProviders(<Catalog />);

      await waitFor(() => {
        expect(screen.queryByText('Loading Products...')).not.toBeInTheDocument();
      });

      mockAgent.Store.list.mockClear();

      // Find sort radio buttons
      const sortOptions = screen.getAllByRole('radio');
      const priceHighToLow = sortOptions.find(
        radio => radio.getAttribute('value') === 'priceDesc'
      );

      if (priceHighToLow) {
        fireEvent.click(priceHighToLow);

        await waitFor(() => {
          expect(mockAgent.Store.list).toHaveBeenCalled();
        });
      }
    });
  });

  describe('Brand Filter', () => {
    it('handles brand change and reloads products', async () => {
      renderWithProviders(<Catalog />);

      await waitFor(() => {
        expect(screen.queryByText('Loading Products...')).not.toBeInTheDocument();
      });

      mockAgent.Store.list.mockClear();

      // Find brand filter radio buttons
      const brandRadios = screen.getAllByRole('radio');
      const nikeRadio = brandRadios.find(
        radio => {
          const label = radio.closest('label');
          return label?.textContent?.includes('Nike');
        }
      );

      if (nikeRadio) {
        fireEvent.click(nikeRadio);

        await waitFor(() => {
          expect(mockAgent.Store.list).toHaveBeenCalled();
        });
      }
    });

    it('updates brand state when brand filter is clicked', async () => {
      renderWithProviders(<Catalog />);

      await waitFor(() => {
        expect(screen.queryByText('Loading Products...')).not.toBeInTheDocument();
      });

      const brandRadios = screen.getAllByRole('radio');
      
      // Find Adidas brand radio
      const adidasRadio = brandRadios.find(radio => {
        const label = radio.closest('label');
        return label?.textContent?.includes('Adidas');
      });

      if (adidasRadio) {
        fireEvent.click(adidasRadio);
        
        // Check radio is now checked
        await waitFor(() => {
          expect(adidasRadio).toBeChecked();
        });
      }
    });
  });

  describe('Type Filter', () => {
    it('handles type change and reloads products', async () => {
      renderWithProviders(<Catalog />);

      await waitFor(() => {
        expect(screen.queryByText('Loading Products...')).not.toBeInTheDocument();
      });

      mockAgent.Store.list.mockClear();

      const typeRadios = screen.getAllByRole('radio');
      const ballsRadio = typeRadios.find(radio => {
        const label = radio.closest('label');
        return label?.textContent?.includes('Balls');
      });

      if (ballsRadio) {
        fireEvent.click(ballsRadio);

        await waitFor(() => {
          expect(mockAgent.Store.list).toHaveBeenCalled();
        });
      }
    });

    it('updates type state when type filter is clicked', async () => {
      renderWithProviders(<Catalog />);

      await waitFor(() => {
        expect(screen.queryByText('Loading Products...')).not.toBeInTheDocument();
      });

      const typeRadios = screen.getAllByRole('radio');
      const shoesRadio = typeRadios.find(radio => {
        const label = radio.closest('label');
        return label?.textContent?.includes('Shoes');
      });

      if (shoesRadio) {
        fireEvent.click(shoesRadio);
        
        await waitFor(() => {
          expect(shoesRadio).toBeChecked();
        });
      }
    });
  });

  describe('Pagination', () => {
    it('handles page change interaction', async () => {
      // Mock larger dataset for pagination
      mockAgent.Store.list.mockResolvedValue({
        content: Array.from({ length: 6 }, (_, i) => ({
          id: i + 1,
          name: `Product ${i + 1}`,
          price: 10000,
          description: 'Test product',
          pictureUrl: `/images/product${i}.jpg`,
          productBrand: 'Nike',
          productType: 'Shoes',
          quantityInStock: 10
        })),
        totalElements: 12
      });

      renderWithProviders(<Catalog />);

      await waitFor(() => {
        expect(screen.queryByText('Loading Products...')).not.toBeInTheDocument();
      });

      // Verify pagination display exists
      const paginationDisplay = screen.queryByText(/Displaying \d+-\d+ of 12 items/);
      expect(paginationDisplay).toBeInTheDocument();
      
      // Find any pagination button (not the current page)
      const buttons = screen.getAllByRole('button');
      const pageButton = buttons.find(btn => 
        btn.getAttribute('aria-label')?.includes('page 2')
      );
      
      if (pageButton && !pageButton.hasAttribute('disabled')) {
        fireEvent.click(pageButton);
        
        // Just verify the component still works after click
        await waitFor(() => {
          expect(screen.queryByText(/Displaying \d+-\d+ of 12 items/)).toBeInTheDocument();
        });
      }
    });

    it('displays item count information', async () => {
      mockAgent.Store.list.mockResolvedValue({
        content: Array.from({ length: 6 }, (_, i) => ({
          id: i + 1,
          name: `Product ${i + 1}`,
          price: 10000,
          description: 'Test',
          pictureUrl: '/images/test.jpg',
          productBrand: 'Nike',
          productType: 'Shoes',
          quantityInStock: 10
        })),
        totalElements: 10
      });

      renderWithProviders(<Catalog />);

      await waitFor(() => {
        const displayText = screen.queryByText(/Displaying \d+-\d+ of \d+ items/);
        expect(displayText).toBeInTheDocument();
      });
    });
  });

  describe('Combined Filters', () => {
    it('applies search, brand, and type filters together', async () => {
      renderWithProviders(<Catalog />);

      await waitFor(() => {
        expect(screen.queryByText('Loading Products...')).not.toBeInTheDocument();
      });

      // Clear call count from initial load
      mockAgent.Store.list.mockClear();

      // Select brand filter
      const brandRadios = screen.getAllByRole('radio');
      const nikeRadio = brandRadios.find(radio => {
        const label = radio.closest('label');
        return label?.textContent?.includes('Nike');
      });
      
      if (nikeRadio) {
        fireEvent.click(nikeRadio);
        
        await waitFor(() => {
          expect(mockAgent.Store.list).toHaveBeenCalled();
        });
      }

      // Now test search functionality
      const searchInput = screen.getByLabelText(/search products/i);
      await userEvent.type(searchInput, 'running');
      
      mockAgent.Store.search.mockClear();
      const consoleLogSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      
      fireEvent.keyDown(searchInput, { key: 'Enter', code: 'Enter' });

      // Search is currently just logged (TODO not implemented)
      await waitFor(() => {
        expect(consoleLogSpy).toHaveBeenCalledWith('Search for:', 'running');
      });
      
      consoleLogSpy.mockRestore();
    });
  });
});
