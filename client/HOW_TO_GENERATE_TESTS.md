# 如何生成 E-commerce 测试工作区

本文档描述了如何从零开始为 E-commerce React 项目设置完整的单元测试环境。

---

## 📦 第一步：安装测试依赖

在项目的 `client` 目录下运行：

```bash
cd client

npm install --save-dev \
  @testing-library/react \
  @testing-library/jest-dom \
  @testing-library/user-event \
  vitest \
  jsdom \
  @vitest/ui \
  @vitest/coverage-v8 \
  @types/node
```

### 依赖说明：
- **@testing-library/react**: React 组件测试库
- **@testing-library/jest-dom**: DOM 匹配器
- **@testing-library/user-event**: 模拟用户交互
- **vitest**: Vite 原生测试运行器
- **jsdom**: 浏览器环境模拟
- **@vitest/ui**: 测试 UI 界面
- **@vitest/coverage-v8**: 代码覆盖率工具
- **@types/node**: Node.js 类型定义

---

## ⚙️ 第二步：配置测试环境

### 1. 创建 `vitest.config.ts`

在 `client/` 目录下创建：

```typescript
/// <reference types="vitest" />
import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react-swc';
import { resolve } from 'path';

export default defineConfig({
  plugins: [react()],
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './src/setupTests.ts',
    css: true,
    include: ['src/**/*.{test,spec}.{ts,tsx}'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'html', 'lcov'],
      exclude: [
        'node_modules/',
        'src/setupTests.ts',
        '**/*.d.ts',
        '**/*.config.*',
        '**/mockData.ts',
        'src/main.tsx',
      ],
      thresholds: {
        branches: 80,
        functions: 85,
        lines: 90,
        statements: 90,
      },
    },
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, './src'),
    },
  },
});
```

### 2. 创建 `src/setupTests.ts`

```typescript
import '@testing-library/jest-dom';
import { expect, afterEach } from 'vitest';
import { cleanup } from '@testing-library/react';

// 自动清理 DOM
afterEach(() => {
  cleanup();
});

// 扩展 expect 匹配器
expect.extend({});
```

### 3. 更新 `tsconfig.json`

在 `compilerOptions` 中添加：

```json
{
  "compilerOptions": {
    // ... 其他配置
    "types": ["vitest/globals", "@testing-library/jest-dom"]
  }
}
```

### 4. 更新 `package.json`

在 `scripts` 中添加：

```json
{
  "scripts": {
    "test": "vitest",
    "test:ui": "vitest --ui",
    "test:coverage": "vitest run --coverage",
    "test:watch": "vitest --watch"
  }
}
```

---

## 📁 第三步：创建测试目录结构

```bash
mkdir -p src/__tests__/{components,features,mocks,utils}
```

创建的目录结构：
```
src/__tests__/
├── components/     # 组件测试
├── features/       # 功能模块测试
├── mocks/          # Mock 数据
└── utils/          # 测试工具函数
```

---

## 🧪 第四步：编写测试文件

### 1. 创建 Mock 数据 (`src/__tests__/mocks/mockData.ts`)

```typescript
import { Product } from '../../app/models/product';
import { Basket } from '../../app/models/basket';

export const mockProduct: Product = {
  id: 1,
  name: 'Test Product',
  description: 'Test description',
  price: 1000,
  pictureUrl: '/images/products/test.jpg',
  productType: 'Test Type',
  productBrand: 'Test Brand',
};

export const mockBasket: Basket = {
  id: 'basket-123',
  items: [
    {
      id: 1,
      name: 'Tennis Racket',
      description: 'Professional racket',
      price: 2500,
      pictureUrl: '/images/products/racket.jpg',
      productBrand: 'Wilson',
      productType: 'Equipment',
      quantity: 2,
    },
  ],
};
```

### 2. 创建测试工具 (`src/__tests__/utils/test-utils.tsx`)

```typescript
import { ReactElement } from 'react';
import { render, RenderOptions } from '@testing-library/react';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';
import { configureStore } from '@reduxjs/toolkit';
import { basketSlice } from '../../features/basket/basketSlice';
import { accountSlice } from '../../features/account/accountSlice';

interface ExtendedRenderOptions extends Omit<RenderOptions, 'queries'> {
  preloadedState?: any;
  store?: any;
}

export function renderWithProviders(
  ui: ReactElement,
  {
    preloadedState,
    store = configureStore({
      reducer: {
        basket: basketSlice.reducer,
        account: accountSlice.reducer,
      },
      ...(preloadedState && { preloadedState }),
    }),
    ...renderOptions
  }: ExtendedRenderOptions = {}
) {
  function Wrapper({ children }: { children: React.ReactNode }) {
    return (
      <Provider store={store}>
        <BrowserRouter>{children}</BrowserRouter>
      </Provider>
    );
  }

  return { store, ...render(ui, { wrapper: Wrapper, ...renderOptions }) };
}

export * from '@testing-library/react';
```

### 3. 编写基础测试 (`src/__tests__/basic.test.ts`)

```typescript
import { describe, it, expect } from 'vitest';

describe('Basic Math Test', () => {
  it('should add two numbers correctly', () => {
    expect(1 + 1).toBe(2);
  });

  it('string contains substring', () => {
    expect('Hello World').toContain('World');
  });
});
```

---

## 🚀 第五步：运行测试

### 运行所有测试
```bash
npm test
```

### 运行特定测试文件
```bash
npm test -- src/__tests__/basic.test.ts
```

### 生成覆盖率报告
```bash
npm run test:coverage
```

覆盖率报告将生成在 `client/coverage/` 目录：
- **终端输出**: 文本格式摘要
- **HTML 报告**: `coverage/index.html`（在浏览器中打开）
- **LCOV 报告**: 用于 CI/CD 集成

### 使用 UI 界面
```bash
npm run test:ui
```

访问 `http://localhost:51204/__vitest__/` 查看可视化测试界面。

### 监视模式（开发时推荐）
```bash
npm run test:watch
```

---

## 📊 测试类型示例

### 1. 渲染测试

```typescript
import { describe, it, expect } from 'vitest';
import { screen } from '@testing-library/react';
import { renderWithProviders } from '../utils/test-utils';
import MyComponent from '../../features/MyComponent';

describe('MyComponent', () => {
  it('renders component text', () => {
    renderWithProviders(<MyComponent />, {
      preloadedState: {
        basket: { basket: null },
        account: { user: null },
      },
    });

    expect(screen.getByText(/expected text/i)).toBeInTheDocument();
  });
});
```

### 2. 用户交互测试

```typescript
import { describe, it, expect } from 'vitest';
import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithProviders } from '../utils/test-utils';
import MyForm from '../../features/MyForm';

describe('MyForm Interactions', () => {
  it('allows user to type in input field', async () => {
    const user = userEvent.setup();
    renderWithProviders(<MyForm />);

    const input = screen.getByLabelText(/username/i);
    await user.type(input, 'testuser');

    expect(input).toHaveValue('testuser');
  });
});
```

### 3. 异步测试

```typescript
import { describe, it, expect, vi } from 'vitest';
import { screen, waitFor } from '@testing-library/react';
import { renderWithProviders } from '../utils/test-utils';
import DataList from '../../features/DataList';

vi.mock('../../app/api/agent', () => ({
  default: {
    Store: {
      list: vi.fn(() => Promise.resolve({ content: mockData })),
    },
  },
}));

describe('DataList Async', () => {
  it('loads and displays data', async () => {
    renderWithProviders(<DataList />);

    await waitFor(() => {
      expect(screen.getByText('Expected Item')).toBeInTheDocument();
    });
  });
});
```

---

## 🔍 故障排查

### 问题 1: 模块解析错误
**错误**: `Cannot find module '../../utils/test-utils'`

**解决方案**:
1. 确保文件扩展名为 `.tsx` 或 `.ts`
2. 检查相对路径是否正确
3. 使用绝对路径或路径别名

### 问题 2: Redux Store 错误
**错误**: `Cannot read properties of undefined (reading 'reducer')`

**解决方案**:
- 使用 `renderWithProviders` 而不是 `render`
- 确保 preloadedState 包含所有必需的 slice

### 问题 3: 测试超时
**错误**: `Test timed out after 5000ms`

**解决方案**:
```typescript
await waitFor(() => {
  expect(screen.getByText('Data')).toBeInTheDocument();
}, { timeout: 10000 }); // 增加超时时间
```

---

## 📚 后续步骤

1. **扩展测试覆盖率**: 为更多组件编写测试
2. **集成 CI/CD**: 在 GitHub Actions 中自动运行测试
3. **E2E 测试**: 使用 Playwright 或 Cypress
4. **性能测试**: 监控组件渲染性能
5. **可访问性测试**: 使用 jest-axe

---

## 📖 参考资源

- [Vitest 官方文档](https://vitest.dev/)
- [React Testing Library](https://testing-library.com/react)
- [Testing Best Practices](https://kentcdodds.com/blog/common-mistakes-with-react-testing-library)

---

**生成日期**: 2025-10-20  
**作者**: GitHub Copilot  
**版本**: 1.0
