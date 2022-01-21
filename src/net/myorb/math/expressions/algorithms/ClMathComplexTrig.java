
package net.myorb.math.expressions.algorithms;

import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.complexnumbers.ComplexAlgorithmAccess;

import net.myorb.math.expressions.SymbolMap;

/**
 * Implementation of TrigPow library for use with complex numbers (ComplexValue).
 * @author Michael Druckman
 */
public class ClMathComplexTrig extends ClMathTrig<ComplexValue<Double>>
	implements SymbolMap.FactoryForImports
{

	public ClMathComplexTrig ()
	{
		super (ComplexAlgorithmAccess.getTrigPowImplementation ());
		this.setFactory (ClMathComplexTrig.class.getCanonicalName ());
	}

}
