
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.VectorPlotEnabled;
import net.myorb.math.expressions.ConventionalNotations;

import net.myorb.math.expressions.evaluationstates.ArrayDescriptor;
import net.myorb.math.expressions.evaluationstates.Environment;

/**
 * utilities for multi-dimensional transform realization
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class MultiDimensionalUtilities <T> extends MultiComponentUtilities <T>
{


	public MultiDimensionalUtilities
		(
			ContextProperties contextProperties,
			Environment <T> environment
		)
	{
		super (contextProperties, environment);
	}


	/**
	 * multi-dimensional Vector Enabled function plot
	 * @param functionName name of the function to use as a title
	 * @param transform a vector enabled transform for plot computations
	 * @param domainDescription a descriptor of the plot domain
	 */
	public void multiDimensionalFunctionPlot
		(
			String functionName,
			VectorPlotEnabled <T> transform,
			ArrayDescriptor <T> domainDescription
		)
	{
		multiComponentPlot
			(
				functionName,
				
				ConventionalNotations.determineNotationFor
				(
					domainDescription.getVariable ()
				),

				evaluateSeries
				(
					transform, domainDescription
				)
			);
	}


}

