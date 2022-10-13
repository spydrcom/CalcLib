
package net.myorb.math.complexnumbers;

import net.myorb.data.abstractions.FunctionWrapper;

/**
 * helper class for working with complex functions
 * @author Michael Druckman
 */
public class ComplexSpaceTestEnvironment extends ComplexSpaceCore
{


	/**
	 * @param f a function body commonly specified using lambda
	 * @return the function as described in the application
	 */
	public static FunctionWrapper <ComplexValue<Double>>
		wrapperFor (ComplexFunction f)
	{
		return new FunctionWrapper <> (f, manager);
	}
	
	/**
	 * a function evaluation passing in value components
	 * @param f function (using wrapper) being tested
	 * @param r the real part of the test value
	 * @param i the real imag of the test value
	 * @return the computed value
	 */
	public static ComplexValue<Double> eval
		(FunctionWrapper <ComplexValue<Double>> f, double r, double i)
	{
		return f.eval (manager.C (r, i));
	}
	
	/**
	 * a function evaluation passing in value components
	 * @param f function (using interface) being tested
	 * @param r the real part of the test value
	 * @param i the real imag of the test value
	 * @return the computed value
	 */
	public static ComplexValue<Double> eval
		(ComplexFunction f, double r, double i)
	{
		return eval (wrapperFor (f), r, i);
	}


}

