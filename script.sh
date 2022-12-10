#!/bin/bash

mvn clean compile assembly:single

# Models
Model1a="data/models/questions-skill-model1a.xlsx"
Model1b="data/models/questions-skill-model1b.xlsx"
Model2b="data/models/questions-skill-model2b.xlsx"
Model3b="data/models/questions-skill-model3b.xlsx"
Model4b="data/models/questions-skill-model4b.xlsx"

# Answers
Answers1="data/answers/student-answers_1a.xlsx"
Answers1a="data/answers/student-answers_1a_constrained.xlsx"
Answersb="data/answers/student-answers_b_constrained.xlsx"

# Baseline: without constraints and without supplementary skills
Results1="data/results/results_questions-skill-baseline_model.xlsx"
java -Xmx16g -jar target/itas-1.0.jar $Model1a $Answers1 $Results1 "unconstrained" "exact"

# Model 1a: baseline + constraints
Results1a="data/results/results_questions-skill-model1a.xlsx"
java -Xmx16g -jar target/itas-1.0.jar $Model1a $Answers1a $Results1a "constrained" "exact"

# Model 1b: baseline + constraints + supplementary skills
Results1b="data/results/results_questions-skill-model1b.xlsx"
java -Xmx16g -jar target/itas-1.0.jar $Model1b $Answersb $Results1b "constrained" "exact"

# Model 2b: model 2 + constraints + supplementary skills
Results2b="data/results/results_questions-skill-model2b.xlsx"
java -Xmx16g -jar target/itas-1.0.jar $Model2b $Answersb $Results2b "constrained" "exact"

# Model 3b: model 3 + constraints + supplementary skills
Results3b="data/results/results_questions-skill-model3b.xlsx"
java -Xmx16g -jar target/itas-1.0.jar $Model3b $Answersb $Results3b "constrained" "exact"

# Model 4b: model 4 + constraints + supplementary skills
Results4b="data/results/results_questions-skill-model4b.xlsx"
java -Xmx16g -jar target/itas-1.0.jar $Model4b $Answersb $Results4b "constrained" "exact"
