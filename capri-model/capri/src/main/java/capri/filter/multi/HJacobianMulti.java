package capri.filter.multi;

import capri.env.Environment;
import capri.model.BidModelMulti;
import filter.kalman.OneFunctionalMatrix;
import utils.math.linearalgebra.Matrix;

/**
 * The Jacobian H function 
 * H = dh(X)/dX
 * 
 * @author anonymous
 */
public class HJacobianMulti extends OneFunctionalMatrix {

	protected Environment env;
	protected float delta;

	public HJacobianMulti(Environment env, float delta) {
		super();
		this.env = env;
		this.delta =  delta;
	}
	
	public Matrix evaluate(Matrix x) {
		float eta = (float) x.get(0, 0);
		BidModelMulti model = new BidModelMulti(env.getBidModel(), eta, env.avgServTime);

		float[] perf = model.solve(env.bid);
		float slowdown0 = perf[0];

		double[][] H = new double[1][1];
		float slowdownR = model.solvePlus(env.bid, delta)[0];
		H[0][0] = (slowdownR - slowdown0) / (delta * eta);

		return new Matrix(H);
	}

}
