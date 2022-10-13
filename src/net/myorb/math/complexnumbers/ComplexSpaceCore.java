
package net.myorb.math.complexnumbers;

import net.myorb.math.expressions.managers.ExpressionComplexFieldManager;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

import net.myorb.data.abstractions.FunctionWrapper;

/**
 * components required for working in complex space
 * @author Michael Druckman
 */
public class ComplexSpaceCore
{


	/**
	 * wrapper interface specific to complex functions
	 */
	public interface ComplexFunction
	extends FunctionWrapper.F <ComplexValue<Double>>
	{}


	/*
	 * managers for data types
	 */

	public static ExpressionFloatingFieldManager realMgr = new ExpressionFloatingFieldManager ();
	public static ExpressionComplexFieldManager manager = new ExpressionComplexFieldManager ();
	public static ComplexLibrary<Double> cplxLib;


	/*
	 * most efficient form of the complex library configurations
	 */

	static
	{
		cplxLib = new ComplexLibrary<Double> (realMgr, manager);
		cplxLib.setMathLib (new JreComplexSupportLibrary (realMgr));
		cplxLib.initializeGamma (15);
	}


}

