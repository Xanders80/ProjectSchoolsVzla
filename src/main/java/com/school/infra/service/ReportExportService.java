package com.school.infra.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.school.infra.dto.LabStatisticsDTO;
import com.school.infra.dto.TeacherUsageDTO;

@Service
public class ReportExportService {

    private final LabStatisticsService statisticsService;

    public ReportExportService(LabStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    public byte[] exportToPdf(LocalDate from, LocalDate to) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Title
        Paragraph title = new Paragraph("Reporte de Estadísticas de Laboratorios")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);

        // Date range
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Paragraph dateRange = new Paragraph(
                String.format("Período: %s - %s", from.format(formatter), to.format(formatter)))
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(dateRange);

        // General Statistics
        Map<String, Object> generalStats = statisticsService.getGeneralStatistics(from, to);
        document.add(new Paragraph("Resumen General").setFontSize(14).setBold().setMarginTop(10));

        Table summaryTable = new Table(UnitValue.createPercentArray(new float[] { 1, 1 }))
                .useAllAvailableWidth();
        summaryTable.addCell(createCell("Total de Reservas:", true));
        summaryTable.addCell(createCell(generalStats.get("totalReservations").toString(), false));
        summaryTable.addCell(createCell("Reservas Aprobadas:", true));
        summaryTable.addCell(createCell(generalStats.get("approvedReservations").toString(), false));
        summaryTable.addCell(createCell("Tasa de Aprobación:", true));
        summaryTable.addCell(createCell(generalStats.get("approvalRate") + "%", false));
        summaryTable.addCell(createCell("Total Horas Reservadas:", true));
        summaryTable.addCell(createCell(generalStats.get("totalHoursReserved").toString(), false));
        summaryTable.addCell(createCell("Promedio Horas/Reserva:", true));
        summaryTable.addCell(createCell(generalStats.get("averageHoursPerReservation").toString(), false));

        document.add(summaryTable);

        // Lab Statistics
        List<LabStatisticsDTO> labStats = statisticsService.getStatisticsByDateRange(from, to);
        document.add(new Paragraph("Estadísticas por Laboratorio").setFontSize(14).setBold().setMarginTop(20));

        Table labTable = new Table(UnitValue.createPercentArray(new float[] { 2, 1, 1, 1, 1, 1 }))
                .useAllAvailableWidth();

        // Header
        labTable.addHeaderCell(createHeaderCell("Laboratorio"));
        labTable.addHeaderCell(createHeaderCell("Total"));
        labTable.addHeaderCell(createHeaderCell("Aprobadas"));
        labTable.addHeaderCell(createHeaderCell("Rechazadas"));
        labTable.addHeaderCell(createHeaderCell("Ocupación %"));
        labTable.addHeaderCell(createHeaderCell("Horas"));

        // Data
        for (LabStatisticsDTO stat : labStats) {
            labTable.addCell(createCell(stat.getRoomNumber(), false));
            labTable.addCell(createCell(stat.getTotalReservations().toString(), false));
            labTable.addCell(createCell(stat.getApprovedReservations().toString(), false));
            labTable.addCell(createCell(stat.getRejectedReservations().toString(), false));
            labTable.addCell(createCell(stat.getOccupancyRate().toString(), false));
            labTable.addCell(createCell(stat.getTotalHoursReserved().toString(), false));
        }

        document.add(labTable);

        // Top Teachers
        List<TeacherUsageDTO> topTeachers = statisticsService.getTopTeachersByUsage(10, from, to);
        document.add(new Paragraph("Top 10 Docentes").setFontSize(14).setBold().setMarginTop(20));

        Table teacherTable = new Table(UnitValue.createPercentArray(new float[] { 3, 2, 1, 1 }))
                .useAllAvailableWidth();

        teacherTable.addHeaderCell(createHeaderCell("Docente"));
        teacherTable.addHeaderCell(createHeaderCell("DNI"));
        teacherTable.addHeaderCell(createHeaderCell("Reservas"));
        teacherTable.addHeaderCell(createHeaderCell("Horas"));

        for (TeacherUsageDTO teacher : topTeachers) {
            teacherTable.addCell(createCell(teacher.getTeacherName(), false));
            teacherTable.addCell(createCell(teacher.getTeacherDni(), false));
            teacherTable.addCell(createCell(teacher.getReservationCount().toString(), false));
            teacherTable.addCell(createCell(teacher.getTotalHours().toString(), false));
        }

        document.add(teacherTable);

        // Footer
        Paragraph footer = new Paragraph(
                "Generado el: " + LocalDate.now().format(formatter))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(30);
        document.add(footer);

        document.close();
        return baos.toByteArray();
    }

    public byte[] exportToExcel(LocalDate from, LocalDate to) throws Exception {
        Workbook workbook = new XSSFWorkbook();

        // Styles
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        // Sheet 1: General Statistics
        Sheet summarySheet = workbook.createSheet("Resumen General");
        createSummarySheet(summarySheet, from, to, headerStyle, dataStyle);

        // Sheet 2: Lab Statistics
        Sheet labSheet = workbook.createSheet("Por Laboratorio");
        createLabStatisticsSheet(labSheet, from, to, headerStyle, dataStyle);

        // Sheet 3: Top Teachers
        Sheet teacherSheet = workbook.createSheet("Top Docentes");
        createTeacherStatisticsSheet(teacherSheet, from, to, headerStyle, dataStyle);

        // Auto-size columns
        for (int i = 0; i < 3; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            for (int j = 0; j < 10; j++) {
                try {
                    sheet.autoSizeColumn(j);
                } catch (Exception e) {
                    // Ignore
                }
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        return baos.toByteArray();
    }

    private void createSummarySheet(Sheet sheet, LocalDate from, LocalDate to, CellStyle headerStyle,
            CellStyle dataStyle) {
        Map<String, Object> stats = statisticsService.getGeneralStatistics(from, to);

        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Resumen General de Estadísticas");
        titleCell.setCellStyle(headerStyle);

        int rowNum = 2;
        createDataRow(sheet, rowNum++, "Total de Reservas", stats.get("totalReservations").toString(), headerStyle,
                dataStyle);
        createDataRow(sheet, rowNum++, "Reservas Aprobadas", stats.get("approvedReservations").toString(),
                headerStyle, dataStyle);
        createDataRow(sheet, rowNum++, "Reservas Pendientes", stats.get("pendingReservations").toString(),
                headerStyle, dataStyle);
        createDataRow(sheet, rowNum++, "Reservas Rechazadas", stats.get("rejectedReservations").toString(),
                headerStyle, dataStyle);
        createDataRow(sheet, rowNum++, "Tasa de Aprobación (%)", stats.get("approvalRate").toString(), headerStyle,
                dataStyle);
        createDataRow(sheet, rowNum++, "Total Horas Reservadas", stats.get("totalHoursReserved").toString(),
                headerStyle, dataStyle);
        createDataRow(sheet, rowNum++, "Promedio Horas/Reserva", stats.get("averageHoursPerReservation").toString(),
                headerStyle, dataStyle);
    }

    private void createLabStatisticsSheet(Sheet sheet, LocalDate from, LocalDate to, CellStyle headerStyle,
            CellStyle dataStyle) {
        List<LabStatisticsDTO> labStats = statisticsService.getStatisticsByDateRange(from, to);

        // Header
        Row headerRow = sheet.createRow(0);
        String[] headers = { "Laboratorio", "Total Reservas", "Aprobadas", "Rechazadas", "Pendientes", "Ocupación %",
                "Horas Totales" };
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data
        int rowNum = 1;
        for (LabStatisticsDTO stat : labStats) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(stat.getRoomNumber());
            row.createCell(1).setCellValue(stat.getTotalReservations());
            row.createCell(2).setCellValue(stat.getApprovedReservations());
            row.createCell(3).setCellValue(stat.getRejectedReservations());
            row.createCell(4).setCellValue(stat.getPendingReservations());
            row.createCell(5).setCellValue(stat.getOccupancyRate());
            row.createCell(6).setCellValue(stat.getTotalHoursReserved());

            for (int i = 0; i < 7; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }
    }

    private void createTeacherStatisticsSheet(Sheet sheet, LocalDate from, LocalDate to, CellStyle headerStyle,
            CellStyle dataStyle) {
        List<TeacherUsageDTO> teachers = statisticsService.getTopTeachersByUsage(10, from, to);

        // Header
        Row headerRow = sheet.createRow(0);
        String[] headers = { "Docente", "DNI", "Total Reservas", "Reservas Aprobadas", "Horas Totales" };
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data
        int rowNum = 1;
        for (TeacherUsageDTO teacher : teachers) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(teacher.getTeacherName());
            row.createCell(1).setCellValue(teacher.getTeacherDni());
            row.createCell(2).setCellValue(teacher.getReservationCount());
            row.createCell(3).setCellValue(teacher.getApprovedCount());
            row.createCell(4).setCellValue(teacher.getTotalHours());

            for (int i = 0; i < 5; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }
    }

    private void createDataRow(Sheet sheet, int rowNum, String label, String value, CellStyle headerStyle,
            CellStyle dataStyle) {
        Row row = sheet.createRow(rowNum);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(headerStyle);

        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value);
        valueCell.setCellStyle(dataStyle);
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    // PDF Helper methods
    private com.itextpdf.layout.element.Cell createCell(String content, boolean isBold) {
        com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(content));
        if (isBold) {
            cell.setBold();
        }
        return cell;
    }

    private com.itextpdf.layout.element.Cell createHeaderCell(String content) {
        return new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(content))
                .setBold()
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER);
    }
}
