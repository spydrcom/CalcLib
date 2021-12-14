
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.specialfunctions.Library;
import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.math.ExtendedPowerLibrary;

import net.myorb.math.Polynomial;
import net.myorb.math.SpaceManager;
import net.myorb.math.computational.TanhSinhQuadratureAlgorithms;
import net.myorb.math.Function;

/**
 * support for describing Bessel J (Ordinary First Kind) functions
 * @author Michael Druckman
 */
public class OrdinaryFirstKind extends BesselPrimitive
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
		return ordinarySumOfTerms (n, termCount, psm);
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
		return new JpFunction<T>(p, termCount, psm);
	}


	/**
	 * connect polynomial part with exponential part of function
	 * @param <T> type on which operations are to be executed
	 */
	public static class JpFunction<T> extends ExponentialFunction<T>
	{

		JpFunction
			(
				T a, int n,
				PolynomialSpaceManager<T> psm
			)
		{
			this (a, n, psm, getExpressionManager (psm));
		}
	
		JpFunction
			(
				T a, int n,
				PolynomialSpaceManager<T> psm,
				ExpressionSpaceManager<T> sm
			)
		{
			this (a, getOrdinaryPoly (a, n, psm, sm), sm);
		}

		JpFunction
			(
				T p,
				Function<T> polynomial,
				ExpressionSpaceManager<T> sm
			)
		{
			super (sm.toNumber (p), polynomial, sm);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionDescription()
		 */
		public StringBuffer getFunctionDescription ()
		{
			return new StringBuffer ("Bessel: J(p=").append (displayParameter).append (")");
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getRenderIdentifier()
		 */
		public String getRenderIdentifier () { return "J"; }

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionName()
		 */
		public String getFunctionName ()
		{
			return "JP" + formatParameterDisplay (displayParameter.doubleValue ());
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
		return new JpExtendedFunction<T>(p, termCount, psm, lib);
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
		return getJ (parameter, terms, precision, sm);
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
		getJ (T a, int termCount, double precision, ExpressionSpaceManager<T> sm)
	{
		return new JaFunctionDescription<T> (a, termCount, precision, sm);
	}


}


/**
 * Function Description for special case of Ja implementation.
 * @param <T> data type in use
 */
class JaFunctionDescription<T> implements SpecialFunctionFamilyManager.FunctionDescription<T>
{

	JaFunctionDescription (T a, int termCount, double precision, ExpressionSpaceManager<T> sm)
	{ this.J = new Ja (sm.convertToDouble (a), termCount, precision); this.sm = sm; this.a = a; }
	ExpressionSpaceManager<T> sm; Ja J; T a;

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T x)
	{
		return sm.convertFromDouble ( J.eval ( sm.convertToDouble (x) ) );
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionDescription()
	 */
	public StringBuffer getFunctionDescription ()
	{
		return new StringBuffer ("Bessel: J(a=").append (a).append (")");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getRenderIdentifier()
	 */
	public String getRenderIdentifier () { return "J"; }

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionName()
	 */
	public String getFunctionName () { return "J_" + a; }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceDescription<T> getSpaceDescription () { return sm; }
	public SpaceManager<T> getSpaceManager () { return sm; }

}


/**
 * provide real version of Ja using integral algorithm.
 * TanhSinh Quadrature is used to provide numerical integration
 */
class Ja
{

	Ja (double a, int infinity, double prec)
	{
		this.pi = Math.PI; this.a = a; this.infinity = infinity;
		this.nonIntegerAlpha = ! Library.isInteger (a);
		this.sinAlphaPi = - Math.sin (a * pi);
		this.targetAbsoluteError = prec;
	}
	double pi, a, sinAlphaPi, targetAbsoluteError;
	int infinity; boolean nonIntegerAlpha;

	/**
	 * Integral form of Ja
	 * @param x parameter to Ja function
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
		Function<Double> f = new JalphaPart1Integrand (x, a);
		return TanhSinhQuadratureAlgorithms.Integrate
		(f, 0, pi, targetAbsoluteError, null);
	}
	double part2 (double x)
	{
		Function<Double> f = new JalphaPart2Integrand (x, a);
		return TanhSinhQuadratureAlgorithms.Integrate
		(f, 0, infinity, targetAbsoluteError, null);
	}

/*
	!! jap1 (x,a,t) = cos ( a * t - x * sin (t) ) )
	!! jap2 (x,a,t) = exp ( - x * sinh (t) - a * t )

	!! Ja (x,a) = 1/pi * ( INTEGRAL [0 <= t <= pi <> dt] (jap1 (x, a, t) * <*> t) -
			sin (a * pi) * INTEGRAL [0 <= t <= INFINITY <> dt] (jap2 (x, a, t) * <*> t) )
 */

}


/**
 * part1 integral form of Ja
 */
class JalphaPart1Integrand extends RealIntegrandFunctionBase
{
	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public Double eval (Double t)
	{ return Math.exp ( Math.cos ( a * t - x * Math.sin (t) ) ); }
	JalphaPart1Integrand (double x, double a) { super (x, a); }
}


/**
 * part2 integral form of Ja
 */
class JalphaPart2Integrand extends RealIntegrandFunctionBase
{
	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public Double eval (Double t)
	{ return Math.exp ( - x * Math.sinh (t) - a * t ); }
	JalphaPart2Integrand (double x, double a) { super (x, a); }
}

