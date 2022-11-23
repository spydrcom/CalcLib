
package net.myorb.math.matrices.decomposition;

import net.myorb.math.matrices.CellSequencePrimitives;
import net.myorb.math.matrices.VectorAccess;
import net.myorb.math.matrices.Matrix;

import java.util.ArrayList;
import java.util.List;

import net.myorb.math.SpaceManager;

/**
 * support for matrix operations implementing linear algebra algorithms
 * - specific to data represented in CalcLib generic structures
 * @author Michael Druckman
 */
public class GenericSupport extends CellSequencePrimitives
{


	/**
	 * @param x the generic value
	 * @param mgr the data type manager
	 * @return the absolute value
	 */
	public static <T> T abs (T x, SpaceManager <T> mgr)
	{ return mgr.lessThan (x, mgr.getZero ()) ? mgr.negate (x) : x; }


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


	/**
	 * @param items an array of integer values
	 * @return list of text of values
	 */
	public static List <Integer> toList (int [] items)
	{
		List <Integer> list = new ArrayList <Integer> ();
		for (int item : items) list.add (item);
		return list;
	}


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


}

