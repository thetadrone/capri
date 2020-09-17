package utils.io;

/**
 * @author anonymous
 *
 */
public class PrintHelper {

	/**
	 * Constructor for PrintHelper.
	 */
	public PrintHelper() {
		super();
	}

	public void vectorPrint(String name, int[] x) {
		int n = x.length;
		int i;
		System.out.print("\n" + name + ":\n");
		for (i = 0; i < n; i++) {
			System.out.print(x[i] + "  ");
		}
		System.out.print("\n");
	}

	public void vectorPrint(String name, float[] x) {
		int n = x.length;
		int i;
		System.out.print("\n" + name + ":\n");
		for (i = 0; i < n; i++) {
			System.out.print(x[i] + "  ");
		}
		System.out.print("\n");
	}

	public void vectorPrint(String name, double[] x) {
		int n = x.length;
		int i;
		System.out.print("\n" + name + ":\n");
		for (i = 0; i < n; i++) {
			System.out.print(x[i] + "  ");
		}
		System.out.print("\n");
	}
	
	public void matrixPrint(String name, int[][] x) {
		int n = x.length;
		int i, j;
		System.out.print("\n" + name + ":\n");
		for (i = 0; i < n; i++) {
			int m = x[i].length;
			for (j = 0; j < m; j++) {
				System.out.print(x[i][j] + "  ");
			}
			System.out.print("\n");
		}
	}

	public void matrixPrint(String name, float[][] x) {
		int n = x.length;
		int i, j;
		System.out.print("\n" + name + ":\n");
		for (i = 0; i < n; i++) {
			int m = x[i].length;
			for (j = 0; j < m; j++) {
				System.out.print(x[i][j] + "  ");
			}
			System.out.print("\n");
		}
	}
	
	public void matrixPrint(String name, double[][] x) {
		int n = x.length;
		int i, j;
		System.out.print("\n" + name + ":\n");
		for (i = 0; i < n; i++) {
			int m = x[i].length;
			for (j = 0; j < m; j++) {
				System.out.print(x[i][j] + "  ");
			}
			System.out.print("\n");
		}
	}

	public void threeDPrint(String name, float[][][] x) {
		int n = x.length;
		int m = x[0].length;
		int p = x[0][0].length;

		System.out.print("\n" + name + ":\n");
		for (int k = 0; k < p; k++) {
			System.out.print(k+"\n");
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < m; j++) {
					System.out.print(
							x[i][j][k]
							+ " ");
				}
				System.out.print("\n");
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}
	
	public void threeDPrint(String name, double[][][] x) {
		int n = x.length;
		int m = x[0].length;
		int p = x[0][0].length;

		System.out.print("\n" + name + ":\n");
		for (int k = 0; k < p; k++) {
			System.out.print(k+"\n");
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < m; j++) {
					System.out.print(
							x[i][j][k]
							+ " ");
				}
				System.out.print("\n");
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}
		
	public void multiMatrixPrint(String name, double[][][][] x) {
		int n = x.length;
		int m = x[0].length;
		int p = x[0][0].length;
		int q = x[0][0][0].length;

		System.out.print("\n" + name + ":\n");
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				for (int k = 0; k < p; k++) {
					for (int l = 0; l < q; l++) {
						System.out.print(
							name
								+ "["
								+ i
								+ "]["
								+ j
								+ "]["
								+ k
								+ "]["
								+ l
								+ "]="
								+ x[i][j][k][l]
								+ "\n");
					}
				}
			}
		}
		System.out.print("\n");
	}
}

