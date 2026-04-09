# QA Specialist - Test Strategy Prompt

## Estrategia de Pruebas para SMS

### Pirámide de Testing
```
        /E2E\          ← Pocos tests (flujos críticos)
       /Integration\   ← Tests moderados (servicios, repos)
      /Unit Testing\   ← Muchos tests (lógica de negocio)
```

### Cobertura Objetivo
- **Unit Tests:** > 80% de cobertura
- **Integration Tests:** Endpoints críticos cubiertos
- **E2E Tests:** Flujos principales (login, CRUD estudiantes, notas)

### Tipos de Tests por Capa

#### Repository Tests (`@DataJpaTest`)
```java
@DataJpaTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb"
})
class StudentRepositoryTest {
    @Autowired private StudentRepository repository;
    
    @Test
    void shouldFindActiveStudents() {
        Student student = new Student();
        student.setFirstName("Test");
        repository.save(student);
        
        List<Student> result = repository.findByDeletedFalse();
        assertThat(result).hasSize(1);
    }
}
```

#### Service Tests (`@ExtendWith(MockitoExtension.class)`)
```java
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    @Mock private StudentRepository repository;
    @InjectMocks private StudentServiceImpl service;
    
    @Test
    void shouldCreateStudent() {
        Student student = new Student();
        when(repository.save(any())).thenReturn(student);
        
        Student result = service.create(student);
        assertThat(result).isEqualTo(student);
        verify(repository).save(student);
    }
}
```

#### Controller Tests (`@WebMvcTest`)
```java
@WebMvcTest(StudentController.class)
class StudentControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private StudentService service;
    
    @Test
    void shouldReturnStudentList() throws Exception {
        when(service.findAll(any(Pageable.class))).thenReturn(Page.empty());
        
        mockMvc.perform(get("/students"))
            .andExpect(status().isOk())
            .andExpect(view().name("academic/students/list"));
    }
}
```

### Matriz de Testing por Módulo
| Módulo | Unit | Integration | E2E | Priority |
|--------|------|-------------|-----|----------|
| academic | 80% | 70% | 50% | Critical |
| admin | 80% | 70% | 40% | High |
| finance | 85% | 75% | 50% | Critical |
| health | 80% | 70% | 30% | High |
| hr | 75% | 65% | 30% | Medium |
| library | 70% | 60% | 30% | Medium |
| infra | 70% | 60% | 20% | Medium |
| communication | 75% | 65% | 30% | Medium |
