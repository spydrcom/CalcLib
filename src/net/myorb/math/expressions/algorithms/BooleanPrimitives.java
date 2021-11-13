
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.symbols.AbstractBinaryOperator;
import net.myorb.math.expressions.symbols.AbstractUnaryPostfixOperator;
import net.myorb.math.expressions.symbols.AbstractBuiltinVariableLookup;
import net.myorb.math.expressions.symbols.AbstractUnaryOperator;
import net.myorb.math.expressions.*;

import java.util.ArrayList;

/**
 * implementations of algorithms computing boolean operations
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class BooleanPrimitives<T> extends AlgorithmCore<T>
{


	/**
	 * type manager used to evaluate computations
	 * @param environment access to the evaluation environment
	 */
	public BooleanPrimitives (Environment<T> environment)
	{
		super (environment);
	}


	/**
	 * evaluate value as TRUE = 1 or FALSE = 0
	 * @param value the logical state to evaluate
	 * @return value? 1: 0
	 */
	public ValueManager.GenericValue logicalResult (boolean value)
	{
		if (value) return namedValue (spaceManager.getOne (), "true");
		else return namedValue (spaceManager.getZero (), "false");
	}


	/**
	 * evaluate left LT right
	 * @param left left side value of operator expression
	 * @param right right side value of operator expression
	 * @return computed result
	 */
	public boolean lt (ValueManager.GenericValue left, ValueManager.GenericValue right)
	{ return spaceManager.lessThan (valueManager.toDiscrete (left), valueManager.toDiscrete (right)); }


	/**
	 * evaluate item != 0
	 * @param item the item to be evaluated
	 * @return TRUE = item != 0
	 */
	public boolean isTrue (ValueManager.GenericValue item) { return !spaceManager.isZero (valueManager.toDiscrete (item)); }


	/**
	 * get the value of the condition code
	 * @return the value of the condition code
	 */
	public ValueManager.GenericValue getCC ()
	{
		return conditionStack.remove (0);
	}


	/**
	 * set the value of the condition code
	 * @param parameter the value to treat as the condition code
	 */
	public void setCC (ValueManager.GenericValue parameter) { conditionStack.add (0, parameter); }
	protected ArrayList<ValueManager.GenericValue> conditionStack = new ArrayList<ValueManager.GenericValue>();


	/**
	 * compute the logical value of left OR right
	 * @param left left side value of operator expression
	 * @param right right side value of operator expression
	 * @return computed result
	 */
	public boolean or (ValueManager.GenericValue left, ValueManager.GenericValue right) { return isTrue (left) || isTrue (right); }


	/**
	 * compute the logical value of left XOR right
	 * @param left left side value of operator expression
	 * @param right right side value of operator expression
	 * @return computed result
	 */
	public boolean xor (ValueManager.GenericValue left, ValueManager.GenericValue right) { return or (left, right) && !and (left, right); }


	/**
	 * compute the logical value of left AND right
	 * @param left left side value of operator expression
	 * @param right right side value of operator expression
	 * @return computed result
	 */
	public boolean and (ValueManager.GenericValue left, ValueManager.GenericValue right) { return isTrue (left) && isTrue (right); }


	/*
	 * symbols for true and false
	 */


	/**
	 * identify value - TRUE
	 * @param symbol the symbol associated with this object
	 * @return variable object referencing value
	 */
	public AbstractBuiltinVariableLookup getTrueValue (String symbol)
	{
		return new AbstractBuiltinVariableLookup (symbol)
		{
			public ValueManager.GenericValue getValue ()
			{ return logicalResult (true); }
		};
	}


	/**
	 * identify value - FALSE
	 * @param symbol the symbol associated with this object
	 * @return variable object referencing value
	 */
	public AbstractBuiltinVariableLookup getFalseValue (String symbol)
	{
		return new AbstractBuiltinVariableLookup (symbol)
		{
			public ValueManager.GenericValue getValue ()
			{ return logicalResult (false); }
		};
	}


	/*
	 * algorithms for condition code operators
	 */


	/**
	 * implement operator - :
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getChooseAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue
			execute (ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return isTrue (getCC ())? left: right; }
		};
	}


	/**
	 * implement operator - ?
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryPostfixOperator getConditionCodeAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryPostfixOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{ setCC (parameter); return null; }
		};
	}


	/*
	 * algorithms for boolean unary/binary functions (not, and, or, ...)
	 */


	/**
	 * implement operator - NOT
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getNotAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{ return logicalResult (!isTrue (parameter)); }
		};
	}


	/**
	 * implement operator - AND
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getAndAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return logicalResult (and (left, right)); }
		};
	}


	/**
	 * implement operator - OR
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getOrAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return logicalResult (or (left, right)); }
		};
	}


	/**
	 * implement operator - NAND
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getNandAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return logicalResult (!and (left, right)); }
		};
	}


	/**
	 * implement operator - NOR
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getNorAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return logicalResult (!or (left, right)); }
		};
	}


	public AbstractBinaryOperator getXorAlgorithm (String symbol, int precedence)
	/**
	 * implement operator - XOR
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return logicalResult (xor (left, right)); }
		};
	}


	/**
	 * implement operator - NXOR
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getNxorAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return logicalResult (!xor (left, right)); }
		};
	}


	/**
	 * implement operator - (IMPLIES)
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getImpliesAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return logicalResult (!isTrue (left) || isTrue (right)); }
		};
	}


	/**
	 * implement operator - (NOT IMPLIES)
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getNimpliesAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return logicalResult (isTrue (left) && !isTrue (right)); }
		};
	}


	/**
	 * implement operator - IMPLIED
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getImpliedByAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return logicalResult (isTrue (left) || !isTrue (right)); }
		};
	}


	/**
	 * implement operator - ~IMPLIED
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getNimpliedByAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return logicalResult (!isTrue (left) && isTrue (right)); }
		};
	}


	/*
	 * algorithms for relational boolean operators
	 */


	/**
	 * implement operator - LT
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getLtAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return logicalResult (lt (left, right)); }
		};
	}


	/**
	 * implement operator - LT ABS
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getLtAbsAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return logicalResult (lt (left, abs (right))); }
		};
	}


	/**
	 * implement operator - GT
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getGtAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return logicalResult (lt (right, left)); }
		};
	}


	/**
	 * implement operator - GT ABS
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getGtAbsAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return logicalResult (lt (abs (right), left)); }
		};
	}


	/**
	 * implement operator - LE
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getLeAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return logicalResult (!lt (right, left)); }
		};
	}


	/**
	 * implement operator - GE
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getGeAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return logicalResult (!lt (left, right)); }
		};
	}


	/**
	 * implement operator - ~=
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getNeAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return logicalResult (lt (left, right) || lt (right, left)); }
		};
	}


	/**
	 * implement operator - =
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getEqAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return logicalResult (!lt (left, right) && !lt (right, left)); }
		};
	}


}

