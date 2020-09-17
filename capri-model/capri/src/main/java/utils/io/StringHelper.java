package utils.io;

/**
 * @author anonymous
 *
 */
public class StringHelper {

	/**
	 * Constructor for PrintHelper.
	 */
	public StringHelper() {
		super();
	}

	public String vectorPrint(String name, int[] x) {
		int n = x.length;
		int i;
		StringBuffer s = new StringBuffer();
		s.append(name + ":\n");
		for (i = 0; i < n; i++) {
			s.append(x[i] + "  ");
		}
		s.append("\n");
		return s.toString();
	}

	public String vectorPrint(String name, float[] x) {
		int n = x.length;
		int i;
		StringBuffer s = new StringBuffer();
		s.append(name + ":\n");
		for (i = 0; i < n; i++) {
			s.append(x[i] + "  ");
		}
		s.append("\n");
		return s.toString();
	}

	public  String vectorPrint(String name, double[] x) {
		int n = x.length;
		int i;
		StringBuffer s = new StringBuffer();
		s.append(name + ":\n");
		for (i = 0; i < n; i++) {
			s.append(x[i] + "  ");
		}
		s.append("\n");
		return s.toString();
	}
	
	public String vectorPrint(String name, String[] x) {
		int n = x.length;
		int i;
		StringBuffer s = new StringBuffer();
		s.append(name + ":\n");
		for (i = 0; i < n; i++) {
			s.append(x[i] + "  ");
		}
		s.append("\n");
		return s.toString();
	}
	
	public String matrixPrint(String name, int[][] x) {
		int n = x.length;
		int i, j;
		StringBuffer s = new StringBuffer();
		s.append(name + ":\n");
		for (i = 0; i < n; i++) {
			int m = x[i].length;
			for (j = 0; j < m; j++) {
				s.append(x[i][j] + "  ");
			}
			s.append("\n");
		}
		return s.toString();
	}

	public  String matrixPrint(String name, float[][] x) {
		int n = x.length;
		int i, j;
		StringBuffer s = new StringBuffer();
		s.append(name + ":\n");
		for (i = 0; i < n; i++) {
			int m = x[i].length;
			for (j = 0; j < m; j++) {
				s.append(x[i][j] + "  ");
			}
			s.append("\n");
		}
		return s.toString();
	}
	
	public String threeDPrint(String name, int[][][] x) {
		int n = x.length;
		int m = x[0].length;
		int p = x[0][0].length;

		StringBuffer s = new StringBuffer();
		s.append(name + ":\n");
		for (int k = 0; k < p; k++) {
			s.append(k+"\n");
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < m; j++) {
					s.append(x[i][j][k] + "  ");
				}
				s.append("\n");
			}
			s.append("\n");
		}
		return s.toString();
	}
}

