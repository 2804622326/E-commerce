import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import ServerError from '../../app/errors/ServerError';
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

const ServerErrorWithRouter = () => (
  <BrowserRouter>
    <ServerError />
  </BrowserRouter>
);

describe('ServerError Component', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should render server error message', () => {
    render(<ServerErrorWithRouter />);
    
    expect(screen.getByText('Oops! Something went wrong.')).toBeInTheDocument();
    expect(screen.getByText('The server encountered an internal error and was unable to complete your request.')).toBeInTheDocument();
  });

  it('should render 500 server error image with correct alt text', () => {
    render(<ServerErrorWithRouter />);
    
    const image = screen.getByRole('img');
    expect(image).toHaveAttribute('src', '/images/server-error.png');
    expect(image).toHaveAttribute('alt', '500 Server Error');
  });

  it('should render Go Home button', () => {
    render(<ServerErrorWithRouter />);
    
    const button = screen.getByRole('button', { name: /go home/i });
    expect(button).toBeInTheDocument();
  });

  it('should navigate to home when Go Home button is clicked', () => {
    render(<ServerErrorWithRouter />);
    
    const button = screen.getByRole('button', { name: /go home/i });
    fireEvent.click(button);
    
    expect(mockNavigate).toHaveBeenCalledWith('/');
  });

  it('should render heading with correct text', () => {
    render(<ServerErrorWithRouter />);
    
    const heading = screen.getByRole('heading', { level: 1 });
    expect(heading).toHaveTextContent('Oops! Something went wrong.');
  });

  it('should have proper container structure', () => {
    const { container } = render(<ServerErrorWithRouter />);
    
    expect(container.firstChild).toBeInTheDocument();
  });
});