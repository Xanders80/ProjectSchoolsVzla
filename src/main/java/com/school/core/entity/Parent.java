package com.school.core.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.school.academic.entity.Student;
import com.school.academic.validation.ValidationGroups;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "parents",
       indexes = {
           @Index(name = "idx_parent_user", columnList = "user_id")
       })
@EntityListeners(com.school.core.listener.AuditEntityListener.class)
public class Parent extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_parent_user"))
    private User user;

    @NotBlank(message = "La relación es obligatoria", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(min = 2, max = 50, message = "La relación debe tener entre 2 y 50 caracteres", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "La relación solo puede contener letras y espacios", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Column(name = "relationship", nullable = false, length = 50)
    private String relationship;

    @Column(name = "previous_relationship", length = 50)
    private String previousRelationship;

    @Column(name = "relationship_changed_at")
    private LocalDateTime relationshipChangedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "parent_student",
        joinColumns = @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_parent_student_parent")),
        inverseJoinColumns = @JoinColumn(name = "student_id", foreignKey = @ForeignKey(name = "fk_parent_student_student")),
        indexes = {
            @Index(name = "idx_parent_student_parent", columnList = "parent_id"),
            @Index(name = "idx_parent_student_student", columnList = "student_id")
        }
    )
    private Set<Student> children = new HashSet<>();

    public Parent() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) {
        String newValue = relationship != null ? relationship.trim() : null;
        if (this.relationship != null && !Objects.equals(this.relationship, newValue)) {
            this.previousRelationship = this.relationship;
            this.relationshipChangedAt = LocalDateTime.now();
        }
        this.relationship = newValue;
    }
    
    public String getPreviousRelationship() { return previousRelationship; }
    public LocalDateTime getRelationshipChangedAt() { return relationshipChangedAt; }
    
    public Set<Student> getChildren() { return children; }
    public void setChildren(Set<Student> children) { this.children = children; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Parent parent)) return false;
        return Objects.equals(id, parent.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Parent{id=" + id + ", relationship='" + relationship + "'}";
    }
}
