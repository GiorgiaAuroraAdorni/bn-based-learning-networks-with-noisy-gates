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

	record Skill(int column, String name, String desc, int index) {
		@Override
		public String toString() {
			return name;
		}
	}

	record Connection(Skill skill, double lambda) {
	}

	final static Set<String> OR_LEFT = new HashSet<>();
	final static Set<String> OR_RIGHT = new HashSet<>();

	final static Set<String> CT_CUBE = Set.of("X11", "X12", "X13", "X21", "X22", "X23", "X31", "X32", "X33");

	static {
		for (int i = 1; i <= 14; i++)
			OR_LEFT.add("E" + i);
		for (int i = 15; i <= 30; i++)
			OR_RIGHT.add("E" + i);
	}

	static int Q_ID = 0;
	static int SQ_ID = 1;
	static int Q_TEXT = 3;
	static int SKILL_START = 4;

	static int QUESTION_ROW_START = 4;

	final DAGModel<BayesianFactor> model = new DAGModel<>();

	final Map<String, Integer> nameToIdx = new LinkedHashMap<>();
	final Map<Integer, String> idxToName = new LinkedHashMap<>();

	final List<Skill> skills = new ArrayList<>();
	final Set<String> questionIds = new HashSet<>();

	final Map<Integer, BayesianFactor> factors = new LinkedHashMap<>();

	final List<Integer> constraints = new ArrayList<>();

	boolean hasLeak = false;
	int leakVar = -1;

	int nSkill() {
		return skills.size();
	}

	int[] skillIds() {
		return skills.stream()
				.mapToInt(Skill::index)
				.toArray();
	}

	private Skill find(String name) {
		for (Skill skill : skills) {
			if (skill.name.equals(name))
				return skill;
		}
		throw new IllegalStateException("Skill " + name + " does not exist!");
	}

	/**
	 * @param name Name of the new skill
	 * @return the created skill
	 */
	Skill addSkill(int c, String name, String desc, double p1) {
		final int v = model.addVariable(2);
		final Skill s = new Skill(c, name, desc, v);

		idxToName.put(v, name);
		nameToIdx.put(name, v);
		skills.add(s);

		factors.put(v, BayesianFactorFactory.factory()
				.domain(model.getDomain(v))
				.data(new double[]{1.0 - p1, p1})
				.get()
		);

		if (name.equalsIgnoreCase("leak")) {
			hasLeak = true;
			leakVar = v;
		}

		return s;
	}

	/**
	 * Add a constraint node where the logic relation corresponds to (~L1)OR(L2).
	 *
	 * @param l1 skill L1
	 * @param l2 skill L2
	 */
	void addConstraint(String l2, String l1) {
		// left < right: (NOT left) OR (right)
		int L1 = find(l1).index;
		int L2 = find(l2).index;

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
		final Skill skill = skills.get(i);
		final int xi = skill.index;

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
	 * @param skill    skill associated with this inhibitor node
	 * @param lambda_i lambda_i value to use
	 * @return the index of the new variable
	 */
	int addXiOr(Skill skill, double lambda_i) {
		final int xi = skill.index;

		// add Xi' nodes for each parents...
		final int xit = model.addVariable(2);
		model.addParent(xit, xi);

		/// ... and assign lambda
		factors.put(xit, BayesianFactorFactory.factory()
				.domain(model.getDomain(xit, xi))
				.set(1.0 - lambda_i, 1, 1) // P(Xi'=1|Xi=1) = 1 - lambda_i
				.set(lambda_i, 0, 1) // P(Xi'=0|Xi=1) = lambda_i
				.set(0.0, 1, 0) // P(Xi'=1|Xi=0) = 0
				.set(1.0, 0, 0) // P(Xi'=0|Xi=0) = 1
				.get());

		return xit;
	}

	int addOrNode(TIntList parents) {
		final int or = model.addVariable(2);
		model.addParents(or, parents.toArray());
		final TIntList vars = new TIntArrayList(parents);
		vars.insert(0, or);

		factors.put(or, BayesianFactorFactory.factory()
				.domain(model.getDomain(vars.toArray()))
				.or(parents.toArray())
		);

		return or;
	}

	int addAndNode(TIntList parents) {
		final int and = model.addVariable(2);
		model.addParents(and, parents.toArray());
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
	 */
	int addQuestion(String nodeName, List<Connection> parents) {
		if (parents.isEmpty())
			return -1;

		questionIds.add(nodeName);
		int q;

		if (parents.size() == 1) {
			// in case we have only one parents
			final Connection c = parents.get(0);
			q = addXiOr(c.skill, c.lambda);
			nameToIdx.put(nodeName, q);
			idxToName.put(q, nodeName);
			return q;
		}

		// (E1 OR ... OR E14) AND (E15 OR ... OR E27 OR E28 OR E29 OR E30)

		final TIntList parentsLeft = new TIntArrayList();
		final TIntList parentsRight = new TIntArrayList();
		final TIntList parentsCT = new TIntArrayList();

		for (Connection p : parents) {
			int xi = addXiOr(p.skill, p.lambda);
			nameToIdx.put(p.skill + "_i", xi);
			idxToName.put(xi, p.skill + "_i");

			if (OR_LEFT.contains(p.skill.name))
				parentsLeft.add(xi);
			if (OR_RIGHT.contains(p.skill.name))
				parentsRight.add(xi);
			if (CT_CUBE.contains(p.skill.name))
				parentsCT.add(xi);
		}

		final TIntList list = new TIntArrayList();

		// create and add OR node with X' parents for LEFT side of expression
		if (!parentsLeft.isEmpty()) {
			final int or = addOrNode(parentsLeft);
			final String orName = nodeName + "_or1";
			list.add(or);
			nameToIdx.put(orName, or);
			idxToName.put(or, orName);
		}
		// create and add OR node with X' parents for RIGHT side of expression
		if (!parentsRight.isEmpty()) {
			final int or = addOrNode(parentsRight);
			final String orName = nodeName + "_or2";
			list.add(or);
			nameToIdx.put(orName, or);
			idxToName.put(or, orName);
		}
		// create and add OR node with X' parents for CT side of expression
		if (!parentsCT.isEmpty()) {
			final int or = addOrNode(parentsCT);
			final String orName = nodeName + "_or3";
			list.add(or);
			nameToIdx.put(orName, or);
			idxToName.put(or, orName);
		}

		if (list.size() == 1) {
			q = list.get(0);
		} else {
			q = addAndNode(list);
		}

		nameToIdx.put(nodeName, q);
		idxToName.put(q, nodeName);

		return q;
	}

	public void print(String question, int depth) {
		var q = nameToIdx.get(question);
		for (int i = 0; i < depth; i++)
			System.out.print("- ");

		System.out.println(q + " " + question + " " + factors.get(q));
		for (int p : model.getParents(q)) {
			var n = idxToName.get(p);
			print(n, depth + 1);
		}
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
					case "QUESTION_TEXT" -> Q_TEXT = c;
					case "SKILLS" -> SKILL_START = c;
				}
			}

			// parse for skills
			final Row skillDesc = sheetQuestions.getRow(2);
			final Row skillName = sheetQuestions.getRow(3);

			for (int c = SKILL_START; c < header.getLastCellNum(); c++) {
				final String desc = skillDesc.getCell(c).getStringCellValue();
				final String skill = skillName.getCell(c).getStringCellValue();
				final double value = 0.5;
				model.addSkill(c, skill, desc, value);
			}

			// parse for questions
			String qid = null, sqid = null;

			for (int r = QUESTION_ROW_START; r < sheetQuestions.getLastRowNum(); r++) {
				final Row row = sheetQuestions.getRow(r);

				final Cell cellQID = row.getCell(Q_ID);
				final Cell cellSQID = row.getCell(SQ_ID);

				if (cellSQID == null || cellSQID.toString().isEmpty())
					continue;

				if (qid == null || !cellQID.toString().isEmpty())
					qid = "" + cellToInt(cellQID);

				if (sqid == null || !cellSQID.toString().isEmpty())
					sqid = "" + cellToInt(cellSQID);

				final List<Connection> parents = new ArrayList<>();

				for (int i = 0; i < model.nSkill(); i++) {
					final Cell cell = row.getCell(SKILL_START + i);
					if (cell == null)
						continue;

					final double value = cell.getNumericCellValue();

					// skip if there is no lambda (no connection)
					if (value <= 0)
						continue;

					final double lambda_i = 1.0 - value;

					parents.add(new Connection(model.skills.get(i), lambda_i));
				}

				model.addQuestion(questionName(qid, sqid), parents);
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
