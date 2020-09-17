package utils.prob.distribution;

/**
 * Histogram representation of sampled statistical data.
 * @author anonymous
 */
public class Histogram extends StatisticalDistribution implements
		ProbabilityDistribution {

	/**
	 * Histogram of samples. The histogram consists of numBins bins, numbered
	 * from 0 to numBins-1. Bin i holds samples with value: (binOffset + i *
	 * binSize) <= value < (binOffset + (i+1) * binSize).
	 */
	protected double[] histogram;

	protected int numBins = 10;

	protected double binOffset = 0;

	protected double binSize = 1;

	// TODO: Select good default values for histogram parameters: numBins,
	// binOffset, and binSize.

	/**
	 * Create a new Histogram.
	 * 
	 * @param name
	 */
	public Histogram(String name) {
		super(name);
		histogram = new double[numBins];
	}
	
	/**
	 * Create a new Histogram.
	 * 
	 * @param name
	 * @param numBins
	 * @param binOffset
	 * @param binSize
	 */
	public Histogram(String name, int numBins, double binOffset, double binSize) {
		super(name);
		this.numBins = numBins;
		this.binOffset = binOffset;
		this.binSize = binSize;
		histogram = new double[numBins];
	}

	/**
	 * Add a sample to the histogram.
	 * @param x
	 */
	public void addSample(double x) {
		
		super.addSample(x);
		
		int index = (int) Math.floor((x - binOffset) / binSize);
		index = Math.max(index, 0);
		index = Math.min(index, numBins - 1);
		
		for (int i = 0; i < numBins; i++) {
			histogram[i] *= sampleDiscountingFactor;
		}
		histogram[index]++;
	}
	
	public void reset() {
		super.reset();
		for (int i = 0; i < numBins; i++) {
			histogram[i] = 0;
		}
	}
	
	/**
	 * Probability distribution function (PDF) of X.
	 * The probability that X <= x.
	 * @param x	
	 * @return value in [0, 1].
	 */
	public double PDF(double x) {
		int index = (int) Math.floor((x - binOffset) / binSize);
		if (index < 0) {
			return 0;
		}
		if (index >= numBins) {
			return 1;
		}
		double sumProb = 0;
		if (numSamples > 0) {
			for (int i = 0; i < index; i++) {
				sumProb += histogram[i] / numSamples;
			}
		
			// add fraction of bin index, where the value x lies, assuming
			// uniform distribution within a bin.
			double fraction = (x - binOffset) - (index * binSize);
			sumProb += (fraction / binSize) * histogram[index] / numSamples;
		}
		
		sumProb = Math.max(sumProb, 0);
		sumProb = Math.min(sumProb, 1);
		return sumProb;
	}
	
	/**
	 * Expected residual life of X given current age.
	 * (Returns the age if no data is available.)
	 * @param age
	 * @return average remaining life.
	 */
	public double meanResidualLife (double age) {
		int index = (int) Math.floor((age - binOffset) / binSize);
		if (index < 0) {
			return firstMoment;
		}
		if (index >= numBins) {
			return age;
		}
		double sumAvg = 0;
		double sumProb = 0;
		if (numSamples > 0) {
			
			// Consider fraction of bin index, where the value jobAge lies, assuming
			// uniform distribution within a bin.
			double fraction = (age - binOffset) - (index * binSize);
			double temp = (1 - (fraction / binSize)) * histogram[index] / numSamples;
			temp = Math.max(temp, 0);
			temp = Math.min(temp, 1);
			sumProb += temp;
			sumAvg += (((index + 1) * binSize) - (binSize - fraction) / 2) * temp;
			
			if (index < numBins - 1) {
				for (int i = index + 1; i < numBins; i++) {
					temp = histogram[i] / numSamples;
					sumProb += temp;
					sumAvg += ((i + 0.5d) * binSize)* temp;
				}
			}
		}

		double meanResidual = (sumProb > 0) ? (sumAvg / sumProb) + binOffset - age : age;
		return meanResidual;
	}

	public int matchMoments(double[] moments) {
		return 0;
	}
	
    public String briefTrace()
    {
        return super.briefTrace();
    }
    
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        buf.append("\nHistogram Parameters:");
        buf.append(" numBins=").append(numBins);
        buf.append(" binOffset=").append(binOffset);
        buf.append(" binSize=").append(binSize);
        buf.append("\nHistogram Data:");
//        buf.append(" histogram=[");
        buf.append(" histogram= ");
        for (int i = 0; i < numBins; i++) {
        	buf.append(histogram[i]);
        	if ((numBins > 1) && (i < numBins - 1)) {
        		buf.append(";");
        	}
		}
//        buf.append("]");
        buf.append(" ");
        return buf.toString();
    }
    
}
