
package net.myorb.math.expressions.algorithms;

import net.myorb.math.specialfunctions.BesselFunctions;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.rendering.MathMarkupNodes;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;

import net.myorb.math.expressions.symbols.LibraryObject;
import net.myorb.math.expressions.SymbolMap.Named;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

import java.util.List;
import java.util.Map;

/**
 * manage parameterization of functions declared as Bessel
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class ClMathBessel<T> extends InstanciableFunctionLibrary<T>
{


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#getInstance(java.lang.String, net.myorb.math.expressions.symbols.LibraryObject)
	 */
	public Named getInstance (String sym, LibraryObject<T> lib)
	{
		return new BesselAbstraction (sym, lib);
	}


	/**
	 * Bessel function object base class
	 */
	public class BesselAbstraction extends MultipleMarshalingWrapper
	{

		BesselAbstraction (String sym, LibraryObject<T> lib)
		{
			this
			(
				sym, lib.getParameterization ()
			);
		}

		BesselAbstraction
		(String sym, Map<String,Object> parameterMap)
		{
			this
			(
				sym,
				configureBesselManager (parameterMap),
				new BesselImplementation ()
			);
			this.parameterMap = parameterMap;
		}
		Map<String,Object> parameterMap;

		BesselAbstraction
		(String sym, BesselParameterManager<T> manager, BesselImplementation impl)
		{
			super (sym, impl);
			impl.setParameterManager (manager);
			this.parameterManager = manager;
		}
		BesselParameterManager<T> parameterManager;

		/**
		 * @return configured alpha value
		 */
		public T getAlpha ()
		{
			return parameterManager.getAlpha ();
		}

	}


	/**
	 * @return allocate an object that manages configuration from start-up XML source
	 */
	public CommonFunctionImplementation getConfigurableBesselImplementation ()
	{
		return new ConfigurableBesselImplementation ();
	}


	/**
	 * an object that manages configuration from start-up XML source
	 */
	class ConfigurableBesselImplementation extends BesselImplementation
	{
		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.CommonOperatorLibrary.CommonFunctionImplementation#configure(java.lang.String)
		 */
		public void configure (String parameters)
		{ setParameterManager (configureBesselManager (parameters)); }
	}


	/**
	 * allocate manager object for function
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
	 * allocate manager object for function
	 * @param parameters the parameter map supplied in configuration
	 * @return a configured function wrapper
	 */
	public BesselParameterManager<T> configureBesselManager (Map<String,Object> parameters)
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
		 * connect parameter manager from configuration
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

	static final int DEFAULT_TERM_COUNT = 20;


	/**
	 * @param parameters map of configured parameters
	 * @param environment description of the session
	 */
	BesselParameterManager (Map<String,Object> parameters, Environment<T> environment)
	{
		this (parameters.get ("kind"), parameters.get ("alpha"), parameters.get ("terms"), environment);
	}
	BesselParameterManager (String parameters, Environment<T> environment)
	{
		this (parameters.split (";"), environment);
	}
	BesselParameterManager (String[] parameters, Environment<T> environment)
	{
		this (parameters[0], parameters[1], parameters.length>2? parameters[2]: null, environment);
	}
	BesselParameterManager (Object kind, Object alpha, Object terms, Environment<T> environment)
	{
		parseAlpha (alpha.toString (), environment);
		if (terms != null) this.terms = Integer.parseInt (terms.toString ());
		this.kind = kind.toString ();
	}
	String kind;


	/**
	 * get configured term count
	 * @return configured count of terms
	 */
	public int getTermCount () { return this.terms; }
	int terms = DEFAULT_TERM_COUNT;


	/**
	 * construct MML for function identifier
	 * @param using the node formatting tool
	 * @return the function reference MML
	 */
	String render (NodeFormatting using)
	{
		try
		{
			String sp = MathMarkupNodes.space ("5");
			String id = using.formatIdentifierReference (kind);
			return using.formatSubScript (id, alphaManager.render ()) + sp;
		}
		catch (Exception e) { return ""; }
	}


	/**
	 * parse the configuration source for the alpha value
	 * @param configuration the text of the configuration parameters
	 * @param environment description of the session
	 */
	void parseAlpha (String configuration, Environment<T> environment)
	{
		alphaManager = new ParameterManager<T> (environment);
		alphaManager.setExpression (configuration);
	}
	public T getAlpha () { return alphaManager.eval (); }
	ParameterManager<T> alphaManager;


	/**
	 * get Bessel function instance from library
	 * @param manager the description of the domain space
	 * @param library the library holding the model for the function
	 */
	void buildFunction (SpaceManager<T> manager, ExtendedPowerLibrary<T> library)
	{
		BesselFunctions<T> functions;
		(functions = new BesselFunctions<T> ()).init (manager);
		function = functions.getFunction (kind, alphaManager.eval (), terms, library);
	}
	Function<T> function;


}

