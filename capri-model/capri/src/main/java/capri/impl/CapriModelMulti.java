package capri.impl;

import capri.filter.multi.HJacobianMulti;
import capri.filter.multi.hFunctionMulti;
import capri.interfaces.Capri;
import capri.solver.BidSolver;
import capri.solver.BidSolverMulti;
import config.Configurator;
import filter.kalman.ExtendedKalmanFilter;

/**
 * Implementation of the {@link Capri} interface
 * 
 * @author anonymous
 */
public class CapriModelMulti extends CapriModelBase {

	protected CapriModelSingle capriModelSingle;
	
	/**
	 * Default parameter values
	 */

	/* additional model parameters */
	protected float classInitValues = 1;
	protected float classMinValues = 0.1f;
	protected float classMaxValues = 5.0f;
	protected float classPercentChange = 5.0f;

	/**
	 * States: 1. eta
	 */

	/**
	 * Constructor of a model using default configuration parameters
	 */
	public CapriModelMulti(Capri capriModelSingle) {
		super();
		this.capriModelSingle = (CapriModelSingle) capriModelSingle;
		init();
	}

	/**
	 * Constructor of a model using parameters from a configuration file
	 * 
	 * @param cfgFileName
	 *            name of configuration file
	 */
	public CapriModelMulti(String cfgFileName, Capri capriModelSingle) {
		super();
		this.capriModelSingle = (CapriModelSingle) capriModelSingle;
		readParms(cfgFileName);
		checkParms();
		init();
	}

	/**
	 * initialize model and filter
	 */
	private void init() {
		
		numStates = 1;
		initValues = new float[] { classInitValues };
		minValues = new float[] { classMinValues};
		maxValues = new float[] { classMaxValues };
		percentChange = classPercentChange;

		initBase();

		logger.info("Initializing ...");
		logger.info(printParms());

		/**
		 * setting environment for the filter
		 */
		env.capriModel = capriModelSingle;

		logger.info("Environment created ...");

		/**
		 * functional definitions
		 */
		smallh = new hFunctionMulti(env);
		bigH = new HJacobianMulti(env, stepSize);

		/**
		 * create Kalman filter
		 */
		filter = new ExtendedKalmanFilter(numStates, numMeasures, initX, initP, smallh, bigH, smallf, bigF);

		filter.setStateLimit(xLimiter);

		logger.info("ExtendedKalmanFilter created ...");
	}

	@Override
	public float getBid(float targetSlowDown) {
		float[] eta = filter.getStateVector();
		BidSolver bidSolver = new BidSolverMulti(env, eta[0]);

		return super.getBidUsingSolver(targetSlowDown, bidSolver);
	}

	@Override
	public float getBid(float targetSlowDown, float servTime) {
		float[] theta = filter.getStateVector();
		BidSolver bidSolver = new BidSolverMulti(env, theta[0]);

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
		classInitValues = cfg.getFloatValue("classInitValues");
		classMinValues = cfg.getFloatValue("classMinValues");
		classMaxValues = cfg.getFloatValue("classMaxValues");
		classPercentChange = cfg.getFloatValue("classPercentChange");
	}

	/**
	 * adjust parameter values if needed
	 */
	protected void checkParms() {

		checkParmsBase();

		/* model parameters */
		
		float classInitValuesDefault = 1;
		float classMinValuesDefault = 0.1f;
		float classMaxValuesDefault = 5;

		classInitValues = (classInitValues <= classMinValuesDefault) || (classInitValues >= classMaxValuesDefault)
				? classInitValuesDefault
				: classInitValues;
		
		classMinValues = (classMinValues < classMinValuesDefault) || (classMinValues >= classMaxValuesDefault)
				? classMinValuesDefault
				: classMinValues;
		
		classMaxValues = (classMaxValues <= classMinValuesDefault) || (classMaxValues > classMaxValuesDefault)
				? classMaxValuesDefault
				: classMaxValues;
		
		classPercentChange = (classPercentChange <= 0) ? 5 : classPercentChange;
	}

	protected String printParms() {
		StringBuilder str = new StringBuilder(printParmsBase());

		/* model parameters */
		str.append("\n");
		str.append("classInitValues=" + classInitValues + "; ");
		str.append("classMinValues=" + classMinValues + "; ");
		str.append("classMaxValues=" + classMaxValues + "; ");
		str.append("classPercentChange=" + classPercentChange + "; ");

		return str.toString();
	}

}
