
package net.myorb.math.expressions.charting.multidimensional;

import net.myorb.math.expressions.charting.PlotComputers;

/**
 * storage matrix objects allocated for computed results
 * @author Michael Druckman
 */
public class TwoComponentResultsCollection
	implements PlotComputers.TransformResultsCollection
{


	public TwoComponentResultsCollection (int pointsPerAxis)
	{
		this.pointsPerAxis = pointsPerAxis;
		allocate ();
	}
	protected int pointsPerAxis;


	/**
	 * allocate the arrays which will hold the results
	 */
	protected void allocate ()
	{
		X = new double[pointsPerAxis][pointsPerAxis];
		Y = new double[pointsPerAxis][pointsPerAxis];
		Z = new double[pointsPerAxis][pointsPerAxis];
		Zs0 = new double[pointsPerAxis][pointsPerAxis];
		Zs1 = new double[pointsPerAxis][pointsPerAxis];
	}
	protected double[][] X, Y, Z, Zs0, Zs1;


	/*
	 * access matrix objects holding description of results
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.PlotComputers.TransformResultsCollection#getX()
	 */
	public double[][] getX () { return X; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.PlotComputers.TransformResultsCollection#getY()
	 */
	public double[][] getY () { return Y; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.PlotComputers.TransformResultsCollection#getZ()
	 */
	public double[][] getZ () { return Z; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.PlotComputers.TransformResultsCollection#getZ(int)
	 */
	public double[][] getZ (int n)
	{
		return n==0? Zs0: Zs1;
	}


}

