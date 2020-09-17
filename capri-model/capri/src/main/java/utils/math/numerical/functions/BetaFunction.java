package utils.math.numerical.functions;

/**
 * Beta related functions
 * 
 * @author anonymous
 */
public class BetaFunction {
	
	/**
	 * the method of continued fraction is much more numerically stable than
	 * series expansion
	 */
	public boolean incompleteByContinuedFraction = true;
	
	
	public BetaFunction() {
		super();
	}

	/**
	 * complete beta function, B(a,b)
	 * @param a
	 * @param b
	 * @return
	 */
	public double complete(double a, double b) {
		return Math.exp(SpecialFunctions.gammaln(a) + SpecialFunctions.gammaln(b)
						- SpecialFunctions.gammaln(a + b));
	}
	
	/**
	 * I_x(a,b) = B(x;a,b) / B(a,b)
	 * where B(x;a,b) is the incomplete and B(a,b) is the complete beta function
	 * 
	 * @param x
	 * @param a
	 * @param b
	 * @return
	 */
	public double regularizedIncomplete(double x, double a, double b) {
		if (incompleteByContinuedFraction) {
			return regIncByContFraction(x, a, b, true);
		} else {
			return regIncBySeriesExpansion(x, a, b, true);
		}
	}
	
	/**
	 * B(x;a,b) is the incomplete beta function
	 * @param x
	 * @param a
	 * @param b
	 * @return
	 */
	public double incomplete(double x, double a, double b) {
		if (incompleteByContinuedFraction) {
			return regIncByContFraction(x, a, b, false);
		} else {
			return regIncBySeriesExpansion(x, a, b, false);
		}
	}
	
	/**
	 * calculate regularized incomplete beta function using continued fraction representation
	 * 
	 * see "Numerical Recipes" by Press, Flannery, Teukolsky, and Vetterling, 
	 * Cambridge University Press
	 * 
	 * @param x (0 <= x <= 1)
	 * @param a
	 * @param b
	 * @return
	 */
	private double regIncByContFraction(double x, double a, double b, boolean isRegularized) {
		double bt = 0;
		if (x == 0 || x == 1) {
			bt = 0;
		} else {
			bt = Math.exp((a * Math.log(x)) + (b * Math.log(1 - x)));
			if (isRegularized && bt != 0) {
				bt /= complete(a, b);
			}
		}

		double betai = 0;
		if (x < (a + 1) / (a + b + 2)) {
			betai = bt * betaCF(x, a, b) / a;
		} else {
			betai = 1 - bt * betaCF(1 - x, b, a) / b;
		}

		return betai;
	}
	
	private static double betaCF(double x, double a, double b) {
		int itmax = 100;
		double eps = 3E-7;
		double am = 1;
		double bm = 1;
		double az = 1;
		double qab = a + b;
		double qap = a + 1;
		double qam = a - 1;
		double bz = 1 - qab * x / qap;
		
		for (int m = 1; m <= itmax; m++) {
			double tm = m + m;
			
			// even step
			double d = m * (b - m) * x / ((qam + tm) * (a + tm));
			double ap = az + d * am;
			double bp = bz + d * bm;
			
			// odd step
			d = - (a + m) * (qab + m) * x / ((a + tm) * (qap + tm));
			double app = ap + d * az;
			double bpp = bp + d * bz;
			
			// save old answer
			double aold = az;
			
			// renormalize to prevent overflows
			am = ap / bpp;
			bm = bp / bpp;
			az = app / bpp;
			bz = 1;
			
			// check tolerance
			if (Math.abs(az - aold) < eps * Math.abs(az)) {
				return az;
			}
		}
		
		return az;
	}
	
	private double regIncBySeriesExpansion(double x, double a, double b, boolean isRegularized) {
		int i;
		int numIter = 100;

		double betax = 1. / a;
		double fact = 1.;
		for (i = 1; i < numIter; ++i) {
			fact *= (i - b) * x / i;
			betax += fact / (a + i);
		}

		betax *= Math.pow(x, a);
		
		if (isRegularized && betax != 0) {
			betax /= complete(a, b);
		}
		
		return betax;
	}

}
