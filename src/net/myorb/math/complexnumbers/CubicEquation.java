
package net.myorb.math.complexnumbers;

import net.myorb.math.*;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

import java.util.ArrayList;
import java.util.List;

/**
 * implementation of the cubic equation solution
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class CubicEquation<T> extends Arithmetic<ComplexValue<T>>
	implements FunctionAnalyzer<ComplexValue<T>>
{


	/**
	 * managers required for the complex type and the component type
	 * @param manager a complex field manager object reference
	 */
	@SuppressWarnings("unchecked")
	public CubicEquation (SpaceManager<ComplexValue<T>> manager)
	{ this (manager, new ComplexLibrary<T> (manager.getComponentManager (), null)); }
	@SuppressWarnings("unchecked") public CubicEquation (SpaceManager<ComplexValue<T>> manager, ComplexLibrary<T> lib)
	{
		super (manager);
		this.scalar = manager.getComponentManager ();
		this.generateConstants (lib);
	}
	SpaceManager<T> scalar;


	/**
	 * constant objects built for value with multiple references
	 * @param lib a library capable of working with imaginary numbers
	 */
	public void generateConstants (ComplexLibrary<T> lib)
	{
		this.setLibrary (this.clib = lib);
		ONE = real (1); TWO = real (2); THREE = real (3);
		FOUR = real (4); NINE = real (9); T7 = real (27);
	}
	ComplexValue<T> ONE, TWO, THREE, FOUR, NINE, T7;
	ComplexLibrary<T> clib;


	/**
	 * compute the discriminant.
	 *  D = 18abcd - 4b^3d + b^2c^2 - 4ac^3 - 27a^2d^2
	 * @param a the coefficient of x^3
	 * @param b the coefficient of x^2
	 * @param c the coefficient of x
	 * @param d the Y-intercept value
	 * @return the discriminant
	 */
	@SuppressWarnings("unchecked")
	public ComplexValue<T> discriminant
		(
				ComplexValue<T> a, ComplexValue<T> b,
				ComplexValue<T> c, ComplexValue<T> d
		)
	{
		ComplexValue<T> AD = a.times (d), BC = b.times (c);

		return sigma
			(
				BC.squared (),
				real (18).times (AD).times (BC),
				neg (FOUR.times (d).times (b.toThe (3))),
				neg (FOUR.times (a).times (c.toThe (3))),
				neg (T7.times (AD.squared ()))
			);
	}


	/**
	 * get the list of unity solutions
	 * @return a list of 3 values, 2 complex, 1 real
	 */
	public List<ComplexValue<T>> getU ()
	{
		List<ComplexValue<T>> u = new ArrayList<ComplexValue<T>> ();
		ComplexValue<T> rad32 = lib.sqrt (neg (THREE)).divideBy (TWO);
		ComplexValue<T> cRoot = neg (ONE).divideBy (TWO).plus (rad32);

		u.add (ONE);
		u.add (cRoot.conjugate ());
		u.add (cRoot);

		return u;
	}


	/**
	 * compute the delta values
	 * @param a the coefficient of x^3
	 * @param b the coefficient of x^2
	 * @param c the coefficient of x
	 * @param d the Y-intercept
	 * @return list of deltas
	 */
	public List<ComplexValue<T>> getDelta
		(
				ComplexValue<T> a, ComplexValue<T> b,
				ComplexValue<T> c, ComplexValue<T> d
		)
	{
		ComplexValue<T> AC = a.times (c);
		ComplexValue<T> d0, d1, D = discriminant (a, b, c, d);
		List<ComplexValue<T>> deltas = new ArrayList<ComplexValue<T>> ();
		deltas.add (d0 = b.squared ().minus (THREE.times (AC)));						// d#0 = b^2 - 3ac
		deltas.add																	// d#1 = 2b^3 - 9abc + 27a^2d
		(
			d1 = TWO.times (b.toThe (3)).minus (NINE.times (AC).times (b))
						.plus (T7.times (a.squared ()).times (d))
		);
		deltas.add
		(d1.squared ().minus (FOUR.times (d0.toThe (3))));							//  d#2 = d#1^2 - 4d#0^3
		deltas.add (neg (T7).times (a.squared ()).times (D));				// d#3 is a sanity check, should be same as d#2

		deltas.add (D);																// save a copy of D as d#4
		return deltas;
	}


	/**
	 * get the value of C.
	 *  C = root3 ((d#1 + sqrt (d#1^2 - 4d#0^3)) / 2)
	 * @param a the coefficient of x^3
	 * @param b the coefficient of x^2
	 * @param c the coefficient of x
	 * @param d the Y-intercept
	 * @return value of C
	 */
	public ComplexValue<T> getC
		(
			ComplexValue<T> a, ComplexValue<T> b,
			ComplexValue<T> c, ComplexValue<T> d
		)
	{
		return getC (getDelta (a, b, c, d));
	}


	/**
	 * compute C from delta values.
	 *  C = root3 ((d#1 + sqrt (d#1^2 - 4d#0^3)) / 2)
	 * @param delta the list of delta values
	 * @return value of C
	 */
	public ComplexValue<T> getC (List<ComplexValue<T>> delta)
	{
		ComplexValue<T> d2, cCube;
		if ((d2 = delta.get (2)).isZero ()) cCube = delta.get (1);
		else cCube = delta.get (1).plus (clib.sqrt (d2)).divideBy (TWO);
		return clib.root (cCube, 3);
	}


	/**
	 * compute the roots.
	 *  x#n = - (b + U#n * C + d#0 / (U#n * C)) / 3a
	 * @param a the coefficient of x^3
	 * @param b the coefficient of x^2
	 * @param c the coefficient of x
	 * @param d the Y-intercept
	 * @return computed roots
	 */
	public List<ComplexValue<T>> getRoots
		(
			ComplexValue<T> a, ComplexValue<T> b,
			ComplexValue<T> c, ComplexValue<T> d
		)
	{
		List<ComplexValue<T>> deltaValues = getDelta (a, b, c, d);
		List<ComplexValue<T>> X = specialCaseCheck (a, b, c, d, deltaValues);
		if (X == null) X = getRoots (a, b, deltaValues);
		return X;
	}


	/**
	 * no special case so compute roots from a,b,delta
	 * @param a the (possible complex) coefficient of x^3
	 * @param b the (possible complex) coefficient of x^2
	 * @param deltaValues list of delta values
	 * @return list of roots
	 */
	@SuppressWarnings("unchecked")
	public List<ComplexValue<T>> getRoots
		(
			ComplexValue<T> a, ComplexValue<T> b,
			List<ComplexValue<T>> deltaValues
		)
	{
		ComplexValue<T> UC, d0OverUC;
		List<ComplexValue<T>> X, U = getU ();
		ComplexValue<T> C = getC (deltaValues), deltaZero = deltaValues.get (0);
		ComplexValue<T> multiplier = neg (ONE).divideBy (THREE.times (a));
		X = new ArrayList<ComplexValue<T>> ();

		for (ComplexValue<T> u : U)
		{
			UC = u.times (C); d0OverUC = deltaZero.divideBy (UC);
			X.add (multiplier.times (sigma (b, UC, d0OverUC)));
		}

		return X;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.FunctionAnalyzer#analyze(net.myorb.math.Polynomial.PowerFunction)
	 */
	public List<ComplexValue<T>> analyze (Polynomial.PowerFunction<ComplexValue<T>> polynomial)
	{
		Polynomial.Coefficients<ComplexValue<T>> coefficients = polynomial.getCoefficients ();
		ComplexValue<T> a = coefficients.get (3), b = coefficients.get (2),
				c = coefficients.get (1), d = coefficients.get (0);
		List<ComplexValue<T>> roots = getRoots (a, b, c, d);
		return roots;
	}


	/**
	 * check for discriminant zero
	 * @param a the (possible complex) coefficient of x^3
	 * @param b the (possible complex) coefficient of x^2
	 * @param c the (possible complex) coefficient of x
	 * @param d the Y-intercept (constant term)
	 * @param delta list of delta values
	 * @return the list of roots
	 */
	public List<ComplexValue<T>> specialCaseCheck
		(
			ComplexValue<T> a, ComplexValue<T> b, ComplexValue<T> c,
			ComplexValue<T> d, List<ComplexValue<T>> delta
		)
	{
		if (delta.get (4).isZero ()) // discriminant was saved as d#4
		{
			ComplexValue<T> deltaZero;
			if (!(deltaZero = delta.get (0)).isZero ())
				return doublePlusExtraReal (a, b, c, d, deltaZero);
			else return tripleReal (a, b);
		}
		else return null;
	}


	/**
	 * discriminant is zero and d#0 is not zero.
	 *  there will be one double root and one single, all real
	 * @param a the (possible complex) coefficient of x^3
	 * @param b the (possible complex) coefficient of x^2
	 * @param c the (possible complex) coefficient of x
	 * @param d the Y-intercept (constant term)
	 * @param deltaZero the value of d#0
	 * @return the list of roots
	 */
	public List<ComplexValue<T>> doublePlusExtraReal
		(
			ComplexValue<T> a, ComplexValue<T> b, ComplexValue<T> c,
			ComplexValue<T> d, ComplexValue<T> deltaZero
		)
	{
		ComplexValue<T>
			BC = b.times (c),
			nineAD = NINE.times (a).times (d);
		List<ComplexValue<T>> X = new ArrayList<ComplexValue<T>> ();
		ComplexValue<T> doubleRoot = nineAD.plus (neg (BC)).divideBy (TWO.times (deltaZero));
		ComplexValue<T> singleRoot = FOUR.times (a).times (BC).plus (neg (nineAD.times (a)
					.plus (b.toThe (3)))).divideBy (a.times (deltaZero));
		X.add (doubleRoot); X.add (doubleRoot); X.add (singleRoot);
		return X;
	}


	/**
	 * discriminant is zero and d#0 is also zero.
	 *  list will contain one triple real root entered 3 times.
	 * @param a the (possible complex) coefficient of x^3
	 * @param b the (possible complex) coefficient of x^2
	 * @return the list of roots
	 */
	public List<ComplexValue<T>> tripleReal (ComplexValue<T> a, ComplexValue<T> b)
	{
		List<ComplexValue<T>> X = new ArrayList<ComplexValue<T>> ();
		ComplexValue<T> value = neg (b).divideBy (THREE.times (a));
		X.add (value); X.add (value); X.add (value);
		return X;
	}


	/**
	 * body of the unit test
	 * @return computed roots
	 */
	public List<ComplexValue<T>> test ()
	{
		ComplexValue<T> a = discrete(1), b = discrete(-3), c = discrete(-144), d = discrete(432);

		System.out.println ("x^3 - 3x^2 - 144x + 432");

		System.out.println ("discriminant="+discriminant (a, b, c, d));
		System.out.println ("delta="+getDelta (a, b, c, d));
		System.out.println ("C="+getC (a, b, c, d));
		System.out.println ("U="+getU());

		System.out.println ("roots="+getRoots (a, b, c, d));
		System.out.println ();

		a = discrete(1); b = discrete(2); c = discrete(-7); d = discrete(4);

		System.out.println ("x^3 + 2x^2 - 7x + 4");

		System.out.println ("discriminant="+discriminant (a, b, c, d));
		System.out.println ("delta="+getDelta (a, b, c, d));
		System.out.println ("C="+getC (a, b, c, d));
		System.out.println ("U="+getU());

		System.out.println ("roots="+getRoots (a, b, c, d));
		System.out.println ();

		a = discrete(1); b = discrete(-3); c = discrete(3); d = discrete(1);

		System.out.println ("x^3 - 3x^2 + 3x + 1");

		System.out.println ("discriminant="+discriminant (a, b, c, d));
		System.out.println ("delta="+getDelta (a, b, c, d));
		System.out.println ("C="+getC (a, b, c, d));
		System.out.println ("U="+getU());

		List<ComplexValue<T>> roots = getRoots (a, b, c, d);
		System.out.println ("roots="+roots);
		return roots;
	}


	/**
	 * unit test driver
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		ComplexFieldManager<Double> cmgr =
			new ComplexFieldManager<Double>(new DoubleFloatingFieldManager ());
		CubicEquation<Double> equation = new CubicEquation<Double> (cmgr);
		equation.test ();
	}


}


