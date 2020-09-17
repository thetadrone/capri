package capri.filter;

import filter.kalman.OneFunctionalMatrix;
import utils.math.linearalgebra.Matrix;

/**
 * The Jacobian F function 
 * X[k] = F[k] . X[k-1]
 *  
 * @author anonymous
 */
public class FJacobian extends OneFunctionalMatrix {

	/** number of states */
	protected int nStates;

	/**
	 * @param states	number of states
	 */
	public FJacobian(int states) {
		super();
		nStates = states;
	}

	public Matrix evaluate(Matrix x) {
		return new Matrix(nStates);
	}
}
