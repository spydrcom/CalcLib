
package net.myorb.math.expressions.algorithms;

import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.complexnumbers.ComplexAlgorithmAccess;

/**
 * Implementation of TrigPow library for use with complex numbers (ComplexValue).
 * @author Michael Druckman
 */
public class ClMathComplexTrig extends ClMathTrig<ComplexValue<Double>>
{

	public ClMathComplexTrig ()
	{
		super (ComplexAlgorithmAccess.getTrigPowImplementation ());
	}

}
