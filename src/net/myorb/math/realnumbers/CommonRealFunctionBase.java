
package net.myorb.math.realnumbers;

import net.myorb.math.expressions.symbols.CommonFunctionBase;

/**
 * common base for real functions
 * @author Michael Druckman
 */
public abstract class CommonRealFunctionBase extends CommonFunctionBase < Double >
{


	/**
	 * @param named the name to give the symbol
	 */
	public CommonRealFunctionBase (String named)
	{
		super (named, "x");
	}


}

