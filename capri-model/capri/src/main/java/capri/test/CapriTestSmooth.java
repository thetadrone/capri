package capri.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import capri.advisor.BidAdvisor;
import data.DataProvider;

public class CapriTestSmooth {

	static DataProvider g;

	/**
	 * Testing Capri model
	 * 
	 * @param args
	 *            dataFile configFile
	 */
	public static void main(String[] args) {

		// define data source
		String dataFileName = (args.length > 0) ? args[0] : "bidData.txt";
		FileReader fr = null;
		BufferedReader in;

		try {
			fr = new FileReader(dataFileName);
			in = new BufferedReader(fr);

			g = new DataProvider(in);
		} catch (FileNotFoundException e) {
			System.err.print("File " + dataFileName + " not found. \n");
			return;
		}

		// create bid advisor
		String cfgFileName = (args.length > 1) ? args[1] : "capri.cfg";
		BidAdvisor bidAdvisor = new BidAdvisor(cfgFileName);

		// process data
		float[] data = getNextDataMatrix();

		while (data != null) {

			/*
			 * update filter using measured data
			 */
			float bid = data[0];
			float respTime = data[1];
			float servTime = data[2];
			
			bidAdvisor.update(bid, respTime, servTime);
			
			/*
			 * get estimates
			 */
			bidAdvisor.getParamaters();
			System.out.print("\n");

			/*
			 * read next data point
			 */
			data = getNextDataMatrix();
		}

	}

	public static float[] getNextDataMatrix() {
		int size = 3;
		double[] sample = g.getSample(size);
		float[] data = new float[3];

		if (sample == null) {
			return null;
		}

		float bid = (float) sample[0];
		float respTime = (float) sample[1];
		float servTime = (float) sample[2];

		data[0] = bid;
		data[1] = respTime;
		data[2] = servTime;

		return data;
	}

}
