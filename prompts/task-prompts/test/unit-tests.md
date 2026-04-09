# Test Generation Prompt

Genera tests para el SMS.

## Tipos de Tests
1. Unit Tests: @ExtendWith(MockitoExtension.class)
2. Repository Tests: @DataJpaTest
3. Controller Tests: @WebMvcTest
4. Integration Tests: @SpringBootTest
5. E2E Tests: Selenium

## Naming Convention
`should[ExpectedBehavior]When[Condition]`

## Coverage Targets
- Unit: > 80%
- Integration: > 70%
- Overall: > 75%
