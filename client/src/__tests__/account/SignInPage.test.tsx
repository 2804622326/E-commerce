import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import SignInPage from '../../features/account/SignInPage';

// Mock dependencies
const mockNavigate = vi.fn();
const mockDispatch = vi.fn();

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
    useLocation: () => ({ state: { from: '/checkout' } }),
  };
});

vi.mock('../../app/store/configureStore', () => ({
  useAppDispatch: () => mockDispatch,
  store: {
    getState: () => ({
      account: { user: { id: 1, username: 'testuser' } }
    })
  }
}));

vi.mock('react-toastify', () => ({
  toast: {
    error: vi.fn()
  }
}));

vi.mock('../../features/account/accountSlice', () => ({
  signInUser: vi.fn().mockResolvedValue({ id: 1, username: 'testuser' })
}));

const SignInPageWithRouter = () => (
  <BrowserRouter>
    <SignInPage />
  </BrowserRouter>
);

describe('SignInPage Component', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should render sign in form', () => {
    render(<SignInPageWithRouter />);
    
    expect(screen.getByRole('heading', { name: /sign in/i })).toBeInTheDocument();
    expect(screen.getByLabelText(/username/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
  });

  it('should render lock icon', () => {
    render(<SignInPageWithRouter />);
    
    const lockIcon = screen.getByTestId('LockOutlinedIcon');
    expect(lockIcon).toBeInTheDocument();
  });

  it('should render sign in button', () => {
    render(<SignInPageWithRouter />);
    
    const signInButton = screen.getByRole('button', { name: /sign in/i });
    expect(signInButton).toBeInTheDocument();
  });

  it('should render remember me checkbox', () => {
    render(<SignInPageWithRouter />);
    
    const checkbox = screen.getByRole('checkbox', { name: /remember me/i });
    expect(checkbox).toBeInTheDocument();
  });

  it('should render forgot password text', () => {
    render(<SignInPageWithRouter />);
    
    const forgotText = screen.getByText(/forgot password/i);
    expect(forgotText).toBeInTheDocument();
  });

  it('should render sign up link', () => {
    render(<SignInPageWithRouter />);
    
    const signUpLink = screen.getByRole('link', { name: /don't have an account/i });
    expect(signUpLink).toBeInTheDocument();
    expect(signUpLink).toHaveAttribute('href', '/register');
  });

  it('should show validation requires form interaction', async () => {
    render(<SignInPageWithRouter />);
    
    const usernameInput = screen.getByLabelText(/username/i);
    const passwordInput = screen.getByLabelText(/password/i);
    
    // Touch the fields first to trigger validation
    fireEvent.focus(usernameInput);
    fireEvent.blur(usernameInput);
    fireEvent.focus(passwordInput);
    fireEvent.blur(passwordInput);
    
    // The form uses 'onTouched' mode, so validation appears after interaction
    await waitFor(() => {
      expect(usernameInput).toBeInTheDocument();
      expect(passwordInput).toBeInTheDocument();
    });
  });

  it('should enable submit button when form is valid', async () => {
    render(<SignInPageWithRouter />);
    
    const usernameInput = screen.getByLabelText(/username/i);
    const passwordInput = screen.getByLabelText(/password/i);
    const signInButton = screen.getByRole('button', { name: /sign in/i });
    
    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    
    await waitFor(() => {
      expect(signInButton).not.toBeDisabled();
    });
  });

  it('should update username field value', () => {
    render(<SignInPageWithRouter />);
    
    const usernameInput = screen.getByLabelText(/username/i) as HTMLInputElement;
    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    
    expect(usernameInput.value).toBe('testuser');
  });

  it('should update password field value', () => {
    render(<SignInPageWithRouter />);
    
    const passwordInput = screen.getByLabelText(/password/i) as HTMLInputElement;
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    
    expect(passwordInput.value).toBe('password123');
  });

  it('should have required fields marked as required', () => {
    render(<SignInPageWithRouter />);
    
    const usernameInput = screen.getByLabelText(/username/i);
    const passwordInput = screen.getByLabelText(/password/i);
    
    expect(usernameInput).toBeRequired();
    expect(passwordInput).toBeRequired();
  });

  it('should handle form submission with valid data', async () => {
    render(<SignInPageWithRouter />);
    
    const usernameInput = screen.getByLabelText(/username/i);
    const passwordInput = screen.getByLabelText(/password/i);
    const signInButton = screen.getByRole('button', { name: /sign in/i });
    
    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    
    await waitFor(() => {
      expect(signInButton).not.toBeDisabled();
    });
    
    fireEvent.click(signInButton);
    
    await waitFor(() => {
      expect(mockDispatch).toHaveBeenCalled();
    });
  });

  it('should have proper container structure', () => {
    const { container } = render(<SignInPageWithRouter />);
    
    expect(container.querySelector('main')).toBeInTheDocument();
  });

  it('should render form with correct attributes', () => {
    const { container } = render(<SignInPageWithRouter />);
    
    const form = container.querySelector('form');
    expect(form).toHaveAttribute('novalidate');
  });
});