
package net.myorb.math.realnumbers;

import net.myorb.data.abstractions.FunctionWrapper;

/**
 * simple lambda expression declaration wrapper for Real functions
 * @author Michael Druckman
 */
public class RealFunctionWrapper extends FunctionWrapper <Double>
{

	/**
	 * wrapper for a Real domain function body
	 */
	public interface RealFunctionBodyWrapper
		extends FunctionWrapper.F <Double>
	{}

	/**
	 * @param f the function to be wrapped
	 */
	public RealFunctionWrapper (RealFunctionBodyWrapper f)
	{
		super (f, new DoubleFloatingFieldManager ());
	}

}
