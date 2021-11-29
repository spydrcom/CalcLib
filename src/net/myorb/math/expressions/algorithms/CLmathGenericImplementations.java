
package net.myorb.math.expressions.algorithms;

import net.myorb.math.specialfunctions.GenericIncompleteGamma;
import net.myorb.math.specialfunctions.BesselFunctions;
import net.myorb.math.specialfunctions.EulerProduct;
import net.myorb.math.specialfunctions.Zeta;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.rendering.MathMarkupNodes;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.data.abstractions.SpaceConversion;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

import java.util.List;

/**
 * library interface implementation for generic classes
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public abstract class CLmathGenericImplementations<T>
	extends CLmathPrimitives<T>
{


	protected CLmathGenericImplementations
		(
			SpaceManager<T> manager,
			SpaceConversion<T> conversion,
			ExtendedPowerLibrary<T> library,
			Environment<T> environment
		)
	{
		this.manager = manager;
		this.conversion = conversion;
		this.environment = environment;
		this.library = library;
	}
	SpaceManager<T> manager;
	SpaceConversion<T> conversion;
	ExtendedPowerLibrary<T> library;
	Environment<T> environment;


	/*
	 * 		Euler Product
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#getEulerProductWrapper()
	 */
	public CommonWrapper getEulerProductWrapper ()
	{
		return new CommonWrapper
		(
			new CommonFunction ()
			{
				public T eval (T x) { return eulerProduct.eval (x); }
			}
		);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#initEulerProduct(java.lang.String)
	 */
	public void initEulerProduct (String parameter)
	{
		eulerProduct = new EulerProduct<T> (manager, library, conversion);
		eulerProduct.configure (Integer.parseInt (parameter));
	}
	protected EulerProduct<T> eulerProduct = null;


	/*
	 * 		ZETA
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#zeta(java.lang.Object)
	 */
	public T zeta (T parameter)
	{
		return zeta.eval (parameter);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#initZeta(java.lang.String)
	 */
	public void initZeta (String parameter)
	{
		zeta = new Zeta<T> (manager, library, conversion);
		zeta.configure (Integer.parseInt (parameter));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#initZetaAnalytic(java.lang.String)
	 */
	public void initZetaAnalytic (String parameter)
	{
		throw new RuntimeException ("Unimplemented function: Zeta");
	}
	protected Zeta<T> zeta = null;


	/*
	 * 		GAMMA
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#getGammaWrapper()
	 */
	public CommonWrapper getGammaWrapper ()
	{
		return new CommonWrapper
		(
			new CommonFunction () { public T eval (T z) { return GAMMA.eval (z); } }
		);
	}
	protected Function<T> GAMMA = null;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#getIncompleteGammaWrapper()
	 */
	public CommonWrapper getIncompleteGammaWrapper ()
	{
		return new CommonWrapper
		(
			new CommonFunction ()
			{
				public T eval (T z, T x) { return incompleteGamma.lower (z, x); }
			}
		);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#initGammaInc()
	 */
	public void initGammaInc ()
	{
		incompleteGamma = new GenericIncompleteGamma<T> (GAMMA, library);
	}
	protected GenericIncompleteGamma<T> incompleteGamma = null;


	/*
	 * 		Bernoulli
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#B(java.lang.Object)
	 */
	public T B (T x)
	{
		throw new RuntimeException ("Unimplemented function: Bernoulli Number");
	}
	public T BP (T n, T x)
	{
		throw new RuntimeException ("Unimplemented function: Bernoulli Polynomial");
	}


	/*
	 * 		Bessel
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#getBesselImplementation()
	 */
	public CommonOperatorImplementation getBesselImplementation ()
	{
		return new CommonOperatorImplementation ()
		{

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.algorithms.CommonOperatorLibrary.CommonOperatorImplementation#configure(java.lang.String)
			 */
			public void configure (String parameters)
			{ bessel = configureBesselWrapper (parameters); }

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.algorithms.CommonOperatorLibrary.CommonOperatorImplementation#evaluate(java.lang.Object)
			 */
			public T evaluate (T using) { return bessel.function.eval (using); }
			protected BesselWrapper<T> bessel;

		};
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#getBesFunImplementation()
	 */
	public CommonFunctionImplementation getBesFunImplementation ()
	{
		return new CommonFunctionImplementation ()
		{

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.algorithms.CommonOperatorLibrary.CommonFunctionImplementation#evaluate(java.util.List)
			 */
			public T evaluate (List<T> using)
			{ return bessel.function.eval (using.get (0)); }

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.algorithms.CommonOperatorLibrary.CommonFunctionImplementation#markupForDisplay(java.lang.String, java.lang.String, net.myorb.math.expressions.gui.rendering.NodeFormatting)
			 */
			public String markupForDisplay
			(String operator, String parameters, NodeFormatting using)
			{ return bessel.render (using) + using.formatParenthetical (parameters); }

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.algorithms.CommonOperatorLibrary.CommonFunctionImplementation#markupForDisplay(java.lang.String, java.lang.String, boolean, net.myorb.math.expressions.gui.rendering.NodeFormatting)
			 */
			public String markupForDisplay
			(String operator, String operand, boolean fenceOperand, NodeFormatting using)
			{ return markupForDisplay (operator, operand, using); }

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.algorithms.CommonOperatorLibrary.CommonFunctionImplementation#configure(java.lang.String)
			 */
			public void configure (String parameters)
			{ bessel = configureBesselWrapper (parameters); }
			protected BesselWrapper<T> bessel;

		};
	}

	/**
	 * allocate wrapper object for function
	 * @param parameters the parameter text supplied in configuration
	 * @return a configured function wrapper
	 */
	public BesselWrapper<T> configureBesselWrapper (String parameters)
	{
		BesselWrapper<T> bessel =
			new BesselWrapper<T> (parameters, environment);
		bessel.buildFunction (manager, library);
		return bessel;
	}


}


/**
 * localize Bessel function processing as operator and as function syntax
 */
class BesselWrapper<T>
{

	BesselWrapper (String parameters, Environment<T> environment)
	{
		// family Bessel 2 Jp,2.5
		String[] fields = parameters.split (";");
		parseParameters (fields[1], environment);
		this.kind = fields[0];
	}
	String kind;

	String render (NodeFormatting using)
	{
		try
		{
			String sp = MathMarkupNodes.space ("5");
			String id = using.formatIdentifierReference (kind);
			return using.formatSubScript (id, pManager.render ()) + sp;
		}
		catch (Exception e) { return ""; }
	}

	void parseParameters (String configuration, Environment<T> environment)
	{
		pManager = new ParameterManager<T> (environment);
		pManager.setExpression (configuration);
	}
	ParameterManager<T> pManager;

	void buildFunction (SpaceManager<T> manager, ExtendedPowerLibrary<T> library)
	{
		BesselFunctions<T> functions;
		(functions = new BesselFunctions<T> ()).init (manager);
		function = functions.getFunction (kind, pManager.eval (), library);
	}
	Function<T> function;

}

