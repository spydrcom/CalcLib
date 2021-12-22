
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;
import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.Polynomial;
import net.myorb.math.Function;

/**
 * support for describing Struve L (Modified First Kind) functions
 * @author Michael Druckman
 */
public class ModifiedFirstKindStruve extends StruvePrimitive
{


	// L#a = SUMMATION [ 0 <= m <= INFINITY ] ( (x/2)^(2*m+a+1) / ( GAMMA(m+3/2) * GAMMA(m+a+3/2) ) )


	/**
	 * build the integer exponent polynomial description of Ln
	 * @param n the integer number identifying the order of the Ln description
	 * @param termCount the number of terms to include in the polynomial
	 * @param psm a space manager for polynomial management
	 * @return description of La polynomial
	 * @param <T> data type manager
	 */
	public static <T> Polynomial.PowerFunction<T>
		getL (int n, int termCount, PolynomialSpaceManager<T> psm)
	{
		return modifiedSumOfTerms (n, termCount, psm);
	}


	/**
	 * describe a Struve function La where a is a real number
	 * @param a a real number identifying the order of the La description
	 * @param termCount the number of terms to include in the polynomial
	 * @param psm a space manager for polynomial management
	 * @return a function description for La
	 * @param <T> data type manager
	 */
	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getL (T a, int termCount, PolynomialSpaceManager<T> psm)
	{
		return new LaFunction<T>(a, termCount, psm);
	}


	/**
	 * connect polynomial part with exponential part of function
	 * @param <T> type on which operations are to be executed
	 */
	public static class LaFunction<T> extends ExponentialFunction<T>
	{

		LaFunction
			(
				T a, int n,
				PolynomialSpaceManager<T> psm
			)
		{
			this (a, n, psm, getExpressionManager (psm));
		}

		LaFunction
			(
				T a, int n,
				PolynomialSpaceManager<T> psm,
				ExpressionSpaceManager<T> sm
			)
		{
			this (a, getModifiedPoly (a, n, psm, sm), sm);
		}

		LaFunction
			(
				T a,
				Function<T> polynomial,
				ExpressionSpaceManager<T> sm
			)
		{
			// Struve uses Double to allow change exponent to alpha+1
			super (sm.convertToDouble (a), polynomial, sm);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionDescription()
		 */
		public StringBuffer getFunctionDescription ()
		{
			return new StringBuffer ("Struve: L(a=").append (displayParameter).append (")");
		}
		public StringBuffer getElaborateFunctionDescription () { return getFunctionDescription (); }

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.bessel.UnderlyingOperators#getRenderIdentifier()
		 */
		public String getRenderIdentifier () { return "L"; }

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionName()
		 */
		public String getFunctionName ()
		{
			return "LA" + formatParameterDisplay (displayParameter.doubleValue ());
		}
		
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.bessel.UnderlyingOperators#getFunction(java.lang.Object, int, net.myorb.math.polynomial.PolynomialSpaceManager)
	 */
	public <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getFunction (T parameter, int terms, PolynomialSpaceManager<T> psm)
	{
		return getL (parameter, terms, psm);
	}


	/**
	 * extended to domain beyond Real
	 * @param <T> the data type
	 */
	public static class LaExtendedFunction<T> extends LaFunction<T>
	{

		LaExtendedFunction
			(
				T a, int n,
				PolynomialSpaceManager<T> psm,
				ExtendedPowerLibrary<T> lib
			)
		{
			super (a, n, psm);
			this.lib = lib;
		}
		ExtendedPowerLibrary<T> lib;
	
		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.bessel.UnderlyingOperators.ExponentialFunction#TraisedToT(java.lang.Object, java.lang.Object)
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
		return getL (parameter, terms, lib, psm);
	}

	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getL (T p, int termCount, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm)
	{
		return new LaExtendedFunction<T>(p, termCount, psm, lib);
	}


}

