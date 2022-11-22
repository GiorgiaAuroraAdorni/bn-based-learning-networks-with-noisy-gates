#!/bin/bash

AnswersOldB="data/old_posteriors/elementary_skills/student-answers-commands.xlsx"

ModelOld1bLeak="data/old_posteriors/elementary_skills/simple_model/leak/questions-skill-model1b_leak.xlsx"
ResultsOld1bLeakUnconstrained="data/old_posteriors/elementary_skills/simple_model/leak/unconstrained/results_model1b_leak_unconstrained.xlsx"
ResultsOld1bLeakConstrained="data/old_posteriors/elementary_skills/simple_model/leak/constrained/results_model1b_leak_constrained.xlsx"

java -jar target/itas-1.0.jar $ModelOld1bLeak $AnswersOldB $ResultsOld1bLeakUnconstrained "unconstrained"
java -jar target/itas-1.0.jar $ModelOld1bLeak $AnswersOldB $ResultsOld1bLeakConstrained "constrained"

ModelOld1bNoleak="data/old_posteriors/elementary_skills/simple_model/noleak/questions-skill-model1b_noleak.xlsx"
ResultsOld1bNoleakUnconstrained="data/old_posteriors/elementary_skills/simple_model/noleak/unconstrained/results_model1b_noleak_unconstrained.xlsx"
ResultsOld1bNoleakConstrained="data/old_posteriors/elementary_skills/simple_model/noleak/constrained/results_model1b_noleak_constrained.xlsx"

java -jar target/itas-1.0.jar $ModelOld1bNoleak $AnswersOldB $ResultsOld1bNoleakUnconstrained "unconstrained"
java -jar target/itas-1.0.jar $ModelOld1bNoleak $AnswersOldB $ResultsOld1bNoleakConstrained "constrained"

ModelOld2bLeak="data/old_posteriors/elementary_skills/complex_model/leak/questions-skill-model2b_leak.xlsx"
ResultsOld2bLeakUnconstrained="data/old_posteriors/elementary_skills/complex_model/leak/unconstrained/results_model2b_leak_unconstrained.xlsx"
ResultsOld2bLeakConstrained="data/old_posteriors/elementary_skills/complex_model/leak/constrained/results_model2b_leak_constrained.xlsx"

java -jar target/itas-1.0.jar $ModelOld2bLeak $AnswersOldB $ResultsOld2bLeakUnconstrained "unconstrained"
java -jar target/itas-1.0.jar $ModelOld2bLeak $AnswersOldB $ResultsOld2bLeakConstrained "constrained"

ModelOld2bNoleak="data/old_posteriors/elementary_skills/complex_model/noleak/questions-skill-model2b_noleak.xlsx"
ResultsOld2bNoleakUnconstrained="data/old_posteriors/elementary_skills/complex_model/noleak/unconstrained/results_model2b_noleak_unconstrained.xlsx"
ResultsOld2bNoleakConstrained="data/old_posteriors/elementary_skills/complex_model/noleak/constrained/results_model2b_noleak_constrained.xlsx"

java -jar target/itas-1.0.jar $ModelOld2bNoleak $AnswersOldB $ResultsOld2bNoleakUnconstrained "unconstrained"
java -jar target/itas-1.0.jar $ModelOld2bNoleak $AnswersOldB $ResultsOld2bNoleakConstrained "constrained"

AnswersNewB="data/new_posteriors/elementary_skills/student-answers-commands.xlsx"

ModelNew1bLeak="data/new_posteriors/elementary_skills/simple_model/leak/questions-skill-model1b_leak.xlsx"
ResultsNew1bLeakUnconstrained="data/new_posteriors/elementary_skills/simple_model/leak/unconstrained/results_model1b_leak_unconstrained.xlsx"
ResultsNew1bLeakConstrained="data/new_posteriors/elementary_skills/simple_model/leak/constrained/results_model1b_leak_constrained.xlsx"

java -jar target/itas-1.0.jar $ModelNew1bLeak $AnswersNewB $ResultsNew1bLeakUnconstrained "unconstrained"
java -jar target/itas-1.0.jar $ModelNew1bLeak $AnswersNewB $ResultsNew1bLeakConstrained "constrained"

ModelNew1bNoleak="data/new_posteriors/elementary_skills/simple_model/noleak/questions-skill-model1b_noleak.xlsx"
ResultsNew1bNoleakUnconstrained="data/new_posteriors/elementary_skills/simple_model/noleak/unconstrained/results_model1b_noleak_unconstrained.xlsx"
ResultsNew1bNoleakConstrained="data/new_posteriors/elementary_skills/simple_model/noleak/constrained/results_model1b_noleak_constrained.xlsx"

java -jar target/itas-1.0.jar $ModelNew1bNoleak $AnswersNewB $ResultsNew1bNoleakUnconstrained "unconstrained"
java -jar target/itas-1.0.jar $ModelNew1bNoleak $AnswersNewB $ResultsNew1bNoleakConstrained "constrained"

ModelNew2bLeak="data/new_posteriors/elementary_skills/complex_model/leak/questions-skill-model2b_leak.xlsx"
ResultsNew2bLeakUnconstrained="data/new_posteriors/elementary_skills/complex_model/leak/unconstrained/results_model2b_leak_unconstrained.xlsx"
ResultsNew2bLeakConstrained="data/new_posteriors/elementary_skills/complex_model/leak/constrained/results_model2b_leak_constrained.xlsx"

java -jar target/itas-1.0.jar $ModelNew2bLeak $AnswersNewB $ResultsNew2bLeakUnconstrained "unconstrained"
java -jar target/itas-1.0.jar $ModelNew2bLeak $AnswersNewB $ResultsNew2bLeakConstrained "constrained"

ModelNew2bNoleak="data/new_posteriors/elementary_skills/complex_model/noleak/questions-skill-model2b_noleak.xlsx"
ResultsNew2bNoleakUnconstrained="data/new_posteriors/elementary_skills/complex_model/noleak/unconstrained/results_model2b_noleak_unconstrained.xlsx"
ResultsNew2bNoleakConstrained="data/new_posteriors/elementary_skills/complex_model/noleak/constrained/results_model2b_noleak_constrained.xlsx"

java -jar target/itas-1.0.jar $ModelNew2bNoleak $AnswersNewB $ResultsNew2bNoleakUnconstrained "unconstrained"
java -jar target/itas-1.0.jar $ModelNew2bNoleak $AnswersNewB $ResultsNew2bNoleakConstrained "constrained"
