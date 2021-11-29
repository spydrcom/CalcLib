
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.VectorPlotEnabled;
import net.myorb.math.expressions.GreekSymbols;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.expressions.TypedRangeDescription.TypedRangeProperties;
import net.myorb.math.expressions.symbols.AbstractParameterizedFunction;
import net.myorb.math.expressions.symbols.AbstractUnaryOperator;

import net.myorb.charting.DisplayGraphTypes.Point.Series;

import net.myorb.utilities.Configurable;

import java.util.List;

/**
 * library management implementation for operator/function classes
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class CommonOperatorLibrary<T>
{

	
	/**
	 * ValueManager object provides parameter and result marshaling
	 */
	public CommonOperatorLibrary ()
	{
		this.valueManager = new ValueManager<T> ();
	}
	protected ValueManager<T> valueManager;


	/**
	 * most simple form of describing operator algorithm
	 */
	public class CommonOperatorImplementation implements Configurable
	{
		/**
		 * evaluate the operation on the specified parameter
		 * @param using the parameter to the operation
		 * @return the calculated result
		 */
		public T evaluate (T using) { return null; }

		/* (non-Javadoc)
		 * @see net.myorb.utilities.Configurable#configure(java.lang.String)
		 */
		public void configure (String text) {}
	}


	/**
	 * describe the profile of a function
	 */
	public static class FunctionProfile
	{

		/**
		 * produce error for incorrect supply of parameters to function
		 * @param count count of parameters seen on call
		 */
		public void parameterCheck (int count)
		{
			if (count < lo || count > hi)
			{
				throw new RuntimeException
				(
					"Incorrect parameter count for " + name + ": " +
					count + " outside " + lo + "-" + hi
				);
			}
		}

		/**
		 * @param name the name of the function
		 * @param lo the lowest number of parameters allowed
		 * @param hi the highest number of parameters allowed
		 */
		public FunctionProfile (String name, int lo, int hi)
		{ this.name = name; this.lo = lo; this.hi = hi; }
		String name; int lo, hi;

	}


	/**
	 * most simple form of describing function algorithm
	 */
	public class CommonFunctionImplementation implements Configurable
	{
		/**
		 * evaluate the function on the specified parameter list
		 * @param using the parameter list to the function
		 * @return the calculated result
		 */
		public T evaluate (List<T> using) { return null; }

		/**
		 * format markup for operation
		 * @param operator the operator symbol
		 * @param parameters the text of the operand(s)
		 * @param using markup formatting object for display
		 * @return text of the formatted operation
		 */
		public String markupForDisplay
		(String operator, String parameters, NodeFormatting using)
		{
			return using.formatUnaryPrefixOperation (operator, parameters);
		}

		public String markupForDisplay (String operator, String operand, boolean fenceOperand, NodeFormatting using)
		{
			String value = using.formatParenthetical (operand, fenceOperand);
			return using.formatUnaryPrefixOperation (operator, value);
		}

		/* (non-Javadoc)
		 * @see net.myorb.utilities.Configurable#configure(java.lang.String)
		 */
		public void configure (String text) {}
	}

	/**
	 * provide interface for vector processing objects
	 */
	public class CommonVectoredFunctionImplementation
		extends CommonFunctionImplementation
		implements VectorPlotEnabled<T>
	{
		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.VectorPlotEnabled#evaluateSeries(net.myorb.math.expressions.TypedRangeDescription.TypedRangeProperties, java.util.List, net.myorb.math.expressions.evaluationstates.Environment)
		 */
		public void evaluateSeries
			(
				TypedRangeProperties<T> domainDescription,
				List<Series> series, Environment<T> environment
			)
		{
			throw new RuntimeException ("Series evaluation not implemented");
		}
	}

	/**
	 * establish an error state
	 * @param name the name of the operator found to not have an implementation
	 * @return nothing, exception always thrown
	 */
	public T notFound (String name)
	{
		throw new RuntimeException ("Imlementation for algorithm not found: " + name);
	}


	/**
	 * an instance of the implementation class that provides for error states
	 */
	public class Unimplemented extends CommonOperatorImplementation
	{
		/**
		 * @param name the name of the operator to display in the error message
		 */
		public Unimplemented (String name) { this.name = name; }
		public void configure (String parameters) { notFound (name); }
		protected String name;
	}


	/**
	 * an instance of the implementation class that provides for error states
	 */
	public class Missing extends CommonVectoredFunctionImplementation
	{
		/**
		 * @param name the name of the function to display in the error message
		 */
		public Missing (String name) { this.name = name; }
		public void configure (String parameters) { notFound (name); }
		protected String name;
	}


	/**
	 * provide for conversion of parameters and results
	 */
	public class MarshalingWrapper extends AbstractUnaryOperator
	{

		/**
		 * @param symbol the symbol text that will reference this operation
		 * @param precedence the value of the precedence to apply to this operation
		 * @param implementation the object holding the operator implementation
		 */
		public MarshalingWrapper
			(
				String symbol, int precedence,
				CommonOperatorImplementation implementation
			)
		{
			super (symbol, precedence);
			this.implementation = implementation;
		}
		protected CommonOperatorImplementation implementation;

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.SymbolMap.ExecutableUnaryOperator#execute(net.myorb.math.expressions.ValueManager.GenericValue)
		 */
		public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
		{
			T value = valueManager.toDiscrete (parameter);
			
			return valueManager.newDiscreteValue
				(
					implementation.evaluate (value)
				);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.symbols.AbstractUnaryOperator#addParameterization(java.lang.String)
		 */
		public void addParameterization (String options)
		{ implementation.configure (options); }

	}


	/**
	 * provide for conversion of parameters and results
	 */
	public class MultipleMarshalingWrapper
		extends AbstractParameterizedFunction
		implements FormattingRequirement
	{

		/**
		 * @param symbol the symbol text that will reference this operation
		 * @param implementation the object holding the function implementation
		 */
		public MultipleMarshalingWrapper
			(
				String symbol,
				CommonFunctionImplementation implementation
			)
		{
			super (symbol);
			this.commonImplementation = implementation;
		}
		protected CommonFunctionImplementation commonImplementation;

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.SymbolMap.ExecutableUnaryOperator#execute(net.myorb.math.expressions.ValueManager.GenericValue)
		 */
		public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
		{
			List<T> array = valueManager.toArray (parameters);
			
			return valueManager.newDiscreteValue
				(
					commonImplementation.evaluate (array)
				);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.symbols.AbstractParameterizedFunction#markupForDisplay(java.lang.String, java.lang.String, net.myorb.math.expressions.gui.rendering.NodeFormatting)
		 */
		public String markupForDisplay (String operator, String parameters, NodeFormatting using)
		{
			return commonImplementation.markupForDisplay (operator, parameters, using);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.symbols.AbstractParameterizedFunction#markupForDisplay(java.lang.String, java.lang.String, boolean, net.myorb.math.expressions.gui.rendering.NodeFormatting)
		 */
		public String markupForDisplay (String operator, String operand, boolean fenceOperand, NodeFormatting using)
		{
			return commonImplementation.markupForDisplay (operator, operand, fenceOperand, using);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.symbols.AbstractUnaryOperator#addParameterization(java.lang.String)
		 */
		public void addParameterization (String options)
		{ commonImplementation.configure (options); }

	}


	/**
	 * a marker interface forcing rendering implementation for a function
	 */
	public interface FormattingRequirement
	extends SymbolMap.EnhancedFunctionFormattingRequirement
	{}


	/**
	 * marshaling layer that will pass-thru series evaluation requests
	 */
	public class MultipleVectoredMarshalingWrapper extends MultipleMarshalingWrapper
		implements FormattingRequirement, VectorPlotEnabled<T>
	{

		public MultipleVectoredMarshalingWrapper
			(String symbol, CommonVectoredFunctionImplementation implementation)
		{
			super (symbol, implementation);
			this.vectoredImplementation = implementation;
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.VectorPlotEnabled#evaluateSeries(net.myorb.math.expressions.TypedRangeDescription.TypedRangeProperties, java.util.List, net.myorb.math.expressions.evaluationstates.Environment)
		 */
		public void evaluateSeries
			(
				TypedRangeProperties<T> domainDescription, List<Series> series,
				Environment<T> environment
			)
		{
			vectoredImplementation.evaluateSeries (domainDescription, series, environment);
		}
		protected CommonVectoredFunctionImplementation vectoredImplementation;

	}


	/**
	 * add FormattingRequirement to a ParameterizedFunction
	 */
	public static abstract class EnhancedParameterizedFunction
	extends AbstractParameterizedFunction implements FormattingRequirement
	{ public EnhancedParameterizedFunction(String name) { super (name); } }


	/**
	 * provide an interface to calling simple function
	 * @param <T> data types used in function call
	 */
	public static class DiscreteFunctionInterface<T>
	{

		/**
		 * describe function with one or two parameters
		 * @param <T> the type of the parameters
		 */
		public static class Function<T>
		{
			/**
			 * for a function implementation using the wrong profile
			 * @return never used
			 */
			T parameterError ()
			{
				throw new RuntimeException ("Incorrect parameter profile used");
			}

			/**
			 * evaluate function, double parameter
			 * @param parameter1 the first value of the parameter
			 * @param parameter2 the second value of the parameter
			 * @return the function result
			 */
			public T eval (T parameter1, T parameter2) { return parameterError (); }

			/**
			 * evaluate function, single parameter
			 * @param parameter the value of the parameter
			 * @return the function result
			 */
			public T eval (T parameter) { return parameterError (); }
		}

		/**
		 * @param parameter the parameter to function
		 * @return function result in generic wrapper
		 */
		public ValueManager.GenericValue
				eval (T parameter)
		{
			return wrapperManager.newDiscreteValue
				(function.eval (parameter));
		}

		/**
		 * @param values parameter to function in generic wrapper
		 * @return function result in generic wrapper
		 */
		public ValueManager.GenericValue
			eval (ValueManager.GenericValue values)
		{ return eval (wrapperManager.toDiscrete (values)); }

		/**
		 * @param array function parameters wrapped as 2 element array
		 * @return the computed function result
		 */
		public ValueManager.GenericValue
			evalFor (List<T> array)
		{
			return wrapperManager.newDiscreteValue
				(
					function.eval (array.get (0), array.get (1))
				);
		}

		/**
		 * @param values 2 parameters to function in generic wrapper
		 * @return function result in generic wrapper
		 */
		public ValueManager.GenericValue evalForDual
			(ValueManager.GenericValue values)
		{ return evalFor (wrapperManager.toArray (values)); }

		/**
		 * @param values generic wrapper for one or two parameters
		 * @return function result in generic wrapper
		 */
		public ValueManager.GenericValue evalFor
			(
				ValueManager.GenericValue values
			)
		{
			if (wrapperManager.isDiscrete (values))
			{
				return eval (values);
			}

			List<T> array = wrapperManager.toArray (values);

			switch (array.size ())
			{
				case 1: return eval (array.get (0));
				case 2: return evalFor (array);
				default:
			}

			throw new RuntimeException ("Invalid parameter profile");
		}

		/**
		 * @param valueManager a value manager for the generic wrapper
		 */
		public void setValueManager
		(ValueManager<T> valueManager) { this.wrapperManager = valueManager; }
		protected ValueManager<T> wrapperManager;

		/**
		 * @param function a function wrapper for the function being called
		 */
		public DiscreteFunctionInterface
		(Function<T> function) { this.function = function; }
		protected Function<T> function;

	}


	/**
	 * simple common function and wrapper interface descriptions
	 */
	public class CommonFunction
	extends DiscreteFunctionInterface.Function<T> {}
	public class CommonWrapper extends DiscreteFunctionInterface<T>
	{
		public CommonWrapper (CommonFunction function)
		{ super (function); this.setValueManager (valueManager); }
	}


	/**
	 * translate to Greek letter for use as symbol where appropriate
	 * @param symbol the English name text
	 * @return the symbol to use
	 */
	public static String lookupIdentifierFor (String symbol)
	{
		String greek = GreekSymbols.getEnglishToGreekMap ().get (symbol);
		if (greek != null) return greek;
		return symbol;
	}


	/**
	 * format identifier for render
	 * @param symbol the English name text
	 * @param using the node formatting object used by the render agent
	 * @return the MML identifier node for the reference
	 */
	public static String formatIdentifierFor (String symbol, NodeFormatting using)
	{
		return using.formatIdentifierReference (lookupIdentifierFor (symbol));
	}


	/**
	 * format identifier with subscript for render
	 * @param identifier the text for the identifier
	 * @param subscript the numeric value for the subscript
	 * @param using the node formatting object used by the render agent
	 * @return the MML identifier node for the reference
	 */
	public static String formatNumericSubscript
		(
			String identifier, String subscript, NodeFormatting using
		)
	{
		return using.formatSubScript
		(
			identifier, using.formatNumericReference (subscript)
		);
	}


}

