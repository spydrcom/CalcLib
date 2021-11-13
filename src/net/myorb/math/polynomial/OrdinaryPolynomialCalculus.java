
package net.myorb.math.polynomial;

import net.myorb.data.abstractions.Function;

import net.myorb.math.Polynomial;
import net.myorb.math.SpaceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * compute derivatives and anti-derivatives for ordinary polynomials
 * @param <T> the types of coefficients in the polynomial terms
 * @author Michael Druckman
 */
public class OrdinaryPolynomialCalculus<T> extends PolynomialCalculus<T>
	implements PolynomialCalculusSupport<T>
{


	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 */
	public OrdinaryPolynomialCalculus
		(SpaceManager<T> manager)
	{ super (manager); }


	/**
	 * compute the coefficients of the integral function
	 * @param coefficients the coefficients of the function being integrated
	 * @return the Coefficients object defining the integral
	 */
	public Coefficients<T> computeIntegralCoefficients
		(Coefficients<T> coefficients)
	{
		int divisor = 1;
		Coefficients<T> integral = new Coefficients<T> ();
		integral.add (manager.getZero ());

		for (int i = 0; i < coefficients.size (); i++)
		{
			integral.add
			(
				X (coefficients.get (i), manager.invert (discrete (divisor++)))
			);
		}

		return integral;
	}


	/**
	 * compute the coefficients of the derivative function
	 * @param coefficients the coefficients of the function being derived
	 * @return the Coefficients object defining the derivative
	 */
	public Coefficients<T> computeDerivativeCoefficients
				(Coefficients<T> coefficients)
	{
		Coefficients<T> derivative = new Coefficients<T> ();
		if (coefficients.size () <= 1)
		{
			derivative.add (discrete (0));
		}
		else
		{
			int multiplier = 1;
			for (int i = 1; i < coefficients.size (); i++)
			{
				derivative.add
				(
					X (coefficients.get (i), discrete (multiplier++))
				);
			}
		}
		return derivative;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialCalculus#getFunctionDerivative(net.myorb.math.Function)
	 */
	public Function<T> getFunctionDerivative (Function<T> f)
	{
		return getFunctionDerivative ((Polynomial.PowerFunction<T>)f);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialCalculus#getFunctionIntegral(net.myorb.math.Function)
	 */
	public Function<T> getFunctionIntegral (Function<T> f)
	{
		return getFunctionIntegral ((Polynomial.PowerFunction<T>)f);
	}


	/**
	 * build derivative of a polynomial function
	 * @param function the function used to compute the derivative
	 * @return the function wrapper for the derivative
	 */
	public PowerFunction<T> getFunctionDerivative (PowerFunction<T> function)
	{
		return getPolynomialFunction (computeDerivativeCoefficients (function.getCoefficients ()));
	}


	/**
	 * build anti-derivative of a polynomial function
	 * @param function the function used to compute the anti-derivative
	 * @return the function wrapper for the anti-derivative
	 */
	public PowerFunction<T> getFunctionIntegral (PowerFunction<T> function)
	{
		return getPolynomialFunction (computeIntegralCoefficients (function.getCoefficients ()));
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.Polynomial#evaluatePolynomialDerivative(net.myorb.math.GeneratingFunctions.Coefficients, java.lang.Object)
	 */
	public T evaluatePolynomialDerivative (Coefficients<T> a, T atX)
	{
		return evaluatePolynomial (computeDerivativeCoefficients (a), atX);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.Polynomial#evaluatePolynomialIntegral(net.myorb.math.GeneratingFunctions.Coefficients, java.lang.Object)
	 */
	public T evaluatePolynomialIntegral (Coefficients<T> a, T atX)
	{
		return evaluatePolynomial (computeIntegralCoefficients (a), atX);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.Polynomial#evaluatePolynomialIntegral(net.myorb.math.GeneratingFunctions.Coefficients, java.lang.Object, java.lang.Object)
	 */
	public T evaluatePolynomialIntegral (Coefficients<T> a, T fromX, T toX)
	{
		Coefficients<T> integral =
			computeIntegralCoefficients (a);
		T hi =  evaluatePolynomial (integral, toX);
		T lo =  evaluatePolynomial (integral, fromX);
		return manager.add (hi, manager.negate (lo));
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.Polynomial#evaluatePolynomialIntegral(net.myorb.math.GeneratingFunctions.Coefficients, java.lang.Object, java.util.List)
	 */
	public List<T> evaluatePolynomialIntegral (Coefficients<T> a, T fromX, List<T> toX)
	{
		List<T> results = new ArrayList<T>();
		Coefficients<T> integral = computeIntegralCoefficients (a);
		T negLo =  manager.negate (evaluatePolynomial (integral, fromX));

		for (T x : toX)
		{
			T hi =  evaluatePolynomial (integral, x);
			T value = manager.add (hi, negLo);
			results.add (value);
		}

		return results;
	}


}

