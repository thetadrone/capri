package filter.kalman;

import utils.math.linearalgebra.Matrix;

/**
 * Bound the values in the state vector
 * 
 * @author anonymous
 */
public class StateLimiter {

	/**
	 * Minimum values for state
	 */
	protected double[] minStateValue = null;

	/**
	 * Maximum values for state
	 */
	protected double[] maxStateValue = null;
	
	/**
	 * Constructor
	 */
	public StateLimiter() {
		super();
	}

	/**
	 * Create a StateLimiter object to bound the values in the state vector.
	 * @param minStateValue
	 * @param maxStateValue
	 */
	public StateLimiter(double[] minStateValue, double[] maxStateValue) {
		this.minStateValue = minStateValue;
		this.maxStateValue = maxStateValue;
	}
	
	public void setUpperLimit(double[] maxStateValue) {
		this.maxStateValue = maxStateValue;
	}
	
	public void setLowerLimit(double[] minStateValue) {
		this.minStateValue = minStateValue;
	}	

	/**
	 * Bound the values in state vector x
	 * @param x
	 */
	public void limit(Matrix x) {
		for (int j = 0; j < x.getRows(); j++) {
			double value = x.get(j, 0);
			if (minStateValue != null) {
				value = Math.max(minStateValue[j], value);
			}
			if (maxStateValue != null) {
				value = Math.min(maxStateValue[j], value);
			}
			x.set(j, 0, value);
		}
	}
}
