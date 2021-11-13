
package net.myorb.testing;

import net.myorb.math.*;

import net.myorb.math.matrices.Vector;
import net.myorb.math.matrices.Matrix;

import net.myorb.math.matrices.MatrixOperations;
import net.myorb.math.matrices.VectorOperations;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.computational.PolynomialRoots;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;
import net.myorb.math.characteristics.*;

import java.util.*;

public class PolyTest<T> extends Polynomial<T>
{


	static DoubleFloatingFieldManager mgr = new DoubleFloatingFieldManager ();

	static PolynomialSpaceManager<Double> pfm = new PolynomialSpaceManager<Double> (mgr);
	static Matrix<Polynomial.PowerFunction<Double>> m3x3 = new Matrix<Polynomial.PowerFunction<Double>> (3, 3, pfm);

	//static VectorOperations<Polynomial.PowerFunction<Double>> vops = new VectorOperations<Polynomial.PowerFunction<Double>> (pfm);
	//static ExponentiationLib<Polynomial.PowerFunction<Double>> lib = new ExponentiationLib<Polynomial.PowerFunction<Double>> (pfm);
	//static PolynomialRoots<Polynomial.PowerFunction<Double>> roots = new PolynomialRoots<Polynomial.PowerFunction<Double>> (pfm, lib);
	//static MatrixOperations<Polynomial.PowerFunction<Double>> mops = new MatrixOperations<Polynomial.PowerFunction<Double>> (pfm);


	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 * @param lib an implementation of the power library
	 */
	public PolyTest
		(SpaceManager<T> manager, PowerLibrary<T> lib)
	{ super (manager); this.plib = lib; proots = new PolynomialRoots<T> (manager, lib); }
	protected PolynomialRoots<T> proots;
	protected PowerLibrary<T> plib;


	static Vector<Polynomial.PowerFunction<Double>> makeRow (Double... c)
	{
		Vector<Polynomial.PowerFunction<Double>> v =
			new Vector<Polynomial.PowerFunction<Double>> (c.length, pfm);
		for (int i = 0; i < c.length; i++) { v.set (i+1, pfm.getPolynomialFunction (pfm.newCoefficients (c[i]))); }
		return v;
	}


	/**
	 * execute tests
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		detTest ();
		eigenTest ();
	}


	public static void eigenTest ()
	{
		System.out.println ("eigenTest");
		
		Matrix<Double> m3x3 = new Matrix<Double> (3, 3, mgr);
		ExponentiationLib<Double> lib = new ExponentiationLib<Double> (mgr);
		EigenvaluesAndEigenvectors<Double> ev = new EigenvaluesAndEigenvectors<Double> (mgr, lib);
		PolynomialRoots<Double> roots = new PolynomialRoots<Double> (mgr, lib);
		VectorOperations<Double> vops = new VectorOperations<Double> (mgr);
		MatrixOperations<Double> mops = new MatrixOperations<Double> (mgr);

		mops.setRow (1, m3x3, vops.V (4d, -1d, 2d));
		mops.setRow (2, m3x3, vops.V (6d, 3d, 1d));
		mops.setRow (3, m3x3, vops.V (6d, 3d, -3d));

		Polynomial.PowerFunction<Double> p = ev.computeCharacteristicPolynomialFor (m3x3);
		System.out.println (pfm.toString (p));

		List<Double> r = roots.evaluateEquation (p);
		System.out.println (r);

		Double lambda = r.get (0);
		System.out.println (p.eval (lambda));
		System.out.println ("===");
		System.out.println ();

		Matrix<Double> mmIl = ev.eigenvalueEquation (m3x3, lambda);
		mops.show (mmIl);

		System.out.println ();
		System.out.println ("det = " + mops.det (mmIl));
		System.out.println ();
		System.out.println ();

		mmIl = ev.eigenvalueEquation (m3x3, r.get (1));
		mops.show (mmIl);

		System.out.println ();
		System.out.println ("det = " + mops.det (mmIl));
		System.out.println ();
		System.out.println ("===");
		System.out.println ();
		System.out.println ("eigenvalues ");
		System.out.println (ev.computeEigenvaluesFor (m3x3));
		System.out.println ("===");
		System.out.println ();
		
		Vector<Double> eigenvector = new Vector<Double> (mgr);
		Double eigenvalue = ev.findDominantEigensystemMemberFor (m3x3, eigenvector, 20, 5);
		System.out.println ("dominant eigensystem"); vops.show (eigenvector); System.out.println (eigenvalue);
		boolean check = ev.checkSolution (m3x3, eigenvector, eigenvalue, 2);
		System.out.println (check? "Verified !!!": "Check failed !!!");
		System.out.println ("===");
		System.out.println ();
	}


	public static void detTest ()
	{
		System.out.println ("detTest");

		MatrixOperations<Polynomial.PowerFunction<Double>> mops =
			new MatrixOperations<Polynomial.PowerFunction<Double>> (pfm);

		mops.setRow (1, m3x3, makeRow (4d, -1d, 2d));
		mops.setRow (2, m3x3, makeRow (6d, 3d, 1d));
		mops.setRow (3, m3x3, makeRow (6d, 3d, -3d));

		Polynomial.PowerFunction<Double> x =
			pfm.getPolynomialFunction (pfm.newCoefficients (0.0, -1.0));
		Matrix<Polynomial.PowerFunction<Double>> I = mops.identity (3);
		Matrix<Polynomial.PowerFunction<Double>> xI = mops.times (x, I);

		Matrix<Polynomial.PowerFunction<Double>> sum = mops.sum (m3x3, xI);
		
		Polynomial.PowerFunction<Double> det = mops.det (sum);
		System.out.println (pfm.toString (det));

		ExponentiationLib<Double> lib = new ExponentiationLib<Double> (mgr);
		PolynomialRoots<Double> roots = new PolynomialRoots<Double> (mgr, lib);

		List<Double> r = roots.evaluateEquation (det);
		System.out.println (r);

		System.out.println (det.eval (r.get (0)));
		System.out.println ("===");
		System.out.println ();
	}


}

