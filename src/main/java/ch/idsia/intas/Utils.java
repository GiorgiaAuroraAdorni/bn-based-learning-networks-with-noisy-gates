package ch.idsia.intas;

import org.apache.poi.ss.usermodel.Cell;

/**
 * Author:  Claudio "Dna" Bonesana
 * Project: 2022-flairs
 * Date:    09.02.2022 11:10
 */
public class Utils {

	public static int cellToInt(Cell cell) {
		return Double.valueOf(cell.getNumericCellValue()).intValue();
	}

	public static String questionName(String questionId, String subQuestionId) {
		return questionId + "_" + subQuestionId;
	}
}
