#!/bin/bash

mvn clean compile assembly:single

# Basic model without constraints
# 1-> questions-skill-model1a_leak		results_questions-skill-model1a_leak_unconstrained		student-answers_1a_unconstrained
Model1="data/models/questions-skill-model1a_leak.xlsx"
Results1="data/results/results_questions-skill-model1a_leak_unconstrained.xlsx"
Answers1="data/answers/student-answers_1a_unconstrained.xlsx"
java -Xmx16g -jar target/itas-1.0.jar $Model1 $Answers1 $Results1 "unconstrained" "exact"

# Basic model with constraints
# 2 -> questions-skill-model1a_leak		results_questions-skill-model1a_leak_constrained 		student-answers_1a_constrained
Model2="data/models/questions-skill-model1a_leak.xlsx"
Results2="data/results/results_questions-skill-model1a_leak_constrained.xlsx"
Answers2="data/answers/student-answers_1a_constrained.xlsx"
java -Xmx16g -jar target/itas-1.0.jar $Model2 $Answers2 $Results2 "constrained" "exact"

# Basic model with constraints and supplementary skills
# 3 -> questions-skill-model1b_leak		results_questions-skill-model1b_leak_constrained		student-answers_b_constrained
Model3="data/models/questions-skill-model1b_leak.xlsx"
Results3="data/results/results_questions-skill-model1b_leak_constrained.xlsx"
Answers3="data/answers/student-answers_b_constrained.xlsx"
java -Xmx16g -jar target/itas-1.0.jar $Model3 $Answers3 $Results3 "constrained" "exact"

# More elaborated model with constraints and supplementary skills
# 4 -> questions-skill-model2b_leak		results_questions-skill-model2b_leak_constrained		student-answers_b_constrained
Model4="data/models/questions-skill-model2b_leak.xlsx"
Results4="data/results/results_questions-skill-model2b_leak_constrained.xlsx"
Answers4="data/answers/student-answers_b_constrained.xlsx"
java -Xmx16g -jar target/itas-1.0.jar $Model4 $Answers4 $Results4 "constrained" "exact"

# Complex model with constraints and supplementary skills
# 5 -> questions-skill-model3b_leak		results_questions-skill-model3b_leak_constrained		student-answers_b_constrained
Model5="data/models/questions-skill-model3b_leak.xlsx"
Results5="data/results/results_questions-skill-model3b_leak_constrained.xlsx"
Answers5="data/answers/student-answers_b_constrained.xlsx"
java -Xmx16g -jar target/itas-1.0.jar $Model5 $Answers5 $Results5 "constrained" "exact"
