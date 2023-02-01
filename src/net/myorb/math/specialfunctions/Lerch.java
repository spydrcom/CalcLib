
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

		Object parm;
		Lerch.TargetFunctions
			target = Lerch.TargetFunctions.PHI;
		if ( (parm = parameters.get ("target")) != null)
		{ target = Lerch.TargetFunctions.valueOf (parm.toString ()); }

		if ( (parm = parameters.get ("terms")) != null) { termCount = CV (parm); }
		alpha = parameters.get ("alpha"); order = parameters.get ("order");
		tolerance = parameters.get ("tolerance"); 

		Lerch.Transcendent transcendent =
				parameters.get ("series") != null ?
				new Lerch.Series (CV (tolerance), termCount) :
				Lerch.quadratureImplementation (new Parameterization (parameters));
		LerchIdentities ID = new LerchIdentities (transcendent);

		switch (target)
		{
			case BETA:
				this.setimplementation ( (s) -> ID.beta (s) );
				break;
			case CHI:
				this.setimplementation ( (z) -> ID.chi (getOrder (), z) );
				break;
			case ETA:
				this.setimplementation ( (s) -> ID.eta (s) );
				break;
			case HURWITZ:
				this.setimplementation ( (s) -> ID.zeta (s, getAlpha ()) );
				break;
			case L:
				this.setimplementation ( (lambda) -> ID.L (lambda, getOrder (), getAlpha ()) );
				break;
			case Li:
				this.setimplementation ( (z) -> ID.Li (getOrder (), z) );
				break;
			case PHI:
				this.setimplementation ( (z) -> ID.phi (z, getOrder (), getAlpha ()) );
				break;
			case PSI:
				this.setimplementation ( (s) -> ID.psi (getOrder (), getAlpha ()) );
				break;
			case Ti:
				this.setimplementation ( (z) -> ID.Ti (getOrder (), z) );
				break;
			case ZETA:
				this.setimplementation ( (s) -> ID.zeta (s) );
				break;
		}
	}
	ComplexValue <Double> getOrder ()
	{
		if (order == null)
		{ throw new RuntimeException ("Order is required"); }
		return CV (order.toString ());
	}
	ComplexValue <Double> getAlpha ()
	{
		if (alpha == null)
		{ throw new RuntimeException ("Alpha is required"); }
		return CV (alpha.toString ());
	}
	ComplexValue <Double> CV (Object parameter)
	{
		if (parameter == null) return ComplexSpaceCore.S (0);
		return ComplexSpaceCore.parseComplex (parameter.toString ());
	}

	LerchTranscendent () { super ("PHI"); }
	ComplexValue <Double> termCount = ComplexSpaceCore.RE (100);
	Object order = null, alpha = null, tolerance = null;

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
	static boolean withinTolerance
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


}

