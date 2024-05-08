#!/bin/bash

mvn clean compile assembly:single

# Models
Model="data/models/virtual/questions-skill-model.xlsx"

# Answers
Answers="data/answers/virtual/student-answers_b_constrained.xlsx"

# Model 3b: model 3 + constraints + supplementary skills
Results="data/results/virtual/predictor_results_questions-skill-model3b.xlsx"


java -Xmx16g -jar target/itas-1.0.jar $Model $Answers $Results "constrained" "exact"
