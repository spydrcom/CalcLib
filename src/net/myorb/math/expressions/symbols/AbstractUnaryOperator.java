
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.gui.rendering.NodeFormatting;

import net.myorb.math.expressions.SymbolMap.SymbolType;
import net.myorb.math.expressions.SymbolMap;

/**
 * boiler-plate class providing for anonymous construction of Unary postfix operator objects
 * @author Michael Druckman
 */
public abstract class AbstractUnaryOperator extends OperationObject
	implements SymbolMap.UnaryOperator
{

	/**
	 * construct based on
	 *  variable name and operator precedence
	 * @param name the name of the variable or other symbol
	 * @param precedence the precedence to apply to this operator
	 */
	public AbstractUnaryOperator (String name, int precedence)
	{
		super (name, precedence);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Named#getSymbolType()
	 */
	public SymbolType getSymbolType () { return SymbolType.UNARY; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.OperationObject#displayParameters()
	 */
	public String displayParameters () { return "x"; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.UnaryOperator#markupForDisplay(java.lang.String, java.lang.String, boolean, net.myorb.math.expressions.gui.rendering.NodeFormatting)
	 */
	public String markupForDisplay (String operator, String operand, boolean fenceOperand, NodeFormatting using)
	{
		//????: fix NEGATE logic - appears fixed as of 08/09/17
		//   this is the appropriate form of this method, but it breaks the NEGATE logic...
		//   so the version below will stand and appears to function correctly, but the NEGATE logic should be fixed
		// => 08/09/17 - NEGATE had precedence set to 10 which appears to be bug, set to 8 the logic performs correctly
		String value = using.formatParenthetical (operand, fenceOperand);
		return using.formatUnaryPrefixOperation (operator, value);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.UnaryOperator#markupForDisplay(java.lang.String, java.lang.String, net.myorb.math.expressions.gui.rendering.NodeFormatting)
	 */
	public String markupForDisplay (String operator, String operand, NodeFormatting using)
	{
		return using.formatUnaryPrefixOperation (operator, operand);
	}
	
}
