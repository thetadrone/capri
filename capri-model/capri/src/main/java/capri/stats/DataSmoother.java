package capri.stats;

import config.Configurator;

/**
 * Processes point data and performs smoothing for consumption by the filter
 * 
 * @author anonymous
 */

public class DataSmoother {

	/**
	 * factor used to smooth data
	 */
	protected float smoothFactor;

	/**
	 * number of bins over bid range
	 */
	protected int numBidBins;

	/**
	 * smoothed average bid
	 */
	protected double[] avgBid;

	/**
	 * smoothed average service time
	 */
	protected double[] avgServiceTime;

	/**
	 * smoothed average response time
	 */
	protected double[] avgResponseTime;
	
	protected int[] count;
	
	protected float smoothFactorDefault = 0.05f;
	protected int numBidBinsDefault = 40;

	/**
	 * constructor of a {@link DataSmoother}
	 * 
	 * @param smoothFactor
	 * @param numBidBins
	 */
	public DataSmoother(float smoothFactor, int numBidBins) {
		this.smoothFactor = smoothFactor;
		this.numBidBins = numBidBins;

		init();
	}
	
	/**
	 * constructor of a {@link DataSmoother} using configuration file
	 */
	public DataSmoother(String cfgFileName) {
		if (cfgFileName == null || cfgFileName.isEmpty()) {
			cfgFileName = "capri.cfg";
		}

		Configurator cfg = new Configurator(cfgFileName);
		
		float smoothFactor = cfg.getFloatValue("smootherFactor");
		int numBidBins = cfg.getIntValue("smootherNumBins");
		
		if (smoothFactor < 0 || numBidBins <= 0) {
			smoothFactor = smoothFactorDefault;
		}
		if (numBidBins <= 0) {
			numBidBins = numBidBinsDefault;
		}
		
		this.smoothFactor = smoothFactor;
		this.numBidBins = numBidBins;
		
		init();
	}
	
	/**
	 * initializer
	 * 
	 */
	protected void init() {
		
		this.avgBid = new double[numBidBins];
		this.avgServiceTime = new double[numBidBins];
		this.avgResponseTime = new double[numBidBins];
		
		this.count = new int[numBidBins];
	}

	/**
	 * add a data point
	 * 
	 * @param bid
	 * @param servTime
	 * @param respTime
	 */
	public void addData(float bid, float servTime, float respTime) {
		int index = getBinIndex(bid);
		
		count[index]++;
		float f = (count[index] < (int) 1 / smoothFactor) ? 1f / count[index] : smoothFactor;
		
		avgBid[index] = ((1 - f) * avgBid[index]) + (f * bid);
		avgServiceTime[index] = ((1 - f) * avgServiceTime[index]) + (f * servTime);
		avgResponseTime[index] = ((1 - f) * avgResponseTime[index]) + (f * respTime);
	}
	
	public float getAvgBid(float bid) {
		return (float) avgBid[getBinIndex(bid)];
	}
	
	public float getAvgServiceTime(float bid) {
		return (float) avgServiceTime[getBinIndex(bid)];
	}
	
	public float getAvgResponseTime(float bid) {
		return (float) avgResponseTime[getBinIndex(bid)];
	}
	
	public float getAvgSlowDown(float bid) {
		float st = getAvgServiceTime(bid);
		float rt = getAvgResponseTime(bid);
		return (st > 0) ? (rt / st) : 1;
	}

	protected int getBinIndex(float bid) {
		int index = (int) (bid * numBidBins);
		index = Math.min(index, numBidBins - 1);
		index = Math.max(index, 0);
		return index;
	}
	
	protected String printArray(String name, int size, double[] array) {
		StringBuilder s = new StringBuilder();
		s.append(name + "=[");
		for (int i = 0; i < size; i++) {
			s.append(String.format("%.4f", array[i]));
			if (i < size - 1) {
				s.append(";");
			}
		}
		s.append("] \n");
		return s.toString();
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("DataSmoother: ");
		s.append("smoothFactor=" + smoothFactor + "; ");
		s.append("numBidBins=" + numBidBins + "; ");
		s.append("\n");
		
		s.append(printArray("avgBid", numBidBins, avgBid));
		s.append(printArray("avgServiceTime", numBidBins, avgServiceTime));
		s.append(printArray("avgResponseTime", numBidBins, avgResponseTime));

		return s.toString();
	}

}
