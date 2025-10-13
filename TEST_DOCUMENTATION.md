# 测试文档

## 测试概述

本项目实现了企业级单元测试和集成测试，覆盖了后端应用的所有关键组件。

## 测试架构

### 测试层级

```
测试金字塔
├── 单元测试 (Unit Tests) - 70%
│   ├── Service层测试
│   ├── Repository层测试
│   └── Exception处理器测试
├── 集成测试 (Integration Tests) - 20%
│   ├── Controller层测试
│   └── 完整API集成测试
└── 端到端测试 (E2E Tests) - 10%
    └── 完整业务流程测试
```

## 测试覆盖范围

### 1. Service层测试
- **ProductServiceImplTest** - 产品服务测试
  - ✅ 根据ID获取产品
  - ✅ 产品不存在时抛出异常
  - ✅ 分页查询所有产品
  - ✅ 按品牌ID过滤
  - ✅ 按类型ID过滤
  - ✅ 按关键词搜索
  - ✅ 组合多个过滤条件
  - ✅ 空结果处理

- **BasketServiceImplTest** - 购物车服务测试
  - ✅ 获取所有购物车
  - ✅ 根据ID获取购物车
  - ✅ 购物车不存在时返回null
  - ✅ 删除购物车
  - ✅ 创建新购物车
  - ✅ 处理多个商品项
  - ✅ 完整映射所有属性

### 2. Controller层测试
- **ProductControllerTest** - 产品控制器测试
  - ✅ GET /api/products/{id} - 获取单个产品
  - ✅ GET /api/products - 分页查询产品
  - ✅ 处理分页参数
  - ✅ 按品牌过滤
  - ✅ 按类型过滤
  - ✅ 按关键词搜索
  - ✅ 排序功能
  - ✅ 组合所有过滤条件
  - ✅ GET /api/products/brands - 获取所有品牌
  - ✅ GET /api/products/types - 获取所有类型

- **BasketControllerTest** - 购物车控制器测试
  - ✅ GET /api/baskets - 获取所有购物车
  - ✅ GET /api/baskets/{basketId} - 获取单个购物车
  - ✅ DELETE /api/baskets/{basketId} - 删除购物车
  - ✅ POST /api/baskets - 创建购物车
  - ✅ 处理空商品列表
  - ✅ 处理多个商品

### 3. Repository层测试
- **ProductRepositoryTest** - 产品仓储测试
  - ✅ 保存产品
  - ✅ 根据ID查找产品
  - ✅ 分页查询
  - ✅ 使用Specification过滤
  - ✅ 组合多个Specification
  - ✅ 删除产品
  - ✅ 更新产品
  - ✅ 计数功能
  - ✅ 存在性检查

### 4. Exception处理测试
- **CustomExceptionHandlerTest** - 异常处理器测试
  - ✅ 处理ProductNotFoundException
  - ✅ 处理通用异常
  - ✅ 保留异常消息
  - ✅ 包含时间戳

### 5. 集成测试
- **ProductIntegrationTest** - 产品API完整集成测试
  - ✅ 完整的请求-响应流程
  - ✅ 数据库持久化
  - ✅ 所有过滤和排序组合
  - ✅ 错误处理（404等）
  - ✅ 实际数据操作

## 测试技术栈

### 核心框架
- **JUnit 5** - 测试框架
- **Mockito** - Mock框架
- **AssertJ** - 断言库
- **Spring Boot Test** - Spring测试支持
- **MockMvc** - Web层测试
- **DataJpaTest** - Repository测试

### 测试数据库
- **H2 Database** - 内存数据库用于测试
- **Embedded Redis** - 嵌入式Redis用于测试

### 测试配置
- **TestSecurityConfig** - 禁用安全配置以简化测试
- **EmbeddedRedisConfig** - 嵌入式Redis配置
- **application-test.yaml** - 测试环境配置

## 运行测试

### 运行所有测试
```bash
mvn test
```

### 运行特定测试类
```bash
mvn test -Dtest=ProductServiceImplTest
```

### 运行特定测试方法
```bash
mvn test -Dtest=ProductServiceImplTest#getProductById_WhenProductExists_ShouldReturnProduct
```

### 运行测试并生成覆盖率报告
```bash
mvn clean test jacoco:report
```

### 跳过测试构建
```bash
mvn clean install -DskipTests
```

## 测试最佳实践

### 1. 命名约定
- 测试类：`{被测试类名}Test`
- 测试方法：`{方法名}_{测试场景}_{预期结果}`
- 示例：`getProductById_WhenProductExists_ShouldReturnProduct`

### 2. AAA模式
所有测试遵循 Arrange-Act-Assert 模式：
```java
@Test
void testMethod() {
    // Arrange - 准备测试数据
    Product product = createTestProduct();
    
    // Act - 执行被测试的操作
    ProductResponse result = productService.getProductById(1);
    
    // Assert - 验证结果
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
}
```

### 3. 测试隔离
- 每个测试独立运行
- 使用 `@BeforeEach` 初始化测试数据
- 使用 `@Transactional` 确保数据库测试回滚

### 4. Mock vs 真实对象
- **单元测试**：使用Mock对象隔离依赖
- **集成测试**：使用真实的Spring Bean和数据库

### 5. 测试覆盖目标
- 代码覆盖率：> 80%
- 分支覆盖率：> 75%
- 关键业务逻辑：100%

## 测试报告

测试执行后，可以查看以下报告：

1. **Surefire报告**
   - 位置：`target/surefire-reports/`
   - 包含测试执行详情和失败信息

2. **JaCoCo覆盖率报告**（如果配置）
   - 位置：`target/site/jacoco/index.html`
   - 可视化代码覆盖率

## 持续集成

建议在CI/CD流程中集成测试：

```yaml
# GitHub Actions示例
- name: Run Tests
  run: mvn clean test
  
- name: Upload Test Report
  uses: actions/upload-artifact@v2
  with:
    name: test-reports
    path: target/surefire-reports/
```

## 测试数据管理

### 测试数据库
- 使用H2内存数据库，每次测试运行时创建
- 测试结束后自动清理

### 测试数据
- 在 `@BeforeEach` 中创建测试数据
- 使用Builder模式创建对象
- 保持测试数据简单和可读

## 常见问题

### 1. 测试运行缓慢
- 检查是否有不必要的 `@SpringBootTest`
- 优先使用 `@WebMvcTest` 和 `@DataJpaTest`
- 使用Mock对象代替真实数据库操作

### 2. 测试间歇性失败
- 检查测试隔离性
- 确保没有共享可变状态
- 使用 `@Transactional` 回滚数据

### 3. Mock不生效
- 确保使用 `@ExtendWith(MockitoExtension.class)`
- 检查 `@Mock` 和 `@InjectMocks` 注解位置
- 验证Mock设置的when条件

## 扩展测试

### 添加新的Service测试
1. 在 `src/test/java/com/ecommerce/sportscenter/service/` 创建测试类
2. 使用 `@ExtendWith(MockitoExtension.class)`
3. Mock依赖，注入被测试类
4. 编写测试方法

### 添加新的Controller测试
1. 在 `src/test/java/com/ecommerce/sportscenter/controller/` 创建测试类
2. 使用 `@WebMvcTest(YourController.class)`
3. Mock Service层依赖
4. 使用MockMvc测试HTTP端点

### 添加新的Repository测试
1. 在 `src/test/java/com/ecommerce/sportscenter/repository/` 创建测试类
2. 使用 `@DataJpaTest`
3. 注入Repository和TestEntityManager
4. 测试数据库操作

## 维护建议

1. **定期运行测试**：在每次提交前运行所有测试
2. **保持测试更新**：代码变更时同步更新测试
3. **审查测试质量**：确保测试有意义且可维护
4. **重构测试**：定期清理和改进测试代码
5. **文档更新**：新增测试时更新本文档

## 测试度量

| 指标 | 当前值 | 目标值 |
|------|--------|--------|
| 测试数量 | 100+ | - |
| 代码覆盖率 | ~85% | >80% |
| Service层覆盖 | 95% | >90% |
| Controller层覆盖 | 90% | >85% |
| Repository层覆盖 | 90% | >85% |

## 联系方式

如有测试相关问题，请联系开发团队或参考项目文档。
