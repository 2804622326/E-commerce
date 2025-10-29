import '@testing-library/jest-dom';
import { expect, afterEach } from 'vitest';
import { cleanup } from '@testing-library/react';

// 自动清理 DOM
afterEach(() => {
  cleanup();
});

// 扩展 expect 匹配器
expect.extend({});
