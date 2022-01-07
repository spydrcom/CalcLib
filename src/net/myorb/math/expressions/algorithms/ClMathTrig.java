
package net.myorb.math.expressions.algorithms;

import net.myorb.math.TrigPowImplementation;
import net.myorb.math.TrigPowImplementation.Operations;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction.ImplementedFeatures;
import net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction.ParameterizationManager;
import net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction.Renderer;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.expressions.symbols.LibraryObject;
import net.myorb.math.expressions.SymbolMap.Named;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

import net.myorb.data.abstractions.SpaceDescription;

import java.util.Map;

/**
 * provide full complement of trigonometric function using Algorithm Implementation Abstraction.
 *  this layer allows provides access to configuration using LIBRARY / INSTANCE commands.
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class ClMathTrig<T> extends AlgorithmImplementationAbstraction<T>
{


	/**
	 * @param trigPowImpl an actual implementation of TrigPow to use for calculations
	 */
	public ClMathTrig
	(TrigPowImplementation<T> trigPowImpl) { this.trigPowImpl = trigPowImpl; }
	protected TrigPowImplementation<T> trigPowImpl;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#getInstance(java.lang.String, net.myorb.math.expressions.symbols.LibraryObject)
	 */
	public Named getInstance (String sym, LibraryObject<T> lib)
	{
		return new TrigAbstraction (sym, lib);
	}


	/**
	 * TrigPow function object base class
	 */
	public class TrigAbstraction extends ImplementationAbstraction
	{

		TrigAbstraction (String sym, LibraryObject<T> lib)
		{
			super (sym, lib);
		}

	}


	/**
	 * @return allocate an object that manages configuration from start-up XML source
	 */
	public CommonFunctionImplementation getConfigurableTrigImplementation ()
	{
		return new ConfigurableTrigImplementation ();
	}


	/**
	 * an object that manages configuration from start-up XML source
	 */
	class ConfigurableTrigImplementation extends Implementation
	{
		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.CommonOperatorLibrary.CommonFunctionImplementation#configure(java.lang.String)
		 */
		public void configure (String parameters) { establishImplementedFeatures (configureTrigManager (parameters)); }
	}


	/**
	 * allocate manager object for function
	 * @param parameters the parameter text supplied in configuration
	 * @return a configured function wrapper
	 */
	public TrigPowParameterManager<T> configureTrigManager (String parameters)
	{ return configureTrigManager (new TrigPowParameterManager<T> (parameters, environment)); }


	/**
	 * allocate manager object for function
	 * @param parameters the parameter map supplied in configuration
	 * @return a configured function wrapper
	 */
	public TrigPowParameterManager<T> configureTrigManager (Map<String,Object> parameters)
	{ return configureTrigManager (new TrigPowParameterManager<T> (parameters, environment)); }


	/**
	 * configure a parameter manager to represent a function
	 * @param trig manager for parameters of Trig function
	 * @return the manager being configured
	 */
	public TrigPowParameterManager<T> configureTrigManager (TrigPowParameterManager<T> trig)
	{ trig.setTrigPowImplementation (trigPowImpl); return trig.buildFunction (manager, library); }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction#configureManager(java.util.Map)
	 */
	public Configuration<T> configureManager (Map<String, Object> parameterMap) { return configureTrigManager (parameterMap); }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction#provideImplementation()
	 */
	public Implementation provideImplementation () { return new ConfigurableTrigImplementation (); }


}


/**
 * localize trig function processing as operator and as function syntax
 * @param <T> data type being processed
 */
class TrigPowParameterManager<T> implements
	AlgorithmImplementationAbstraction.ImplementedFeatures<T>,
	AlgorithmImplementationAbstraction.Configuration<T>,
	AlgorithmImplementationAbstraction.Renderer,
	Function<T>
{


	/**
	 * @param parameters map of configured parameters
	 * @param environment description of the session
	 */
	TrigPowParameterManager (Map<String,Object> parameters, Environment<T> environment)
	{
		this (parameters.get ("OP"), parameters.get ("POW"), environment);
	}
	TrigPowParameterManager (String parameters, Environment<T> environment)
	{
		this (parameters.split (";"), environment);
	}
	TrigPowParameterManager (String[] parameters, Environment<T> environment)
	{
		this (parameters[0], parameters[1], environment);
	}
	TrigPowParameterManager (Object OP, Object POW, Environment<T> environment)
	{
		this.pow = POW.toString ();
		this.identifier = OP.toString ();
		this.exponent = Integer.parseInt (pow);
		this.op = Operations.valueOf (this.identifier);
		this.space = environment.getSpaceManager ();
	}
	protected String pow, identifier;
	protected Operations op;
	protected int exponent;


	/**
	 * @param trigPowImpl the implementation object used in ClMath constructor
	 */
	void setTrigPowImplementation
	(TrigPowImplementation<T> trigPowImpl) { this.impl = trigPowImpl; }
	protected TrigPowImplementation<T> impl;


	/**
	 * construct MML for function identifier
	 * @param using the node formatting tool
	 * @return the function reference MML
	 */
	public String render (NodeFormatting using)
	{
		return using.formatSuperScript
			(
				using.formatIdentifierReference (this.identifier),
				using.formatNumericReference (this.pow)
			);
	}


	/**
	 * get TrigPow function instance from library
	 * @param manager the description of the domain space
	 * @param library the library holding the model for the function
	 * @return THIS for chaining
	 */
	TrigPowParameterManager<T> buildFunction (SpaceManager<T> manager, ExtendedPowerLibrary<T> library)
	{
		return this;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction.ImplementedFeatures#getFunction()
	 */
	public Function<T> getFunction () { return this; }


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
				return null;
			};
		};
	}


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T x)
	{
		return impl.trigPow (op, x, exponent);
	}


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceDescription<T> getSpaceDescription () { return space; }

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager<T> getSpaceManager () { return space; }
	protected SpaceManager<T> space;


}

