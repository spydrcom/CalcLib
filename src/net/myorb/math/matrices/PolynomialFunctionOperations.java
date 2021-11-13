
package net.myorb.math.matrices;

import net.myorb.math.OptimizedMathLibrary;
import net.myorb.math.Polynomial;
import net.myorb.math.characteristics.EigenvaluesAndEigenvectors;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

public class PolynomialFunctionOperations
{


	static DoubleFloatingFieldManager mgr = new DoubleFloatingFieldManager ();
	static OptimizedMathLibrary<Double> lib = new OptimizedMathLibrary<Double> (mgr);
	static PolynomialSpaceManager<Double> pfm = new PolynomialSpaceManager<Double> (mgr);


	static MatrixOperations<Polynomial.PowerFunction<Double>> matrices =
			new MatrixOperations<Polynomial.PowerFunction<Double>> (pfm);
	static EigenvaluesAndEigenvectors<Double> eigen = new EigenvaluesAndEigenvectors<Double> (mgr, lib);


	public static Vector<Polynomial.PowerFunction<Double>> makeRow (Double... c)
	{
		Vector<Polynomial.PowerFunction<Double>> v =
			new Vector<Polynomial.PowerFunction<Double>> (c.length, pfm);
		for (int i = 0; i < c.length; i++) { v.set (i+1, pfm.getPolynomialFunction (pfm.newCoefficients (c[i]))); }
		return v;
	}


	public static Matrix<Polynomial.PowerFunction<Double>>
		characteristicEquation (Matrix<Polynomial.PowerFunction<Double>> m)
	{
		Polynomial.PowerFunction<Double> x =
			pfm.getPolynomialFunction (pfm.newCoefficients (0.0, -1.0));
		Matrix<Polynomial.PowerFunction<Double>> I = matrices.identity (m.rowCount ());
		Matrix<Polynomial.PowerFunction<Double>> xI = matrices.times (x, I);
		return matrices.sum (m, xI);
	}

	
	public static Polynomial.PowerFunction<Double> characteristicPolynomial (Matrix<Polynomial.PowerFunction<Double>> m)
	{
		Matrix<Polynomial.PowerFunction<Double>> charEq = characteristicEquation (m);
		Polynomial.PowerFunction<Double> det = matrices.det (charEq);
		return det;
	}


}

