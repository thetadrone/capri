package filter.kalman;

import utils.math.linearalgebra.Matrix;

/**
 * Create an approximate R matrix (the measurement noise covariance matrix)
 * Assumptions:
 * <ul>
 * <li> diagonal matrix
 * <li> gamma = actual measurement interval / standard measurement interval,
 * where standard measurement interval is needed to get 95% confidence
 * <li> R[i,i] = (0.05 * mean of z[i] / 1.96)^2  /  gamma
 * </ul>
 * 
 * @author anonymous
 */
public class RMatrixCreator {

	/**
	 * default 95% confidence interval
	 */
	protected float errorLevel = 0.05f;
	
	/**
	 * Based on the t distribution, the confidence interval is
	 * 1.96 * standard deviation
	 */
	protected float tPercentile = 1.96f;
	
	/**
	 * ratio of actual to standard measurement interval
	 */
	protected float gamma = 1;
	
	/**
	 * Default Constructor
	 */
	public RMatrixCreator() {
		super();
	}
	
	/**
	 * Parametrized Constructor
	 * @param errorLevel
	 * @param tPercentile
	 * @param gamma
	 */
	public RMatrixCreator(float errorLevel, float tPercentile, float gamma) {
		this.errorLevel = errorLevel;
		this.tPercentile = tPercentile;
		this.gamma = gamma;
	}
	
	/**
	 * Return an approximate R matrix given mean of measurements
	 * @param meanMeasure
	 * @return
	 */
	public Matrix getMatrix(double[] meanMeasure) {
		int n = meanMeasure.length;
		double[][] r = new double[n][n];
		double factor = Math.pow(errorLevel / tPercentile, 2) / gamma;
		for (int i = 0; i < n; i++) {
			r[i][i] = factor * Math.pow(meanMeasure[i], 2);
		}
		return new Matrix(r);
	}

}
