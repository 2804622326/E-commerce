import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import ProductCard from '../../features/catalog/ProductCard';
import { Product } from '../../app/models/product';

vi.mock('../../app/store/configureStore', () => ({
  useAppDispatch: () => vi.fn(),
}));

vi.mock('../../app/api/agent', () => ({
  default: {
    Basket: {
      addItem: vi.fn(),
    },
  },
}));

vi.mock('../../features/basket/basketSlice', () => ({
  setBasket: vi.fn(),
}));

const mockProduct: Product = {
  id: 1,
  name: 'Tennis Racket Pro',
  description: 'Professional tennis racket',
  price: 2500,
  pictureUrl: '/images/products/tennis-racket.jpg',
  productType: 'Equipment',
  productBrand: 'Wilson',
};

describe('ProductCard - Rendering Tests', () => {
  let mockAddItem: any;

  beforeEach(async () => {
    vi.clearAllMocks();
    const agent = await import('../../app/api/agent');
    mockAddItem = agent.default.Basket.addItem;
    mockAddItem.mockResolvedValue({ basket: { id: '123', items: [] } });
  });

  const renderCard = (product: Product) => {
    return render(
      <BrowserRouter>
        <ProductCard product={product} />
      </BrowserRouter>
    );
  };

  it('renders product name', () => {
    renderCard(mockProduct);
    expect(screen.getByText('Tennis Racket Pro')).toBeInTheDocument();
  });

  it('displays brand and type', () => {
    renderCard(mockProduct);
    expect(screen.getByText(/Wilson/)).toBeInTheDocument();
    expect(screen.getByText(/Equipment/)).toBeInTheDocument();
  });

  it('formats price correctly', () => {
    renderCard(mockProduct);
    expect(screen.getByText(/₹2,500\.00/)).toBeInTheDocument();
  });

  it('shows avatar with first letter', () => {
    renderCard(mockProduct);
    expect(screen.getByText('T')).toBeInTheDocument();
  });

  it('has Add to cart button', () => {
    renderCard(mockProduct);
    const button = screen.getByRole('button', { name: /add to cart/i });
    expect(button).toBeInTheDocument();
  });

  it('has View link', () => {
    renderCard(mockProduct);
    const link = screen.getByRole('link', { name: /view/i });
    expect(link).toHaveAttribute('href', '/store/1');
  });

  it('shows loading state when adding item to cart', async () => {
    // Create a deferred promise to control when it resolves
    let resolvePromise: (value: any) => void;
    const promise = new Promise((resolve) => {
      resolvePromise = resolve;
    });
    
    mockAddItem.mockReturnValue(promise);
    
    renderCard(mockProduct);
    
    const addButton = screen.getByRole('button', { name: /add to cart/i });
    fireEvent.click(addButton);
    
    // Should show loading indicator
    await waitFor(() => {
      expect(addButton).toBeDisabled();
    });
    
    // Resolve the promise
    resolvePromise!({ basket: { id: '123', items: [] } });
    
    // Loading should be complete
    await waitFor(() => {
      expect(addButton).not.toBeDisabled();
    });
  });

  it('handles add to cart success', async () => {
    const mockBasket = { id: '123', items: [{ id: 1, quantity: 1 }] };
    mockAddItem.mockResolvedValue({ basket: mockBasket });
    
    renderCard(mockProduct);
    
    const addButton = screen.getByRole('button', { name: /add to cart/i });
    fireEvent.click(addButton);
    
    await waitFor(() => {
      expect(mockAddItem).toHaveBeenCalledWith(mockProduct, expect.any(Function));
    });
  });

  it('handles add to cart error', async () => {
    const consoleLogSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
    mockAddItem.mockRejectedValue(new Error('Failed to add item'));
    
    renderCard(mockProduct);
    
    const addButton = screen.getByRole('button', { name: /add to cart/i });
    fireEvent.click(addButton);
    
    // Wait for error to be logged
    await waitFor(() => {
      expect(consoleLogSpy).toHaveBeenCalled();
    });
    
    // Button should be enabled again after error
    await waitFor(() => {
      expect(addButton).not.toBeDisabled();
    });
    
    consoleLogSpy.mockRestore();
  });

  it('extracts image name and displays image', () => {
    const { container } = renderCard(mockProduct);
    
    // CardMedia renders as a div with background image, not img
    const cardMedia = container.querySelector('.MuiCardMedia-root');
    expect(cardMedia).toBeInTheDocument();
  });

  it('handles product with null image', () => {
    const productWithoutImage = { ...mockProduct, pictureUrl: '' };
    renderCard(productWithoutImage);
    
    // Should still render without crashing
    expect(screen.getByText('Tennis Racket Pro')).toBeInTheDocument();
  });
});
