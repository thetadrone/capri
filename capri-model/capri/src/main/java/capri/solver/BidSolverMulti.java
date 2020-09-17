package capri.solver;

import capri.env.Environment;
import capri.model.BidModelMulti;
import utils.math.NewtonSolver;
import utils.math.ScalarFunction;
import utils.math.ScalarGradientFunction;

/**
 * Solves for a bid value in [0,1] given a slowdown value.
 * 
 * @author anonymous
 *
 */
public class BidSolverMulti extends BidSolverBase {
	
	protected Environment env;
	protected BidModelMulti bidModel;
	
	public BidSolverMulti(Environment env, float eta) {
		super();
		this.env = env;
		bidModel = new BidModelMulti(env.getBidModel(), eta, env.avgServTime);
	}
	
	@Override
	public float solve(float slowdown) {
		ScalarFunction fx = new BidSolverMulti.f(slowdown);
		ScalarGradientFunction fxPrime = new BidSolverMulti.fPrime(slowdown);
		
		float[] lowerBound = new float[] { 0 };
		float[] upperBound = new float[] { 1 };
		if (fx.eval(lowerBound) >= 0) {
			return 0;
		}
		if (fx.eval(upperBound) <= 0) {
			return 1;
		}
		
		NewtonSolver solver = new NewtonSolver(fx, fxPrime);
		solver.setBounds(lowerBound, upperBound);
		
		float[] initValue = new float[] { 0.5f };
		float[] y = solver.solve(numIterations, tolerance, initValue);
		return y[0];
	}
	
	@Override
	public float solve(float slowdown, float servTime) {
		return solve(slowdown);
	}
	
	public class f extends ScalarFunction {
		protected float slowdown;

		public f(float slowdown) {
			super();
			this.slowdown = slowdown;
		}

		@Override
		public float eval(float[] x) {
			
			if (x[0] < 0)
				return eval(new float[] { 0 });
			if (x[0] > 1)
				return eval(new float[] { 1 });
			
			float[] out = bidModel.solve(x[0]);
			float stilde = out[0];
			return slowdown - stilde;
		}
	}
	
	public class fPrime extends ScalarGradientFunction {
		protected float slowdown;

		public fPrime(float slowdown) {
			super();
			this.slowdown = slowdown;
		}
		
		@Override
		public float[] eval(float[] x) {
			
			if (x[0] < 0)
				return eval(new float[] { 0 });
			if (x[0] >= 1)
				return eval(new float[] { 1 - delta });

			ScalarFunction fx = new BidSolverMulti.f(slowdown);
			float yx = fx.eval(x);

			float xPlusDelta = x[0] + delta;
			float[] x1 = new float[] { xPlusDelta };
			ScalarFunction fx1 = new BidSolverMulti.f(slowdown);
			float yx1 = fx1.eval(x1);

			float slope = (yx1 - yx) / delta;
			return new float[] { slope };
		}
	}
	
}
