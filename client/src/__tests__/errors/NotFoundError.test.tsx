import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import NotFoundError from '../../app/errors/NotFoundError';
import { BrowserRouter } from 'react-router-dom';

// Mock useNavigate
const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

const NotFoundErrorWithRouter = () => (
  <BrowserRouter>
    <NotFoundError />
  </BrowserRouter>
);

describe('NotFoundError Component', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should render not found message', () => {
    render(<NotFoundErrorWithRouter />);
    
    expect(screen.getByText('Oops! Page not found.')).toBeInTheDocument();
    expect(screen.getByText("We can't seem to find the page you're looking for.")).toBeInTheDocument();
  });

  it('should render 404 image with correct alt text', () => {
    render(<NotFoundErrorWithRouter />);
    
    const image = screen.getByRole('img');
    expect(image).toHaveAttribute('src', '/images/page-not-found.png');
    expect(image).toHaveAttribute('alt', '404 Not Found');
  });

  it('should render Go Home button', () => {
    render(<NotFoundErrorWithRouter />);
    
    const button = screen.getByRole('button', { name: /go home/i });
    expect(button).toBeInTheDocument();
  });

  it('should navigate to home when Go Home button is clicked', () => {
    render(<NotFoundErrorWithRouter />);
    
    const button = screen.getByRole('button', { name: /go home/i });
    fireEvent.click(button);
    
    expect(mockNavigate).toHaveBeenCalledWith('/');
  });

  it('should render heading with correct text', () => {
    render(<NotFoundErrorWithRouter />);
    
    const heading = screen.getByRole('heading', { level: 1 });
    expect(heading).toHaveTextContent('Oops! Page not found.');
  });

  it('should have proper container structure', () => {
    const { container } = render(<NotFoundErrorWithRouter />);
    
    expect(container.firstChild).toBeInTheDocument();
  });
});