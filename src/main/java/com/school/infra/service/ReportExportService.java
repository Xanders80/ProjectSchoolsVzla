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

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;

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
		Document document = new Document();
		PdfWriter.getInstance(document, baos);
		document.open();

		com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA, 18, com.lowagie.text.Font.BOLD);
		com.lowagie.text.Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
		com.lowagie.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 10, com.lowagie.text.Font.BOLD);
		com.lowagie.text.Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

		Paragraph title = new Paragraph("Reporte de Estadísticas de Laboratorios", titleFont);
		title.setAlignment(Element.ALIGN_CENTER);
		document.add(title);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		Paragraph dateRange = new Paragraph(
				String.format("Período: %s - %s", from.format(formatter), to.format(formatter)), normalFont);
		dateRange.setAlignment(Element.ALIGN_CENTER);
		dateRange.setSpacingAfter(20);
		document.add(dateRange);

		Map<String, Object> generalStats = statisticsService.getGeneralStatistics(from, to);
		document.add(new Paragraph("Resumen General", FontFactory.getFont(FontFactory.HELVETICA, 14, com.lowagie.text.Font.BOLD)));

		Table summaryTable = new Table(2);
		summaryTable.setWidth(100);
		summaryTable.setPadding(5);
		addPdfRow(summaryTable, "Total de Reservas:", String.valueOf(generalStats.get("totalReservations")), headerFont, cellFont);
		addPdfRow(summaryTable, "Reservas Aprobadas:", String.valueOf(generalStats.get("approvedReservations")), headerFont, cellFont);
		addPdfRow(summaryTable, "Tasa de Aprobación:", generalStats.get("approvalRate") + "%", headerFont, cellFont);
		addPdfRow(summaryTable, "Total Horas Reservadas:", String.valueOf(generalStats.get("totalHoursReserved")), headerFont, cellFont);
		addPdfRow(summaryTable, "Promedio Horas/Reserva:", String.valueOf(generalStats.get("averageHoursPerReservation")), headerFont, cellFont);
		document.add(summaryTable);

		List<LabStatisticsDTO> labStats = statisticsService.getStatisticsByDateRange(from, to);
		document.add(new Paragraph("Estadísticas por Laboratorio", FontFactory.getFont(FontFactory.HELVETICA, 14, com.lowagie.text.Font.BOLD)));

		Table labTable = new Table(6);
		labTable.setWidth(100);
		labTable.setPadding(5);
		labTable.addCell(new Phrase("Laboratorio", headerFont));
		labTable.addCell(new Phrase("Total", headerFont));
		labTable.addCell(new Phrase("Aprobadas", headerFont));
		labTable.addCell(new Phrase("Rechazadas", headerFont));
		labTable.addCell(new Phrase("Ocupación %", headerFont));
		labTable.addCell(new Phrase("Horas", headerFont));

		for (LabStatisticsDTO stat : labStats) {
			labTable.addCell(new Phrase(stat.getRoomNumber(), cellFont));
			labTable.addCell(new Phrase(stat.getTotalReservations().toString(), cellFont));
			labTable.addCell(new Phrase(stat.getApprovedReservations().toString(), cellFont));
			labTable.addCell(new Phrase(stat.getRejectedReservations().toString(), cellFont));
			labTable.addCell(new Phrase(stat.getOccupancyRate().toString(), cellFont));
			labTable.addCell(new Phrase(stat.getTotalHoursReserved().toString(), cellFont));
		}
		document.add(labTable);

		List<TeacherUsageDTO> topTeachers = statisticsService.getTopTeachersByUsage(10, from, to);
		document.add(new Paragraph("Top 10 Docentes", FontFactory.getFont(FontFactory.HELVETICA, 14, com.lowagie.text.Font.BOLD)));

		Table teacherTable = new Table(4);
		teacherTable.setWidth(100);
		teacherTable.setPadding(5);
		teacherTable.addCell(new Phrase("Docente", headerFont));
		teacherTable.addCell(new Phrase("DNI", headerFont));
		teacherTable.addCell(new Phrase("Reservas", headerFont));
		teacherTable.addCell(new Phrase("Horas", headerFont));

		for (TeacherUsageDTO teacher : topTeachers) {
			teacherTable.addCell(new Phrase(teacher.getTeacherName(), cellFont));
			teacherTable.addCell(new Phrase(teacher.getTeacherDni(), cellFont));
			teacherTable.addCell(new Phrase(teacher.getReservationCount().toString(), cellFont));
			teacherTable.addCell(new Phrase(teacher.getTotalHours().toString(), cellFont));
		}
		document.add(teacherTable);

		Paragraph footer = new Paragraph("Generado el: " + LocalDate.now().format(formatter),
				FontFactory.getFont(FontFactory.HELVETICA, 10));
		footer.setAlignment(Element.ALIGN_RIGHT);
		document.add(footer);

		document.close();
		return baos.toByteArray();
	}

	private void addPdfRow(Table table, String label, String value, com.lowagie.text.Font labelFont, com.lowagie.text.Font valueFont) {
		table.addCell(new Phrase(label, labelFont));
		table.addCell(new Phrase(value, valueFont));
	}

	public byte[] exportToExcel(LocalDate from, LocalDate to) throws Exception {
		Workbook workbook = new XSSFWorkbook();

		CellStyle headerStyle = createHeaderStyle(workbook);
		CellStyle dataStyle = createDataStyle(workbook);

		Sheet summarySheet = workbook.createSheet("Resumen General");
		createSummarySheet(summarySheet, from, to, headerStyle, dataStyle);

		Sheet labSheet = workbook.createSheet("Por Laboratorio");
		createLabStatisticsSheet(labSheet, from, to, headerStyle, dataStyle);

		Sheet teacherSheet = workbook.createSheet("Top Docentes");
		createTeacherStatisticsSheet(teacherSheet, from, to, headerStyle, dataStyle);

		for (int i = 0; i < 3; i++) {
			Sheet sheet = workbook.getSheetAt(i);
			for (int j = 0; j < 10; j++) {
				try {
					sheet.autoSizeColumn(j);
				} catch (Exception e) {
					org.slf4j.LoggerFactory.getLogger(ReportExportService.class)
							.debug("Could not auto-size column {} on sheet {}", j, i);
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
		createDataRow(sheet, rowNum++, "Total de Reservas", stats.get("totalReservations").toString(), headerStyle, dataStyle);
		createDataRow(sheet, rowNum++, "Reservas Aprobadas", stats.get("approvedReservations").toString(), headerStyle, dataStyle);
		createDataRow(sheet, rowNum++, "Reservas Pendientes", stats.get("pendingReservations").toString(), headerStyle, dataStyle);
		createDataRow(sheet, rowNum++, "Reservas Rechazadas", stats.get("rejectedReservations").toString(), headerStyle, dataStyle);
		createDataRow(sheet, rowNum++, "Tasa de Aprobación (%)", stats.get("approvalRate").toString(), headerStyle, dataStyle);
		createDataRow(sheet, rowNum++, "Total Horas Reservadas", stats.get("totalHoursReserved").toString(), headerStyle, dataStyle);
		createDataRow(sheet, rowNum++, "Promedio Horas/Reserva", stats.get("averageHoursPerReservation").toString(), headerStyle, dataStyle);
	}

	private void createLabStatisticsSheet(Sheet sheet, LocalDate from, LocalDate to, CellStyle headerStyle,
			CellStyle dataStyle) {
		List<LabStatisticsDTO> labStats = statisticsService.getStatisticsByDateRange(from, to);

		Row headerRow = sheet.createRow(0);
		String[] headers = { "Laboratorio", "Total Reservas", "Aprobadas", "Rechazadas", "Pendientes", "Ocupación %", "Horas Totales" };
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerStyle);
		}

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

		Row headerRow = sheet.createRow(0);
		String[] headers = { "Docente", "DNI", "Total Reservas", "Reservas Aprobadas", "Horas Totales" };
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerStyle);
		}

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
}
