
package net.myorb.math.expressions;

import net.myorb.math.expressions.TypedRangeDescription.TypedRangeProperties;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.charting.DisplayGraphTypes.Point.Series;
import net.myorb.charting.DisplayGraphTypes;

import net.myorb.data.abstractions.Function;

import java.util.List;

/**
 * build series from components of values
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class ExpressionAnalysis<T>
{

	/**
	 * construct graph point series
	 *  for values that have multiple components
	 * @param f function used to calculate series
	 * @param domainDescription the parameters of the real domain
	 * @param series the series of points per component to be constructed
	 * @param mgr data type management object
	 */
	public void evaluateSeries
		(
			Function <T> f,
			TypedRangeDescription.TypedRangeProperties <T> domainDescription,
			List <DisplayGraphTypes.Point.Series> series, 
			ExpressionComponentSpaceManager <T> mgr
		)
	{
		ExpressionComponentElaboration.evaluateSeries
		(
			(x, s) ->
			{
				T y = f.eval (x);
				double domain = mgr.component (x, 0);

				for (int n = 0; n < s.size (); n++)
				{
					series.get (n).add
					(
						new DisplayGraphTypes.Point (domain, mgr.component (y, n))
					);
				}

			},
			domainDescription, series, mgr
		);
	}

	/**
	 * @param f the function that requires a wrapper
	 * @return a transform engine that enables vector plot functionality
	 */
	public VectorPlotEnabled<T> getTransformEngine (Function<T> f)
	{
		return new TransformEngine<T> (f, this);
	}

}


/**
 * provide vector enabled plot functionality for generic functions
 * @param <T> type used for calculations
 */
class TransformEngine<T> implements VectorPlotEnabled<T>
{

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.VectorPlotEnabled#evaluateSeries(net.myorb.math.expressions.TypedRangeDescription.TypedRangeProperties, java.util.List, net.myorb.math.expressions.evaluationstates.Environment)
	 */
	public void evaluateSeries
		(
			TypedRangeProperties<T> domainDescription, List<Series> series,
			Environment<T> environment
		)
	{
		ea.evaluateSeries
		(
			f, domainDescription, series,
			( ExpressionComponentSpaceManager <T> ) environment.getSpaceManager ()
		);
	}

	/**
	 * @param f the function to enable
	 * @param ea the Expression Analysis object to use
	 */
	public TransformEngine (Function<T> f, ExpressionAnalysis<T> ea)
	{
		this.ea = ea;
		this.f = f;
	}
	ExpressionAnalysis<T> ea;
	Function<T> f;

}

