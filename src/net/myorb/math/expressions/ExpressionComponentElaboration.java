
package net.myorb.math.expressions;

import net.myorb.charting.DisplayGraphTypes;

import java.util.List;

/**
 * domain processing for operations that produce series
 * @author Michael Druckman
 */
public class ExpressionComponentElaboration
{


	/**
	 * the operator to be executed
	 * @param <T> the data type used
	 */
	public interface Operation <T>
	{

		/**
		 * the operation for each domain value
		 * @param domain a domain element to operate on
		 * @param series the list of point series to add to
		 */
		void execute (T domain, List <DisplayGraphTypes.Point.Series> series);

	}


	/**
	 * process the range of a domain and produce the series of range values
	 * @param op the operation being executed to generate the series of range values
	 * @param domainDescription the description of the domain of operations
	 * @param series the container of the series being generated
	 * @param mgr a data type manager for the operations
	 */
	public static <T> void evaluateSeries
		(
			Operation <T> op,
			TypedRangeDescription.TypedRangeProperties <T> domainDescription,
			List <DisplayGraphTypes.Point.Series> series, 
			ExpressionComponentSpaceManager <T> mgr
		)
	{

		T
			x = domainDescription.getTypedLo (),
			inc = domainDescription.getTypedIncrement (),
			hi = domainDescription.getTypedHi ();
	
		while ( ! mgr.lessThan (hi, x) )
		{

			op.execute (x, series);

			x = mgr.add (x, inc);

		}

	}


}

