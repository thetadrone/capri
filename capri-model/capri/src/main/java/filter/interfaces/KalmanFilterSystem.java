package filter.interfaces;

import java.io.Serializable;

/**
 * 
 * @author anonymous
 *
 */
public interface KalmanFilterSystem extends Serializable {
	
	public void setPercentChange(float[] percentChange);
	
	public void setTStudentGammaFactor (float gammaFactor);
	
	public void setTStudentErrorLevel (float errorLevel);
	
	public void setTStudentPercentile (float percentile);
	
	public void setInitState (float[] initState);
	
	public void setMeanMeasure(float meanMeasure);
	
	public void setMinState (float[] minState);
	
	public void setMaxState (float[] maxState);
	
	public boolean step(float[] measuredValues);
	
	public float[] getCurrentState();
	
	public float[] getEstimationVariance();
	
	public float[] getResiduals();
	
	public float[][] getKalmanGain();
	
	public int getNumStates();
	
	public int getNumMeasures();
	
	
	
	

}
