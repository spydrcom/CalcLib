
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.ExtendedPowerLibrary;

import net.myorb.math.Polynomial;
import net.myorb.math.Function;

/**
 * support for describing Bessel J (Ordinary First Kind) functions
 * @author Michael Druckman
 */
public class OrdinaryFirstKind extends UnderlyingOperators
{


	// J#p = SUMMATION [ 0 <= k <= INFINITY ] ( (-1)^k * (x/2)^(2*k+p) / ( k! * GAMMA(k+p+1) ) )


	/**
	 * build the integer exponent polynomial description of Jn
	 * @param n the integer number identifying the order of the Jn description
	 * @param termCount the number of terms to include in the polynomial
	 * @param psm a space manager for polynomial management
	 * @return description of Jn polynomial
	 * @param <T> data type manager
	 */
	public static <T> Polynomial.PowerFunction<T>
		getJ (int n, int termCount, PolynomialSpaceManager<T> psm)
	{
		return sumOfTerms (n, n, termCount, n, psm, false, getFactorialSum ());
	}


	/**
	 * describe a Bessel function Jp where p is a real number
	 * @param p a real number identifying the order of the Jp description
	 * @param termCount the number of terms to include in the polynomial
	 * @param psm a space manager for polynomial management
	 * @return a function description for Jp
	 * @param <T> data type manager
	 */
	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getJ (T p, int termCount, PolynomialSpaceManager<T> psm)
	{
		ExpressionSpaceManager<T> sm = getExpressionManager (psm);
		return new JpFunction<T>(p, getPoly (p, false, termCount, psm, sm), sm);
	}


	/**
	 * connect polynomial part with exponential part of function
	 * @param <T> type on which operations are to be executed
	 */
	public static class JpFunction<T> extends ExponentialFunction<T>
	{

		JpFunction
			(
				T p,
				Function<T> polynomial,
				ExpressionSpaceManager<T> sm
			)
		{
			super (p, polynomial, sm);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionDescription()
		 */
		public StringBuffer getFunctionDescription ()
		{
			return new StringBuffer ("Bessel: J(p=").append (parameterValue).append (")");
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionName()
		 */
		public String getFunctionName ()
		{
			return "JP" + formatParameterDisplay (parameterValue);
		}
		
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.bessel.UnderlyingOperators#getFunction(java.lang.Object, int, net.myorb.math.polynomial.PolynomialSpaceManager)
	 */
	public <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getFunction (T parameter, int terms, PolynomialSpaceManager<T> psm)
	{
		return getJ (parameter, terms, psm);
	}


	/**
	 * extended to domain beyond Real
	 * @param <T> the data type
	 */
	public static class JpExtendedFunction<T> extends JpFunction<T>
	{
	
		JpExtendedFunction
			(
				T p,
				Function<T> polynomial,
				ExtendedPowerLibrary<T> lib,
				ExpressionSpaceManager<T> sm
			)
		{
			super (p, polynomial, sm);
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


	public <T> SpecialFunctionFamilyManager.FunctionDescription<T> getFunction
		(T parameter, int terms, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm)
	{
		return getJ (parameter, terms, lib, psm);
	}


	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getJ (T p, int termCount, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm)
	{
		ExpressionSpaceManager<T> sm = getExpressionManager (psm);
		return new JpExtendedFunction<T>(p, getPoly (p, false, termCount, psm, sm), lib, sm);
	}


}

