# Intelligent Tutoring Assessment System for the CAT

### Citation
[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.10958613.svg)](https://doi.org/10.5281/zenodo.10958613)

If you use the materials provided in this repository, please cite the following work:

```bibtex
   @misc{Adorni_BN-based_Learning_Networks_2024,
     author = {Adorni, Giorgia and Bonesana, Claudio},
     doi = {10.5281/zenodo.10958613},
     month = apr,
     title = {{BN-based Learning Networks with Noisy Gates}},
     note = {Zenodo Software. \url{https://doi.org/10.5281/zenodo.10958613}},
     version = {1.0.0},
     year = {2024}
   }
```

## Cloning the Repository

To get started, clone the repository to your local machine by running the following command in your terminal:

```bash
git clone https://github.com/GiorgiaAuroraAdorni/bn-based-learning-networks-with-noisy-gates.git
```

## Installation Instructions

### Maven

Ensure you have Maven installed on your system. You can install Maven by running the following command in your terminal:

```bash
sudo apt update
sudo apt install maven
```

After installation, you can verify that Maven is installed by running:

```bash
mvn -version
```
### Java

Ensure you have Java 11 or later installed on your system. You can install OpenJDK 11 by running:

```bash
sudo apt update
sudo apt install openjdk-18-jdk
```

After installation, you can verify that Java is installed by running:

```bash
java -version
```

### Tested Environment

The code in this repository has been tested with the following versions:

- Java: 18
- Maven: 3.6.3
  
## Method 1: Using Command Line

Run the following command in your terminal:

```
java -jar target/itas-1.0.jar "questions-skill.xlsx" "students-answers.xlsx" "results.xlsx" "constrained" "exact"
```

In the command above, replace "questions-skill.xlsx", "students-answers.xlsx", and "results.xlsx" with the paths to the appropriate XLSX files containing your questions, students' answers, and the file where you want to save the results, respectively.

Note: It's recommended to use one of the provided XLSX files or your own files following the same format.

The last two parameters, "constrained" and "exact", determine the mode of operation of the Intelligent Tutoring Assessment System (ITAS):

- Constrained Mode: In this mode, the assessment process is constrained, meaning that it strictly follows the rules and criteria defined for evaluating the students' answers. This mode typically provides more precise but potentially stricter evaluation results.
- Exact Mode: In this mode, the assessment process aims for exact inference, meaning that it tries to precisely determine the correctness of each answer based on the defined criteria. This mode may require more computational resources but can provide more accurate assessment results.

Change the content of the XLSX files to test for other models, questions, and answers.

## Method 2: Using script.sh

Alternatively, you can use one of the provided `.sh` files to execute the experiments using Maven. Open your terminal and navigate to the root directory of the project, then run the following commands:

```
chmod +x script.sh
./script.sh
```

Ensure you have Maven installed and it's available in your system's PATH.  
This method automates the process and includes predefined models and answers. 
You can edit the script if you want to modify the models or answers file accordingly.

## Available Models and Answers
The "data" directory contains the following structure of files and folders:

- **answers**: This directory contains Excel files with student answers. Navigate to the appropriate subdirectory (`unplugged` or `virtual`) to find the files. You can modify these files with your preferred spreadsheet editor.
# Intelligent Tutoring Assessment System for the CAT

## Cloning the Repository

To get started, clone the repository to your local machine by running the following command in your terminal:

```bash
git clone https://github.com/GiorgiaAuroraAdorni/bn-based-learning-networks-with-noisy-gates.git
```

## Installation Instructions

### Maven

Ensure you have Maven installed on your system. You can install Maven by running the following command in your terminal:

```bash
sudo apt update
sudo apt install maven
```

After installation, you can verify that Maven is installed by running:

```bash
mvn -version
```
### Java

Ensure you have Java 11 or later installed on your system. You can install OpenJDK 11 by running:

```bash
sudo apt update
sudo apt install openjdk-11-jdk
```

After installation, you can verify that Java is installed by running:

```bash
java -version
```

## Method 1: Using Command Line

Before running the application, ensure you have generated the JAR file by following these steps:

1. Navigate to the project's root directory in your terminal.
2. Run the following Maven command to generate the JAR file:
```bash
mvn package
```
This command will compile the project and generate the JAR file in the `target` directory.
Once you've generated the JAR file, you can proceed to run the application using the following command:
```
java -jar target/itas-1.0.jar "questions-skill.xlsx" "students-answers.xlsx" "results.xlsx" "constrained" "exact"
```

In the command above, replace "questions-skill.xlsx", "students-answers.xlsx", and "results.xlsx" with the paths to the appropriate XLSX files containing your questions, students' answers, and the file where you want to save the results, respectively.

Note: It's recommended to use one of the provided XLSX files or your own files following the same format.

The last two parameters, "constrained" and "exact", determine the mode of operation of the Intelligent Tutoring Assessment System (ITAS):

- Constrained Mode: In this mode, the assessment process is constrained, meaning that it strictly follows the rules and criteria defined for evaluating the students' answers. This mode typically provides more precise but potentially stricter evaluation results.
- Exact Mode: In this mode, the assessment process aims for exact inference, meaning that it tries to precisely determine the correctness of each answer based on the defined criteria. This mode may require more computational resources but can provide more accurate assessment results.

Change the content of the XLSX files to test for other models, questions, and answers.

## Method 2: Using script.sh

Alternatively, you can use one of the provided `.sh` files to execute the experiments using Maven. Open your terminal and navigate to the root directory of the project, then run the following commands:

```
chmod +x script.sh
./script.sh
```

Ensure you have Maven installed and it's available in your system's PATH.  
This method automates the process and includes predefined models and answers.
You can edit the script if you want to modify the models or answers file accordingly.

## Available Models and Answers
The "data" directory contains the following structure of files and folders:

- **answers**: This directory contains Excel files with student answers. Navigate to the appropriate subdirectory (`unplugged` or `virtual`) to find the files. You can modify these files with your preferred spreadsheet editor.

- **models**: Inside this directory, you'll find Excel files representing the models used in the experiments. Navigate to the appropriate subdirectory (`unplugged` or `virtual`) to find the files. You can modify these files with your preferred spreadsheet editor.

- **results**: This directory contains Excel files with the output results of the experiments. Navigate to the appropriate subdirectory (`unplugged` or `virtual`) to find the files. You can modify these files with your preferred spreadsheet editor.

Remember to navigate to the correct subdirectory (`unplugged` or `virtual`) depending on whether you're working with unplugged or virtual data.

## Experiment Results
The experiment results will be saved in the `data/results/` directory with filenames matching the respective models.

- **models**: Inside this directory, you'll find Excel files representing the models used in the experiments. Navigate to the appropriate subdirectory (`unplugged` or `virtual`) to find the files. You can modify these files with your preferred spreadsheet editor.

- **results**: This directory contains Excel files with the output results of the experiments. Navigate to the appropriate subdirectory (`unplugged` or `virtual`) to find the files. You can modify these files with your preferred spreadsheet editor.

Remember to navigate to the correct subdirectory (`unplugged` or `virtual`) depending on whether you're working with unplugged or virtual data.

## Experiment Results
The experiment results will be saved in the `data/results/` directory with filenames matching the respective models.
