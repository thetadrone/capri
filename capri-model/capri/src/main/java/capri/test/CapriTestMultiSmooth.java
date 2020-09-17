package capri.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import capri.advisor.BidAdvisor;
import data.DataProvider;

public class CapriTestMultiSmooth {

	static DataProvider g;

	/**
	 * Testing Capri model
	 * 
	 * @param args
	 *            dataFile configFile
	 */
	public static void main(String[] args) {

		// define data source
		String fName = (args.length > 0) ? args[0] : "bidDataMulti.txt";
		FileReader fr = null;
		BufferedReader in;

		try {
			fr = new FileReader(fName);
			in = new BufferedReader(fr);

			g = new DataProvider(in);
		} catch (FileNotFoundException e) {
			System.err.print("File " + fName + " not found. \n");
			return;
		}

		// create bid advisor
		int numClasses = 3;

		String cfgFileName = (args.length > 1) ? args[1] : "capri.cfg";
		BidAdvisor bidAdvisor = new BidAdvisor(cfgFileName);
		
		BidAdvisor[] bidAdvisorClass = new BidAdvisor[numClasses];
		for (int k = 0; k < numClasses; k++) {
			bidAdvisorClass[k] = new BidAdvisor(cfgFileName, bidAdvisor.getCapriModel());
			bidAdvisorClass[k].setPrint(false);
			bidAdvisorClass[k].setPrintPred(true);
		}

		// process data
		float[] data = getNextDataMatrix();

		while (data != null) {
			
			/*
			 * update filter using measured data
			 */
			int cls = (int) data[0] - 1;
			float bid = data[1];
			float respTime = data[2];
			float servTime = data[3];
			
			bidAdvisor.update(bid, respTime, servTime);
			bidAdvisorClass[cls].update(bid, respTime, servTime);
			
			/*
			 * get estimates
			 */
			bidAdvisor.getParamaters();
			
			System.out.print("eta=[ ");
			for (int k = 0; k < numClasses; k++) {
				float ek = bidAdvisorClass[k].getParamaters()[2];
				System.out.print(String.format("%.5f ", ek));
			}
			System.out.print("] \t");
			System.out.print("class=" + cls + "\t");
			System.out.print("\n");

			/*
			 * read next data point
			 */
			data = getNextDataMatrix();
		}

	}

	public static float[] getNextDataMatrix() {
		int size = 4;
		double[] sample = g.getSample(size);
		float[] data = new float[4];

		if (sample == null) {
			return null;
		}

		float c = (float) sample[0];
		float bid = (float) sample[1];
		float respTime = (float) sample[2];
		float servTime = (float) sample[3];

		data[0] = c;
		data[1] = bid;
		data[2] = respTime;
		data[3] = servTime;

		return data;
	}

}
