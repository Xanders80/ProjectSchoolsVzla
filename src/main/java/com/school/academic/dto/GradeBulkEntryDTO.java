package com.school.academic.dto;

import java.util.List;

import com.school.academic.enums.EvaluationType;

public class GradeBulkEntryDTO {
    private Long courseId;
    private Long sectionId;
    private Long periodId;
    private EvaluationType evaluationType;
    private List<StudentGradeDTO> studentGrades;

    public GradeBulkEntryDTO() {
    }

    // Getters and Setters
    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Long periodId) {
        this.periodId = periodId;
    }

    public EvaluationType getEvaluationType() {
        return evaluationType;
    }

    public void setEvaluationType(EvaluationType evaluationType) {
        this.evaluationType = evaluationType;
    }

    public List<StudentGradeDTO> getStudentGrades() {
        return studentGrades;
    }

    public void setStudentGrades(List<StudentGradeDTO> studentGrades) {
        this.studentGrades = studentGrades;
    }

    public static class StudentGradeDTO {
        private Long studentId;
        private String studentName;
        private Double score;
        private String comments;

        public StudentGradeDTO() {
        }

        // Getters and Setters
        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public Double getScore() {
            return score;
        }

        public void setScore(Double score) {
            this.score = score;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }
    }
}
