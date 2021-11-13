
package net.myorb.math;

import java.util.ArrayList;
import java.util.List;

/**
 * realization of generating function forms
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class GeneratingFunctions<T> extends Tolerances<T>
{


	/**
	 * an ordered list of coefficients defines a polynomial function
	 * @param <T> type of component values on which operations are to be executed
	 */
	public static class Coefficients <T> extends java.util.ArrayList <T>
	{
		/*
		 * f(x) = c0 + c1*x + c2*x^2 + c3*x^3 + ...
		 */
		public Coefficients () {}
		public Coefficients (List <T> c) { addAll (c); }
		
		public String toString ()
		{
			StringBuffer text = new StringBuffer ("(").append (get (0));
			for (int i = 1; i < this.size (); i++) text.append (", ").append (get (i));
			return text.append (")").toString ();
		}
		static final long serialVersionUID = 1l;
	}


	/**
	 * build a new coefficients list
	 * @param c an ordered sequence of coefficient values
	 * @return the new Coefficients object
	 */
	@SafeVarargs
	public final Coefficients<T> newCoefficients (T... c)
	{
		Coefficients<T> list;
		addToList (list = new Coefficients<T> (), c);
		return list;
	}
	public Coefficients<T> toCoefficients (T[] c)
	{ return newCoefficients (c); }

	/**
	 * build a new coefficients list
	 * @param c an ordered sequence of integer values
	 * @return the new Coefficients object
	 */
	public Coefficients<T> coefficients (int[] c)
	{
		List<T> converted = new ArrayList<T>();
		for (int i : c) { converted.add (manager.newScalar (i)); }
		Coefficients<T> result = new Coefficients<T>();
		result.addAll (converted);
		return result;
	}


	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 */
	public GeneratingFunctions
		(SpaceManager<T> manager)
	{ super (manager); }


	/**
	 * add coefficient into series sum
	 * @param sumOfPowerTerms sum of all power terms of series
	 * @param coefficients the list of coefficients of the polynomial
	 * @param n the number of the term being applied to the series
	 * @return Nth coefficient added to power term sum
	 */
	public Value<T> addCoefficientIntoSum
	(Value<T> sumOfPowerTerms, Coefficients<T> coefficients, int n)
	{ return sumOfPowerTerms.plus (forValue (coefficients.get (n))); }


	/**
	 * add the constant coefficient term into the series sum
	 * @param sumOfPowerTerms sum of all power terms of series
	 * @param coefficients the list of coefficients of the polynomial
	 * @return constant term added to power term sum
	 */
	public Value<T> addConstantTerm (Value<T> sumOfPowerTerms, Coefficients<T> coefficients)
	{ return addCoefficientIntoSum (sumOfPowerTerms, coefficients, 0); }


	/**
	 * compute the value of the
	 *  ordinary generating function at specified X value
	 * @param coefficients the list of coefficients of the polynomial
	 * @param x the value of X to use for the evaluation
	 * @return the computed result
	 */
	public Value<T> ordinary (Coefficients<T> coefficients, Value<T> x)
	{
		Value<T> value = zeroValue ();
		int n = coefficients.size () - 1;
		for (int i = n; i > 0; i--)
		{
			value =
				addCoefficientIntoSum
					(value, coefficients, i)
				.times (x);
		}
		return addConstantTerm (value, coefficients);
	}
	public T G (Coefficients<T> coefficients, T x)
	{ return ordinary (coefficients, forValue (x)).getUnderlying (); }


	/**
	 * compute the value of the
	 *  exponential generating function at specified X value
	 * @param coefficients the list of coefficients of the generating function
	 * @param x the value of X to use for the evaluation
	 * @return the computed result
	 */
	public Value<T> exponential (Coefficients<T> coefficients, Value<T> x)
	{
		Value<T> value = zeroValue ();
		int n = coefficients.size () - 1;
		for (int i = n; i > 0; i--)
		{
			value =
				addCoefficientIntoSum
					(value, coefficients, i)
				.times (x).over (forValue (i));
		}
		return addConstantTerm (value, coefficients);
	}
	public T EG (Coefficients<T> coefficients, T x)
	{ return exponential (coefficients, forValue (x)).getUnderlying (); }


	/**
	 * compute the value of the
	 *  Lambert generating function at specified X value
	 * @param coefficients the list of coefficients of the generating function
	 * @param x the value of X to use for the evaluation
	 * @return the computed result
	 */
	public Value<T> lambert (Coefficients<T> coefficients, Value<T> x)
	{
		int n = coefficients.size () - 1;
		Value<T> value = forValue (coefficients.get (0));
		Value<T> xn = x, ONE = forValue (1);

		for (int i = 1; i <= n; i++)
		{
			value = value.plus
				(
					xn.over (ONE.minus (xn)).times
					(forValue (coefficients.get (i)))
				);
			xn = xn.times (x);
		}

		return value;
	}
	public T LG (Coefficients<T> coefficients, T x)
	{ return lambert (coefficients, forValue (x)).getUnderlying (); }


	/**
	 * compute the value of the
	 *  Poisson generating function at specified X value
	 * @param coefficients the list of coefficients of the generating function
	 * @param x the value of X to use for the evaluation
	 * @return the computed result
	 */
	public T PG (Coefficients<T> coefficients, T x)
	{ return manager.multiply (lib.exp (x), EG (coefficients, x)); }
	public Value<T> poisson (Coefficients<T> coefficients, Value<T> x)
	{ return forValue (PG (coefficients, x.getUnderlying ())); }


}


