
package net.myorb.math.expressions.charting.multidimensional;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.charting.ContourPlotProperties;
import net.myorb.math.expressions.charting.PlotComputers;
import net.myorb.math.computational.SurfaceAnalyzer;

import net.myorb.charting.DisplayGraphTypes;

/**
 * a computation processing engine for a vectored transform
 * @param <T> data type for plot
 * @author Michael Druckman
 */
public class VectoredComputer<T> extends VectoredTransform<T>
	implements PlotComputers.TransformProcessing
{


	public VectoredComputer
	(ContourPlotProperties proprties, Environment<T> environment)
	{
		super (proprties, environment);
		this.allocate ();
	}


	/**
	 * initialize computer using ContourPlotProperties
	 */
	public void init ()
	{
		/*
		 * call to get transform from properties object
		 */
		this.initTransform ();

		/*
		 * call to initialize position tracking
		 */
		this.init
		(
			proprties.getLowCorner (),
			proprties.getEdgeSize (),
			proprties.getAltEdgeSize ()
		);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.ResultCollector#compute()
	 */
	public void compute ()
	{
		/*
		 * compute Z axis results vector-by-vector
		 */
		init ();

		System.out.println ("VectoredComputer invoked");

		traverseMatrix
		(
			x0, xinc,
			y0, yinc
		);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.multidimensional.PlotMatrixTraversal#processYAxis(double, double, double)
	 */
	public void processYAxis (double x, double y0, double incrementY)
	{
		updateVector (x);
		super.processYAxis (x, y0, incrementY);
		evalTransform (); fillZ (s0, s1);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.multidimensional.PlotMatrixTraversal#processPoint(double, double)
	 */
	public void processPoint (double x, double y)
	{
		identifyDomainPoint (x, y);
	}


	/**
	 * @param ps0 first of two component values forming transform of domain point
	 * @param ps1 second of two component values forming transform of domain point
	 */
	void fillZ
		(
			DisplayGraphTypes.Point ps0, 
			DisplayGraphTypes.Point ps1
		)
	{
		if (
				! ps0.outOfRange && ! ps1.outOfRange &&
				! SurfaceAnalyzer.isApproachingInfinite (ps0.y) &&
				! SurfaceAnalyzer.isApproachingInfinite (ps1.y)
			)
		{
			double
			s0 = ps0.y, s1 = ps1.y, magnitude = s0*s0 + s1*s1;
			Zs0[x_index][y_index] = s0; Zs1[x_index][y_index] = s1;
			if (SurfaceAnalyzer.isApproachingInfinite (magnitude))
			{ Z[x_index][y_index] = SurfaceAnalyzer.APPROACHES_INFINITY; }
			else { Z[x_index][y_index] = magnitude; }
		}
		else
		{
			Zs0[x_index][y_index] = SurfaceAnalyzer.APPROACHES_INFINITY;
			Zs1[x_index][y_index] = SurfaceAnalyzer.APPROACHES_INFINITY;
		}
	}
	void fillZ (DisplayGraphTypes.Point.Series s0, DisplayGraphTypes.Point.Series s1)
	{ for (y_index = 0; y_index < pointsPerAxis; y_index++) fillZ (s0.get (y_index), s1.get (y_index)); }


}

