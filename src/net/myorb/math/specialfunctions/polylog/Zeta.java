
package net.myorb.math.specialfunctions.polylog;

import net.myorb.math.specialfunctions.PolylogRatioFormulas;

import net.myorb.math.complexnumbers.ComplexValue;

/**
 * zeta function computed from Amdeberhan integral
 * @author Michael Druckman
 */
public class Zeta extends Eta
{


	public Zeta () { super ("zeta"); }


	/*
	 * implementation of function
	 */

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public ComplexValue<Double> eval (ComplexValue<Double> z)
	{
		return PolylogRatioFormulas.zetaFromEta (z, super.eval (z));
	}


}

