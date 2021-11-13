
package net.myorb.math.expressions.charting;

import net.myorb.math.MultiDimensional;

/**
 * special case for mesh/membrane type plots
 * @param <T> data type for plot
 * @author Michael Druckman
 */
public class Plot3DMesh<T> extends Plot3D<T>
{


	/**
	 * @param equation multi-dimensional function descriptor
	 */
	public Plot3DMesh
		(
			MultiDimensional.Function<T> equation
		)
	{
		super (); this.setEquation (equation);
	}
	public Plot3DMesh () { super (); }


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run ()
	{
		this.setEquation (this);
		this.setPlotNumber (1000);
		this.setTransformIdentity (EQUATION_IDENTITY);
		DisplayGraph3D.plotMesh (setScale (MeshPlotEdgeSize, 3), title);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.Plot3D#getPlotEdgeSize()
	 */
	public int getPlotEdgeSize () { return MeshPlotEdgeSize; }
	public static void setMeshPlotEdgeSize (int to) { MeshPlotEdgeSize = to; }
	public static int getMeshPlotEdgeSize () { return MeshPlotEdgeSize; }
	static int MeshPlotEdgeSize = 30;


	private static final long serialVersionUID = -8578133631973995104L;
}
