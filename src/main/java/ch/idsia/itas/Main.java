package ch.idsia.itas;

import ch.idsia.crema.factor.bayesian.BayesianFactor;
import ch.idsia.crema.inference.bp.LoopyBeliefPropagation;
import gnu.trove.map.hash.TIntIntHashMap;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static ch.idsia.itas.Results.results;

/**
 * Author:  Claudio "Dna" Bonesana
 * Project: 2022-flairs
 * Date:    09.02.2022 10:06
 */
public class Main {

	public static void main(String[] args) throws IOException {

		// input arguments
		final String filenameQuestions = args[0]; // "questions-skill.xlsx"
		final String filenameAnswers = args[1]; // "student-answers.xlsx"
		final String filenameResults = args[2]; // "results.xlsx"

		final Set<Integer> sts = new HashSet<>(); // 1, 11, 12
		if (args.length > 3)
			for (int i = 3; i < args.length; i++)
				sts.add(Integer.parseInt(args[i]));

		System.out.println("Questions filename: " + filenameQuestions);
		System.out.println("Answers filename:   " + filenameAnswers);
		System.out.println("Results filename:   " + filenameResults);

		final Path questionsSkillsXLSX = Paths.get(filenameQuestions);
		final Path studentAnswersXLSX = Paths.get(filenameAnswers);
		final Path resultsXLSX = Paths.get(filenameResults);

		// model parsing
		final Model model = Model.parse(questionsSkillsXLSX);

		System.out.println("Found model with " + model.model.getVariables().length + " variables");
		System.out.println("- skills:    " + model.nSkill());
		System.out.println("- questions: " + model.questionIds.size());
		System.out.println("- leak node: " + model.hasLeak);

		// students parsing
		final List<Student> students = Student.parse(studentAnswersXLSX);

		System.out.println("Found " + students.size() + " students");

		if (!sts.isEmpty())
			System.out.println("Limited to ids:     " + sts);

		// available skills
		final int[] skills = model.skillIds();

		// inference engine
		final LoopyBeliefPropagation<BayesianFactor> inf = new LoopyBeliefPropagation<>();

		final long startTime = System.currentTimeMillis();

		// sequential students analysis
		int studentCount = 0;
		for (Student student : students) {
			if (!sts.isEmpty() && !sts.contains(student.id))
				continue;

			studentCount ++;

			final TIntIntHashMap obs = new TIntIntHashMap();
			// add constraints variables
			for (Integer constraint : model.constraints)
				obs.put(constraint, 1);

			student.answers.forEach((q, answer) -> {
				if (!model.questionIds.contains(q))
					// we have questions not supported by the model: we skip them
					return;

				// answers can be yes (1), no (0), empty (no evidence)
				if (!answer.isEmpty()) {
					final int i = model.nameToIdx.get(q);
					if (answer.equals("yes"))
						obs.put(i, 1);
					if (answer.equals("no"))
						obs.put(i, 0);
				}

				if (!sts.isEmpty()) {
					final List<BayesianFactor> qs = inf.query(model.model, obs, skills);
					final Map<Model.Skill, BayesianFactor> ans = new LinkedHashMap<>();
					for (int i = 0; i < qs.size(); i++) {
						final Model.Skill skl = model.skills.get(i);
						final BayesianFactor res = qs.get(i);
						ans.put(skl, res);
					}
					student.resultsPerQuestion.put(q, ans);
				}
			});

			// in case of a leak variable, observe it
			if (model.hasLeak)
				obs.put(model.leakVar, 1);

			final List<BayesianFactor> query = inf.query(model.model, obs, skills);

			for (int i = 0; i < query.size(); i++)
				student.results.put(model.skills.get(i), query.get(i));

			final double[] outs = query.stream().map(x -> x.getValue(1)).mapToDouble(x -> x).toArray();

			System.out.printf("%3d: %s%n", student.id, Arrays.toString(outs));
		}

		if (studentCount == 0)
			studentCount = 1;

		final long endTime = System.currentTimeMillis();
		final double timeSpan = (endTime - startTime) / 1000.0;
		final double avgTime = timeSpan / studentCount;

		System.out.printf("Completed in %.3f seconds (average: %.3f seconds)%n", timeSpan, avgTime);

		results(model, students, resultsXLSX, questionsSkillsXLSX);

		System.out.println("Results saved to file " + resultsXLSX);
	}

}