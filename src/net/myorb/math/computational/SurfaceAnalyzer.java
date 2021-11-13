
package net.myorb.math.computational;

import net.myorb.math.expressions.charting.DisplayGraph;
import net.myorb.math.expressions.charting.PlotComputers;

import net.myorb.charting.Histogram;

import java.util.Comparator;
import java.util.ArrayList;

/**
 * process surface data to find low magnitude points
 * @author Michael Druckman
 */
public class SurfaceAnalyzer implements Comparator<PointMag>
{


	/**
	 * @param surface a surface object holding the Z mappings
	 */
	public SurfaceAnalyzer (PlotComputers.TransformResultsCollection surface)
	{
		this.surface = surface;
	}
	PlotComputers.TransformResultsCollection surface;
	ArrayList<PointMag> mags;


	/**
	 * @return a matrix of the magnitude of the points
	 */
	public double [][] computeSurfaceMagnitude ()
	{
		double mag, s0ij, s1ij;

		histogram = new Histogram ();
		mags = new ArrayList<PointMag> ();

		double[][] s0 = surface.getZ (0), s1 = surface.getZ (1);
		double[][] m = new double[s0.length][s0.length];

		for (int i=0; i<m.length; i++)
		{
			for (int j=0; j<m.length; j++)
			{
				s0ij = s0[i][j];
				s1ij = s1[i][j];

				try
				{
					mag = isApproachingInfinite (s0ij) || isApproachingInfinite (s1ij)?
							APPROACHES_INFINITY: s0ij*s0ij + s1ij*s1ij;
					mags.add (new PointMag (mag, i, j));

					if ( ! isApproachingInfinite (mag) && mag < HISTOGRAM_MAX )
					{
						long iMag = (long) (100 * mag);
						histogram.increase (iMag);
					}
				}
				catch (Exception e)
				{
					mag = APPROACHES_INFINITY;
				}
				m[i][j] = mag;
			}
		}

		DisplayGraph.histogramPlot (histogram, "Display Spectrum");

		return m;
	}
	public Histogram getHistogram () { return histogram; }
	double HISTOGRAM_MAX = Long.MAX_VALUE / 100;
	Histogram histogram;


	/**
	 * show points found with lowest magnitude
	 */
	public void report ()
	{
		PointMag p;
		mags.sort (this);
		double[][] X = surface.getX (), Y = surface.getY ();
		for (int i = 0; i < Math.min (mags.size(), 100); i++)
		{
			p = mags.get (i);

			System.out.println
			(
				p.mag + " => (" + 
				X[p.i][p.j] + "," + Y[p.i][p.j] +
				")"
			);
		}
	}


	/**
	 * recognize a value as being a problem
	 * @param value a value to be tested
	 * @return TRUE for flagged problem
	 */
	public static boolean isApproachingInfinite (double value)
	{
		return value >= APPROACHES_INFINITY;
	}
	public static final double APPROACHES_INFINITY = 9E99;


	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare (PointMag p1, PointMag p2)
	{
		return p1.mag == p2.mag ? 0 : p1.mag > p2.mag ? 1 : -1;
	}


}

/**
 * hold points for sorting
 */
class PointMag
{
	PointMag (double mag, int i, int j)
	{ this.mag = mag; this.i = i; this.j = j; }
	double mag; int i, j;
}

