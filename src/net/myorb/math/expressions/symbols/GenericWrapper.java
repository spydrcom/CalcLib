
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.SymbolMap;

import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.data.abstractions.Function;
import net.myorb.math.SpaceManager;

/**
 * encapsulate symbol table operator and expose as function
 * @author Michael Druckman
 * @param <T> data type
 */
public class GenericWrapper<T>
{


	/**
	 * function operating on GenericValue
	 * @param <T> data type
	 */
	public interface GenericFunction<T> extends Function<T>
	{
		ValueManager.GenericValue eval (ValueManager.GenericValue parameter);
	}


	/**
	 * @param spaceManager a space manager for the type
	 */
	public GenericWrapper (SpaceManager<T> spaceManager)
	{ this.valueManager = new ValueManager<T> (); this.spaceManager = spaceManager; }
	protected ValueManager<T> valueManager; protected SpaceManager<T> spaceManager;
	public GenericWrapper (SpaceDescription<T> spaceManager)
	{ this ((SpaceManager<T>) spaceManager); }


	/**
	 * wrapper for operator
	 */
	public class WrappedOperator implements GenericFunction<T>
	{

		/**
		 * @param operator the operator wrapped inside
		 */
		WrappedOperator (SymbolMap.ExecutableUnaryOperator operator) { this.operator = operator; }

		/* (non-Javadoc)
		 * @see net.myorb.math.Function#eval(java.lang.Object)
		 */
		public T eval (T x)
		{
			ValueManager.GenericValue value = valueManager.newDiscreteValue (x);
			return valueManager.toDiscreteValue (operator.execute (value)).getValue ();
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.symbols.OperatorWrapper.GenericFunction#eval(net.myorb.math.expressions.ValueManager.GenericValue)
		 */
		public ValueManager.GenericValue eval (ValueManager.GenericValue parameter)
		{
			return operator.execute (parameter);
		}
		SymbolMap.ExecutableUnaryOperator operator;

		/* (non-Javadoc)
		 * @see net.myorb.math.ManagedSpace#getSpaceManager()
		 */
		public SpaceManager<T> getSpaceDescription() { return spaceManager; }
		public SpaceManager<T> getSpaceManager() { return spaceManager; }

	}


	/**
	 * wrap an ExecutableUnaryOperator as a function
	 * @param operator the operator to be wrapped inside
	 * @return the function object wrapping the operator
	 */
	public GenericFunction<T> functionFor (SymbolMap.ExecutableUnaryOperator operator)
	{
		return new WrappedOperator (operator);
	}


	/**
	 * wrap an ExecutableUnaryOperator as a function
	 * @param symbol the symbol map object of unknown sub-type
	 * @return the symbol wrapped as a function
	 */
	public GenericFunction<T> functionFor (SymbolMap.Named symbol)
	{
		if (symbol instanceof SymbolMap.ExecutableUnaryOperator)
		{
			return functionFor ((SymbolMap.ExecutableUnaryOperator) symbol);
		}
		throw new RuntimeException ("Symbol does not refer to a function: " + symbol.getName ());
	}


	/**
	 * wrap function for operation on GenericValue
	 */
	public class WrappedFunction implements GenericFunction<T>
	{

		WrappedFunction (Function<T> function)
		{ this.function = function; }
		Function<T> function;

		/* (non-Javadoc)
		 * @see net.myorb.math.Function#eval(java.lang.Object)
		 */
		public T eval (T x) { return function.eval (x); }

		/* (non-Javadoc)
		 * @see net.myorb.math.ManagedSpace#getSpaceManager()
		 */
		public SpaceManager<T> getSpaceDescription () { return (SpaceManager<T>) function.getSpaceDescription (); }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.symbols.OperatorWrapper.GenericFunction#eval(net.myorb.math.expressions.ValueManager.GenericValue)
		 */
		public GenericValue eval (GenericValue parameter)
		{
			return valueManager.newDiscreteValue (function.eval (valueManager.toDiscrete (parameter)));
		}
		
	}


	/**
	 * @param function the function to be wrapped
	 * @return the function wrapped into GenericFunction wrapper
	 */
	public GenericFunction<T> functionFor (Function<T> function)
	{
		return new WrappedFunction (function);
	}


}

