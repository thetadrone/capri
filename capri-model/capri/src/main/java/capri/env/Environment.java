package capri.env;

import capri.impl.CapriModelSingle;
import capri.model.BidModel;
import capri.model.BidModelSingle;
import utils.prob.distribution.Beta;

/**
 * Environment for the filter.
 * Contains non-parametric data.
 * 
 * (For convenience, field member data is made public.)
 * 
 * @author anonymous
 */
public class Environment {

	public float bid = 0;
	public float servTime = 1;
	public float avgServTime = 1;
	public float smoothFactor = 0.05f;

	public float alpha = 1;
	public float beta = 1;
	
	public boolean conditionalServiceTime = false;
	public float r0 = 0.1f;
	
	public double avgBid = 0.5f;
	public double avgBid2 = 0.33f;
	
	public CapriModelSingle capriModel;
	
	public Environment() {
		super();
	}
	
	/**
	 * Update environment given a measured value of service time
	 * 
	 * @param sTime
	 */
	public void addSampleServTime(float sTime) {
		avgServTime = ((1 - smoothFactor) * avgServTime) + (smoothFactor * sTime);
		if (sTime < r0) {
			r0 = sTime;
		}
	}
	
	/**
	 * Update environment given a measured value of bid
	 * 
	 * @param sTime
	 */
	public void addSampleBid(float bid) {
		avgBid = ((1 - smoothFactor) * avgBid) + (smoothFactor * bid);
		avgBid2 = ((1 - smoothFactor) * avgBid2) + (smoothFactor * bid * bid);
		
		Beta dist = new Beta();
		double[] moments = new double[] { avgBid, avgBid2 };
		if (dist.matchMoments(moments) > 0) {
			alpha = (float) dist.getAlpha();
			beta = (float) dist.getBeta();
		}
	}
	
	public BidModel getBidModel() {

		if (capriModel == null) {
			return null;
		}
		
		Environment env = capriModel.getEnv();
		float mu = 1 / env.avgServTime;

		float[] parms = capriModel.getModelParameters();
		int numParms = parms.length;
		float alpha = parms[0];
		float beta = parms[1];
		int m = numParms - 2;
		float[] theta = new float[m];
		for (int i = 0; i < m; i++) {
			theta[i] = parms[i + 2];
		}

		BidModelSingle model = new BidModelSingle(alpha, beta, theta, mu, env.r0);
		model.setConditionalServiceTime(env.conditionalServiceTime);

		return model;
	}

}
