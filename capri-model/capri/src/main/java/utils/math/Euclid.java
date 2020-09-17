package utils.math;
public class Euclid {

	/**
	 * The Euclidean Algorithm
	 * 
	 * Input: strictly positive integers
	 * Output: GCD
	 */
	public Euclid() {
		super();
	}

	/**
	 * GCD coefficient of two integers
	 * @param int1 positive integer
	 * @param int2 positive integer
	 * @return gcd(int1, int2)
	 */
	static public int gcd(int int1, int int2) {
		int b, c;
		if (int1 <= int2) {
			b = int1;
			c = int2;
		} else {
			b = int2;
			c = int1;
		}

		int r = b;

		while (r > 0) {
			int q = c / b;
			r = c - q * b;
			if (r == 0) {
				return b;
			} else {
				c = b;
				b = r;
			}
		}
		return 0;
	}
	
	/**
	 * GCD coefficient (through inefficient recursive algorithm)
	 * @param int1 positive integer
	 * @param int2 positive integer
	 * @return gcd(int1, int2)
	 */
	static public int gcdRecursive (int b, int c) {
		int g = (b <= c) ? b : c;
		while (g > 1) {
			if ((b - (b / g) * g == 0) && (c - (c / g) * g == 0)) {
				return g;
			}
			g--;
		}
		return 1;	
	}
	
	/**
	 * GCD coefficient of a list of integers
	 * @param a array of two or more positive integers
	 * @return GCD
	 */
	static public int gcd(int[] a) {
		int n = a.length;
		if(n == 1) {
			return a[0];
		}
		int g = gcd(a[0], a[1]);
		for (int i = 2; i < n; i++) {
			g = gcd(g, a[i]);
		}
		return g;
	}
}
