
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;
import net.myorb.math.specialfunctions.bessel.BesselDescription.OrderTypes;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.polynomial.PolynomialSpaceManager;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.Function;

import java.util.Map;

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
			this.setBesselDescription ("I", "a", OrderTypes.NON_SPECIFIC);
		}

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


	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.bessel.UnderlyingOperators#getFunction(java.lang.Object, int, net.myorb.math.ExtendedPowerLibrary, net.myorb.math.polynomial.PolynomialSpaceManager)
	 */
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


	/**
	 * special case for using integral
	 * @param parameter the alpha value order
	 * @param terms the count of terms for the series
	 * @param parameters a hash of name/value pairs passed from configuration
	 * @param psm the manager for the polynomial space
	 * @return the function description
	 * @param <T> data type manager
	 */
	public <T> SpecialFunctionFamilyManager.FunctionDescription<T> getSpecialCase
		(T parameter, int terms, Map<String,Object> parameters, PolynomialSpaceManager<T> psm)
	{
		ExpressionSpaceManager<T> sm = getExpressionManager (psm);
		return getI (parameter, terms, parameters, sm);
	}


	/**
	 * an alternative to the LIM [ a -&gt; n ]...
	 *  the function evaluation algorithm from integral
	 * @param a the value of alpha which is integer/real
	 * @param termCount the count of terms for the series
	 * @param parameters a hash of name/value pairs passed from configuration
	 * @param sm the manager for the data type
	 * @return the function description
	 * @param <T> data type manager
	 */
	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getI (T a, int termCount, Map<String,Object> parameters, ExpressionSpaceManager<T> sm)
	{
		return new IaFunctionDescription<T> (a, termCount, parameters, sm);
	}


}


/**
 * encapsulation of descriptive text portions of FunctionDescription
 * @param <T> data type in use
 */
abstract class IDescription<T> extends BesselDescription<T>
{
	IDescription
	(T a, ExpressionSpaceManager<T> sm)
	{ super (a, OrderTypes.NON_SPECIFIC, "I", "a", sm); }
}


/**
 * Function Description for special case of Ia implementation.
 *  domain of real numbers, integral form is: exp ( x * cos (t) ) * cos (a * t)
 * @param <T> data type in use
 */
class IaFunctionDescription<T> extends IDescription<T>
{

	IaFunctionDescription
		(
			T a, int termCount,
			Map<String,Object> parameters,
			ExpressionSpaceManager<T> sm
		)
	{
		super (a, sm);
		this.I = new Ia (sm.convertToDouble (a), termCount, parameters);
		this.parameters = parameters;
	}
	Ia I;

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.bessel.BesselDescription#getElaboration()
	 */
	public String getElaboration () { return "   " + parameters.toString (); }
	Map<String,Object> parameters;

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.bessel.BesselDescription#evalReal(double)
	 */
	public double evalReal (double x) { return I.eval (x); }

}


/**
 * provide real version of Ia using integral algorithm.
 * TanhSinh Quadrature is used to provide numerical integration
 */
class Ia extends BesselCommonSectionedAlgorithm
{

	Ia (double a, int infinity, Map<String,Object> parameters)
	{
		super
		(
			new IalphaPart1Integrand (a),
			new IalphaPart2Integrand (a),
			parameters, infinity
		);
	}

/*
	!! iap2 (x,a,t) = exp ( - x * cosh (t) - a * t )
	!! iap1 (x,a,t) = exp ( x * cos (t) ) * cos (a * t)

	!! Ia (x,a) = 1/pi * ( INTEGRAL [0 <= t <= pi <> dt] (iap1 (x, a, t) * <*> t) -
			sin (a * pi) * INTEGRAL [0 <= t <= INFINITY <> dt] (iap2 (x, a, t) * <*> t) )
 */

}


/**
 * part1 integral form of Ia
 */
class IalphaPart1Integrand extends BesselSectionedAlgorithm.BesselSectionIntegrand
{
	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public Double eval (Double t)
	{ return Math.exp ( x * Math.cos (t) ) * Math.cos (a * t); }
	IalphaPart1Integrand (double a) { super (a); }
}


/**
 * part2 integral form of Ia
 */
class IalphaPart2Integrand extends BesselSectionedAlgorithm.BesselSectionIntegrand
{
	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public Double eval (Double t)
	{ return Math.exp ( - x * Math.cosh (t) - a * t ); }
	IalphaPart2Integrand (double a) { super (a); }
}

