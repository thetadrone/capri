package filter.system;

import filter.interfaces.KalmanFilterSystem;

public class KFSImpl implements KalmanFilterSystem{

	private static final long serialVersionUID = 1L;

	public float[] getCurrentState() {
		// TODO Auto-generated method stub
		return null;
	}

	public float[] getEstimationVariance() {
		// TODO Auto-generated method stub
		return null;
	}

	public float[][] getKalmanGain() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getNumMeasures() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getNumStates() {
		// TODO Auto-generated method stub
		return 0;
	}

	public float[] getResiduals() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setInitState(float[] initState) {
		// TODO Auto-generated method stub
		
	}

	public void setMaxState(float[] maxState) {
		// TODO Auto-generated method stub
		
	}

	public void setMeanMeasure(float meanMeasure) {
		// TODO Auto-generated method stub
		
	}

	public void setMinState(float[] minState) {
		// TODO Auto-generated method stub
		
	}

	public void setPercentChange(float[] percentChange) {
		// TODO Auto-generated method stub
		
	}

	public void setTStudentErrorLevel(float errorLevel) {
		// TODO Auto-generated method stub
		
	}

	public void setTStudentGammaFactor(float gammaFactor) {
		// TODO Auto-generated method stub
		
	}

	public void setTStudentPercentile(float percentile) {
		// TODO Auto-generated method stub
		
	}

	public boolean step(float[] measuredValues) {
		// TODO Auto-generated method stub
		return false;
	}


}
