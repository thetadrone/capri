package utils.prob.distribution;

public class FittedDistribution extends StatisticalDistribution implements
		ProbabilityDistribution {
	
	protected ProbabilityDistribution distribution;

	public FittedDistribution(String name) {
		super(name);
		distribution = new HyperExponential2();
	}

	public void addSample(double x) {
		super.addSample(x);
		distribution.matchMoments(new double[] {firstMoment, secondMoment, thirdMoment});
	}

	public double PDF(double x) {
		return distribution.PDF(x);
	}

	public int matchMoments(double[] moments) {
		return distribution.matchMoments(moments);
	}

	public double meanResidualLife(double age) {
		return distribution.meanResidualLife(age);
	}

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append(super.toString());
        buf.append("\nFitted Distribution Parameters: ");
        buf.append(distribution.toString());
        return buf.toString();
    }
}
