package filter.kalman;

import utils.math.linearalgebra.Matrix;

/**
 * Create an approximate Q matrix (the process noise covariance matrix)
 * Assumptions:
 * <ul>
 * <li> diagonal matrix
 * <li> Q[i,i] = (approx magnitude of change in state x[i])^2
 * </ul>
 * 
 * @author anonymous
 */
public class QMatrixCreator {

	/**
	 * Constructor
	 */
	public QMatrixCreator() {
		super();
	}

	/**
	 * Return an approximate Q matrix given magnitude of state change
	 * @param stateChange
	 * @return
	 */
	public Matrix getMatrix(double[] stateChange) {
		int m = stateChange.length;
		double[][] q = new double[m][m];
		for (int i = 0; i < m; i++) {
			q[i][i] = Math.pow(stateChange[i], 2);
		}
		return new Matrix(q);
	}

}
