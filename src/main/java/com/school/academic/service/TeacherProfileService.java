package com.school.academic.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.TeacherProfile;
import com.school.academic.repository.TeacherProfileRepository;
import com.school.admin.entity.Staff;

@Service
@Transactional
public class TeacherProfileService {

    private final TeacherProfileRepository teacherProfileRepository;

    public TeacherProfileService(TeacherProfileRepository teacherProfileRepository) {
        this.teacherProfileRepository = teacherProfileRepository;
    }

    public Optional<TeacherProfile> getProfileByStaffId(Long staffId) {
        return teacherProfileRepository.findByStaffId(staffId);
    }

    @SuppressWarnings("null")
    public Optional<TeacherProfile> getProfileById(Long id) {
        return teacherProfileRepository.findById(id);
    }

    @SuppressWarnings("null")
    public TeacherProfile saveProfile(TeacherProfile profile) {
        return teacherProfileRepository.save(profile);
    }

    public void createOrUpdateProfile(Staff staff, String academicTitle, String specialization, Integer maxHours,
            String bio, Integer yearsExperience, String preferredSubjects, String certifications,
            String escalafonCategory, java.time.LocalDate seniorityDate, Double currentPoints) {
        TeacherProfile profile = teacherProfileRepository.findByStaffId(staff.getId())
                .orElse(new TeacherProfile());

        profile.setStaff(staff);
        profile.setAcademicTitle(academicTitle);
        profile.setSpecializationArea(specialization);
        profile.setMaxHoursPerWeek(maxHours);
        profile.setBio(bio);
        profile.setYearsExperience(yearsExperience);
        profile.setPreferredSubjects(preferredSubjects);
        profile.setCertifications(certifications);
        profile.setEscalafonCategory(escalafonCategory);
        profile.setSeniorityDate(seniorityDate);
        profile.setCurrentPoints(currentPoints != null ? currentPoints : 0.0);

        teacherProfileRepository.save(profile);
    }

    public void deleteProfileByStaffId(Long staffId) {
        teacherProfileRepository.findByStaffId(staffId).ifPresent(teacherProfileRepository::delete);
    }
}
