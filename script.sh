#!/bin/bash

mvn clean compile assembly:single

# Basic model
Model1="data/models/questions-skill-model1a_leak.xlsx"

# Without constraints
# 1-> questions-skill-model1a_leak		results_questions-skill-model1a_leak_unconstrained		student-answers_1a_unconstrained
Results1="data/results/results_questions-skill-model1a_leak.xlsx"
Answers1="data/answers/student-answers_1a.xlsx"
java -Xmx16g -jar target/itas-1.0.jar $Model1 $Answers1 $Results1 "unconstrained" "exact"

# With constraints
# 2 -> questions-skill-model1a_leak		results_questions-skill-model1a_leak_constrained 		student-answers_1a_constrained
Results2="data/results/results_questions-skill-model1a_leak_constrained.xlsx"
Answers2="data/answers/student-answers_1a_constrained.xlsx"
java -Xmx16g -jar target/itas-1.0.jar $Model1 $Answers2 $Results2 "constrained" "exact"

## 2b -> questions-skill-model1a_leak		results_questions-skill-model1a_leak_constrained_new 		student-answers_1a_constrained_new
#Results2b="data/results/results_questions-skill-model1a_leak_constrained_new.xlsx"
#Answers2b="data/answers/student-answers_1a_constrained_new.xlsx"
#java -Xmx16g -jar target/itas-1.0.jar $Model1 $Answers2b $Results2b "constrained" "exact"

# Basic model with constraints and supplementary skills
Answers3="data/answers/student-answers_b_constrained.xlsx"
#Answers3b="data/answers/student-answers_b_constrained_new.xlsx"

Model3="data/models/questions-skill-model1b_leak.xlsx"

# 3 -> questions-skill-model1b_leak		results_questions-skill-model1b_leak_constrained		student-answers_b_constrained
Results3="data/results/results_questions-skill-model1b_leak_constrained.xlsx"

java -Xmx16g -jar target/itas-1.0.jar $Model3 $Answers3 $Results3 "constrained" "exact"

## 3b -> questions-skill-model1b_leak		results_questions-skill-model1b_leak_constrained_new		student-answers_b_constrained_new
#Results3b="data/results/results_questions-skill-model1b_leak_constrained_new.xlsx"
#java -Xmx16g -jar target/itas-1.0.jar $Model3 $Answers3b $Results3b "constrained" "exact"

# More elaborated model with constraints and supplementary skills
# 4 -> questions-skill-model2b_leak		results_questions-skill-model2b_leak_constrained		student-answers_b_constrained
Model4="data/models/questions-skill-model2b_leak.xlsx"
Results4="data/results/results_questions-skill-model2b_leak_constrained.xlsx"
java -Xmx16g -jar target/itas-1.0.jar $Model4 $Answers3 $Results4 "constrained" "exact"

## 4b -> questions-skill-model2b_leak		results_questions-skill-model2b_leak_constrained_new		student-answers_b_constrained_nww
#Results4b="data/results/results_questions-skill-model2b_leak_constrained_new.xlsx"
#java -Xmx16g -jar target/itas-1.0.jar $Model4 $Answers3b $Results4b "constrained" "exact"

# Complex model with constraints and supplementary skills
# 5 -> questions-skill-model3b_leak		results_questions-skill-model3b_leak_constrained		student-answers_b_constrained
Model5="data/models/questions-skill-model3b_leak.xlsx"
Results5="data/results/results_questions-skill-model3b_leak_constrained.xlsx"
java -Xmx16g -jar target/itas-1.0.jar $Model5 $Answers3 $Results5 "constrained" "exact"

## 5b -> questions-skill-model3b_leak		results_questions-skill-model3b_leak_constrained_new		student-answers_b_constrained_new
#Results5b="data/results/results_questions-skill-model3b_leak_constrained_new.xlsx"
#java -Xmx16g -jar target/itas-1.0.jar $Model5 $Answers3b $Results5b "constrained" "exact"
