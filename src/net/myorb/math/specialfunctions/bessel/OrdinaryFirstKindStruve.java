
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.ExtendedPowerLibrary;

import net.myorb.math.Polynomial;
import net.myorb.math.Function;

/**
 * support for describing Struve H (Ordinary First Kind) functions
 * @author Michael Druckman
 */
public class OrdinaryFirstKindStruve extends UnderlyingOperators
{

	// J#p = SUMMATION [ 0 <= k <= INFINITY ] ( (-1)^k * (x/2)^(2*k+p) / ( k! * GAMMA(k+p+1) ) )

	// H#a = SUMMATION [ 0 <= m <= INFINITY ] ( (-1)^m * (x/2)^(2*m+a+1) / ( GAMMA(m+3/2) * GAMMA(m+a+3/2) ) )


	/**
	 * build the integer exponent polynomial description of Hn
	 * @param n the integer number identifying the order of the Hn description
	 * @param termCount the number of terms to include in the polynomial
	 * @param psm a space manager for polynomial management
	 * @return description of Ha polynomial
	 * @param <T> data type manager
	 */
	public static <T> Polynomial.PowerFunction<T>
		getH (int n, int termCount, PolynomialSpaceManager<T> psm)
	{
		return sumOfTerms (n, n, termCount, n, psm, false, getFactorialSum ());
	}


	/**
	 * describe a Struve function Ha where a is a real number
	 * @param a a real number identifying the order of the Ha description
	 * @param termCount the number of terms to include in the polynomial
	 * @param psm a space manager for polynomial management
	 * @return a function description for Ha
	 * @param <T> data type manager
	 */
	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getH (T a, int termCount, PolynomialSpaceManager<T> psm)
	{
		ExpressionSpaceManager<T> sm = getExpressionManager (psm);
		return new HaFunction<T>(a, getPoly (a, false, termCount, psm, sm), sm);
	}


	/**
	 * connect polynomial part with exponential part of function
	 * @param <T> type on which operations are to be executed
	 */
	public static class HaFunction<T> extends ExponentialFunction<T>
	{

		HaFunction
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
			return new StringBuffer ("Struve: H(a=").append (parameterValue).append (")");
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionName()
		 */
		public String getFunctionName ()
		{
			return "HA" + formatParameterDisplay (parameterValue);
		}
		
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.bessel.UnderlyingOperators#getFunction(java.lang.Object, int, net.myorb.math.polynomial.PolynomialSpaceManager)
	 */
	public <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getFunction (T parameter, int terms, PolynomialSpaceManager<T> psm)
	{
		return getH (parameter, terms, psm);
	}


	/**
	 * extended to domain beyond Real
	 * @param <T> the data type
	 */
	public static class HaExtendedFunction<T> extends HaFunction<T>
	{
	
		HaExtendedFunction
			(
				T a,
				Function<T> polynomial,
				ExtendedPowerLibrary<T> lib,
				ExpressionSpaceManager<T> sm
			)
		{
			super (a, polynomial, sm);
			this.lib = lib;
		}
		ExtendedPowerLibrary<T> lib;
	
		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.bessel.OrdinaryFirstKind.JpFunction#TraisedToT(java.lang.Object, java.lang.Object)
		 */
		public T TraisedToT (T base, T power)
		{
			return lib.power (base, power);
		}
	
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.bessel.UnderlyingOperators#getFunction(java.lang.Object, int, net.myorb.math.ExtendedPowerLibrary, net.myorb.math.polynomial.PolynomialSpaceManager)
	 */
	public <T> SpecialFunctionFamilyManager.FunctionDescription<T> getFunction
		(T parameter, int terms, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm)
	{
		return getH (parameter, terms, lib, psm);
	}
	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getH (T p, int termCount, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm)
	{
		ExpressionSpaceManager<T> sm = getExpressionManager (psm);
		return new HaExtendedFunction<T>(p, getPoly (p, false, termCount, psm, sm), lib, sm);
	}


}

