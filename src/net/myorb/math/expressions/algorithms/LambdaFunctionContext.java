
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.TypedRangeDescription;
import net.myorb.math.expressions.ExpressionComponentElaboration;
import net.myorb.math.expressions.ExpressionComponentSpaceManager;

import net.myorb.math.expressions.charting.MultiComponentUtilities;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.charting.DisplayGraphTypes;

import net.myorb.math.Function;

import java.util.List;

/**
 * Vector enabled wrapper for Lambda functions
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class LambdaFunctionContext <T>
	implements LambdaFunctionPlotter.VectorEnabledContext <T>
{


	public LambdaFunctionContext
		(
			List < Function <T> > functions,
			LambdaFunctionPlotter <T> plotter
		)
	{
		this.functions = functions; this.plotter = plotter;
	}
	protected LambdaFunctionPlotter <T> plotter;
	protected List < Function <T> > functions;
	
	
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.VectorPlotEnabled#evaluateSeries(net.myorb.math.expressions.TypedRangeDescription.TypedRangeProperties, java.util.List, net.myorb.math.expressions.evaluationstates.Environment)
	 */
	public void evaluateSeries
		(
			TypedRangeDescription.TypedRangeProperties <T> domainDescription,
			List <DisplayGraphTypes.Point.Series> series,
			Environment <T> environment
		)
	{
		ExpressionComponentSpaceManager <T>
			mgr = (ExpressionComponentSpaceManager <T>)
				environment.getSpaceManager ();
		ExpressionComponentElaboration.evaluateSeries
		(
			(x, s) ->
			{
				double domain = mgr.component (x, 0);

				for (int n = 0; n < s.size (); n++)
				{
					T y = functions.get (n).eval (x);
					
					s.get (n).add
					(
						new DisplayGraphTypes.Point (domain, mgr.convertToDouble (y))
					);
				}
			},
			domainDescription, series, mgr
		);
	}
	
	
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.MultiDimensionalUtilities.ContextProperties#assignColors(net.myorb.charting.DisplayGraphTypes.Colors)
	 */
	public void assignColors (DisplayGraphTypes.Colors colors)
	{
		MultiComponentUtilities.assignStandardColors (colors, getComponentCount ());
	}
	
	
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.MultiDimensionalUtilities.ContextProperties#componentIdentifiers()
	 */
	public String[] componentIdentifiers ()
	{
		return plotter.getLambdaList ();
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.MultiDimensionalUtilities.ContextProperties#getComponentCount()
	 */
	public int getComponentCount () { return functions.size (); }
	
	
}

