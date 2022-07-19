
package net.myorb.math.expressions.charting.multidimensional;

import net.myorb.math.expressions.charting.ContourPlotProperties;

/**
 * computer for brute force serial function evaluation
 * @author Michael Druckman
 */
public class SerialCalculationComputer extends PlotMatrixTraversal
{

	public SerialCalculationComputer (ContourPlotProperties proprties)
	{
		super (proprties);
		proprties.getActivityDescriptor ().setProducer ("SerialCalculationComputer");
		proprties.getActivityDescriptor ().setDescription ("Serial 3D Plot");
	}

	/**
	 * @param x coordinates to domain point (x-axis)
	 * @param y coordinates to domain point (y-axis)
	 * @return function evaluation at coordinates
	 */
	double eval (double x, double y)
	{
		try { return proprties.evaluateReal (x, y); }
		catch (Exception e) { return 0; }
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.PlotMatrixTraversal#processPoint(double, double)
	 */
	public void processPoint (double x, double y)
	{
		identify3DPoint (x, y, eval (x, y));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.ResultCollector#compute()
	 */
	public void compute ()
	{
		allocate ();
		/*
		 * run function calls to map (x,y) to z results
		 */
		//System.out.println ("SerialCalculationComputer invoked");
		traverseMatrix ();
	}

}
