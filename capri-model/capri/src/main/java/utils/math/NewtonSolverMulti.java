package utils.math;

import utils.math.linearalgebra.Matrix;
import utils.math.linearalgebra.MatrixException;

public class NewtonSolverMulti {

	protected MultiFunction f;
	protected JacobianFunction g;

	protected boolean isBounded;
	protected float[] lowerBound;	
	protected float[] upperBound;
	
	protected boolean isDebugging = false;

	public NewtonSolverMulti(MultiFunction f, JacobianFunction g) {
		super();
		this.f = f;
		this.g = g;
		this.isBounded = false;
	}

	public void setBounds(float[] lowerBound, float[] upperBound) {
		if (lowerBound == null || upperBound == null) {
			this.isBounded = false;
		}
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.isBounded = true;
	}
	
	public float[] solve(int numIterations, float[] initValue) {
		int p = initValue.length;
		float[] x = new float[p];
		for (int i = 0; i < p; i++) {
			x[i] = initValue[i];
		}
		applyBounds(x);

		for (int i = 0; i < numIterations; i++) {

			if (isDebugging) {
				System.out.print(i + ": " + this.toString(x) + "\n");
			}

			float[] fValue = f.eval(x);
			float[][] gValue = g.eval(x);

			double[][] gd = new double[p][p];
			for (int k = 0; k < p; k++) {
				for (int j = 0; j < p; j++) {
					gd[k][j] = gValue[k][j];
				}
			}
			Matrix jacobian = new Matrix(gd);
			Matrix jacobianInverse;
			try {
				jacobianInverse = jacobian.inverse();
			} catch (MatrixException e) {
				System.err.print("Jacobian is not invertible.\n");
				e.printStackTrace();
				return x;
			}

			for (int j = 0; j < p; j++) {
				double temp = 0;
				for (int k = 0; k < p; k++) {
					temp += jacobianInverse.get(j, k) * fValue[k];
				}
				x[j] -= temp;
			}
			
			applyBounds(x);
		}

		if (isDebugging) {
			System.out.print(numIterations + ": " + this.toString(x) + "\n");
		}

		return x;
	}
	
	public float[] solve(int numIterations, double tolerance, float[] initValue) {
		int p = initValue.length;
		float[] x = new float[p];
		for (int i = 0; i < p; i++) {
			x[i] = initValue[i];
		}
		
		boolean withinTolerance = false;
		double[] ratio = new double[p];
		applyBounds(x);

		int i = 0;
		while (!withinTolerance && i < numIterations) {

			if (isDebugging) {
				System.out.print(i + ": " + this.toString(x) + "\n");
			}

			float[] fValue = f.eval(x);
			float[][] gValue = g.eval(x);

			double[][] gd = new double[p][p];
			for (int k = 0; k < p; k++) {
				for (int j = 0; j < p; j++) {
					gd[k][j] = gValue[k][j];
				}
			}
			Matrix jacobian = new Matrix(gd);
			Matrix jacobianInverse;
			try {
				jacobianInverse = jacobian.inverse();
			} catch (MatrixException e) {
				System.err.print("Jacobian is not invertible.\n");
				e.printStackTrace();
				return x;
			}

			for (int j = 0; j < p; j++) {
				double temp = 0;
				for (int k = 0; k < p; k++) {
					temp += jacobianInverse.get(j, k) * fValue[k];
				}
				ratio[j] = temp;
				double relError = (x[j] != 0) ? Math.abs(((x[j] - ratio[j]) / x[j]) - 1) : 1;
				withinTolerance |= relError <= tolerance;
			}
			
			if (!withinTolerance) {
				for (int j = 0; j < p; j++) {
					x[j] -= ratio[j];
				}
			}
			
			applyBounds(x);
			i++;
		}

		if (isDebugging) {
			System.out.print(i + ": " + this.toString(x) + "\n");
		}

		return x;
	}

	public void setDebugging(boolean isDebugging) {
		this.isDebugging = isDebugging;
	}
	
	protected void applyBounds(float[] x) {
		if (isBounded) {
			for (int i = 0; i < x.length; i++) {
				x[i] = Math.min(x[i], upperBound[i]);
				x[i] = Math.max(x[i], lowerBound[i]);
			}
		}
	}

	public String toString(float[] x) {
		int p = x.length;
		StringBuffer s = new StringBuffer();

		s.append("x=[ ");
		for (int i = 0; i < p; i++) {
			s.append(x[i] + " ");
		}
		s.append("]");
		s.append("\t");

		float[] fv = f.eval(x);
		s.append("f(x)=[ ");
		for (int j = 0; j < p; j++) {
			s.append(fv[j] + " ");
		}
		s.append("]");
		s.append("\t");
		
		float[][] fPrime = g.eval(x);
		s.append("g(x)=[ ");
		for (int i = 0; i < p; i++) {
			s.append("[ ");
			for (int j = 0; j < p; j++) {
				s.append(fPrime[i][j] + " ");
			}
			s.append("] ");
		}
		s.append("]");

		return s.toString();
	}

}
