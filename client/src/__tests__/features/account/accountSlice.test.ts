import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';

// Mock router
vi.mock('../../../app/router/Routes', () => ({
  router: {
    navigate: vi.fn()
  }
}));

// Mock agent
vi.mock('../../../app/api/agent', () => ({
  default: {
    Account: {
      login: vi.fn()
    }
  }
}));

// Mock toast
vi.mock('react-toastify', () => ({
  toast: {
    success: vi.fn(),
    error: vi.fn()
  }
}));

import { configureStore } from '@reduxjs/toolkit';
import { accountSlice, signInUser, fetchCurrentUser, logoutUser } from '../../../features/account/accountSlice';

describe('accountSlice - Reducers, Actions & Async Thunks', () => {
  let store: any;

  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();
    
    store = configureStore({
      reducer: {
        account: accountSlice.reducer
      }
    });
  });

  afterEach(() => {
    localStorage.clear();
  });

  describe('Synchronous Actions', () => {
    it('logOut action clears user and error state', () => {
      // Set initial state with user
      store = configureStore({
        reducer: { account: accountSlice.reducer },
        preloadedState: {
          account: {
            user: { username: 'testuser', token: 'token123' },
            error: 'some error'
          }
        }
      });

      // Dispatch logOut
      store.dispatch(accountSlice.actions.logOut());

      const state = store.getState().account;
      expect(state.user).toBeNull();
      expect(state.error).toBeNull();
    });

    it('logOut removes user from localStorage', () => {
      localStorage.setItem('user', JSON.stringify({ username: 'test' }));
      
      store.dispatch(accountSlice.actions.logOut());
      
      expect(localStorage.getItem('user')).toBeNull();
    });

    it('logOut navigates to home page', async () => {
      const routerModule = await import('../../../app/router/Routes');
      
      store.dispatch(accountSlice.actions.logOut());
      
      expect(routerModule.router.navigate).toHaveBeenCalledWith('/');
    });

    it('clearError action clears error state only', () => {
      store = configureStore({
        reducer: { account: accountSlice.reducer },
        preloadedState: {
          account: {
            user: { username: 'testuser', token: 'token123' },
            error: 'some error'
          }
        }
      });

      store.dispatch(accountSlice.actions.clearError());

      const state = store.getState().account;
      expect(state.error).toBeNull();
      expect(state.user).toEqual({ username: 'testuser', token: 'token123' });
    });
  });

  describe('signInUser Async Thunk', () => {
    it('sets user on successful login', async () => {
      const agent = await import('../../../app/api/agent');
      const mockUser = { username: 'testuser', token: 'token123' };
      (agent.default.Account.login as any).mockResolvedValue(mockUser);

      const credentials = { username: 'test', password: 'pass' };
      await store.dispatch(signInUser(credentials));

      const state = store.getState().account;
      expect(state.user).toEqual(mockUser);
      expect(state.error).toBeNull();
    });

    it('saves user to localStorage on successful login', async () => {
      const agent = await import('../../../app/api/agent');
      const mockUser = { username: 'testuser', token: 'token123' };
      (agent.default.Account.login as any).mockResolvedValue(mockUser);

      await store.dispatch(signInUser({ username: 'test', password: 'pass' }));

      const savedUser = localStorage.getItem('user');
      expect(savedUser).toBe(JSON.stringify(mockUser));
    });

    it('sets error on failed login', async () => {
      const agent = await import('../../../app/api/agent');
      const errorData = { message: 'Invalid credentials' };
      (agent.default.Account.login as any).mockRejectedValue({ data: errorData });

      await store.dispatch(signInUser({ username: 'test', password: 'wrong' }));

      const state = store.getState().account;
      expect(state.error).toEqual({ error: errorData });
    });

    it('shows success toast on successful sign in', async () => {
      const agent = await import('../../../app/api/agent');
      const toast = await import('react-toastify');
      const mockUser = { username: 'testuser', token: 'token123' };
      (agent.default.Account.login as any).mockResolvedValue(mockUser);

      await store.dispatch(signInUser({ username: 'test', password: 'pass' }));

      expect(toast.toast.success).toHaveBeenCalledWith('Sign in successful');
    });
  });

  describe('fetchCurrentUser Async Thunk', () => {
    it('returns user from localStorage if exists', async () => {
      const mockUser = { username: 'testuser', token: 'token123' };
      localStorage.setItem('user', JSON.stringify(mockUser));

      await store.dispatch(fetchCurrentUser());

      const state = store.getState().account;
      expect(state.user).toEqual(mockUser);
    });

    it('returns null if no user in localStorage', async () => {
      await store.dispatch(fetchCurrentUser());

      const state = store.getState().account;
      expect(state.user).toBeNull();
    });

    it('handles JSON parse errors gracefully', async () => {
      localStorage.setItem('user', 'invalid json');

      await store.dispatch(fetchCurrentUser());

      const state = store.getState().account;
      expect(state.user).toBeNull();
    });

    it('clears error on successful fetch', async () => {
      const mockUser = { username: 'testuser', token: 'token123' };
      localStorage.setItem('user', JSON.stringify(mockUser));

      store = configureStore({
        reducer: { account: accountSlice.reducer },
        preloadedState: {
          account: { user: null, error: 'previous error' }
        }
      });

      await store.dispatch(fetchCurrentUser());

      const state = store.getState().account;
      expect(state.error).toBeNull();
    });
  });

  describe('logoutUser Async Thunk', () => {
    it('removes user from localStorage', async () => {
      localStorage.setItem('user', JSON.stringify({ username: 'test' }));

      await store.dispatch(logoutUser());

      expect(localStorage.getItem('user')).toBeNull();
    });

    it('completes logout successfully', async () => {
      await store.dispatch(logoutUser());

      // Just verify the action completed
      expect(localStorage.getItem('user')).toBeNull();
    });

    it('handles localStorage removal errors', async () => {
      const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});
      
      // Mock localStorage to throw error
      vi.spyOn(Storage.prototype, 'removeItem').mockImplementation(() => {
        throw new Error('Storage error');
      });

      await store.dispatch(logoutUser());

      expect(consoleErrorSpy).toHaveBeenCalledWith('Error logging out user');
      
      consoleErrorSpy.mockRestore();
    });
  });

  describe('Extra Reducers - Matchers', () => {
    it('handles signInUser.fulfilled with toast', async () => {
      const toast = await import('react-toastify');
      const agent = await import('../../../app/api/agent');
      (agent.default.Account.login as any).mockResolvedValue({ username: 'test' });

      await store.dispatch(signInUser({ username: 'test', password: 'pass' }));

      expect(toast.toast.success).toHaveBeenCalledWith('Sign in successful');
    });

    it('handles signInUser.rejected with toast and error payload', async () => {
      const toast = await import('react-toastify');
      const agent = await import('../../../app/api/agent');
      (agent.default.Account.login as any).mockRejectedValue({ data: 'error' });

      await store.dispatch(signInUser({ username: 'test', password: 'wrong' }));

      expect(toast.toast.success).toHaveBeenCalledWith('Sign in failed. Please try again');
    });

    it('handles fetchCurrentUser.rejected', async () => {
      // Force rejection by making localStorage throw
      vi.spyOn(Storage.prototype, 'getItem').mockImplementation(() => {
        throw new Error('Storage error');
      });

      await store.dispatch(fetchCurrentUser());

      const state = store.getState().account;
      expect(state.user).toBeNull();
    });
  });
});
