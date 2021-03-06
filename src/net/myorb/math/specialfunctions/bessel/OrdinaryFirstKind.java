
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.specialfunctions.Library;
import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;
import net.myorb.math.specialfunctions.bessel.BesselDescription.OrderTypes;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.ExtendedPowerLibrary;

import net.myorb.math.Polynomial;
import net.myorb.math.Function;

import java.util.Map;

/**
 * support for describing Bessel J (Ordinary First Kind) functions
 * @author Michael Druckman
 */
public class OrdinaryFirstKind extends BesselPrimitive
{


	// J#p = SUMMATION [ 0 <= k <= INFINITY ] ( (-1)^k * (x/2)^(2*k+p) / ( k! * GAMMA(k+p+1) ) )

	// J#n = 1/(2*pi) * INTEGRAL [-pi <= t <= pi <> dt] (exp (i * (x * sin t - n*t)) * <*> t)

	// J#a (i*z) = exp (a*i*pi/2) * I#a(z)
	
	/*
	!! jap1 (x,a,t) = cos ( a * t - x * sin (t) ) )
	!! jap2 (x,a,t) = exp ( - x * sinh (t) - a * t )

	!! Ja (x,a) = 1/pi * ( INTEGRAL [0 <= t <= pi <> dt] (jap1 (x, a, t) * <*> t) -
			sin (a * pi) * INTEGRAL [0 <= t <= INFINITY <> dt] (jap2 (x, a, t) * <*> t) )
	 */


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
		Polynomial.PowerFunction<T>
			J = ordinarySumOfTerms (Math.abs (n), termCount, psm);
		if (n < 0 && n%2 == 1) return psm.negate (J);					// J#-n = -1^n * Jn
		return J;
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
		Number n;
		ExpressionSpaceManager<T> sm = getExpressionManager (psm);
		if (Library.isInteger (n = sm.toNumber (p)))
		{
			return new BesselDescription<T> (sm.newScalar (n.intValue ()), OrderTypes.INT, "J", "n", sm)
			{
				Polynomial.PowerFunction<T> J = getJ (n.intValue (), termCount, psm);
				public T eval (T x) { return J.eval (x); }
			};
		}
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
			this.setBesselDescription ("J", "p", OrderTypes.NON_SPECIFIC);
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
		protected ExtendedPowerLibrary<T> lib;
	
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
		return getJ (parameter, terms, lib, psm);
	}


	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getJ (T p, int termCount, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm)
	{
		ExpressionSpaceManager<T> sm = getExpressionManager (psm);
		if (Library.isInteger (sm.toNumber (p))) return getJ (p, termCount, psm);
		return new JpExtendedFunction<T>(p, termCount, psm, lib);
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
		return getJ (parameter, terms, parameters, sm);
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
		getJ (T a, int termCount, Map<String,Object> parameters, ExpressionSpaceManager<T> sm)
	{
		return new JaFunctionDescription<T> (a, termCount, parameters, sm);
	}


}


/**
 * Function Description for special case of Ja implementation.
 * @param <T> data type in use
 */
class JaFunctionDescription<T> extends BesselDescription<T>
{

	JaFunctionDescription (T a, int termCount, Map<String,Object> parameters, ExpressionSpaceManager<T> sm)
	{
		super (a, OrderTypes.NON_SPECIFIC, "J", "a", sm);
		this.J = new Ja (sm.convertToDouble (a), termCount, parameters);
		this.parameters = parameters;
	}
	protected Map<String,Object> parameters;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.RealDomainImplementation#evalReal(double)
	 */
	public double evalReal (double parameter) { return J.eval (parameter); }
	protected Ja J;

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.bessel.BesselDescription#getElaboration()
	 */
	public String getElaboration () { return "   " + parameters.toString (); }

}


/**
 * provide real version of Ja using integral algorithm.
 * TanhSinh Quadrature is used to provide numerical integration
 */
class Ja extends BesselCommonSectionedAlgorithm
{

	Ja (double a, int infinity, Map<String,Object> parameters)
	{
		super
		(
			new JalphaPart1Integrand (a),
			new JalphaPart2Integrand (a),
			parameters, infinity
		);
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
class JalphaPart1Integrand extends BesselSectionedAlgorithm.BesselSectionIntegrand
{
	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public Double eval (Double t)
	{ return Math.cos ( a * t - x * Math.sin (t) ); }
	JalphaPart1Integrand (double a) { super (a); }
}


/**
 * part2 integral form of Ja
 */
class JalphaPart2Integrand extends BesselSectionedAlgorithm.BesselSectionIntegrand
{
	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public Double eval (Double t)
	{ return Math.exp ( - x * Math.sinh (t) - a * t ); }
	JalphaPart2Integrand (double a) { super (a); }
}

