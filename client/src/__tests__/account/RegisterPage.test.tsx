import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import RegisterPage from '../../features/account/RegisterPage';

const RegisterPageWithRouter = () => (
  <BrowserRouter>
    <RegisterPage />
  </BrowserRouter>
);

describe('RegisterPage Component', () => {
  it('should render register form', () => {
    render(<RegisterPageWithRouter />);
    
    expect(screen.getByRole('heading', { name: /register/i })).toBeInTheDocument();
    expect(screen.getByLabelText(/username/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
  });

  it('should render register button', () => {
    render(<RegisterPageWithRouter />);
    
    const registerButton = screen.getByRole('button', { name: /register/i });
    expect(registerButton).toBeInTheDocument();
  });

  it('should render lock icon', () => {
    render(<RegisterPageWithRouter />);
    
    // Check for the lock icon using data-testid
    const lockIcon = screen.getByTestId('LockOutlinedIcon');
    expect(lockIcon).toBeInTheDocument();
  });

  it('should update username field value', () => {
    render(<RegisterPageWithRouter />);
    
    const usernameInput = screen.getByLabelText(/username/i) as HTMLInputElement;
    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    
    expect(usernameInput.value).toBe('testuser');
  });

  it('should update email field value', () => {
    render(<RegisterPageWithRouter />);
    
    const emailInput = screen.getByLabelText(/email/i) as HTMLInputElement;
    fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
    
    expect(emailInput.value).toBe('test@example.com');
  });

  it('should update password field value', () => {
    render(<RegisterPageWithRouter />);
    
    const passwordInput = screen.getByLabelText(/password/i) as HTMLInputElement;
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    
    expect(passwordInput.value).toBe('password123');
  });

  it('should handle form submission', () => {
    const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
    render(<RegisterPageWithRouter />);
    
    const usernameInput = screen.getByLabelText(/username/i);
    const emailInput = screen.getByLabelText(/email/i);
    const passwordInput = screen.getByLabelText(/password/i);
    const submitButton = screen.getByRole('button', { name: /register/i });
    
    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.click(submitButton);
    
    expect(consoleSpy).toHaveBeenCalledWith({
      username: 'testuser',
      email: 'test@example.com',
      password: 'password123'
    });
    
    consoleSpy.mockRestore();
  });

  it('should render sign in link', () => {
    render(<RegisterPageWithRouter />);
    
    const signInLink = screen.getByRole('link', { name: /already have an account/i });
    expect(signInLink).toBeInTheDocument();
    expect(signInLink).toHaveAttribute('href', '/login');
  });

  it('should have required fields', () => {
    render(<RegisterPageWithRouter />);
    
    const usernameInput = screen.getByLabelText(/username/i);
    const emailInput = screen.getByLabelText(/email/i);
    const passwordInput = screen.getByLabelText(/password/i);
    
    expect(usernameInput).toBeRequired();
    expect(emailInput).toBeRequired();
    expect(passwordInput).toBeRequired();
  });

  it('should have proper container structure', () => {
    render(<RegisterPageWithRouter />);
    
    const container = screen.getByRole('main');
    expect(container).toBeInTheDocument();
  });

  it('should render all form elements correctly', () => {
    render(<RegisterPageWithRouter />);
    
    // Check all required form elements are present
    expect(screen.getByLabelText(/username/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /register/i })).toBeInTheDocument();
  });
});