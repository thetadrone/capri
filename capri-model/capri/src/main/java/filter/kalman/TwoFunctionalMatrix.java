package filter.kalman;

import utils.math.linearalgebra.Matrix;

/**
 * Functional matrix of two variables
 * 
 * @author anonymous
 */
public abstract class TwoFunctionalMatrix extends FunctionalMatrix{

	/**
	 * Evaluate a matrix as a function of one variable
	 * @param x given first matrix
	 * @param u given second matrix
	 * @return new matrix
	 */
	public abstract Matrix evaluate(Matrix x, Matrix u);

}
