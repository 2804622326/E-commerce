import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import HomePage from '../../features/home/HomePage';

describe('HomePage Component', () => {
  it('should render home page heading', () => {
    render(<HomePage />);
    
    expect(screen.getByRole('heading', { level: 2 })).toBeInTheDocument();
    expect(screen.getByText('Home Page')).toBeInTheDocument();
  });

  it('should render with h2 variant', () => {
    render(<HomePage />);
    
    const heading = screen.getByRole('heading');
    expect(heading).toHaveAttribute('class');
  });

  it('should have proper structure', () => {
    const { container } = render(<HomePage />);
    
    expect(container.firstChild).toBeInTheDocument();
  });
});