
package net.myorb.math.expressions.algorithms;

import net.myorb.data.abstractions.SpaceConversion;
import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.Function;
import net.myorb.math.SpaceManager;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.rendering.MathMarkupNodes;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.specialfunctions.BesselFunctions;

import java.util.List;

public class ClMathBessel<T> extends CommonOperatorLibrary<T>
{

	protected ClMathBessel
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

	public CommonFunctionImplementation getConfigurableBesselImplementation ()
	{
		return new ConfigurableBesselImplementation ();
	}

	class ConfigurableBesselImplementation extends BesselImplementation
	{
		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.CommonOperatorLibrary.CommonFunctionImplementation#configure(java.lang.String)
		 */
		public void configure (String parameters)
		{ setParameterManager (configureBesselManager (parameters)); }
	}

	/**
	 * allocate wrapper object for function
	 * @param parameters the parameter text supplied in configuration
	 * @return a configured function wrapper
	 */
	public BesselParameterManager<T> configureBesselManager (String parameters)
	{
		BesselParameterManager<T> bessel =
			new BesselParameterManager<T> (parameters, environment);
		bessel.buildFunction (manager, library);
		return bessel;
	}

	/**
	 * Bessel as common function
	 */
	class BesselImplementation extends CommonFunctionImplementation
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

		/**
		 * @param bessel parameter processing object
		 */
		public void setParameterManager
		(BesselParameterManager<T> bessel) { this.bessel = bessel; }
		protected BesselParameterManager<T> bessel;

	}

}

/**
 * localize Bessel function processing as operator and as function syntax
 */
class BesselParameterManager<T>
{

	// family Bessel 2 Jp,2.5

	BesselParameterManager (String parameters, Environment<T> environment)
	{
		this (parameters.split (";"), environment);
	}
	BesselParameterManager (String[] parameters, Environment<T> environment)
	{
		this (parameters[0], parameters[1], environment);
	}
	BesselParameterManager (String kind, String alpha, Environment<T> environment)
	{
		parseParameters (alpha, environment);
		this.kind = kind;
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

