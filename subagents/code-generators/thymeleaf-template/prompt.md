# Thymeleaf Template Generator

Genera templates Thymeleaf para el School Management System usando SB Admin 2.

## Template List
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" th:replace="~{fragments/sb-admin-layout :: layout(~{::title}, ~{::main})}">
<head>
    <title th:text="#{${module}.${resource}.list.title}">List</title>
</head>
<body>
<main>
    <div class="container-fluid">
        <h1 class="h3 mb-4" th:text="#{${module}.${resource}.list.title}">List</h1>

        <div th:if="${success}" class="alert alert-success alert-dismissible fade show" role="alert">
            <span th:text="#{${success}}"></span>
            <button type="button" class="close" data-dismiss="alert">&times;</button>
        </div>

        <div class="card shadow mb-4">
            <div class="card-header py-3 d-flex justify-content-between align-items-center">
                <h6 class="m-0 font-weight-bold text-primary" th:text="#{${module}.${resource}.list.title}">List</h6>
                <a th:href="@{/${resource}/new}" class="btn btn-primary btn-sm">
                    <i class="fas fa-plus"></i> <span th:text="#{button.new}">New</span>
                </a>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
                        <thead>
                            <tr>
                                <th th:each="field : ${fields}" th:text="#{${entity}.${field}}">Field</th>
                                <th th:text="#{common.actions}">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr th:each="item : ${${resource}}">
                                <td th:each="field : ${fields}" th:text="${item[field]}"></td>
                                <td>
                                    <a th:href="@{/${resource}/{id}(id=${item.id})}" class="btn btn-info btn-sm"><i class="fas fa-eye"></i></a>
                                    <a th:href="@{/${resource}/{id}/edit(id=${item.id})}" class="btn btn-warning btn-sm"><i class="fas fa-edit"></i></a>
                                    <button type="button" class="btn btn-danger btn-sm" th:attr="data-id=${item.id}" onclick="confirmDelete(this)"><i class="fas fa-trash"></i></button>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</main>
</body>
</html>
```

## Reglas
- Usar fragments del layout SB Admin 2
- Todos los textos con `#{message.key}` para i18n
- CSRF automático con `th:action`
- DataTables para tablas
- Delete modal para confirmación
- Responsive con Bootstrap 4
