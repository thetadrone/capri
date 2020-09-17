package capri.filter.multi;

import capri.env.Environment;
import capri.model.BidModelMulti;
import filter.kalman.OneFunctionalMatrix;
import utils.math.linearalgebra.Matrix;

/**
 * The H function 
 * Z = h(X)
 * 
 * @author anonymous
 */
public class hFunctionMulti extends OneFunctionalMatrix {

	protected Environment env;

	public hFunctionMulti(Environment env) {
		super();
		this.env = env;
	}

	public Matrix evaluate(Matrix x) {
		float eta = (float) x.get(0,  0);

		double[][] h = new double[1][1];
		float[] perf;

		BidModelMulti model = new BidModelMulti(env.getBidModel(), eta, env.avgServTime);
		perf = model.solve(env.bid);

		h[0][0] = perf[0];
		return new Matrix(h);
	}

}
