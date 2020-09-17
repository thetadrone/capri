package utils.prob.distribution;

/**
 * Statistical distribution based on samples of a random variable X.
 * @author anonymous
 */
public abstract class StatisticalDistribution{

	/**
	 * Name (identification) of random variable.
	 */
	public String name;

	/**
	 * Sum of samples divided by the number of samples.
	 */
	public double firstMoment;

	/**
	 * Sum of samples squared divided by the number of samples.
	 */
	public double secondMoment;

	/**
	 * Sum of samples cubed divided by the number of samples.
	 */
	public double thirdMoment;

	/**
	 * Number of samples.
	 */
	public double numSamples = 0;

	/**
	 * Minimum sample.
	 */
	public double minSample;

	/**
	 * Maximum sample.
	 */
	public double maxSample;
	
	/**
	 * Factor for discounting older samples using geometric discounting.
	 */
	protected double sampleDiscountingFactor = 1;
	
	/**
	 * Create a statistical distribution for named data.
	 * @param name
	 */
	public StatisticalDistribution(String name) {
		this.name = name;
	}

	public double mean() {
		return firstMoment;
	}

	public double variance() {
		return secondMoment - (firstMoment * firstMoment);
	}

	public double getSampleDiscountingFactor() {
		return sampleDiscountingFactor;
	}

	public void setSampleDiscountingFactor(double sampleDiscountingFactor) {
		this.sampleDiscountingFactor = sampleDiscountingFactor;
	}
	
	public abstract double meanResidualLife(double age);
	
	public void addSample(double x){
		double x2 = x * x;
		double x3 = x2 * x;
		
		if (numSamples == 0) {
			firstMoment = x;
			secondMoment = x2;
			thirdMoment = x3;
			minSample = maxSample = x;
		}
		else {
			// compute mean values in two steps to avoid loss of numerical significance
			numSamples *= sampleDiscountingFactor;
			double temp = numSamples / (numSamples + 1);

			firstMoment = (firstMoment * temp) + (x / (numSamples + 1));
			secondMoment = (secondMoment * temp) + (x2 / (numSamples + 1));
			thirdMoment = (thirdMoment * temp) + (x3 / (numSamples + 1));
			
			minSample = Math.min(minSample, x);
			maxSample = Math.max(maxSample, x);
		}
		
		numSamples++;
	}
	
	public void reset() {
		firstMoment = 0;
		secondMoment = 0;
		thirdMoment = 0;
		numSamples = 0;
		minSample = 0;
		maxSample = 0;
	}
	
    public String briefTrace()
    {
    	StringBuilder buf = new StringBuilder();
        buf.append("name=" + name + ", ");
        buf.append("mean=" + mean() + ", ");
        buf.append("stdDev=" + Math.sqrt(variance()) + ", ");
        buf.append("numSamples=" + numSamples + ", ");
        buf.append("minSample=" + minSample  + ", ");
        buf.append("maxSample=" + maxSample);
        return buf.toString();
    }
	
    public String toString()
    {
    	StringBuilder buf = new StringBuilder();
		int point = this.getClass().getName().lastIndexOf(".");
		buf.append("\n" + this.getClass().getName().substring(point + 1) + ":");
        buf.append(" Name=").append(name);
        buf.append(" sampleDiscountingFactor=").append(sampleDiscountingFactor);
        buf.append("\nStatistics:");
        buf.append(" mean=").append(mean());
        buf.append(" variance=").append(variance());
        buf.append(" firstMoment=").append(firstMoment);
        buf.append(" secondMoment=").append(secondMoment);
        buf.append(" thirdMoment=").append(thirdMoment);
        buf.append(" numSamples=").append(numSamples);
        buf.append(" minSample=").append(minSample);
        buf.append(" maxSample=").append(maxSample);
        return buf.toString();
    }

}
