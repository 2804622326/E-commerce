import { describe, it, expect, vi, beforeEach } from 'vitest';
// Mock the app store to prevent real configureStore execution
vi.mock('../../../../src/app/store/configureStore', () => ({
  store: { getState: () => ({ account: { user: null } }) },
  useAppDispatch: () => (fn: any) => fn,
  useAppSelector: (fn: any) => fn({ account: { user: null } }),
}));
import { configureStore } from '@reduxjs/toolkit';
import { accountSlice, signInUser, fetchCurrentUser } from '../../../../src/features/account/accountSlice';

// Note: import path above uses workspace structure; adjust if necessary

describe('accountSlice reducers and thunks', () => {
  let store: any;

  beforeEach(() => {
    vi.clearAllMocks();
    store = configureStore({
      reducer: {
        account: accountSlice.reducer
      }
    });
  });

  it('should set user on signInUser.fulfilled', async () => {
    const user = { username: 'test', token: 'abc' };
    await store.dispatch({ type: signInUser.fulfilled.type, payload: user });
    const state = store.getState().account;
    expect(state.user).toEqual(user);
    expect(state.error).toBeNull();
  });

  it('should set error on signInUser.rejected', async () => {
    await store.dispatch({ type: signInUser.rejected.type, payload: 'Invalid' });
    const state = store.getState().account;
    expect(state.error).toBe('Invalid');
  });

  it('should set user on fetchCurrentUser.fulfilled with null payload', async () => {
    await store.dispatch({ type: fetchCurrentUser.fulfilled.type, payload: null });
    const state = store.getState().account;
    expect(state.user).toBeNull();
    expect(state.error).toBeNull();
  });

  it('should clear user on logout', async () => {
    await store.dispatch({ type: signInUser.fulfilled.type, payload: { username: 'x' } });
    // Dispatch the slice's logOut action to clear the user
    store.dispatch(accountSlice.actions.logOut());
    const state = store.getState().account;
    expect(state.user).toBeNull();
  });
});
