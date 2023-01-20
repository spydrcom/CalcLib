
package net.myorb.math.complexnumbers;

import net.myorb.math.expressions.symbols.CommonFunctionBase;

/**
 * common base for complex functions
 * @author Michael Druckman
 */
public abstract class CommonComplexFunctionBase extends CommonFunctionBase < ComplexValue <Double> >
{


	/**
	 * @param named the name to give the symbol
	 */
	public CommonComplexFunctionBase (String named)
	{
		super (named, "z");
	}


}

