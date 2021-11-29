
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

import net.myorb.math.specialfunctions.ExponentialIntegral;
import net.myorb.math.specialfunctions.ZetaRealAnalytic;
import net.myorb.math.specialfunctions.AiryFunctions;
import net.myorb.math.specialfunctions.Bernoulli;
import net.myorb.math.specialfunctions.Gamma;

import net.myorb.math.ExtendedPowerLibrary;

import java.util.List;

/**
 * library interface implementation for real domain classes
 * @author Michael Druckman
 */
public class CLmathRealImplementations
	extends CLmathGenericImplementations<Double>
{


	static JrePowerLibrary jrePowerLibrary = new JrePowerLibrary ();
	static ExpressionFloatingFieldManager realMgr = new ExpressionFloatingFieldManager ();
	static ExtendedPowerLibrary<Double> library = jrePowerLibrary;


	public CLmathRealImplementations (Environment<Double> environment)
	{
		super (realMgr, realMgr, library, environment);
	}


	/*
	 * 		Zeta (with Analytic Continuation)
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathGenericImplementations#initZetaAnalytic(java.lang.String)
	 */
	public void initZetaAnalytic (String parameter)
	{
		zeta = new ZetaRealAnalytic (realMgr, library, realMgr);
		zeta.configure (Integer.parseInt (parameter));
	}

	/*
	 * 		EXP
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#getExpWrapper()
	 */
	public CommonWrapper getExpWrapper ()
	{
		return new CommonWrapper
		(
			new CommonFunction ()
			{
				public Double eval (Double x) { return Math.exp (x); }
			}
		);
	}

	/*
	 * 		GAMMA
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#initGamma(java.lang.String)
	 */
	public void initGamma (String parameter)
	{
		GAMMA = new Gamma ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#getLoggammaWrapper()
	 */
	public CommonWrapper getLoggammaWrapper ()
	{
		return new CommonWrapper
		(
			new CommonFunction ()
			{
				public Double eval (Double x)
				{
					return jrePowerLibrary.ln (GAMMA.eval (x));
				}
			}
		);
	}


	/*
	 * 		Exponential Integral
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#getEiImplementation()
	 */
	public CommonOperatorImplementation getEiImplementation ()
	{
		return new CommonOperatorImplementation ()
		{
			public Double evaluate (Double using) { return ExponentialIntegral.Ei (using); }
		};
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#getLiImplementation()
	 */
	public CommonOperatorImplementation getLiImplementation ()
	{
		return new CommonOperatorImplementation ()
		{
			public Double evaluate (Double using) { return ExponentialIntegral.li (using); }
		};
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#getE1Implementation()
	 */
	public CommonOperatorImplementation getE1Implementation ()
	{
		return new CommonOperatorImplementation ()
		{
			public Double evaluate (Double using) { return ExponentialIntegral.E1 (using); }
		};
	}


	/*
	 * 		Airy
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#getAiImplementation()
	 */
	public CommonFunctionImplementation getAiImplementation ()
	{
		return new CommonFunctionImplementation ()
		{
			FunctionProfile profile =
				new FunctionProfile ("Ai", 1, 1);

			public Double evaluate (List<Double> using)
			{
				profile.parameterCheck (using.size ());
				return AiryFunctions.Ai (using.get (0));
			}
		};
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#getBiImplementation()
	 */
	public CommonFunctionImplementation getBiImplementation ()
	{
		return new CommonFunctionImplementation ()
		{
			FunctionProfile profile =
				new FunctionProfile ("Bi", 1, 1);

			public Double evaluate (List<Double> using)
			{
				profile.parameterCheck (using.size ());
				return AiryFunctions.Bi (using.get (0));
			}
		};
	}


	/*
	 * 		Bernoulli
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#getBernoulliBnImplementation()
	 */
	public CommonOperatorImplementation getBernoulliBnImplementation ()
	{
		return new CommonOperatorImplementation ()
		{
			public Double evaluate (Double using) { return Bernoulli.number (using.intValue ()); }
		};
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#getBernoulliPolynomialImplementation()
	 */
	public CommonFunctionImplementation getBernoulliPolynomialImplementation ()
	{
		return new CommonFunctionImplementation ()
		{
			FunctionProfile profile =
				new FunctionProfile ("BernPoly", 2, 2);

			public Double evaluate (List<Double> using)
			{
				profile.parameterCheck (using.size ());
				Double p1 = using.get (0), p2 = using.get (1);
				return Bernoulli.evalPoly (p1.intValue (), p2);
			}
		};
	}


}

