package utils.prob.distribution;

public class Exponential extends DistributionBase implements
		ProbabilityDistribution {

	private double mu;
	
	public Exponential() {
		this.mu = 1;
		computeMoments();
	}

	public Exponential(double mu) {
		this.mu = mu;
		computeMoments();
	}

	public void computeMoments() {
		firstMoment = mu;
		secondMoment = 2 * mu * mu;
		thirdMoment = 6 * mu * mu * mu;
	}
	
	public double PDF(double x) {
		return (1 - Math.exp(-x / mu));
	}

	public double meanResidualLife(double age) {
		return mu;
	}

	public int matchMoments(double firstMoment) {
		if (firstMoment <= 0) {
			return 0;
		}
		this.mu = firstMoment;
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
        buf.append("; mu=").append(mu);
        return buf.toString();
    }

}
