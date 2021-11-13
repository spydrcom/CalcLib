
package net.myorb.testing;

import net.myorb.math.matrices.*;
import net.myorb.math.realnumbers.*;

public class MatrixTests
{

	static final FloatingFieldManager manager = new FloatingFieldManager ();
	static final SimultaneousEquations<Float> mops = new SimultaneousEquations<Float> (manager);
	static final VectorOperations<Float> vops = new VectorOperations<Float> (manager);

	public static void main (String... args)
	{
		System.out.println ("I (4) =========");
		mops.show (mops.identity (4));

		System.out.println ("M =========");
		Matrix<Float> m = new Matrix<Float> (2, 3, manager);
		mops.setRow (1, m, vops.V (2f, 3f, 4f));
		mops.setRow (2, m, vops.V (1f, 0f, 0f));
		mops.show (m);

		System.out.println ("N =========");
		Matrix<Float> n = new Matrix<Float> (3, 2, manager);
		mops.setRow (1, n, vops.V (0.0f, 1000.0f));
		mops.setRow (2, n, vops.V (1.0f, 100.0f));
		mops.setRow (3, n, vops.V (0.0f, 10.0f));
		mops.show (n);

		System.out.println ("M * N =========");
		mops.show (mops.product (m, n));

		System.out.println ("X =========");
		Matrix<Float> x = new Matrix<Float> (3, 3, manager);
		mops.setRow (1, x, vops.V (1f, 3f, -2f));
		mops.setRow (2, x, vops.V (3f, 5f, 6f));
		mops.setRow (3, x, vops.V (2f, 4f, 3f));
		mops.show (x);

		System.out.println ("Y =========");
		Matrix<Float> y = new Matrix<Float> (3, 1, manager);
		mops.setCol (1, y, vops.V (5f, 7f, 8f));
		mops.show (y);

		System.out.println ("inv (X) * Y =========");
		mops.show (mops.product (mops.inv (x), y));

		System.out.println ("A =========");
		Matrix<Float> a = new Matrix<Float> (4, 4, manager);
		mops.setRow (1, a, vops.V (1f, 2f, 3f, 4f));
		mops.setRow (2, a, vops.V (5f, 6f, 7f, 8f));
		mops.setRow (3, a, vops.V (9f, 10f, 11f, 12f));
		mops.setRow (4, a, vops.V (13f, 14f, 15f, 16f));
		mops.show (a);

		System.out.println ("det (A) =========");
		System.out.println ("DET = " + mops.det (a));

		System.out.println ("B =========");
		Matrix<Float> b = new Matrix<Float> (4, 4, manager);
		mops.setRow (1, b, vops.V (112f, 15f, 110f, 99f));
		mops.setRow (2, b, vops.V (12f, 105f, 11f, 9f));
		mops.setRow (3, b, vops.V (9f, 10f, 11f, 12f));
		mops.setRow (4, b, vops.V (13f, 14f, 15f, 16f));
		mops.show (b);

		System.out.println ("det (B) =========");
		System.out.println ("DET = " + mops.det (b));

		System.out.println ("comatrix (B) =========");
		mops.show (mops.comatrix (b));

		System.out.println ("adj (B) =========");
		mops.show (mops.adj (b));

		System.out.println ("inv (B) =========");
		mops.show (mops.inv (b));

		System.out.println ("B * inv (B) =========");
		mops.show (mops.product (b, mops.inv (b)));

		System.out.println ("=========");
		
		a = new Matrix<Float> (3, 3, manager);
		mops.setRow (1, a, vops.V (1f, 3f, -2f));
		mops.setRow (2, a, vops.V (3f, 5f, 6f));
		mops.setRow (3, a, vops.V (2f, 4f, 3f));
		mops.show (a);

		Vector<Float> v = vops.V (5f, 7f, 8f);
		vops.show (v);

		System.out.print ("DET solution=");
		vops.show (mops.solve (a, v));

		System.out.print ("INV solution=");
		vops.show (mops.inverseSolution (a, v));
		System.out.println ();

		System.out.println ("augmented");
		SimultaneousEquations.AugmentedMatrix<Float> aug = mops.buildAugmentedMatrix (a, v);
		mops.show (aug);

		//Gaussian elimination

		System.out.println ();
		System.out.println ("*** Gaussian elimination");
		System.out.println ();

		System.out.println ("scaleAndAddToRow 2");
		aug.scaleAndAddToRow (-3f, 1, 2);
		mops.show (aug);

		System.out.println ("scaleAndAddToRow 3");
		aug.scaleAndAddToRow (-2f, 1, 3);
		mops.show (aug);

		System.out.println ("scaleRow 2");
		aug.scaleRow (2, -0.25f);
		mops.show (aug);

		System.out.println ("scaleAndAddToRow 3");
		aug.scaleAndAddToRow (2f, 2, 3);
		mops.show (aug);

		System.out.println ("scaleAndAddToRow 2");
		aug.scaleAndAddToRow (3f, 3, 2);
		mops.show (aug);

		System.out.println ("scaleAndAddToRow 1");
		aug.scaleAndAddToRow (-3f, 2, 1);
		mops.show (aug);

		System.out.println ("scaleAndAddToRow 1");
		aug.scaleAndAddToRow (2f, 3, 1);
		mops.show (aug);

		System.out.print ("solution = ");
		vops.show (aug.getColAccess (4));
		System.out.println ();

		System.out.println ("===");
		
		SimultaneousEquations<Float> se = new SimultaneousEquations<Float> (manager);
		vops.show (se.applyGaussianElimination (a, v));
	}

}
