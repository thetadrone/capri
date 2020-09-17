package capri.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import capri.interfaces.Capri;
import capri.interfaces.CapriFactory;
import data.DataProvider;

public class CapriTestMulti {

	static DataProvider g;
	
	/**
	 * Testing Capri model
	 * 
	 * @param args dataFile configFile
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

		Capri capriModel = (args.length > 1) ? CapriFactory.create(args[1]) : CapriFactory.create();
		
		int numClasses = 3;
		Capri[] capriModelMulti = new Capri[numClasses];
		for (int k = 0; k < numClasses; k++) {
			capriModelMulti[k] = (args.length > 1) ? CapriFactory.createMulti(args[1], capriModel)
					: CapriFactory.createMulti(capriModel);
		}
		
		// process data		
		int k = 0;
		float[] data = getNextDataMatrix();
		
		while (data != null) {
			
			/*
			 * estimate next slowdown
			 */
			float servTime = data[3] - data[2];
			float measuredSD = data[3] / servTime;
			float[] range = new float[2];
			float estimatedSD = capriModel.getSlowDown(data[1], servTime, range);

			System.out.print("measuredSD=" + measuredSD + "\t");
			System.out.print("estimatedAvgSD="+ estimatedSD + "\t");
			System.out.print("range=[" + range[0] + "," + range[1] + "]" + "\t");
			System.out.print("\n");
			
			/*
			 * get a bid advice
			 */
			float targetSlowDown = estimatedSD;
			float bidAdvised = capriModel.getBid(targetSlowDown, servTime);
			System.out.println("targetSlowDown=" + targetSlowDown + "\t"
					+ "bidAdvised=" + bidAdvised);
			
			/*
			 * update filter using measured data
			 */
			String id = Integer.toString(k);
			capriModel.update(id, data[1], data[2], data[3]);
			
			float[] parms = capriModel.getModelParameters();
			int numStates = parms.length - 2;
			
			int c = (int) data[0];
			capriModelMulti[c - 1].update(id, data[1], data[2], data[3]);
			float eta = capriModelMulti[c - 1].getModelParameters()[2];
			
			System.out.print("alpha=" + parms[0] + "\t");
			System.out.print("beta=" + parms[1] + "\t");
			System.out.print("theta=[ ");
			for (int i = 0; i < numStates; i++) {
				System.out.print(parms[2 + i] + " ");
			}
			System.out.print("]; \t");
			
			System.out.print("eta[" + c + "]=" + eta + "\t");
			
			System.out.print("\n");
			
			data = getNextDataMatrix();
			k++;
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
		data[2] = respTime - servTime;
		data[3] = respTime;

		return data;
	}

}
