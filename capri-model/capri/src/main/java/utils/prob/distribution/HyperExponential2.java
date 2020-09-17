package utils.prob.distribution;

public class HyperExponential2 extends DistributionBase implements ProbabilityDistribution{

	private double p;
	private double mu1;
	private double mu2;
	
	public HyperExponential2() {
		this.p = 1;
		this.mu1 = 1;
		this.mu2 = 1;
		computeMoments();
	}

	public HyperExponential2(double p, double mu1, double mu2) {
		this.p = p;
		this.mu1 = mu1;
		this.mu2 = mu2;		
		computeMoments();
	}

	public void computeMoments() {
		firstMoment = (p * mu1) + ((1-p) * mu2);
		secondMoment = (2 * p * mu1 * mu1) + (2 * (1-p) * mu2 * mu2);
		thirdMoment = (6 * p * mu1 * mu1 * mu1) + (2 * (1-p) * mu2 * mu2 * mu2);
	}
	
	public double PDF(double x) {
		double temp1 = (mu1 != 0) ? p * (1 - Math.exp(-x / mu1)) : 1;
		double temp2 = (mu2 != 0) ? p * (1 - Math.exp(-x / mu2)) : 1;
		return p * temp1 + (1 - p) * temp2;
	}

	public double meanResidualLife(double age) {
		double prob1 = (mu1 != 0) ? Math.exp(-age / mu1) : 0;
		double prob2 = (mu2 != 0) ? Math.exp(-age / mu2) : 0;
		double probSum = prob1 + prob2;
		return (probSum != 0) ? ((prob1 * mu1) + (prob2 * mu2))	/ probSum : age;
	}
	
	public int matchMoments(double firstMoment, double secondMoment, double thirdMoment) {
		
		this.firstMoment = firstMoment;
		this.secondMoment = secondMoment;
		this.thirdMoment = thirdMoment;
		
		double x = firstMoment;
		double y = secondMoment / 2;
		double z = thirdMoment / 6;
		
		double x2 = x * x;
		double y2 = y * y;
		double z2 = z * z;
		double x3 = x2 * x;
		double y3 = y2 * y;
		
		double temp1 = x * y - z;
		double temp2 = x2 - y;
		
		double c = temp1 / temp2;
//		System.out.print("c = "+c+"\n");

		double temp3 = (-3 * x2 * y2) + (4 * y3) + (4 * x3 * z)
				+ (-6 * x * y * z) + (z2);

		double a = (((2 * x * temp2) - temp1) + Math.sqrt(temp3)) / (2 * temp2);
//		System.out.print("a = "+a+"\n");
		
		double b = (temp1 - Math.sqrt(temp3)) / (2 * temp2);
//		System.out.print("b = "+b+"\n");
		
		mu2 = b;
		mu1 = c - mu2;
		p = a / (mu1 - mu2);
		
		if (Double.isNaN(p) || (p < 0) || (p > 1) || Double.isNaN(mu1)
				|| (mu1 < 0) || Double.isNaN(mu2) || (mu2 < 0)) {
			// match only two moments
			return matchMoments(firstMoment, secondMoment);
		}
		
		return 3;
	}
	
	public int matchMoments(double firstMoment, double secondMoment) {
		double m1 = firstMoment;
		double m2 = secondMoment;
		
		double temp = Math.sqrt((m2 / 2) - (m1 * m1));
		this.p = 0.5f;
		mu1 = m1 + temp;
		mu2 = m1 - temp;
		
		if (Double.isNaN(mu1) || (mu1 < 0) || Double.isNaN(mu2) || (mu2 < 0)) {
			// match only one moment
			return matchMoments(firstMoment);
		}		
		
		computeMoments();
		return 2;
	}
	
	public int matchMoments(double firstMoment) {
		if (firstMoment <= 0) {
			return 0;
		}
		
		this.p = 1f;
		mu1 = firstMoment;
		mu2 = 0;
		
		computeMoments();
		return 1;
	}
	
	public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append(super.toString());
        buf.append("; p=").append(p);
        buf.append("; mu1=").append(mu1);
        buf.append("; mu2=").append(mu2);
        return buf.toString();
    }

}
