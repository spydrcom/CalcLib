
package net.myorb.math.matrices.decomposition;

import net.myorb.math.matrices.*;

import net.myorb.math.SpaceManager;

/**
 * support for matrix operations implementing linear algebra algorithms
 * - specific to data represented in CalcLib generic structures
 * @author Michael Druckman
 */
public class GenericSupport
{


	/**
	 * @param source the source for content
	 * @return a copy of the source matrix
	 */
	public static <T> Matrix <T> copyOf (Matrix <T> source)
	{
		int N = source.getEdgeCount ();
		Matrix <T> M = new Matrix <T> (N, N, source.getSpaceManager ());
		for (int i = 1; i <= N; i++) copyCells (source.getRow (i), i, M);
		return M;
	}


	/**
	 * @param v the vector
	 * @param element index of the element
	 * @param value the value to multiply
	 * @param mgr the data type manager
	 * @return the computed value
	 */
	public static <T> T multiplyInto (VectorAccess <T> v, int element, T value, SpaceManager <T> mgr)
	{ T cellValue;  v.set (element, cellValue = mgr.multiply (v.get (element), value));return cellValue; }
	public static <T> T divideInto (VectorAccess <T> v, int element, T value, SpaceManager <T> mgr)
	{ return multiplyInto (v, element, mgr.invert (value), mgr); }


	/**
	 * @param v the vector
	 * @param element index of the element
	 * @param value the value to add
	 * @param mgr the data type manager
	 * @return the computed value
	 */
	public static <T> T addInto (VectorAccess <T> v, int element, T value, SpaceManager <T> mgr)
	{ T cellValue; v.set (element, cellValue = mgr.add (v.get (element), value)); return cellValue; }
	public static <T> T reduceBy (VectorAccess <T> v, int element, T value, SpaceManager <T> mgr)
	{ return addInto (v, element, mgr.negate (value), mgr); }


	/**
	 * @param A the matrix 
	 * @param row the row number
	 * @param col the column number
	 * @param value the value to multiply
	 * @param mgr the data type manager
	 * @return the computed value
	 */
	public static <T> T multiplyInto (Matrix <T> A, int row, int col, T value, SpaceManager <T> mgr)
	{ T cellValue; A.set (row, col, cellValue = mgr.multiply (A.get (row, col), value)); return cellValue; }
	public static <T> T divideInto (Matrix <T> A, int row, int col, T value, SpaceManager <T> mgr)
	{ return multiplyInto (A, row, col, mgr.invert (value), mgr); }


	/**
	 * @param A the matrix 
	 * @param row the row number
	 * @param col the column number
	 * @param value the value to add
	 * @param mgr the data type manager
	 * @return the computed value
	 */
	public static <T> T addInto (Matrix <T> A, int row, int col, T value, SpaceManager <T> mgr)
	{ T cellValue; A.set (row, col, cellValue = mgr.add (A.get (row, col), value)); return cellValue; }
	public static <T> T reduceBy (Matrix <T> A, int row, int col, T value, SpaceManager <T> mgr)
	{ return addInto (A, row, col, mgr.negate (value), mgr); }


	/**
	 * @param x the generic value
	 * @param mgr the data type manager
	 * @return the absolute value
	 */
	public static <T> T abs (T x, SpaceManager <T> mgr)
	{ return mgr.lessThan (x, mgr.getZero ()) ? mgr.negate (x) : x; }


	/**
	 * exchange content in two matrix rows
	 * @param A the matrix holding rows to exchange
	 * @param row1 the first row number for exchange
	 * @param row2 the second row number
	 */
	public static <T> void interchange (Matrix <T> A, int row1, int row2)
	{
		Vector <T> v1 = A.getRow (row1), v2 = A.getRow (row2);
		copyCells (v1, row2, A); copyCells (v2, row1, A);
	}


	/**
	 * @param v source of content
	 * @param toRow destination row number for content
	 * @param of matrix holding destination row
	 */
	public static <T> void copyCells (Vector <T> v, int toRow, Matrix <T> of)
	{ VectorOperations.copyContent (of.getRowAccess (toRow), v); }


	/**
	 * @param v  source of content
	 * @param to destination for content
	 */
	public static <T> void copyCells (Vector <T> v, Vector <T> to)
	{ VectorOperations.copyContent (to, v); }


	/**
	 * dot product of vector segment
	 * @param A first vector for product
	 * @param B second vector for product
	 * @param start starting index for segment
	 * @param end ending index for segment
	 * @param mgr the data type manager
	 * @return the computed product
	 */
	public static <T> T dot
		(
			VectorAccess <T> A, VectorAccess <T> B,
			int start, int end, SpaceManager <T> mgr
		)
	{
		T sum = mgr.getZero ();
		for (int k = start; k <= end; k++)
		{ sum = mgr.add (sum, mgr.multiply (A.get (k), B.get (k))); }
		return sum;
	}


	/**
	 * identify the biggest remaining row
	 * @param starting the starting row and column
	 * @param in matrix being searched
	 * @return biggest row found
	 */
	public static <T> int maxRow
		(
			int starting, Matrix <T> in
		)
	{
		int ending = in.getEdgeCount (), imax = starting;
		SpaceManager <T> mgr = in.getSpaceManager ();
		T maxA = mgr.getZero (), absA;
		
	    for (int k = starting; k <= ending; k++)
	    {
	    	absA = abs (in.get (k, starting), mgr);
	    	if (mgr.lessThan (maxA, absA))
	    	{ maxA = absA; imax = k; }
	    }
	
	    if (mgr.isZero (maxA))
	    { throw new RuntimeException ("Degenerate matrix"); }
	    return imax;
	}


	/**
	 * @param x source of value
	 * @param y source of sign
	 * @return product of two
	 */
	public static <T> T SIGN (T x, T y, SpaceManager <T> mgr)
	{
		T mag = abs (x, mgr);
		return mgr.lessThan (y, mgr.getZero ()) ?
			mgr.negate (mag) : mag;
	}


}

