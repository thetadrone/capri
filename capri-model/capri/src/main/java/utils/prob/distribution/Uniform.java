package utils.prob.distribution;

public class Uniform extends DistributionBase implements
		ProbabilityDistribution {

	private double min;
	private double max;
	
	public Uniform() {
		this(0, 1);
	}

	public Uniform(double min, double max) {
		this.min = min;
		this.max = max;
		computeMoments();
	}

	public void computeMoments() {
		firstMoment = (min + max) /2;
		secondMoment = moment(2);
		thirdMoment = moment(3);
	}
	
	private double moment(int k) {
		double m = 0;
		for (int i = 0; i < k; i++) {
			m += Math.pow(min, i) * Math.pow(max, k - i);
		}
		return m / (k + 1);
	}
	
	/**
	 * probability density function
	 * 
	 * @param x
	 * @return
	 */
	public double pdf(double x) {
		if (x < min || x > max) {
			return 0;
		}
		return 1 / (max - min);
	}
	
	public double PDF(double x) {
		if (x < min) {
			return 0;
		}
		if (x > max) {
			return 1;
		}
		return (x - min) / (max - min);
	}

	public double meanResidualLife(double age) {
		if (age >= max) {
			return 0;
		}
		return (max - Math.max(min, age)) / 2;
	}

	public int matchMoments(double firstMoment) {
		if (firstMoment <= 0) {
			return 0;
		}
		this.min = 0;
		this.max = 2 * firstMoment;
		computeMoments();
		return 1;
	}

	int matchMoments(double m1, double m2, double m3) {
		return matchMoments(m1);
	}

	int matchMoments(double m1, double m2) {
		return matchMoments(m1);
	}
	
	public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append(super.toString());
        buf.append("; min=").append(min);
        buf.append("; max=").append(max);
        return buf.toString();
    }

}
