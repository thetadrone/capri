package capri.model;

/**
 * Queueing model
 * 
 * @author anonymous
 */
public interface BidModel {

	/**
	 * Compute average slowdown given a bid value
	 * 
	 * @param x	bid value
	 * @return array containing slowdown
	 */
	public float[] solve(float x);
	
	/**
	 * Compute average slowdown given a bid value and a service time value
	 * 
	 * @param x	bid value
	 * @param r	service time
	 * @return array containing slowdown
	 */
	public float[] solve(float x, float r);
	
	
	/**
	 * Compute slowdown at an increment of theta parameter given a bid value
	 * 
	 * @param x	bid value
	 * @param delta	step value
	 * @return
	 */
	public float[] solvePlus(float x, float delta);
	
	/**
	 * Compute slowdown at an increment of theta parameter given a bid value and
	 * a service time value
	 * 
	 * @param x	bid value
	 * @param delta	step value
	 * @param r	service time value
	 * @return
	 */
	public float[] solvePlus(float x, float delta, float r);
	
	/**
	 * Return the average service time used in the model
	 * 
	 * @return
	 */
	public float getAvgServTime();
	
}
