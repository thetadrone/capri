package filter.system;

import filter.interfaces.KFSBasic;

public class KFSBasicImpl extends KFSImpl implements KFSBasic {

	private static final long serialVersionUID = 1L;

	protected int numStates = 1;
	
	protected int numMeasures = 1;
	
	public KFSBasicImpl() {
		
	}
	
	public void setNumStates(int numStates) {
		this.numStates = numStates;
		
	}
	
	public void setNumMeasures(int numMeasures) {
		this.numMeasures = numMeasures;
		
	}

	public void setBMatrix(float[][] matrix) {
		// TODO Auto-generated method stub
		
	}

	public void setFMatrix(float[][] matrix) {
		// TODO Auto-generated method stub
		
	}

	public void setHMatrix(float[][] matrix) {
		// TODO Auto-generated method stub
		
	}

}
