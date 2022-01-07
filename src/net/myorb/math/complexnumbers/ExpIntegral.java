
package net.myorb.math.complexnumbers;

import net.myorb.math.specialfunctions.Si;

import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

public class ExpIntegral
{

	public static ComplexValue<Double> E1 (ComplexValue<Double> z)
	{
		return new ComplexValue<Double>
		(
			- Si.Ci (z.imagpart),
			Si.si (z.imagpart) - Math.PI/2,
			mgr
		);
	}

	static ExpressionFloatingFieldManager mgr = new ExpressionFloatingFieldManager ();

}
