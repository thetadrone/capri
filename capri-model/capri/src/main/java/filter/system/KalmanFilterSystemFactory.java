package filter.system;

import filter.interfaces.KFSBasic;


public class KalmanFilterSystemFactory {

	public static KFSBasic createBasicFilter() {
		
		return new KFSBasicImpl();
	}
}
