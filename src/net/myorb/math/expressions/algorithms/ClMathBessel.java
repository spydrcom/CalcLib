
package net.myorb.math.expressions.algorithms;

import net.myorb.math.specialfunctions.BesselFunctions;
import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.rendering.MathMarkupNodes;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;

import net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction.ImplementedFeatures;
import net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction.ParameterizationManager;
import net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction.Renderer;

import net.myorb.math.expressions.symbols.LibraryObject;
import net.myorb.math.expressions.SymbolMap.Named;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

import java.util.Map;

/**
 * manage parameterization of functions declared as Bessel
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class ClMathBessel<T> extends AlgorithmImplementationAbstraction<T>
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
	public class BesselAbstraction extends ImplementationAbstraction
	{

		BesselAbstraction (String sym, LibraryObject<T> lib)
		{
			super (sym, lib);
			alphaManager = getParameterCalled ("alpha");
		}
		ParameterManager<T> alphaManager;

		/**
		 * @return configured alpha value
		 */
		public double getAlpha ()
		{
			return getValueFor (alphaManager);
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
	class ConfigurableBesselImplementation extends Implementation
	{
		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.CommonOperatorLibrary.CommonFunctionImplementation#configure(java.lang.String)
		 */
		public void configure (String parameters) { establishImplementedFeatures (configureBesselManager (parameters)); }
	}


	/**
	 * allocate manager object for function
	 * @param parameters the parameter text supplied in configuration
	 * @return a configured function wrapper
	 */
	public BesselParameterManager<T> configureBesselManager (String parameters)
	{ return configureBesselManager (new BesselParameterManager<T> (parameters, environment)); }

	/**
	 * allocate manager object for function
	 * @param parameters the parameter map supplied in configuration
	 * @return a configured function wrapper
	 */
	public BesselParameterManager<T> configureBesselManager (Map<String,Object> parameters)
	{ return configureBesselManager (new BesselParameterManager<T> (parameters, environment)); }

	/**
	 * configure a parameter manager to represent a function
	 * @param bessel manager for parameters of Bessel function
	 * @return the manager being configured
	 */
	public BesselParameterManager<T> configureBesselManager (BesselParameterManager<T> bessel)
	{ return bessel.buildFunction (manager, library); }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction#configureManager(java.util.Map)
	 */
	public Configuration<T> configureManager (Map<String, Object> parameterMap) { return configureBesselManager (parameterMap); }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction#provideImplementation()
	 */
	public Implementation provideImplementation () { return new ConfigurableBesselImplementation (); }


}


/**
 * localize Bessel function processing as operator and as function syntax
 * @param <T> data type being processed
 */
class BesselParameterManager<T> implements
	AlgorithmImplementationAbstraction.ImplementedFeatures<T>,
	AlgorithmImplementationAbstraction.Configuration<T>,
	AlgorithmImplementationAbstraction.Renderer
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
	protected String kind;


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
	public String render (NodeFormatting using)
	{
		String sp = MathMarkupNodes.space ("5");
		String id = using.formatIdentifierReference (identifier);
		return using.formatSubScript (id, formatAlpha (using)) + sp;
	}
	public String formatAlpha (NodeFormatting using)
	{
		try { return alphaManager.render (); }
		catch (Exception e) { return using.formatIdentifierReference ("?"); }
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
	protected ParameterManager<T> alphaManager;


	/**
	 * get Bessel function instance from library
	 * @param manager the description of the domain space
	 * @param library the library holding the model for the function
	 * @return THIS for chaining
	 */
	BesselParameterManager<T> buildFunction (SpaceManager<T> manager, ExtendedPowerLibrary<T> library)
	{
		BesselFunctions<T> functions;
		(functions = new BesselFunctions<T> ()).init (manager);
		function = functions.getFunction (kind, alphaManager.eval (), terms, library);
		identifier = ((SpecialFunctionFamilyManager.FunctionDescription<T>) function).getRenderIdentifier ();
		return this;
	}
	public Function<T> getFunction () { return function; }
	protected Function<T> function;
	protected String identifier;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction.Configuration#getImplementedFeatures()
	 */
	public ImplementedFeatures<T> getImplementedFeatures () { return this; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction.ImplementedFeatures#getFormatter()
	 */
	public Renderer getFormatter () { return this; }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction.Configuration#getParameterizationManager()
	 */
	public ParameterizationManager<T> getParameterizationManager ()
	{
		return new ParameterizationManager<T> ()
		{
			public ParameterManager<T> getManagerFor (String symbol)
			{
				return alphaManager;
			};
		};
	}


}

