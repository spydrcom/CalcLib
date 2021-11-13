
package net.myorb.math.complexnumbers;

import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

/**
 * use optimized Library to improve
 *  performance and precision for Double float data types
 * @author Michael Druckman
 */
public class OptimizedComplexLibrary extends ComplexLibrary<Double>
{


	/**
	 * use DoubleFloatingFieldManager
	 * @param optimizedMathLibrary root library to be used
	 */
	public OptimizedComplexLibrary (ComplexSupportLibrary<Double> optimizedMathLibrary)
	{ super (new DoubleFloatingFieldManager (), null); setLibrary (this.optimizedMathLibrary = optimizedMathLibrary); }
	protected ComplexSupportLibrary<Double> optimizedMathLibrary;


	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.Arithmetic#getMathLib()
	 */
	public ComplexSupportLibrary<Double> getMathLib ()
	{
		return optimizedMathLibrary;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.ComplexLibrary#exp(net.myorb.math.complexnumbers.ComplexValue)
	 */
	public ComplexValue<Double> exp (ComplexValue<Double> z)
	{
		return rCis (z);
	}


}
