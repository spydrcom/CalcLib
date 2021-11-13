
package net.myorb.math.expressions.charting.multidimensional;

import net.myorb.math.expressions.charting.ContourPlotProperties;
import net.myorb.math.expressions.charting.PlotComputers;

/**
 * base class holding results collection
 * @author Michael Druckman
 */
class ResultCollector implements PlotComputers.TransformProcessing
{


	protected ResultCollector (ContourPlotProperties proprties)
	{
		this.proprties = proprties;
	}
	protected ContourPlotProperties proprties;


	/**
	 * build resultsCollection
	 */
	protected void allocate ()
	{
		this.pointsPerAxis = proprties.getPointsPerAxis ();
		this.resultsCollection = new TwoComponentResultsCollection (pointsPerAxis);
		this.Zs0 = resultsCollection.getZ (0); this.Zs1 = resultsCollection.getZ (1);
		this.X = resultsCollection.getX (); this.Y = resultsCollection.getY ();
		this.Z = resultsCollection.getZ ();
	}
	protected PlotComputers.TransformResultsCollection resultsCollection;
	protected double[][] X, Y, Z, Zs0, Zs1;
	protected int pointsPerAxis;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.PlotComputers.TransformProcessing#executeTransform()
	 */
	public PlotComputers.TransformResultsCollection executeTransform ()
	{ compute (); return resultsCollection; }


	/**
	 * must be overridden to compute and set results Collection
	 */
	public void compute () {}


}

