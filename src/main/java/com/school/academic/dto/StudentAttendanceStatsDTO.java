package com.school.academic.dto;

public class StudentAttendanceStatsDTO {
    private String studentName;
    private Long studentId;
    private long presentCount;
    private long lateCount;
    private long absentCount;
    private long excusedCount;
    private double attendancePercentage;

    public StudentAttendanceStatsDTO(String studentName, Long studentId, long presentCount, long lateCount,
            long absentCount, long excusedCount) {
        this.studentName = studentName;
        this.studentId = studentId;
        this.presentCount = presentCount;
        this.lateCount = lateCount;
        this.absentCount = absentCount;
        this.excusedCount = excusedCount;

        long totalSessions = presentCount + lateCount + absentCount + excusedCount;
        if (totalSessions > 0) {
            // Calculating percentage: (Present + Late) / Total * 100
            // Or typically (Present + Late) considered as attended.
            // Absent is 0. Excused might not count against total or count as attended?
            // Let's assume Present + Late + Excused = Attended for now?
            // Usually Absent is the only negative.
            // Let's use: (Total - Absent) / Total
            this.attendancePercentage = (double) (totalSessions - absentCount) / totalSessions * 100.0;
        } else {
            this.attendancePercentage = 100.0; // Default full attendance if no sessions
        }
    }

    public String getStudentName() {
        return studentName;
    }

    public Long getStudentId() {
        return studentId;
    }

    public long getPresentCount() {
        return presentCount;
    }

    public long getLateCount() {
        return lateCount;
    }

    public long getAbsentCount() {
        return absentCount;
    }

    public long getExcusedCount() {
        return excusedCount;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }
}
