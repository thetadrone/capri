package capri.solver;

import capri.env.Environment;
import capri.model.BidModelSingle;
import utils.math.NewtonSolver;
import utils.math.ScalarFunction;
import utils.math.ScalarGradientFunction;

/**
 * Solves for a bid value in [0,1] given a slowdown value.
 * 
 * @author anonymous
 *
 */
public class BidSolverSingle extends BidSolverBase {
	
	protected Environment env;
	protected BidModelSingle bidModel;
	
	public BidSolverSingle(Environment env, float[] theta) {
		super();
		this.env = env;
		float mu = 1 / env.avgServTime;
		bidModel = new BidModelSingle(env.alpha, env.beta, theta, mu, env.r0);
		bidModel.setConditionalServiceTime(env.conditionalServiceTime);
	}
	
	@Override
	public float solve(float slowdown) {
		return solve(slowdown, 0);
	}
	
	@Override
	public float solve(float slowdown, float servTime) {
		ScalarFunction fx = new BidSolverSingle.f(slowdown, servTime);
		ScalarGradientFunction fxPrime = new BidSolverSingle.fPrime(slowdown, servTime);
		
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
	
	public class f extends ScalarFunction {
		protected float slowdown;
		protected float servTime;

		public f(float slowdown, float servTime) {
			super();
			this.slowdown = slowdown;
			this.servTime = servTime;
		}
		
		public f(float slowdown) {
			this(slowdown, 0);
		}

		@Override
		public float eval(float[] x) {
			
			if (x[0] < 0)
				return eval(new float[] { 0 });
			if (x[0] > 1)
				return eval(new float[] { 1 });
			
			float[] out = (servTime > 0) ? bidModel.solve(x[0], env.servTime)
					: bidModel.solve(x[0]);
			float stilde = out[0];
			
			return slowdown - stilde;
		}
	}
	
	public class fPrime extends ScalarGradientFunction {
		protected float slowdown;
		protected float servTime;

		public fPrime(float slowdown, float servTime) {
			super();
			this.slowdown = slowdown;
			this.servTime = servTime;
		}
		
		public fPrime(float slowdown) {
			this(slowdown, 0);
		}

		@Override
		public float[] eval(float[] x) {
			
			if (x[0] < 0)
				return eval(new float[] { 0 });
			if (x[0] >= 1)
				return eval(new float[] { 1 - delta });

			ScalarFunction fx = new BidSolverSingle.f(slowdown, servTime);
			float yx = fx.eval(x);

			float xPlusDelta = x[0] + delta;
			float[] x1 = new float[] { xPlusDelta };
			ScalarFunction fx1 = new BidSolverSingle.f(slowdown, servTime);
			float yx1 = fx1.eval(x1);

			float slope = (yx1 - yx) / delta;
			return new float[] { slope };
		}
	}
	
}
