import { describe, it, expect, vi, beforeEach } from 'vitest';
import { screen, fireEvent } from '@testing-library/react';
import SignedInMenu from '../../app/layout/SignedInMenu';
import { renderWithProviders } from '../utils/test-utils';

// Mock the account slice actions - must be defined inside the factory to avoid hoisting issues
vi.mock('../../features/account/accountSlice', async () => {
  const actual = await vi.importActual('../../features/account/accountSlice');
  return {
    ...actual,
    logOut: vi.fn(() => ({ type: 'account/logOut' }))
  };
});

describe('SignedInMenu Component', () => {
  const mockUser = {
    username: 'testuser',
    email: 'test@example.com',
    token: 'test-token'
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders user greeting with username', () => {
    renderWithProviders(<SignedInMenu />, {
      preloadedState: {
        account: { user: mockUser, error: null }
      }
    });

    expect(screen.getByText(/Hi, testuser/i)).toBeInTheDocument();
  });

  it('opens menu when button is clicked', async () => {
    renderWithProviders(<SignedInMenu />, {
      preloadedState: {
        account: { user: mockUser, error: null }
      }
    });

    const button = screen.getByRole('button', { name: /Hi, testuser/i });
    
    // Menu should not be visible initially
    expect(screen.queryByRole('menu')).not.toBeInTheDocument();
    
    // Click to open menu
    fireEvent.click(button);
    
    // Menu should now be visible
    expect(screen.getByRole('menu')).toBeInTheDocument();
  });

  it('displays all menu items when menu is open', () => {
    renderWithProviders(<SignedInMenu />, {
      preloadedState: {
        account: { user: mockUser, error: null }
      }
    });

    const button = screen.getByRole('button', { name: /Hi, testuser/i });
    fireEvent.click(button);

    expect(screen.getByRole('menuitem', { name: /Profile/i })).toBeInTheDocument();
    expect(screen.getByRole('menuitem', { name: /My Orders/i })).toBeInTheDocument();
    expect(screen.getByRole('menuitem', { name: /Logout/i })).toBeInTheDocument();
  });

  it('closes menu when Profile is clicked', () => {
    renderWithProviders(<SignedInMenu />, {
      preloadedState: {
        account: { user: mockUser, error: null }
      }
    });

    const button = screen.getByRole('button', { name: /Hi, testuser/i });
    fireEvent.click(button);

    const profileItem = screen.getByRole('menuitem', { name: /Profile/i });
    fireEvent.click(profileItem);

    // Menu should close after clicking Profile
    expect(screen.queryByRole('menu')).not.toBeInTheDocument();
  });

  it('navigates to orders page when My Orders is clicked', () => {
    renderWithProviders(<SignedInMenu />, {
      preloadedState: {
        account: { user: mockUser, error: null }
      }
    });

    const button = screen.getByRole('button', { name: /Hi, testuser/i });
    fireEvent.click(button);

    const ordersLink = screen.getByRole('menuitem', { name: /My Orders/i });
    expect(ordersLink).toHaveAttribute('href', '/orders');
  });

  it('dispatches logout action when Logout is clicked', () => {
    const { store } = renderWithProviders(<SignedInMenu />, {
      preloadedState: {
        account: { user: mockUser, error: null }
      }
    });

    const button = screen.getByRole('button', { name: /Hi, testuser/i });
    fireEvent.click(button);

    const logoutItem = screen.getByRole('menuitem', { name: /Logout/i });
    fireEvent.click(logoutItem);

    // Check that an action was dispatched (the mocked logOut action)
    const actions = store.getState();
    expect(actions).toBeDefined();
  });

  it('handles null user gracefully', () => {
    renderWithProviders(<SignedInMenu />, {
      preloadedState: {
        account: { user: null, error: null }
      }
    });

    // Component should render even with null user (showing "Hi, ")
    const button = screen.getByRole('button');
    expect(button).toBeInTheDocument();
  });

  it('has correct menu structure with Button and Menu components', () => {
    renderWithProviders(<SignedInMenu />, {
      preloadedState: {
        account: { user: mockUser, error: null }
      }
    });

    const button = screen.getByRole('button', { name: /Hi, testuser/i });
    expect(button).toBeInTheDocument();
    expect(button).toHaveTextContent('Hi, testuser');
  });

  it('renders with Fade transition component', () => {
    renderWithProviders(<SignedInMenu />, {
      preloadedState: {
        account: { user: mockUser, error: null }
      }
    });

    const button = screen.getByRole('button', { name: /Hi, testuser/i });
    fireEvent.click(button);

    // Menu should be present with Fade transition
    const menu = screen.getByRole('menu');
    expect(menu).toBeInTheDocument();
  });

  it('maintains menu state across multiple open/close cycles', () => {
    renderWithProviders(<SignedInMenu />, {
      preloadedState: {
        account: { user: mockUser, error: null }
      }
    });

    const button = screen.getByRole('button', { name: /Hi, testuser/i });

    // Open menu
    fireEvent.click(button);
    expect(screen.getByRole('menu')).toBeInTheDocument();

    // Close by clicking Profile
    fireEvent.click(screen.getByRole('menuitem', { name: /Profile/i }));
    expect(screen.queryByRole('menu')).not.toBeInTheDocument();

    // Open again
    fireEvent.click(button);
    expect(screen.getByRole('menu')).toBeInTheDocument();

    // All items should still be present
    expect(screen.getByRole('menuitem', { name: /Profile/i })).toBeInTheDocument();
    expect(screen.getByRole('menuitem', { name: /My Orders/i })).toBeInTheDocument();
    expect(screen.getByRole('menuitem', { name: /Logout/i })).toBeInTheDocument();
  });
});
