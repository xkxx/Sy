package akme.mobile.util;

/**
 * Matrix utilities.
 * 
 * @author kmashint
 *
 */
public abstract class MatrixUtil {
	
	/** Herein lies the magic of cubic convolution. */
	private static float[][] CUBIC_CONVOLUTION_MATRIX = new float[][] {
			new float[] { 0,  2,  0,  0},
			new float[] {-1,  0,  1,  0},
			new float[] { 2, -5,  4, -1},
			new float[] {-1,  3, -3,  1},
		};

	/** Interpolation by cubic convolution works from points [-1, 0, 1, 2] and */
	private static float[] CUBIC_CONVOLUTION_SCALE = new float[] {-1, 2};

	
	public static float rescaleForCubicConvolution(float t, float from0, float from1) {
		return (t - from0) * (CUBIC_CONVOLUTION_SCALE[1]-CUBIC_CONVOLUTION_SCALE[0])/(from1-from0) + CUBIC_CONVOLUTION_SCALE[0];
	}
	
	/** Return an array of [1, t, t*t, t*t*t]. */
	public static float[] cubicArray(float t) {
		return new float[] {1, t, t*t, t*t*t};
	}
	
	/** Solve a 1-D cubic spline using cubic convolution -- simply beautiful. */
	public static float cubicConvolve(float[] t, float[] a) {
		return 0.5f * MatrixUtil.multiply( t,
				MatrixUtil.multiply(CUBIC_CONVOLUTION_MATRIX, a)
				);
	}
	
	/**
	 * Solve a 2-D cubic spline using bicubic convolution -- more beauty in relative simplicity.
	 * 
	 * This expects a2t to already be transposed so it doesn't need to keep re-transposing. 
	 * Resulting values work on the 2x2 matrix in the centre of the 4x4 matrix.
	 * Going outside the 2x2 centre will give increasingly worse results.
	 * The centre 2x2 is where x and y are both in [0,1].
	 * The 4x4 is sampled in the equidistant range [-1, 0, 1, 2] in two dimensions.
	 * 
	 * @param x is the x dimension of the desired result z.
	 * @param y is the y dimension of the desired result z.
	 * @param a2trans is the pre-transposed 4x4 matrix of point samples that define the surface.
	 * @return the z coordinate given x and y.
	 * 
	 * @see http://en.wikipedia.org/wiki/Bicubic
	 * @see http://dx.doi.org/10.1109%2FTASSP.1981.1163711
	 */
	public static float bicubicConvolve(float x, float y, float[][] a2t) {
		float[] x1 = cubicArray(x);
		float[] b1 = new float[] {
				cubicConvolve(x1, a2t[0]),
				cubicConvolve(x1, a2t[1]),
				cubicConvolve(x1, a2t[2]),
				cubicConvolve(x1, a2t[3])
		};
		float[] y1 = cubicArray(y);
		return cubicConvolve(y1, b1);
	}
	
	/**
	 * Multiply two vectors/arrays of values of the same size.
	 * This may throw an IndexOutOfBoundException if the matrix and vector are of different sizes.
	 */
	public static float multiply(final float[] v1, final float[] v2) {
		float r = 0f;
		for (int i=0; i<v1.length; i++) {
			r += v1[i] * v2[i];
		}
		return r;
	}
	
	/**
	 * Multiply a matrix with a vector all of the same size.
	 * This may throw an IndexOutOfBoundException if the matrix and vector are of different sizes.
	 */
	public static float[] multiply(final float[][] m1, final float[] v2) {
		float[] r = new float[v2.length];
		for (int i=0; i<v2.length; i++) {
			for (int j=0; j<v2.length; j++) {
				r[i] += m1[i][j] * v2[j];
			}
		}
		return r;
	}

	/**
	 * Multiply a vector matrix with a matrix all of the same size.
	 * This will throw an IndexOutOfBoundException if the matrix and vector are of different sizes.
	 */
	public static float[] multiply(final float[] v1, final float[][] m2) {
		float[] r = new float[v1.length];
		for (int i=0; i<v1.length; i++) {
			for (int j=0; j<v1.length; j++) {
				r[i] += m2[j][i] * v1[j];
			}
		}
		return r;
	}
	
	/** 
	 * Transpose the given square matrix, flipping it diagonally across the top-left to bottom-right axis
	 * so the top-right value moves the bottom-left and vice-versa.
	 */ 
	public static float[][] transpose(float[][] m) {
		float r;
		for (int i=0; i<m.length; i++) {
			for (int j=i+1; j<m.length; j++) {
				r = m[i][j];
				m[i][j] = m[j][i];
				m[j][i] = r;
			}
		}
		return m;
	}
	
}
