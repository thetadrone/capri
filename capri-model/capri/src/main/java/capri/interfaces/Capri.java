package capri.interfaces;

/**
 * Interface to performance model
 * 
 * @author anonymous
 * 
 */
public interface Capri {

	/**
	 * update the model with a data point, including updating distributions
	 * of bid and service time
	 * 
	 * @param id		id of data point
	 * @param bid		bid value in [0, 1]
	 * @param waitTime	wait (queue) time >= 0
	 * @param respTime	response (total) time > 0
	 */
	public void update(String id, float bid, float waitTime, float respTime);
	
	/**
	 * update statistics of bid distribution given a data point
	 * 
	 * @param bid
	 */
	public void updateBidStats(float bid);
	
	/**
	 * update statistics of service time distribution given a data point
	 * 
	 * @param servTime
	 */
	public void updateServTimeStats(float servTime);
	
	/**
	 * update filter model given a data point
	 * 
	 * @param bid
	 * @param waitTime
	 * @param respTime
	 */
	public void updateModel(float bid, float waitTime, float respTime);

	/**
	 * calculate bid value resulting in a given target slow down value
	 * 
	 * @param targetSlowDown	target slow down value > 1
	 * @return					bid value in [0, 1]
	 */
	public float getBid(float targetSlowDown);
	
	/**
	 * calculate bid value resulting in a given target slow down value,
	 * given a service time value
	 * 
	 * @param targetSlowDown	target slow down value > 1
	 * @param servTime			service time
	 * @return					bid value in [0, 1]
	 */
	public float getBid(float targetSlowDown, float servTime);

	/**
	 * calculate average slow down value resulting from using a given bid value
	 * 
	 * @param bid				bid value in [0, 1]
	 * @param lowHighRange		array of size two to return [ lowSlowDown, HighSlowDown ]
	 * @return					average slow down value > 1
	 */
	public float getSlowDown(float bid, float[] lowHighRange);
	
	/**
	 * calculate slow down value resulting from using a given bid value
	 * 
	 * @param bid				bid value in [0, 1]
	 * @param servTime			service time
	 * @param lowHighRange		array of size two to return [ lowSlowDown, HighSlowDown ]
	 * @return					average slow down value > 1
	 */
	public float getSlowDown(float bid, float servTime, float[] lowHighRange);
	
	/**
	 * Parameters:
	 * <ol>
	 * <li> alpha
	 * <li> beta
	 * <li> theta[]
	 * </ol>
	 * 
	 * @return float[] { alpha, beta, theta[] }
	 */
	public float[] getModelParameters();

}
