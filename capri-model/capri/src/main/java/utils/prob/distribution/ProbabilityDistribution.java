
package utils.prob.distribution;

/**
 * Probability distribution function for random variable X.
 * @author anonymous
 */
public interface ProbabilityDistribution {
	
	/**
	 * The mean of the random variable X.
	 * @return E[X].
	 */
	double mean ();

	/**
	 * The variance of the random variable X.
	 * @return V[X] = E[X^2] - (E[X])^2.
	 */
	double variance();
	
	/**
	 * Probability distribution function, PDF(x) of random variable X.
	 * The probability that X <= x.
	 * @param x
	 * @return PDF(x)
	 */
	double PDF (double x);
	
	/**
	 * Expected residual life of X given a job age.
	 * @param age
	 * @return E[X | age]
	 */
	double meanResidualLife (double age);
	
	/**
	 * Match moments.
	 * @param moments, i<sup>th</sup> element is the i<sup>th</sup> moment.
	 * @return number of moments used in matching.
	 */
	int matchMoments(double[] moments);

}
