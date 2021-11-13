
package net.myorb.math.expressions;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.symbols.*;

/**
 * provide for recognition of standard mathematical operators
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class BuiltInFunctions<T> extends BooleanFunctions<T>
{


	/**
	 * type manager used to evaluate computations
	 * @param environment access to the evaluation environment
	 */
	public BuiltInFunctions (Environment<T> environment) { super (environment); }


	/**
	 * import symbols found in space manager into map.
	 *  also included are function that can be derived from type management primitives
	 * @param into the symbol map object collecting imported symbols
	 */
	public void importFromSpaceManager (SymbolMap into)
	{
		into.add
		(
			new AbstractUnaryOperator (ABSOLUTE_VALUE_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameter) { return abs (parameter); }
			}, "Absolute value function of parameter"
		);
		into.add
		(
			new AbstractUnaryOperator (SIGN_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
				{
					T p = valueManager.toDiscrete (parameter);
					return valueManager.newDiscreteValue (spaceManager.isNegative (p)? spaceManager.newScalar (-1): spaceManager.newScalar (1));
				}
			}, "Sign (SGN) function of parameter"
		);
		into.add
		(
			new AbstractBinaryOperator (LT_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue left, ValueManager.GenericValue right) { return logicalResult (lt (left, right)); } },
			"Less than"
		);
		into.add
		(
			new AbstractBinaryOperator (LT_ABS_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue left, ValueManager.GenericValue right) { return logicalResult (lt (left, abs (right))); } },
			"Less than absolute value of"
		);
		into.add
		(
			new AbstractBinaryOperator (LE_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue left, ValueManager.GenericValue right) { return logicalResult (!lt (right, left)); } },
			"Less than or equal to"
		);
		into.add
		(
			new AbstractBinaryOperator (GT_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue left, ValueManager.GenericValue right) { return logicalResult (lt (right, left)); } },
			"Greater than"
		);
		into.add
		(
			new AbstractBinaryOperator (GT_ABS_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue left, ValueManager.GenericValue right) { return logicalResult (lt (abs (right), left)); } },
			"Greater than  absolute value of"
		);
		into.add
		(
			new AbstractBinaryOperator (GE_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue left, ValueManager.GenericValue right) { return logicalResult (!lt (left, right)); } },
			"Greater than or equal to"
		);
		into.add
		(
			new AbstractBinaryOperator (NE_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue left, ValueManager.GenericValue right) { return logicalResult (lt (left, right) || lt (right, left)); } },
			"Not equal to"
		);
		into.add
		(
			new AbstractBinaryOperator (EQ_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue left, ValueManager.GenericValue right) { return logicalResult (!lt (left, right) && !lt (right, left)); } },
			"Equal to"
		);
		into.add
		(
			new AbstractBinaryOperator (OR_OPERATOR, SymbolMap.ADDITION_PRECEDENCE)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue left, ValueManager.GenericValue right) { return logicalResult (or (left, right)); } },
			"Logical OR"
		);
		into.add
		(
			new AbstractBinaryOperator (AND_OPERATOR, SymbolMap.MULTIPLICATION_PRECEDENCE)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue left, ValueManager.GenericValue right) { return logicalResult (and (left, right)); } },
			"Logical AND"
		);
		into.add
		(
			new AbstractUnaryOperator (NOT_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue parameter) { return logicalResult (!isTrue (parameter)); } },
			"Logical NOT"
		);
		into.add
		(
			new AbstractBinaryOperator (NAND_OPERATOR, SymbolMap.FUNCTTION_PRECEDENCE)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue left, ValueManager.GenericValue right) { return logicalResult (!and (left, right)); } },
			"Logical NAND"
		);
		into.add
		(
			new AbstractBinaryOperator (NOR_OPERATOR, SymbolMap.FUNCTTION_PRECEDENCE)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue left, ValueManager.GenericValue right) { return logicalResult (!or (left, right)); } },
			"Logical NOR"
		);
		into.add
		(
			new AbstractBinaryOperator (XOR_OPERATOR, SymbolMap.FUNCTTION_PRECEDENCE)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue left, ValueManager.GenericValue right) { return logicalResult (xor (left, right)); } },
			"Logical XOR"
		);
		into.add
		(
			new AbstractBinaryOperator (NXOR_OPERATOR, SymbolMap.FUNCTTION_PRECEDENCE)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue left, ValueManager.GenericValue right) { return logicalResult (!xor (left, right)); } },
			"Logical NOT XOR"
		);
		into.add
		(
			new AbstractBinaryOperator (IMPLIES_OPERATOR, SymbolMap.FUNCTTION_PRECEDENCE)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue left, ValueManager.GenericValue right) { return logicalResult (!isTrue (left) || isTrue (right)); } },
			"Logical implies"
		);
		into.add
		(
			new AbstractBinaryOperator (NOT_IMPLIES_OPERATOR, SymbolMap.FUNCTTION_PRECEDENCE)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue left, ValueManager.GenericValue right) { return logicalResult (isTrue (left) && !isTrue (right)); } },
			"Logical NOT implies"
		);
		into.add
		(
			new AbstractBinaryOperator (IMPLIED_BY_OPERATOR, SymbolMap.FUNCTTION_PRECEDENCE)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue left, ValueManager.GenericValue right) { return logicalResult (isTrue (left) || !isTrue (right)); } },
			"Logical implied by"
		);
		into.add
		(
			new AbstractBinaryOperator (NOT_IMPLIED_BY_OPERATOR, SymbolMap.FUNCTTION_PRECEDENCE)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return logicalResult (!isTrue (left) && isTrue (right)); } },
			"Logical NOT implied by"
		);
		into.add
		(
			new AbstractUnaryPostfixOperator (SET_CONDITION_OPERATOR, SymbolMap.FUNCTTION_PRECEDENCE)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue parameter) { setCC (parameter); return null; } },
			"Logical condition code set"
		);
		into.add
		(
			new AbstractBinaryOperator (CHOOSE_OPERATOR, SymbolMap.ADDITION_PRECEDENCE)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return isTrue (getCC ())? left: right; } },
			"Choice based on condition code"
		);
	}

}
