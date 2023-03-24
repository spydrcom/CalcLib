
package net.myorb.testing.linalg;

import net.myorb.math.matrices.Matrix;
import net.myorb.math.matrices.MatrixOperations;

import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

/**
 * Simultaneous Equation solution to coefficients of Chebyshev DiffEQ
 * @author Michael Druckman
 */
public class Solution
{

	/*
	 * Simultaneous Equation solution to coefficients of Chebyshev DiffEQ
	 * 

		-------------------
		the expanded series
		-------------------

	   ( f_0 * n ^ 2 +             2 * c_2 + 
	   ( f_1 * n ^ 2 -      f_1 +  6 * c_3 ) * z + 
	   ( c_2 * n ^ 2 + 12 * c_4 -  4 * c_2 ) * z ^ 2 + 
	   ( c_3 * n ^ 2 + 20 * c_5 -  9 * c_3 ) * z ^ 3 + 
	   ( c_4 * n ^ 2 + 30 * c_6 - 16 * c_4 ) * z ^ 4 + 
	   ( c_5 * n ^ 2 + 42 * c_7 - 25 * c_5 ) * z ^ 5 + 
	   ( c_6 * n ^ 2 - 36 * c_6            ) * z ^ 6 + 
	   ( c_7 * n ^ 2 - 49 * c_7            ) * z ^ 7 ) 


		// Initial conditions of Chebyshev DiffEQ ( T polynomial solutions )

		//	T	n	  f_0=f(0)	  f_1=f'(0)
		//	===============================
		//	T0	0		 0			 0
		//	T1	1		 0			 1
		//	T2	2		-1			 0
		//	T3	3		 0			-3
		//	T4	4		 1			 0
		//	T5	5		 0			 5
		//	T6	6		-1			 0
		//	T7	7		 0			-7
		
		//	equations for coefficients
		
		//	 ( n^2 ) * f_0	+  2 * c_2 = 0
		//	 (n^2-1) * f_1	+  6 * c_3 = 0
		//	 (n^2-4) * c_2	+ 12 * c_4 = 0
		//	 (n^2-9) * c_3	+ 20 * c_5 = 0
		//	(n^2-16) * c_4	+ 30 * c_6 = 0
		//	(n^2-25) * c_5	+ 42 * c_7 = 0
		
		// define T5
		
		n = 5
		f_0 = 0
		f_1 = 5

		// algebraic solution
		// ------------------

		c_2 = -( n^2 ) * f_0 /  2
		c_3 =  (1-n^2) * f_1 /  6	
		c_4 =  (4-n^2) * c_2 / 12
		c_5 =  (9-n^2) * c_3 / 20
		c_6 = (16-n^2) * c_4 / 30
		c_7 = (25-n^2) * c_5 / 42

		// linear algebra solution
		// -----------------------
		// substitute constants

		25 *  0 +  2 * c2
		24 *  5 +  6 * c3
		21 * c2 + 12 * c4
		16 * c3 + 20 * c5
		 9 * c4 + 30 * c6
		 0 * c5 + 42 * c7

		// Matrix to solve

		c2	c3	c4	c5	c6	c7	  =
		----------------------------
		 2						   0
			 6					-120
		21		12				   0
			16		20			   0
				 9		30		   0
					 0		42	   0
					
	*/

	public static void main (String... args)
	{
		MatrixOperations<Double> ops = 
				new MatrixOperations<Double> (manager);
		Matrix<Double> A = new Matrix<Double>(6, 6, manager);
		Matrix<Double> Z = new Matrix<Double>(6, 1, manager);

		for (int r = 1; r <= 6; r++)
		{
			Z.set (r,  1, C[r-1]);

			for (int c = 1; c <= 6; c++)
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
	static double [][] M =
		new double [][]
		{
			{  2,  0,  0,  0,  0,  0 },
			{  0,  6,  0,  0,  0,  0 },
			{ 21,  0, 12,  0,  0,  0 },
			{  0, 16,  0, 20,  0,  0 },
			{  0,  0,  9,  0, 30,  0 },
			{  0,  0,  0,  0,  0, 42 }
		};
	static double [] C = {  0,  -120,  0,  0,  0,  0};
}
