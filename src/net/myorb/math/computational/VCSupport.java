
package net.myorb.math.computational;

import net.myorb.math.expressions.DataIO;
import net.myorb.math.matrices.Triangular;

import net.myorb.math.matrices.VectorOperations;
import net.myorb.math.matrices.MatrixOperations;

import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

/**
 * LUD support for VanChe based algorithms (Vandermonde-Chebychev)
 * @author Michael Druckman
 */
public class VCSupport
{

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
