
package net.myorb.math.computational;

import net.myorb.math.MultiDimensional;
import net.myorb.math.Function;

/**
 * integral objects based on Tanh-Sinh Quadrature
 * @author Michael Druckman
 */
public class TSQIntegration
		extends MultiDimensionalRealIntegralSupport
	implements MultiDimensionalIntegral<Double>
{


	public TSQIntegration (Function<Double> integrand) { super (integrand); }


	/* (non-Javadoc)
	* @see net.myorb.math.computational.MultiDimensionalIntegral#computeApproximation(java.lang.Object, java.lang.Object)
	*/
	public Double computeApproximation (Double lo, Double hi)
	{
		double targetAbsoluteError = Math.pow (10, -level.doubleValue ());
		return TanhSinhQuadratureAlgorithms.Integrate (integrand1D, lo, hi, targetAbsoluteError, null);
	}
	
	/**
	* @return new factory instance for objects of this integral implementation
	*/
	public static MultiDimensionalIntegralEngineFactory<Double> newFactoryInstance ()
	{
		return new MultiDimensionalIntegralEngineFactory<Double>()
			{
		
				/* (non-Javadoc)
				 * @see net.myorb.math.computational.MultiDimensionalIntegralEngineFactory#newMultiDimensionalIntegral(net.myorb.math.MultiDimensional.Function)
				 */
				public MultiDimensionalIntegral<Double> newMultiDimensionalIntegral (MultiDimensional.Function<Double> integrand) { return newInstance (integrand); }
		
				/* (non-Javadoc)
				 * @see net.myorb.math.computational.MultiDimensionalIntegralEngineFactory#newMultiDimensionalIntegral(net.myorb.math.MultiDimensional.Function)
				 */
				public MultiDimensionalIntegral<Double> newMultiDimensionalIntegral (Function<Double> integrand) { return newInstance (integrand); }
		
			};
	}
	
	/**
	* @param integrand the function to be integrated
	* @return a new instance of this integral implementation
	*/
	public static MultiDimensionalIntegral<Double> newInstance (MultiDimensional.Function<Double> integrand)
	{ return MultiDimensionalRealQuadrature.newFactoryInstance (newFactoryInstance ()).newMultiDimensionalIntegral (integrand); }
	
	/**
	* @param integrand the function to be integrated
	* @return a new instance of this integral implementation
	*/
	public static MultiDimensionalIntegral<Double> newInstance (Function<Double> integrand) { return new TSQIntegration (integrand); }

}
