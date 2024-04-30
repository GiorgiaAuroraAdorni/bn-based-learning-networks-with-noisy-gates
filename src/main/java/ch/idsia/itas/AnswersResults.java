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
			Row headerRow = sheet.createRow(0);
			headerRow.createCell(0).setCellValue("Question_ID");
			headerRow.createCell(1).setCellValue("Sub_Question_ID");
			headerRow.createCell(2).setCellValue("Question_Text");

			for (int i = 0; i < students.size(); i++) {
				headerRow.createCell(i + 3).setCellValue("Student_" + (i + 1));
			}

			// styles used for header
			final CellStyle styleHeader = workbook.createCellStyle();
			styleHeader.cloneStyleFrom(source.getRow(0).getCell(0).getCellStyle());

			// applying style to header
			for (Cell cell : headerRow) {
				cell.setCellStyle(styleHeader);
			}

			// merging cells for header
			for (int i = 0; i < 3; i++) {
				final CellRangeAddress mergedRegion = new CellRangeAddress(0, 1, i, i);
				sheet.addMergedRegion(mergedRegion);
				RegionUtil.setBorderBottom(BorderStyle.THIN, mergedRegion, sheet);
				RegionUtil.setBorderRight(BorderStyle.THIN, mergedRegion, sheet);
			}

			for (int i = 0, l = 0; i < students.size(); i++) {
				final Student student = students.get(i);
				Row row;
				int j;
				Cell cell;

				// Ciclo attraverso le domande e le sottodomande del modello
				for (Question question : model.questions.filter()) {
					for (SubQuestion subQuestion : question.subQuestions.filter()) {
						row = sheet.createRow(HEADER_ROWS + (l++));
						j = 0;
						// Inserisci l'ID dello studente
						cell = row.createCell(j++);
						cell.setCellValue(student.id);

						// Inserisci l'ID della domanda
						cell = row.createCell(j++);
						cell.setCellValue(question.id);

						// Inserisci l'ID della sottodomanda
						cell = row.createCell(j++);
						cell.setCellValue(subQuestion.id);

						// Inserisci la risposta dello studente (se necessario)
						cell = row.createCell(j++);
						String answerKey = question.id + "_" + subQuestion.id;
						cell.setCellValue(student.answers.getOrDefault(answerKey, ""));

						// Inserisci i valori di probabilità dal campo student.results
						Map<String, BayesianFactor> studentResults = student.resultsAnswersPerQuestion.get(answerKey);
						if (studentResults != null) {
							for (BayesianFactor results : studentResults.values()) {
								for (BayesianFactor result : results.filter()) {
									cell = row.createCell(j++);
									cell.setCellValue(result.getValue(1));
									cell.setCellStyle(df); // Se necessario
								}
							}
						}
					}
				}
			}

		workbook.write(fos);
		}
	}
}
