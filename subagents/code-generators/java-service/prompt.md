# Java Service Generator

Genera servicios para el School Management System con el patrón Interface + Implementation.

## Template Interface
```java
package com.school.${module}.service;

import com.school.${module}.entity.${Entity};
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ${Entity}Service {
    Page<${Entity}> findAll(Pageable pageable);
    ${Entity} findById(Long id);
    ${Entity} create(${Entity} entity);
    ${Entity} update(Long id, ${Entity} entity);
    void softDelete(Long id);
}
```

## Template Implementation
```java
package com.school.${module}.service.impl;

import com.school.${module}.entity.${Entity};
import com.school.${module}.repository.${Entity}Repository;
import com.school.${module}.service.${Entity}Service;
import com.school.core.exception.BusinessValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ${Entity}ServiceImpl implements ${Entity}Service {

    private final ${Entity}Repository repository;

    @Override
    @Transactional(readOnly = true)
    public Page<${Entity}> findAll(Pageable pageable) {
        return repository.findByDeletedFalse(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ${Entity} findById(Long id) {
        return repository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new BusinessValidationException("${entity}.not.found"));
    }

    @Override
    @Transactional
    public ${Entity} create(${Entity} entity) {
        log.info("Creating new ${entity}");
        return repository.save(entity);
    }

    @Override
    @Transactional
    public ${Entity} update(Long id, ${Entity} entity) {
        ${Entity} existing = findById(id);
        // Update fields
        return repository.save(existing);
    }

    @Override
    @Transactional
    public void softDelete(Long id) {
        ${Entity} entity = findById(id);
        entity.setDeleted(true);
        entity.setDeletedAt(LocalDateTime.now());
        repository.save(entity);
        log.info("Soft deleted ${entity} with id: {}", id);
    }
}
```
