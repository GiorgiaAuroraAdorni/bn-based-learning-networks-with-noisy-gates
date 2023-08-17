# Intelligent Tutoring Assessment System for CAT

This software requires Java 11 or later. Download the latest release and follow one of the two methods below to execute the experiments.

## Method 1: Using Command Line

Run the following command in your terminal:

```
java -jar 2022-flairs-*.jar "questions-skill.xlsx" "students-answers.xlsx" "results.xlsx"
```

Change the content of the XLSX files to test for other models, questions, and answers.

## Method 2: Using script.sh

Alternatively, you can use the provided `script.sh` to execute the experiments using Maven. Open your terminal and navigate to the root directory of the project, then run the following commands:

```
chmod +x script.sh
./script.sh
```

Ensure you have Maven installed and it's available in your system's PATH.  
This method automates the process and includes predefined models and answers. 
You can edit the script if you want to modify the models or answers file accordingly.

## Available Models and Answers
The `script.sh` file includes the following predefined models and answers:

- Model1a
- Model1b
- Model2b
- Model3b
- Model4b

You can find these models in the `data/models/` directory, and the corresponding answers in the `data/answers/` directory.

## Experiment Results
The experiment results will be saved in the `data/results/` directory with filenames matching the respective models.

Note: Before running the experiments, please make sure that you have Java 11 or later installed, and if using Method 2, that Maven is properly set up on your system.

Feel free to reach out if you have any questions or need further assistance.
