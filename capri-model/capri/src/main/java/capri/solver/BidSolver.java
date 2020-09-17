package capri.solver;


/**
 * Solves for a bid value in [0,1] given a slowdown value.
 * 
 * @author anonymous
 *
 */
public interface BidSolver {
	
	public void setParms(float delta, int numIterations, double tolerance);
		
	public float solve(float slowdown);
	
	public float solve(float slowdown, float servTime);

}
