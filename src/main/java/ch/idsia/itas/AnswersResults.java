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
import java.util.*;

/**
 * Author:  Giorgia Adorni
 * Date:    29.04.2024 15:35
 */
public class AnswersResults {

	static final int HEADER_ROWS = 2;

	public static void answersResults(Model model, List<Student> students, Path output, Path input) throws IOException {

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

			Map<String, Integer> questionIdColumnIndexMap = new HashMap<>();
			int columnIndex = 4; // Index of the column after existing columns

			// Sort question IDs
			List<String> sortedQuestionIds = new ArrayList<>(model.questionIds);
			Collections.sort(sortedQuestionIds, (id1, id2) -> {
				String[] parts1 = id1.split("_");
				String[] parts2 = id2.split("_");
				int x1 = Integer.parseInt(parts1[0]);
				int y1 = Integer.parseInt(parts1[1]);
				int x2 = Integer.parseInt(parts2[0]);
				int y2 = Integer.parseInt(parts2[1]);
				if (x1 != x2) {
					return Integer.compare(x1, x2);
				} else {
					return Integer.compare(y1, y2);
				}
			});


			// Add header for each sorted question ID
			for (String questionId : sortedQuestionIds) {
				Cell headerCell = sheet.getRow(0).createCell(columnIndex);
				headerCell.setCellValue(questionId);
				questionIdColumnIndexMap.put(questionId, columnIndex); // Map question ID to column index
				columnIndex++;
			}

			// Format for data
			final DataFormat format = workbook.createDataFormat();
			final CellStyle df = workbook.createCellStyle();
			final short f = format.getFormat("0.00");
			df.setDataFormat(f);

			// Insert student answers
			int rowIndex = HEADER_ROWS;
			for (Student student : students) {
				for (String k : student.resultsAnswersPerQuestion.keySet()) {
					Row row = sheet.createRow(rowIndex);
					String[] ks = k.split("_");
					int qid = Integer.parseInt(ks[0]);
					int sqid = Integer.parseInt(ks[1]);
					row.createCell(0).setCellValue(student.id);
					row.createCell(1).setCellValue(qid);
					row.createCell(2).setCellValue(sqid);
					row.createCell(3).setCellValue(student.answers.get(k));

					// Insert probabilities for each question ID
					for (String questionId : sortedQuestionIds) {
						Cell cell = row.createCell(questionIdColumnIndexMap.get(questionId));
						Map<String, BayesianFactor> probabilities = student.resultsAnswersPerQuestion.get(k);
						if (probabilities.containsKey(questionId)) {
							BayesianFactor probability = probabilities.get(questionId);
							cell.setCellValue(probability.getValue(1));
						} else {
							cell.setCellValue(""); // Leave cell empty if no associated probability
						}
						cell.setCellStyle(df);
					}
					rowIndex++;
				}

				// Writing results for each answer
				int j = 4;
				Row rowAnswers = sheet.createRow(rowIndex);
				rowAnswers.createCell(0).setCellValue(student.id);
				rowIndex++;

				for (String answerQuestion : student.resultsAnswers.keySet()) {
					BayesianFactor probability = student.resultsAnswers.get(answerQuestion);
					int column = questionIdColumnIndexMap.get(answerQuestion);
					Cell cell = rowAnswers.createCell(column);
					cell.setCellValue(probability.getValue(1));
					cell.setCellStyle(df);
				}
			}

			workbook.write(fos);
		}
	}
}
