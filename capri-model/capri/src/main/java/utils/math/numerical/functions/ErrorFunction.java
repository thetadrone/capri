package utils.math.numerical.functions;

// TODO: Check accuracy

/**
 * erf() function
 * <p>
 * erf(z) is the "error function" encountered in integrating the normal
 * distribution (which is a normalized form of the Gaussian function). It is an
 * entire function defined by erf(z)=2/(sqrt(pi)) integral (0 to t) [ z e^(-t^2)
 * dt].
 * 
 * @author anonymous
 */
public class ErrorFunction {

	/**
	 * erf(z)
	 */
	public static double eval(double z) {
		int numTerms = 100;
		
		if (z == 0) {
			return 0;
		}

		if (z < 0) {
			return -eval(-z);
		}

		// Maclaurin series
		double term1 = z;
		double term2 = term1 * termRatio(z, 0);
		double sum = term1 + term2;
		for (int i = 1; i < numTerms; i += 2) {
			term1 = term2 * termRatio(z, i);
			term2 = term1 * termRatio(z, i + 1);
			sum += (term1 + term2);
		}
		sum *= 2 / Math.sqrt(Math.PI);
		sum = Math.min(sum, 1);
		sum = Math.max(sum, 0);
		return sum;
	}
	
	private static double termRatio (double z, int n) {
		double r = (2. * n + 1.) / ((n + 1.) * (2. * n + 3.));
		double temp = Math.pow(z, 2) * r;
		temp = Math.min(temp, 1);
		return - temp;
	}

}
