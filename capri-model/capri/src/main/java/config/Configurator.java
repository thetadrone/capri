package config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * The Configurator is a convenient class for reading configuration parameters from a property file 
 * and providing values of parameters through query methods for specific typed parameter names. 
 * A query may return a {@link ConfigException} in case the parameter does not exist or there is 
 * a problem parsing its value. <br>
 * <br>
 * Each parameter is specified in one line in the file with format <br>
 * <b> key = value </b> <br>
 * where <key> is the parameter name and <value> is its value. <br>
 * Spaces are trimmed from values.
 * No quotation marks are needed around strings.
 * Commas and semicolons are special characters.
 * <br> 
 * <br>
 * There are three categories of values: <br>
 * <b> singular, array, and matrix. </b> <br>
 * <br>
 * In each category, the following types are considered: <br>
 * <b> int, float, long, boolean, and string. </b> <br>
 * <br>
 * The singular case is straightforward: <br>
 * size = 10 <br>
 * width = 4.5 <br>
 * image = hello-world <br>
 * logging = false <br>
 * <br>
 * In the case of array, the property value should be provided as a sequence of comma separated elements: <br>
 * capacity = 32, 1024, 5000 <br>
 * length =  2.5, 43, 0.02 <br>
 * distribution = Constant, Poisson <br>
 * overflow = false, false, true, true <br>
 * <br>
 * As for matrices, the property value should be provided as a sequence of semicolon separated,
 * comma separated elements: <br>
 * x = 1, 512 ; 2, 1024 ; 4, 2048 <br>
 * y =  1.5, 2.6, 3.7; 0.9, 0.6, 0.3 <br>
 * l = A1, A2; B; C1, C2, C3 <br>
 * z = false, true; true, false <br>
 * <br>
 * 
 * @author anonymous
 * 
 */
public class Configurator {

	/**
	 * Name of the configuration file
	 */
	protected String cfgFileName;

	/**
	 * Properties in the configuration file
	 */
	protected Properties props;

	/**
	 * Read and store properties from a given configuration file.
	 * 
	 * @param cfgFileName
	 */
	public Configurator(String cfgFileName) {
		super();
		this.cfgFileName = cfgFileName;
		props = new Properties();
		FileInputStream in = null;
		try {
			in = new FileInputStream(cfgFileName);
		} catch (FileNotFoundException e) {
			System.err.println("Configuration file not found: " + cfgFileName);
		}

		if (in != null) {
			try {
				props.load(in);
				in.close();
			} catch (IOException e) {
				System.err.println("Configuration file exception: ");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Value of a parameter given by its name
	 * 
	 * @param parName
	 * @return value of parameter given by parName key
	 * @throws ConfigException
	 */
	public String getValue(String parName) throws ConfigException {
		String parValue = props.getProperty(parName);
		if (parValue == null) {
			throw new ConfigException();
		}
		return parValue.trim();
	}

	/**
	 * Value of a (int) parameter given by its name
	 * 
	 * @param parName
	 * @return value or 0 if parameter does not exist
	 */
	public int getIntValue(String parName) {
		int value = 0;
		try {
			value = new Integer(getValue(parName));
		} catch (Exception e) {
			System.out.println("Warning: parameter " + parName + " not set");
		}
		return value;
	}
	
	/**
	 * Value of a (float) parameter given by its name
	 * 
	 * @param parName
	 * @return value or 0 if parameter does not exist
	 */
	public float getFloatValue(String parName) {
		float value = 0;
		try {
			value = new Float(getValue(parName));
		} catch (Exception e) {
			System.out.println("Warning: parameter " + parName + " not set");
		}
		return value;
	}

	/**
	 * Value of a (long) parameter given by its name
	 * 
	 * @param parName
	 * @return value or 0 if parameter does not exist
	 */
	public long getLongValue(String parName) {
		long value = 0;
		try {
			value = new Long(getValue(parName));
		} catch (Exception e) {
			System.out.println("Warning: parameter " + parName + " not set");
		}
		return value;
	}

	/**
	 * Value of a (boolean) parameter given by its name
	 * 
	 * @param parName
	 * @return value or false if parameter does not exist
	 */
	public boolean getBoolValue(String parName) {
		boolean value = false;
		try {
			value = new Boolean(getValue(parName));
		} catch (Exception e) {
			System.out.println("Warning: parameter " + parName + " not set");
		}
		return value;
	}

	/**
	 * Value of a (string) parameter given by its name
	 * 
	 * @param parName
	 * @return value or "" if parameter does not exist
	 */
	public String getStringValue(String parName) {
		String value = "";
		try {
			value = getValue(parName);
		} catch (Exception e) {
			System.out.println("Warning: parameter " + parName + " not set");
		}
		return value;
	}

	/**
	 * Value of a (int array) parameter given by its name. <br>
	 * String value is a sequence of comma separated elements. <br>
	 * As an example, the string "1, 2, 4" would map to new int[] {1, 2, 4}
	 * 
	 * @param parName
	 * @return value or [0] if parameter does not exist
	 */
	public int[] getIntArray(String parName) {
		int[] value = new int[1];
		try {
			String s = getValue(parName);
			String[] ss = s.split(",");
			int n = ss.length;
			value = new int[n];
			for (int i = 0; i < n; i++) {
				float f = new Float(ss[i]);
				value[i] = (int) f;
			}
		} catch (Exception e) {
			System.out.println("Warning: parameter " + parName + " not set");
		}
		return value;
	}
	
	/**
	 * Value of a (float array) parameter given by its name. String value is a
	 * sequence of comma separated elements.
	 * 
	 * @param parName
	 * @return value or [0] if parameter does not exist
	 */
	public float[] getFloatArray(String parName) {
		float[] value = new float[1];
		try {
			String s = getValue(parName);
			String[] ss = s.split(",");
			int n = ss.length;
			value = new float[n];
			for (int i = 0; i < n; i++) {
				value[i] = new Float(ss[i]);
			}
		} catch (Exception e) {
			System.out.println("Warning: parameter " + parName + " not set");
		}
		return value;
	}

	public boolean[] getBoolArray(String parName) {
		boolean[] value = new boolean[1];
		try {
			String s = getValue(parName);
			String[] ss = s.split(",");
			int n = ss.length;
			value = new boolean[n];
			for (int i = 0; i < n; i++) {
				value[i] = new Boolean(ss[i].trim());
			}
		} catch (Exception e) {
			System.out.println("Warning: parameter " + parName + " not set");
		}
		return value;
	}
	
	public long[] getLongArray(String parName) {
		long[] value = new long[1];
		try {
			String s = getValue(parName);
			String[] ss = s.split(",");
			int n = ss.length;
			value = new long[n];
			for (int i = 0; i < n; i++) {
				value[i] = new Long(ss[i].trim());
			}
		} catch (Exception e) {
			System.out.println("Warning: parameter " + parName + " not set");
		}
		return value;
	}

	public String[] getStringArray(String parName) {
		String[] value = new String[1];
		value[0] = "";
		try {
			String s = getValue(parName);
			String[] ss = s.split(",");
			int n = ss.length;
			value = new String[n];
			for (int i = 0; i < n; i++) {
				value[i] = ss[i].trim();
			}
		} catch (Exception e) {
			System.out.println("Warning: parameter " + parName + " not set");
		}
		return value;
	}

	/**
	 * Value of a (int matrix) parameter given by its name. <br>
	 * String value is a sequence of semicolon separated, comma separated
	 * elements. <br>
	 * As an example, the string "1, 512 ; 2, 1024 ; 4, 2048" would map to new
	 * int[][] {{1, 512}, {2, 1024}, {4, 2048}}
	 * 
	 * @param parName
	 * @return value or [[0]] if parameter does not exist
	 */
	public int[][] getIntMatrix(String parName) {
		int[][] value = new int[1][1];
		try {
			String s = getValue(parName);
			String[] sRows = s.split(";");
			int n = sRows.length;
			value = new int[n][];
			for (int i = 0; i < n; i++) {
				String[] ss = sRows[i].split(",");
				int m = ss.length;
				value[i] = new int[m];
				for (int j = 0; j < m; j++) {
					float f = new Float(ss[j]);
					value[i][j] = (int) f;
				}
			}
		} catch (Exception e) {
			System.out.println("Warning: parameter " + parName + " not set");
		}
		return value;
	}
	
	/**
	 * Value of a (float matrix) parameter given by its name. <br>
	 * String value is a sequence of semicolon separated, comma separated
	 * elements. <br>
	 * As an example, the string "1, 512 ; 2, 1024 ; 4, 2048" would map to new
	 * float[][] {{1, 512}, {2, 1024}, {4, 2048}}
	 * 
	 * @param parName
	 * @return value or [[0]] if parameter does not exist
	 */
	public float[][] getFloatMatrix(String parName) {
		float[][] value = new float[1][1];
		try {
			String s = getValue(parName);
			String[] sRows = s.split(";");
			int n = sRows.length;
			value = new float[n][];
			for (int i = 0; i < n; i++) {
				String[] ss = sRows[i].split(",");
				int m = ss.length;
				value[i] = new float[m];
				for (int j = 0; j < m; j++) {
					value[i][j] = new Float(ss[j]);
				}
			}
		} catch (Exception e) {
			System.out.println("Warning: parameter " + parName + " not set");
		}
		return value;
	}
	
	public boolean[][] getBoolMatrix(String parName) {
		boolean[][] value = new boolean[1][1];
		try {
			String s = getValue(parName);
			String[] sRows = s.split(";");
			int n = sRows.length;
			value = new boolean[n][];
			for (int i = 0; i < n; i++) {
				String[] ss = sRows[i].split(",");
				int m = ss.length;
				value[i] = new boolean[m];
				for (int j = 0; j < m; j++) {
					value[i][j] = new Boolean(ss[j].trim());
				}
			}
		} catch (Exception e) {
			System.out.println("Warning: parameter " + parName + " not set");
		}
		return value;
	}
	
	public long[][] getLongMatrix(String parName) {
		long[][] value = new long[1][1];
		try {
			String s = getValue(parName);
			String[] sRows = s.split(";");
			int n = sRows.length;
			value = new long[n][];
			for (int i = 0; i < n; i++) {
				String[] ss = sRows[i].split(",");
				int m = ss.length;
				value[i] = new long[m];
				for (int j = 0; j < m; j++) {
					value[i][j] = new Long(ss[j].trim());
				}
			}
		} catch (Exception e) {
			System.out.println("Warning: parameter " + parName + " not set");
		}
		return value;
	}
	
	public String[][] getStringMatrix(String parName) {
		String[][] value = new String[1][1];
		value[0][0] = "";
		try {
			String s = getValue(parName);
			String[] sRows = s.split(";");
			int n = sRows.length;
			value = new String[n][];
			for (int i = 0; i < n; i++) {
				String[] ss = sRows[i].split(",");
				int m = ss.length;
				value[i] = new String[m];
				for (int j = 0; j < m; j++) {
					value[i][j] = ss[j].trim();
				}
			}
		} catch (Exception e) {
			System.out.println("Warning: parameter " + parName + " not set");
		}
		return value;
	}
	
	/**
	 * A dump of (sorted) parameters and their values
	 */
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("Parameters " + cfgFileName + ":");
		s.append("\n");

		if (props == null) {
			return s.toString();
		}

		Set<Object> keys = props.keySet();

		// sort by names
		List<String> keyList = new ArrayList<String>();
		for (Object key : keys) {
			keyList.add(key.toString());
		}
		Collections.sort(keyList);
		for (String key : keyList) {
			Object value = props.get(key);
			s.append(key + " = " + value);
			s.append("\n");
		}

		return s.toString();
	}

}
