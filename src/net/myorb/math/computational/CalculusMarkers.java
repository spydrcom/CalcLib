
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
	 * markers for built-in forms of quadrature
	 */
	public enum CalculusMarkerTypes
	{
		INTERVAL,										// interval calculation between domain points
		QUADRATURE_CLENSHAW, QUADRATURE_TANH_SINH,		// Curtis-Clenshaw and Double Exponential quadrature forms
		QUADRATURE_TRAP_EVAL, QUADRATURE_TRAP_ADJUST,	// Trapezoidal rule evaluation and post-sum adjust
		DERIVATIVE										// simple function derivative approximation
	}


	/**
	 * metadata marker for calculus operators
	 */
	public interface CalculusMetadata extends ValueManager.Metadata 
	{
		/**
		 * @return enumerated element which identifies data set
		 */
		CalculusMarkerTypes typeOfOperation ();
	}


	/**
	 * meta-data marker for interval evaluation
	 */
	public static class IntervalEvaluationMarker implements CalculusMetadata
	{
		public CalculusMarkerTypes typeOfOperation ()
		{
			return CalculusMarkerTypes.INTERVAL;
		}
	}


	/**
	 * markers for quadrature
	 */
	public static class ClenshawCurtisEvaluationMarker implements CalculusMetadata
	{
		public CalculusMarkerTypes typeOfOperation ()
		{
			return CalculusMarkerTypes.QUADRATURE_CLENSHAW;
		}
	}
	public static class TrapezoidalEvaluationMarker implements CalculusMetadata
	{
		public CalculusMarkerTypes typeOfOperation ()
		{
			return CalculusMarkerTypes.QUADRATURE_TRAP_EVAL;
		}
	}
	public static class TrapezoidalAdjustmentMarker implements CalculusMetadata
	{
		public CalculusMarkerTypes typeOfOperation ()
		{
			return CalculusMarkerTypes.QUADRATURE_TRAP_ADJUST;
		}
	}
	public static class TanhSinhEvaluationMarker implements CalculusMetadata
	{
		public CalculusMarkerTypes typeOfOperation ()
		{
			return CalculusMarkerTypes.QUADRATURE_TANH_SINH;
		}
	}


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

