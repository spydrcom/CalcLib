
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.computational.TanhSinhQuadratureAlgorithms;
import net.myorb.math.specialfunctions.Library;
import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.data.abstractions.SpaceDescription;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;
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


	/**
	 * special case for using integral
	 * @param parameter the alpha value order
	 * @param terms the count of terms for the series
	 * @param precision target value for approximation error
	 * @param psm the manager for the polynomial space
	 * @return the function description
	 * @param <T> data type manager
	 */
	public <T> SpecialFunctionFamilyManager.FunctionDescription<T> getSpecialCase
		(T parameter, int terms, double precision, PolynomialSpaceManager<T> psm)
	{
		ExpressionSpaceManager<T> sm = getExpressionManager (psm);
		return getI (parameter, terms, precision, sm);
	}


	/**
	 * an alternative to the LIM [ a -&gt; n ]...
	 *  the function evaluation algorithm from integral
	 * @param a the value of alpha which is integer/real
	 * @param termCount the count of terms for the series
	 * @param precision target value for approximation error
	 * @param sm the manager for the data type
	 * @return the function description
	 * @param <T> data type manager
	 */
	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getI (T a, int termCount, double precision, ExpressionSpaceManager<T> sm)
	{
		return new IaFunctionDescription<T> (a, termCount, precision, sm);
	}


}


/**
 * Function Description for special case of Ia implementation.
 *  domain of real numbers, integral form is: exp ( x * cos (t) ) * cos (a * t)
 * @param <T> data type in use
 */
class IaFunctionDescription<T> implements SpecialFunctionFamilyManager.FunctionDescription<T>
{

	IaFunctionDescription (T a, int termCount, double precision, ExpressionSpaceManager<T> sm)
	{ this.I = new Ia (sm.convertToDouble (a), termCount, precision); this.sm = sm; this.a = a; }
	ExpressionSpaceManager<T> sm; Ia I; T a;

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T x)
	{
		return sm.convertFromDouble ( I.eval ( sm.convertToDouble (x) ) );
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionDescription()
	 */
	public StringBuffer getFunctionDescription ()
	{
		return new StringBuffer ("Bessel: I(a=").append (a).append (")");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getRenderIdentifier()
	 */
	public String getRenderIdentifier () { return "I"; }

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionName()
	 */
	public String getFunctionName () { return "I_" + a; }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceDescription<T> getSpaceDescription () { return sm; }
	public SpaceManager<T> getSpaceManager () { return sm; }

}


/**
 * provide real version of Ia using integral algorithm.
 * TanhSinh Quadrature is used to provide numerical integration
 */
class Ia
{

	Ia (double a, int infinity, double prec)
	{
		this.pi = Math.PI; this.a = a; this.infinity = infinity;
		this.nonIntegerAlpha = ! Library.isInteger (a);
		this.sinAlphaPi = - Math.sin (a * pi);
		this.targetAbsoluteError = prec;
	}
	double pi, a, sinAlphaPi, targetAbsoluteError;
	int infinity; boolean nonIntegerAlpha;

	/**
	 * Integral form of Ia
	 * @param x parameter to Ia function
	 * @return calculated result
	 */
	double eval (double x)
	{
		double sum = part1 (x);
		if (nonIntegerAlpha) sum += sinAlphaPi * part2 (x);
		return sum / pi;
	}
	double part1 (double x)
	{
		Function<Double> f = new IalphaPart1Integrand (x, a);
		return TanhSinhQuadratureAlgorithms.Integrate
		(f, 0, pi, targetAbsoluteError, null);
	}
	double part2 (double x)
	{
		Function<Double> f = new IalphaPart2Integrand (x, a);
		return TanhSinhQuadratureAlgorithms.Integrate
		(f, 0, infinity, targetAbsoluteError, null);
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
class IalphaPart1Integrand extends RealIntegrandFunctionBase
{
	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public Double eval (Double t)
	{ return Math.exp ( x * Math.cos (t) ) * Math.cos (a * t); }
	IalphaPart1Integrand (double x, double a) { super (x, a); }
}


/**
 * part2 integral form of Ia
 */
class IalphaPart2Integrand extends RealIntegrandFunctionBase
{
	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public Double eval (Double t)
	{ return Math.exp ( - x * Math.cosh (t) - a * t ); }
	IalphaPart2Integrand (double x, double a) { super (x, a); }
}

