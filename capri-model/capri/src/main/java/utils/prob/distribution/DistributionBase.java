package utils.prob.distribution;

/**
 * Base Probability distribution function for random variable X.
 * @author anonymous
 */
public abstract class DistributionBase {
	
	protected double firstMoment;
	protected double secondMoment;
	protected double thirdMoment;
	protected boolean isValid = true;
	
	public double mean() {
		return firstMoment;
	}
	
	public double variance() {
		return secondMoment - firstMoment * firstMoment;
	}
	
	public int matchMoments(double[] moments) {
		switch (moments.length) {
		case 3:
			return matchMoments(moments[0], moments[1], moments[2]);
		case 2:
			return matchMoments(moments[0], moments[1]);
		case 1:
			return matchMoments(moments[0]);
		case 0:
			return 0;
		default:
			return matchMoments(moments[0], moments[1], moments[2]);
		}
	}
	
	abstract int matchMoments(double m1, double m2, double m3);
	
	abstract int matchMoments(double m1, double m2);
	
	abstract int matchMoments(double m1);
	
	public String toString()
    {
        StringBuffer buf = new StringBuffer();
		int point = this.getClass().getName().lastIndexOf(".");
		buf.append(this.getClass().getName().substring(point + 1) + ": ");
        buf.append("isValid=").append(isValid);
        buf.append("; mean=").append(mean());
        buf.append("; variance=").append(variance());
        buf.append("; firstMoment=").append(firstMoment);
        buf.append("; secondMoment=").append(secondMoment);
        buf.append("; thirdMoment=").append(thirdMoment);
        return buf.toString();
    }
}
