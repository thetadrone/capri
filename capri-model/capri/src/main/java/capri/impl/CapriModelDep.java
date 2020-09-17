package capri.impl;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import capri.env.Environment;
import capri.filter.FJacobian;
import capri.filter.fFunction;
import capri.filter.single.HJacobian;
import capri.filter.single.hFunction;
import capri.interfaces.Capri;
import capri.solver.BidSolverSingle;
import config.Configurator;
import filter.kalman.ExtendedKalmanFilter;
import filter.kalman.OneFunctionalMatrix;
import filter.kalman.QMatrixCreator;
import filter.kalman.RMatrixCreator;
import filter.kalman.StateLimiter;
import utils.math.linearalgebra.Matrix;

/**
 * Implementation of the {@link Capri} interface
 * 
 * @author anonymous
 */
public class CapriModelDep implements Capri{
	
	/**
	 * Default parameter values
	 */
	
	/* model parameters */
	protected boolean conditionalServiceTime = true;
	protected float initAverageServiceTime = 400;
	protected float initMinimumServiceTime = 50;
	protected float initAverageBid = 0.5f;
	protected float initAverageBidSquared = 0.3f;
	protected float averageSmoothingFactor = 1f / 30;
	
	/* filter parameters */
	protected float gammaFactor = 0.01f;
	protected float errorLevel = 0.05f;
	protected float studentPercentile = 1.96f;
	protected float confidenceIntervalLevel = 0.1f;
	protected float stepSize = 0.01f;
	
	/* Range of parameters */
	protected int numStates = 1;
	protected float percentChange = 5f;
	protected float[] initValues = new float[] { 0.5f, 1.0f };
	protected float[] minValues = new float[] { 0.0f, 0.0f };
	protected float[] maxValues = new float[] { 1.0f, 10.0f };
	protected float epsilon = 0.0001f;
	protected float initSlowDown = 4f;
	
	/* Solver parameters */
	protected float solverStepSize = 0.01f;
	protected int solverNumIterations = 10;
	protected double solverTolerance = 1E-3;
	
	/* Miscellaneous */
	protected String logFileName = "capri.log";
	
	/* Logger */
	private static final Logger logger = Logger.getLogger(
			CapriModelDep.class.getName(), null /* resource bundle */);
	
	
	/**
	 * States:
	 * 1. theta0 (or rho, if numStates == 1)
	 * 2. theta1
	 */
	
	/**
	 * Observations:
	 * 1. slow down
	 */
	protected int numMeasures = 1;
	
	protected ExtendedKalmanFilter filter;
	protected Environment env;
	protected Matrix processCov;
	protected Matrix measureCov;
	protected hFunction smallh;
	protected HJacobian bigH;
	
	
	/**
	 * Constructor of a model using default configuration parameters
	 */
	public CapriModelDep() {
		super();
		init();
	}
	
	
	/**
	 * Constructor of a model using parameters from a configuration file
	 * @param cfgFileName	name of configuration file
	 */
	public CapriModelDep(String cfgFileName) {
		super();
		readParms(cfgFileName);
		checkParms();
		init();
	}
	
	
	/**
	 * initialize model and filter
	 */
	private void init() {
		
		/**
		 * initialize file logging
		 */
		logger.setUseParentHandlers(false);
		FileHandler fh;
		try {
			fh = new FileHandler(logFileName);
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		logger.info("Initializing ...");
		
		logger.info(printParms());
		
		/**
		 * create environment for the filter
		 */
		env = new Environment();
		env.conditionalServiceTime = conditionalServiceTime;
		env.avgServTime = initAverageServiceTime;
		env.r0 = initMinimumServiceTime;
		env.avgBid = initAverageBid;
		env.avgBid2 = initAverageBidSquared;
		env.smoothFactor = averageSmoothingFactor;
		
		logger.info("Environment created ...");
		
		/**
		 *  functional definitions
		 */
		smallh = new hFunction(env);
		bigH = new HJacobian(env, stepSize);
		OneFunctionalMatrix smallf = new fFunction();
		OneFunctionalMatrix bigF = new FJacobian(numStates);
	
		/**
		 *  covariance estimators
		 */
		QMatrixCreator qMatrixCreator = new QMatrixCreator();
		RMatrixCreator rMatrixCreator = new RMatrixCreator(errorLevel,
				studentPercentile, gammaFactor);
		double[] stateChange = new double[numStates];
		double[] meanMeasure = new double[numMeasures];
		
		/**
		 *  initialization
		 */
		double[][] x = new double[numStates][1];
		for (int i = 0; i < numStates; i++) {
			x[i][0] = initValues[i];
		}
		Matrix initX = new Matrix(x);
		
		/**
		 *  R matrix
		 */
		meanMeasure[0] = initSlowDown;
		measureCov = rMatrixCreator.getMatrix(meanMeasure);
		
		/**
		 *  set state limits
		 */
		StateLimiter xLimiter = new StateLimiter();
		double[] minStateLimit = new double[numStates];
		double[] maxStateLimit = new double[numStates];
		for (int i = 0; i < numStates; i++) {
			minStateLimit[i] = minValues[i];
			maxStateLimit[i] = maxValues[i];
		}
		xLimiter.setLowerLimit(minStateLimit);
		xLimiter.setUpperLimit(maxStateLimit);
		
		/**
		 *  Q matrix
		 */
		for (int j = 0; j < numStates; j++) {
			stateChange[j] = x[j][0] * percentChange / 100;
		}
		processCov = qMatrixCreator.getMatrix(stateChange);
		
		/**
		 *  P matrix
		 */
		double[][] p = new double[numStates][numStates];
		for (int i = 0; i < numStates; i++) {
			p[i][i] = Math.pow(stateChange[i], 2);
		}
		Matrix initP = new Matrix(p);
		
		/**
		 *  create Kalman filter
		 */
		filter = new ExtendedKalmanFilter(numStates,
				numMeasures, initX, initP, smallh, bigH, smallf, bigF);
		
		filter.setStateLimit(xLimiter);
		
		logger.info("ExtendedKalmanFilter created ...");
	}
	
	
	@Override
	public void update(String id, float bid, float waitTime, float respTime) {
		
		float servTime = respTime - waitTime;
		servTime = (servTime > 0) ? servTime : 1;
		
		env.addSampleServTime(servTime);
		env.addSampleBid(bid);
		
		double[][] d = new double[1][1];
		d[0][0] = respTime / servTime;
		
		logger.info("MEASURES: " + "id=" + id + "; bid=" + bid + "; waitTime="
				+ waitTime + "; respTime=" + respTime + "; sdMeas="
				+ (float) d[0][0]);
		
		Matrix measures = new Matrix(d);
		
		filter.predict(processCov);
		filter.sethFunction(smallh);
		filter.setHFunction(bigH);
		filter.correct(measures, measureCov);
		
		// print estimates and filter data
		StringBuilder str = new StringBuilder();
		str.append("ESTIMATES: ");
		str.append("id=" + id + "; ");
		str.append("alpha=" + env.alpha + "; ");
		str.append("beta=" + env.beta + "; ");
		float[] state = filter.getStateVector();
		str.append("theta=[ ");
		for (int i = 0; i < numStates; i++) {
			str.append(state[i] + " ");
		}
		str.append("]; ");
		float[] out = filter.getOutputVector();
		str.append("sdAvg=" + out[0] + "; ");
		
		logger.info(str.toString());
		
		str = new StringBuilder();
		str.append("FILTERDATA: ");
		str.append("id=" + id + "; ");
		float[] var = filter.getEstimationVariance();
		str.append("thetaVar=[ ");
		for (int i = 0; i < var.length; i++) {
			str.append(var[i] + " ");
		}
		str.append("]; ");
		float[] res =filter.getResiduals();
		str.append("resid=" + res[0] + "; ");
		float[][] gain = filter.getKalmanGain();
		str.append("gain=[ ");
		for (int i = 0; i < numStates; i++) {
			str.append(gain[i][0] + " ");
		}
		str.append("]; ");
		logger.info(str.toString());
	}
	
	public void updateBidStats(float bid) {
		env.addSampleBid(bid);
	}
	
	public void updateServTimeStats(float servTime) {
		env.addSampleServTime(servTime);
	}
	
	public void updateModel(float bid, float waitTime, float respTime) {

		float servTime = respTime - waitTime;
		servTime = (servTime > 0) ? servTime : 1;
		
		env.bid = bid;
		env.servTime = servTime;
		
		double[][] d = new double[1][1];
		d[0][0] = respTime / servTime;
		
		Matrix measures = new Matrix(d);
		
		filter.predict(processCov);
		filter.sethFunction(smallh);
		filter.setHFunction(bigH);
		filter.correct(measures, measureCov);
	}

	@Override
	public float getBid(float targetSlowDown) {
		float[] theta = filter.getStateVector();
		BidSolverSingle bidSolver = new BidSolverSingle(env, theta);
		bidSolver.setParms(solverStepSize, solverNumIterations, solverTolerance);
		float bidAdvice = bidSolver.solve(targetSlowDown);
		
		StringBuilder str = new StringBuilder();
		str.append("BIDADVICE: ");
		str.append("targetSlowDown=" + targetSlowDown + "; ");
		str.append("bidAdvice=" + bidAdvice + "; ");
		logger.info(str.toString());
		
		return bidAdvice;
	}
	
	@Override
	public float getBid(float targetSlowDown, float servTime) {
		float[] theta = filter.getStateVector();
		BidSolverSingle bidSolver = new BidSolverSingle(env, theta);
		float bidAdvice =  bidSolver.solve(targetSlowDown, servTime);
		
		StringBuilder str = new StringBuilder();
		str.append("BIDADVICE: ");
		str.append("targetSlowDown=" + targetSlowDown + "; ");
		str.append("servTime=" + servTime + "; ");
		str.append("bidAdvice=" + bidAdvice + "; ");
		logger.info(str.toString());
		
		return bidAdvice;
	}

	@Override
	public float getSlowDown(float bid, float[] lowHighRange) {
		env.bid = bid;
		boolean isServTime = env.conditionalServiceTime;
		env.conditionalServiceTime = false;
		float[] estOut = filter.getOutputVector();
		env.conditionalServiceTime = isServTime;
		float avgSlowDown = estOut[0];
		calculateRange(avgSlowDown, lowHighRange);
		
		StringBuilder str = new StringBuilder();
		str.append("SLOWDOWNADVICE: ");
		str.append("bid=" + bid + "; ");
		str.append("lowRange=" + lowHighRange[0] + "; ");
		str.append("highRange=" + lowHighRange[1] + "; ");
		str.append("avgSlowDown=" + avgSlowDown + "; ");
		logger.info(str.toString());
		
		return avgSlowDown;
	}
	
	@Override
	public float getSlowDown(float bid, float servTime, float[] lowHighRange) {
		env.bid = bid;
		env.servTime = servTime;
		float[] estOut = filter.getOutputVector();
		float avgSlowDown = estOut[0];
		calculateRange(avgSlowDown, lowHighRange);
		
		StringBuilder str = new StringBuilder();
		str.append("SLOWDOWNADVICE: ");
		str.append("bid=" + bid + "; ");
		str.append("servTime=" + servTime + "; ");
		str.append("lowRange=" + lowHighRange[0] + "; ");
		str.append("highRange=" + lowHighRange[1] + "; ");
		str.append("avgSlowDown=" + avgSlowDown + "; ");
		logger.info(str.toString());
		
		return avgSlowDown;
	}
	
	
	@Override
	public float[] getModelParameters() {
		int numParms = 2 + numStates;
		float[] parms = new float[numParms];
		
		parms[0] = env.alpha;
		parms[1] = env.beta;
		for (int i = 0; i < numStates; i++) {
			parms[2 + i] = filter.getStateVector()[i];
		}
		
		StringBuilder str = new StringBuilder();
		str.append("MODELPARMS: ");
		str.append("alpha=" + parms[0] + "; ");
		str.append("beta=" + parms[1] + "; ");
		str.append("theta=[ ");
		for (int i = 0; i < numStates; i++) {
			str.append(parms[2 + i] + " ");
		}
		str.append("]; ");
		logger.info(str.toString());
		
		return parms;
	}
	
	
	/**
	 * calculate percentile range given an average value
	 * assuming an exponential distribution
	 * 
	 * @param averageValue the average value of the distribution
	 * @param lowHighRange float[2] where low and high values of range are returned
	 */
	private void calculateRange(float averageValue, float[] lowHighRange) {
		if (lowHighRange != null && lowHighRange.length >= 2) {
			lowHighRange[0] = 1 - (averageValue - 1)
					* (float) Math.log(1 - confidenceIntervalLevel);
			lowHighRange[1] = 1 - (averageValue - 1)
					* (float) Math.log(confidenceIntervalLevel);
		}
	}
	
	
	/**
	 * read all parameters from configuration file
	 * 
	 * @param cfgFileName
	 */
	private void readParms(String cfgFileName) {
		
		if (cfgFileName == null || cfgFileName.isEmpty()) {
			cfgFileName = "capri.cfg";
		}
		
		Configurator cfg = new Configurator(cfgFileName);
		System.out.println(cfg);
		
		/* model parameters */
		conditionalServiceTime = cfg.getBoolValue("conditionalServiceTime");
		initAverageServiceTime = cfg.getFloatValue("initAverageServiceTime");
		initMinimumServiceTime = cfg.getFloatValue("initMinimumServiceTime");
		initAverageBid = cfg.getFloatValue("initAverageBid");
		initAverageBidSquared = cfg.getFloatValue("initAverageBidSquared");
		averageSmoothingFactor = cfg.getFloatValue("averageSmoothingFactor");

		/* filter parameters */
		gammaFactor = cfg.getFloatValue("gammaFactor");
		errorLevel = cfg.getFloatValue("errorLevel");
		studentPercentile = cfg.getFloatValue("studentPercentile");
		confidenceIntervalLevel = cfg.getFloatValue("confidenceIntervalLevel");
		stepSize = cfg.getFloatValue("stepSize");

		/* Range of parameters */
		numStates = cfg.getIntValue("numStates");
		initValues = cfg.getFloatArray("initValues");
		minValues = cfg.getFloatArray("minValues");
		maxValues = cfg.getFloatArray("maxValues");
		
		percentChange = cfg.getFloatValue("percentChange");
		epsilon = cfg.getFloatValue("epsilon");
		initSlowDown = cfg.getFloatValue("initSlowDown");
		
		/* Solver parameters */
		solverStepSize = cfg.getFloatValue("solverStepSize");
		solverNumIterations = cfg.getIntValue("solverNumIterations");
		solverTolerance = cfg.getFloatValue("solverTolerance");
		
		/* Miscellaneous */
		logFileName = cfg.getStringValue("logFileName");
	}
	
	
	/**
	 * adjust parameter values if needed
	 */
	private void checkParms() {
		/* model parameters */
		initAverageServiceTime = (initAverageServiceTime <= 0) ? 300
				: initAverageServiceTime;
		initMinimumServiceTime = (initMinimumServiceTime <= 0) ? 60
				: initMinimumServiceTime;
		initAverageBid = (initAverageBid <=0 || initAverageBid >= 1) ? 0.5f
				: initAverageBid;
		initAverageBidSquared = (initAverageBidSquared <=0 || initAverageBidSquared >= 1) ? 0.3f
				: initAverageBidSquared;
		averageSmoothingFactor = (averageSmoothingFactor <= 0 || averageSmoothingFactor >= 1) ? 0.05f
				: averageSmoothingFactor;

		/* filter parameters */
		gammaFactor = (gammaFactor <= 0) ? 0.01f : gammaFactor;
		errorLevel = (errorLevel <= 0 || errorLevel >= 1) ? 0.05f : errorLevel;
		studentPercentile = (studentPercentile <= 0) ? 1.96f : studentPercentile;
		confidenceIntervalLevel = (confidenceIntervalLevel <= 0 || confidenceIntervalLevel >= 1) ? 0.1f
				: confidenceIntervalLevel;
		stepSize = (stepSize <= 0 || stepSize >= 1) ? 0.01f : stepSize;

		/* Range of parameters */
		numStates = (numStates <= 0) ? 1 : numStates;
		percentChange = (percentChange <= 0) ? 5 : percentChange;
		epsilon = (epsilon <= 0 || epsilon >= 1) ? 0.0001f : epsilon;
		initSlowDown = (initSlowDown < 1) ? 1 : initSlowDown;
		
		float[] initDefault = new float[] { 0.5f, 1f };
		float[] minDefault = new float[] { 0, 0 };
		float[] maxDefault = new float[] { 1, 10 };
		for (int i = 0; i < numStates; i++) {
			initValues[i] = (initValues[i] <= minDefault[i] || initValues[i] >= maxDefault[i]) ? initDefault[i]
					: initValues[i];
			maxValues[i] = (maxValues[i] >= maxDefault[i] || maxValues[i] < minDefault[i]) ? maxDefault[i] * (1 - epsilon) : maxValues[i];
			minValues[i] = (minValues[i] <= minDefault[i] || minValues[i] > maxDefault[i]) ? minDefault[i] + epsilon : minValues[i];
		}
		
		/* Solver parameters */
		solverStepSize = (solverStepSize <= 0 || solverStepSize >= 1) ? 0.1f : solverStepSize;
		solverNumIterations = (solverNumIterations <= 0) ? 10 : solverNumIterations;
		solverTolerance = (solverTolerance <= 0) ? 1E-3 : solverTolerance;
		
		/* Miscellaneous */
		logFileName = (logFileName.isEmpty()) ? "capri.log" : logFileName;
	}
	
	private String printParms() {
		StringBuilder str = new StringBuilder();
		str.append("CONFIGURATION: ");		
		
		/* model parameters */
		str.append("\n");
		str.append("conditionalServiceTime=" + conditionalServiceTime + "; ");
		str.append("initAverageServiceTime=" + initAverageServiceTime + "; ");
		str.append("initMinimumServiceTime=" + initMinimumServiceTime + "; ");
		str.append("initAverageBid=" + initAverageBid + "; ");
		str.append("initAverageBidSquared=" + initAverageBidSquared + "; ");
		str.append("averageSmoothingFactor=" + averageSmoothingFactor + "; ");
		
		/* filter parameters */
		str.append("\n");
		str.append("gammaFactor=" + gammaFactor + "; ");
		str.append("errorLevel=" + errorLevel + "; ");
		str.append("studentPercentile=" + studentPercentile + "; ");
		str.append("confidenceIntervalLevel=" + confidenceIntervalLevel + "; ");
		str.append("stepSize=" + stepSize + "; ");
		
		/* Range of parameters */
		str.append("\n");
		str.append("numStates=" + numStates + "; ");
		str.append("initValues=[ ");
		for (int i = 0; i < numStates; i++) {
			str.append(initValues[i] + " ");
		}
		str.append("]; ");
		str.append("minValues=[ ");
		for (int i = 0; i < numStates; i++) {
			str.append(minValues[i] + " ");
		}
		str.append("]; ");
		str.append("maxValues=[ ");
		for (int i = 0; i < numStates; i++) {
			str.append(maxValues[i] + " ");
		}
		str.append("]; ");
		
		str.append("percentChange=" + percentChange + "; ");
		str.append("epsilon=" + epsilon + "; ");
		str.append("initSlowDown=" + initSlowDown + "; ");
		
		/* Solver parameters */
		str.append("\n");
		str.append("solverStepSize=" + solverStepSize + "; ");
		str.append("solverNumIterations=" + solverNumIterations + "; ");
		str.append("solverTolerance=" + solverTolerance + "; ");
		
		/* Miscellaneous */
		str.append("\n");
		str.append("logFileName=" + logFileName + "; ");

		return str.toString();
	}

}
