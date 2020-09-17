package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Provider of measurement data from file. Each line in the file represents one
 * data point. Each data point consists of the same number of measurements,
 * separated by tabs.
 * 
 * @author anonymous
 */
public class DataProvider {

	protected BufferedReader in;

	protected String s;

	protected String delim = "\t";

	/**
	 * Create a provider of measurement data from a file
	 * 
	 * @param in
	 */
	public DataProvider(BufferedReader in) {
		this.in = in;
	}
	
	public DataProvider(BufferedReader in, String delim) {
		this(in);
		this.delim = delim;
	}

	/**
	 * get next sample data vector (one line from the file)
	 * 
	 * @param size
	 *            number of elements in vector
	 * @return
	 */
	public double[] getSample(int size) {

		double[] z = new double[size];
		int numColums = 0;

		try {
			s = in.readLine();
			if (s == null) {
				return null;
			}
			StringTokenizer st = new StringTokenizer(s, delim);

			while (true) {
				String field;
				try {
					field = st.nextToken();
				} catch (NoSuchElementException e) {
					break;
				}
				Float F = new Float(field);
				float value = F.floatValue();
				if (numColums < size) {
					z[numColums] = value;
					// System.out.print(z[numColums] + "\t");
				}
				numColums++;
			}
			
			// System.out.print("\n");
		} catch (IOException e) {
			System.out.print("Caught exception: " + e);
			return null;
		}
		return z;
	}
	
	public String[] getSample() {
		String[] z;
		try {
			s = in.readLine();
			if (s == null) {
				return null;
			}
			StringTokenizer st = new StringTokenizer(s, delim);
			int n = st.countTokens();
			z = new String[n];

			for (int i = 0; i < n; i++) {
				z[i] = st.nextToken();
			}

			// System.out.print("\n");
		} catch (IOException e) {
			System.out.print("Caught exception: " + e);
			return null;
		}
		return z;
	}
	
	public String getLine() {
		try {
			s = in.readLine();
			// System.out.print("\n");
		} catch (IOException e) {
			System.out.print("Caught exception: " + e);
			return null;
		}
		return s;
	}

	public String getOriginalUnParsedRecord() {
		return s;
	}
}
