package com.school.academic.entity;

import com.school.admin.entity.Staff;
import com.school.infra.entity.Room;
import jakarta.persistence.*;

@Entity
@Table(name = "sections")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g. "Group A"
    private String term; // e.g. "2025-1"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Staff teacher; // Must be role TEACHER

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room; // Primary room

    public Section() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public Staff getTeacher() { return teacher; }
    public void setTeacher(Staff teacher) { this.teacher = teacher; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
}
