package ch.idsia.itas;

import ch.idsia.crema.factor.bayesian.BayesianFactor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static ch.idsia.itas.Utils.cellToInt;
import static ch.idsia.itas.Utils.questionName;

/**
 * Author:  Claudio "Dna" Bonesana
 * Project: 2022-flairs
 * Date:    09.02.2022 10:49
 */
public class Student {

	static int Q_ID = 0;
	static int SQ_ID = 1;
	static int ANSWER_START = 3;

	static int STUDENTS_ID_ROW = 0;
	static int ANSWERS_FIRST_ROW = 1;

	final int id;
	final Map<String, String> answers = new LinkedHashMap<>();

	final Map<String, BayesianFactor> results = new LinkedHashMap<String, BayesianFactor>();
	final Map<String, Map<String, BayesianFactor>> resultsPerQuestion = new LinkedHashMap<String, Map<String, BayesianFactor>>();

	public Student(int id) {
		this.id = id;
	}

	/**
	 * @param qid    question id
	 * @param sqid   sub-question id
	 * @param answer answer given
	 */
	public void addAnswer(String qid, String sqid, String answer) {
		answers.put(questionName(qid, sqid), answer);
	}

	public static List<Student> parse(Path path) throws IOException {
		List<Student> students = new ArrayList<>();
		try (final Workbook workbook = new XSSFWorkbook(new FileInputStream(path.toFile()))) {
			final Sheet sheetAnswer = workbook.getSheetAt(0);

			// parse first row for column types
			final Row header = sheetAnswer.getRow(0);
			for (int c = header.getFirstCellNum(); c < header.getLastCellNum(); c++) {
				final Cell cell = header.getCell(c);
				if (!cell.getCellType().equals(CellType.STRING))
					continue;
				switch (cell.getStringCellValue().toUpperCase()) {
					case "QUESTION_ID" -> Q_ID = c;
					case "SUB_QUESTION_ID" -> SQ_ID = c;
					case "ANSWERS" -> ANSWER_START = c;
				}
			}

			// parse for students
			final Row studentIds = sheetAnswer.getRow(STUDENTS_ID_ROW);
			for (int i = ANSWER_START; i < studentIds.getLastCellNum(); i++) {
				final int id = cellToInt(studentIds.getCell(i));
				students.add(new Student(id));
			}

			String qid = null, sqid = null;

			// parse for answers
			for (int r = ANSWERS_FIRST_ROW; r < sheetAnswer.getLastRowNum(); r++) {
				final Row row = sheetAnswer.getRow(r);

				final Cell cellQID = row.getCell(Q_ID);
				final Cell cellSQID = row.getCell(SQ_ID);

				if (cellSQID == null || cellSQID.toString().isEmpty())
					continue;

				if (qid == null || !cellQID.toString().isEmpty())
					qid = "" + cellToInt(cellQID);

				if (sqid == null || !cellSQID.toString().isEmpty())
					sqid = "" + cellToInt(cellSQID);

				for (int i = 0; i < students.size(); i++) {
					final Cell cell = row.getCell(ANSWER_START + i);

					if (cell == null || cell.getStringCellValue().isEmpty())
						continue;

					students.get(i).addAnswer(qid, sqid, cell.toString().trim().toLowerCase());
				}
			}
		}

		return students;
	}
}
