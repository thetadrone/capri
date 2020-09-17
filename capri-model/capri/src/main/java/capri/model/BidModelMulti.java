package capri.model;

/**
 * Solves the multiple class bribing queue model
 * 
 * @author anonymous
 */
public class BidModelMulti implements BidModel {
	
	/** bid model over all classes */
	protected BidModel bidModelSingle;
	
	/** server parameter */
	protected float eta;
	
	/** average service time */
	protected float avgServTime;


	/**
	 * Constructor for {@link BidModelMulti}
	 * 
	 * @param eta model parameter
	 */
	public BidModelMulti(BidModel bidModelSingle, float eta, float avgServTime) {
		super();
		this.bidModelSingle = bidModelSingle;
		this.eta = eta;
		this.avgServTime = avgServTime;
	}
	
	/* (non-Javadoc)
	 * @see capri.model.BidModel#solve(float)
	 */
	public float[] solve(float x) {
		float[] perf = new float[] { 1 };

		if (avgServTime == 0) {
			return perf;
		}

		double sx = bidModelSingle.solve(x)[0];
		double ratio = bidModelSingle.getAvgServTime() / avgServTime;
		double factor = ratio * eta;		
		
		double slowdown = (factor * sx) + ((1 - factor) * Math.sqrt(sx));
		perf[0] = (float) slowdown;
		return perf;
	}

	/* (non-Javadoc)
	 * @see capri.model.BidModel#solve(float, float)
	 */
	public float[] solve(float x, float r) {
		return solve(x);
	}
	
	/* (non-Javadoc)
	 * @see capri.model.BidModel#solvePlus(float, float)
	 */
	public float[] solvePlus(float x, float delta) {
		float[] perf = new float[1];
		float curEta = eta;
		eta *= (1 + delta);
		perf = solve(x);
		eta = curEta;
		return perf;
	}

	/* (non-Javadoc)
	 * @see capri.model.BidModel#solvePlus(float, float, float)
	 */
	public float[] solvePlus(float x, float delta, float r) {
		return solvePlus(x, delta);
	}
	
	/* (non-Javadoc)
	 * @see capri.model.BidModel#getAvgServTime()
	 */
	public float getAvgServTime() {
		return avgServTime;
	}

}
