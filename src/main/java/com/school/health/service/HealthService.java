package com.school.health.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.Student;
import com.school.academic.repository.StudentRepository;
import com.school.health.entity.MedicalRecord;
import com.school.health.entity.Vaccine;
import com.school.health.repository.MedicalRecordRepository;
import com.school.health.repository.VaccineRepository;

@Service
@Transactional
public class HealthService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final VaccineRepository vaccineRepository;
    private final StudentRepository studentRepository;

    public HealthService(MedicalRecordRepository medicalRecordRepository,
            VaccineRepository vaccineRepository,
            StudentRepository studentRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.vaccineRepository = vaccineRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public MedicalRecord getMedicalRecordByStudentId(@NonNull Long studentId) {
        return medicalRecordRepository.findByStudentId(studentId)
                .orElseGet(() -> {
                    MedicalRecord newRecord = new MedicalRecord();
                    // Don't set student yet or save, just return empty template
                    // Front-end will submit studentId
                    return newRecord;
                });
    }

    public MedicalRecord getOrCreateMedicalRecord(@NonNull Long studentId) {
        return medicalRecordRepository.findByStudentId(studentId)
                .orElseGet(() -> {
                    Student student = studentRepository.findById(studentId)
                            .orElseThrow(() -> new IllegalArgumentException("No se encontro registro de estudiante"));
                    MedicalRecord record = new MedicalRecord();
                    record.setStudent(student);
                    return medicalRecordRepository.save(record);
                });
    }

    public MedicalRecord saveMedicalRecord(@NonNull Long studentId, MedicalRecord recordData) {
        MedicalRecord existing = getOrCreateMedicalRecord(studentId);

        existing.setBloodType(recordData.getBloodType());
        existing.setAllergies(recordData.getAllergies());
        existing.setConditions(recordData.getConditions());
        existing.setMedications(recordData.getMedications());
        existing.setEmergencyContactName(recordData.getEmergencyContactName());
        existing.setEmergencyContactPhone(recordData.getEmergencyContactPhone());
        existing.setLastUpdated(LocalDateTime.now());

        return medicalRecordRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public List<Vaccine> getVaccinesByStudentId(@NonNull Long studentId) {
        return vaccineRepository.findByStudentIdAndDeletedFalseOrderByAdministrationDateDesc(studentId);
    }

    public Vaccine addVaccine(@NonNull Long studentId, Vaccine vaccine) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro registro de estudiante"));
        vaccine.setStudent(student);
        return vaccineRepository.save(vaccine);
    }

    public void deleteVaccine(@NonNull Long vaccineId) {
        Vaccine vaccine = vaccineRepository.findById(vaccineId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro registro de vacuna"));
        vaccine.setDeleted(true);
        vaccine.setDeletedAt(LocalDateTime.now());
        vaccine.setDeletedBy(getCurrentUser());
        vaccineRepository.save(vaccine);
    }

    @NonNull
    private String getCurrentUser() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (auth != null) {
            String name = auth.getName();
            if (name != null) {
                return name;
            }
        }
        return "system";
    }

    public void deleteStudentHealthData(@NonNull Long studentId) {
        medicalRecordRepository.deleteByStudentId(studentId);
        vaccineRepository.deleteByStudentId(studentId);
    }
}
