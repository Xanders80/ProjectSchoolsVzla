package com.school.academic.dto;

public class AdmissionResult {
    
    private Long examId;
    private String applicantName;
    private String status;
    private Double score;
    private String recommendation;
    private String comments;
    private boolean approved;

    public AdmissionResult() {}

    public AdmissionResult(Long examId, String applicantName, String status, Double score, boolean approved) {
        this.examId = examId;
        this.applicantName = applicantName;
        this.status = status;
        this.score = score;
        this.approved = approved;
    }

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
}