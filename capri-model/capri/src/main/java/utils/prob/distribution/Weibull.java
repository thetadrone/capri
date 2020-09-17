package utils.prob.distribution;

import utils.math.numerical.functions.SpecialFunctions;

/**
 * <b> Weibull distribution </b>
 * <br>
 * f(t) = (beta / eta) (t / eta)^(beta - 1) exp[-(t / eta)^beta]
 * <br>
 * where t >= 0, beta > 0 shape parameter, and eta > 0 scale parameter
 * <br>
 * F(t) = 1 - exp[-(t / eta)^beta]
 * <br>
 * E[T] = eta G[1 + 1/beta], where G is the Gamma function G[n] = int 0 to inf exp[-x] x^(n-1) dx
 * <br>
 * V[T] = eta^2 [ G[1 + 2/beta] - G^2(1 + 1/beta] ]
 * <br>
 * E[T^m] = eta^m G[1 + m/beta]
 * <p>
 * t = eta [-ln(1 - x)]^(1/beta), where x is a uniform random in [0,1)
 * <p>
 * A shifted Weibull distribution is given by
 * f(t) = (beta / eta) ((t - gamma) / eta)^(beta - 1) exp[-((t - gamma) / eta)^beta]
 * <br>
 * where t >= gamma and gamma is a  location parameter
 */
public class Weibull extends DistributionBase implements ProbabilityDistribution {
	
	/** eta > 0 */
	protected double scale;
	
	/** beta > 0 */
	protected double shape;
	
	/** gamma */
	protected double location = 0;


	public Weibull(double scale, double shape) {
		super();
		if (!checkValidity(scale, shape)) {
			return;
		}
		this.scale = scale;
		this.shape = shape;

		computeMoments();
	}

	public Weibull() {
		this(1, 1);
	}
	
	public void computeMoments() {
		if (!isValid) {
			return;
		}
		
		double term = scale;
		firstMoment = location + term * Math.exp(SpecialFunctions.gammaln(1 + 1 / shape));
		
		term *= scale;
		secondMoment = term * Math.exp(SpecialFunctions.gammaln(1 + 2 / shape));
		
		term *= scale;
		thirdMoment = term * Math.exp(SpecialFunctions.gammaln(1 + 3 / shape));
	}

	@Override
	int matchMoments(double m1, double m2, double m3) {
		return matchMoments(m1, m2);
	}

	@Override
	int matchMoments(double m1, double m2) {

		//TODO: FIX THIS
		
		return 1;
	}

	@Override
	int matchMoments(double m1) {
		// need at least two moments
		return 0;
	}

	@Override
	public double PDF(double x) {
		if (x <= location) {
			return 0;
		}
		double term = (x - location) / scale;
		double cum = 1 - Math.exp(-Math.pow(term, shape));
		return cum;
	}
	
	/**
	 * probability density function
	 * 
	 * @param x
	 * @return
	 */
	public double pdf(double x) {
		double term = (x - location) / scale;
		double pdf = (shape / scale) * Math.pow(term, shape - 1) * Math.exp(-Math.pow(term, shape));
		if (Double.isNaN(pdf) || pdf < 0) {
			pdf = 0;
		}
		return pdf;
	}

	@Override
	public double meanResidualLife(double age) {
		
		// TO DO: FIX THIS
		
		/**
		 * m(x) = [ int from x to inf S(t) dt ] / S(x)
		 * 
		 * where x is age and S(t) is the survival function, S(t) = 1 - CDF(t)
		 * 
		 * For Weibull,
		 * S(t) = exp[-(t / eta)^beta]
		 * 
		 * numerator of m(x) evaluates to:
		 * eta * gamma_incomplete(1/beta, (x/eta)^beta) / beta
		 * 
		 */

		return 0;
	}
	
	private boolean checkValidity(double scale, double shape) {
		isValid = scale > 0 && shape > 0;
		return isValid;
	}
	
	public double getScale() {
		return scale;
	}

	public double getShape() {
		return shape;
	}
	
	public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append(super.toString());
        buf.append("; scale=").append(scale);
        buf.append("; shape=").append(shape);
        return buf.toString();
    }

}
