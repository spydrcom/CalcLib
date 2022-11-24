
package net.myorb.math.matrices;

import net.myorb.math.SpaceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * support for matrix and vector operations specific to cell sequences
 * @author Michael Druckman
 */
public class CellSequencePrimitives
{



	/*
	 * vector specific
	 */

	/**
	 * copy into matrix
	 * @param v source of content
	 * @param toRow destination row number for content
	 * @param of matrix holding destination row
	 */
	public static <T> void copyCells (Vector <T> v, int toRow, Matrix <T> of)
	{ VectorOperations.copyContent (of.getRowAccess (toRow), v); }


	/**
	 * populate vector
	 * @param v source of content
	 * @param to destination for content
	 */
	public static <T> void copyCells (Vector <T> v, Vector <T> to)
	{ VectorOperations.copyContent (to, v); }


	/**
	 * @param v the vector to enumerate
	 * @return list of text of values
	 */
	public static <T> List <String> toList (VectorAccess <T> v)
	{
		List <String> list = new ArrayList <String> ();
		for (int i = 1; i <= v.size (); i++) list.add (v.get (i).toString ());
		return list;
	}



	/*
	 * matrix specific
	 */


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



	/*
	 * vector cell modification by math operation
	 */

	/**
	 * @param v the vector
	 * @param element index of the element
	 * @param value the value to multiply
	 * @param mgr the data type manager
	 * @return the computed value
	 */
	public static <T> T multiplyInto (VectorAccess <T> v, int element, T value, SpaceManager <T> mgr)
	{ T cellValue;  v.set (element, cellValue = mgr.multiply (v.get (element), value)); return cellValue; }
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



	/*
	 * matrix cell modification by math operation
	 */

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


	/*
	 * simple value summation
	 */

	/**
	 * @param value starting value
	 * @param offset the difference to apply
	 * @param mgr the data type manager
	 * @return value-offset
	 */
	public static <T> T reduce (T value, T offset, SpaceManager <T> mgr)
	{
		return mgr.add (value, mgr.negate (offset));
	}


}

