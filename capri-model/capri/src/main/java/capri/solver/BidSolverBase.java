package capri.solver;

/**
 * Solves for a bid value in [0,1] given a slowdown value.
 * 
 * @author anonymous
 *
 */
public abstract class BidSolverBase implements BidSolver {

	protected float delta = 0.01f;
	protected int numIterations = 10;
	protected double tolerance = 1E-3;

	public void setParms(float delta, int numIterations, double tolerance) {
		this.delta = delta;
		this.numIterations = numIterations;
		this.tolerance = tolerance;
	}

}
