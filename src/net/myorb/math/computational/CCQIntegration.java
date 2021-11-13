
package net.myorb.math.computational;

import net.myorb.math.MultiDimensional;
import net.myorb.math.Function;

/**
 * integral objects based on Clenshaw-Curtis Quadrature
 * @author Michael Druckman
 */
public class CCQIntegration
		extends MultiDimensionalRealIntegralSupport
	implements MultiDimensionalIntegral<Double>
{


	public CCQIntegration (Function<Double> integrand) { super (integrand); }


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.MultiDimensionalIntegral#computeApproximation(java.lang.Object, java.lang.Object)
	 */
	public Double computeApproximation (Double lo, Double hi)
	{
		ClenshawCurtisQuadrature<Double> ccq = new ClenshawCurtisQuadrature<Double> (tmgr);
		return ClenshawCurtisQuadrature.integrate (ccq.getTransform (integrand1D, lo, hi, 10 * level.intValue (), DCT.Type.I));
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
	public static MultiDimensionalIntegral<Double> newInstance (Function<Double> integrand) { return new CCQIntegration (integrand); }

}
