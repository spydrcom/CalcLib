
package net.myorb.math.computational;

import net.myorb.math.expressions.ValueManager;

/**
 * meta-data structures added to value stack entries
 * 	to pass calculus algorithm computation parameters
 * @author Michael Druckman
 */
public class CalculusMarkers
{

	public enum CalculusMarkerTypes
	{
		INTERVAL,
		QUADRATURE_CLENSHAW, QUADRATURE_TANH_SINH,
		QUADRATURE_TRAP_EVAL, QUADRATURE_TRAP_ADJUST,
		DERIVATIVE
	}

	/**
	 * metadata marker for calculus operators
	 */
	public interface CalculusMetadata extends ValueManager.Metadata 
	{
		CalculusMarkerTypes typeOfOperation ();
	}


	/**
	 * metadata marker for interval evaluation
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

