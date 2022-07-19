package ch.idsia.intas;

import ch.idsia.crema.factor.bayesian.BayesianFactor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Author:  Claudio "Dna" Bonesana
 * Project: 2022-flairs
 * Date:    09.02.2022 14:54
 */
public class Results {

	static final int HEADER_ROWS = 2;

	public static void results(Model model, List<Student> students, Path output, Path input) throws IOException {

		try (
				final FileOutputStream fos = new FileOutputStream(output.toFile());
				final Workbook template = new XSSFWorkbook(new FileInputStream(input.toFile()));
				final Workbook workbook = new XSSFWorkbook()
		) {
			final Sheet sheet = workbook.createSheet("Results");
			final Sheet source = template.getSheetAt(0);

			// header rows
			sheet.createRow(0);
			sheet.createRow(1);

			// styles used for header col0
			final CellStyle styleHeader = workbook.createCellStyle();
			styleHeader.cloneStyleFrom(source.getRow(0).getCell(0).getCellStyle());

			// cell "Student_ID"
			final Cell cellId = sheet.getRow(0).createCell(0);
			cellId.setCellValue("Student_ID");
			cellId.setCellStyle(styleHeader);

			// cell "Question_ID"
			final Cell cellQid = sheet.getRow(0).createCell(1);
			cellQid.setCellValue("Q_ID");
			cellQid.setCellStyle(styleHeader);

			// cell "Answer"
			final Cell cellAnswer = sheet.getRow(0).createCell(2);
			cellAnswer.setCellValue("Answer");
			cellAnswer.setCellStyle(styleHeader);

			final CellRangeAddress r1 = new CellRangeAddress(0, 1, 0, 0);
			sheet.addMergedRegion(r1);
			RegionUtil.setBorderBottom(BorderStyle.THIN, r1, sheet);
			RegionUtil.setBorderRight(BorderStyle.THIN, r1, sheet);

			final CellRangeAddress r2 = new CellRangeAddress(0, 1, 1, 1);
			sheet.addMergedRegion(r2);
			RegionUtil.setBorderBottom(BorderStyle.THIN, r2, sheet);
			RegionUtil.setBorderRight(BorderStyle.THIN, r2, sheet);

			final CellRangeAddress r3 = new CellRangeAddress(0, 1, 2, 2);
			sheet.addMergedRegion(r3);
			RegionUtil.setBorderBottom(BorderStyle.THIN, r3, sheet);
			RegionUtil.setBorderRight(BorderStyle.THIN, r3, sheet);

			final CellStyle styleHeaderSkills = workbook.createCellStyle();
			styleHeaderSkills.cloneStyleFrom(source.getRow(0).getCell(Model.SKILL_START).getCellStyle());

			// cell "Skills"
			final Cell cellSkills = sheet.getRow(0).createCell(3);
			cellSkills.setCellValue("Skills");
			cellSkills.setCellStyle(styleHeaderSkills);

			final CellRangeAddress r4 = new CellRangeAddress(0, 0, 3, 2 + model.nSkill());
			sheet.addMergedRegion(r4);
			RegionUtil.setBorderBottom(BorderStyle.THIN, r4, sheet);
			RegionUtil.setBorderRight(BorderStyle.THIN, r4, sheet);

			// style for skill cells
			final CellStyle styleHeaderBot = workbook.createCellStyle();
			styleHeaderBot.cloneStyleFrom(source.getRow(1).getCell(Model.SKILL_START).getCellStyle());

			final CellStyle styleHeaderBot2 = workbook.createCellStyle();
			styleHeaderBot2.cloneStyleFrom(styleHeaderBot);
			styleHeaderBot2.setBorderRight(BorderStyle.THIN);
			styleHeaderBot2.setBorderBottom(BorderStyle.THIN);

			// add skill cells
			for (int c = 0; c < model.nSkill(); c++) {
				final Cell cell = sheet.getRow(1).createCell(3 + c);
				cell.setCellValue(source.getRow(1).getCell(Model.SKILL_START + c).getStringCellValue());
				cell.setCellStyle(styleHeaderBot);
				sheet.setColumnWidth(c, source.getColumnWidth(Model.SKILL_START + c));
			}

			sheet.getRow(1).getCell(2 + model.nSkill()).setCellStyle(styleHeaderBot2);

			// format for data
			final DataFormat format = workbook.createDataFormat();
			final CellStyle df = workbook.createCellStyle();
			final short f = format.getFormat("0.00");
			df.setDataFormat(f);

			for (int i = 0, l = 0; i < students.size(); i++) {
				final Student student = students.get(i);
				Row row;
				int j;
				Cell cell;

				// student answers
				for (String k : student.resultsPerQuestion.keySet()) {
					row = sheet.createRow(HEADER_ROWS + (l++));
					j = 0;
					cell = row.createCell(j++);
					cell.setCellValue(student.id);

					cell = row.createCell(j++);
					cell.setCellValue("Q" + k);

					cell = row.createCell(j++);
					cell.setCellValue(student.answers.get(k));

					for (String skill : model.skills) {
						cell = row.createCell(j++);
						final Map<String, BayesianFactor> map = student.resultsPerQuestion.get(k);
						if (map.containsKey(skill))
							cell.setCellValue(map.get(skill).getValue(1));
						cell.setCellStyle(df);
					}
				}

				j = 0;
				row = sheet.createRow(HEADER_ROWS + (l++));
				cell = row.createCell(j++);
				cell.setCellValue(student.id);

				j += 2;

				// student skills
				for (String skill : model.skills) {
					cell = row.createCell(j++);
					if (student.results.containsKey(skill))
						cell.setCellValue(student.results.get(skill).getValue(1));
					cell.setCellStyle(df);
				}
			}

			workbook.write(fos);
		}
	}
}
