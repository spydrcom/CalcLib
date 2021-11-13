
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.SymbolMap;

/**
 * unary operator with calculus flag added
 * @author Michael Druckman
 */
public abstract class AbstractCalculusOperator
	extends AbstractUnaryOperator implements SymbolMap.CalculusOperator
{
	public AbstractCalculusOperator (String name, int precedence)
	{
		super (name, precedence);
	}
}
