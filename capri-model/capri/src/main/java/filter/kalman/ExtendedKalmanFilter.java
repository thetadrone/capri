package filter.kalman;

import utils.math.linearalgebra.Matrix;
import utils.math.linearalgebra.MatrixException;

/**
 * Extended Kalman Filter (Nonlinear) <pre> X<sub>k</sub> = F(X<sub>k-1</sub>, U<sub>k-1</sub>, W<sub>k-1</sub>) Z<sub>k</sub> = H(X<sub>k</sub>, V<sub>k</sub>) </pre>
 * @author  anonymous
 */
public class ExtendedKalmanFilter extends KalmanFilter{

	/**
	 * creator of h matrix
	 */
	protected FunctionalMatrix hFunction;

	/**
	 * creator of Jacobian matrix of h
	 */
	protected FunctionalMatrix HFunction;

	/**
	 * creator of f matrix
	 */
	protected FunctionalMatrix fFunction;
	
	/**
	 * creator of Jacobian matrix of f
	 */
	protected FunctionalMatrix FFunction;
	
	/**
	 * Construct an Exnteded Kalman filter
	 * @param nStates
	 * @param nMeasures
	 * @param initX
	 * @param initP
	 * @param hFunction
	 * @param Hfunction
	 * @param fFunction
	 * @param FFunction
	 */
	public ExtendedKalmanFilter(int nStates, int nMeasures, Matrix initX, Matrix initP,
			OneFunctionalMatrix hFunction, OneFunctionalMatrix HFunction,
			OneFunctionalMatrix fFunction, OneFunctionalMatrix FFunction) {
		super(nStates, nMeasures, initX, initP);
		this.hFunction = hFunction;
		this.HFunction = HFunction;
		this.fFunction = fFunction;
		this.FFunction = FFunction;
	}
	
	/**
	 * Construct an Exnteded Kalman filter with control
	 * @param nStates
	 * @param nMeasures
	 * @param initX
	 * @param initP
	 * @param hFunction
	 * @param HFunction
	 * @param fFunction
	 * @param FFunction
	 */
	public ExtendedKalmanFilter(int nStates, int nMeasures, Matrix initX, Matrix initP,
			OneFunctionalMatrix hFunction, OneFunctionalMatrix HFunction,
			TwoFunctionalMatrix fFunction, TwoFunctionalMatrix FFunction) {
		super(nStates, nMeasures, initX, initP);
		this.hFunction = hFunction;
		this.HFunction = HFunction;
		this.fFunction = fFunction;
		this.FFunction = FFunction;
	}

	/**
	 * Filter Prediction step
	 * @param processNoiseCovariance
	 */
	public void predict(Matrix processNoiseCovariance) {
		this.Q = processNoiseCovariance;
		F = ((OneFunctionalMatrix) FFunction).evaluate(X);
		X = ((OneFunctionalMatrix) fFunction).evaluate(X);
		super.predictUsingX();
	}
	
	public void sethFunction(FunctionalMatrix function) {
		hFunction = function;
	}

	/**
	 * @param hFunction  the hFunction to set
	 * @uml.property  name="hFunction"
	 */
	public void setHFunction(FunctionalMatrix function) {
		HFunction = function;
	}
	
	public void setfFunction(FunctionalMatrix function) {
		fFunction = function;
	}
	
	public void setFFunction(FunctionalMatrix function) {
		FFunction = function;
	}

	/**
	 * Filter Prediction step with control
	 * @param processNoiseCovariance
	 */
	public void predict(Matrix control, Matrix processNoiseCovariance) {
		this.Q = processNoiseCovariance;
		F = ((TwoFunctionalMatrix) FFunction).evaluate(X, control);
		X = ((TwoFunctionalMatrix) fFunction).evaluate(X, control);
		super.predictUsingX();
	}
	
	/**
	 * Filter correction (update) step
	 * @param measurements
	 * @param measureNoiseCovariance
	 */
	public void correct(Matrix measurements, Matrix measureNoiseCovariance) {
		this.Z = measurements;
		this.R = measureNoiseCovariance;
		H = ((OneFunctionalMatrix) HFunction).evaluate(X);

		try {
			Y = Z.subtract(((OneFunctionalMatrix) hFunction).evaluate(X));
		} catch (MatrixException e) {
			System.err.print("MatrixException:" + e);
		}
		correctUsingY();
	}
	
	public float[] getOutputVector() {
		Matrix O;
		O = ((OneFunctionalMatrix) hFunction).evaluate(X);
		if (O == null) {
			return null;
		}
		float[] v = new float[n];
		for (int i = 0; i < n; i++) {
			v[i] = (float) O.get(i, 0);
		}
		return v;
	}

}
