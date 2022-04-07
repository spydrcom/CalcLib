
package net.myorb.testing.splines;

import net.myorb.math.computational.splines.CubicSpline;
import net.myorb.math.computational.splines.CubicSplinePolynomial;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

import net.myorb.math.specialfunctions.bessel.OrdinaryFirstKind;
import net.myorb.math.polynomial.PolynomialSpaceManager;

import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;

import java.util.*;

public class CubicTest
{

	static SpaceManager <Double> sm = new ExpressionFloatingFieldManager ();
	static PolynomialSpaceManager <Double> psm = new PolynomialSpaceManager <> (sm);

	public static void main (String[] a) throws Exception
	{
		Polynomial.PowerFunction <Double> j0 = OrdinaryFirstKind.getJ (0, 20, psm);

		for (double x = 0.0; x <= 10; x += 0.1)
		{
			System.out.println (x + " j0 = " + j0.eval (x));
		}

		double delta = 0.00001;
		List <Double> knotPoints = new ArrayList <> ();
		for (double x = 0.0; x <= 10; x += 1) { knotPoints.add (x); }
//		CubicSpline <Double> csp = new CubicSplinePolynomial <> (sm);
		CubicSpline <Double> csp = new CubicSpline <> (sm);

		double max = 0.0;
		CubicSplinePolynomial.Interpolation <Double> i = csp.interpolationFor (j0, knotPoints, delta);
		for (double x = 0.0; x <= 10; x += 0.1)
		{
			double ival = i.eval (x);
			double err = Math.abs (j0.eval (x) - ival);
			if (err > max) max = err;

			System.out.println (x + " i = " + ival + " err = " + err);
		}
		System.out.println ("max = " + max);
	}

}
