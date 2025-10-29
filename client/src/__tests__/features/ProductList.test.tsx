import { describe, it, expect, vi } from 'vitest';
import { screen } from '@testing-library/react';
import { renderWithProviders } from '../utils/test-utils';
import ProductList from '../../features/catalog/ProductList';

// Mock the account slice to avoid import issues
vi.mock('../../features/account/accountSlice', () => ({
  default: {
    reducer: vi.fn((state = { user: null, error: null }) => state),
    actions: {
      logOut: vi.fn(),
      clearError: vi.fn()
    }
  }
}));

describe('ProductList Component', () => {
  const mockProducts = [
    {
      id: 1,
      name: 'Test Product 1',
      price: 100,
      description: 'Test description 1',
      pictureUrl: '/images/test1.jpg',
      productBrand: 'Test Brand 1',
      productType: 'Test Type 1',
    },
    {
      id: 2,
      name: 'Test Product 2',
      price: 200,
      description: 'Test description 2',
      pictureUrl: '/images/test2.jpg',
      productBrand: 'Test Brand 2',
      productType: 'Test Type 2',
    },
  ];

  it('renders list of products', () => {
    renderWithProviders(<ProductList products={mockProducts} />);
    
    // Check if product names are rendered via ProductCard
    expect(screen.getByText('Test Product 1')).toBeInTheDocument();
    expect(screen.getByText('Test Product 2')).toBeInTheDocument();
  });

  it('renders empty list when no products provided', () => {
    renderWithProviders(<ProductList products={[]} />);
    
    // Should not render any product cards
    expect(screen.queryByText('Test Product 1')).not.toBeInTheDocument();
  });

  it('uses grid layout for products', () => {
    const { container } = renderWithProviders(<ProductList products={mockProducts} />);
    
    // Check if grid container exists
    const gridContainer = container.querySelector('.MuiGrid-container');
    expect(gridContainer).toBeInTheDocument();
  });

  it('renders correct number of grid items', () => {
    const { container } = renderWithProviders(<ProductList products={mockProducts} />);
    
    // Check grid items
    const gridItems = container.querySelectorAll('.MuiGrid-item');
    expect(gridItems).toHaveLength(2);
  });

  it('passes product data to ProductCard components', () => {
    renderWithProviders(<ProductList products={mockProducts} />);
    
    // ProductCard should display product information
    expect(screen.getByText('Test Product 1')).toBeInTheDocument();
    expect(screen.getByText('Test Product 2')).toBeInTheDocument();
  });

  it('handles single product correctly', () => {
    const singleProduct = [mockProducts[0]];
    
    renderWithProviders(<ProductList products={singleProduct} />);
    
    expect(screen.getByText('Test Product 1')).toBeInTheDocument();
    expect(screen.queryByText('Test Product 2')).not.toBeInTheDocument();
  });

  it('applies correct grid spacing', () => {
    const { container } = renderWithProviders(<ProductList products={mockProducts} />);
    
    // Check if spacing is applied (MUI adds spacing classes)
    const gridContainer = container.querySelector('.MuiGrid-container');
    expect(gridContainer).toHaveClass('MuiGrid-spacing-xs-4');
  });

  it('sets correct grid item size', () => {
    const { container } = renderWithProviders(<ProductList products={mockProducts} />);
    
    // Check grid item classes for xs=4
    const gridItems = container.querySelectorAll('.MuiGrid-item');
    gridItems.forEach(item => {
      expect(item).toHaveClass('MuiGrid-grid-xs-4');
    });
  });
});