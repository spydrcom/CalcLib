
package net.myorb.math.expressions.algorithms;

import net.myorb.math.complexnumbers.ComplexAlgorithmAccess;
import net.myorb.math.complexnumbers.ComplexValue;

/**
 * a library of trig-pow primitives for complex numbers
 * @author Michael Druckman
 */
public class TrigPowCmplxPrimitive extends TrigPowPrimitives<ComplexValue<Double>>
{


	/**
	 * constant managers are passed to super constructor.
	 *  TrigPowImplementation is provided using ComplexLibrary Quarks implementation
	 */
	public TrigPowCmplxPrimitive ()
	{
		super
		(
			ComplexAlgorithmAccess.getSpaceManager (),
			ComplexAlgorithmAccess.getValueManager (),
			ComplexAlgorithmAccess.getTrigPowImplementation ()
		);
	}


}

