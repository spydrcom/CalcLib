
package net.myorb.math.expressions;

import net.myorb.math.expressions.evaluationstates.Environment;

import java.util.ArrayList;

public class BooleanFunctions<T> extends OperatorNomenclature
{


	/**
	 * type manager used to evaluate computations
	 * @param environment access to the evaluation environment
	 */
	public BooleanFunctions (Environment<T> environment)
	{
		this.environment = environment;
		this.spaceManager = environment.getSpaceManager ();
		this.valueManager = environment.getValueManager ();
	}
	protected ExpressionSpaceManager<T> spaceManager;
	protected ValueManager<T> valueManager;
	protected Environment<T> environment;


	/**
	 * add appropriate name to generic value
	 * @param value the value to be wrapped as generic
	 * @param name the name to be given to the value
	 * @return generic value object with name
	 */
	public ValueManager.GenericValue namedValue (T value, String name)
	{
		ValueManager.GenericValue v =
			valueManager.newDiscreteValue (value);
		v.setName (name);
		return v;
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


	/**
	 * compute absolute value
	 * @param parameter the source value operated on
	 * @return abs(parameter)
	 */
	public ValueManager.GenericValue abs (ValueManager.GenericValue parameter)
	{
		T p = valueManager.toDiscrete (parameter);
		return spaceManager.isNegative (p)? valueManager.newDiscreteValue (spaceManager.negate (p)): parameter;
	}


}

