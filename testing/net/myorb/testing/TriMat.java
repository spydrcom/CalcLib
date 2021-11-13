
package net.myorb.testing;

import net.myorb.math.matrices.*;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

public class TriMat
{


	static DoubleFloatingFieldManager
			mgr = new DoubleFloatingFieldManager ();
	static MatrixOperations<Double> mops = new MatrixOperations<Double>(mgr);
	static VectorOperations<Double> vops = new VectorOperations<Double>(mgr);


	public static void main(String[] args)
	{
		Vector<Double> x; Matrix<Double> l, u;
		Matrix<Double> m = new Matrix<Double>(4, 4, mgr);
		Triangular<Double> t = new Triangular<Double> (mgr);
		randomMatrix (m); mops.dump ("RND", m);

		split ();
		Vector<Double> b = new Vector<Double>(4, mgr);
		for (int i = 1; i < 5; i++) b.set (i, rnd ());
		show ("b = ", b); System.out.println ();
		split (); System.out.println ();

		section (); // lower triangular matrix
		mops.dump ("TRIL", l = mops.tril (m, 0));
		split (); show ("x = ", x = t.lXb (l, b));
		check (l, x, b);

		section (); // upper triangular matrix
		mops.dump ("TRIU", u = mops.triu (m, 0));
		split (); show ("x = ", x = t.uXb (u, b));
		check (u, x, b);

		section ();
		System.out.println ();
		System.out.println ("== LUx = b ==");
		System.out.println ();

		show ("compound x = ",
			x = t.luCompounded (l, u, b));
		check (l, mops.product (u, x), b);

		split (); show ("x = ", x = t.luXb (l, u, b));
		check (l, mops.product (u, x), b);
	}


	public static void randomMatrix (Matrix<Double> m)
	{
		int s = m.rowCount ();
		for (int r = 1; r <= s; r++)
		{
			for (int c = 1; c <= s; c++)
			{ m.set (r, c, rnd ()); }
		}
	}


	public static void section ()
	{
		System.out.println ("= = = = = =");
	}


	public static void split ()
	{
		System.out.println ("===");
		System.out.println ();
	}


	public static void check
	(MatrixAccess<Double> m, VectorAccess<Double> v, VectorAccess<Double> b)
	{
		System.out.print ("check:  ");
		show ("Ax = ", v = mops.product (m, v));
		mse (v, b);
	}


	public static void mse
	(VectorAccess<Double> v, VectorAccess<Double> b)
	{
		System.out.println
			("MSE: " + vops.mse (v, b));
		System.out.println ();
	}


	public static void show (String caption, VectorAccess<Double> v)
	{
		System.out.print (caption);
		vops.show (v);
	}


	public static double rnd ()
	{
		return Math.random()*20 - 10;
	}


}

