package com.school.academic.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.TeacherDevelopment;
import com.school.academic.entity.TeacherEvaluation;
import com.school.academic.entity.TeacherProfile;
import com.school.academic.repository.AcademicPeriodRepository;
import com.school.academic.repository.TeacherDevelopmentRepository;
import com.school.academic.repository.TeacherEvaluationRepository;
import com.school.academic.repository.TeacherProfileRepository;
import com.school.hr.entity.DisciplinaryRecord;
import com.school.hr.repository.DisciplinaryRecordRepository;
import com.school.communication.service.NotificationService;
import com.school.communication.enums.NotificationType;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeacherManagementService {

    private final TeacherEvaluationRepository evaluationRepository;
    private final TeacherDevelopmentRepository developmentRepository;
    private final DisciplinaryRecordRepository disciplinaryRepository;
    private final TeacherProfileRepository profileRepository;
    private final TeacherProfileService profileService;
    private final AcademicPeriodRepository periodRepository;
	private final NotificationService notificationService;
	private final com.school.core.util.DigitalSignatureService signatureService;

	public TeacherManagementService(TeacherEvaluationRepository evaluationRepository,
			TeacherDevelopmentRepository developmentRepository,
			DisciplinaryRecordRepository disciplinaryRepository,
			TeacherProfileRepository profileRepository,
			TeacherProfileService profileService,
			AcademicPeriodRepository periodRepository,
			NotificationService notificationService,
			com.school.core.util.DigitalSignatureService signatureService) {
		this.evaluationRepository = evaluationRepository;
		this.developmentRepository = developmentRepository;
		this.disciplinaryRepository = disciplinaryRepository;
		this.profileRepository = profileRepository;
		this.profileService = profileService;
		this.periodRepository = periodRepository;
		this.notificationService = notificationService;
		this.signatureService = signatureService;
	}

    // --- Gestión de Evaluación (Junta Calificadora) ---

    @SuppressWarnings("null")
    public TeacherEvaluation evaluateTeacher(Long profileId, Long periodId, Double pedagogy, Double seniority,
            Double formation, String comments) {
        Optional<TeacherEvaluation> existing = evaluationRepository.findByTeacherProfileIdAndAcademicPeriodId(profileId,
                periodId);

        TeacherEvaluation evaluation = existing.orElse(new TeacherEvaluation());

        TeacherProfile profile = profileService.getProfileById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil docente no encontrado"));

        evaluation.setTeacherProfile(profile);

        com.school.academic.entity.AcademicPeriod period = periodRepository.findById(periodId)
                .orElseThrow(() -> new IllegalArgumentException("Periodo académico no encontrado"));
        evaluation.setAcademicPeriod(period);

        evaluation.setPedagogicalEfficiencyScore(pedagogy);
        evaluation.setSeniorityScore(seniority);
        evaluation.setAcademicFormationScore(formation);
        evaluation.setCommitteeComments(comments);
        evaluation.setFinalized(true);
        evaluation.setEvaluationDate(LocalDate.now());

        TeacherEvaluation saved = evaluationRepository.save(evaluation);

        // Update teacher profile with the new total score
        profile.setCurrentPoints(saved.getTotalScore());
        profileRepository.save(profile);

        // Notify teacher
        if (profile.getStaff().getUser() != null) {
            notificationService.createNotification(
                    "Nueva Evaluación Registrada",
                    "Se ha registrado una nueva evaluación para el periodo " + period.getName() + ". Puntaje total: "
                            + saved.getTotalScore(),
                    NotificationType.ACADEMIC,
                    profile.getStaff().getUser());
        }

        return saved;
    }

    @SuppressWarnings("null")
    public void signEvaluation(Long evaluationId, String signerName) {
        TeacherEvaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new IllegalArgumentException("Evaluación no encontrada"));

        evaluation.setSignedBy(signerName);
        evaluation.setSignatureDate(java.time.LocalDateTime.now());

		String rawData = evaluation.getId() + "|" + evaluation.getTotalScore() + "|" + signerName;
		evaluation.setVerificationHash(signatureService.generateVerificationHash(rawData));

        evaluationRepository.save(evaluation);
    }

    public List<TeacherEvaluation> getTeacherEvaluations(Long profileId) {
        return evaluationRepository.findByTeacherProfileId(profileId);
    }

    public List<com.school.academic.entity.AcademicPeriod> getAllAcademicPeriods() {
        return periodRepository.findAll();
    }

    // --- Expediente Digital (Desarrollo Profesional) ---

    @SuppressWarnings("null")
    public TeacherDevelopment addProfessionalLog(TeacherDevelopment log) {
        return developmentRepository.save(log);
    }

    @SuppressWarnings("null")
    public void verifyCertificate(Long logId, String folio) {
        TeacherDevelopment log = developmentRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("Registro de desarrollo no encontrado"));

        log.setVerified(true);
        log.setFolioNumber(folio);
        log.setVerificationDate(LocalDate.now());
        developmentRepository.save(log);
    }

    // --- Régimen Disciplinario ---

    @SuppressWarnings("null")
    public DisciplinaryRecord recordInfraction(DisciplinaryRecord record) {
        record.setStatus("NOTIFICADO");
        return disciplinaryRepository.save(record);
    }

    @SuppressWarnings("null")
    public void resolveDisciplinary(Long recordId, String resolution, String sanction) {
        DisciplinaryRecord record = disciplinaryRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Registro disciplinario no encontrado"));

        record.setStatus("RESUELTO");
        record.setCommitteeResolution(resolution);
        record.setSanction(sanction);
        record.setResolutionDate(java.time.LocalDateTime.now());
        disciplinaryRepository.save(record);

        // Notify teacher
        if (record.getTeacherProfile().getStaff().getUser() != null) {
            notificationService.createNotification(
                    "Resolución de Proceso Disciplinario",
                    "Se ha emitido una resolución para su caso. Sanción: " + sanction,
                    NotificationType.DISCIPLINARY,
                    record.getTeacherProfile().getStaff().getUser());
        }
    }

    public List<DisciplinaryRecord> getTeacherDisciplinaryHistory(Long profileId) {
        return disciplinaryRepository.findByTeacherProfileId(profileId);
    }

    @SuppressWarnings("null")
    public DisciplinaryRecord createDisciplinaryRecord(Long profileId, String type, String description,
            java.time.LocalDateTime date) {
        TeacherProfile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil docente no encontrado"));

        DisciplinaryRecord record = new DisciplinaryRecord();
        record.setTeacherProfile(profile);
        record.setInfractionType(type);
        record.setDescription(description);
        record.setIncidentDate(date);
        record.setStatus("INICIADO");

        DisciplinaryRecord saved = disciplinaryRepository.save(record);

        // Notify teacher
        if (profile.getStaff().getUser() != null) {
            notificationService.createNotification(
                    "Notificación Administrativa",
                    "Se ha iniciado un reporte disciplinario por: " + type,
                    NotificationType.DISCIPLINARY,
                    profile.getStaff().getUser());
        }

        return saved;
    }

    public List<TeacherDevelopment> getTeacherFiles(Long profileId) {
        return developmentRepository.findByTeacherProfileId(profileId);
    }

    @SuppressWarnings("null")
    public void updateFileReference(Long logId, String filePath) {
        TeacherDevelopment log = developmentRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("Registro de desarrollo no encontrado"));
        log.setFilePath(filePath);
        developmentRepository.save(log);
    }

    @SuppressWarnings("null")
    public TeacherDevelopment createProfessionalLogWithFile(Long profileId, String title, String institution,
            java.time.LocalDate date,
            String filePath) {
        TeacherProfile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil docente no encontrado"));

        TeacherDevelopment dev = new TeacherDevelopment();
        dev.setTeacherProfile(profile);
        dev.setTitle(title);
        dev.setInstitution(institution);
        dev.setAttainmentDate(date);
        dev.setFilePath(filePath);
        dev.setVerified(false); // Requiere revisión de la junta

        return developmentRepository.save(dev);
    }

    public Map<String, Object> getEscalafonStats() {
        List<TeacherProfile> profiles = profileRepository.findAll();
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalTeachers", profiles.size());

        // Count by category
        Map<String, Long> categoryCount = profiles.stream()
                .filter(p -> p.getEscalafonCategory() != null)
                .collect(Collectors.groupingBy(TeacherProfile::getEscalafonCategory, Collectors.counting()));
        stats.put("categoryStats", categoryCount);

        // Average points
        double avgPoints = profiles.stream()
                .mapToDouble(p -> p.getCurrentPoints() != null ? p.getCurrentPoints() : 0.0)
                .average().orElse(0.0);
        stats.put("averagePoints", avgPoints);

        return stats;
    }
}
