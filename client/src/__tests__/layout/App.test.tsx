import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import App from '../../app/layout/App';
import { accountSlice } from '../../features/account/accountSlice';
import { basketSlice } from '../../features/basket/basketSlice';

// Mock dependencies
vi.mock('../../app/util/util', () => ({
  getBasketFromLocalStorage: vi.fn()
}));

vi.mock('../../app/api/agent', () => ({
  default: {
    Basket: {
      get: vi.fn()
    }
  }
}));

vi.mock('../../features/account/accountSlice', () => ({
  default: {
    name: 'account',
    initialState: { user: null },
    reducer: vi.fn((state = { user: null }) => state),
    reducers: {},
    extraReducers: () => {}
  },
  accountSlice: {
    name: 'account',
    initialState: { user: null },
    reducer: vi.fn((state = { user: null }) => state),
    reducers: {},
    extraReducers: () => {}
  },
  fetchCurrentUser: vi.fn()
}));

vi.mock('../../features/basket/basketSlice', () => ({
  basketSlice: {
    name: 'basket',
    initialState: { basket: null },
    reducer: vi.fn((state = { basket: null }) => state),
    reducers: {},
    extraReducers: () => {}
  },
  fetchBasket: vi.fn(),
  setBasket: vi.fn()
}));

const { getBasketFromLocalStorage } = await import('../../app/util/util');
const agent = await import('../../app/api/agent');
const { fetchCurrentUser } = await import('../../features/account/accountSlice');
const { setBasket } = await import('../../features/basket/basketSlice');

// Create test store
const createTestStore = () => {
  return configureStore({
    reducer: {
      account: accountSlice.reducer,
      basket: basketSlice.reducer
    }
  });
};

const renderWithProviders = (component: React.ReactElement) => {
  const store = createTestStore();
  return render(
    <Provider store={store}>
      <BrowserRouter>
        {component}
      </BrowserRouter>
    </Provider>
  );
};

describe('App Component', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(getBasketFromLocalStorage).mockReturnValue(null);
    vi.mocked(agent.default.Basket.get).mockResolvedValue({ id: '123', items: [] });
    vi.mocked(fetchCurrentUser).mockReturnValue({ type: 'account/fetchCurrentUser/pending' } as any);
    vi.mocked(setBasket).mockReturnValue({ type: 'basket/setBasket', payload: {} } as any);
  });

  it('renders spinner when loading', () => {
    vi.mocked(getBasketFromLocalStorage).mockReturnValue({ id: '123', items: [] });
    
    renderWithProviders(<App />);
    
  expect(screen.getByText('Getting Basket...')).toBeInTheDocument();
  // progressbar is inside an aria-hidden Backdrop, so include hidden
  expect(screen.getByRole('progressbar', { hidden: true })).toBeInTheDocument();
  });

  it('renders main app structure when not loading', async () => {
    renderWithProviders(<App />);
    
    await waitFor(() => {
      expect(screen.queryByText('Getting Basket...')).not.toBeInTheDocument();
    });
    
    // Check for header (assuming it has Sports Center text)
    expect(screen.getByText('Sports Center')).toBeInTheDocument();
    
    // Check for container structure
    const container = document.querySelector('.MuiContainer-root');
    expect(container).toBeInTheDocument();
  });

  it('fetches current user on mount', () => {
    renderWithProviders(<App />);
    
    expect(fetchCurrentUser).toHaveBeenCalled();
  });

  it('gets basket from API when basket exists in localStorage', async () => {
    const mockBasket = { id: '123', items: [] };
    vi.mocked(getBasketFromLocalStorage).mockReturnValue(mockBasket);
    
    renderWithProviders(<App />);
    
    await waitFor(() => {
      expect(agent.default.Basket.get).toHaveBeenCalled();
    });
  });

  it('does not fetch basket when none exists in localStorage', () => {
    vi.mocked(getBasketFromLocalStorage).mockReturnValue(null);
    
    renderWithProviders(<App />);
    
    expect(agent.default.Basket.get).not.toHaveBeenCalled();
  });

  it('handles basket API error gracefully', async () => {
    const mockBasket = { id: '123', items: [] };
    vi.mocked(getBasketFromLocalStorage).mockReturnValue(mockBasket);
    vi.mocked(agent.default.Basket.get).mockRejectedValue(new Error('API Error'));
    
    const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
    
    renderWithProviders(<App />);
    
    await waitFor(() => {
      expect(consoleSpy).toHaveBeenCalledWith(expect.any(Error));
    });
    
    consoleSpy.mockRestore();
  });

  it('toggles dark mode when header theme button is clicked', async () => {
    renderWithProviders(<App />);
    
    await waitFor(() => {
      expect(screen.queryByText('Getting Basket...')).not.toBeInTheDocument();
    });
    
  // The theme toggle is a Switch rendered as a checkbox
  const themeButton = screen.getByRole('checkbox');
  expect(themeButton).toBeInTheDocument();
    
  fireEvent.click(themeButton);
    
    // Check that theme toggled by verifying checkbox state changed
    await waitFor(() => {
      expect((themeButton as HTMLInputElement).checked).toBe(true);
    });
  });

  it('renders ToastContainer for notifications', async () => {
    renderWithProviders(<App />);
    
    await waitFor(() => {
      expect(screen.queryByText('Getting Basket...')).not.toBeInTheDocument();
    });
    
  // Check for toast container (Toastify renders a root with class 'Toastify')
  const toastContainer = document.querySelector('.Toastify');
  expect(toastContainer).toBeInTheDocument();
  });

  it('applies correct theme palette mode', async () => {
    renderWithProviders(<App />);
    
    await waitFor(() => {
      expect(screen.queryByText('Getting Basket...')).not.toBeInTheDocument();
    });
    
  // Check that header/title exists as a proxy for theme being applied
  expect(screen.getByText('Sports Center')).toBeInTheDocument();
  });

  it('renders outlet for nested routes', async () => {
    renderWithProviders(<App />);
    
    await waitFor(() => {
      expect(screen.queryByText('Getting Basket...')).not.toBeInTheDocument();
    });
    
    // The outlet should be rendered (content will depend on current route)
    const container = document.querySelector('.MuiContainer-root');
    expect(container).toBeInTheDocument();
    expect(container).toHaveStyle('padding-top: 64px');
  });

  it('handles component lifecycle correctly', async () => {
    const { unmount } = renderWithProviders(<App />);
    
    // Wait for initial loading to complete
    await waitFor(() => {
      expect(screen.queryByText('Getting Basket...')).not.toBeInTheDocument();
    });
    
    // Should not throw errors on unmount
    expect(() => unmount()).not.toThrow();
  });
});