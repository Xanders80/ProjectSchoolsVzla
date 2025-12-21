/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2025 [Tu Nombre o Empresa]
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.school.academic.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import com.school.academic.validation.ValidationGroups;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "courses",
       indexes = {
           @Index(name = "idx_course_code", columnList = "code", unique = true),
           @Index(name = "idx_course_grade_level", columnList = "gradeLevel")
       })
@EntityListeners(com.school.core.listener.AuditEntityListener.class)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código del curso es obligatorio", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(min = 3, max = 10, message = "El código debe tener entre 3 y 10 caracteres", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Pattern(regexp = "^[A-Z0-9]+$", message = "El código solo puede contener letras mayúsculas y números", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Column(name = "code", unique = true, nullable = false, length = 10)
    private String code;

    @Column(name = "previous_code", length = 10)
    private String previousCode;

    @Column(name = "code_changed_at")
    private LocalDateTime codeChangedAt;

    @NotBlank(message = "El nombre del curso es obligatorio", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Column(name = "description", length = 500)
    private String description;

    @NotNull(message = "Los créditos son obligatorios", groups = ValidationGroups.Create.class)
    @Min(value = 1, message = "Mínimo 1 crédito", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Max(value = 10, message = "Máximo 10 créditos", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Column(name = "credits", nullable = false)
    private Integer credits;

    @NotNull(message = "El grado es obligatorio", groups = ValidationGroups.Create.class)
    @Min(value = 1, message = "Grado mínimo 1", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Max(value = 12, message = "Grado máximo 12", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Column(name = "grade_level", nullable = false)
    private Integer gradeLevel;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by", length = 100)
    private String deletedBy;

    public Course() {
        /*
         * Default constructor required by JPA and other frameworks that create entity
         * instances via reflection; intentionally left empty to avoid side effects.
         */
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        String newValue = code != null ? code.trim().toUpperCase() : null;
        if (this.code != null && !Objects.equals(this.code, newValue)) {
            this.previousCode = this.code;
            this.codeChangedAt = LocalDateTime.now();
        }
        this.code = newValue;
    }

    public String getPreviousCode() {
        return previousCode;
    }

    public LocalDateTime getCodeChangedAt() {
        return codeChangedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public Integer getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(Integer gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    public String getDeletedBy() { return deletedBy; }
    public void setDeletedBy(String deletedBy) { this.deletedBy = deletedBy; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course course)) return false;
        return Objects.equals(id, course.id) && Objects.equals(code, course.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }

    @Override
    public String toString() {
        return "Course{id=" + id + ", code='" + code + "', name='" + name + "'}";
    }
}
