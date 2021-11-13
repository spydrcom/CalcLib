
package net.myorb.math.expressions.charting.fractals;

import net.myorb.math.expressions.charting.DisplayGraph3D;
import net.myorb.charting.DisplayGraphTypes.Point;

import net.myorb.math.expressions.charting.colormappings.ContourColorSchemeRequest;
import net.myorb.charting.ColorSelection;

/**
 * display plots of the Mandelbrot set
 * @author Michael Druckman
 */
public class Mandelbrot extends Fractal implements Fractal.Descriptor
{


	public static final String MANDELBROT = "Mandelbrot";


	/**
	 * limits that indicate divergence
	 */
	public static final int MAX_ITERATIONS = 3000;
	public static final float DIVERGENCE_THRESHOLD = 8;


	/**
	 * full view coordinates
	 */
	public static final Point fullViewLowCorner = new Point (-2.5f, -2.5f);
	public static final float fullViewEdgeSize = 5;


	/**
	 * default full size view
	 */
	public Mandelbrot () { this (fullViewLowCorner, fullViewEdgeSize); }


	/**
	 * view selected using low corner and edge size
	 * @param lowCorner the low corner is x-min/y-min
	 * @param edgeSize distance along each axis from low corner
	 */
	public Mandelbrot (Point lowCorner, float edgeSize)
	{
		super (lowCorner, edgeSize, MAX_ITERATIONS);
		setCurrentLimit (DIVERGENCE_THRESHOLD);
	}


	/**
	 * compute the iteration
	 *  count for specified point
	 * @param x0 the X coordinate
	 * @param y0 the Y coordinate
	 * @return the iteration count
	 */
	public int computeIterationsFor (double x0, double y0)
	{
		int iteration = 0;
		double x = 0.0f, y = 0.0f, xn = 0.0f, yn = 0.0f, value;
		while (iteration < getMaxResult ())
		{
			x = xn; y = yn;
			value = x*x + y*y;
			if (value > currentLimit) break;
			xn = x*x - y*y + x0;
			yn = 2*x*y + y0;
			iteration++;
		}
		return iteration;
	}


	/**
	 * construct plot of area defined by corner and edge
	 * @param lowCorner the point of the low x/y corner of the area
	 * @param axisWidth the width of x-axis (and height of y-axis)
	 * @param pointsPerAxis the resolution in pixels per axis
	 * @param pointSize the pixel width/height of a point
	 */
	public static void plot
		(
			Point lowCorner, float axisWidth,
			int pointsPerAxis, int pointSize
		)
	{
		DisplayGraph3D.plotContour
		(
			new Mandelbrot (lowCorner, axisWidth)
				.setScale (pointsPerAxis, pointSize),
			standardTag (MANDELBROT, lowCorner, axisWidth)
		);
	}


	/**
	 * the outer-most view of the set
	 * @param pointsPerAxis the resolution of the plot in points per axis
	 * @param pointSize the pixel size of each point
	 */
	public static void fullSizeView (int pointsPerAxis, int pointSize)
	{
		DisplayGraph3D.plotContour
		(
			new Mandelbrot ().setScale (pointsPerAxis, pointSize),
			"Mandelbrot Full Set View"
		);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.fractals.Fractal#getFractalName()
	 */
	public String getFractalName () { return MANDELBROT; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.fractals.Fractal#reconstitute(java.lang.String)
	 */
	public Fractal reconstitute (String fractalName)
	{
		return new Mandelbrot ();
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		return standardTag (MANDELBROT, getLowCorner (), getEdgeSize ());
	}


	/**
	 * @param fractalName the identity of a transform
	 * @return TRUE for Mandelbrot
	 */
	public static boolean isMember (String fractalName)
	{
		return MANDELBROT.equals (fractalName);
	}


	/**
	 * get collection of Mandelbrot sets
	 * @return map of included sets
	 */
	public static FractalMap getFractalMap ()
	{
		FractalMap map = new FractalMap ();
		map.addNamed (new Mandelbrot ().setViewArea (-0.30882352590560913, -0.7941176295280457, 0.14705882966518402));
		map.addNamed (new Mandelbrot ().setViewArea (-0.23745673894882202, -0.6611158847808838, 0.010596886277198792));
		map.addNamed (new Mandelbrot ().setViewArea (-0.22145327925682068, -0.6552767753601074, 0.011245667934417725));
		map.addNamed (new Mandelbrot ().setViewArea (-0.21907185018062592, -0.6551279425621033, 0.005027487874031067));
		map.addNamed (new Mandelbrot ().setViewArea (-0.21843601763248444, -0.65171217918396, 9.759217500686646E-4));
		map.addNamed (new Mandelbrot ().setViewArea (-0.21830397844314575, -0.6516432762145996, 1.9086897373199463E-4));
		map.addNamed (new Mandelbrot ().setViewArea (0.026340840384364128, 0.630903959274292, 0.008628904819488525));
		map.addNamed (new Mandelbrot ().setViewArea (-0.6102941036224365, -0.7720588445663452, 0.20588234066963196));
		map.addNamed (new Mandelbrot ().setViewArea (9.313225746154785E-9, 0.625, 0.10294117592275143));
		map.addNamed (new Mandelbrot ());
		return map;
	}


	/**
	 * unit test the algorithms
	 * @param args not used
	 */
	public static void main (String[] args)
	{
		new ColorSchemeRequest ();
	}
	static class ColorSchemeRequest extends ContourColorSchemeRequest
	{
		public String formatNotificationFor
		(ColorSelection.Factory selectedItem) { return null; }
		public void setSelectedItem (ColorSelection.Factory item)
		{ super.setSelectedItem (item); fullSizeView (400, 5); }
	}


	private static final long serialVersionUID = 7061660309891199291L;
}

