import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import Header from '../../app/layout/Header';

vi.mock('../../app/store/configureStore', () => ({
  useAppSelector: vi.fn(),
}));

vi.mock('../../app/layout/SignedInMenu', () => ({
  default: () => <div>SignedInMenu</div>,
}));

import { useAppSelector } from '../../app/store/configureStore';

describe('Header - Unauthenticated State', () => {
  it('shows Login and Register links when user is not logged in', () => {
    vi.mocked(useAppSelector).mockImplementation((selector: any) => {
      return selector({
        basket: { basket: null },
        account: { user: null },
      });
    });

    render(
      <BrowserRouter>
        <Header darkMode={false} handleThemeChange={() => {}} />
      </BrowserRouter>
    );

    expect(screen.getByText(/login/i)).toBeInTheDocument();
    expect(screen.getByText(/register/i)).toBeInTheDocument();
  });

  it('does not show SignedInMenu when user is not logged in', () => {
    vi.mocked(useAppSelector).mockImplementation((selector: any) => {
      return selector({
        basket: { basket: null },
        account: { user: null },
      });
    });

    render(
      <BrowserRouter>
        <Header darkMode={false} handleThemeChange={() => {}} />
      </BrowserRouter>
    );

    expect(screen.queryByText('SignedInMenu')).not.toBeInTheDocument();
  });

  it('shows basket icon with zero items', () => {
    vi.mocked(useAppSelector).mockImplementation((selector: any) => {
      return selector({
        basket: { basket: null },
        account: { user: null },
      });
    });

    render(
      <BrowserRouter>
        <Header darkMode={false} handleThemeChange={() => {}} />
      </BrowserRouter>
    );

    expect(screen.getByTestId('ShoppingCartIcon')).toBeInTheDocument();
  });
});

describe('Header - Authenticated State', () => {
  it('shows SignedInMenu when user is logged in', () => {
    vi.mocked(useAppSelector).mockImplementation((selector: any) => {
      return selector({
        basket: { basket: null },
        account: { user: { email: 'test@example.com' } },
      });
    });

    render(
      <BrowserRouter>
        <Header darkMode={false} handleThemeChange={() => {}} />
      </BrowserRouter>
    );

    expect(screen.getByText('SignedInMenu')).toBeInTheDocument();
  });

  it('does not show Login and Register links when user is logged in', () => {
    vi.mocked(useAppSelector).mockImplementation((selector: any) => {
      return selector({
        basket: { basket: null },
        account: { user: { email: 'test@example.com' } },
      });
    });

    render(
      <BrowserRouter>
        <Header darkMode={false} handleThemeChange={() => {}} />
      </BrowserRouter>
    );

    expect(screen.queryByText(/login/i)).not.toBeInTheDocument();
    expect(screen.queryByText(/register/i)).not.toBeInTheDocument();
  });

  it('displays basket item count badge', () => {
    vi.mocked(useAppSelector).mockImplementation((selector: any) => {
      return selector({
        basket: {
          basket: {
            items: [
              { id: 1, quantity: 2 },
              { id: 2, quantity: 3 },
            ],
          },
        },
        account: { user: { email: 'test@example.com' } },
      });
    });

    render(
      <BrowserRouter>
        <Header darkMode={false} handleThemeChange={() => {}} />
      </BrowserRouter>
    );

    expect(screen.getByText('5')).toBeInTheDocument();
  });
});

describe('Header - Navigation Links', () => {
  it('renders Home navigation link', () => {
    vi.mocked(useAppSelector).mockImplementation((selector: any) => {
      return selector({
        basket: { basket: null },
        account: { user: null },
      });
    });

    render(
      <BrowserRouter>
        <Header darkMode={false} handleThemeChange={() => {}} />
      </BrowserRouter>
    );

    expect(screen.getByText(/home/i)).toBeInTheDocument();
  });

  it('renders Store navigation link', () => {
    vi.mocked(useAppSelector).mockImplementation((selector: any) => {
      return selector({
        basket: { basket: null },
        account: { user: null },
      });
    });

    render(
      <BrowserRouter>
        <Header darkMode={false} handleThemeChange={() => {}} />
      </BrowserRouter>
    );

    expect(screen.getByText(/store/i)).toBeInTheDocument();
  });

  it('renders Contact navigation link', () => {
    vi.mocked(useAppSelector).mockImplementation((selector: any) => {
      return selector({
        basket: { basket: null },
        account: { user: null },
      });
    });

    render(
      <BrowserRouter>
        <Header darkMode={false} handleThemeChange={() => {}} />
      </BrowserRouter>
    );

    expect(screen.getByText(/contact/i)).toBeInTheDocument();
  });

  it('renders Sports Center title', () => {
    vi.mocked(useAppSelector).mockImplementation((selector: any) => {
      return selector({
        basket: { basket: null },
        account: { user: null },
      });
    });

    render(
      <BrowserRouter>
        <Header darkMode={false} handleThemeChange={() => {}} />
      </BrowserRouter>
    );

    expect(screen.getByText(/sports center/i)).toBeInTheDocument();
  });
});
