#!/bin/bash

AnswersOldA="data/old_posteriors/student-answers.xlsx"

ModelOld1aLeak="data/old_posteriors/target_skills/models/questions-skill-model1a_leak.xlsx"
ResultsOld1aLeakUnconstrained="data/old_posteriors/target_skills/results/results_model1a_leak_unconstrained.xlsx"
ResultsOld1aLeakConstrained="data/old_posteriors/target_skills/results/results_model1a_leak_constrained.xlsx"

java -jar target/itas-1.0.jar $ModelOld1aLeak $AnswersOldA $ResultsOld1aLeakUnconstrained "unconstrained"
java -jar target/itas-1.0.jar $ModelOld1aLeak $AnswersOldA $ResultsOld1aLeakConstrained "constrained"

ModelOld1aNoleak="data/old_posteriors/target_skills/models/questions-skill-model1a_noleak.xlsx"
ResultsOld1aNoleakUnconstrained="data/old_posteriors/target_skills/results/results_model1a_noleak_unconstrained.xlsx"
ResultsOld1aNoleakConstrained="data/old_posteriors/target_skills/results/results_model1a_noleak_constrained.xlsx"

java -jar target/itas-1.0.jar $ModelOld1aNoleak $AnswersOldA $ResultsOld1aNoleakUnconstrained "unconstrained"
java -jar target/itas-1.0.jar $ModelOld1aNoleak $AnswersOldA $ResultsOld1aNoleakConstrained "constrained"

ModelOld2aLeak="data/old_posteriors/target_skills/models/questions-skill-model2a_leak.xlsx"
ResultsOld2aLeakUnconstrained="data/old_posteriors/target_skills/results/results_model2a_leak_unconstrained.xlsx"
ResultsOld2aLeakConstrained="data/old_posteriors/target_skills/results/results_model2a_leak_constrained.xlsx"

java -jar target/itas-1.0.jar $ModelOld2aLeak $AnswersOldA $ResultsOld2aLeakUnconstrained "unconstrained"
java -jar target/itas-1.0.jar $ModelOld2aLeak $AnswersOldA $ResultsOld2aLeakConstrained "constrained"

ModelOld2aNoleak="data/old_posteriors/target_skills/models/questions-skill-model2a_noleak.xlsx"
ResultsOld2aNoleakUnconstrained="data/old_posteriors/target_skills/results/results_model2a_noleak_unconstrained.xlsx"
ResultsOld2aNoleakConstrained="data/old_posteriors/target_skills/results/results_model2a_noleak_constrained.xlsx"

java -jar target/itas-1.0.jar $ModelOld2aNoleak $AnswersOldA $ResultsOld2aNoleakUnconstrained "unconstrained"
java -jar target/itas-1.0.jar $ModelOld2aNoleak $AnswersOldA $ResultsOld2aNoleakConstrained "constrained"


AnswersNewA="data/new_posteriors/student-answers.xlsx"

ModelNew1aLeak="data/new_posteriors/target_skills/models/questions-skill-model1a_leak.xlsx"
ResultsNew1aLeakUnconstrained="data/new_posteriors/target_skills/results/results_model1a_leak_unconstrained.xlsx"
ResultsNew1aLeakConstrained="data/new_posteriors/target_skills/results/results_model1a_leak_constrained.xlsx"

java -jar target/itas-1.0.jar $ModelNew1aLeak $AnswersNewA $ResultsNew1aLeakUnconstrained "unconstrained"
java -jar target/itas-1.0.jar $ModelNew1aLeak $AnswersNewA $ResultsNew1aLeakConstrained "constrained"

ModelNew1aNoleak="data/new_posteriors/target_skills/models/questions-skill-model1a_noleak.xlsx"
ResultsNew1aNoleakUnconstrained="data/new_posteriors/target_skills/results/results_model1a_noleak_unconstrained.xlsx"
ResultsNew1aNoleakConstrained="data/new_posteriors/target_skills/results/results_model1a_noleak_constrained.xlsx"

java -jar target/itas-1.0.jar $ModelNew1aNoleak $AnswersNewA $ResultsNew1aNoleakUnconstrained "unconstrained"
java -jar target/itas-1.0.jar $ModelNew1aNoleak $AnswersNewA $ResultsNew1aNoleakConstrained "constrained"

ModelNew2aLeak="data/new_posteriors/target_skills/models/questions-skill-model2a_leak.xlsx"
ResultsNew2aLeakUnconstrained="data/new_posteriors/target_skills/results/results_model2a_leak_unconstrained.xlsx"
ResultsNew2aLeakConstrained="data/new_posteriors/target_skills/results/results_model2a_leak_constrained.xlsx"

java -jar target/itas-1.0.jar $ModelNew2aLeak $AnswersNewA $ResultsNew2aLeakUnconstrained "unconstrained"
java -jar target/itas-1.0.jar $ModelNew2aLeak $AnswersNewA $ResultsNew2aLeakConstrained "constrained"

ModelNew2aNoleak="data/new_posteriors/target_skills/models/questions-skill-model2a_noleak.xlsx"
ResultsNew2aNoleakUnconstrained="data/new_posteriors/target_skills/results/results_model2a_noleak_unconstrained.xlsx"
ResultsNew2aNoleakConstrained="data/new_posteriors/target_skills/results/results_model2a_noleak_constrained.xlsx"

java -jar target/itas-1.0.jar $ModelNew2aNoleak $AnswersNewA $ResultsNew2aNoleakUnconstrained "unconstrained"
java -jar target/itas-1.0.jar $ModelNew2aNoleak $AnswersNewA $ResultsNew2aNoleakConstrained "constrained"
