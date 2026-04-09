# Entity Generation Prompt

Inputs:
- module
- entity_name
- fields: [{name, type, required}]
- relations: [{type, target}]

Outputs:
- Java entity class with JPA annotations
- Repository interface for the entity
- Optional enum if needed

Notes:
- Include soft delete fields (deleted, deletedAt)
- Include audit fields (createdAt, updatedAt, createdBy, updatedBy)
- Use Lombok annotations to reduce boilerplate
