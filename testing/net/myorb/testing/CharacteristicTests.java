
package net.myorb.testing;

import net.myorb.math.matrices.*;
import net.myorb.math.matrices.Vector;
import net.myorb.math.characteristics.*;
import net.myorb.math.computational.*;
import net.myorb.math.realnumbers.*;
import net.myorb.math.*;

import java.util.*;

public class CharacteristicTests<T> extends Arithmetic<T>
{


	static DoubleFloatingFieldManager mgr = new DoubleFloatingFieldManager ();

	static VectorOperations<Double> vops = new VectorOperations<Double> (mgr);
	static ExponentiationLib<Double> lib = new ExponentiationLib<Double> (mgr);
	static PolynomialRoots<Double> roots = new PolynomialRoots<Double> (mgr, lib);
	static MatrixOperations<Double> mops = new MatrixOperations<Double> (mgr);


	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 * @param lib an implementation of the power library
	 */
	public CharacteristicTests
		(SpaceManager<T> manager, PowerLibrary<T> lib)
	{ super (manager); this.plib = lib; proots = new PolynomialRoots<T> (manager, lib); }
	protected PolynomialRoots<T> proots;
	protected PowerLibrary<T> plib;


	void eigenvalueDump
		(
			Matrix<Double> m, Double lambda, EigenvaluesAndEigenvectors<Double> e
		)
	{
		Matrix<Double> a =
			e.eigenvalueEquation (m, lambda);
		mops.dump ("m - I*lambda " + lambda, a);
		System.out.println ("det = " + mops.det (a));
		
		Vector<Double> b;
		System.out.print ("Von Mises power iteration computed eigenvector = ");
		vops.show (b = e.findDominantEigenvectorFor (m, 50, 10));
		
		System.out.println ("A*b = ");
		mops.show (mops.product (a, mops.columnMatrix (b)));

		System.out.println ();
	}


	void eigenvaluesTest (Matrix<Double> m)
	{
		EigenvaluesAndEigenvectors<Double> e =
			new EigenvaluesAndEigenvectors<Double>  (mgr, lib);
		List<Double> ev = e.computeEigenvaluesFor (m);

		mops.dump ("m => ", m);
		System.out.println ("eigenvalues = " + ev);
		System.out.println ("det = " + mops.det (m));
		System.out.println ("===");
		System.out.println ();
		
		eigenvalueDump (m, ev.get (0), e);
		eigenvalueDump (m, ev.get (1), e);
		System.out.println ("***");
		System.out.println ();
	}


	static Double eigenvaluesTest3x3 (Polynomial.Coefficients<Double> coef, Double approxRoot)
	{
		Double
			root = roots.newtonRaphsonMethod (coef, approxRoot), fx = roots.evaluatePolynomial (coef, root);
		System.out.println ("root = " + root + "  f(root) = " + fx);
		return root;
	}

	void eigenSystemTest (Matrix<Double> m)
	{
		EigenvaluesAndEigenvectors<Double> e =
			new EigenvaluesAndEigenvectors<Double>  (mgr, lib);
		Vector<Double> eigenvector = new Vector<Double> (mgr);
		Double eigenvalue = e.findDominantEigensystemMemberFor (m, eigenvector, 500, 10);

		System.out.println ("Eigen Both");
		System.out.print ("eigenvector = "); vops.show (eigenvector);
		System.out.println ("eigenvalue = " + eigenvalue);

		Matrix<Double> mMinusLambdaI = e.eigenvalueEquation (m, eigenvalue);
		mops.show (mMinusLambdaI); System.out.println ("det = " + mops.det (mMinusLambdaI));

		System.out.println ("product check");
		mops.show (mops.product (mMinusLambdaI, mops.columnMatrix (eigenvector)));
		if (e.checkSolution (m, eigenvector, eigenvalue, 8))
		{
			System.out.println ("=== solution check shows validation");
		}
		else
		{
			System.out.println ("!!! solution check show failure");
		}
		System.out.println ("===");
	}

	void eigenvaluesTest3x3 (Matrix<Double> m)
	{
		EigenvaluesAndEigenvectors<Double> e =
			new EigenvaluesAndEigenvectors<Double>  (mgr, lib);
		cpc = e.computeCharacteristicPolynomialFor (m).getCoefficients();

		mops.dump ("m => ", m);
		System.out.println ("characteristic polynomial coefficients = " + cpc);

		List<Double> ev = new ArrayList<Double> ();
		ev.add (eigenvaluesTest3x3 (cpc, -10.0));
		ev.add (eigenvaluesTest3x3 (cpc, 0.1));
		ev.add (eigenvaluesTest3x3 (cpc, 10.0));
		System.out.println ("eigenvalues = " + ev);

		System.out.println ("det = " + mops.det (m));
		System.out.println ("===");
		System.out.println ();

		eigenvalueDump (m, ev.get (0), e);
		System.out.println ("***");
		eigenvalueDump (m, ev.get (1), e);
		System.out.println ("***");
		eigenvalueDump (m, ev.get (2), e);
		System.out.println ("***");
		System.out.println ();
	}
	static Polynomial.Coefficients<Double> cpc;


	void eigenvaluesTest ()
	{
		Matrix<Double> m = new Matrix<Double> (2, 2, mgr);

		mops.setRow (1, m, vops.V (4d, 1d));
		mops.setRow (2, m, vops.V (6d, 3d));

		eigenvaluesTest (m);
		eigenSystemTest (m);

		mops.setRow (1, m, vops.V (4d, 1d));
		mops.setRow (2, m, vops.V (6d, -5d));

		eigenvaluesTest (m);
		eigenSystemTest (m);

		mops.setRow (1, m, vops.V (2d, 1d));
		mops.setRow (2, m, vops.V (1d, 2d));

		eigenvaluesTest (m);
		eigenSystemTest (m);

		Matrix<Double> m3x3 = new Matrix<Double> (3, 3, mgr);

		mops.setRow (1, m3x3, vops.V (4d, -1d, 2d));
		mops.setRow (2, m3x3, vops.V (6d, 3d, 1d));
		mops.setRow (3, m3x3, vops.V (6d, 3d, -3d));
		eigenvaluesTest3x3 (m3x3);
		eigenSystemTest (m3x3);

		Matrix<Double> m4x4 = new Matrix<Double> (4, 4, mgr);
		System.out.println ("A =");

		mops.setRow (1, m4x4, vops.V ( 4d, 1d, -2d,  2d));
		mops.setRow (2, m4x4, vops.V ( 1d, 2d,  0d,  1d));
		mops.setRow (3, m4x4, vops.V (-2d, 0d,  3d, -2d));
		mops.setRow (4, m4x4, vops.V ( 2d, 1d, -2d, -1d));

		mops.show (m4x4);
		eigenSystemTest (m4x4);
	}


	/**
	 * execute tests
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		new CharacteristicTests<Double> (mgr, lib).eigenvaluesTest ();
	}


}

