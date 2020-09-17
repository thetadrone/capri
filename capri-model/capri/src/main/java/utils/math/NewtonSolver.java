package utils.math;

/**
 * Solves the equation f(X)=0 using Newton-Raphson method, where X is a vector
 * and f() is a scalar function. <br>
 * X may be bounded in a region defined by lower and upper bound vectors. <br>
 * The function f() as well as its derivative (gradient) g() are to be provided.
 * 
 * @author anonymous
 * 
 */
public class NewtonSolver {

	protected ScalarFunction f;
	protected ScalarGradientFunction g;

	protected boolean isBounded;
	protected float[] lowerBound;	
	protected float[] upperBound;
	
	protected boolean isDebugging = false;

	public NewtonSolver(ScalarFunction f, ScalarGradientFunction g) {
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

			float fValue = f.eval(x);
			float[] gValue = g.eval(x);
			for (int j = 0; j < p; j++) {
				x[j] -= fValue / gValue[j];
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
		float[] ratio = new float[p];
		applyBounds(x);
		
		int i = 0;
		while (!withinTolerance && i < numIterations) {
			
			if (isDebugging) {
				System.out.print(i + ": " + this.toString(x) + "\n");
			}

			float fValue = f.eval(x);
			float[] gValue = g.eval(x);
			
			for (int j = 0; j < p; j++) {
				ratio[j] = fValue / gValue[j];
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

		s.append("f(x)=" + f.eval(x));
		s.append("\t");
		
		float[] fPrime = g.eval(x);
		s.append("g(x)=[ ");
		for (int i = 0; i < p; i++) {
			s.append(fPrime[i] + " ");
		}
		s.append("]");

		return s.toString();
	}

}
