
package net.myorb.math.expressions.charting;

import net.myorb.charting.DisplayGraphTypes.Point;
import net.myorb.math.MultiDimensional;

/**
 * 3D plot control for contour style plots
 * @param <T> data type for plot
 * @author Michael Druckman
 */
public class Plot3DContour<T> extends Plot3D<T>
{


	public Plot3DContour
		(
			MultiDimensional.Function<T> equation
		)
	{
		super (); this.setEquation (equation);
		this.setEdgeSize (0); this.setAltEdgeSize (0);
		this.setLowCorner (new Point ());
	}
	public Plot3DContour () { super (); }


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run ()
	{
		this.setEquation (this);
		this.setPlotNumber (1000);
		this.setTransformIdentity (EQUATION_IDENTITY);
		int ps = DisplayGraph3D.appropriatePointSize (ContourPlotEdgeSize);
		DisplayGraph3D.plotContour (setScale (ContourPlotEdgeSize, ps), title);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.Plot3D#getPlotEdgeSize()
	 */
	public int getPlotEdgeSize () { return ContourPlotEdgeSize; }
	public static void setContourPlotEdgeSize (int to) { ContourPlotEdgeSize = to; }
	public static int getContourPlotEdgeSize () { return ContourPlotEdgeSize; }
	static int ContourPlotEdgeSize = 100;


	private static final long serialVersionUID = -4422802179545296180L;
}

