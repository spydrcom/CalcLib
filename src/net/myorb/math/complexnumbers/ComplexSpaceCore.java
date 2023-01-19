
package net.myorb.math.complexnumbers;

import net.myorb.math.computational.Combinatorics;

import net.myorb.math.expressions.managers.ExpressionComplexFieldManager;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

import net.myorb.data.abstractions.FunctionWrapper;

/**
 * components required for working in complex space
 * @author Michael Druckman
 */
public class ComplexSpaceCore
{


	/**
	 * wrapper interface specific to complex functions
	 */
	public interface ComplexFunction
	extends FunctionWrapper.F <ComplexValue<Double>>
	{}


	/*
	 * managers for data types
	 */

	public static ExpressionFloatingFieldManager realMgr = new ExpressionFloatingFieldManager ();
	public static ExpressionComplexFieldManager manager = new ExpressionComplexFieldManager ();
	public static ComplexLibrary<Double> cplxLib;


	/*
	 * most efficient form of the complex library configurations
	 */

	static
	{
		cplxLib = new ComplexLibrary<Double> (realMgr, manager);
		cplxLib.setMathLib (new JreComplexSupportLibrary (realMgr));
		cplxLib.initializeGamma (15);
	}


	/*
	 * basic complex arithmetic
	 */

	public static ComplexValue <Double> RE (double value) { return manager.C (value, 0.0); }
	public static ComplexValue <Double> CV (double re, double im) { return manager.C (re, im); }
	public static ComplexValue <Double> IM (double value) { return manager.C (0.0, value); }

	public static ComplexValue <Double> S (int N) { return manager.newScalar (N); }
	public static boolean isZ (ComplexValue <Double> x) { return manager.isZero (x); }
	public static ComplexValue <Double> oneOver (ComplexValue <Double> x) { return manager.invert (x); }
	public static ComplexValue <Double> reduce (ComplexValue <Double> x, int by) { return reduce (x, S (by)); }
	public static ComplexValue <Double> negWhenOdd (ComplexValue <Double> x, int n) { return n % 2 == 1 ? x : NEG (x); }
	public static ComplexValue <Double> negWhenEven (ComplexValue <Double> x, int n) { return n % 2 == 0 ? x : NEG (x); }
	public static ComplexValue <Double> productOf (ComplexValue <Double> x, ComplexValue <Double> y) { return manager.multiply (x, y); }
	public static ComplexValue <Double> ratioOf (ComplexValue <Double> x, ComplexValue <Double> y) { return manager.multiply (x, oneOver (y)); }
	public static ComplexValue <Double> reduce (ComplexValue <Double> x, ComplexValue <Double> by) { return sumOf (x, NEG (by)); }
	public static ComplexValue <Double> sumOf (ComplexValue <Double> x, ComplexValue <Double> y) { return manager.add (x, y); }
	public static ComplexValue <Double> POW (ComplexValue <Double> x, int y) { return manager.pow (x, y); }
	public static ComplexValue <Double> NEG (ComplexValue <Double> x) { return manager.negate (x); }


	/*
	 * complex and combination library functions
	 */

	public static ComplexValue <Double>
		bernpoly (ComplexValue <Double> x, int n)
	{ return Combinatorics.BernoulliPolynomial (x, n, manager); }
	public static ComplexValue <Double> ln (ComplexValue <Double> z) { return cplxLib.ln (z); }
	public static double SN (int n, int k) { return Combinatorics.stirlingNumbers2HW (n, k); }
	public static double F (int n) { return Combinatorics.F (n); }


}

