import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import ContactPage from '../../features/contact/ContactPage';

describe('ContactPage Component', () => {
  it('should render contact page heading', () => {
    render(<ContactPage />);
    
    expect(screen.getByRole('heading', { level: 2 })).toBeInTheDocument();
    expect(screen.getByText('Contact Section')).toBeInTheDocument();
  });

  it('should render with h2 variant', () => {
    render(<ContactPage />);
    
    const heading = screen.getByRole('heading');
    expect(heading).toHaveAttribute('class');
  });

  it('should have proper structure', () => {
    const { container } = render(<ContactPage />);
    
    expect(container.firstChild).toBeInTheDocument();
  });
});