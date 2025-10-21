import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import RequireAuth from '../../app/router/RequireAuth';

// Simple mock for useAppSelector
const mockUseAppSelector = vi.fn();
vi.mock('../../app/store/configureStore', () => ({
  useAppSelector: () => mockUseAppSelector()
}));

// Mock Navigate component
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    Navigate: ({ to }: { to: string }) => <div data-testid="navigate">Redirecting to {to}</div>
  };
});

const TestChild = () => <div data-testid="protected-content">Protected Content</div>;

describe('RequireAuth Component', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders outlet content when user is authenticated', () => {
    mockUseAppSelector.mockReturnValue({ user: { email: 'test@test.com' } });
    
    render(
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<RequireAuth />}>
            <Route index element={<TestChild />} />
          </Route>
        </Routes>
      </BrowserRouter>
    );

    expect(screen.getByTestId('protected-content')).toBeInTheDocument();
  });

  it('redirects to login when user is not authenticated', () => {
    mockUseAppSelector.mockReturnValue({ user: null });
    
    render(
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<RequireAuth />}>
            <Route index element={<TestChild />} />
          </Route>
        </Routes>
      </BrowserRouter>
    );

    expect(screen.getByTestId('navigate')).toBeInTheDocument();
    expect(screen.getByText('Redirecting to /login')).toBeInTheDocument();
  });

  it('redirects to login when user is undefined', () => {
    mockUseAppSelector.mockReturnValue({});
    
    render(
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<RequireAuth />}>
            <Route index element={<TestChild />} />
          </Route>
        </Routes>
      </BrowserRouter>
    );

    expect(screen.getByTestId('navigate')).toBeInTheDocument();
  });
});