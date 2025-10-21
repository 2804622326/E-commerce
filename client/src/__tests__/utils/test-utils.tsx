import { ReactElement, ReactNode } from 'react';
import { render, RenderOptions } from '@testing-library/react';
import { Provider } from 'react-redux';
import { BrowserRouter, MemoryRouter } from 'react-router-dom';
import { configureStore } from '@reduxjs/toolkit';
import { basketSlice } from '../../features/basket/basketSlice';
import { accountSlice } from '../../features/account/accountSlice';

interface ExtendedRenderOptions extends Omit<RenderOptions, 'queries'> {
  preloadedState?: any;
  store?: any;
  route?: string;
  useMemoryRouter?: boolean;
}

/**
 * 创建测试用的 Redux store
 */
export function createTestStore(preloadedState?: any): any {
  return configureStore({
    reducer: {
      // Cast to any to avoid type mismatches in test environment
      basket: (basketSlice as any).reducer,
      account: (accountSlice as any).reducer,
    } as any,
    preloadedState,
  });
}

/**
 * 渲染带有 Redux Provider 和 Router 的组件
 */
export function renderWithProviders(
  ui: ReactElement,
  {
    preloadedState,
    store = createTestStore(preloadedState),
    route = '/',
    useMemoryRouter = false,
    ...renderOptions
  }: ExtendedRenderOptions = {}
) {
  function Wrapper({ children }: { children: ReactNode }) {
    const RouterComponent = useMemoryRouter ? MemoryRouter : BrowserRouter;
    const routerProps = useMemoryRouter ? { initialEntries: [route] } : {};
    
    return (
      <Provider store={store}>
        <RouterComponent {...routerProps}>{children}</RouterComponent>
      </Provider>
    );
  }

  return { store, ...render(ui, { wrapper: Wrapper, ...renderOptions }) };
}

export * from '@testing-library/react';
