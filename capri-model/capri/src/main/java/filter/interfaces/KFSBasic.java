package filter.interfaces;

public interface KFSBasic extends KalmanFilterSystem{

	public void setNumStates(int numStates);

	public void setNumMeasures(int numMeasures);
	
	public void setFMatrix (float[][] fMatrix);
	
	public void setHMatrix (float[][] hMatrix);
	
	public void setBMatrix (float[][] bMatrix);
	
}
