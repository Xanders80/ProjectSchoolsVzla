package com.school.web.controller.academic;

import java.util.Map;

import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.school.academic.service.DocumentService;

@Controller
@RequestMapping("/academic/documents")
@PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'STUDENT')")
public class CertificateController {

    private final DocumentService documentService;
    private final com.school.academic.util.PdfGenerator pdfGenerator;
    private final org.thymeleaf.TemplateEngine templateEngine;

    public CertificateController(DocumentService documentService,
            com.school.academic.util.PdfGenerator pdfGenerator,
            org.thymeleaf.TemplateEngine templateEngine) {
        this.documentService = documentService;
        this.pdfGenerator = pdfGenerator;
        this.templateEngine = templateEngine;
    }

    @GetMapping("/certificate/{studentId}")
    public String viewCertificate(@PathVariable @NonNull Long studentId, Model model) {
        Map<String, Object> data = documentService.getCertificateData(studentId);
        model.addAllAttributes(data);
        return "academic/reports/certificate-template";
    }

    @GetMapping("/acta/{sectionId}/{periodId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public String viewSectionActa(@PathVariable @NonNull Long sectionId, @PathVariable @NonNull Long periodId,
            Model model) {
        Map<String, Object> data = documentService.getSectionActaData(sectionId, periodId);
        model.addAllAttributes(data);
        return "academic/reports/acta-template";
    }

    @GetMapping("/download/certificate/{studentId}")
    public org.springframework.http.ResponseEntity<byte[]> downloadCertificate(@PathVariable @NonNull Long studentId) {
        Map<String, Object> data = documentService.getCertificateData(studentId);

        org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
        context.setVariables(data);

        String html = templateEngine.process("academic/reports/certificate-template", context);
        byte[] pdf = pdfGenerator.generatePdf(html);

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Certificado_" + studentId + ".pdf");

        return new org.springframework.http.ResponseEntity<>(pdf, headers, org.springframework.http.HttpStatus.OK);
    }
}
