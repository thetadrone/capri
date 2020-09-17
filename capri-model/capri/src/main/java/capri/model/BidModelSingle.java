package capri.model;

import utils.math.numerical.functions.SpecialFunctions;
import utils.prob.distribution.Beta;

/**
 * Solves the bribing queue model, assuming B(x) is a {@link Beta} distribution.
 * 
 * @author anonymous
 */
public class BidModelSingle implements BidModel {

	/** alpha parameter of beta distribution */
	protected float alpha;
	
	/** beta parameter of beta distribution */
	protected float beta;
	
	/** server parameters */
	protected float[] theta;

	/** option when calculating slowdown, conditional on given service time */
	protected boolean conditionalServiceTime;
	
	/** average service time is 1/mu */
	protected float mu;
	
	/** factor a = nu * r0, where 1/nu = 1/mu - r0 and r0 is the minimum service time*/
	protected float a;
	
	/**
	 * option to evaluate the expectation of the ratio, rather than the ratio of
	 * expectations
	 */
	protected boolean expectationOfRatio = false;

	/**
	 * Constructor for {@link BidModelSingle}
	 * 
	 * @param alpha parameter of beta distribution
	 * @param beta parameter of beta distribution
	 * @param theta queueing model parameters
	 */
	public BidModelSingle(float alpha, float beta, float[] theta) {
		super();
		this.alpha = alpha;
		this.beta = beta;
		this.theta = theta;
	}

	/**
	 * Constructor for {@link BidModelSingle}
	 * 
	 * @param alpha parameter of beta distribution
	 * @param beta parameter of beta distribution
	 * @param theta queueing model parameters
	 * @param mu service rate
	 * @param r0 minimum service time
	 */
	public BidModelSingle(float alpha, float beta, float[] theta, float mu, float r0) {
		this(alpha, beta, theta);
		setParms(mu, r0);
	}
	
	/**
	 * Set parameters related to service time
	 * 
	 * @param mu service rate
	 * @param r0 minimum service time
	 */
	public void setParms(float mu, float r0) {
		this.mu = mu;
		float mur0 = mu * r0;
		this.a = (mur0 < 1) ? 1 / ((1 / mur0) - 1) : 10;
	}
	
	/**
	 * Set conditional service time option
	 * 
	 * @param conditionalServiceTime
	 */
	public void setConditionalServiceTime(boolean conditionalServiceTime) {
		this.conditionalServiceTime = conditionalServiceTime;
	}
	
	/**
	 * Set expectation of ratio option 
	 * 
	 * @param expectationOfRatio
	 */
	public void setExpectationOfRatio(boolean expectationOfRatio) {
		this.expectationOfRatio = expectationOfRatio;
	}
	
	/* (non-Javadoc)
	 * @see capri.model.BidModel#solve(float)
	 */
	public float[] solve(float x) {
		float[] perf = new float[1];
		perf[0] = (float) computeSlowdown(x, expectationOfRatio);
		return perf;
	}
	
	protected double computeSlowdown(float x, boolean ratio) {
		Beta dist = new Beta(alpha, beta);
		double bx = dist.PDF(x);

		double temp = theta[0];
		if (theta.length == 1) {
			temp *= (1 - bx);
		} else {
			temp *= Math.pow((1 - bx), theta[1]);
		}
		double slowdown = 1 / Math.pow((1 - temp), 2);
		
		if (ratio) {
			double ga = (1 + a) * Math.exp(a) * SpecialFunctions.gammaIncompleteZero(a);
			slowdown *= (1 + temp * (ga - 1));
		}
		
		return slowdown;
	}

	/* (non-Javadoc)
	 * @see capri.model.BidModel#solve(float, float)
	 */
	public float[] solve(float x, float r) {
		if (!conditionalServiceTime) {
			return solve(x);
		}
		float[] perf = new float[1];
		double sx = (float) computeSlowdown(x, false);

		float mur = 1 / (mu * r);
		double slowdown = (sx * mur) + (1 - mur) * Math.sqrt(sx);

		perf[0] = (float) slowdown;
		return perf;
	}

	/* (non-Javadoc)
	 * @see capri.model.BidModel#solvePlus(float, float)
	 */
	public float[] solvePlus(float x, float delta) {
		float[] perf = new float[theta.length];
		for (int i = 0; i < theta.length; i++) {
			float curTheta = theta[i];
			theta[i] *= (1 + delta);
			perf[i] = solve(x)[0];
			theta[i] = curTheta;
		}
		return perf;
	}

	/* (non-Javadoc)
	 * @see capri.model.BidModel#solvePlus(float, float, float)
	 */
	public float[] solvePlus(float x, float delta, float r) {
		float[] perf = new float[theta.length];
		for (int i = 0; i < theta.length; i++) {
			float curTheta = theta[i];
			theta[i] *= (1 + delta);
			perf[i] = solve(x, r)[0];
			theta[i] = curTheta;
		}
		return perf;
	}
	
	/* (non-Javadoc)
	 * @see capri.model.BidModel#getAvgServTime()
	 */
	public float getAvgServTime() {
		return 1 / mu;
	}

}
