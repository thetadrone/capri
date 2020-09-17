/*
 * Created on Mar 24, 2006
 */
package utils.io;

/**
 * @author anonymous
 */
public class Tabulate {

	/**
	 * 
	 */
	public Tabulate() {
		
	}
	
	public String toString(float[][] table){
		int numRows = table[0].length;
		int numColumns = table.length;
		StringBuffer s = new StringBuffer();
		
		s.append("\n");
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numColumns; j++) {
				s.append(table[j][i] + "\t");
			}
			s.append("\n");
		}
		
		return s.toString();
	}

}
