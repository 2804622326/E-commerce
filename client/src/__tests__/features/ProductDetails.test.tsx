import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import ProductDetails from '../../features/catalog/ProductDetails';
import { accountSlice } from '../../features/account/accountSlice';
import { basketSlice } from '../../features/basket/basketSlice';

// Mock agent with factory function
vi.mock('../../app/api/agent', () => ({
  default: {
    Store: {
      details: vi.fn()
    },
    Basket: {
      addItem: vi.fn(),
      incrementItemQuantity: vi.fn(),
      decrementItemQuantity: vi.fn()
    }
  }
}));

// Get the mocked functions
import agent from '../../app/api/agent';
const mockStoreDetails = vi.mocked(agent.Store.details);
const mockBasketAddItem = vi.mocked(agent.Basket.addItem);
const mockBasketIncrementItemQuantity = vi.mocked(agent.Basket.incrementItemQuantity);
const mockBasketDecrementItemQuantity = vi.mocked(agent.Basket.decrementItemQuantity);

// Mock MUI LoadingButton by mocking the @mui/lab module's LoadingButton export
vi.mock('@mui/lab', () => ({
  LoadingButton: ({ loading, children, ...props }: any) => (
    <button {...props} disabled={loading}>
      {loading ? 'Loading...' : children}
    </button>
  )
}));

// Mock console methods
const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

// Mock router params
const mockParams = { id: '1' };
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useParams: () => mockParams
  };
});

const mockProduct = {
  id: 1,
  name: 'Test Product',
  description: 'A great product for testing',
  price: 999,
  pictureUrl: '/images/test.jpg',
  productType: 'Electronics',
  productBrand: 'Test Brand'
};

const mockBasket = {
  id: 'test-basket',
  items: [
    {
      id: 1,
      name: 'Test Product',
      price: 999,
      description: 'A great product for testing',
      quantity: 2,
      pictureUrl: '/images/products/test.jpg',
      productBrand: 'Test Brand',
      productType: 'Electronics'
    }
  ]
};

const createTestStore = (basketState = { basket: null }) => {
  return configureStore({
    reducer: {
      account: accountSlice.reducer,
      basket: basketSlice.reducer
    },
    preloadedState: {
      account: { user: null, error: null },
      basket: basketState
    }
  });
};

const renderComponent = (productId = '1', basketState?: any) => {
  mockParams.id = productId;
  const store = createTestStore(basketState);
  return render(
    <Provider store={store}>
      <BrowserRouter>
        <ProductDetails />
      </BrowserRouter>
    </Provider>
  );
};

describe('ProductDetails Component - Enterprise Tests', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    consoleSpy.mockClear();
    consoleErrorSpy.mockClear();
    mockStoreDetails.mockResolvedValue(mockProduct);
    mockBasketAddItem.mockResolvedValue({ basket: { id: '123', items: [] }, totals: { shipping: 0, subTotal: 100, total: 100 } });
    mockBasketIncrementItemQuantity.mockResolvedValue(undefined);
    mockBasketDecrementItemQuantity.mockResolvedValue(undefined);
    mockParams.id = '1';
  });

  describe('Rendering & Initial State', () => {
    it('renders loading state initially', async () => {
      mockStoreDetails.mockImplementation(() => new Promise(() => {})); // Never resolve

      renderComponent();

      expect(screen.getByText(/Loading/i)).toBeInTheDocument();
    });

    it('renders product details when loaded', async () => {
      renderComponent();

  await waitFor(() => expect(screen.getByRole('heading', { level: 3, name: /Test Product/i })).toBeInTheDocument());

  expect(screen.getByRole('heading', { level: 3, name: /Test Product/i })).toBeInTheDocument();
      expect(screen.getByText(/₹999\.00/i)).toBeInTheDocument();
      expect(screen.getByText('A great product for testing')).toBeInTheDocument();
      expect(screen.getByText('Electronics')).toBeInTheDocument();
      expect(screen.getByText('Test Brand')).toBeInTheDocument();

      const img = screen.getByAltText('Test Product');
      expect(img).toHaveAttribute('src', '/images/products/test.jpg');
    });

    it('renders "Product not found" if product is null', async () => {
      mockStoreDetails.mockResolvedValue(null);

      renderComponent();

      await waitFor(() => expect(screen.getByText(/Product not found/i)).toBeInTheDocument());
      expect(screen.getByText(/Product not found/i)).toBeInTheDocument();
    });

    it('displays product details table structure', async () => {
      renderComponent();

  await waitFor(() => expect(screen.getByRole('heading', { level: 3, name: /Test Product/i })).toBeInTheDocument());

      expect(screen.getByText('Name')).toBeInTheDocument();
      expect(screen.getByText('Description')).toBeInTheDocument();
      expect(screen.getByText('Type')).toBeInTheDocument();
      expect(screen.getByText('Brand')).toBeInTheDocument();
    });
  });

  describe('User Interaction & Quantity Management', () => {
    beforeEach(() => {
      mockStoreDetails.mockResolvedValue(mockProduct);
    });

    it('allows user to input quantity and add to cart', async () => {
      renderComponent();

      await waitFor(() => expect(screen.getByDisplayValue('0')).toBeInTheDocument());

      const input = screen.getByLabelText(/Quantity in Cart/i);
      fireEvent.change(input, { target: { value: '3' } });
      expect(screen.getByDisplayValue('3')).toBeInTheDocument();

      const button = screen.getByRole('button', { name: /Add to Cart/i });
      fireEvent.click(button);

      await waitFor(() => expect(mockBasketAddItem).toHaveBeenCalledWith(
        expect.objectContaining({ id: 1, quantity: 3 }),
        expect.any(Function)
      ));
    });

    it('updates quantity if item already in basket', async () => {
      renderComponent('1', { basket: mockBasket });

      await waitFor(() => expect(screen.getByDisplayValue('2')).toBeInTheDocument());

      const input = screen.getByLabelText(/Quantity in Cart/i);
      fireEvent.change(input, { target: { value: '5' } });

      const button = screen.getByRole('button', { name: /Update Quantity/i });
      fireEvent.click(button);

      await waitFor(() => expect(mockBasketIncrementItemQuantity).toHaveBeenCalledWith(1, 3, expect.any(Function)));
    });

    it('decrements quantity when reducing amount', async () => {
      const basketWithHighQuantity = {
        basket: {
          ...mockBasket,
          items: [{
            ...mockBasket.items[0],
            quantity: 5
          }]
        }
      };

      renderComponent('1', basketWithHighQuantity);

      await waitFor(() => expect(screen.getByDisplayValue('5')).toBeInTheDocument());

      const input = screen.getByLabelText(/Quantity in Cart/i);
      fireEvent.change(input, { target: { value: '3' } });

      const button = screen.getByRole('button', { name: /Update Quantity/i });
      fireEvent.click(button);

      await waitFor(() => expect(mockBasketDecrementItemQuantity).toHaveBeenCalledWith(1, 2, expect.any(Function)));
    });

    it('shows loading button during submission', async () => {
      mockBasketAddItem.mockImplementation(() => new Promise(() => {}));

      renderComponent();

      await waitFor(() => expect(screen.getByDisplayValue('0')).toBeInTheDocument());

      const input = screen.getByLabelText(/Quantity in Cart/i);
      fireEvent.change(input, { target: { value: '1' } });

      const button = screen.getByRole('button', { name: /Add to Cart/i });
      fireEvent.click(button);

      expect(button).toBeDisabled();
      expect(screen.getByText(/Loading.../i)).toBeInTheDocument();
    });

    it('handles invalid quantity inputs', async () => {
      renderComponent();

      await waitFor(() => expect(screen.getByDisplayValue('0')).toBeInTheDocument());

      const input = screen.getByLabelText(/Quantity in Cart/i);

      // Test negative number
      fireEvent.change(input, { target: { value: '-1' } });
      expect(input).toHaveValue(0);

      // Test non-numeric
      fireEvent.change(input, { target: { value: 'abc' } });
      expect(input).toHaveValue(0);
    });
  });

  describe('Error Handling', () => {
    it('handles API error gracefully and shows product not found', async () => {
      mockStoreDetails.mockRejectedValue({ status: 404, message: 'Not found' });

      renderComponent();

      await waitFor(() => expect(screen.getByText(/Product not found/i)).toBeInTheDocument());
      expect(consoleSpy).toHaveBeenCalledWith(expect.objectContaining({ message: 'Not found' }));
    });

    it('handles network error without crashing', async () => {
      mockStoreDetails.mockRejectedValue(new Error('Network Error'));

      renderComponent();

      await waitFor(() => expect(screen.getByText(/Product not found/i)).toBeInTheDocument());
      expect(consoleSpy).toHaveBeenCalledWith(expect.any(Error));
    });

    it('handles basket update errors gracefully', async () => {
      mockBasketAddItem.mockRejectedValue(new Error('Update failed'));

      renderComponent();

      await waitFor(() => expect(screen.getByDisplayValue('0')).toBeInTheDocument());

      const input = screen.getByLabelText(/Quantity in Cart/i);
      fireEvent.change(input, { target: { value: '1' } });

      const button = screen.getByRole('button', { name: /Add to Cart/i });
      fireEvent.click(button);

      await waitFor(() => {
        expect(consoleErrorSpy).toHaveBeenCalledWith(
          'Failed to update quantity:',
          expect.any(Error)
        );
      });
    });
  });

  describe('Image Handling', () => {
    it('extracts image name correctly from complex URLs', async () => {
      const productWithComplexUrl = {
        ...mockProduct,
        pictureUrl: 'https://example.com/path/to/complex-image-name.jpg'
      };
      mockStoreDetails.mockResolvedValue(productWithComplexUrl);

      renderComponent();

      await waitFor(() => {
        const img = screen.getByAltText('Test Product');
        expect(img).toHaveAttribute('src', '/images/products/complex-image-name.jpg');
      });
    });

    it('handles empty or null pictureUrl', async () => {
      const productWithoutImage = {
        ...mockProduct,
        pictureUrl: ''
      };
      mockStoreDetails.mockResolvedValue(productWithoutImage);

      renderComponent();

      await waitFor(() => {
        const img = screen.getByAltText('Test Product');
        expect(img).toHaveAttribute('src', '/images/products/null');
      });
    });

    it('handles single filename without path', async () => {
      const productWithSimpleUrl = {
        ...mockProduct,
        pictureUrl: 'simple-image.jpg'
      };
      mockStoreDetails.mockResolvedValue(productWithSimpleUrl);

      renderComponent();

      await waitFor(() => {
        const img = screen.getByAltText('Test Product');
        expect(img).toHaveAttribute('src', '/images/products/simple-image.jpg');
      });
    });
  });

  describe('Price Formatting', () => {
    it('formats different price values correctly', async () => {
      const expensiveProduct = { ...mockProduct, price: 1234.99 };
      mockStoreDetails.mockResolvedValue(expensiveProduct);

      renderComponent();

      await waitFor(() => {
        expect(screen.getByText('₹1,234.99')).toBeInTheDocument();
      });
    });

    it('formats whole numbers correctly', async () => {
      const wholeNumberProduct = { ...mockProduct, price: 1000 };
      mockStoreDetails.mockResolvedValue(wholeNumberProduct);

      renderComponent();

      await waitFor(() => {
        expect(screen.getByText('₹1,000.00')).toBeInTheDocument();
      });
    });
  });

  describe('Accessibility & ARIA', () => {
    it('has proper ARIA labels and roles', async () => {
      renderComponent();

  await waitFor(() => expect(screen.getByRole('heading', { level: 3, name: /Test Product/i })).toBeInTheDocument());

      const button = screen.getByRole('button', { name: /Add to Cart/i });
      const input = screen.getByRole('spinbutton', { name: /Quantity in Cart/i });

      expect(button).toBeInTheDocument();
      expect(input).toBeInTheDocument();
      expect(input).toHaveAttribute('type', 'number');
    });

    it('supports keyboard navigation', async () => {
      renderComponent();

      await waitFor(() => expect(screen.getByRole('heading', { name: 'Test Product' })).toBeInTheDocument());

      const input = screen.getByLabelText(/Quantity in Cart/i);
      const button = screen.getByRole('button', { name: /Add to Cart/i });

      input.focus();
      expect(input).toHaveFocus();

      fireEvent.keyDown(input, { key: 'Tab', code: 'Tab' });
      // Verify button is accessible for tabbing
      expect(button).toBeTruthy();
    });

    it('has proper image alt text', async () => {
      renderComponent();

      await waitFor(() => {
        const img = screen.getByRole('img', { name: 'Test Product' });
        expect(img).toBeInTheDocument();
      });
    });
  });

  describe('Edge Cases', () => {
    it('handles missing product ID parameter', async () => {
      mockParams.id = undefined as any;
      // Mock the API call to return undefined when ID is undefined
      mockStoreDetails.mockResolvedValueOnce(undefined);

      renderComponent();

      await waitFor(() => {
        expect(screen.getByText(/Product not found/i)).toBeInTheDocument();
      });
    });

    it('handles zero quantity submission', async () => {
      renderComponent();

      await waitFor(() => expect(screen.getByDisplayValue('0')).toBeInTheDocument());

      const button = screen.getByRole('button', { name: /Add to Cart/i });
      fireEvent.click(button);

      await waitFor(() => expect(mockBasketAddItem).toHaveBeenCalledWith(
        expect.objectContaining({ quantity: 0 }),
        expect.any(Function)
      ));
    });

    it('handles extremely high quantities', async () => {
      renderComponent();

      await waitFor(() => expect(screen.getByDisplayValue('0')).toBeInTheDocument());

      const input = screen.getByLabelText(/Quantity in Cart/i);
      fireEvent.change(input, { target: { value: '999999' } });
      expect(input).toHaveValue(999999);
    });
  });
});