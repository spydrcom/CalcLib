
package net.myorb.math.specialfunctions;

import net.myorb.math.computational.Parameterization;
import net.myorb.math.computational.integration.QuadratureEntities;

import net.myorb.math.expressions.symbols.CommonRealDomainSubset;
import net.myorb.spline.algorithms.SimpleSplineQuadrature;

import net.myorb.math.complexnumbers.ComplexSpaceCore;
import net.myorb.math.complexnumbers.ComplexValue;

import java.util.Map;

/**
 * description of the Lerch transcendent (PHI) function
 *   source: https://en.wikipedia.org/wiki/Lerch_zeta_function
 * - It is named after Czech mathematician Mathias Lerch,
 * - who published a paper about the function in 1887
 * @author Michael Druckman
 */
public class Lerch <T> extends CommonRealDomainSubset <T>
{


	/*
	 * PHI(z,s,a) PSI(n,alpha)
	 * Li(s,a) BETA(s) CHI(s,z) Ti(s,z)
	 * ETA(s) ZETA(s) ZETA(s,alpha) L(lambda,s,alpha)
	 */
	public enum TargetFunctions { PHI, PSI, Li, BETA, CHI, Ti, ETA, ZETA, HURWITZ, L }


	/**
	 * definition of the Lerch Transcendent (PHI) function
	 */
	public interface Transcendent
	{
		/**
		 * Lerch transcendent (PHI) function
		 * @param z parameter to the function call
		 * @param s the order of the described function
		 * @param alpha offset term for series denominator
		 * @return the computed function result
		 */
		ComplexValue <Double> PHI
		(
			ComplexValue <Double> z,
			ComplexValue <Double> s,
			ComplexValue <Double> alpha
		);
	}


	/**
	 * implementation of Transcendent.PHI using series
	 */
	public static class Series implements Transcendent
	{
		public Series
			(
				ComplexValue <Double> tolerance,
				ComplexValue <Double> termMaximum
			)
		{
			this.tolerance = tolerance; this.terms = termMaximum;
		}
		public ComplexValue <Double> PHI
			(
				ComplexValue <Double> z,
				ComplexValue <Double> s,
				ComplexValue <Double> alpha
			)
		{
			return Lerch.PHI (z, s, alpha, tolerance, terms);
		}
		protected ComplexValue <Double> tolerance, terms;
	}


	/**
	 * Lerch transcendent (PHI) function
	 * - limited form of the infinite series
	 * @param z parameter to the function call
	 * @param s the order of the described function
	 * @param alpha offset term for series denominator
	 * @param tolerance the shortcut point of the terms
	 * @param terms number of terms to apply to the series
	 * @return the computed function result
	 */
	public static ComplexValue <Double> PHI
		(
			ComplexValue <Double> z,
			ComplexValue <Double> s,
			ComplexValue <Double> alpha,
			ComplexValue <Double> tolerance,
			ComplexValue <Double> terms
		)
	{
		boolean ignoreTolerance = tolerance.Re () <= 0;
		ComplexValue <Double> sum = LerchCalculator.S (0), term;

		for (int n = 0; n <= terms.Re (); n++)
		{
			sum = sum.plus
				(
					term = LerchCalculator.nTHterm (z, s, alpha, n)
				);
			if ( ignoreTolerance ) continue;
			if ( LerchCalculator.withinTolerance (term, tolerance) )
			{ return sum; }
		}

		return sum;
	}


	/*
	 * PHI (z,s,a) = 1/GAMMA (s) * INTEGRAL [0..INFINITY]
	 * 		( t^(s-1) / [(1 - z*exp(-t)) * exp(a*t)] * <*> t )
	 */

	/**
	 * compute a value of the PHI integrand
	 * @param z parameter to the function call
	 * @param s the order of the described function
	 * @param a offset term for series denominator
	 * @param t the integration variable
	 * @return the computed value
	 */
	public static ComplexValue <Double> integrand
		(
			ComplexValue <Double> z,
			ComplexValue <Double> s,
			ComplexValue <Double> a,
			ComplexValue <Double> t
		)
	{
		return LerchCalculator.TtoSminus1 (s, t)
			.divideBy (LerchCalculator.expProduct (z, a, t));
	}


	/**
	 * treat the integrand as a function for quadrature
	 * @param z parameter to the instance of the function call
	 * @param s the order of the described function
	 * @param a offset term for series denominator
	 * @return the integrand as function of t
	 */
	public static QuadratureEntities.TargetSpecification < ComplexValue <Double> >
		getIntegrand (ComplexValue <Double> z, ComplexValue <Double> s, ComplexValue <Double> a)
	{ return (t) -> integrand (z, s, a, t); }


	/**
	 * instance the QuadratureImplementation
	 * @param P the Parameterization to use for configuration of the implementation
	 * @return access to the PHI function
	 */
	public static Lerch.Transcendent
		quadratureImplementation (Parameterization P)
	{ return new LerchCalculator.QuadratureImplementation (P); }


	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.CommonFunctionBase#addConfiguration(java.util.Map)
	 */
	public void addConfiguration (Map <String, Object> parameters)
	{
		this.setDefiningOccurrence (new LerchTranscendent ());
		super.addConfiguration (parameters);
	}


	/**
	 * identify requested target function from configured parameters
	 * @param parameters the map of configured parameters
	 * @return the requested target function
	 */
	public static TargetFunctions identifyTargetFrom (Map <String, Object> parameters)
	{
		return identifyTargetCalled (parameters.get ("target"));
	}

	
	/**
	 * identify requested target function from name
	 * @param name the text of the function name or NULL for default
	 * @return the selected target
	 */
	public static TargetFunctions identifyTargetCalled (Object name)
	{ return name == null ? DEFAULT_TARGET : TargetFunctions.valueOf (name.toString ()); }
	public static TargetFunctions DEFAULT_TARGET = TargetFunctions.PHI;


}


/**
 * complex formula for Lerch Transcendent PHI function
 */
class LerchTranscendent extends CommonRealDomainSubset.ComplexDefinition
{

	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.CommonFunctionBase#addConfiguration(java.util.Map)
	 */
	public void addConfiguration (Map <String, Object> parameters)
	{
		super.addConfiguration (parameters);

		this.getFunctionConfigurationValues (parameters);

		this.setimplementation
		(
			LerchCalculator.targetFunctionIdentity (parameters),
			Lerch.identifyTargetFrom (parameters)
		);
	}

	/**
	 * identify the function being configured
	 * @param ID the map of Lerch function identity formulae
	 * @param function the named target function
	 */
	public void setimplementation (LerchIdentities ID, Lerch.TargetFunctions function)
	{
		switch (function)
		{
			case BETA:
				this.setimplementation ( (s) -> ID.beta (s) );
				break;
			case CHI:
				this.setimplementation ( (z) -> ID.chi (cache.getOrder (), z) );
				break;
			case ETA:
				this.setimplementation ( (s) -> ID.eta (s) );
				break;
			case HURWITZ:
				this.setimplementation ( (s) -> ID.zeta (s, cache.getAlpha ()) );
				break;
			case L:
				this.setimplementation ( (lambda) -> ID.L (lambda, cache.getOrder (), cache.getAlpha ()) );
				break;
			case Li:
				this.setimplementation ( (z) -> ID.Li (cache.getOrder (), z) );
				break;
			case PHI:
				this.setimplementation ( (z) -> ID.phi (z, cache.getOrder (), cache.getAlpha ()) );
				break;
			case PSI:
				this.setimplementation ( (s) -> ID.psi (cache.getOrder (), cache.getAlpha ()) );
				break;
			case Ti:
				this.setimplementation ( (z) -> ID.Ti (cache.getOrder (), z) );
				break;
			case ZETA:
				this.setimplementation ( (s) -> ID.zeta (s) );
				break;
		}
	}

	/**
	 * configure ORDER and ALPHA for use in formulae
	 * @param parameters the map of configured parameters
	 */
	public void getFunctionConfigurationValues (Map <String, Object> parameters)
	{ this.cache = new ParameterCache (parameters); }
	protected ParameterCache cache;

	/**
	 * complex definition for Lerch Transcendent PHI
	 */
	LerchTranscendent () { super ("PHI"); }

}


/**
 * cache for values parsed from configuration source
 */
class ParameterCache
{

	ParameterCache (Map <String, Object> parameters)
	{
		this.alpha = parameters.get ("alpha");
		this.order = parameters.get ("order");
	}
	Object order = null, alpha = null;

	/*
	 * cache-on-demand values of configuration items specified in parameters
	 */

	ComplexValue <Double> getOrder ()
	{
		return orderValue == null
			?  orderValue = LerchCalculator.get (order, "Order")
			:  orderValue;
	}
	ComplexValue <Double> orderValue = null;

	ComplexValue <Double> getAlpha ()
	{
		return alphaValue == null
			?  alphaValue = LerchCalculator.get (alpha, "Alpha")
			:  alphaValue;
	}
	ComplexValue <Double> alphaValue = null;

}


/**
 * calculations of Lerch formulae in complex space
 */
class LerchCalculator extends ComplexSpaceCore
{
	

	/*
	 * PHI (z,s,a) = SIGMA [ 0 <= n <= INFINITY ] ( z^n / ( n + a ) ^ s )
	 */

	/**
	 * computation of one term
	 * @param z parameter to the function call
	 * @param s the order of the described function
	 * @param a offset term for series denominator
	 * @param n the index of the term
	 * @return the computed value
	 */
	public static ComplexValue <Double> nTHterm
		(
			ComplexValue <Double> z,
			ComplexValue <Double> s,
			ComplexValue <Double> a,
			int n
		)
	{
		return POW (z, n).times (toThe (a.plus (S (n)), NEG (s)));
	}


	/**
	 * compare a computed term against tolerance
	 * - determination of short-circuit evaluation
	 * @param term the computed value of the formula term
	 * @param tolerance the requested tolerance to be used
	 * @return TRUE when tolerance criteria is met
	 */
	public static boolean withinTolerance
	(ComplexValue <Double> term, ComplexValue <Double> tolerance)
	{ return manager.lessThan (term, tolerance); }


	/**
	 * exponential factor in numerator of integrand
	 * @param s the order of the described function
	 * @param t the integration variable
	 * @return T to (s - 1)
	 */
	public static ComplexValue <Double> TtoSminus1
	(ComplexValue <Double> s, ComplexValue <Double> t)
	{
		return toThe (t, reduce (s, ONE));
	}


	/**
	 * exponential factors in denominator of integrand
	 * @param z parameter to the function call
	 * @param a offset term for series denominator
	 * @param t the integration variable
	 * @return computed product
	 */
	public static ComplexValue <Double> expProduct
	(ComplexValue <Double> z, ComplexValue <Double> a, ComplexValue <Double> t)
	{
		return productOf
			(
				reduce
				(
					ONE,
					productOf
					(
						z, exp (NEG (t))
					)
				),
				exp
				(
					productOf (a, t)
				)
			);
	}
	public static ComplexValue <Double> ONE = S (1);


	/**
	 * Lerch.Transcendent based on quadrature applied to the integral formula
	 */
	public static class QuadratureImplementation
		extends SimpleSplineQuadrature < ComplexValue <Double> >
		implements Lerch.Transcendent
	{

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.Lerch.Transcendent#PHI(net.myorb.math.complexnumbers.ComplexValue, net.myorb.math.complexnumbers.ComplexValue, net.myorb.math.complexnumbers.ComplexValue)
		 */
		public ComplexValue <Double>
				PHI (ComplexValue <Double> z, ComplexValue <Double> s, ComplexValue <Double> alpha)
		{ return integralOf ( (t) -> Lerch.integrand (z, s, alpha, t) ).divideBy (GAMMA (s)); }

		public QuadratureImplementation (Parameterization P)
		{ super (P, ComplexSpaceCore.manager); }

	}


	/**
	 * determine parameter value
	 * @param item the specified parameter
	 * @param name the name of the value being sought
	 * @return the parsed complex parameter value or real treated with imaginary part being zero
	 * @throws RuntimeException for null or erroneous items
	 */
	public static ComplexValue <Double> get (Object item, String name) throws RuntimeException
	{
		if (item == null) { return error (name, REQUIRED); }
		try { return CV (item); } catch (Exception e) { return error (name, UNRECOGNIZED); }
	}
	static ComplexValue <Double> error (String name, String msg) { throw new RuntimeException (name + msg); }
	static String UNRECOGNIZED = " is not recognized", REQUIRED = " is required";


	/**
	 * parse a complex value or zero if parameter is null
	 * @param parameter the parameter specified in configuration
	 * @return the parsed parameter value
	 */
	public static ComplexValue <Double> CV (Object parameter)
	{
		return parameter == null ? S (0) : parseComplex (parameter.toString ());
	}


	/**
	 * get identity mapping object based on configured parameters
	 * @param parameters the map of configured parameters
	 * @return an identity mapping object
	 */
	public static LerchIdentities targetFunctionIdentity (Map <String, Object> parameters)
	{
		return new LerchIdentities
			(
				parameters.get ("series") != null ?
					new Lerch.Series
						(
							CV (parameters.get ("tolerance")),
							configuredSeriesLength (parameters.get ("terms"))
						)
				: Lerch.quadratureImplementation (new Parameterization (parameters))
			);
	}
	public static ComplexValue <Double> configuredSeriesLength (Object terms)
	{ return terms == null ? RE (DEFAULT_SERIES_LENGTH) : CV (terms); }
	public static int DEFAULT_SERIES_LENGTH = 100;


}

