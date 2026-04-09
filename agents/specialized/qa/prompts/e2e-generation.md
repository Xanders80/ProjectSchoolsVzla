# QA Specialist - E2E Testing Prompt

## Testing E2E para SMS con Selenium

### Setup
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentE2ETest {
    @LocalServerPort private int port;
    private WebDriver driver;
    
    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }
    
    @AfterEach
    void tearDown() {
        driver.quit();
    }
    
    @Test
    void shouldLoginAndNavigateToStudents() {
        driver.get("http://localhost:" + port + "/login");
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("admin123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        
        driver.get("http://localhost:" + port + "/students");
        assertThat(driver.getTitle()).contains("Students");
    }
}
```

### Flujos Críticos a Testear
1. **Login/Logout:** Autenticación y cierre de sesión
2. **CRUD Estudiantes:** Crear, ver, editar, eliminar (soft delete)
3. **CRUD Cursos:** Gestión completa de cursos
4. **Registro de Notas:** Bulk grade entry
5. **Asistencia:** Registro diario de asistencia
6. **Inscripciones:** Enrollment de estudiantes
7. **Reportes:** Generación de certificados y actas
8. **Portal Padres:** Acceso de padres a información

### Page Object Pattern
```java
public class LoginPage {
    private WebDriver driver;
    
    @FindBy(id = "username") private WebElement usernameField;
    @FindBy(id = "password") private WebElement passwordField;
    @FindBy(css = "button[type='submit']") private WebElement loginButton;
    
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    
    public HomePage login(String username, String password) {
        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        loginButton.click();
        return new HomePage(driver);
    }
}
```
