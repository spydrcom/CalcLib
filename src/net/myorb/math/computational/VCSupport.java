
package net.myorb.math.computational;

import net.myorb.math.matrices.Vector;
import net.myorb.math.matrices.Triangular;

import net.myorb.math.matrices.VectorOperations;
import net.myorb.math.matrices.MatrixOperations;

import net.myorb.math.GeneratingFunctions;
import net.myorb.math.expressions.DataIO;

import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

/**
 * LUD support for VanChe based algorithms (Vandermonde-Chebychev)
 * @author Michael Druckman
 */
public class VCSupport
{


	/**
	 * the CHEBYSHEV POINTS as defined in approximation theory
	 */


	/**
	 * establish the list of Chebyshev points given the order
	 * @param N the order of the polynomial
	 * @return the list of points for N
	 */
	public double []  computePoints (int N)
	{
		double [] points = new double[N+1];

		// points = ARRAY [ 0 <= i <= N ]   (    f (  i * d  )    )

		double d = Math.PI / N;
		for (int i = 0; i <= N; i++)
		{
			points [i] = - Math.cos ( i * d );
		}

		return points;
	}


	/**
	 * package the solution vector as polynomial coefficients
	 * @param solution the solution vector
	 * @return the coefficients list
	 */
	public static GeneratingFunctions.Coefficients<Double>
			bundle (Vector <Double> solution)
	{
		GeneratingFunctions.Coefficients<Double> c =
				new GeneratingFunctions.Coefficients<Double>();
		solution.addToList (c);
		return c;
	}


	/**
	 * construct matrix operations objects
	 */
	public static void loadOps ()
	{
		if (mgr != null) return;
		mgr = new ExpressionFloatingFieldManager ();
		ops = new MatrixOperations <Double> (mgr);
		vec = new VectorOperations <Double> (mgr);
		tri = ops.getTriangularOperations ();
		dio = new DataIO <Double> (mgr);
	}
	public static ExpressionFloatingFieldManager mgr = null;
	public static MatrixOperations <Double> ops;
	public static VectorOperations <Double> vec;
	public static Triangular <Double> tri;
	public static DataIO <Double> dio;


}

