package capri.advisor;

import capri.interfaces.Capri;
import capri.interfaces.CapriFactory;
import capri.stats.DataSmoother;

public class BidAdvisor {

	protected Capri capriModel;

	protected DataSmoother dataSmoother;

	protected boolean isPrint = true;

	protected boolean isPrintPred = false;

	
	/**
	 * create a bid advisor from configuration
	 */
	public BidAdvisor(String cfgFileName) {

		capriModel = CapriFactory.create(cfgFileName);
		
		dataSmoother = new DataSmoother(cfgFileName);
	}

	public BidAdvisor(String cfgFileName, Capri capriModelSingle) {

		capriModel = CapriFactory.createMulti(cfgFileName, capriModelSingle);
		dataSmoother = new DataSmoother(cfgFileName);
	}

	/**
	 * update filter for a request using data smoother
	 */
	public void update(float bid, float respTime, float servTime) {

		float slowDown = (servTime > 0) ? (respTime / servTime) : 1;

		dataSmoother.addData(bid, servTime, respTime);

		float avgBid = dataSmoother.getAvgBid(bid);
		float avgRespTime = dataSmoother.getAvgResponseTime(bid);
		float avgServTime = dataSmoother.getAvgServiceTime(bid);
		float avgSlowDown = dataSmoother.getAvgSlowDown(bid);
		float avgWaitTime = Math.max(avgRespTime - avgServTime, 0);

		float[] lowHighRange = new float[2];
		float slowDownPredicted = capriModel.getSlowDown(avgBid, lowHighRange);

		float bidAdvised = capriModel.getBid(avgSlowDown);

		capriModel.updateBidStats(bid);
		capriModel.updateServTimeStats(servTime);
		capriModel.updateModel(avgBid, avgWaitTime, avgRespTime);

		if (isPrint) {
			System.out.print("CapriModel: ");
			System.out.print(String.format("%s=%.4f \t", "bidObs", bid));
			// System.out.print(String.format("%s=%.4f \t", "avgBidObs", avgBid));
			System.out.print(String.format("%s=%.4f \t", "SDObs", slowDown));
			System.out.print(String.format("%s=%.4f \t", "avgSvcObs", avgServTime));
			System.out.print(String.format("%s=%.4f \t", "avgRespObs", avgRespTime));
			System.out.print(String.format("%s=%.4f \t", "avgSDObs", avgSlowDown));
			System.out.print(String.format("%s=%.4f \t", "SDPred", slowDownPredicted));
			System.out.print(String.format("%s=%.4f \t", "bidAdv", bidAdvised));
		}

		if (isPrintPred) {
			System.out.print(String.format("%s=%.4f \t", "avgSDObs", avgSlowDown));
			System.out.print(String.format("%s=%.4f \t", "SDPred", slowDownPredicted));
		}

	}

	/**
	 * get a bid advice
	 */
	public float getBidAdvice(float targetSlowDown) {
		return capriModel.getBid(targetSlowDown);
	}
	
	/**
	 * get an estimate of slowdown given a bid value
	 */
	public float estimateSlowdown(float bid, float[] lowHighRange) {
		return capriModel.getSlowDown(bid, lowHighRange);
	}

	/**
	 * return model parameters
	 */
	public float[] getParamaters() {
		float[] parms = capriModel.getModelParameters();
		int numStates = parms.length - 2;

		if (isPrint) {
			System.out.print(String.format("%s=%.5f \t", "alpha", parms[0]));
			System.out.print(String.format("%s=%.5f \t", "beta", parms[1]));
			System.out.print("theta=[ ");
			for (int i = 0; i < numStates; i++) {
				System.out.print(String.format("%.5f ", parms[2 + i]));
			}
			System.out.print("] \t");
			// System.out.print("\n");
		}

		return parms;
	}
	
	public Capri getCapriModel() {
		return capriModel;
	}
	
	public void setPrint(boolean isPrint) {
		this.isPrint = isPrint;
	}

	public void setPrintPred(boolean isPrintPred) {
		this.isPrintPred = isPrintPred;
	}

}
