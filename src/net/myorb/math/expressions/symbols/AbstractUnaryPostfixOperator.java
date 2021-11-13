
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.gui.rendering.NodeFormatting;

import net.myorb.math.expressions.SymbolMap.SymbolType;
import net.myorb.math.expressions.SymbolMap;

/**
 * boiler-plate class providing for anonymous construction of Unary postfix operator lookup objects
 * @author Michael Druckman
 */
public abstract class AbstractUnaryPostfixOperator extends OperationObject
	implements SymbolMap.UnaryPostfixOperator
{

	/**
	 * construct based on
	 *  variable name and operator precedence
	 * @param name the name of the variable or other symbol
	 * @param precedence the precedence to apply to this operator
	 */
	public AbstractUnaryPostfixOperator (String name, int precedence)
	{
		super (name, precedence);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.UnaryPostfixOperator#markupForDisplay(java.lang.String, boolean, java.lang.String, net.myorb.math.expressions.gui.rendering.NodeFormatting)
	 */
	public String markupForDisplay (String operand, boolean fenceOperand, String operator, NodeFormatting using)
	{ return using.formatUnaryPostfixOperation (using.formatParenthetical (operand, fenceOperand), operator); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Named#getSymbolType()
	 */
	public SymbolType getSymbolType () { return SymbolType.POSTFIX; }

}
