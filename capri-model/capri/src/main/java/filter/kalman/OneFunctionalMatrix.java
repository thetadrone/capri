package filter.kalman;

import utils.math.linearalgebra.Matrix;

/**
 * Create a matrix as a function of a given matrix
 * 
 * @author anonymous
 */
public abstract class OneFunctionalMatrix extends FunctionalMatrix{

	/**
	 * Evaluate a matrix as a function of one variable
	 * @param x given matrix
	 * @return new matrix
	 */
	public abstract Matrix evaluate(Matrix x);

}
