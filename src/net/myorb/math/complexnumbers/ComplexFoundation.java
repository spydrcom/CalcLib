
package net.myorb.math.complexnumbers;

import net.myorb.math.specialfunctions.Bernoulli;

import net.myorb.math.expressions.JavaPowerLibrary;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.expressions.managers.ExpressionComplexFieldManager;

import net.myorb.math.realnumbers.DoubleFloatingFieldManager;
import net.myorb.math.computational.Combinatorics;

import net.myorb.math.GeneratingFunctions;
import net.myorb.math.Polynomial;

import net.myorb.utilities.Clib;

import java.util.ArrayList;

/**
 * function library supporting complex function calculations
 * @author Michael Druckman
 */
public class ComplexFoundation
{


	public static double eps = 1E-5, pi = Math.PI;


	/*
	 * libraries for real values
	 */

	protected static ExpressionFloatingFieldManager
			realMgr = new ExpressionFloatingFieldManager ();
	protected static JavaPowerLibrary<Double> realLib = new JavaPowerLibrary<Double> (realMgr);

	/*
	 * real value library for computation of combinatoric algorithms
	 */
	protected static Combinatorics<Double> combo = new Combinatorics<Double> (realMgr, realLib);

	/**
	 * binomial coefficient implementation
	 * @param n number of items in set being evaluated
	 * @param k taken this number of times
	 * @return computed n choose k
	 */
	protected static double choose (int n, int k) { return Combinatorics.binomialCoefficient (n, k); }


	/*
	 * libraries for complex values
	 */
	protected static ExpressionComplexFieldManager cplxMgr = new ExpressionComplexFieldManager ();
	protected static ComplexLibrary<Double> cplxLib = new ComplexLibrary<Double> (realMgr, cplxMgr);


	/**
	 * improved speed and accuracy
	 */
	public void useJRElib ()
	{
		cplxLib.setMathLib (new JreComplexSupportLibrary (realMgr));
	}


	/**
	 * a subtype of generic ComplexValue using Double as component type
	 */
	public static class Complex extends ComplexValue<Double>
	{

		public Complex () { this (realMgr); }
		public Complex (ComplexValue<Double> z) { this (z.Re (), z.Im ()); }
		public Complex (DoubleFloatingFieldManager manager) { super (manager); }
		public Complex (double r) { this (r, 0.0); }

		@SuppressWarnings("unchecked")
		public Complex (ComplexMarker z) { this ((ComplexValue<Double>) z); }

		public Complex (double r, double i)
		{
			this ();
			this.realpart = r;
			this.imagpart = i;
		}

		public boolean isinf () { return realpart==Double.POSITIVE_INFINITY && imagpart==0; }
		public boolean isninf () { return realpart==Double.NEGATIVE_INFINITY && imagpart==0; }
		public boolean isnan () { return Double.isNaN (realpart) && imagpart==0; }
		
		public static Complex
		NINF = new Complex (Double.NEGATIVE_INFINITY),
		INF = new Complex (Double.POSITIVE_INFINITY);
	}
	

	/*
	 * support for polynomials that accept complex parameters
	 */

	protected static Polynomial<ComplexValue<Double>> cplxPoly = new Polynomial<ComplexValue<Double>> (cplxMgr);

	/*
	 * Bernoulli polynomial generator implementation
	 */
	protected static GeneratingFunctions.Coefficients<ComplexValue<Double>> bernPolyCo (int n)
	{
		GeneratingFunctions.Coefficients<ComplexValue<Double>> co =
				new GeneratingFunctions.Coefficients<ComplexValue<Double>>();
		for (int k=0; k<=n; k++) co.add (C (choose (n, k) * Bernoulli.number (n - k)));
		return co;
	}
	protected static Complex evalBernPoly (int n, Complex z) { return CV (bernPoly (n).eval (z)); }
	protected static Polynomial.PowerFunction<ComplexValue<Double>> bernPoly (int n)
	{ return cplxPoly.getPolynomialFunction (bernPolyCo (n)); }

	protected static double evalBernPoly (int n, double z) { throw new RuntimeException ("NI"); }


	/**
	 * maximum of 2 values
	 * @param x first value
	 * @param y second value
	 * @return larger of two
	 */
	public static double max (double x, double y) 
	{
		return x>y? x: y;
	}
	public static double min (double x, double y) 
	{
		return x<y? x: y;
	}

	/**
	 * max for integer array
	 * @param values an array of integers
	 * @return the largest of the values
	 */
	public static int max (int[] values)
	{
		if (values.length == 0) return 0;
		int result = values[0];
		for (int v : values)
		{
			result = v > result ? v : result;
		}
		return result;
	}

	/**
	 * initialized complex array with zero values
	 * @param count the number of elements for the array
	 * @return array of complex zeros
	 */
	public static Complex[] zeros (int count)
	{
		if (count == 0)
			return new Complex[]{};
		Complex[] array = new Complex[count];
		for (int i=0; i<count; i++) array[i] = C (0);
		return array;
	}

    /**
     * dot product between complex array and real value array
     * @param z the array of complex values
     * @param r the array of real values
     * @return the dot product
     */
    public static Complex fdot (ArrayList<Complex> z, ArrayList<Double> r)
    {
    	Complex dot = C (0);
    	for (int k=0; k<z.size(); k++)
    	{ dot = CV (dot.plus (z.get (k).times (r.get (k)))); }
    	return dot;
    }

    /**
     * alternating sign integer value
     * @param n the integer exponent determining sign (even=1, odd=-1)
     * @return the sign of the calculation
     */
    public static double sign (int n) { return Math.pow (-1, n); }



	/*
	 * magnitude of value (distance from origin)
	 */

	/**
	 * real version
	 * @param x parameter value
	 * @return absolute value of parameter
	 */
	public static double abs (double x) 
	{
		return x<0? -x: x;
	}

	/**
	 * complex version
	 * @param x parameter value
	 * @return absolute value of parameter
	 */
	public static double abs (ComplexValue<Double> x) 
	{
		return Math.sqrt (x.times (x.conjugate ()).Re ());
	}


	/**
	 * factorial computation
	 * @param x parameter value
	 * @return computed factorial of parameter
	 */
	public static double fac (double x) 
	{
		return realLib.factorial (x);
	}


	/*
	 * logarithms
	 */

	/**
	 * real version
	 * @param x parameter value
	 * @return computed natural LOG of parameter
	 */
	public static double log (double x) 
	{
		return Math.log (x);
	}
	
	/**
	 * complex version
	 * @param x parameter value
	 * @return computed natural LOG of parameter
	 */
	public static ComplexValue<Double> log (ComplexValue<Double> x) 
	{
		return cplxLib.ln (x);
	}
	
	/**
	 * complex version
	 * @param x parameter value
	 * @return computed natural LOG of parameter
	 */
	public static ComplexValue<Double> ln (ComplexValue<Double> x) 
	{
		return cplxLib.ln (x);
	}

	/**
	 * real version
	 * @param x parameter value
	 * @return computed natural EXP of parameter
	 */
	public static double exp (double x) 
	{
		return Math.exp (x);
	}

	/**
	 * complex version
	 * @param x parameter value
	 * @return computed natural EXP of parameter
	 */
	public static ComplexValue<Double> exp (ComplexValue<Double> x) 
	{
		return cplxLib.exp (x);
	}

	/**
	 * real version
	 * @param x parameter value
	 * @param base base of logarithm
	 * @return computed LOG (base) of parameter
	 */
	public static double log (double x, int base) 
	{
		return Math.log (x) / Math.log (base);
	}

	/**
	 * complex version
	 * @param x parameter value
	 * @param base base of logarithm
	 * @return computed LOG (base) of parameter
	 */
	public static ComplexValue<Double> log (ComplexValue<Double> x, int base) 
	{
		return cplxLib.ln (x).times (C (base).inverted ());
	}


	/*
	 * trigonometry
	 */

	/**
	 * real version
	 * @param x parameter value
	 * @return computed ATAN of parameter
	 */
	public static double atan (double x) 
	{
		return Math.atan (x);
	}

	/**
	 * complex version
	 * @param x parameter value
	 * @return computed ATAN of parameter
	 */
	public static ComplexValue<Double> atan (ComplexValue<Double> x) 
	{
		return cplxLib.atan (x);
	}

	/**
	 * real version
	 * @param x parameter value
	 * @return computed SIN of parameter
	 */
	public static double sin (double x) 
	{
		return Math.sin (x);
	}

	/**
	 * complex version
	 * @param x parameter value
	 * @return computed SIN of parameter
	 */
	public static ComplexValue<Double> sin (ComplexValue<Double> x) 
	{
		return cplxLib.sin (x);
	}


	/*
	 * exponentiation
	 */

	/**
	 * real version
	 * @param x base of computation
	 * @param y value of exponent
	 * @return calculated value
	 */
	public static double toThe (double x, double y) 
	{
		return Math.exp (Math.log (x) * y);
	}

	/**
	 * complex version
	 * @param x base of computation
	 * @param y value of exponent
	 * @return calculated value
	 */
	public static ComplexValue<Double> toThe (ComplexValue<Double> x, ComplexValue<Double> y) 
	{
		return cplxLib.power (x, y);
	}

	/**
	 * complex version
	 * @param x parameter value
	 * @return computed SQRT of parameter
	 */
	public static ComplexValue<Double> sqrt (ComplexValue<Double> x) 
	{
		return cplxLib.sqrt (x);
	}

	/**
	 * real version
	 * @param x parameter value
	 * @return computed SQRT of parameter
	 */
	public static double sqrt (double x) 
	{
		return Math.sqrt (x);
	}


	/*
	 * determine type of value
	 */

	/**
	 * @param z a complex value
	 * @return TRUE if value has no imaginary part and real part is integer
	 */
	public static boolean isint (Complex z)
	{
		return z.Im () == 0 && Clib.isint (z.Re ());
	}

	/**
	 * @param z a complex value
	 * @param r a real value to compart to real part of comlex value
	 * @return TRUE if value has no imaginary part and real part matches r
	 */
	public static boolean isReal (Complex z, double r)
	{
		return z.Im () == 0 && z.Re () == r;
	}

	/**
	 * @param z a complex value
	 * @return the real part as integer
	 */
	public static int toInt (Complex z)
	{
		return new Double (z.Re ()).intValue ();
	}


	/*
	 * conversion between generic and Double specific Complex
	 */

	/**
	 * @param r real value to treat as complex
	 * @return complex value
	 */
	public static Complex C (double r)
	{
		return new Complex (r);
	}

	/**
	 * @param r real part
	 * @param i imaginary part
	 * @return complex value
	 */
	public static Complex C (double r, double i)
	{
		return new Complex (r, i);
	}

	/**
	 * @param z generic form of value
	 * @return complex value using Double components
	 */
	public static Complex CV (ComplexValue<Double> z)
	{
		return new Complex (z);
	}
	public static Complex CV (ComplexWrapper z)
	{
		return new Complex (z.getWrapped ());
	}


	/**
	 * representation for imaginary constant i
	 */
	protected static Complex i = C (0, 1);


}

