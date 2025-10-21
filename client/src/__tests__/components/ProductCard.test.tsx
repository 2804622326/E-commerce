import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import ProductCard from '../../features/catalog/ProductCard';
import { Product } from '../../app/models/product';

vi.mock('../../app/store/configureStore', () => ({
  useAppDispatch: () => vi.fn(),
}));

vi.mock('../../app/api/agent', () => ({
  default: {
    Basket: {
      addItem: vi.fn(() => Promise.resolve({ basket: { id: '123', items: [] } })),
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
});
