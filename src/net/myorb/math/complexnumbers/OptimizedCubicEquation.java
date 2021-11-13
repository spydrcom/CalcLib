
package net.myorb.math.complexnumbers;

import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

/**
 * use HighSpeedMathLibrary to improve
 *  performance and precision for Double float data types
 * @author Michael Druckman
 */
public class OptimizedCubicEquation extends CubicEquation<Double>
{

	static final HSComplexSupportImplementation hsml =
		new HSComplexSupportImplementation ();			// high speed math library instance
	static final OptimizedComplexLibrary ocl =
		new OptimizedComplexLibrary (hsml);				// optimized complex library based on HS library
	static final DoubleFloatingFieldManager dmgr =
		new DoubleFloatingFieldManager ();				// double float field manager as component type
	static final ComplexFieldManager<Double> cmgr =
		new ComplexFieldManager<Double>(dmgr);			// complex manager based on Double
	public OptimizedCubicEquation ()
	{ super (cmgr, ocl);}

	/**
	 * unit test driver
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		new OptimizedCubicEquation ().test ();
	}

}

