# Test Suite Generator

Genera tests unitarios e integration tests para el School Management System.

## Template Unit Test
```java
@ExtendWith(MockitoExtension.class)
class ${Entity}ServiceTest {
    @Mock private ${Entity}Repository repository;
    @InjectMocks private ${Entity}ServiceImpl service;
    
    @Test
    void shouldCreate${Entity}() {
        ${Entity} entity = ${Entity}.builder().build();
        when(repository.save(any())).thenReturn(entity);
        
        ${Entity} result = service.create(entity);
        assertThat(result).isEqualTo(entity);
        verify(repository).save(entity);
    }
}
```

## Template Integration Test
```java
@DataJpaTest
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:testdb"})
class ${Entity}RepositoryTest {
    @Autowired private ${Entity}Repository repository;
    
    @Test
    void shouldFindActiveEntities() {
        ${Entity} entity = ${Entity}.builder().build();
        repository.save(entity);
        
        List<${Entity}> result = repository.findByDeletedFalse();
        assertThat(result).hasSize(1);
    }
}
```
