package capri.filter;

import filter.kalman.OneFunctionalMatrix;
import utils.math.linearalgebra.Matrix;

/**
 * The F function 
 * X[k] = F[k] . X[k-1]
 *  
 * @author anonymous
 */
public class fFunction extends OneFunctionalMatrix {
	
	public Matrix evaluate(Matrix x) {
		return x;
	}

}
