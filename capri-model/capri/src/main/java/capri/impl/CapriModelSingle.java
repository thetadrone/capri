package capri.impl;

import capri.filter.single.HJacobian;
import capri.filter.single.hFunction;
import capri.solver.BidSolver;
import capri.solver.BidSolverSingle;
import config.Configurator;
import filter.kalman.ExtendedKalmanFilter;

/**
 * Implementation of the {@link CapriModelSingle} interface
 * 
 * @author anonymous
 */
public class CapriModelSingle extends CapriModelBase {

	/**
	 * Default parameter values
	 */

	/* additional model parameters */
	protected boolean conditionalServiceTime = false;
	protected float initMinimumServiceTime = 50;
	protected float initAverageBid = 0.5f;
	protected float initAverageBidSquared = 0.33f;

	/**
	 * States: 1. theta0 (or rho, if numStates == 1) 2. theta1
	 */

	/**
	 * Constructor of a model using default configuration parameters
	 */
	public CapriModelSingle() {
		super();
		init();
	}

	/**
	 * Constructor of a model using parameters from a configuration file
	 * 
	 * @param cfgFileName
	 *            name of configuration file
	 */
	public CapriModelSingle(String cfgFileName) {
		super();
		readParms(cfgFileName);
		checkParms();
		init();
	}

	/**
	 * initialize model and filter
	 */
	private void init() {

		initBase();

		logger.info("Initializing ...");
		logger.info(printParms());

		/**
		 * setting environment for the filter
		 */
		env.conditionalServiceTime = conditionalServiceTime;
		env.r0 = initMinimumServiceTime;
		env.avgBid = initAverageBid;
		env.avgBid2 = initAverageBidSquared;

		logger.info("Environment created ...");

		/**
		 * functional definitions
		 */
		smallh = new hFunction(env);
		bigH = new HJacobian(env, stepSize);

		/**
		 * create Kalman filter
		 */
		filter = new ExtendedKalmanFilter(numStates, numMeasures, initX, initP, smallh, bigH, smallf, bigF);

		filter.setStateLimit(xLimiter);

		logger.info("ExtendedKalmanFilter created ...");
	}

	@Override
	public float getBid(float targetSlowDown) {
		float[] theta = filter.getStateVector();
		BidSolver bidSolver = new BidSolverSingle(env, theta);

		return super.getBidUsingSolver(targetSlowDown, bidSolver);
	}

	@Override
	public float getBid(float targetSlowDown, float servTime) {
		float[] theta = filter.getStateVector();
		BidSolver bidSolver = new BidSolverSingle(env, theta);

		return super.getBidUsingSolver(targetSlowDown, servTime, bidSolver);
	}

	/**
	 * read all parameters from configuration file
	 * 
	 * @param cfgFileName
	 */
	protected void readParms(String cfgFileName) {

		if (cfgFileName == null || cfgFileName.isEmpty()) {
			cfgFileName = "capri.cfg";
		}

		Configurator cfg = new Configurator(cfgFileName);
		System.out.println(cfg);

		readParmsBase(cfg);

		/* additional model parameters */
		conditionalServiceTime = cfg.getBoolValue("conditionalServiceTime");
		initMinimumServiceTime = cfg.getFloatValue("initMinimumServiceTime");
		initAverageBid = cfg.getFloatValue("initAverageBid");
		initAverageBidSquared = cfg.getFloatValue("initAverageBidSquared");
	}

	/**
	 * adjust parameter values if needed
	 */
	protected void checkParms() {

		checkParmsBase();

		/* model parameters */
		initMinimumServiceTime = (initMinimumServiceTime <= 0) ? 50 : initMinimumServiceTime;
		initAverageBid = (initAverageBid <= 0 || initAverageBid >= 1) ? 0.5f : initAverageBid;
		initAverageBidSquared = (initAverageBidSquared <= 0 || initAverageBidSquared >= 1) ? 0.33f
				: initAverageBidSquared;
	}

	protected String printParms() {
		StringBuilder str = new StringBuilder(printParmsBase());

		/* model parameters */
		str.append("\n");
		str.append("initMinimumServiceTime=" + initMinimumServiceTime + "; ");
		str.append("conditionalServiceTime=" + conditionalServiceTime + "; ");
		str.append("initAverageBid=" + initAverageBid + "; ");
		str.append("initAverageBidSquared=" + initAverageBidSquared + "; ");

		return str.toString();
	}

}
