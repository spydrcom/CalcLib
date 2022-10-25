
package net.myorb.math.realnumbers;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.data.abstractions.FunctionWrapper;

import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

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
		super (f, manager);
	}

	/**
	 * @return a core application function object
	 */
	public Function <Double> toCommonFunction ()
	{
		return new Function <Double> ()
		{
			/* (non-Javadoc)
			 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
			 */
			public Double eval (Double x)
			{ return f.body (x); }

			/* (non-Javadoc)
			 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
			 */
			public SpaceDescription <Double> getSpaceDescription ()
			{ return manager; }

			/* (non-Javadoc)
			 * @see net.myorb.math.Function#getSpaceManager()
			 */
			public SpaceManager <Double> getSpaceManager ()
			{ return manager; }
		};
	}

	public static final ExpressionComponentSpaceManager <Double> manager = new ExpressionFloatingFieldManager ();
}
