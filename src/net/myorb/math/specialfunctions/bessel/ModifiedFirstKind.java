
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.polynomial.PolynomialSpaceManager;

import net.myorb.math.Polynomial;
import net.myorb.math.Function;

/**
 * support for describing Bessel I (Modified First Kind) functions
 * @author Michael Druckman
 */
public class ModifiedFirstKind extends UnderlyingOperators
{


	// I#a(x) = SUMMATION [ 0 <= k <= INFINITY ] ( (x/2)^(2*k+a) / ( k! * GAMMA(k+a+1) ) )


	/**
	 * describe a Bessel function Ia where a is a real number
	 * @param a real number identifying the order of the Ia description
	 * @param termCount the number of terms to include in the polynomial
	 * @param psm a space manager for polynomial management
	 * @return a function description for Ia
	 * @param <T> data type manager
	 */
	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getI (T a, int termCount, PolynomialSpaceManager<T> psm)
	{
		ExpressionSpaceManager<T> sm = getExpressionManager (psm);
		Polynomial.PowerFunction<T> poly = sumOfTerms (0, 0, termCount, plusOne (a, sm), psm, true, getGammaSum ());
		return new IaFunction<T>(a, poly, sm);
	}


	/**
	 * connect polynomial part with exponential part of function
	 * @param <T> type on which operations are to be executed
	 */
	public static class IaFunction<T> extends ExponentialFunction<T>
	{

		IaFunction
			(
				T a,
				Function<T> polynomial,
				ExpressionSpaceManager<T> sm
			)
		{
			super (a, polynomial, sm);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionDescription()
		 */
		public StringBuffer getFunctionDescription ()
		{
			return new StringBuffer ("Bessel: I(a=").append (parameterValue).append (")");
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionName()
		 */
		public String getFunctionName ()
		{
			return "IA" + formatParameterDisplay (parameterValue);
		}
		
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.bessel.UnderlyingOperators#getFunction(java.lang.Object, int, net.myorb.math.polynomial.PolynomialSpaceManager)
	 */
	public <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getFunction (T parameter, int terms, PolynomialSpaceManager<T> psm)
	{
		return getI (parameter, terms, psm);
	}


}

