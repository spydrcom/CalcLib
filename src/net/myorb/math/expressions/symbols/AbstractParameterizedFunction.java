
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.SymbolMap.SymbolType;
import net.myorb.math.expressions.SymbolMap.ParameterizedFunction;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.expressions.*;

/**
 * boiler-plate class providing for anonymous construction of Parameterized Function operator objects
 * @author Michael Druckman
 */
public abstract class AbstractParameterizedFunction extends OperationObject
		implements ParameterizedFunction
{

	/**
	 * construct based on
	 *  variable name and operator precedence
	 * @param name the name of the variable or other symbol
	 */
	public AbstractParameterizedFunction (String name)
	{
		super (name, SymbolMap.FUNCTTION_PRECEDENCE);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.ParameterizedFunction#markupForDisplay(java.lang.String, java.lang.String, net.myorb.math.expressions.gui.rendering.NodeFormatting)
	 */
	public String markupForDisplay (String operator, String parameters, NodeFormatting using) { return ""; }

	public String markupForDisplay (String operator, String operand, boolean fenceOperand, NodeFormatting using) { return ""; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Named#getSymbolType()
	 */
	public SymbolType getSymbolType () { return SymbolType.PARAMETERIZED; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.ParameterizedFunction#getParameterList()
	 */
	public String getParameterList () { return ""; }

	/**
	 * function recognition error
	 * @param sym Named symbol referenced
	 * @throws RuntimeException always thrown as error for missing function
	 */
	public static void functionExpected (SymbolMap.Named sym) throws RuntimeException
	{ throw new RuntimeException ("Function expected, '" + sym.getName () + "' found"); }

	/**
	 * verify symbol as function or issue error
	 * @param symbol Named symbol to be treated as function
	 * @return symbol cast to ParameterizedFunction if verification succeeds
	 * @throws RuntimeException error for missing function
	 */
	public static ParameterizedFunction
		verifyFunction (SymbolMap.Named symbol)
	throws RuntimeException
	{
		if (symbol instanceof ParameterizedFunction)
		{ return (ParameterizedFunction) symbol; }
		functionExpected (symbol);
		return null;
	}

	/**
	 * @param options text of options to use
	 */
	public void addParameterization (String options) {}

}
