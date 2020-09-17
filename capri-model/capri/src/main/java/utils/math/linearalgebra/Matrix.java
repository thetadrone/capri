package utils.math.linearalgebra;

/*
* Created on May 11, 2004
*/

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
* Matrix
* <p>
* @author anonymous
*/
public class Matrix
{
 private double[][] fData = null;
 
 private int fSingularIndex = -1;

 /**
  * Constructor
  */
 public Matrix()
 {
     super();
 }
 
 public Matrix( Matrix m )
 {
     if (m.fData == null)
     {
         fData = null;
     }
     else
     {
         int rows = m.getRows();
         int cols = m.getColumns();
         fData = new double[rows][cols];
         for (int r=0; r<rows; r++)
         {
             System.arraycopy(m.fData[r], 0, fData[r], 0, cols);
         }
     }
     fSingularIndex = m.fSingularIndex;
 }

 /**
  * Constructor with initialization string.
  * Initialized the matrix with the contents of the string.
  * @param s syntax of the string is: 
  * <pre>
  * matrix := "{" row *( "," row ) "}"
  * row := "{" val *( "," val ) "}"
  * val := double
  * </pre>
  * The number of values in each row must be the same.
  * @throws MatrixException
  */
 public Matrix(String s) throws MatrixException
 {
     load(s);
 }

 /**
  * Constructor
  * @param data the data.  The matrix will take ownership of data.
  */
 public Matrix(double[][] data)
 {
     super();
     fData = data;
 }
 
 /**
  * Constructor of identity square matrix
  * @param size of matrix
  */
public Matrix(int size)
{
	super();
	fData = new double[size][size];
	for (int i = 0; i < size; i++) {
		fData[i][i] = 1;
	}
}
 
 /**
  * Add or remove rows and columns to/from the matrix.
  * If rows or columns are added the added elements get initialized with zero.
  * If rows or columns are removed, the removed elements are lost.
  * @param rows new row size.
  * @param cols new col size.
  */
 public void resize(int rows, int cols)
 {
     double[][] m = new double[rows][cols];

     int currentRows = getRows();
     int currentCols = getColumns();

     int nRows = (rows < currentRows) ? rows : currentRows;
     int nCols = (cols < currentCols) ? cols : currentCols;

     int ir = 0;

     for (ir = 0; ir < nRows; ir++)
     {
         int ic = 0;
         for (ic = 0; ic < nCols; ic++)
         {
             m[ir][ic] = fData[ir][ic];
         }
         for (; ic < cols; ic++)
         {
             m[ir][ic] = 0;
         }
     }
     for (; ir < rows; ir++)
     {
         for (int ic = 0; ic < cols; ic++)
         {
             m[ir][ic] = 0;
         }
     }
     fData = m;
 }

 /**
  * Get a value.
  * @param row the row
  * @param column the Column
  * @return the value
  */
 public double get(int row, int column)
 {
     return fData[row][column];
 }

 /**
  * Set a value
  * @param row the row
  * @param column the column
  * @param value the new value
  */
 public void set(int row, int column, double value)
 {
     fData[row][column] = value;
 }

 /**
  * Get the number of rows.
  * @return the number of rows.
  */
 public int getRows()
 {
     return fData.length;
 }

 /**
  * Get the number of columns.
  * @return the number of columns.
  */
 public int getColumns()
 {
     if (fData.length != 0)
     {
         return fData[0].length;
     }
     return 0;
 }

 /**
  * Multiply two matrices together.  The number of columns in this matrix must match 
  * the number of rows in the right matrix.
  * @param right the right matrix.
  * @return this*right
  * @throws MatrixException
  */
 public Matrix multiply(Matrix right) throws MatrixException
 {
     if (getColumns() != right.getRows())
     {
         throw new MatrixException("Number of rows in right not equal to number of columns in left", true);
     }

     int nr = getRows();
     int nc = right.getColumns();

     double[][] m = new double[nr][nc];

     int l = getColumns();

     for (int ir = 0; ir < nr; ir++)
     {
         for (int ic = 0; ic < nc; ic++)
         {
             double sum = 0;
             for (int i = 0; i < l; i++)
             {
                 sum += get(ir, i) * right.get(i, ic);
             }
             m[ir][ic] = sum;
         }
     }
     return new Matrix(m);
 }

 /**
  * Multiply two matrices together.  The number of columns in this matrix must match 
  * right.length.  The result will be a column vector with a length equal to the number
  * of rows in this matrix.
  * @param right the right column vector.
  * @return this*right, a column vector with this.{@link #getRows()} entries.
  * @throws MatrixException
  */
 public double[] multiply(double[] right) throws MatrixException
 {
     if (getColumns() != right.length)
     {
         throw new MatrixException("Number of entries in the right column vector not equal to number of columns in left", true);
     }

     int nr = getRows();

     double[] m = new double[nr];

     int l = getColumns();

     for (int ir = 0; ir < nr; ir++)
     {
         double sum = 0;
         for (int i = 0; i < l; i++)
         {
             sum += get(ir, i) * right[i];
         }
         m[ir] = sum;
     }
     return m;
 }

// /**
//  * Find the inverse of the matrix.
//  * @return the inverse matrix.
//  * @throws MatrixException if there is no inverse or if the matrix is not square.
//  */
// public Matrix inverseOld() throws MatrixException
// {
//     if (getColumns() != getRows())
//     {
//         throw new MatrixException("Matrix must be square", true);
//     }
//
//     int n = getRows();
//
//     double[][] m = new double[n][n];
//     double[][] r = new double[n][n];
//
//     for (int i = 0; i < n; i++)
//     {
//         for (int j = 0; j < n; j++)
//         {
//             m[i][j] = fData[i][j];
//             r[i][j] = (i == j) ? 1 : 0;
//         }
//     }
//
//     for (int i = 0; i < n; i++)
//     {
//         int max = findMax(m, i, i);
//         if (max != i)
//         {
//             pivot(m, i, max);
//             pivot(r, i, max);
//         }
//         if (m[max][max] == 0)
//         {
//             throw new MatrixException("Matrix has no inverse");
//         }
//
//         for (int j = 0; j < n; j++)
//         {
//             if (i != j)
//             {
//                 double multiplier = m[j][i] / m[i][i];
//                 modify(m, i, multiplier, j);
//                 modify(r, i, multiplier, j);
//             }
//         }
//         double mult = m[i][i];
//         normalize(m, i, mult);
//         normalize(r, i, mult);
//     }
//
//     return new Matrix(r);
// }

 /**
  * Find the inverse of the matrix.
  * @return the inverse matrix.
  * @throws MatrixException if there is no inverse or if the matrix is not square.
  */
 public Matrix inverse() throws MatrixException
 {
     if (getColumns() != getRows())
     {
         throw new MatrixException("Matrix must be square", true);
     }

     int n = getRows();

     double[][] m = new double[n][n];
     double[][] r = new double[n][n];

     for (int i = 0; i < n; i++)
     {
         for (int j = 0; j < n; j++)
         {
             m[i][j] = fData[i][j];
             r[i][j] = 0;
         }
         r[i][i] = 1;
     }

     // first make matrix m an upper triangular matrix
     for (int i = 0; i < n; i++)
     {
         int max = findMax(m, i, i);
         if (max != i)
         {
             pivot(m, i, max);
             pivot(r, i, max);
         }
         if (m[max][max] == 0)
         {
             fSingularIndex = max;
             throw new MatrixException("Matrix has no inverse");
         }

         double mult = m[i][i];
         normalize(m, i, mult);
         normalize(r, i, mult);
         for (int j = i+1; j < n; j++)
         {
             double multiplier = m[j][i] ;
             modify(m, i, multiplier, j);
             modify(r, i, multiplier, j);
         }
     }
     
     // matrix m is an upper triangular matrix
     // make matrix m an identity matrix.
     // No need to modify matrix m in the following loop. 
     // m is not used after this, and the changes that would be made to m
     // in the loop aren't overlapping.
     // If m is needed, then just set m[i][j] to zero where 0 <= i < j < n
     for (int i=n-1; i>0; i--)
     {
         for (int j=0; j<i; j++)
         {
             double multiplier = m[j][i];
//             modify(m, i, multiplier, j);   // not needed, only sets m[j][i] to zero.
             modify(r, i, multiplier, j);
         }
     }

     return new Matrix(r);
 }

 /**
  * Solves AX = Y.  A is this matrix.  
  * This must be a square matrix, of the same size as Y.
  * @param y the Y vector
  * @return the X vector.  Null is returned if the matrix is singular, 
  * the {@link #getSingularIndex()} method can be used to get the 
  * index at which the singular matrix was detected.
  * @throws MatrixException if matrix is not square,
  * matrix and vector are not the same size. 
  */
 public double[] solve(double[] y) throws MatrixException
 {
     fSingularIndex = -1;
     if (getColumns() != getRows())
     {
         throw new MatrixException("Matrix must be square", true);
     }
     if (getRows() != y.length)
     {
         throw new MatrixException("Matrix and vector must be same size", true);
     }

     int n = getRows();

     double[][] m = new double[n][n];
     double[] r = new double[n];

     for (int i = 0; i < n; i++)
     {
         for (int j = 0; j < n; j++)
         {
             m[i][j] = fData[i][j];
         }
         r[i] = y[i];
     }

     for (int i = 0; i < n; i++)
     {
         int max = findMax(m, i, i);
         if (max != i)
         {
             pivot(m, i, max);
             pivot(r, i, max);
         }
         if (m[max][max] == 0)
         {
             fSingularIndex = i;
             return null;
         }

         for (int j = 0; j < n; j++)
         {
             if (i != j)
             {
                 double multiplier = m[j][i] / m[i][i];
                 modify(m, i, multiplier, j);
                 modify(r, i, multiplier, j);
             }
         }
         double mult = m[i][i];
         normalize(m, i, mult);
         normalize(r, i, mult);
     }

     return r;
 }
 
 public int getSingularIndex()
 {
     return fSingularIndex;
 }

 /**
  * m[r2][i] = m[r2][i] - multiplier*m[r1][i]
  * @param m
  * @param r1
  * @param multiplier
  * @param r2
  */
 private void modify(double[][] m, int r1, double multiplier, int r2)
 {
     int l = m.length;
     for (int i = 0; i < l; i++)
     {
         m[r2][i] = m[r2][i] - multiplier * m[r1][i];
     }
 }
 private void modify(double[] m, int r1, double multiplier, int r2)
 {
     m[r2] = m[r2] - multiplier * m[r1];
 }

 /**
  * m[r1][i] = m[r1][i]/mult
  * @param m
  * @param r1
  * @param mult
  */
 private void normalize(double[][] m, int r1, double mult)
 {
     int l = m.length;
     for (int i = 0; i < l; i++)
     {
         m[r1][i] = m[r1][i] / mult;
     }
 }
 private void normalize(double[] m, int r1, double mult)
 {
     m[r1] = m[r1] / mult;
 }

 /**
  * Switch two rows.
  * @param m the matrix
  * @param r1 one of the rows
  * @param r2 the other row
  */
 private void pivot(double[][] m, int r1, int r2)
 {
     double[] t = m[r1];
     m[r1] = m[r2];
     m[r2] = t;
 }
 private void pivot(double[] m, int r1, int r2)
 {
     double t = m[r1];
     m[r1] = m[r2];
     m[r2] = t;
 }

 /**
  * Find the max value in the specified column in a row larger than the specified row.
  * @param m the matrix
  * @param r1 the row
  * @param col the col
  * @return the row that contains the largest absolute value in the specified column.
  */
 private int findMax(double[][] m, int r1, int col)
 {
     int maxrow = r1;
     double maxvalue = m[r1][col];
     if (maxvalue < 0)
     {
         maxvalue = -maxvalue;
     }
     int l = m.length;
     for (int i = r1 + 1; i < l; i++)
     {
         double t = m[i][col];
         if (t < 0)
         {
             t = -t;
         }
         if (maxvalue < t)
         {
             maxvalue = t;
             maxrow = i;
         }
     }
     return maxrow;
 }

 /* (non-Javadoc)
  * @see java.lang.Object#toString()
  */
 @Override
 public String toString()
 {
     StringBuilder buf = new StringBuilder();

     buf.append("{");
     int nr = getRows();
     int nc = getColumns();
     for (int ir = 0; ir < nr; ir++)
     {
         if (ir != 0)
         {
             buf.append(",");
         }
         buf.append("{");
         for (int ic = 0; ic < nc; ic++)
         {
             if (ic != 0)
             {
                 buf.append(",");
             }
             buf.append(get(ir, ic));
         }
         buf.append("}");
     }
     buf.append("}");

     return buf.toString();
 }

 /**
  * Initialized the matrix with the contents of the string.
  * @param s syntax of the string is: 
  * <pre>
  * matrix := "{" row *( "," row ) "}"
  * row := "{" val *( "," val ) "}"
  * val := double
  * </pre>
  * The number of values in each row must be the same.
  * @throws MatrixException
  */
 private void load(String s) throws MatrixException
 {
     List<List<Double>> d = new ArrayList<List<Double>>();
     List<Double> r = null;

     int curly = 0;

     StringTokenizer tok = new StringTokenizer(s, "{}, \n\r", true);
     while (tok.hasMoreTokens())
     {
         String t = tok.nextToken();
         if (t.equals("{"))
         {
             curly++;
             if (curly == 1)
             {
                 // nothing to do
             }
             else if (curly == 2)
             {
                 r = new ArrayList<Double>();
             }
             else
             {
                 throw new MatrixException("extra {", true);
             }
         }
         else if (t.equals("}"))
         {
             if (curly == 2)
             {
                 d.add(r);
                 r = null;
             }
             else if (curly == 1)
             {
                 break;
             }
             curly--;
         }
         else if (
             t.equals(",")
                 || t.equals(" ")
                 || t.equals("\r")
                 || t.equals("\n"))
         {
             // nothing to do
         }
         else
         {
             double v = Double.parseDouble(t);
             r.add(new Double(v));
         }
     }

     int nr = d.size();
     if (nr == 0)
     {
         throw new MatrixException("matrix has 0 rows", true);
     }
     r = d.get(0);
     int nc = r.size();
     if (nc == 0)
     {
         throw new MatrixException("matrix has 0 cols", true);
     }

     fData = new double[nr][nc];
     for (int ir = 0; ir < nr; ir++)
     {
         r = d.get(ir);
         if (r.size() != nc)
         {
             throw new MatrixException("matrix rows don't have the same number of cols", true);
         }
         for (int ic = 0; ic < nc; ic++)
         {
             fData[ir][ic] = (r.get(ic)).doubleValue();
         }
     }

 }

 /**
  * Find the transpose of the matrix.
  * @return the transposed matrix.
  */
 public Matrix transpose()
 {
     if (fData.length == 0)
     {
         return null;
     }
     double[][] d = new double[fData[0].length][fData.length];

     int nr = getRows();
     int nc = getColumns();

     for (int ir = 0; ir < nr; ir++)
     {
         for (int ic = 0; ic < nc; ic++)
         {
             d[ic][ir] = fData[ir][ic];
         }
     }

     return new Matrix(d);
 }

 /**
  * Subtract the specified matrix from this matrix.  The specified matrix and this matrix must be the same size.
  * The result matrix will be the same size.  This matrix is not changed.
  * @param o the specified matrix.
  * @return the resulting matrix.
  * @throws MatrixException
  */
 public Matrix subtract(Matrix o) throws MatrixException
 {
     int nr = getRows();
     int nc = getColumns();
     if (nr != o.getRows() && nc != o.getColumns())
     {
         throw new MatrixException("Matrix not same size", true);
     }

     double[][] d = new double[nr][nc];
     for (int ir = 0; ir < nr; ir++)
     {
         for (int ic = 0; ic < nc; ic++)
         {
             d[ir][ic] = fData[ir][ic] - o.get(ir, ic);
         }
     }

     return new Matrix(d);
 }
 /**
  * Add the specified matrix to this matrix.  The specified matrix and this matrix must be the same size.
  * The result matrix will be the same size.  This matrix is not changed.
  * @param o the specified matrix.
  * @return the resulting matrix.
  * @throws MatrixException
  */
 public Matrix add(Matrix o) throws MatrixException
 {
     int nr = getRows();
     int nc = getColumns();
     if (nr != o.getRows() && nc != o.getColumns())
     {
         throw new MatrixException("Matrix not same size", true);
     }

     double[][] d = new double[nr][nc];
     for (int ir = 0; ir < nr; ir++)
     {
         for (int ic = 0; ic < nc; ic++)
         {
             d[ir][ic] = fData[ir][ic] + o.get(ir, ic);
         }
     }

     return new Matrix(d);
 }

 /**
  * Multiply this matrix by a scaler.  this matrix is changed.
  * @param scaler
  */
 public void multiply(double scaler)
 {
     int nr = getRows();
     int nc = getColumns();

     for (int ir = 0; ir < nr; ir++)
     {
         for (int ic = 0; ic < nc; ic++)
         {
             fData[ir][ic] = fData[ir][ic] * scaler;
         }
     }
 }

 /**
  * Remove a row and column from the matrix.  The matrix must be square.
  * The matrix is changed in place.
  * @param n The row and column to remove.
  * @throws MatrixException if matrix is not square, 
  * or if n is negative.
  */
 public void remove(int n) throws MatrixException
 {

     int currentRows = getRows();
     int currentCols = getColumns();

     if (currentRows != currentCols)
     {
         throw new MatrixException("Must be square", true);
     }
     if (n >= currentRows)
     {
         return;
     }
     if (n < 0)
     {
         throw new IllegalArgumentException("Must not be negative");
     }

     double[][] m = new double[currentRows - 1][currentCols - 1];

     int ir = 0;

     for (ir = 0; ir < n; ir++)
     {
         int ic = 0;
         for (ic = 0; ic < n; ic++)
         {
             m[ir][ic] = fData[ir][ic];
         }
         for (ic++; ic < currentCols; ic++)
         {
             m[ir][ic - 1] = fData[ir][ic];
         }
     }
     for (ir++; ir < currentRows; ir++)
     {
         int ic = 0;
         for (ic = 0; ic < n; ic++)
         {
             m[ir - 1][ic] = fData[ir][ic];
         }
         for (ic++; ic < currentCols; ic++)
         {
             m[ir - 1][ic - 1] = fData[ir][ic];
         }
     }
     fData = m;
 }

 /**
  * Combine row/col i and parameter j.  
  * The resulting matrix will have the combined 
  * row/col at the min of i and j.
  * The max of i and j row and column are deleted from the matrix.
  * The matrix must be square.
  * The matrix is changed in place.
  * <p>
  * If i or j is out of range or equal the matrix is not modified.
  * <p>
  * <pre>
  * min = min(i,j)
  * max = max(i,j)
  * 
  * r: all 0<=r<getRows(), r!=i, r!=j
  * 
  * d[min][r] = d[min][r] + d[max][r]
  * d[r][min] = d[r][min] + d[r][max]
  * d[min][min] = d[min][min] + d[min][max] + d[max][min] + d[max][max]
  * remove(max)
  * 
  * </pre>
  * @param i
  * @param j
  * @throws MatrixException if matrix is not square, 
  */
 public void combine(int i, int j) throws MatrixException
 {
     int n = getRows();
     if (n != getColumns())
     {
         throw new MatrixException( "Matrix must be square", true);
     }
     {
         int low = 0;
         if (i<low || j<low || i==j || i>=n || j>=n)
         {
             return;
         }
     }
     int min = Math.min(i,j);
     int max = Math.max(i,j);

     for (int r=0; r<n; r++)
     {
         if (r!=i && r!=j)
         {
             fData[min][r] = fData[min][r] + fData[max][r];         
             fData[r][min] = fData[r][min] + fData[r][max];         
         }
     }
     fData[min][min] = fData[min][min] + fData[min][max] + fData[max][min] + fData[max][max];
     remove(max); 
 }
 
}
