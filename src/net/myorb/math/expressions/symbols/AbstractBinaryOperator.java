
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.ConventionalNotations;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;

import net.myorb.math.expressions.SymbolMap.SymbolType;
import net.myorb.math.expressions.SymbolMap;

/**
 * boiler-plate class providing for anonymous construction of Binary operator lookup objects
 * @author Michael Druckman
 */
public abstract class AbstractBinaryOperator extends OperationObject
	implements SymbolMap.BinaryOperator
{

	/**
	 * construct based on
	 *  variable name and operator precedence
	 * @param name the name of the variable or other symbol
	 * @param precedence the precedence to apply to this operator
	 */
	public AbstractBinaryOperator (String name, int precedence)
	{
		super (name, precedence);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.BinaryOperator#markupForDisplay(java.lang.String, java.lang.String, java.lang.String, boolean, boolean, net.myorb.math.expressions.gui.rendering.NodeFormatting)
	 */
	public String markupForDisplay
	(String operator, String firstOperand, String secondOperand, boolean fenceFirst, boolean fenceSecond, NodeFormatting using)
	{
		String specialCase, notation = ConventionalNotations.findMarkupFor (operator); notation = notation==null? operator: notation;
		String left = using.formatParenthetical (firstOperand, fenceFirst), right = using.formatParenthetical (secondOperand, fenceSecond);
		/* an evaluation of operands may suggest a more efficient or commonly accepted notation, this allows for that short circuit */
		if ((specialCase = shortCircuit (left, right)) != null) return specialCase;
		else return using.formatBinaryOperation (left, notation, right);
	}

	/**
	 * special case check
	 * @param left the operand on left side
	 * @param right the operand on right side
	 * @return NULL for no special case, otherwise markup
	 */
	public String shortCircuit (String left, String right) { return null; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Named#getSymbolType()
	 */
	public SymbolType getSymbolType () { return SymbolType.BINARY; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.OperationObject#displayParameters()
	 */
	public String displayParameters () { return "left, right"; }

}
