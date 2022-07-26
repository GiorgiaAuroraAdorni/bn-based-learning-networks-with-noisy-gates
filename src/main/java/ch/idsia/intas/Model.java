package ch.idsia.intas;

import ch.idsia.crema.factor.bayesian.BayesianFactor;
import ch.idsia.crema.factor.bayesian.BayesianFactorFactory;
import ch.idsia.crema.model.graphical.DAGModel;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static ch.idsia.intas.Utils.cellToInt;
import static ch.idsia.intas.Utils.questionName;

/**
 * Author:  Claudio "Dna" Bonesana
 * Project: 2022-flairs
 * Date:    09.02.2022 11:17
 */
public class Model {

	static int Q_ID = 0;
	static int SQ_ID = 1;
	static int Q_TYPE = 3;
	static int SKILL_START = 4;

	final DAGModel<BayesianFactor> model = new DAGModel<>();

	final Map<String, Integer> nameToIdx = new LinkedHashMap<>();
	final Map<Integer, String> idxToName = new LinkedHashMap<>();

	final List<String> skills = new ArrayList<>();
	final Set<String> questionIds = new HashSet<>();

	final Map<Integer, BayesianFactor> factors = new LinkedHashMap<>();

	final List<Integer> constraints = new ArrayList<>();

	boolean hasLeak = false;
	int leakVar = -1;

	int nSkill() {
		return skills.size();
	}

	/**
	 * @param name Name of the new skill
	 * @return the index of the new variable
	 */
	int addSkill(String name, double p1) {
		final int v = model.addVariable(2);
		nameToIdx.put(name, v);
		idxToName.put(v, name);
		skills.add(name);

		factors.put(v, BayesianFactorFactory.factory()
				.domain(model.getDomain(v))
				.data(new double[]{1.0 - p1, p1})
				.get()
		);

		if (name.equalsIgnoreCase("leak")) {
			hasLeak = true;
			leakVar = v;
		}

		return v;
	}

	/**
	 * Add a constraint node where the logic relation corresponds to (~L1)OR(L2).
	 *
	 * @param l1 skill L1
	 * @param l2 skill L2
	 */
	void addConstraint(String l2, String l1) {
		// left < right: (NOT left) OR (right)
		var L1 = nameToIdx.get(l1);
		var L2 = nameToIdx.get(l2);

		int D = model.addVariable(2);
		factors.put(D, BayesianFactorFactory.factory()
				.domain(model.getDomain(L1, L2, D))
				//  p(D) L1 L2  D
				.set(1.0, 0, 1, 1) // P(D=1|L1=0, L2=1)
				.set(0.0, 0, 1, 0) // P(D=0|L1=0, L2=1)

				.set(1.0, 0, 0, 1) // P(D=1|L1=0, L2=0)
				.set(0.0, 0, 0, 0) // P(D=0|L1=0, L2=0)

				.set(1.0, 1, 1, 1) // P(D=1|L1=1, L2=1)
				.set(0.0, 1, 1, 0) // P(D=0|L1=1, L2=1)

				.set(0.0, 1, 0, 1) // P(D=1|L1=1, L2=0)
				.set(1.0, 1, 0, 0) // P(D=0|L1=1, L2=0)
				.get()
		);

		constraints.add(D);
		model.addParents(D, L1, L2);
	}

	/**
	 * @param i        index of the skill
	 * @param lambda_i lambda_i value to use
	 * @return the index of the new variable
	 */
	int addXiAnd(int i, double lambda_i) {
		final String skill = skills.get(i);
		final int xi = nameToIdx.get(skill);

		// add Xi' nodes for each parents...
		final int xit = model.addVariable(2);
		model.addParent(xit, xi);
		nameToIdx.put(skill + "_i", xit);
		idxToName.put(xit, skill + "_i");

		/// ... and assign lambda
		factors.put(xit, BayesianFactorFactory.factory()
				.domain(model.getDomain(xi, xit))
				.set(1.0, 1, 1)            // P(Xi'=1|Xi=1) = 1
				.set(lambda_i, 0, 1)       // P(Xi'=1|Xi=0) = lambda_i
				.set(0.0, 1, 0)            // P(Xi'=0|Xi=1) = 0
				.set(1.0 - lambda_i, 0, 0) // P(Xi'=0|Xi=0) = 1 - lambda_i
				.get());

		// collect parents for AND node
		return xit;
	}

	/**
	 * @param i        index of the skill
	 * @param lambda_i lambda_i value to use
	 * @return the index of the new variable
	 */
	int addXiOr(int i, double lambda_i) {
		final String skill = skills.get(i);
		final int xi = nameToIdx.get(skill);

		// add Xi' nodes for each parents...
		final int xit = model.addVariable(2);
		model.addParent(xit, xi);
		nameToIdx.put(skill + "_i", xit);
		idxToName.put(xit, skill + "_i");

		/// ... and assign lambda
		factors.put(xit, BayesianFactorFactory.factory()
				.domain(model.getDomain(xi, xit))
				.set(1.0 - lambda_i, 1, 1) // P(Xi'=1|Xi=1) = 1 - lambda_i
				.set(0.0, 0, 1)            // P(Xi'=1|Xi=0) = 0
				.set(lambda_i, 1, 0)       // P(Xi'=0|Xi=1) = lambda_i
				.set(1.0, 0, 0)            // P(Xi'=0|Xi=0) = 1
				.get());

		// collect parents for AND node
		return xit;
	}

	/**
	 * @param nodeName Name of this node
	 * @param parents  parents variables of this node
	 * @return the index of the new variable
	 */
	int addQuestionAnd(String nodeName, TIntList parents) {
		questionIds.add(nodeName);

		// create and add AND node with X' parents
		final int and = model.addVariable(2);
		model.addParents(and, parents.toArray());
		nameToIdx.put(nodeName, and);
		idxToName.put(and, nodeName);

		final TIntList vars = new TIntArrayList(parents);
		vars.insert(0, and);

		factors.put(and, BayesianFactorFactory.factory()
				.domain(model.getDomain(vars.toArray()))
				.and(parents.toArray())
		);

		return and;
	}

	/**
	 * @param nodeName Name of this node
	 * @param parents  parents variables of this node
	 * @return the index of the new variable
	 */
	int addQuestionOr(String nodeName, TIntList parents) {
		questionIds.add(nodeName);

		// create and add AND node with X' parents
		final int or = model.addVariable(2);
		model.addParents(or, parents.toArray());
		nameToIdx.put(nodeName, or);
		idxToName.put(or, nodeName);

		final TIntList vars = new TIntArrayList(parents);
		vars.insert(0, or);

		factors.put(or, BayesianFactorFactory.factory()
				.domain(model.getDomain(vars.toArray()))
				.or(parents.toArray())
		);

		return or;
	}

	/**
	 * Finalize model by assigning all its factors.
	 */
	void assignFactors() {
		factors.forEach(model::setFactor);
	}

	public static Model parse(Path path) throws IOException {
		final Model model = new Model();

		try (final Workbook workbook = new XSSFWorkbook(new FileInputStream(path.toFile()))) {
			final Sheet sheetQuestions = workbook.getSheetAt(0);

			// parse first row for column types
			final Row header = sheetQuestions.getRow(0);
			for (int c = header.getFirstCellNum(); c < header.getLastCellNum(); c++) {
				final Cell cell = header.getCell(c);
				switch (cell.getStringCellValue().toUpperCase()) {
					case "QUESTION_ID" -> Q_ID = c;
					case "SUB_QUESTION_ID" -> SQ_ID = c;
					case "QUESTION_TYPE" -> Q_TYPE = c;
					case "SKILLS" -> SKILL_START = c;
				}
			}

			// parse for skills
			final Row skillName = sheetQuestions.getRow(1);
			final Row skillValue = sheetQuestions.getRow(2);

			for (int c = SKILL_START; c < header.getLastCellNum(); c++) {
				final String skill = skillName.getCell(c).getStringCellValue();
				final double value = skillValue.getCell(c).getNumericCellValue();
				model.addSkill(skill, value);
			}

			// parse for questions
			String qid = null, sqid = null;

			for (int r = 3; r < sheetQuestions.getLastRowNum(); r++) {
				final Row row = sheetQuestions.getRow(r);

				final Cell cellQID = row.getCell(Q_ID);
				final Cell cellSQID = row.getCell(SQ_ID);
				final Cell cellType = row.getCell(Q_TYPE);

				if (cellSQID == null || cellSQID.toString().isEmpty())
					continue;

				if (qid == null || !cellQID.toString().isEmpty())
					qid = "" + cellToInt(cellQID);

				if (sqid == null || !cellSQID.toString().isEmpty())
					sqid = "" + cellToInt(cellSQID);

				String QType = "AND";
				if (cellType != null)
					QType = cellType.getStringCellValue().toUpperCase();

				final TIntList parents = new TIntArrayList();

				for (int i = 0; i < model.nSkill(); i++) {
					final Cell cell = row.getCell(SKILL_START + i);
					if (cell == null)
						continue;

					final double lambda_i = 1.0 - cell.getNumericCellValue();

					// skip if there is no lambda (no connection)
					if (lambda_i <= 0)
						continue;

					if (QType.equals("AND")) {
						final int xit = model.addXiAnd(i, lambda_i);
						parents.add(xit);
					} else if (QType.equals("OR")) {
						final int xit = model.addXiOr(i, lambda_i);
						parents.add(xit);
					}
				}

				if (QType.equals("AND")) {
					model.addQuestionAnd(questionName(qid, sqid), parents);
				} else if (QType.equals("OR")) {
					model.addQuestionOr(questionName(qid, sqid), parents);
				}
			}
		}

		// Add relations between skills
		model.addConstraint("X11", "X12");
		model.addConstraint("X12", "X13");
		model.addConstraint("X21", "X22");
		model.addConstraint("X22", "X23");
		model.addConstraint("X31", "X32");
		model.addConstraint("X32", "X33");
		model.addConstraint("X11", "X21");
		model.addConstraint("X21", "X31");
		model.addConstraint("X12", "X22");
		model.addConstraint("X22", "X32");
		model.addConstraint("X13", "X23");
		model.addConstraint("X23", "X33");

		model.assignFactors();

		return model;
	}
}
