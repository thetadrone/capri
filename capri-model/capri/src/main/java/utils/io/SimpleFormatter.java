package utils.io;

public class SimpleFormatter {

	public static StringBuffer arrayToString(StringBuffer s, String arrayName,
			float[] array) {
		s.append(arrayName + " = ");
		if(array == null) {
			s.append("null" + "\n");
			return s;
		}
		s.append("[");
		for (int i = 0; i < array.length; i++) {
			s.append(array[i] + " ");
		}
		s.append("]" + "\n");
		return s;
	}
	
	public static StringBuffer arrayToString(StringBuffer s, String arrayName,
			int[] array) {
		s.append(arrayName + " = ");
		if(array == null) {
			s.append("null" + "\n");
			return s;
		}
		s.append("[");
		for (int i = 0; i < array.length; i++) {
			s.append(array[i] + " ");
		}
		s.append("]" + "\n");
		return s;
	}

	public static StringBuffer doubleArrayToString(StringBuffer s,
			String arrayName, float[][] array) {
		s.append(arrayName + " = ");
		if(array == null) {
			s.append("null" + "\n");
			return s;
		}
		s.append("[");
		for (int i = 0; i < array.length; i++) {
			s.append("[");
			for (int j = 0; j < array[i].length; j++) {
				s.append(array[i][j] + " ");
			}
			s.append("]");
			s.append("\n");
		}
		s.append("]");
		s.append("\n");
		return s;
	}
	
	public static StringBuffer multipleArrayToString(StringBuffer s,
			String arrayName, float[][][][] array) {
		s.append(arrayName + " = ");
		if(array == null) {
			s.append("null" + "\n");
			return s;
		}
		s.append("[");
		for (int i = 0; i < array.length; i++) {
			s.append("[");
			for (int j = 0; j < array[i].length; j++) {
				s.append("[");
				for (int k = 0; k < array[i][j].length; k++) {
					s.append("[");
					for (int l = 0; l < array[i][j][k].length; l++) {
						s.append(array[i][j][k][l] + " ");
					}
					s.append("]");
				}
				s.append("]");
			}
			s.append("]");
		}
		s.append("]" + "\n");
		return s;
	}
}
