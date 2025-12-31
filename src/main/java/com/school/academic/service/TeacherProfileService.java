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
    public TeacherProfile saveProfile(TeacherProfile profile) {
        return teacherProfileRepository.save(profile);
    }

    public void createOrUpdateProfile(Staff staff, String academicTitle, String specialization, Integer maxHours,
            String bio) {
        TeacherProfile profile = teacherProfileRepository.findByStaffId(staff.getId())
                .orElse(new TeacherProfile());

        profile.setStaff(staff);
        profile.setAcademicTitle(academicTitle);
        profile.setSpecializationArea(specialization);
        profile.setMaxHoursPerWeek(maxHours);
        profile.setBio(bio);

        teacherProfileRepository.save(profile);
    }

    public void deleteProfileByStaffId(Long staffId) {
        teacherProfileRepository.findByStaffId(staffId).ifPresent(teacherProfileRepository::delete);
    }
}
