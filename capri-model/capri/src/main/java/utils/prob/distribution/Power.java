package utils.prob.distribution;

/**
 * Power distribution in the range [a, b] with exponent r and f(a) = alpha.
 * <p>
 * pdf f(x) of the form:
 * <br><br>
 * f(x) = alpha + c (x - a) ^ r
 * <br><br>
 * where c constant s.t. integral from a to b  f(x) dx = 1,
 * <br>
 * a < b, r != -1, and a <= x <= b
 * <p>
 * c = (r + 1) [1 - alpha (b - a)] / (b - a)^(r+1)
 * <p>
 * F(x) = alpha (x - a) + c (x - a)^(r + 1) / (r + 1)
 * 
 * 
 * @author anonymous
 */
public class Power extends DistributionBase implements ProbabilityDistribution {

	protected float a;
	
	protected float b;
	
	protected float r;
	
	protected float alpha;

	
	public Power(float a, float b, float r, float alpha) {
		super();
		this.a = a;
		this.b = b;
		this.r = r;
		this.alpha = alpha;
	}

	/**
	 * probability density function
	 * 
	 * @param x
	 * @return
	 */
	public double pdf(double x) {
		if ((x < a) || (x > b)) {
			return 0;
		}

		float support = b - a;
		float factor = (1 - alpha * support) / (float) Math.pow(support, r + 1);
		float c = (r + 1) * factor;

		float range = (float) x - a;
		float val = alpha + c * (float) Math.pow(range, r);

		return val;
	}
	
	@Override
	public double PDF(double x) {
		if (x < a) {
			return 0;
		}
		if (x > b) {
			return 1;
		}

		float support = b - a;
		float factor = (1 - alpha * support) / (float) Math.pow(support, r + 1);

		float range = (float) x - a;
		float val = range * (alpha + factor * (float) Math.pow(range, r));

		return val;
	}

	@Override
	public double meanResidualLife(double age) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	int matchMoments(double m1, double m2, double m3) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	int matchMoments(double m1, double m2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	int matchMoments(double m1) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(super.toString());
		buf.append("a=" + a +"; ");
		buf.append("b=" + b +"; ");
		buf.append("r=" + r +"; ");
		buf.append("alpha=" + alpha +"; ");
		return buf.toString();
	}

}
