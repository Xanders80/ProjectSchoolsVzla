// Component Scaffolder - Frontend Tool for SMS
// Scaffolds Thymeleaf templates for CRUD operations

export interface ScaffoldConfig {
  entity: string;
  module: string;
  fields: Field[];
  operations: string[];
}

export interface Field {
  name: string;
  type: string;
  label: string;
  required: boolean;
}

export class ComponentScaffolder {
  scaffold(config: ScaffoldConfig): string[] {
    const files: string[] = [];

    if (config.operations.includes('list')) {
      files.push(this.generateListTemplate(config));
    }
    if (config.operations.includes('form')) {
      files.push(this.generateFormTemplate(config));
    }
    if (config.operations.includes('view')) {
      files.push(this.generateViewTemplate(config));
    }

    return files;
  }

  private generateListTemplate(config: ScaffoldConfig): string {
    return `<!-- ${config.module}/${config.entity}/list.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" th:replace="~{fragments/sb-admin-layout :: layout(~{::title}, ~{::main})}">
<head><title th:text="#{${config.entity}.list.title}">List</title></head>
<body>
<main>
  <div class="card shadow mb-4">
    <div class="card-header py-3 d-flex justify-content-between align-items-center">
      <h6 class="m-0 font-weight-bold text-primary" th:text="#{${config.entity}.list.title}">List</h6>
      <a th:href="@{/${config.entity}/new}" class="btn btn-primary btn-sm">
        <i class="fas fa-plus"></i> <span th:text="#{button.new}">New</span>
      </a>
    </div>
    <div class="card-body">
      <table class="table table-bordered" id="dataTable">
        <thead><tr>
          <th th:each="f : ${config.fields.map(f => f.name)}" th:text="#{${config.entity}.${f}}">${config.fields.map(f => f.label).join('</th><th>')}</th>
          <th th:text="#{common.actions}">Actions</th>
        </tr></thead>
        <tbody>
          <tr th:each="item : ${${config.entity}s}">
            <td th:each="f : ${config.fields.map(f => f.name)}" th:text="\${item[f]}"></td>
            <td>
              <a th:href="@{/${config.entity}/{id}(id=\${item.id})}" class="btn btn-info btn-sm"><i class="fas fa-eye"></i></a>
              <a th:href="@{/${config.entity}/{id}/edit(id=\${item.id})}" class="btn btn-warning btn-sm"><i class="fas fa-edit"></i></a>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</main>
</body>
</html>`;
  }

  private generateFormTemplate(config: ScaffoldConfig): string {
    return `<!-- ${config.module}/${config.entity}/form.html -->
<form th:action="@{/${config.entity}}" th:object="${config.entity}" method="post">
  <div th:each="f : ${config.fields.map(f => f.name)}" class="form-group">
    <label th:for="${config.fields.map(f => f.name).join('" th:text="#{entity."}')}"></label>
    <input type="text" th:field="*{${config.fields.map(f => f.name).join('}" class="form-control"><input type="text" th:field="*{')}}" class="form-control">
  </div>
  <button type="submit" class="btn btn-primary" th:text="#{button.save}">Save</button>
</form>`;
  }

  private generateViewTemplate(config: ScaffoldConfig): string {
    return `<!-- ${config.module}/${config.entity}/view.html -->
<div th:each="f : ${config.fields.map(f => f.name)}" class="form-group">
  <label th:text="#{${config.entity}.${config.fields.map(f => f.name).join('}"></label><span th:text="${config.entity + "." + config.fields.map(f => f.name).join('}"></label><span th:text="${config.entity + "."}}}')}"></span>
</div>`;
  }
}
