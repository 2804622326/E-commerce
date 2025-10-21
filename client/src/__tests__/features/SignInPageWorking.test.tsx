import { describe, it, expect, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import SignInPage from '../../features/account/SignInPage';

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => vi.fn(),
    useLocation: () => ({ state: null }),
  };
});

vi.mock('react-toastify', () => ({
  toast: {
    error: vi.fn(),
    success: vi.fn(),
  },
}));

vi.mock('../../app/store/configureStore', () => ({
  store: {
    getState: () => ({
      account: { user: null }
    })
  },
  useAppDispatch: () => vi.fn(),
}));

vi.mock('../../features/account/accountSlice', () => ({
  signInUser: vi.fn(),
}));

describe('SignInPage - Form Rendering', () => {
  const renderSignInPage = () => {
    return render(
      <BrowserRouter>
        <SignInPage />
      </BrowserRouter>
    );
  };

  it('renders Sign in heading', () => {
    renderSignInPage();
    expect(screen.getByText('Sign in')).toBeInTheDocument();
  });

  it('renders username input field', () => {
    renderSignInPage();
    const usernameInput = screen.getByLabelText(/username/i);
    expect(usernameInput).toBeInTheDocument();
    expect(usernameInput).toHaveAttribute('type', 'text');
  });

  it('renders password input field', () => {
    renderSignInPage();
    const passwordInput = screen.getByLabelText(/password/i);
    expect(passwordInput).toBeInTheDocument();
    expect(passwordInput).toHaveAttribute('type', 'password');
  });

  it('renders remember me checkbox', () => {
    renderSignInPage();
    const checkbox = screen.getByRole('checkbox', { name: /remember me/i });
    expect(checkbox).toBeInTheDocument();
  });

  it('renders sign in button', () => {
    renderSignInPage();
    const signInButton = screen.getByRole('button', { name: /sign in/i });
    expect(signInButton).toBeInTheDocument();
  });

  it('renders register link', () => {
    renderSignInPage();
    const registerLink = screen.getByText(/don't have an account/i);
    expect(registerLink).toBeInTheDocument();
  });
});

describe('SignInPage - User Interactions', () => {
  const renderSignInPage = () => {
    return render(
      <BrowserRouter>
        <SignInPage />
      </BrowserRouter>
    );
  };

  it('allows user to type in username field', async () => {
    const user = userEvent.setup();
    renderSignInPage();
    
    const usernameInput = screen.getByLabelText(/username/i);
    await user.type(usernameInput, 'testuser');
    
    expect(usernameInput).toHaveValue('testuser');
  });

  it('allows user to type in password field', async () => {
    const user = userEvent.setup();
    renderSignInPage();
    
    const passwordInput = screen.getByLabelText(/password/i);
    await user.type(passwordInput, 'password123');
    
    expect(passwordInput).toHaveValue('password123');
  });

  it('allows user to toggle remember me checkbox', async () => {
    const user = userEvent.setup();
    renderSignInPage();
    
    const checkbox = screen.getByRole('checkbox', { name: /remember me/i });
    expect(checkbox).not.toBeChecked();
    
    await user.click(checkbox);
    expect(checkbox).toBeChecked();
  });

  it('shows required field validation messages', async () => {
    const user = userEvent.setup();
    renderSignInPage();
    
    const usernameInput = screen.getByLabelText(/username/i);
    await user.click(usernameInput);
    await user.tab();
    
    await waitFor(() => {
      expect(screen.getByText(/username is required/i)).toBeInTheDocument();
    });
  });
});
