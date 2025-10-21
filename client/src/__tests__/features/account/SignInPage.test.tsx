import { describe, it, expect, vi, beforeEach } from 'vitest';
// Mock the app store to prevent real configureStore execution during import
vi.mock('../../../../src/app/store/configureStore', () => ({
  store: { getState: () => ({ account: { user: null } }) },
  useAppDispatch: () => () => Promise.resolve(),
  useAppSelector: (fn: any) => fn({ account: { user: null } }),
}));
import { renderWithProviders, screen, fireEvent, waitFor } from '../../utils/test-utils';
import SignInPage from '../../../../src/features/account/SignInPage';

describe('SignInPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders sign in form', () => {
    renderWithProviders(<SignInPage />);
    expect(screen.getByText('Sign in')).toBeInTheDocument();
    expect(screen.getByLabelText(/Username/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Password/i)).toBeInTheDocument();
  });

  it('shows validation messages when fields empty', async () => {
    renderWithProviders(<SignInPage />);
    const username = screen.getByLabelText(/Username/i);
    const password = screen.getByLabelText(/Password/i);
    fireEvent.blur(username);
    fireEvent.blur(password);

    await waitFor(() => {
      expect(screen.getByText('Username is required')).toBeInTheDocument();
      expect(screen.getByText('Password is required')).toBeInTheDocument();
    });
  });
});
