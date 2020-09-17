package capri.filter.single;

import capri.env.Environment;
import capri.model.BidModelSingle;
import filter.kalman.OneFunctionalMatrix;
import utils.math.linearalgebra.Matrix;

/**
 * The Jacobian H function 
 * H = dh(X)/dX
 * 
 * @author anonymous
 */
public class HJacobian extends OneFunctionalMatrix {

	protected Environment env;
	protected float delta;

	public HJacobian(Environment env, float delta) {
		super();
		this.env = env;
		this.delta =  delta;
	}
	
	public Matrix evaluate(Matrix x) {
		int m = x.getRows();
		float[] theta = new float[m];
		for (int i = 0; i < m; i++) {
			theta[i] = (float) x.get(i,  0);
		}

		float mu = 1 / env.avgServTime;
		BidModelSingle model = new BidModelSingle(env.alpha, env.beta, theta, mu, env.r0);
		model.setConditionalServiceTime(env.conditionalServiceTime);

		float[] perf = model.solve(env.bid, env.servTime);
		float slowdown0 = perf[0];

		double[][] H = new double[1][m];
		perf = model.solvePlus(env.bid, delta, env.servTime);
		for (int i = 0; i < m; i++) {
			float slowdownR = perf[i];
			H[0][i] = (slowdownR - slowdown0) / (delta * theta[i]);
		}

		return new Matrix(H);
	}

}
