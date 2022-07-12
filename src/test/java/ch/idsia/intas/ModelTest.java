package ch.idsia.intas;

import ch.idsia.crema.factor.bayesian.BayesianFactor;
import ch.idsia.crema.inference.bp.LoopyBeliefPropagation;
import ch.idsia.crema.model.graphical.DAGModel;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Author:  Claudio "Dna" Bonesana
 * Project: 2022-flairs
 * Date:    09.02.2022 17:16
 */
class ModelTest {
	static final double pX1 = 0.4;
	static final double pX2 = 0.35;

	static final double lambda1 = 0.8;
	static final double lambda2 = 0.7;

	Model m;
	DAGModel<BayesianFactor> model;

	@BeforeEach
	void setUp() {
		m = new Model();
		model = m.model;
	}

	@Test
	void noisyTestTwoParentsAnd() {
		final int X1 = m.addSkill("X1", pX1);
		final int X2 = m.addSkill("X2", pX2);

		final int x1t = m.addXiAnd(X1, lambda1);
		final int x2t = m.addXiAnd(X2, lambda2);

		final TIntList parents = new TIntArrayList(new int[]{x1t, x2t});

		final int Y = m.addQuestionAnd("Y", parents);
		m.assignFactors();

		final LoopyBeliefPropagation<BayesianFactor> inf = new LoopyBeliefPropagation<>();

		// P(X1 = 1 | Y = 0)
		final TIntIntMap obs = new TIntIntHashMap();
		obs.put(Y, 0);
		final BayesianFactor q0 = inf.query(model, obs, X1);

		// P(X1 = 1)
		obs.clear();
		final BayesianFactor q1 = inf.query(model, obs, X1);

		// P(X1 = 1 | Y = 1)
		obs.clear();
		obs.put(Y, 1);
		final BayesianFactor q2 = inf.query(model, obs, X1);

		System.out.println("NOISY-AND");
		System.out.printf("P(X1 = 1 | Y = 0) = %.4f %n", q0.getValue(1));
		System.out.printf("P(X1 = 1)         = %.4f %n", q1.getValue(1));
		System.out.printf("P(X1 = 1 | Y = 1) = %.4f %n", q2.getValue(1));

		Assertions.assertEquals(0.267489712, q0.getValue(1), 1e-3);
		Assertions.assertEquals(0.4, q1.getValue(1), 1e-3);
		Assertions.assertEquals(0.454545455, q2.getValue(1), 1e-3);
	}

	@Test
	void noisyTestTwoParentsOr() {
		final int X1 = m.addSkill("X1", pX1);
		final int X2 = m.addSkill("X2", pX2);

		final int x1t = m.addXiOr(X1, lambda1);
		final int x2t = m.addXiOr(X2, lambda2);

		final TIntList parents = new TIntArrayList(new int[]{x1t, x2t});

		final int Y = m.addQuestionOr("Y", parents);
		m.assignFactors();

		final LoopyBeliefPropagation<BayesianFactor> inf = new LoopyBeliefPropagation<>();

		// P(X1 = 1 | Y = 0)
		final TIntIntMap obs = new TIntIntHashMap();
		obs.put(Y, 0);
		final BayesianFactor q0 = inf.query(model, obs, X1);

		// P(X1 = 1)
		obs.clear();
		final BayesianFactor q1 = inf.query(model, obs, X1);

		// P(X1 = 1 | Y = 1)
		obs.clear();
		obs.put(Y, 1);
		final BayesianFactor q2 = inf.query(model, obs, X1);

		System.out.println("NOISY-OR");
		System.out.printf("P(X1 = 1 | Y = 0) = %.4f %n", q0.getValue(1));
		System.out.printf("P(X1 = 1)         = %.4f %n", q1.getValue(1));
		System.out.printf("P(X1 = 1 | Y = 1) = %.4f %n", q2.getValue(1));

		Assertions.assertEquals(0.34782609, q0.getValue(1), 1e-3);
		Assertions.assertEquals(0.4, q1.getValue(1), 1e-3);
		Assertions.assertEquals(0.64326161, q2.getValue(1), 1e-3);
	}
}