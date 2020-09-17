package utils.math.numerical.functions;

public class SpecialFunctions {

	/**
	 * Original reference
	 * http://www.swcp.com/~spsvs/resume/StatsCalc/probFunc.java
	 */
	public SpecialFunctions() {
		super();
	}

	
	/**
	 * replacement for UNIX erf() function. Computes: erf(x) = 2/sqrt(pi) *
	 * intergral[0 to x] exp(-t*t) dt
	 * <P>
	 * Algorithm from Abramowitz & Stegun, "Handbook of Mathematical
	 * Functions", National Bureau of Standards, Ninth printing, 1970. Uses
	 * rational approximation 7.1.26, page 299.
	 */
	public static double erf(double x) {
		if (x == 0) {
			return 0;
		}

		if (x < 0) {
			return -erf(-x);
		}
		
		double mp_a[] = { .254829592, -.284496736, 1.421413741, -1.453152027,
				1.061405429 };

		int i;
		double t, p, tval, rslt;

		p = .3275911;
		t = 1. / (1. + p * x);
		tval = 1.;
		rslt = 0.;

		for (i = 0; i < 5; ++i) {
			tval *= t;
			rslt += mp_a[i] * tval;
		}

		rslt *= Math.exp(-x * x);
		rslt = 1. - rslt;

		return (rslt);
	}

	
	/**
	 * replacement for UNIX erf() function. Computes: erfc(x) = 1 - erf(x) =
	 * 1 - 2/sqrt(pi) * intergral[0 to x] exp(-t*t) dt
	 * <P>
	 * Algorithm from Abramowitz & Stegun, "Handbook of Mathematical
	 * Functions", National Bureau of Standards, Ninth printing, 1970. Uses
	 * rational approximation 7.1.26, page 299.
	 */
	public static double erfc(double x) {
		double mp_a[] = { .254829592, -.284496736, 1.421413741, -1.453152027,
				1.061405429 };

		int i;
		double t, p, tval, rslt;

		p = .3275911;
		t = 1. / (1. + p * x);
		tval = 1.;
		rslt = 0.;

		for (i = 0; i < 5; ++i) {
			tval *= t;
			rslt += mp_a[i] * tval;
		}

		rslt *= Math.exp(-x * x);

		return (rslt);
	}
	

	/**
	 * Originally written by S.C. Pohlig, adapted by J.N. Sanders
	 * <P>
	 * This function returns an approximation to the inverse of the standard
	 * normal probability distribution. The approximation error is less than
	 * 4.5e-4. The approximation formula is from M. Abramowitz and I. A.
	 * Stegun, Handbook of Mathematical Functions, eqn. 26.2.23, Dover
	 * Publications, Inc.
	 * <P>
	 * The C language error function returns erf(x) = (2/sqrt(pi)) *
	 * Integral(0,x) of exp(-t*t)dt, which gives erf(infinity) = 1. In
	 * essence, this gives the area under the curve between -x and +x,
	 * normalized to 1. However, this function (inverf), solves for the
	 * inverse of (1/sqrt(pi)) * Integral(-infinity, x) of exp(-t*t)dt. As a
	 * result, the symmetric inverse is: x = inverf(erf(x) / 2. + .5)
	 * <P>
	 * Given the integral of a unit variance gaussian, from -infinity to x,
	 * normalized such that the integral to +infinity is 1, multiply this
	 * result by sqrt(2) to obtain x.
	 */
	public static double inverf(double p) /** 0 <= p <= 1 * */ {
		double C0 = 2.515517;
		double C1 = 0.802853;
		double C2 = 0.010328;
		double D1 = 1.432788;
		double D2 = 0.189269;
		double D3 = 0.001308;
		double MAX_SIGMA = 7;

		double t1, t2, q, x;

		if (p >= 1.)
			return (MAX_SIGMA);
		else if (p <= 0.)
			return (-MAX_SIGMA);
		else if (p == 0.5)
			return (0.0);

		if (p < 0.5)
			q = p;
		else
			q = 1.0 - p;

		t2 = -2.0 * Math.log(q);
		t1 = Math.sqrt(t2);

		x = t1 - (C0 + C1 * t1 + C2 * t2)
				/ (1.0 + D1 * t1 + D2 * t2 + D3 * t1 * t2);
		x = x / Math.sqrt(2.);

		if (p < 0.5)
			return (-x);
		else
			return (x);

	}
	

	/**
	 * Returns the natural logarithm of the gamma function. x > 0
	 */
	public static double gammaln(double xx) {
		if (xx == 1 || xx == 2) {
			return 0;
		}
		if (xx == 0) {
			return Double.POSITIVE_INFINITY;
		}
		
		int j;
		double x, tmp, ser;
		double cof[] = { 76.18009173, -86.50532033, 24.01409822, -1.231739156,
				0.120858003e-2, -0.536382e-5 };

		x = xx - 1.;
		tmp = x + 5.5;
		tmp -= (x + 0.5) * Math.log(tmp);
		ser = 1.;

		for (j = 0; j < 6; ++j) {
			x += 1.;
			ser += cof[j] / x;
		}

		return (-tmp + Math.log(2.50662827465 * ser));
	}

	
	/**
	 * Returns the incomplete gamma function, Q(a,x), -2 for invalid
	 * arguments, -1 for non-convergence. The Chi^2 distribution, Q(x^2|v) =
	 * Q(v/2, x^2/2), where x^2 is chi^2, and v is the number of degrees of
	 * freedom.
	 * <P>
	 * Ref: Numerical Recipes in C, Press et al, pg 171-177.
	 */
	public static double gammaq(double a, double x) {
		int i;
		double ln_gamma, ap, del, sum, g, gold, fac, b0, b1, a0;
		double a1, an, ana, anf, prob;

		if ((x < 0.) || (a <= 0.))
			return (-2);

		if (x < a + 1.) /** series representation * */
		{
			if (x == 0.)
				return (1.);
			ln_gamma = gammaln(a);
			ap = a;
			del = sum = 1.0 / a;
			for (i = 0; i < 100; ++i) {
				ap += 1.;
				del *= x / ap;
				sum += del;
				if (Math.abs(del) < Math.abs(sum) * 3.e-7) {
					prob = sum * Math.exp(-x + a * Math.log(x) - ln_gamma);
					return (1 - prob);
				}
			}
			return (-1);
			/** no convergence, return an error * */
		}

		else /** continued fraction representation * */
		{
			gold = 0.;
			fac = 1.;
			b1 = 1.;
			b0 = 0.;
			a0 = 1.;
			a1 = x;
			ln_gamma = gammaln(a);
			for (i = 1; i <= 100; ++i) {
				an = (double) i;
				ana = an - a;
				a0 = (a1 + a0 * ana) * fac;
				b0 = (b1 + b0 * ana) * fac;
				anf = an * fac;
				a1 = x * a0 + anf * a1;
				b1 = x * b0 + anf * b1;
				if (a1 != 0) {
					/** renormalize to prevent overflow of partial num & denom * */
					fac = 1. / a1;
					g = b1 * fac;
					if (Math.abs((g - gold) / g) < 3.e-7) {
						prob = Math.exp(-x + a * Math.log(x) - ln_gamma) * g;
						return (prob);
					}
					gold = g;
				}
			}

			return (-1);
			/** no convergence, return an error * */
		}
	}

	
	/**
	 * Returns returns the probability function Q(lambda) used in the
	 * Kolmogorov-Smirnov Test
	 * <P>
	 * Qks(lambda) = 2 SUM(j=1, infinity) pow(-1, j-1) *
	 * exp(-2*sqr(j)*sqr(lambda))
	 */
	public static double q_kolmogorov(double lambda) {
		int i;
		double lambda_sqr_2, fac, sum, term, termbf;

		fac = 2.;
		sum = 0.;
		termbf = 0.;
		lambda_sqr_2 = -2. * (lambda * lambda);

		for (i = 1; i <= 100; ++i) {
			term = fac * Math.exp((i * i) * lambda_sqr_2);
			sum += term;
			if ((Math.abs(term) <= termbf * .001)
					|| (Math.abs(term) <= sum * 1.e-8))
				return (sum);
			fac = -fac;
			termbf = Math.abs(term);
		}

		return (1.);
		/** failed to converge * */
	}
	
	/**
	 * The (upper) incomplete gamma function for a=0 and x>0,
	 * <P>
	 * gamma(a,x) = integral from x to infinity y^(a-1) e^-y dy ,
	 * <P>
	 * evaluated using a series
	 * <P>
	 * gamma(0,x) = -E - ln(x) - sum k=1 to infinity (-x)^k / (k k!) ,
	 * <P>
	 * where E is the Euler-Mascheroni constant.
	 */
	public static double gammaIncompleteZero(double x) {
		// x negative: has imaginary component, not handled
		if (x < 0) {
			return -1;
		}
		
		// x zero: undefined, infinite
		if (x == 0) {
			return Double.POSITIVE_INFINITY;
		}
		
		// x large: numerical errors
		//TODO: fix divergence when x > 10.
		if (x > 10) {
			return 0;
		}
		
		double EM = 0.57721566490153286061;
		double g = EM + Math.log(x);
		
		/*
		 * Method I: add one term at a time
		 */
//		int iter = 101;
//		double prod = - x;
//		double sum = prod;
//		for (int k = 2; k < iter; k++) {
//			prod *= (-x) / k;
//			sum += prod / k;
//		}
		
		/*
		 * Method II: add two terms (plus and minus) at a time (more stable)
		 */
		int iter = 100;
		double prod = x;
		double sum = 0;
		for (int k = 1; k < iter; k+=2) {
			int k1 = k + 1;
			int k2 = k + 2;
			double term1 = x / (k1 * k1);
			double term2 = 1.0 / k;
			sum += prod * (term1 - term2);
			prod *= (x * x) / (k1 * k2);
		}
		
		g += sum;
		return -g;
	}

}
