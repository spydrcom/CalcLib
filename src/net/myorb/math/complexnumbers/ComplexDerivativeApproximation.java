
package net.myorb.math.complexnumbers;

import net.myorb.math.computational.DerivativeApproximation;
import net.myorb.math.computational.DerivativeApproximationEngine;

import net.myorb.data.abstractions.Function;
import net.myorb.math.SpaceManager;

/**
 * computational algorithms for derivative approximation on complex functions
 * @author Michael Druckman
 */
public class ComplexDerivativeApproximation
	extends DerivativeApproximation<ComplexValue<Double>>
	implements DerivativeApproximationEngine<ComplexValue<Double>>
{


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.DerivativeApproximationEngine#approximateDerivativesFor(net.myorb.data.abstractions.Function, java.lang.Object)
	 */
	public DerivativeApproximation.Functions<ComplexValue<Double>> approximateDerivativesFor
		(
			Function<ComplexValue<Double>> f, ComplexValue<Double> delta
		)
	{
		return getDerivativeApproximationFunctions (f, delta);
	}


	public ComplexDerivativeApproximation
		(SpaceManager<ComplexValue<Double>> sm)
	{ super (sm); setComponentManager (sm); }

	@SuppressWarnings("unchecked")
	void setComponentManager (SpaceManager<ComplexValue<Double>> sm)
	{ cm = sm.getComponentManager (); i = new ComplexValue<Double> (0.0, 1.0, cm); }
	SpaceManager<Double> cm; ComplexValue<Double> i;


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.DerivativeApproximation#firstOrderDerivative(net.myorb.data.abstractions.Function, java.lang.Object, java.lang.Object)
	 */
	public ComplexValue<Double> firstOrderDerivative
	(Function<ComplexValue<Double>> op, ComplexValue<Double> x, ComplexValue<Double> delta)
	{ return computeDerivative (op, 1, x, delta); }


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.DerivativeApproximation#secondOrderDerivative(net.myorb.data.abstractions.Function, java.lang.Object, java.lang.Object)
	 */
	public ComplexValue<Double> secondOrderDerivative
	(Function<ComplexValue<Double>> op, ComplexValue<Double> x, ComplexValue<Double> delta)
	{ return computeDerivative (op, 2, x, delta); }


	/**
	 * @param op the function
	 * @param order the number of derivatives
	 * @param x the point at which to compute
	 * @param delta the delta value for the approximation
	 * @return the computed derivative value
	 */
	public ComplexValue<Double> computeDerivative
		(
			Function<ComplexValue<Double>> op, int order,
			ComplexValue<Double> x, ComplexValue<Double> delta
		)
	{
		ComplexValue<Double> realPartial, imagPartial;
		realPartial = ddx (op, order, x, delta); super.setDimensionMultiplier (i);
		imagPartial = ddx (op, order, x, delta); super.resetDimensionMultiplier ();
		return new ComplexValue<Double> (realPartial.Re (), imagPartial.Im (), cm);
	}


	/**
	 * @param op the function
	 * @param order the number of derivatives
	 * @param x the point at which to compute
	 * @param delta the delta value for the approximation
	 * @return the computed derivative value
	 */
	public ComplexValue<Double> ddx
		(
			Function<ComplexValue<Double>> op, int order,
			ComplexValue<Double> x, ComplexValue<Double> delta
		)
	{
		switch (order)
		{
			case 1: return super.firstOrderDerivative (op, x, delta);
			case 2: return super.secondOrderDerivative (op, x, delta);
		}
		return null;
	}


}

