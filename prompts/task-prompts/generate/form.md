# Form Generation Prompt

Inputs:
- entity
- module
- fields: [{name, type, required}]

Outputs:
- Thymeleaf form template
- Validation notes

Guidance:
- Use Bootstrap form classes
- Bind fields with th:field
- Include error messages using th:errors
