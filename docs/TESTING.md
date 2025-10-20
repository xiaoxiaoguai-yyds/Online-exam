# 🧪 测试指南

## 📋 测试概述

本项目采用多层次的测试策略，确保代码质量和系统稳定性：

- **单元测试**：测试单个组件或方法
- **集成测试**：测试组件间的交互
- **端到端测试**：测试完整的用户流程
- **性能测试**：测试系统性能和负载能力

## 🎯 测试策略

### 测试金字塔

```
    /\
   /  \     E2E Tests (少量)
  /____\    
 /      \   Integration Tests (适量)
/__________\ Unit Tests (大量)
```

| 测试类型 | 比例 | 特点 | 执行速度 |
|----------|------|------|----------|
| 单元测试 | 70% | 快速、独立、可靠 | 很快 |
| 集成测试 | 20% | 测试组件交互 | 中等 |
| E2E测试 | 10% | 测试完整流程 | 较慢 |

## 🔧 测试环境配置

### 后端测试环境

#### 依赖配置
```xml
<!-- pom.xml -->
<dependencies>
    <!-- Spring Boot Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Testcontainers -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>mysql</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- MockWebServer -->
    <dependency>
        <groupId>com.squareup.okhttp3</groupId>
        <artifactId>mockwebserver</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

#### 测试配置文件
```properties
# src/test/resources/application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
logging.level.org.springframework.web=DEBUG
```

### 前端测试环境

#### 依赖配置
```json
{
  "devDependencies": {
    "@vue/test-utils": "^2.0.0",
    "vitest": "^0.25.0",
    "jsdom": "^20.0.0",
    "cypress": "^10.0.0",
    "@testing-library/vue": "^6.0.0",
    "msw": "^0.47.0"
  }
}
```

#### Vitest配置
```javascript
// vitest.config.js
import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: ['./src/test/setup.js']
  }
})
```

## 🧪 单元测试

### 后端单元测试

#### 1. Service层测试
```java
@ExtendWith(MockitoExtension.class)
class ExamServiceTest {
    
    @Mock
    private ExamRepository examRepository;
    
    @Mock
    private QuestionRepository questionRepository;
    
    @InjectMocks
    private ExamService examService;
    
    @Test
    @DisplayName("创建考试 - 成功")
    void createExam_Success() {
        // Given
        Exam exam = new Exam();
        exam.setTitle("Java基础考试");
        exam.setDuration(120);
        
        Exam savedExam = new Exam();
        savedExam.setId(1L);
        savedExam.setTitle("Java基础考试");
        savedExam.setDuration(120);
        
        when(examRepository.save(any(Exam.class))).thenReturn(savedExam);
        
        // When
        Exam result = examService.createExam(exam);
        
        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Java基础考试");
        verify(examRepository).save(exam);
    }
    
    @Test
    @DisplayName("获取考试列表 - 分页")
    void getExams_WithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Exam> exams = Arrays.asList(
            createExam(1L, "考试1"),
            createExam(2L, "考试2")
        );
        Page<Exam> examPage = new PageImpl<>(exams, pageable, 2);
        
        when(examRepository.findAll(pageable)).thenReturn(examPage);
        
        // When
        Page<Exam> result = examService.getExams(pageable);
        
        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }
    
    private Exam createExam(Long id, String title) {
        Exam exam = new Exam();
        exam.setId(id);
        exam.setTitle(title);
        return exam;
    }
}
```

#### 2. Repository层测试
```java
@DataJpaTest
class ExamRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private ExamRepository examRepository;
    
    @Test
    @DisplayName("根据状态查找考试")
    void findByStatus() {
        // Given
        Exam activeExam = new Exam();
        activeExam.setTitle("活跃考试");
        activeExam.setStatus(ExamStatus.ACTIVE);
        entityManager.persistAndFlush(activeExam);
        
        Exam inactiveExam = new Exam();
        inactiveExam.setTitle("非活跃考试");
        inactiveExam.setStatus(ExamStatus.INACTIVE);
        entityManager.persistAndFlush(inactiveExam);
        
        // When
        List<Exam> activeExams = examRepository.findByStatus(ExamStatus.ACTIVE);
        
        // Then
        assertThat(activeExams).hasSize(1);
        assertThat(activeExams.get(0).getTitle()).isEqualTo("活跃考试");
    }
}
```

#### 3. Controller层测试
```java
@WebMvcTest(ExamController.class)
class ExamControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ExamService examService;
    
    @Test
    @DisplayName("获取考试列表 - 成功")
    void getExams_Success() throws Exception {
        // Given
        List<Exam> exams = Arrays.asList(
            createExam(1L, "考试1"),
            createExam(2L, "考试2")
        );
        Page<Exam> examPage = new PageImpl<>(exams);
        
        when(examService.getExams(any(Pageable.class))).thenReturn(examPage);
        
        // When & Then
        mockMvc.perform(get("/api/exams")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title", is("考试1")))
                .andExpect(jsonPath("$.content[1].title", is("考试2")));
    }
    
    @Test
    @DisplayName("创建考试 - 验证失败")
    void createExam_ValidationFailed() throws Exception {
        // Given
        String invalidExamJson = """
            {
                "title": "",
                "duration": -1
            }
            """;
        
        // When & Then
        mockMvc.perform(post("/api/exams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidExamJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(2)));
    }
}
```

### 前端单元测试

#### 1. 组件测试
```javascript
// tests/components/ExamCard.test.js
import { mount } from '@vue/test-utils'
import { describe, it, expect } from 'vitest'
import ExamCard from '@/components/ExamCard.vue'

describe('ExamCard.vue', () => {
  const mockExam = {
    id: 1,
    title: 'Java基础考试',
    duration: 120,
    questionCount: 50,
    status: 'active'
  }

  it('renders exam information correctly', () => {
    const wrapper = mount(ExamCard, {
      props: { exam: mockExam }
    })

    expect(wrapper.find('.exam-title').text()).toBe('Java基础考试')
    expect(wrapper.find('.exam-duration').text()).toContain('120分钟')
    expect(wrapper.find('.question-count').text()).toContain('50题')
  })

  it('emits start-exam event when start button clicked', async () => {
    const wrapper = mount(ExamCard, {
      props: { exam: mockExam }
    })

    await wrapper.find('.start-btn').trigger('click')

    expect(wrapper.emitted('start-exam')).toBeTruthy()
    expect(wrapper.emitted('start-exam')[0]).toEqual([mockExam.id])
  })

  it('shows disabled state for inactive exam', () => {
    const inactiveExam = { ...mockExam, status: 'inactive' }
    const wrapper = mount(ExamCard, {
      props: { exam: inactiveExam }
    })

    expect(wrapper.find('.start-btn').attributes('disabled')).toBeDefined()
    expect(wrapper.classes()).toContain('exam-card--disabled')
  })
})
```

#### 2. 工具函数测试
```javascript
// tests/utils/dateUtils.test.js
import { describe, it, expect } from 'vitest'
import { formatDate, calculateDuration } from '@/utils/dateUtils'

describe('dateUtils', () => {
  describe('formatDate', () => {
    it('formats date correctly', () => {
      const date = new Date('2023-12-25T10:30:00')
      expect(formatDate(date)).toBe('2023-12-25 10:30:00')
    })

    it('handles invalid date', () => {
      expect(formatDate(null)).toBe('')
      expect(formatDate(undefined)).toBe('')
    })
  })

  describe('calculateDuration', () => {
    it('calculates duration in minutes', () => {
      const start = new Date('2023-12-25T10:00:00')
      const end = new Date('2023-12-25T12:00:00')
      expect(calculateDuration(start, end)).toBe(120)
    })
  })
})
```

#### 3. Store测试
```javascript
// tests/stores/exam.test.js
import { describe, it, expect, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useExamStore } from '@/stores/exam'

describe('Exam Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('initializes with empty state', () => {
    const store = useExamStore()
    expect(store.exams).toEqual([])
    expect(store.currentExam).toBeNull()
    expect(store.loading).toBe(false)
  })

  it('sets loading state correctly', () => {
    const store = useExamStore()
    store.setLoading(true)
    expect(store.loading).toBe(true)
  })

  it('adds exam to list', () => {
    const store = useExamStore()
    const exam = { id: 1, title: 'Test Exam' }
    
    store.addExam(exam)
    
    expect(store.exams).toHaveLength(1)
    expect(store.exams[0]).toEqual(exam)
  })
})
```

## 🔗 集成测试

### 后端集成测试

#### 1. API集成测试
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ExamIntegrationTest {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private ExamRepository examRepository;
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }
    
    @Test
    @DisplayName("完整的考试创建流程")
    void createExamFlow() {
        // 1. 创建考试
        Exam exam = new Exam();
        exam.setTitle("集成测试考试");
        exam.setDuration(90);
        
        ResponseEntity<Exam> createResponse = restTemplate.postForEntity(
            "/api/exams", exam, Exam.class);
        
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody().getId()).isNotNull();
        
        Long examId = createResponse.getBody().getId();
        
        // 2. 获取创建的考试
        ResponseEntity<Exam> getResponse = restTemplate.getForEntity(
            "/api/exams/" + examId, Exam.class);
        
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getTitle()).isEqualTo("集成测试考试");
        
        // 3. 验证数据库中的数据
        Optional<Exam> savedExam = examRepository.findById(examId);
        assertThat(savedExam).isPresent();
        assertThat(savedExam.get().getTitle()).isEqualTo("集成测试考试");
    }
}
```

#### 2. 数据库集成测试
```java
@SpringBootTest
@Transactional
class DatabaseIntegrationTest {
    
    @Autowired
    private ExamService examService;
    
    @Autowired
    private QuestionService questionService;
    
    @Test
    @DisplayName("考试和题目关联测试")
    void examQuestionAssociation() {
        // 创建考试
        Exam exam = new Exam();
        exam.setTitle("关联测试考试");
        Exam savedExam = examService.createExam(exam);
        
        // 创建题目
        Question question1 = new Question();
        question1.setContent("题目1");
        question1.setExam(savedExam);
        
        Question question2 = new Question();
        question2.setContent("题目2");
        question2.setExam(savedExam);
        
        questionService.createQuestion(question1);
        questionService.createQuestion(question2);
        
        // 验证关联
        Exam examWithQuestions = examService.getExamWithQuestions(savedExam.getId());
        assertThat(examWithQuestions.getQuestions()).hasSize(2);
    }
}
```

### 前端集成测试

#### 1. 组件集成测试
```javascript
// tests/integration/ExamManagement.test.js
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia } from 'pinia'
import ExamManagement from '@/views/ExamManagement.vue'

describe('ExamManagement Integration', () => {
  let wrapper
  let router
  let pinia

  beforeEach(async () => {
    router = createRouter({
      history: createWebHistory(),
      routes: [
        { path: '/exams', component: ExamManagement }
      ]
    })
    
    pinia = createPinia()
    
    wrapper = mount(ExamManagement, {
      global: {
        plugins: [router, pinia]
      }
    })
    
    await router.push('/exams')
    await router.isReady()
  })

  it('loads and displays exam list', async () => {
    // 模拟API响应
    const mockExams = [
      { id: 1, title: '考试1', status: 'active' },
      { id: 2, title: '考试2', status: 'inactive' }
    ]
    
    // 等待组件加载完成
    await wrapper.vm.$nextTick()
    
    expect(wrapper.find('.exam-list').exists()).toBe(true)
  })
})
```

## 🌐 端到端测试

### Cypress E2E测试

#### 1. 用户登录流程
```javascript
// cypress/e2e/auth.cy.js
describe('用户认证流程', () => {
  beforeEach(() => {
    cy.visit('/login')
  })

  it('管理员登录成功', () => {
    cy.get('[data-cy=username]').type('admin')
    cy.get('[data-cy=password]').type('admin123')
    cy.get('[data-cy=login-btn]').click()
    
    cy.url().should('include', '/dashboard')
    cy.get('[data-cy=welcome-message]').should('contain', '欢迎，管理员')
  })

  it('登录失败显示错误信息', () => {
    cy.get('[data-cy=username]').type('invalid')
    cy.get('[data-cy=password]').type('invalid')
    cy.get('[data-cy=login-btn]').click()
    
    cy.get('[data-cy=error-message]').should('be.visible')
    cy.get('[data-cy=error-message]').should('contain', '用户名或密码错误')
  })
})
```

#### 2. 考试管理流程
```javascript
// cypress/e2e/exam-management.cy.js
describe('考试管理', () => {
  beforeEach(() => {
    cy.login('admin', 'admin123') // 自定义命令
    cy.visit('/exams')
  })

  it('创建新考试', () => {
    cy.get('[data-cy=create-exam-btn]').click()
    
    cy.get('[data-cy=exam-title]').type('Cypress测试考试')
    cy.get('[data-cy=exam-duration]').type('120')
    cy.get('[data-cy=exam-description]').type('这是一个测试考试')
    
    cy.get('[data-cy=save-exam-btn]').click()
    
    cy.get('[data-cy=success-message]').should('be.visible')
    cy.get('[data-cy=exam-list]').should('contain', 'Cypress测试考试')
  })

  it('编辑考试信息', () => {
    cy.get('[data-cy=exam-item]').first().find('[data-cy=edit-btn]').click()
    
    cy.get('[data-cy=exam-title]').clear().type('修改后的考试标题')
    cy.get('[data-cy=save-exam-btn]').click()
    
    cy.get('[data-cy=success-message]').should('be.visible')
    cy.get('[data-cy=exam-list]').should('contain', '修改后的考试标题')
  })

  it('删除考试', () => {
    cy.get('[data-cy=exam-item]').first().find('[data-cy=delete-btn]').click()
    cy.get('[data-cy=confirm-delete-btn]').click()
    
    cy.get('[data-cy=success-message]').should('be.visible')
  })
})
```

#### 3. 学生考试流程
```javascript
// cypress/e2e/student-exam.cy.js
describe('学生考试流程', () => {
  beforeEach(() => {
    cy.login('student', 'student123')
  })

  it('完整的考试流程', () => {
    // 进入考试页面
    cy.visit('/student/exams')
    cy.get('[data-cy=exam-card]').first().find('[data-cy=start-exam-btn]').click()
    
    // 确认开始考试
    cy.get('[data-cy=confirm-start-btn]').click()
    
    // 答题
    cy.get('[data-cy=question-1]').find('[data-cy=option-a]').click()
    cy.get('[data-cy=question-2]').find('[data-cy=option-b]').click()
    
    // 提交考试
    cy.get('[data-cy=submit-exam-btn]').click()
    cy.get('[data-cy=confirm-submit-btn]').click()
    
    // 验证结果页面
    cy.url().should('include', '/student/results')
    cy.get('[data-cy=exam-score]').should('be.visible')
  })

  it('考试时间限制', () => {
    cy.clock() // 控制时间
    
    cy.visit('/student/exams')
    cy.get('[data-cy=exam-card]').first().find('[data-cy=start-exam-btn]').click()
    cy.get('[data-cy=confirm-start-btn]').click()
    
    // 快进到考试结束时间
    cy.tick(120 * 60 * 1000) // 120分钟
    
    // 验证自动提交
    cy.get('[data-cy=time-up-message]').should('be.visible')
    cy.url().should('include', '/student/results')
  })
})
```

## ⚡ 性能测试

### JMeter性能测试

#### 1. 测试计划配置
```xml
<!-- exam-performance-test.jmx -->
<?xml version="1.0" encoding="UTF-8"?>
<jmeterTestPlan version="1.2">
  <hashTree>
    <TestPlan testname="考试系统性能测试">
      <elementProp name="TestPlan.arguments" elementType="Arguments" guiclass="ArgumentsPanel">
        <collectionProp name="Arguments.arguments">
          <elementProp name="host" elementType="Argument">
            <stringProp name="Argument.name">host</stringProp>
            <stringProp name="Argument.value">localhost</stringProp>
          </elementProp>
          <elementProp name="port" elementType="Argument">
            <stringProp name="Argument.name">port</stringProp>
            <stringProp name="Argument.value">8080</stringProp>
          </elementProp>
        </collectionProp>
      </elementProp>
    </TestPlan>
  </hashTree>
</jmeterTestPlan>
```

#### 2. 负载测试场景
```javascript
// k6-load-test.js
import http from 'k6/http'
import { check, sleep } from 'k6'

export let options = {
  stages: [
    { duration: '2m', target: 100 }, // 2分钟内增加到100用户
    { duration: '5m', target: 100 }, // 保持100用户5分钟
    { duration: '2m', target: 200 }, // 2分钟内增加到200用户
    { duration: '5m', target: 200 }, // 保持200用户5分钟
    { duration: '2m', target: 0 },   // 2分钟内减少到0用户
  ],
  thresholds: {
    http_req_duration: ['p(99)<1500'], // 99%的请求在1.5秒内完成
    http_req_failed: ['rate<0.1'],     // 错误率小于10%
  },
}

export default function () {
  // 登录
  let loginResponse = http.post('http://localhost:8080/api/auth/login', {
    username: 'testuser',
    password: 'testpass'
  })
  
  check(loginResponse, {
    'login successful': (r) => r.status === 200,
  })
  
  let token = loginResponse.json('token')
  
  // 获取考试列表
  let examResponse = http.get('http://localhost:8080/api/exams', {
    headers: { Authorization: `Bearer ${token}` }
  })
  
  check(examResponse, {
    'exam list loaded': (r) => r.status === 200,
    'response time < 500ms': (r) => r.timings.duration < 500,
  })
  
  sleep(1)
}
```

### 前端性能测试

#### 1. Lighthouse CI配置
```json
// lighthouserc.json
{
  "ci": {
    "collect": {
      "url": ["http://localhost:3000"],
      "numberOfRuns": 3
    },
    "assert": {
      "assertions": {
        "categories:performance": ["error", {"minScore": 0.8}],
        "categories:accessibility": ["error", {"minScore": 0.9}],
        "categories:best-practices": ["error", {"minScore": 0.9}],
        "categories:seo": ["error", {"minScore": 0.8}]
      }
    },
    "upload": {
      "target": "temporary-public-storage"
    }
  }
}
```

## 📊 测试报告

### 测试覆盖率

#### 后端覆盖率配置
```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.7</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

#### 前端覆盖率配置
```javascript
// vitest.config.js
export default defineConfig({
  test: {
    coverage: {
      provider: 'c8',
      reporter: ['text', 'json', 'html'],
      exclude: [
        'node_modules/',
        'src/test/',
        '**/*.d.ts',
        '**/*.config.js'
      ],
      thresholds: {
        global: {
          branches: 80,
          functions: 80,
          lines: 80,
          statements: 80
        }
      }
    }
  }
})
```

### 测试执行命令

```bash
# 后端测试
mvn test                    # 运行所有测试
mvn test -Dtest=ExamServiceTest  # 运行特定测试类
mvn jacoco:report          # 生成覆盖率报告

# 前端测试
npm run test               # 运行单元测试
npm run test:coverage      # 运行测试并生成覆盖率
npm run test:e2e          # 运行E2E测试
npm run test:performance   # 运行性能测试

# 集成测试
npm run test:integration   # 运行集成测试
```

## 🔍 测试最佳实践

### 1. 测试命名规范
```java
// 好的测试命名
@Test
@DisplayName("当用户名为空时，登录应该失败并返回400错误")
void login_WhenUsernameIsEmpty_ShouldFailWithBadRequest() {
    // 测试实现
}

// 避免的命名
@Test
void testLogin() {
    // 测试实现
}
```

### 2. 测试数据管理
```java
// 使用Builder模式创建测试数据
public class ExamTestDataBuilder {
    private String title = "默认考试";
    private Integer duration = 120;
    private ExamStatus status = ExamStatus.ACTIVE;
    
    public ExamTestDataBuilder withTitle(String title) {
        this.title = title;
        return this;
    }
    
    public ExamTestDataBuilder withDuration(Integer duration) {
        this.duration = duration;
        return this;
    }
    
    public Exam build() {
        Exam exam = new Exam();
        exam.setTitle(title);
        exam.setDuration(duration);
        exam.setStatus(status);
        return exam;
    }
}

// 使用示例
@Test
void testCreateExam() {
    Exam exam = new ExamTestDataBuilder()
        .withTitle("Java基础考试")
        .withDuration(90)
        .build();
    
    // 测试逻辑
}
```

### 3. Mock使用原则
```java
// 好的Mock使用
@Test
void getUserExams_ShouldReturnUserSpecificExams() {
    // Given
    Long userId = 1L;
    List<Exam> userExams = Arrays.asList(
        createExam("用户考试1"),
        createExam("用户考试2")
    );
    
    when(examRepository.findByUserId(userId)).thenReturn(userExams);
    
    // When
    List<Exam> result = examService.getUserExams(userId);
    
    // Then
    assertThat(result).hasSize(2);
    verify(examRepository).findByUserId(userId);
}
```

### 4. 测试隔离
```java
@TestMethodOrder(OrderAnnotation.class)
class ExamIntegrationTest {
    
    @BeforeEach
    void setUp() {
        // 每个测试前清理数据
        examRepository.deleteAll();
    }
    
    @Test
    @Order(1)
    void createExam() {
        // 测试1
    }
    
    @Test
    @Order(2)
    void updateExam() {
        // 测试2，不依赖测试1的数据
    }
}
```

## 🚀 持续集成中的测试

### GitHub Actions配置
```yaml
# .github/workflows/test.yml
name: Tests

on: [push, pull_request]

jobs:
  backend-tests:
    runs-on: ubuntu-latest
    
    services:
      mysql:
        image: mysql:8.0
        env:
          MYSQL_ROOT_PASSWORD: root
          MYSQL_DATABASE: testdb
        options: >-
          --health-cmd="mysqladmin ping"
          --health-interval=10s
          --health-timeout=5s
          --health-retries=3
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Run tests
      run: mvn test
    
    - name: Generate test report
      run: mvn jacoco:report
    
    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v3

  frontend-tests:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Setup Node.js
      uses: actions/setup-node@v3
      with:
        node-version: '18'
        cache: 'npm'
        cache-dependency-path: vue/package-lock.json
    
    - name: Install dependencies
      run: |
        cd vue
        npm ci
    
    - name: Run unit tests
      run: |
        cd vue
        npm run test:coverage
    
    - name: Run E2E tests
      run: |
        cd vue
        npm run test:e2e:headless
```

---

*完整的测试策略确保系统的稳定性和可靠性。如有测试相关问题，欢迎在GitHub Issues中讨论。*