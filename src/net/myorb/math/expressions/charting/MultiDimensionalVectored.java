
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;
import net.myorb.math.expressions.VectorPlotEnabled;
import net.myorb.math.expressions.SymbolMap;

/**
 * implementation of multi-dimensional transform realization
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class MultiDimensionalVectored <T>
{


	/**
	 * @param functionSymbol must represent a VectorPlotEnabled function
	 * @param environment an environment descriptor
	 */
	public MultiDimensionalVectored
		(
			SymbolMap.Named functionSymbol,
			Environment<T> environment
		)
	{
		this.environment = environment;
		this.mgr = (ExpressionComponentSpaceManager<T>) environment.getSpaceManager ();
		this.identifyTransform (functionSymbol);
	}
	protected ExpressionComponentSpaceManager<T> mgr;


	public Environment<T> getEnvironment () { return environment; }
	protected Environment<T> environment;


	/**
	 * @param configuredSize the edge size to use (Points-Per-Axis)
	 */
	public void setResolution
	(int configuredSize) { pointsPerAxis = configuredSize; }
	protected int pointsPerAxis = 100;


	/**
	 * @param transform a named symbol expected to be vector enabled
	 */
	@SuppressWarnings("unchecked")
	public void identifyTransform (SymbolMap.Named transform) { this.transform = (VectorPlotEnabled<T>) transform; }
	public VectorPlotEnabled<T> getVectorPlotEnabledTransform () { return transform; }
	protected VectorPlotEnabled<T> transform;


}

