
package net.myorb.math.computational;

import net.myorb.math.expressions.ValueManager;

/**
 * meta-data structures added to value stack entries
 * 	to pass calculus algorithm computation parameters
 * @author Michael Druckman
 */
public class CalculusMarkers
{


	/**
	 * metadata marker for calculus operators
	 */
	public interface CalculusMetadata extends ValueManager.Metadata {}


	/**
	 * metadata marker for interval evaluation
	 */
	public static class IntervalEvaluationMarker implements CalculusMetadata {}


	/**
	 * markers for quadrature
	 */
	public static class ClenshawCurtisEvaluationMarker implements CalculusMetadata {}
	public static class TrapezoidalEvaluationMarker implements CalculusMetadata {}
	public static class TrapezoidalAdjustmentMarker implements CalculusMetadata {}
	public static class TanhSinhEvaluationMarker implements CalculusMetadata {}


	/**
	 * parameterization meta-data for evaluation of function derivative
	 * @param <T> type on which operations are to be executed
	 */
	public interface DerivativeMetadata<T>
		extends CalculusMetadata
	{
		/**
		 * @return number of derivatives to apply
		 */
		int getCount ();

		/**
		 * @return TRUE = approximation
		 */
		boolean usesApproximation ();

		/**
		 * @param order number of derivatives to apply
		 */
		void setCount (int order);

		/**
		 * @return LIM value to use for approximation
		 */
		T getDelta ();
	}


}

