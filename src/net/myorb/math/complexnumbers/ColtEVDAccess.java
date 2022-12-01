
package net.myorb.math.complexnumbers;

import net.myorb.math.matrices.*;
import net.myorb.math.expressions.managers.ExpressionComplexFieldManager;

import cern.colt.library.Linalg;

/**
 * interface to Colt library for implementation of EVD operator
 * @author Michael Druckman
 */
public class ColtEVDAccess
{

	static ExpressionComplexFieldManager mgr = new ExpressionComplexFieldManager ();


	/**
	 * pass source to Colt and return results
	 * @param inout source of matrix to decomposes returning Eigen-vectors
	 * @return diagonal matrix of Eigen-values
	 */
	public static Matrix <ComplexValue<Double>> getEigenVals (Matrix <ComplexValue<Double>> inout)
	{
		int
			n = inout.rowCount (),
			m = inout.columnCount ();
		double [][] vecs = new double [n][m];

		/*
		 * copy source values into double array
		 */
		for (int r=1; r<=n; r++)
			for (int c=1; c<=m; c++)
			{
				ComplexValue<Double> cell = inout.get (r, c);
				if (cell.Im() != 0.0) throw new RuntimeException ("Source matrix must be real domain");
				vecs[r-1][c-1] = cell.Re ();
			}
		Linalg.prep (vecs);

		/*
		 * replace source cells with Eigen-vector values
		 */
		vecs = Linalg.getEvdV ();
		for (int r=1; r<=n; r++)
			for (int c=1; c<=m; c++)
			{
				inout.set (r, c, mgr.C (vecs[r-1][c-1], 0.0));
			}

		double []
			ivals = Linalg.getEvdDimag (),
			rvals = Linalg.getEvdDreal ();
		n = ivals.length;

		/*
		 * construct diagonal matrix holding Eigen-values
		 */
		Matrix <ComplexValue<Double>> result =
				new Matrix <ComplexValue<Double>> (n, m, mgr);
		for (int i=1; i<=n; i++) result.set (i, i, mgr.C (rvals[i-1], ivals[i-1]));

		return result;
	}


}

