package capri.filter.single;

import capri.env.Environment;
import capri.model.BidModelSingle;
import filter.kalman.OneFunctionalMatrix;
import utils.math.linearalgebra.Matrix;

/**
 * The H function 
 * Z = h(X)
 * 
 * @author anonymous
 */
public class hFunction extends OneFunctionalMatrix {

	protected Environment env;

	public hFunction(Environment env) {
		super();
		this.env = env;
	}

	public Matrix evaluate(Matrix x) {
		int m = x.getRows();
		float[] theta = new float[m];
		for (int i = 0; i < m; i++) {
			theta[i] = (float) x.get(i,  0);
		}

		double[][] h = new double[1][1];
		float[] perf;

		float mu = 1 / env.avgServTime;
		BidModelSingle model = new BidModelSingle(env.alpha, env.beta, theta, mu, env.r0);
		model.setConditionalServiceTime(env.conditionalServiceTime);
		perf = model.solve(env.bid, env.servTime);

		h[0][0] = perf[0];
		return new Matrix(h);
	}

}
