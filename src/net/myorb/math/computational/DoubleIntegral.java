
package net.myorb.math.computational;

import net.myorb.math.MultiDimensional;
import net.myorb.math.Function;

import java.util.List;

/**
 * approximation of double integral of 2D function.
 *  details taken from "Calculus Concepts and Contexts" by James Stewart.
 *  Section 12.1, Double Integrals Over Rectangles.
 * @author Michael Druckman
 */
public class DoubleIntegral
		extends MultiDimensionalRealIntegralSupport
	implements MultiDimensionalIntegral<Double>
{


	/**
	 * @param integrand the function used to collect samples
	 * @param xDelta the distance between sample on the x axis
	 * @param yDelta the distance between sample on the y axis
	 */
	public DoubleIntegral
		(
			MultiDimensional.Function<Double> integrand,
			double xDelta, double yDelta
		)
	{
		super (integrand);
		this.setDeltas (xDelta, yDelta);
	}


	public DoubleIntegral
		(
			MultiDimensional.Function<Double> integrand
		)
	{
		super (integrand);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.MultiDimensionalIntegral#setDeltas(java.util.List)
	 */
	public void setDeltas (List<Double> deltas)
	{
		super.setDeltas (deltas); verify (deltas);
		this.xDelta = deltas.get (0); this.yDelta = deltas.get (1);
		this.xMidPoint = xDelta / 2; this.yMidPoint = yDelta / 2;
	}
	protected double xMidPoint, yMidPoint;
	protected double xDelta, yDelta;


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.MultiDimensionalIntegral#computeApproximation(java.util.List, java.util.List)
	 */
	public Double computeApproximation (List<Double> lo, List<Double> hi)
	{
		forceDeltaSet (2);
		verify (lo); verify (hi);

		return computeApproximation
		(
			lo.get (0), hi.get (0),
			lo.get (1), hi.get (1)
		);
	}


	/**
	 * actual double integral approximation
	 * @param xLo lo end of the x axis interval
	 * @param xHi hi end of the x axis interval
	 * @param yLo lo end of the y axis interval
	 * @param yHi hi end of the y axis interval
	 * @return approximate integral value
	 */
	public Double computeApproximation
	(double xLo, double xHi, double yLo, double yHi)
	{
		forceDeltaSet (2);

		double
			x = xLo + xMidPoint,
			y = yLo + yMidPoint;
		double accumulation = 0.0;

		xLo = x;
		while (y < yHi)
		{
			x = xLo; 
	
			while (x < xHi)
			{
				accumulation += evaluateIntegrandAt (x, y);
				x += xDelta;
			}
	
			y += yDelta;
		}

		return accumulation * unitContribution;
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
				public MultiDimensionalIntegral<Double> newMultiDimensionalIntegral (Function<Double> integrand)
				{ throw new RuntimeException ("Single dimension integration not supported"); }
			};
	}


	/**
	 * @param integrand function to be integrated
	 * @return a new instance of this integral implementation
	 */
	public static MultiDimensionalIntegral<Double> newInstance (MultiDimensional.Function<Double> integrand)
	{ return new DoubleIntegral (integrand); }


}

