
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction.ImplementedFeatures;
import net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction.ParameterizationManager;
import net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction.Renderer;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;

import net.myorb.math.expressions.symbols.LibraryObject;
import net.myorb.math.expressions.SymbolMap.Named;

import net.myorb.math.ExtendedPowerLibrary;

import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

import net.myorb.data.abstractions.SpaceDescription;

import java.util.Map;

/**
 * a manager for building quadrature consumer objects 
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class ClMathQuad<T> extends AlgorithmImplementationAbstraction<T>
{


	/**
	 * 
	 */
	public ClMathQuad ()
	{
		
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#getInstance(java.lang.String, net.myorb.math.expressions.symbols.LibraryObject)
	 */
	public Named getInstance (String sym, LibraryObject<T> lib)
	{
		return new QuadAbstraction (sym, lib);
	}


	/**
	 * Quad function object base class
	 */
	public class QuadAbstraction extends ImplementationAbstraction
	{

		QuadAbstraction (String sym, LibraryObject<T> lib)
		{
			super (sym, lib);
		}

	}


	/**
	 * @return allocate an object that manages configuration from start-up XML source
	 */
	public CommonFunctionImplementation getConfigurableQuadImplementation ()
	{
		return new ConfigurableQuadImplementation ();
	}


	/**
	 * an object that manages configuration from start-up XML source
	 */
	class ConfigurableQuadImplementation extends Implementation
	{
		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.CommonOperatorLibrary.CommonFunctionImplementation#configure(java.lang.String)
		 */
		public void configure (String parameters) { establishImplementedFeatures (configureQuadManager (parameters)); }
	}


	/**
	 * allocate manager object for function
	 * @param parameters the parameter text supplied in configuration
	 * @return a configured function wrapper
	 */
	public QuadParameterManager<T> configureQuadManager (String parameters)
	{ return configureQuadManager (new QuadParameterManager<T> (parameters, environment)); }


	/**
	 * allocate manager object for function
	 * @param parameters the parameter map supplied in configuration
	 * @return a configured function wrapper
	 */
	public QuadParameterManager<T> configureQuadManager (Map<String,Object> parameters)
	{ return configureQuadManager (new QuadParameterManager<T> (parameters, environment)); }


	/**
	 * configure a parameter manager to represent a function
	 * @param quad manager for parameters of quad function
	 * @return the manager being configured
	 */
	public QuadParameterManager<T> configureQuadManager (QuadParameterManager<T> quad)
	{ return quad.buildFunction (manager, library); }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction#configureManager(java.util.Map)
	 */
	public Configuration<T> configureManager (Map<String, Object> parameterMap) { return configureQuadManager (parameterMap); }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction#provideImplementation()
	 */
	public Implementation provideImplementation () { return new ConfigurableQuadImplementation (); }

}


class QuadParameterManager<T> implements
	AlgorithmImplementationAbstraction.ImplementedFeatures<T>,
	AlgorithmImplementationAbstraction.Configuration<T>,
	AlgorithmImplementationAbstraction.Renderer,
	Function<T>
{


	QuadParameterManager (Map<String,Object> parameters, Environment<T> environment)
	{
		this (parameters.get ("OP"), parameters.get ("POW"), environment);
	}
	QuadParameterManager (String parameters, Environment<T> environment)
	{
		this (parameters.split (";"), environment);
	}
	QuadParameterManager (String[] parameters, Environment<T> environment)
	{
		this (parameters[0], parameters[1], environment);
	}
	QuadParameterManager (Object OP, Object POW, Environment<T> environment)
	{
		
	}


	/**
	 * @param manager the description of the domain space
	 * @param library the library holding the model for the function
	 * @return THIS for chaining
	 */
	QuadParameterManager<T> buildFunction (SpaceManager<T> manager, ExtendedPowerLibrary<T> library)
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
	public T eval (T x) { return null; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction.Renderer#render(net.myorb.math.expressions.gui.rendering.NodeFormatting)
	 */
	public String render (NodeFormatting using) { return null; }


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

