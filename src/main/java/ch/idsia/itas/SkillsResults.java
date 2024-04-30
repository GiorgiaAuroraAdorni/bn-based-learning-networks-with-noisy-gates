package ch.idsia.itas;

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
public class SkillsResults {

	static final int HEADER_ROWS = 2;

	public static void skillsResults(Model model, List<Student> students, Path output, Path input) throws IOException {

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

			final CellRangeAddress rId = new CellRangeAddress(0, 1, 0, 0);
			sheet.addMergedRegion(rId);
			RegionUtil.setBorderBottom(BorderStyle.THIN, rId, sheet);
			RegionUtil.setBorderRight(BorderStyle.THIN, rId, sheet);

			// cell "Question_ID"
			final Cell cellQid = sheet.getRow(0).createCell(1);
			cellQid.setCellValue("Question_ID");
			cellQid.setCellStyle(styleHeader);

			final CellRangeAddress rQid = new CellRangeAddress(0, 1, 1, 1);
			sheet.addMergedRegion(rQid);
			RegionUtil.setBorderBottom(BorderStyle.THIN, rQid, sheet);
			RegionUtil.setBorderRight(BorderStyle.THIN, rQid, sheet);

			// cell "Sub_Question_ID"
			final Cell cellSqid = sheet.getRow(0).createCell(2);
			cellSqid.setCellValue("Sub_Question_ID");
			cellSqid.setCellStyle(styleHeader);

			final CellRangeAddress rSqid = new CellRangeAddress(0, 1, 2, 2);
			sheet.addMergedRegion(rSqid);
			RegionUtil.setBorderBottom(BorderStyle.THIN, rSqid, sheet);
			RegionUtil.setBorderRight(BorderStyle.THIN, rSqid, sheet);

			// cell "Answer"
			final Cell cellAnswer = sheet.getRow(0).createCell(3);
			cellAnswer.setCellValue("Answer");
			cellAnswer.setCellStyle(styleHeader);

			final CellRangeAddress rAns = new CellRangeAddress(0, 1, 3, 3);
			sheet.addMergedRegion(rAns);
			RegionUtil.setBorderBottom(BorderStyle.THIN, rAns, sheet);
			RegionUtil.setBorderRight(BorderStyle.THIN, rAns, sheet);

			// add skill cells
			for (int c = 0; c < model.nSkill(); c++) {
				final Cell srcDescr = source.getRow(2).getCell(Model.SKILL_START + c);
				final CellStyle styleDescr = workbook.createCellStyle();
				styleDescr.cloneStyleFrom(srcDescr.getCellStyle());

				final Cell cellDescr = sheet.getRow(0).createCell(Model.SKILL_START + c + 1);
				cellDescr.setCellValue(srcDescr.getStringCellValue());
				cellDescr.setCellStyle(styleDescr);
				sheet.setColumnWidth(c, source.getColumnWidth(Model.SKILL_START + c));

				final Cell srcName = source.getRow(3).getCell(Model.SKILL_START + c);
				CellStyle styleName = workbook.createCellStyle();
				styleDescr.cloneStyleFrom(srcDescr.getCellStyle());
				styleName.setBorderBottom(BorderStyle.THIN);

				final Cell cellName = sheet.getRow(1).createCell(Model.SKILL_START + c + 1);
				cellName.setCellValue(srcName.getStringCellValue());
				cellName.setCellStyle(styleName);
				sheet.setColumnWidth(c, source.getColumnWidth(Model.SKILL_START + c));
			}

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
				for (String k : student.resultsSkillsPerQuestion.keySet()) {
					row = sheet.createRow(HEADER_ROWS + (l++));
					j = 0;
					cell = row.createCell(j++);
					cell.setCellValue(student.id);

					final String[] ks = k.split("_");
					final int qid = Integer.parseInt(ks[0]);
					final int sqid = Integer.parseInt(ks[1]);

					cell = row.createCell(j++);
					cell.setCellValue(qid);

					cell = row.createCell(j++);
					cell.setCellValue(sqid);

					cell = row.createCell(j++);
					cell.setCellValue(student.answers.get(k));

					for (Model.Skill skill : model.skills) {
						cell = row.createCell(j++);
						final Map<Model.Skill, BayesianFactor> map = student.resultsSkillsPerQuestion.get(k);
						if (map.containsKey(skill))
							cell.setCellValue(map.get(skill).getValue(1));
						cell.setCellStyle(df);
					}
				}

				j = 0;
				row = sheet.createRow(HEADER_ROWS + (l++));
				cell = row.createCell(j++);
				cell.setCellValue(student.id);

				j += 3;

				// student skills
				for (Model.Skill skill : model.skills) {
					cell = row.createCell(j++);
					if (student.resultsSkills.containsKey(skill))
						cell.setCellValue(student.resultsSkills.get(skill).getValue(1));
					cell.setCellStyle(df);
				}
			}

			workbook.write(fos);
		}
	}
}
