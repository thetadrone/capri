package capri.interfaces;

import capri.impl.CapriModelMulti;
import capri.impl.CapriModelSingle;

/**
 * Factory to create the proper class that implements the {@link CapriModelSingle} interface
 * @author anonymous
 *
 */
public class CapriFactory {
	
	public static Capri create() {
		return new CapriModelSingle();
	}
	
	public static Capri create(String cfgFileName) {
		return new CapriModelSingle(cfgFileName);
	}
	
	public static Capri createMulti(Capri capriModelSingle) {
		return new CapriModelMulti(capriModelSingle);
	}
	
	public static Capri createMulti(String cfgFileName, Capri capriModelSingle) {
		return new CapriModelMulti(cfgFileName, capriModelSingle);
	}

}
