
package net.myorb.testing.linalg;

import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.matrices.Matrix;
import net.myorb.math.matrices.MatrixOperations;

public class SolutionAlt
{

	public static void main (String... args)
	{
		int N = C.length;
		MatrixOperations<Double> ops = 
				new MatrixOperations<Double> (manager);
		Matrix<Double> A = new Matrix<Double>(N, N, manager);
		Matrix<Double> Z = new Matrix<Double>(N, 1, manager);

		for (int r = 1; r <= N; r++)
		{
			Z.set (r,  1, C[r-1]);

			for (int c = 1; c <= N; c++)
			{
				A.set (r, c, M[r-1][c-1]);
			}
		}
		ops.show (A);
		System.out.println ();

		ops.show (Z);
		System.out.println ();

		Matrix<Double> AI = ops.inv (A); ops.show (AI);
		System.out.println ();

		Matrix<Double> S = ops.product (AI, Z);
		ops.show (S);
	}
	static ExpressionFloatingFieldManager manager =
		new ExpressionFloatingFieldManager ();
//	static double [][] M =
//		new double [][]
//		{
////			  C0  C1  C3  C3  C4  C5  C6  C7
//			{  1,  0,  0,  0,  0,  0,  0,  0 },
//			{  0,  1,  0,  0,  0,  0,  0,  0 },
//			{  0,  0,  2,  0,  0,  0,  0,  0 },
//			{  0,  0,  0,  6,  0,  0,  0,  0 },
//			{  0,  0, 21,  0, 12,  0,  0,  0 },
//			{  0,  0,  0, 16,  0, 20,  0,  0 },
//			{  0,  0,  0,  0,  9,  0, 30,  0 },
//			{  0,  0,  0,  0,  0,  0,  0, 42 }
//		};
//	static double [] C =
//			{  0,  5, 0, -120, 0,  0,  0,  0 };

	static double [][] M =
			new double [][]
			{
//				  C0  C1  C3  C3  C4  C5  C6  C7
				{  1,  0,  0,  0,  0,  0 },
				{  0,  1,  0,  0,  0,  0 },
				{  0,  0,  2,  0,  0,  0 },
				{  0,  0,  0,  6,  0,  0 },
				{  0,  0, 21,  0, 12,  0 },
				{  0,  0,  0, 16,  0, 20 }
			};
		static double [] C =
				{  0,  5, 0, -120, 0,  0 };

		/*
	 * 
	   ( f_0 * n ^ 2 +             2 * c_2 + 
	   ( f_1 * n ^ 2 -      f_1 +  6 * c_3 ) * z + 
	   ( c_2 * n ^ 2 + 12 * c_4 -  4 * c_2 ) * z ^ 2 + 
	   ( c_3 * n ^ 2 + 20 * c_5 -  9 * c_3 ) * z ^ 3 + 
	   ( c_4 * n ^ 2 + 30 * c_6 - 16 * c_4 ) * z ^ 4 + 
	   ( c_5 * n ^ 2 + 42 * c_7 - 25 * c_5 ) * z ^ 5 + 
	   ( c_6 * n ^ 2 - 36 * c_6            ) * z ^ 6 + 
	   ( c_7 * n ^ 2 - 49 * c_7            ) * z ^ 7 ) 

		c2	c3	c4	c5	c6	c7	  =
		----------------------------
		 2						   0
			 6					-120
		21		12				   0
			16		20			   0
				 9		30		   0
					 0		42	   0
	 * */
}
