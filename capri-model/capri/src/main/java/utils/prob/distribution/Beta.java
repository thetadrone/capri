package utils.prob.distribution;

import utils.math.numerical.functions.BetaFunction;

/**
 * Beta distribution
 * http://en.wikipedia.org/wiki/Beta_distribution
 * 
 * @author anonymous
 */
public class Beta extends DistributionBase implements ProbabilityDistribution {
	
	/**
	 * alpha shape parameter (alpha > 0)
	 */
	private double alpha;
	
	/**
	 * beta shape parameter (beta > 0)
	 */
	private double beta;
	
	/**
	 * used to calculate complete and incomplete beta functions
	 */
	private BetaFunction betaFunction;


	public Beta(double alpha, double beta) {
		super();
		if (!checkValidity(alpha, beta)) {
			return;
		}
		this.alpha = alpha;
		this.beta = beta;

		computeMoments();
		betaFunction = new BetaFunction();
	}

	public Beta() {
		this(1, 1);
	}
	
	public void computeMoments() {
		if (!isValid) {
			return;
		}
		firstMoment = alpha / (alpha + beta);
		secondMoment = firstMoment * (alpha + 1) / (alpha + beta + 1);
		thirdMoment = secondMoment * (alpha + 2) / (alpha + beta + 2);
	}

	@Override
	int matchMoments(double m1, double m2, double m3) {
		return matchMoments(m1, m2);
	}

	@Override
	int matchMoments(double m1, double m2) {
		double var = m2 - m1 * m1;
		if (m1 < 0 || m1 > 1 || var < 0 || var >= m1 * (1 - m1)) {
			return 0;
		}
		double temp = (m1 * (1 - m1) / var) - 1;
		temp = Math.max(temp, Double.MIN_VALUE);
		alpha = m1 * temp;
		beta = (1 - m1) * temp;
		if (checkValidity(alpha, beta)) {
			computeMoments();
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	int matchMoments(double m1) {
		// need at least two moments
		return 0;
	}

	@Override
	public double PDF(double x) {
		return betaFunction.regularizedIncomplete(x, alpha, beta);
	}
	
	/**
	 * probability density function
	 * 
	 * @param x
	 * @return
	 */
	public double pdf(double x) {
		double betax = Math.pow(x, alpha - 1.) * Math.pow((1. - x), (beta - 1.));
		double betac = betaFunction.complete(alpha, beta);
		double pdf = betax / betac;
		if (Double.isNaN(pdf) || pdf < 0) {
			pdf = 0;
		}
		return pdf;
	}

	@Override
	/**
	 * "Representing the Mean Residual Life in Terms of the Failure Rate" 
	 * by R. C. Gupta and D. M. Bradley
	 */
	public double meanResidualLife(double age) {
		double survival = 1 - PDF(age);
		if (survival == 0) {
			return 0;
		}
		double failureRate = pdf(age) / survival;
		if (Double.isInfinite(failureRate)) {
			return failureRate;
		}
		double m = failureRate * (age * (1 - age)) / (alpha + beta);
		m += firstMoment - age;
		return m;
	}
	
	private boolean checkValidity(double alpha, double beta) {
		isValid = alpha > 0 && beta > 0;
		return isValid;
	}
	
	public double getAlpha() {
		return alpha;
	}

	public double getBeta() {
		return beta;
	}
	
	public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append(super.toString());
        buf.append("; alpha=").append(alpha);
        buf.append("; beta=").append(beta);
        return buf.toString();
    }

}
