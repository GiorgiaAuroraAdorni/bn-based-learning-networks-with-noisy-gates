#!/cin/cash

AnswersOldC="data/old_posteriors/elementary_skills_reduced/student-answers-commands_reduced.xlsx"

ModelOld1cLeak="data/old_posteriors/elementary_skills_reduced/simple_model/leak/questions-skill-model1c_leak.xlsx"
ResultsOld1cLeakUnconstrained="data/old_posteriors/elementary_skills_reduced/simple_model/leak/unconstrained/results_model1c_leak_unconstrained.xlsx"
ResultsOld1cLeakConstrained="data/old_posteriors/elementary_skills_reduced/simple_model/leak/constrained/results_model1c_leak_constrained.xlsx"

java -jar target/itas-1.0.jar $ModelOld1cLeak $AnswersOldC $ResultsOld1cLeakUnconstrained "unconstrained"
java -jar target/itas-1.0.jar $ModelOld1cLeak $AnswersOldC $ResultsOld1cLeakConstrained "constrained"

ModelOld1cNoleak="data/old_posteriors/elementary_skills_reduced/simple_model/noleak/questions-skill-model1c_noleak.xlsx"
ResultsOld1cNoleakUnconstrained="data/old_posteriors/elementary_skills_reduced/simple_model/noleak/unconstrained/results_model1c_noleak_unconstrained.xlsx"
ResultsOld1cNoleakConstrained="data/old_posteriors/elementary_skills_reduced/simple_model/noleak/constrained/results_model1c_noleak_constrained.xlsx"

java -jar target/itas-1.0.jar $ModelOld1cNoleak $AnswersOldC $ResultsOld1cNoleakUnconstrained "unconstrained"
java -jar target/itas-1.0.jar $ModelOld1cNoleak $AnswersOldC $ResultsOld1cNoleakConstrained "constrained"

ModelOld2cLeak="data/old_posteriors/elementary_skills_reduced/complex_model/leak/questions-skill-model2c_leak.xlsx"
ResultsOld2cLeakUnconstrained="data/old_posteriors/elementary_skills_reduced/complex_model/leak/unconstrained/results_model2c_leak_unconstrained.xlsx"
ResultsOld2cLeakConstrained="data/old_posteriors/elementary_skills_reduced/complex_model/leak/constrained/results_model2c_leak_constrained.xlsx"

java -jar target/itas-1.0.jar $ModelOld2cLeak $AnswersOldC $ResultsOld2cLeakUnconstrained "unconstrained"
java -jar target/itas-1.0.jar $ModelOld2cLeak $AnswersOldC $ResultsOld2cLeakConstrained "constrained"

ModelOld2cNoleak="data/old_posteriors/elementary_skills_reduced/complex_model/noleak/questions-skill-model2c_noleak.xlsx"
ResultsOld2cNoleakUnconstrained="data/old_posteriors/elementary_skills_reduced/complex_model/noleak/unconstrained/results_model2c_noleak_unconstrained.xlsx"
ResultsOld2cNoleakConstrained="data/old_posteriors/elementary_skills_reduced/complex_model/noleak/constrained/results_model2c_noleak_constrained.xlsx"

java -jar target/itas-1.0.jar $ModelOld2cNoleak $AnswersOldC $ResultsOld2cNoleakUnconstrained "unconstrained"
java -jar target/itas-1.0.jar $ModelOld2cNoleak $AnswersOldC $ResultsOld2cNoleakConstrained "constrained"

AnswersNewC="data/new_posteriors/elementary_skills_reduced/student-answers-commands_reduced.xlsx"

ModelNew1cLeak="data/new_posteriors/elementary_skills_reduced/simple_model/leak/questions-skill-model1c_leak.xlsx"
ResultsNew1cLeakUnconstrained="data/new_posteriors/elementary_skills_reduced/simple_model/leak/unconstrained/results_model1c_leak_unconstrained.xlsx"
ResultsNew1cLeakConstrained="data/new_posteriors/elementary_skills_reduced/simple_model/leak/constrained/results_model1c_leak_constrained.xlsx"

java -jar target/itas-1.0.jar $ModelNew1cLeak $AnswersNewC $ResultsNew1cLeakUnconstrained "unconstrained"
java -jar target/itas-1.0.jar $ModelNew1cLeak $AnswersNewC $ResultsNew1cLeakConstrained "constrained"

ModelNew1cNoleak="data/new_posteriors/elementary_skills_reduced/simple_model/noleak/questions-skill-model1c_noleak.xlsx"
ResultsNew1cNoleakUnconstrained="data/new_posteriors/elementary_skills_reduced/simple_model/noleak/unconstrained/results_model1c_noleak_unconstrained.xlsx"
ResultsNew1cNoleakConstrained="data/new_posteriors/elementary_skills_reduced/simple_model/noleak/constrained/results_model1c_noleak_constrained.xlsx"

java -jar target/itas-1.0.jar $ModelNew1cNoleak $AnswersNewC $ResultsNew1cNoleakUnconstrained "unconstrained"
java -jar target/itas-1.0.jar $ModelNew1cNoleak $AnswersNewC $ResultsNew1cNoleakConstrained "constrained"

ModelNew2cLeak="data/new_posteriors/elementary_skills_reduced/complex_model/leak/questions-skill-model2c_leak.xlsx"
ResultsNew2cLeakUnconstrained="data/new_posteriors/elementary_skills_reduced/complex_model/leak/unconstrained/results_model2c_leak_unconstrained.xlsx"
ResultsNew2cLeakConstrained="data/new_posteriors/elementary_skills_reduced/complex_model/leak/constrained/results_model2c_leak_constrained.xlsx"

java -jar target/itas-1.0.jar $ModelNew2cLeak $AnswersNewC $ResultsNew2cLeakUnconstrained "unconstrained"
java -jar target/itas-1.0.jar $ModelNew2cLeak $AnswersNewC $ResultsNew2cLeakConstrained "constrained"

ModelNew2cNoleak="data/new_posteriors/elementary_skills_reduced/complex_model/noleak/questions-skill-model2c_noleak.xlsx"
ResultsNew2cNoleakUnconstrained="data/new_posteriors/elementary_skills_reduced/complex_model/noleak/unconstrained/results_model2c_noleak_unconstrained.xlsx"
ResultsNew2cNoleakConstrained="data/new_posteriors/elementary_skills_reduced/complex_model/noleak/constrained/results_model2c_noleak_constrained.xlsx"

java -jar target/itas-1.0.jar $ModelNew2cNoleak $AnswersNewC $ResultsNew2cNoleakUnconstrained "unconstrained"
java -jar target/itas-1.0.jar $ModelNew2cNoleak $AnswersNewC $ResultsNew2cNoleakConstrained "constrained"
