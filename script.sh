#!/bin/bash

java -jar itas-1.0.jar "data/JCOMSS/questions-skill_model1a.xlsx" "data/JCOMSS/student-answers.xlsx" "data/JCOMSS/results_model1a.xlsx"

java -jar itas-1.0.jar "data/JCOMSS/questions-skill_model2a.xlsx" "data/JCOMSS/student-answers.xlsx" "data/JCOMSS/results_model2a.xlsx"

java -jar itas-1.0.jar "data/CAT_COMMANDS/questions-skill_model1b.xlsx" "data/CAT_COMMANDS/student-answers-commands.xlsx" "data/CAT_COMMANDS/results_model1b.xlsx"

java -jar itas-1.0.jar "data/CAT_COMMANDS/questions-skill_model2b.xlsx" "data/CAT_COMMANDS/student-answers-commands.xlsx" "data/CAT_COMMANDS/results_model2b.xlsx"