package com.school.academic.dto;

import java.util.List;

import com.school.academic.entity.CurriculumGrid;
import com.school.academic.entity.StudyPlan;

public class StudyPlanSummary {
    
    private StudyPlan studyPlan;
    private int totalGrades;
    private int totalCourses;
    private int totalHours;
    private List<CurriculumGrid> curriculumGrids;

    public StudyPlanSummary() {}

    public StudyPlanSummary(StudyPlan studyPlan, int totalGrades, int totalCourses, int totalHours) {
        this.studyPlan = studyPlan;
        this.totalGrades = totalGrades;
        this.totalCourses = totalCourses;
        this.totalHours = totalHours;
    }

    public StudyPlan getStudyPlan() { return studyPlan; }
    public void setStudyPlan(StudyPlan studyPlan) { this.studyPlan = studyPlan; }

    public int getTotalGrades() { return totalGrades; }
    public void setTotalGrades(int totalGrades) { this.totalGrades = totalGrades; }

    public int getTotalCourses() { return totalCourses; }
    public void setTotalCourses(int totalCourses) { this.totalCourses = totalCourses; }

    public int getTotalHours() { return totalHours; }
    public void setTotalHours(int totalHours) { this.totalHours = totalHours; }

    public List<CurriculumGrid> getCurriculumGrids() { return curriculumGrids; }
    public void setCurriculumGrids(List<CurriculumGrid> curriculumGrids) { this.curriculumGrids = curriculumGrids; }
}