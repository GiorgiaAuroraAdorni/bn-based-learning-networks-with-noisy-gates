package ch.idsia.itas;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

/**
 * Author:  Claudio "Dna" Bonesana
 * Project: 2022-flairs
 * Date:    09.02.2022 11:10
 */
public class Utils {

	public static int cellToInt(Cell cell) {
		if (cell.getCellType().equals(CellType.STRING))
			return Integer.parseInt(cell.getStringCellValue());
		return Double.valueOf(cell.getNumericCellValue()).intValue();
	}

	public static String questionName(String questionId, String subQuestionId) {
		return questionId + "_" + subQuestionId;
	}
}
