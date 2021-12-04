
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.expressions.symbols.LibraryObject;

import net.myorb.math.Function;

import java.util.List;
import java.util.Map;

/**
 * a completely generic abstraction for representations of functions
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public abstract class AlgorithmImplementationAbstraction<T>
		extends InstanciableFunctionLibrary<T>
{


	/**
	 * control the parameters for a specified function
	 * @param <T> data type being processed
	 */
	public interface ParameterizationManager<T>
	{
		/**
		 * @param symbol the name of a configuration parameter
		 * @return the manager for the named parameter
		 */
		ParameterManager<T> getManagerFor (String symbol);
	}

	/**
	 * the interface to the rendering layer
	 */
	public interface Renderer
	{
		/**
		 * @param using the node formatting tools object
		 * @return the rendered identifier for the function
		 */
		String render (NodeFormatting using);
	}

	/**
	 * provide the functionality that implements the function features
	 * @param <T> data type being processed
	 */
	public interface ImplementedFeatures<T>
	{
		/**
		 * @return a function object that evaluates the algorithm at specified points
		 */
		Function<T> getFunction ();

		/**
		 * @return a rendering object for the function identifier
		 */
		Renderer getFormatter ();
	}

	/**
	 * access to the code and data that describes a function instance
	 * @param <T> data type being processed
	 */
	public interface Configuration<T>
	{
		/**
		 * @return access to the code for evaluation and rendering
		 */
		ImplementedFeatures<T> getImplementedFeatures ();

		/**
		 * @return access to the controller for the configured parameters
		 */
		ParameterizationManager<T> getParameterizationManager ();
	}

	/**
	 * @param parameterMap a map of name-value pairs that configure the function
	 * @return the configuration captured from the map
	 */
	public abstract Configuration<T> configureManager (Map<String,Object> parameterMap);

	/**
	 * @return an instance of the class implementing the function
	 */
	public abstract Implementation provideImplementation ();


	/**
	 * function object base class
	 */
	public class ImplementationAbstraction extends MultipleMarshalingWrapper
	{

		/**
		 * connect an implementation with a configuration
		 * @param sym an identifier for the function being declared
		 * @param lib the library object that provides access to the code and configuration parameter values
		 */
		public ImplementationAbstraction (String sym, LibraryObject<T> lib)
		{
			this
			(
				sym, lib.getParameterization ()
			);
		}

		public ImplementationAbstraction
		(String sym, Map<String,Object> parameterMap)
		{
			this
			(
				sym,
				configureManager (parameterMap),
				provideImplementation ()
			);
			this.parameterMap = parameterMap;
		}
		protected Map<String,Object> parameterMap;

		public ImplementationAbstraction
		(String sym, Configuration<T> configuration, Implementation impl)
		{
			super (sym, impl);
			impl.establishImplementedFeatures (configuration.getImplementedFeatures ());
			this.parameterizationManager = configuration.getParameterizationManager ();
		}
		protected ParameterizationManager<T> parameterizationManager;

	}


	/**
	 * Implementation of common function
	 */
	public class Implementation extends CommonFunctionImplementation
	{

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.CommonOperatorLibrary.CommonFunctionImplementation#evaluate(java.util.List)
		 */
		public T evaluate (List<T> using)
		{ return function.eval (using.get (0)); }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.CommonOperatorLibrary.CommonFunctionImplementation#markupForDisplay(java.lang.String, java.lang.String, net.myorb.math.expressions.gui.rendering.NodeFormatting)
		 */
		public String markupForDisplay
		(String operator, String parameters, NodeFormatting using)
		{ return formatter.render (using) + using.formatParenthetical (parameters); }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.CommonOperatorLibrary.CommonFunctionImplementation#markupForDisplay(java.lang.String, java.lang.String, boolean, net.myorb.math.expressions.gui.rendering.NodeFormatting)
		 */
		public String markupForDisplay
		(String operator, String operand, boolean fenceOperand, NodeFormatting using)
		{ return markupForDisplay (operator, operand, using); }

		/**
		 * @param implementedFeatures access to implemented features
		 */
		public void establishImplementedFeatures
		(ImplementedFeatures<T> implementedFeatures)
		{
			this.function = implementedFeatures.getFunction ();
			this.formatter = implementedFeatures.getFormatter ();
		}
		protected Function<T> function;
		protected Renderer formatter;

	}


}

