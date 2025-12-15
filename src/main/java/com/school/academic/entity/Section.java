package com.school.academic.entity;

import com.school.admin.entity.Staff;
import com.school.infra.entity.Room;
import com.school.academic.validation.ValidationGroups;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "sections",
       indexes = {
           @Index(name = "idx_section_term", columnList = "term"),
           @Index(name = "idx_section_course", columnList = "course_id")
       })
@EntityListeners(com.school.core.listener.AuditEntityListener.class)
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la sección es obligatorio", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @NotBlank(message = "El período es obligatorio", groups = ValidationGroups.Create.class)
    @Pattern(regexp = "^\\d{4}-[12]$", message = "El período debe tener formato YYYY-1 o YYYY-2", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Column(name = "term", nullable = false, length = 10)
    private String term;

    @Column(name = "previous_term", length = 10)
    private String previousTerm;

    @Column(name = "term_changed_at")
    private LocalDateTime termChangedAt;

    @NotNull(message = "El curso es obligatorio", groups = ValidationGroups.Create.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_course"))
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", foreignKey = @ForeignKey(name = "fk_section_teacher"))
    private Staff teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", foreignKey = @ForeignKey(name = "fk_section_room"))
    private Room room;

    public Section() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTerm() { return term; }
    public void setTerm(String term) {
        String newValue = term != null ? term.trim() : null;
        if (this.term != null && !Objects.equals(this.term, newValue)) {
            this.previousTerm = this.term;
            this.termChangedAt = LocalDateTime.now();
        }
        this.term = newValue;
    }

    public String getPreviousTerm() {
        return previousTerm;
    }

    public LocalDateTime getTermChangedAt() {
        return termChangedAt;
    }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public Staff getTeacher() { return teacher; }
    public void setTeacher(Staff teacher) { this.teacher = teacher; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section section)) return false;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Section{id=" + id + ", name='" + name + "', term='" + term + "'}";
    }
}
