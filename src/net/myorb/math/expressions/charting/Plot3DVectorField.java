
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.evaluationstates.Subroutine;
import net.myorb.charting.DisplayGraphTypes.Point;

/**
 * 3D plot control for vector field style plots
 * @param <T> data type for plot
 * @author Michael Druckman
 */
public class Plot3DVectorField <T> extends Plot3D <T>
{

	public Plot3DVectorField
		(
			Subroutine <T> equation, Double vectorCount
		)
	{
		super (); this.setEquation (equation);
		this.setEdgeSize (0); this.setAltEdgeSize (0);
		this.vectorCount = vectorCount.intValue ();
		this.setLowCorner (new Point ());
	}
	public Plot3DVectorField () { super (); }
	protected int vectorCount;


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
	static int ContourPlotEdgeSize = 200;

	private static final long serialVersionUID = 6489115687060243925L;

}
