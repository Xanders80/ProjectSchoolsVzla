# Frontend Specialist - React/Component Patterns Prompt

## Patrones de Componentes Thymeleaf para SMS

### Estructura de Template CRUD Estándar
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" th:replace="~{fragments/sb-admin-layout :: layout(~{::title}, ~{::main})}">
<head>
    <title th:text="#{students.list.title}">Students</title>
</head>
<body>
<main>
    <!-- Breadcrumb -->
    <ol class="breadcrumb">
        <li class="breadcrumb-item"><a th:href="@{/}">Dashboard</a></li>
        <li class="breadcrumb-item active" th:text="#{students.breadcrumb}">Students</li>
    </ol>

    <!-- Alert Messages -->
    <div th:if="${success}" class="alert alert-success alert-dismissible fade show" role="alert">
        <span th:text="#{${success}}"></span>
        <button type="button" class="close" data-dismiss="alert">&times;</button>
    </div>

    <!-- Card -->
    <div class="card shadow mb-4">
        <div class="card-header py-3 d-flex justify-content-between align-items-center">
            <h6 class="m-0 font-weight-bold text-primary" th:text="#{students.list.title}">Students</h6>
            <a th:href="@{/students/new}" class="btn btn-primary btn-sm">
                <i class="fas fa-plus"></i> <span th:text="#{button.new}">New</span>
            </a>
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
                    <thead>
                        <tr>
                            <th th:text="#{student.name}">Name</th>
                            <th th:text="#{student.email}">Email</th>
                            <th th:text="#{common.actions}">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr th:each="student : ${students}">
                            <td th:text="${student.fullName}"></td>
                            <td th:text="${student.email}"></td>
                            <td>
                                <a th:href="@{/students/{id}(id=${student.id})}" class="btn btn-info btn-sm">
                                    <i class="fas fa-eye"></i>
                                </a>
                                <a th:href="@{/students/{id}/edit(id=${student.id})}" class="btn btn-warning btn-sm">
                                    <i class="fas fa-edit"></i>
                                </a>
                                <button type="button" class="btn btn-danger btn-sm" 
                                        th:attr="data-id=${student.id}" 
                                        onclick="confirmDelete(this)">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</main>
</body>
</html>
```

### Patrones de Formularios
```html
<form th:action="@{/students}" th:object="${student}" method="post" th:classappend="${#fields.hasErrors('*')} ? 'was-validated'">
    <div class="form-group">
        <label th:for="firstName" th:text="#{student.firstName}">First Name</label>
        <input type="text" th:field="*{firstName}" class="form-control" 
               th:classappend="${#fields.hasErrors('firstName')} ? 'is-invalid'" required>
        <div class="invalid-feedback" th:if="${#fields.hasErrors('firstName')}" 
             th:errors="*{firstName}"></div>
    </div>
    <button type="submit" class="btn btn-primary" th:text="#{button.save}">Save</button>
</form>
```

### DataTables Configuration
```javascript
$(document).ready(function() {
    $('#dataTable').DataTable({
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.24/i18n/Spanish.json"
        },
        "pageLength": 25,
        "order": [[0, "asc"]]
    });
});
```

### Reglas de Diseño
1. Usar fragments reutilizables del layout SB Admin 2
2. Todos los textos con `#{message.key}` para i18n
3. Formularios con validación client-side y server-side
4. Tablas con DataTables para búsqueda, paginación y ordenamiento
5. Botones de acción consistentes: ver (info), editar (warning), eliminar (danger)
6. Responsive con clases de Bootstrap 4
7. Incluir delete-modal fragment para confirmación de eliminación
