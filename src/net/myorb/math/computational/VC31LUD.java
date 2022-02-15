
package net.myorb.math.computational;

import net.myorb.math.expressions.DataIO;
import net.myorb.math.GeneratingFunctions;
import net.myorb.math.computational.Parameterization;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

import net.myorb.math.matrices.VectorOperations;
import net.myorb.math.matrices.MatrixOperations;
import net.myorb.math.matrices.Triangular;
import net.myorb.math.matrices.Matrix;
import net.myorb.math.matrices.Vector;

import net.myorb.math.Function;

import java.io.File;

/**
 * Lower-Upper Decomposition for VanChe-31 algorithm (Vandermonde-Chebychev)
 * @author Michael Druckman
 */
public class VC31LUD
{


	public VC31LUD
		(
			Parameterization configuration
		)
	{
		this.configuration = configuration;
		if (L == null) loadVC ();
	}
	protected Parameterization configuration;


	/**
	 * @param f function for regression
	 * @param lo the lo end of the spline range
	 * @param hi the hi end of the spline range
	 * @return Chebyshev-T coefficients for the spline
	 */
	public GeneratingFunctions.Coefficients<Double> spline
		(
			Function<Double> f,
			Double lo, Double hi
		)
	{
		double x = lo, delta = (hi - lo) / 31;

		Vector<Double> b = new Vector<Double>(31, mgr);

		for (int i=1; i<32; i++)
		{
			b.set (i, f.eval (x));
			x += delta;
		}

		return solve (b);
	}


	/**
	 * @param b the vector to solve for
	 * @return the solution vector computed
	 */
	public GeneratingFunctions.Coefficients<Double> solve (Vector<Double> b)
	{
		GeneratingFunctions.Coefficients<Double> c =
				new GeneratingFunctions.Coefficients<Double>();
		tri.luXb (L, U, b, P).addToList (c);
		return c;
	}


	/**
	 * load VanChe matrix data
	 */
	public static void loadVC ()
	{
		loadOps ();
		L = dio.read (new File ("data/VC31L.TDF"));
		P = dio.read (new File ("data/VC31P.TDF")).getCol (1);
		U = dio.read (new File ("data/VC31U.TDF"));
	}
	public static Matrix<Double> L = null, U = null;
	public static Vector<Double> P = null;


	/**
	 * construct matrix operations objects
	 */
	public static void loadOps ()
	{
		mgr = new ExpressionFloatingFieldManager ();
		ops = new MatrixOperations<Double>(mgr);
		vec = new VectorOperations<Double>(mgr);
		tri = ops.getTriangularOperations ();
		dio = new DataIO<Double>(mgr);
	}
	public static ExpressionFloatingFieldManager mgr;
	public static MatrixOperations<Double> ops;
	public static VectorOperations<Double> vec;
	public static Triangular<Double> tri;
	public static DataIO<Double> dio;


}

