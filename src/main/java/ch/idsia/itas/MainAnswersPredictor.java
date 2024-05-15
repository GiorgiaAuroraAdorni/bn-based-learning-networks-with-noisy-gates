package ch.idsia.itas;

import ch.idsia.crema.factor.bayesian.BayesianFactor;
import ch.idsia.crema.inference.InferenceJoined;
import ch.idsia.crema.inference.bp.LoopyBeliefPropagation;
import ch.idsia.crema.inference.ve.FactorVariableElimination;
import ch.idsia.crema.inference.ve.order.MinFillOrdering;
import ch.idsia.crema.model.graphical.GraphicalModel;
import gnu.trove.map.hash.TIntIntHashMap;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static ch.idsia.itas.AnswersResults.answersResults;

/**
 * Author name:    Giorgia Adorni
 * Date creation:  16.11.2023
 */
public class MainAnswersPredictor {


    // Method to process a student's answers
    private static void processStudentAnswers(Student student, Set<Integer> sts, Model model, boolean exactInference,
                                              String[] observedQuestionsArray, int[] inferenceQuestionsIdsArray,
                                              String[] inferenceQuestionsArray, boolean hasConstraint,
                                              LoopyBeliefPropagation<BayesianFactor> infLBP,
                                              InferenceJoined<GraphicalModel<BayesianFactor>, BayesianFactor> infVE) {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Schedule the task to print student ID every hour
        scheduler.scheduleAtFixedRate(() -> {
            // Assuming you have a variable holding the current student ID
            System.out.println("Current student ID: " + student.id);
        }, 0, 1, TimeUnit.MINUTES); // Run every minute

        final TIntIntHashMap obsObserved = new TIntIntHashMap();
        // add constraints variables
        if (hasConstraint)
            for (Integer constraint : model.constraints)
                obsObserved.put(constraint, 1);

        // in case of a leak variable, observe it
        if (model.hasLeak)
            obsObserved.put(model.leakVar, 1);

        student.answers.forEach((q, answer) -> {
            if (!model.questionIds.contains(q))
                // we have questions not supported by the model: we skip them
                return;

            // iterate only over the answers related to the questions observed, if it is not in the list, skip it
            if (!Arrays.asList(observedQuestionsArray).contains(q))
                return;

            // answers can be yes (1), no (0), empty (no evidence)
            if (!answer.isEmpty()) {
                final int i = model.nameToIdx.get(q);
                // When creating obs, differentiate between obsObserved and obsInference,
                // if q is in observedQuestionsArray, put it in obsObserved
                if (Arrays.asList(observedQuestionsArray).contains(q)) {
                    // differentiate between yes, no, and empty
                    if (answer.equals("yes")) {
                        obsObserved.put(i, 1);
                    }
                    if (answer.equals("no")) {
                        obsObserved.put(i, 0);
                    }
                }

            }
            // Otherwise put it in obsInference
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

                // TODO: cross entropy
                student.resultsAnswersPerQuestion.put(q, ans);
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
            student.resultsAnswers.put(inferenceQuestionsArray[i], query.get(i));

        final double[] outs = query.stream().map(x -> x.getValue(1)).mapToDouble(x -> x).toArray();
        System.out.printf("%3d: %s%n", student.id, Arrays.toString(outs));
    }

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
        List<Student> students = Student.parse(studentAnswersXLSX);

        System.out.println("Found " + students.size() + " students");

        if (!sts.isEmpty())
            System.out.println("Limited to ids:     " + sts);

        // available questions
        final Set<String> questions = model.questionIds;

        // Define the file path for saving combinations
        Path filePath = Paths.get("data/results/virtual/combinations.csv");

        // Define the number of blocks
        int numBlocks = 12;
        // Define the number of observed blocks
        int numObservedBlocks = 8;
        // Define the number of inference blocks
        int numInferenceBlocks = 4;

        // Create a list to store combinations
        List<String> combinations = new ArrayList<>();

        // Check if the file already contains combinations
        if (Files.exists(filePath)) {
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    combinations.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Generate new combinations for each student
            for (Student student : students) {
                StringBuilder combination = new StringBuilder();
                Set<Integer> observedBlocks = new HashSet<>();
                Set<Integer> inferenceBlocks = new HashSet<>();

                // Randomly select observed blocks
                while (observedBlocks.size() < numObservedBlocks) {
                    int block = (int) (Math.random() * numBlocks) + 1;
                    observedBlocks.add(block);
                }

                // Randomly select inference blocks
                while (inferenceBlocks.size() < numInferenceBlocks) {
                    int block = (int) (Math.random() * numBlocks) + 1;
                    // Check if the block is not already selected as observed
                    if (!observedBlocks.contains(block)) {
                        inferenceBlocks.add(block);
                    }
                }

                // Append observed questions to the combination string
                for (int block : observedBlocks) {
                    combination.append(block).append(",");
                }

                // Append inference questions to the combination string
                for (int block : inferenceBlocks) {
                    combination.append(block);
                    if (inferenceBlocks.size() > 1) {
                        combination.append(",");
                    }
                }

                combinations.add(student.id + "," + combination.toString());
            }

            // Save combinations to file
            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                // Write header row
                writer.write("Student,Observed1,Observed2,Observed3,Observed4,Observed5,Observed6,Observed7,Observed8,Inference1,Inference2,Inference3,Inference4");
                writer.newLine();

                // Write student combinations
                for (String combination : combinations) {
                    writer.write(combination);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Parse the combinations
        List<String> observedQuestions = new ArrayList<>();
        List<String> inferenceQuestions = new ArrayList<>();
        List<Integer> observedQuestionsIds = new ArrayList<>();
        List<Integer> inferenceQuestionsIds = new ArrayList<>();

        // Iterate over combinations, skipping the header
        for (int i = 1; i < combinations.size(); i++) {
            String combination = combinations.get(i);
            String[] parts = combination.split(",");
            // The observed blocks are the first 8 parts
            int[] observedBlocks = Arrays.stream(parts).limit(numObservedBlocks).mapToInt(Integer::parseInt).toArray();
            // The inference blocks are the last 4 parts
            int[] inferenceBlocks = Arrays.stream(parts).skip(numObservedBlocks).mapToInt(Integer::parseInt).toArray();

            // Iterate over all questions
            for (String question : questions) {
                // Split the question value based on the "_" character
                String[] questionParts = question.split("_");
                // Extract the block number (1-12)
                int block = Integer.parseInt(questionParts[0]);
                // Extract the question number (1-26)
                int questionNumber = Integer.parseInt(questionParts[1]);

                // Check if the block is in observedBlocks
                if (Arrays.stream(observedBlocks).anyMatch(b -> b == block)) {
                    // Add the question to observedQuestions
                    observedQuestions.add(question);
                    // Get the index of the question
                    int questionIndex = model.nameToIdx.get(question);
                    // Add the question index to observedQuestionsIds
                    observedQuestionsIds.add(questionIndex);
                } else if (Arrays.stream(inferenceBlocks).anyMatch(b -> b == block)) {
                    // Add the question to inferenceQuestions
                    inferenceQuestions.add(question);
                    // Get the index of the question
                    int questionIndex = model.nameToIdx.get(question);
                    // Add the question index to inferenceQuestionsIds
                    inferenceQuestionsIds.add(questionIndex);
                }
            }
        }

        // Convert the lists to arrays
        String[] observedQuestionsArray = observedQuestions.toArray(new String[0]);
        String[] inferenceQuestionsArray = inferenceQuestions.toArray(new String[0]);
        int[] inferenceQuestionsIdsArray = inferenceQuestionsIds.stream().mapToInt(i -> i).toArray();

        // inference engine
        final LoopyBeliefPropagation<BayesianFactor> infLBP;
        final InferenceJoined<GraphicalModel<BayesianFactor>, BayesianFactor> infVE;

        if (exactInference) {
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

        // Define the number of threads for parallel execution
        int numThreads = Runtime.getRuntime().availableProcessors(); // Use available processors

        // Create a fixed-size thread pool
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // FIXME: For test purposes, run just for 10 students
        // students = students.subList(0, 10);  // TODO: comment and uncomment this line

        for (Student student : students) {
            if (!sts.isEmpty() && !sts.contains(student.id))
                continue;

            executor.submit(() -> processStudentAnswers(student, sts, model, exactInference, observedQuestionsArray, inferenceQuestionsIdsArray, inferenceQuestionsArray, hasConstraint, infLBP, infVE));
        }

        // Shutdown the executor to indicate that no more tasks will be submitted
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.err.println("Error waiting for executor to terminate: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        if (studentCount == 0)
            studentCount = 1;

        // TODO: nuovo file con probabilità di yes/no per ogni skill piu quella vera (threshold per considerarla vera?)

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

        answersResults(model, students, resultsXLSX, studentAnswersXLSX);

        System.out.println("Results saved to file " + resultsXLSX);
    }
}
