
package net.myorb.testing.linalg;

import net.myorb.math.linalg.LU;
import net.myorb.math.matrices.Matrix;
import net.myorb.math.matrices.MatrixOperations;
//import net.myorb.math.matrices.decomposition.LU;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

public class LUD
{
	/**
	 * unit test
	 * @param args not used
	 */
	public static void main (String... args)
	{
		MatrixOperations<Double> ops = 
				new MatrixOperations<Double> (manager);
		Matrix<Double> A = new Matrix<Double>(3, 3, manager);
//		Matrix<Double> L = new Matrix<Double>(3, 3, manager);
//		Matrix<Double> U = new Matrix<Double>(3, 3, manager);

		A.set (1, 1, 1d); A.set (1, 2, 2d); A.set (1, 3, 3d);
		A.set (2, 1, 4d); A.set (2, 2, 5d); A.set (2, 3, 6d);
		A.set (3, 1, 7d); A.set (3, 2, 8d); A.set (3, 3, 9d);

//		new LU<Double> (manager).decompose (A, U, L);
//		ops.show (U); ops.show (L);
//
//		Matrix<Double>
//			LU = ops.product (L, U);
//		ops.show (LU);

		LU.Decomposition <Double> lud = LU.decompose (A);
		ops.show (lud.getL ()); ops.show (lud.getU ());

		Matrix <Double>
			LU = ops.product (lud.getL (), lud.getU ());
		ops.show (LU);
	}
	static ExpressionFloatingFieldManager manager = new ExpressionFloatingFieldManager ();
}
