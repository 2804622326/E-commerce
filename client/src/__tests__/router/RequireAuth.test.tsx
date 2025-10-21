import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import RequireAuth from '../../app/router/RequireAuth';

// Mock the store
const mockUseAppSelector = vi.fn();
vi.mock('../../app/store/configureStore', () => ({
  useAppSelector: () => mockUseAppSelector()
}));

// Mock useLocation
const mockUseLocation = vi.fn();

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useLocation: () => mockUseLocation(),
    Navigate: ({ to }: { to: string }) => <div>Redirecting to {to}</div>,
  };
});

const TestComponent = () => <div>Protected Content</div>;

const RequireAuthWithTestSetup = ({ children }: { children: React.ReactNode }) => (
  <MemoryRouter initialEntries={['/protected']}>
    <Routes>
      <Route path="/protected" element={<RequireAuth />}>
        <Route path="/protected" element={children as any} />
      </Route>
    </Routes>
  </MemoryRouter>
);

describe('RequireAuth Component', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    mockUseLocation.mockReturnValue({ pathname: '/test', state: null });
  });

  it('should render children when user is authenticated', () => {
    mockUseAppSelector.mockReturnValue({
      user: { id: 1, username: 'testuser' }
    });

    render(
      <RequireAuthWithTestSetup>
        <TestComponent />
      </RequireAuthWithTestSetup>
    );

    expect(screen.getByText('Protected Content')).toBeInTheDocument();
  });

  it('should redirect to login when user is not authenticated', () => {
    mockUseAppSelector.mockReturnValue({
      user: null
    });

    render(
      <RequireAuthWithTestSetup>
        <TestComponent />
      </RequireAuthWithTestSetup>
    );

  // Should show redirect message instead of protected content
  expect(screen.getByText('Redirecting to /login')).toBeInTheDocument();
    expect(screen.queryByText('Protected Content')).not.toBeInTheDocument();
  });

  it('should call useAppSelector with correct selector', () => {
    mockUseAppSelector.mockReturnValue({
      user: { id: 1, username: 'testuser' }
    });

    render(
      <RequireAuthWithTestSetup>
        <TestComponent />
      </RequireAuthWithTestSetup>
    );

    expect(mockUseAppSelector).toHaveBeenCalled();
  });

  it('should call useLocation hook', () => {
    mockUseAppSelector.mockReturnValue({
      user: { id: 1, username: 'testuser' }
    });

    render(
      <RequireAuthWithTestSetup>
        <TestComponent />
      </RequireAuthWithTestSetup>
    );

    expect(mockUseLocation).toHaveBeenCalled();
  });
});