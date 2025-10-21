# E-commerce React 项目测试文档

## 📋 项目概述

本项目为 E-commerce React 应用配置了完整的测试环境，使用 Vitest 作为测试运行器，React Testing Library 进行组件测试。

## 🛠️ 安装的依赖

```bash
npm install --save-dev \
  @testing-library/react \
  @testing-library/jest-dom \
  @testing-library/user-event \
  vitest \
  jsdom \
  @vitest/ui \
  @vitest/coverage-v8
```

## 📁 测试文件结构

```
client/src/__tests__/
├── components/
│   ├── BasketSummary.test.tsx    # 购物车摘要组件测试
│   ├── Header.test.tsx            # 头部组件测试
│   ├── ProductCard.test.tsx       # 产品卡片组件测试
│   └── Snapshot.test.tsx          # 快照测试
├── features/
│   ├── Catalog.test.tsx           # 商品目录异步测试
│   └── SignInPage.test.tsx        # 登录页面交互测试
├── mocks/
│   └── mockData.ts                # Mock 数据定义
└── utils/
    └── test-utils.tsx             # 测试工具函数
```

## 🧪 测试类型

### 1. 渲染测试 (Rendering Tests)
- **BasketSummary.test.tsx**: 验证购物车摘要正确渲染
- **Header.test.tsx**: 验证导航栏和购物车数量显示
- **ProductCard.test.tsx**: 验证产品卡片信息展示

### 2. 用户交互测试 (User Interaction Tests)
- **SignInPage.test.tsx**: 
  - 表单输入验证
  - 错误消息显示
  - 复选框切换
  - 导航链接

### 3. 快照测试 (Snapshot Tests)
- **Snapshot.test.tsx**: 捕获组件 UI 快照，防止意外变更

### 4. 异步数据测试 (Async Tests)
- **Catalog.test.tsx**: 
  - API 数据加载
  - 加载状态显示
  - 数据渲染验证

## 🚀 运行测试

### 运行所有测试
```bash
cd client
npm test
```

### 运行测试并生成覆盖率报告
```bash
npm run test:coverage
```

### 使用 UI 界面运行测试
```bash
npm run test:ui
```

### 监视模式（开发时使用）
```bash
npm run test:watch
```

## 📊 覆盖率要求

在 `vitest.config.ts` 中配置的覆盖率阈值：

```typescript
coverage: {
  thresholds: {
    branches: 80,    // 分支覆盖率 ≥ 80%
    functions: 85,   // 函数覆盖率 ≥ 85%
    lines: 90,       // 行覆盖率 ≥ 90%
    statements: 90,  // 语句覆盖率 ≥ 90%
  },
}
```

## 📄 生成的报告

运行 `npm run test:coverage` 后，会生成以下报告：

1. **终端文本报告**: 直接在命令行显示覆盖率摘要
2. **HTML 报告**: 在 `client/coverage/index.html` 查看详细报告
3. **LCOV 报告**: 可用于 CI/CD 集成

## 🎯 测试最佳实践

### ✅ 已实现的最佳实践

1. **隔离测试环境**: 使用 Redux store 的独立实例
2. **Mock 外部依赖**: API 调用、路由等已被 mock
3. **可读性强的测试**: 使用描述性的测试名称
4. **全面的断言**: 验证文本、状态、属性等
5. **自动清理**: 每个测试后自动清理 DOM

### 📝 编写新测试的建议

```typescript
// 1. 导入必要的测试工具
import { describe, it, expect, vi } from 'vitest';
import { screen, waitFor } from '@testing-library/react';
import { renderWithProviders } from '../../utils/test-utils';

// 2. Mock 外部依赖
vi.mock('../../../app/api/agent', () => ({
  default: {
    // mock 实现
  },
}));

// 3. 编写测试套件
describe('MyComponent', () => {
  it('should render correctly', () => {
    renderWithProviders(<MyComponent />, {
      preloadedState: {
        basket: { basket: null },
        account: { user: null },
      },
    });

    expect(screen.getByText('Expected Text')).toBeInTheDocument();
  });
});
```

## 🔧 配置文件

### vitest.config.ts
- 测试环境: jsdom
- 全局变量: 启用
- 覆盖率提供者: v8
- Setup 文件: setupTests.ts

### tsconfig.json
- 添加了 Vitest 和 Testing Library 类型定义

### package.json 新增脚本
```json
{
  "test": "vitest",
  "test:ui": "vitest --ui",
  "test:coverage": "vitest run --coverage",
  "test:watch": "vitest --watch"
}
```

## 🐛 常见问题排查

### 问题 1: 模块导入错误
**解决方案**: 确保 tsconfig.json 包含 `"types": ["vitest/globals", "@testing-library/jest-dom"]`

### 问题 2: Redux store 错误
**解决方案**: 使用 `renderWithProviders` 而不是直接使用 `render`

### 问题 3: 异步测试超时
**解决方案**: 在 `waitFor` 中增加 timeout 参数：
```typescript
await waitFor(() => {
  expect(screen.getByText('Data')).toBeInTheDocument();
}, { timeout: 3000 });
```

## 📈 后续改进建议

1. **增加集成测试**: 测试多个组件协同工作
2. **E2E 测试**: 使用 Playwright 或 Cypress
3. **CI/CD 集成**: 在 GitHub Actions 中自动运行测试
4. **性能测试**: 监控组件渲染性能
5. **可访问性测试**: 使用 jest-axe 检查 a11y 问题

## 📚 参考资源

- [Vitest 官方文档](https://vitest.dev/)
- [React Testing Library](https://testing-library.com/react)
- [Testing Library 最佳实践](https://kentcdodds.com/blog/common-mistakes-with-react-testing-library)

---

**创建日期**: 2025-10-20  
**维护者**: 开发团队
