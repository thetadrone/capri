package filter.kalman;

import utils.math.linearalgebra.Matrix;
import utils.math.linearalgebra.MatrixException;

/**
 * Kalman Filter (Linear) 
 * <pre> X<sub>k</sub> = F<sub>k</sub> X<sub>k-1</sub> + B<sub>k</sub> U<sub>k-1</sub> + W<sub>k-1</sub> </pre>
 * <p>
 * <pre> Z<sub>k</sub> = H<sub>k</sub> X<sub>k</sub> + V<sub>k</sub> </pre> 
 * where 
 * <pre> W<sub>k</sub> = Normal(0, Q<sub>k</sub>) V<sub>k</sub> = Normal(0, R<sub>k</sub>) </pre>
 * @author  anonymous
 */
public class KalmanFilter {

	/**
	 * number of elements in state
	 */
	protected int m;

	/**
	 * number of measurements
	 */
	protected int n;
	
	/**
	 * number of controls
	 */
	protected int c = 0;

	/**
	 * state matrix (m x 1)
	 */
	protected Matrix X;

	/**
	 * measurement matrix (n x 1)
	 */
	protected Matrix Z;
	
	/**
	 * state control matrix (m x c)
	 */
	protected Matrix B = null;
	
	/**
	 * input control matrix (c x 1)
	 */
	protected Matrix U;

	/**
	 * state transition matrix (m x m)
	 */
	protected Matrix F;

	/**
	 * estimation covariance matrix (m x m)
	 */
	protected Matrix P;

	/**
	 * process noise covariance matrix (m x m)
	 */
	protected Matrix Q;

	/**
	 * measurements as function of state (n X m)
	 */
	protected Matrix H;

	/**
	 * residual matrix (n X 1)
	 */
	protected Matrix Y;

	/**
	 * residual covariance matrix (n x n)
	 */
	protected Matrix S;

	/**
	 * measurement noise covariance matrix (n x n)
	 */
	protected Matrix R;

	/**
	 * Kalman gain matrix (m x n)
	 */
	protected Matrix K;
	
	/**
	 * sets limit on entries in state vector X
	 */
	protected StateLimiter xLimiter = null;
	
	/**
	 * Construct a Kalman filter
	 * @param nStates
	 * @param nMeasures
	 * @param initX
	 * @param initP
	 */
	public KalmanFilter(int nStates, int nMeasures, Matrix initX, Matrix initP) {
		this.m = nStates;
		this.n = nMeasures;
		this.X = initX;
		this.P = initP;

		double[][] d = new double[n][1];
		Z = new Matrix(d);
	}
	
	/**
	 * Construct a Kalman filter
	 * @param F
	 * @param H
	 * @param initX
	 * @param initP
	 */
	public KalmanFilter(Matrix F, Matrix H, Matrix initX, Matrix initP) {
		this.F = F;
		this.H = H;
		this.X = initX;
		this.P = initP;

		m = F.getRows();
		n = H.getRows();

		double[][] d = new double[n][1];
		Z = new Matrix(d);
	}
	
	/**
	 * Construct a Kalman filter with control
	 * @param F
	 * @param H
	 * @param initX
	 * @param initP
	 * @param B
	 */
	public KalmanFilter(Matrix F, Matrix H, Matrix initX, Matrix initP, Matrix B) {
		this(F, H, initX, initP);
		this.B = B;
		c = B.getColumns();
	}
	
	/**
	 * Set lower and upper bound on state values
	 * @param xLimiter
	 */
	public void setStateLimit(StateLimiter xLimiter) {
		this.xLimiter = xLimiter;
	}

	/**
	 * Filter Prediction step
	 * @param processNoiseCovariance
	 */
	public void predict(Matrix processNoiseCovariance) {
		this.Q = processNoiseCovariance;
		
		try {
			X = F.multiply(X);
		} catch (MatrixException e) {
			System.err.print("MatrixException:" + e);
			// e.printStackTrace();
		}
		predictUsingX();
	}
	
	protected void predictUsingX() {
		try {
			if(B != null) {
				X = X.add(B.multiply(U));
			}
			if (xLimiter != null) {
				xLimiter.limit(X);
			}
			P = ((F.multiply(P)).multiply(F.transpose())).add(Q);
		} catch (MatrixException e) {
			System.err.print("MatrixException:" + e);
			// e.printStackTrace();
		}
	}
	
	/**
	 * Filter Prediction step with control
	 * @param control
	 * @param processNoiseCovariance
	 */
	public void predict(Matrix control, Matrix processNoiseCovariance) {
		this.U = control;
		predict(processNoiseCovariance);
	}

	/**
	 * Filter correction (update) step
	 * @param measurements
	 * @param measureNoiseCovariance
	 */
	public void correct(Matrix measurements, Matrix measureNoiseCovariance) {
		this.Z = measurements;
		this.R = measureNoiseCovariance;

		try {
			Y = Z.subtract(H.multiply(X));
		} catch (MatrixException e) {
			System.err.print("MatrixException:" + e);
		}
		correctUsingY();
	}

	protected void correctUsingY() {
		try {
			S = ((H.multiply(P)).multiply(H.transpose())).add(R);
			K = (P.multiply(H.transpose())).multiply(S.inverse());
			
//			System.out.print(toString() + "\n");
			
			X = X.add(K.multiply(Y));
			if (xLimiter != null) {
				xLimiter.limit(X);
			}
			Matrix I = new Matrix(m);
			P = (I.subtract(K.multiply(H))).multiply(P);
		} catch (MatrixException e) {
			System.err.print("MatrixException:" + e);
		}
	}
	
	public float[] getStateVector() {
		float[] v = new float[m];
		for (int i = 0; i < m; i++) {
			v[i] = (float) X.get(i, 0);
		}
		return v;
	}
	
	public float[] getEstimationVariance() {
		float[] v = new float[m];
		for (int i = 0; i < m; i++) {
			v[i] = (float) P.get(i, i);
		}
		return v;
	}
	
	public float[] getResiduals() {
		float[] v = new float[n];
		for (int i = 0; i < n; i++) {
			v[i] = (float) Y.get(i, 0);
		}
		return v;
	}
	
	public float[][] getKalmanGain() {
		float[][] v = new float[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				v[i][j] = (float) K.get(i, j);
			}
		}
		return v;
	}
	
	public float[] getOutputVector() {
		Matrix O;
		try {
			O = H.multiply(X);
		} catch (MatrixException e) {
			e.printStackTrace();
			return null;
		}
		float[] v = new float[n];
		for (int i = 0; i < n; i++) {
			v[i] = (float) O.get(i, 0);
		}
		return v;
	}

	/**
	 * @param b  the b to set
	 * @uml.property  name="b"
	 */
	public void setB(Matrix b) {
		B = b;
	}

	/**
	 * @param f  the f to set
	 * @uml.property  name="f"
	 */
	public void setF(Matrix f) {
		F = f;
	}

	/**
	 * @param h  the h to set
	 * @uml.property  name="h"
	 */
	public void setH(Matrix h) {
		H = h;
	}
	
	/**
	 * current state of the filter
	 * @return
	 */
	public String toString() {
		StringBuffer s = new StringBuffer();
//		int point = this.getClass().getName().lastIndexOf(".");
//		s.append(this.getClass().getName().substring(point + 1) + ": ");
//		s.append("m=" + m);
//		s.append("; n=" + n);
		s.append("\t" + "X=" + X.toString());
		s.append("\t" + "Z=" + Z.toString());
		s.append("\t" + "y=" + Y.toString());
		s.append("\t" + "K=" + K.toString());
		
		s.append("\t" + "P=" + P.toString());
		s.append("\t" + "Q=" + Q.toString());
		s.append("\t" + "R=" + R.toString());
		return s.toString();
	}
}
