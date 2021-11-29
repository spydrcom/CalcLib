
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.expressions.symbols.AbstractParameterizedFunction;
import net.myorb.math.expressions.symbols.AbstractUnaryOperator;
import net.myorb.math.expressions.ValueManager;

/**
 * primitives from CalcLib library available to configuration reference
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public abstract class CLmathPrimitives<T> extends CommonOperatorLibrary<T>
{


	/*
	 * 		ZETA
	 */

	/**
	 * implement function - ZETA (with analytic continuation)
	 * @param symbol the symbol associated with function
	 * @param precedence the associated precedence
	 * @return function implementation object
	 */
	public AbstractUnaryOperator getZetaAnalyticAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				return zetaAnalytic (parameter);
			}
			public void addParameterization (String options)
			{ initZetaAnalytic (options); }
		};
	}
	public abstract void initZetaAnalytic (String parameter);

	/**
	 * zeta function implementation (with analytic continuation)
	 * @param values the array of values presented
	 * @return the computed result
	 */
	public ValueManager.GenericValue zetaAnalytic
		(
			ValueManager.GenericValue values
		)
	{
		T parameter = valueManager.toDiscrete (values);

		return valueManager.newDiscreteValue
			(
				zeta (parameter)
			);
	}

	/**
	 * implement function - ZETA
	 * @param symbol the symbol associated with function
	 * @param precedence the associated precedence
	 * @return function implementation object
	 */
	public AbstractUnaryOperator getZetaAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				return zeta (parameter);
			}
			public void addParameterization (String options)
			{ initZeta (options); }
		};
	}
	public abstract void initZeta (String parameter);

	/**
	 * zeta function implementation
	 * @param values the array of values presented
	 * @return the computed result
	 */
	public ValueManager.GenericValue zeta
		(
			ValueManager.GenericValue values
		)
	{
		T parameter = valueManager.toDiscrete (values);

		return valueManager.newDiscreteValue
			(
				zeta (parameter)
			);
	}
	public abstract T zeta (T parameter);


	/*
	 * 		EXP
	 */

	/**
	 * implement function - EXP
	 * @param symbol the symbol associated with function
	 * @return function implementation object
	 */
	public AbstractParameterizedFunction getExpAlgorithm (String symbol)
	{
		return new EnhancedParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue parameters) { return wrapper.eval (parameters); }
			public String markupForDisplay (String operator, String operand, NodeFormatting using)
			{ return using.formatSuperScript (expRender (using), operand); }
			public void addParameterization (String options) {}
			CommonWrapper wrapper = getExpWrapper ();
		};
	}
	public CommonWrapper getExpWrapper () { throw new RuntimeException ("Exp has no implementation"); }
	String expRender (NodeFormatting using) { return using.formatIdentifierReference (expLookup ()); }
	String expLookup () { return lookupIdentifierFor ("epsilon"); }


	/*
	 * 		GAMMA
	 */

	/**
	 * implement function - GAMMA
	 * @param symbol the symbol associated with function
	 * @return function implementation object
	 */
	public AbstractParameterizedFunction getGammaAlgorithm (String symbol)
	{
		return new EnhancedParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue parameters) { return Wrapper.eval (parameters); }
			public String markupForDisplay (String operator, String operand, NodeFormatting using)
			{ return gammaRender (using) + using.formatParenthetical (operand); }
			public void addParameterization (String options) { initGamma (options); }
			CommonWrapper Wrapper = getGammaWrapper ();
		};
	}
	public CommonWrapper getGammaWrapper () { throw new RuntimeException ("Gamma has no implementation"); }
	String gammaRender (NodeFormatting using) { return formatIdentifierFor ("GAMMA", using); }
	public abstract void initGamma (String parameter);


	/**
	 * implement function - LOGGAMMA
	 * @param symbol the symbol associated with function
	 * @return function implementation object
	 */
	public AbstractParameterizedFunction getLoggammaAlgorithm (String symbol)
	{
		return new EnhancedParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue parameters) { return wrapper.eval (parameters); }
			public String markupForDisplay (String operator, String operand, NodeFormatting using)
			{ return loggammaRender (using) + using.formatParenthetical (operand); }
			public void addParameterization (String options) { initGamma (options); }
			CommonWrapper wrapper = getLoggammaWrapper ();
		};
	}
	public CommonWrapper getLoggammaWrapper () { throw new RuntimeException ("Loggamma has no implementation"); }
	String loggammaRender (NodeFormatting using) { return using.formatIdentifierReference ("Ln" + gammaLookup ()); }
	String gammaLookup () { return lookupIdentifierFor ("GAMMA"); }

	/**
	 * implement function - Incomplete GAMMA
	 * @param symbol the symbol associated with function
	 * @return function implementation object
	 */
	public AbstractParameterizedFunction getIncompleteGammaAlgorithm (String symbol)
	{
		initGammaInc ();
		return new AbstractParameterizedFunction (symbol)
		{
			CommonWrapper wrapper = getIncompleteGammaWrapper ();
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return wrapper.evalForDual (parameters); }
		};
	}
	public CommonWrapper getIncompleteGammaWrapper ()
	{ throw new RuntimeException ("Euler Product has no implementation"); }
	public abstract void initGammaInc ();


	/*
	 * 		Euler Product
	 */

	/**
	 * implement function - Euler Product
	 * @param symbol the symbol associated with function
	 * @param precedence the associated precedence
	 * @return function implementation object
	 */
	public AbstractUnaryOperator getEulerProductAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue parameter) { return wrapper.eval (parameter); }
			CommonWrapper wrapper = getEulerProductWrapper ();
			public void addParameterization (String options)
			{ initEulerProduct (options); }
		};
	}
	public CommonWrapper getEulerProductWrapper ()
	{ throw new RuntimeException ("Euler Product has no implementation"); }
	public abstract void initEulerProduct (String parameter);


	/*
	 * 		Exponential Integral
	 */

	/**
	 * implement function - Ei
	 * @param symbol the symbol associated with function
	 * @param precedence the associated precedence
	 * @return function implementation object
	 */
	public AbstractUnaryOperator getExponentialIntegralAlgorithm (String symbol, int precedence)
	{ return new MarshalingWrapper (symbol, precedence, getEiImplementation ()); }
	public CommonOperatorImplementation getEiImplementation ()
	{ return new Unimplemented ("Exponential Integral"); }

	/**
	 * implement function - Li
	 * @param symbol the symbol associated with function
	 * @param precedence the associated precedence
	 * @return function implementation object
	 */
	public AbstractUnaryOperator getLogarithmicIntegralAlgorithm (String symbol, int precedence)
	{ return new MarshalingWrapper (symbol, precedence, getLiImplementation ()); }
	public CommonOperatorImplementation getLiImplementation ()
	{ return new Unimplemented ("Logarithmic Integral"); }

	/**
	 * implement function - E1
	 * @param symbol the symbol associated with function
	 * @param precedence the associated precedence
	 * @return function implementation object
	 */
	public AbstractUnaryOperator getE1IntegralAlgorithm (String symbol, int precedence)
	{ return new MarshalingWrapper (symbol, precedence, getE1Implementation ()); }
	public CommonOperatorImplementation getE1Implementation ()
	{ return new Unimplemented ("E1 Integral"); }


	/*
	 * 		Airy functions
	 */

	/**
	 * implement function - Ai
	 * @param symbol the symbol associated with function
	 * @return function implementation object
	 */
	public AbstractParameterizedFunction getAiryAiAlgorithm (String symbol)
	{ return new MultipleMarshalingWrapper (symbol, getAiImplementation ()); }
	public CommonFunctionImplementation getAiImplementation ()
	{ return new Missing ("Airy Ai"); }

	/**
	 * implement function - Bi
	 * @param symbol the symbol associated with function
	 * @return function implementation object
	 */
	public AbstractParameterizedFunction getAiryBiAlgorithm (String symbol)
	{ return new MultipleMarshalingWrapper (symbol, getBiImplementation ()); }
	public CommonFunctionImplementation getBiImplementation ()
	{ return new Missing ("Airy Bi"); }


	/*
	 * 		Bernoulli
	 */

	/**
	 * implement function - Bernoulli Number
	 * @param symbol the symbol associated with function
	 * @param precedence the associated precedence
	 * @return function implementation object
	 */
	public AbstractUnaryOperator getBernoulliNumberAlgorithm (String symbol, int precedence)
	{ return new MarshalingWrapper (symbol, precedence, getBernoulliBnImplementation ()); }
	public CommonOperatorImplementation getBernoulliBnImplementation ()
	{ return new Unimplemented ("Bernoulli Bn"); }

	/**
	 * implement function - Bernoulli Polynomial
	 * @param symbol the symbol associated with function
	 * @return function implementation object
	 */
	public AbstractParameterizedFunction getBernoulliPolynomialAlgorithm (String symbol)
	{ return new MultipleMarshalingWrapper (symbol, getBernoulliPolynomialImplementation ()); }
	public CommonFunctionImplementation getBernoulliPolynomialImplementation ()
	{ return new Missing ("Bernoulli Polynomial"); }


	/*
	 * 		Bessel
	 */

	/**
	 * implement function - Bessel functions
	 * @param symbol the symbol associated with function
	 * @param precedence the associated precedence
	 * @return function implementation object
	 */
	public AbstractUnaryOperator getBesselAlgorithm (String symbol, int precedence)
	{ return new MarshalingWrapper (symbol, precedence, getBesselImplementation ()); }
	public CommonOperatorImplementation getBesselImplementation ()
	{ return new Unimplemented ("Bessel"); }

	/**
	 * implement function - Bessel Function
	 * @param symbol the symbol associated with function
	 * @return function implementation object
	 */
	public AbstractParameterizedFunction getBesFunAlgorithm (String symbol)
	{ return new MultipleMarshalingWrapper (symbol, getBesFunImplementation ()); }
	public CommonFunctionImplementation getBesFunImplementation ()
	{ return new Missing ("Bessel Function"); }

}

