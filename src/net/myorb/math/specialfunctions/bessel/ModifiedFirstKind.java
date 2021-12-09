
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.polynomial.PolynomialSpaceManager;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.Function;

/**
 * support for describing Bessel I (Modified First Kind) functions
 * @author Michael Druckman
 */
public class ModifiedFirstKind extends BesselPrimitive
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
		return new IaFunction<T>(a, termCount, psm);
	}


	/**
	 * connect polynomial part with exponential part of function
	 * @param <T> type on which operations are to be executed
	 */
	public static class IaFunction<T> extends ExponentialFunction<T>
	{

		IaFunction
			(
				T a, int n,
				PolynomialSpaceManager<T> psm
			)
		{
			this (a, n, psm, getExpressionManager (psm));
		}
	
		IaFunction
			(
				T a, int n,
				PolynomialSpaceManager<T> psm,
				ExpressionSpaceManager<T> sm
			)
		{
			this (a, getModifiedPoly (a, n, psm, sm), sm);
		}

		IaFunction
			(
				T a,
				Function<T> polynomial,
				ExpressionSpaceManager<T> sm
			)
		{
			super (sm.toNumber (a), polynomial, sm);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionDescription()
		 */
		public StringBuffer getFunctionDescription ()
		{
			return new StringBuffer ("Bessel: I(a=").append (displayParameter).append (")");
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionName()
		 */
		public String getFunctionName ()
		{
			return "IA" + formatParameterDisplay (displayParameter.doubleValue ());
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getRenderIdentifier()
		 */
		public String getRenderIdentifier () { return "I"; }

	}


	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.bessel.UnderlyingOperators#getFunction(java.lang.Object, int, net.myorb.math.polynomial.PolynomialSpaceManager)
	 */
	public <T> SpecialFunctionFamilyManager.FunctionDescription<T> getFunction
		(T parameter, int terms, PolynomialSpaceManager<T> psm)
	{
		return getI (parameter, terms, psm);
	}


	/**
	 * extended to domain beyond Real
	 * @param <T> the data type
	 */
	public static class IaExtendedFunction<T> extends IaFunction<T>
	{

		IaExtendedFunction
		(
				T p, int n,
				PolynomialSpaceManager<T> psm,
				ExtendedPowerLibrary<T> lib
			)
		{
			super (p, n, psm);
			this.lib = lib;
		}
		ExtendedPowerLibrary<T> lib;
	
		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.bessel.ModifiedFirstKind.IaFunction#TraisedToT(java.lang.Object, java.lang.Object)
		 */
		public T TraisedToT (T base, T power)
		{
			return lib.power (base, power);
		}
	
	}


	public <T> SpecialFunctionFamilyManager.FunctionDescription<T> getFunction
		(T parameter, int terms, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm)
	{
		return getI (parameter, terms, lib, psm);
	}


	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getI (T a, int termCount, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm)
	{
		return new IaExtendedFunction<T>(a, termCount, psm, lib);
	}


}

