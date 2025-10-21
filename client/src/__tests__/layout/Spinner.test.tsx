import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import Spinner from '../../app/layout/Spinner';

describe('Spinner Component', () => {
  it('renders with default loading message', () => {
    render(<Spinner />);
    
    expect(screen.getByText('Loading...')).toBeInTheDocument();
    expect(screen.getByRole('progressbar', { hidden: true })).toBeInTheDocument();
  });

  it('renders with custom message', () => {
    const customMessage = 'Please wait while we process your request...';
    render(<Spinner message={customMessage} />);
    
    expect(screen.getByText(customMessage)).toBeInTheDocument();
    expect(screen.getByRole('progressbar', { hidden: true })).toBeInTheDocument();
  });

  it('renders with empty message', () => {
    render(<Spinner message="" />);
    
    // Check if empty h4 element exists (but with hidden accessibility)
    const heading = screen.getByRole('heading', { level: 4, hidden: true });
    expect(heading).toBeInTheDocument();
    expect(screen.getByRole('progressbar', { hidden: true })).toBeInTheDocument();
  });

  it('has correct backdrop structure', () => {
    const { container } = render(<Spinner />);
    
    const backdrop = container.querySelector('.MuiBackdrop-root');
    expect(backdrop).toBeInTheDocument();
    
    const progressbar = screen.getByRole('progressbar', { hidden: true });
    expect(progressbar).toBeInTheDocument();
  });

  it('renders circular progress indicator', () => {
    render(<Spinner />);
    
    const progressbar = screen.getByRole('progressbar', { hidden: true });
    expect(progressbar).toBeInTheDocument();
    expect(progressbar).toHaveClass('MuiCircularProgress-root');
  });

  it('has correct backdrop structure', () => {
    const { container } = render(<Spinner />);
    
    const backdrop = container.querySelector('.MuiBackdrop-root');
    expect(backdrop).toBeInTheDocument();
    
  const progressbar = screen.getByRole('progressbar', { hidden: true });
    expect(progressbar).toBeInTheDocument();
  });

  it('displays message with correct typography variant', () => {
    render(<Spinner message="Test Message" />);
    
    const messageElement = screen.getByText('Test Message');
    expect(messageElement).toBeInTheDocument();
    expect(messageElement.tagName.toLowerCase()).toBe('h4');
  });

  it('renders circular progress indicator', () => {
    render(<Spinner />);
    
  const progressbar = screen.getByRole('progressbar', { hidden: true });
    expect(progressbar).toBeInTheDocument();
    expect(progressbar).toHaveClass('MuiCircularProgress-root');
  });

  it('handles long messages', () => {
    const longMessage = 'This is a very long loading message that should still be displayed correctly in the spinner component without any issues';
    render(<Spinner message={longMessage} />);
    
    expect(screen.getByText(longMessage)).toBeInTheDocument();
  });

  it('handles special characters in message', () => {
    const specialMessage = 'Loading... 100% & more! 🚀';
    render(<Spinner message={specialMessage} />);
    
    expect(screen.getByText(specialMessage)).toBeInTheDocument();
  });
});