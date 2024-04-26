package ch.idsia.itas;

import ch.idsia.crema.factor.bayesian.BayesianFactor;
import ch.idsia.crema.inference.InferenceJoined;
import ch.idsia.crema.inference.bp.LoopyBeliefPropagation;
import ch.idsia.crema.inference.ve.FactorVariableElimination;
import ch.idsia.crema.inference.ve.order.MinFillOrdering;
import ch.idsia.crema.model.graphical.GraphicalModel;
import gnu.trove.map.hash.TIntIntHashMap;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static ch.idsia.itas.Results.results;

/**
 * Author name:    Giorgia Adorni
 * Date creation:  16.11.2023
 */
public class Main {


	public static void main(String[] args) throws IOException {

		// input arguments
		final String filenameQuestions = args[0]; // "questions-skill.xlsx"
		final String filenameAnswers = args[1]; // "student-answers.xlsx"
		final String filenameResults = args[2]; // "results.xlsx"
		final String strConstraint = args[3]; // "constrained/unconstrained"
		final String strInference = args[4]; // "exact/approximate"

		final boolean hasConstraint;
		final boolean exactInference;

        hasConstraint = strConstraint.equals("constrained");

        exactInference = strInference.equals("exact");

		final Set<Integer> sts = new HashSet<>(); // 1, 11, 12
		if (args.length > 5)
			for (int i = 5; i < args.length; i++)
				sts.add(Integer.parseInt(args[i]));

		System.out.println("Questions filename: " + filenameQuestions);
		System.out.println("Answers filename:   " + filenameAnswers);
		System.out.println("Results filename:   " + filenameResults);
		System.out.println("Constraint: " + hasConstraint);
		System.out.println("Exact inference: " + exactInference);

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

		// available schemas: fixed number of 12
		// final int nSchemas = 12;

		// available skills
//		final int[] skills = model.skillIds();

		// available questions
		final Set<String> questions = model.questionIds;
		// final int[] questionsIds = model.questionIds.stream().mapToInt(model.nameToIdx::get).toArray();

		// Define two lists to hold the desired values
		List<String> observedQuestions = new ArrayList<>();
		List<String> inferenceQuestions = new ArrayList<>();
		List<Integer> observedQuestionsIds = new ArrayList<>();
		List<Integer> inferenceQuestionsIds = new ArrayList<>();

		// Iterate through the set of questions
		for (String question : questions) {
			// Split the question value based on the "_" character
			String[] parts = question.split("_");
			// Extract the block number (1-12)
			int block = Integer.parseInt(parts[0]);
			// Extract the question number (1-26)
			// int questionNumber = Integer.parseInt(parts[1]);

			// Check if the question belongs to the first 8 blocks
			if (block >= 1 && block <= 8) {
				observedQuestions.add(question);
				// Get the index of the question
				int questionIndex = model.nameToIdx.get(question);
				observedQuestionsIds.add(questionIndex);
			} else {
				inferenceQuestions.add(question);
				// Get the index of the question
				int questionIndex = model.nameToIdx.get(question);
				inferenceQuestionsIds.add(questionIndex);
			}
		}

		// Convert the lists to arrays
		String[] observedQuestionsArray = observedQuestions.toArray(new String[0]);
		String[] inferenceQuestionsArray = inferenceQuestions.toArray(new String[0]);
		int[] inferenceQuestionsIdsArray = inferenceQuestionsIds.stream().mapToInt(i -> i).toArray();

		// TODO: modify the observedQuestionsArray and inferenceQuestionsArray so that there are random permutations of the schemas

		// inference engine
		final LoopyBeliefPropagation<BayesianFactor> infLBP;
		final InferenceJoined<GraphicalModel<BayesianFactor>, BayesianFactor> infVE;

		if (exactInference){
			final MinFillOrdering mfo = new MinFillOrdering();
			int[] sequence = mfo.apply(model.model);
			infVE = new FactorVariableElimination<>(sequence);
			infLBP = null;
		} else {
			infVE = null;
			infLBP = new LoopyBeliefPropagation<>(51);
		}

		final long startTime = System.currentTimeMillis();

		// sequential students analysis
		int studentCount = 0;
		for (Student student : students) {
			if (!sts.isEmpty() && !sts.contains(student.id))
				continue;

			studentCount ++;

			final TIntIntHashMap obsObserved = new TIntIntHashMap();
			// add constraints variables
			if (hasConstraint)
				for (Integer constraint : model.constraints)
					obsObserved.put(constraint, 1);

			// in case of a leak variable, observe it
			if (model.hasLeak)
				obsObserved.put(model.leakVar, 1);

			final TIntIntHashMap obsInference = new TIntIntHashMap();
			// TODO: add constraints variables (?)

//			List<BayesianFactor> test_qs = Arrays.stream(inferenceQuestionsIdsArray).mapToObj(iq -> infVE.query(model.model, obsObserved, iq)).toList();
//			final double[] test_outs = test_qs.stream().map(x -> x.getValue(1)).mapToDouble(x -> x).toArray();
//			System.out.printf("%3d: %s%n", student.id, Arrays.toString(test_outs));

			student.answers.forEach((q, answer) -> {
				if (!model.questionIds.contains(q))
					// we have questions not supported by the model: we skip them
					return;

				// answers can be yes (1), no (0), empty (no evidence)
				if (!answer.isEmpty()) {
					final int i = model.nameToIdx.get(q);
					// When creating obs, differentiate between obsObserved and obsInference, comparing q with observedQuestionsArray and inferenceQuestionsArray
					if (Arrays.asList(observedQuestionsArray).contains(q)) {
						if (answer.equals("yes"))
							obsObserved.put(i, 1);
						if (answer.equals("no"))
							obsObserved.put(i, 0);
						// FIXME: not sure if we need this, probably not
					} else if (Arrays.asList(inferenceQuestionsArray).contains(q)) {
						if (answer.equals("yes"))
							obsInference.put(i, 1);
						if (answer.equals("no"))
							obsInference.put(i, 0);
					}
				}

				if (!sts.isEmpty()) {
					final Map<String, BayesianFactor> ans = new LinkedHashMap<>();
					List<BayesianFactor> qs;

					if (exactInference) {
						qs = Arrays.stream(inferenceQuestionsIdsArray).mapToObj(iq -> infVE.query(model.model, obsObserved, iq)).toList();
					} else {
						qs = infLBP.query(model.model, obsObserved, inferenceQuestionsIdsArray);
					}

					for (int i = 0; i < qs.size(); i++) {
						final String iq = inferenceQuestionsArray[i];
						final BayesianFactor res = qs.get(i);
						ans.put(iq, res);
					}

					// cross entropy
					student.resultsPerQuestion.put(q, ans);
					System.out.printf("%3d: %s, %s%n", student.id, q, ans);
				}
			});

			List<BayesianFactor> query;

			if (exactInference) {
				query = Arrays.stream(inferenceQuestionsIdsArray).mapToObj(iq -> infVE.query(model.model, obsObserved, iq)).toList();
			} else {
				query = infLBP.query(model.model, obsObserved, inferenceQuestionsIdsArray);
			}

			for (int i = 0; i < query.size(); i++)
				student.results.put(inferenceQuestionsArray[i], query.get(i));

			final double[] outs = query.stream().map(x -> x.getValue(1)).mapToDouble(x -> x).toArray();
			System.out.printf("%3d: %s%n", student.id, Arrays.toString(outs));
		}

		if (studentCount == 0)
			studentCount = 1;

		// TODO: nuovo file con probabilita di yes/no per ogni skill piu quella vera (threshold per considerarla vera?)

		final long endTime = System.currentTimeMillis();
		final double timeSpan = (endTime - startTime) / 1000.0;
		final double avgTime = timeSpan / studentCount;

		String text = ", " + timeSpan + "\n";
		String filename = "data/model_statistics_virtual.txt";

		Writer output;
		output = new BufferedWriter(new FileWriter(filename, true));
		output.append(filenameResults.replace(".xlsx", "").replace("data/results/virtual/results_questions-skill-", "")).append(text);
		output.close();

 		System.out.printf("Completed in %.3f seconds (average: %.3f seconds)%n", timeSpan, avgTime);

		results(model, students, resultsXLSX, questionsSkillsXLSX);

		System.out.println("Results saved to file " + resultsXLSX);
	}

}
