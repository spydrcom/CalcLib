
package net.myorb.math.expressions.charting;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

/**
 * support a zoom plot functionality
 * @author Michael Druckman
 */
public class MouseEventHandler extends MouseMotionHandler implements MouseListener
{

	public MouseEventHandler (double scalingX, int xAxisWidth, DisplayGraph.RealFunction f)
	{
		super  (scalingX, xAxisWidth, f);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked (MouseEvent e) {}
	public void mouseEntered (MouseEvent e) {}
	public void mouseExited  (MouseEvent e) {}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed (MouseEvent e)
	{
		pressedAt = translateXAxis (e.getX ());
	}
	private double pressedAt;

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e)
	{
		releasedAt = translateXAxis (e.getX ());
		//System.out.println ("pressedAt=" + pressedAt + "   releasedAt=" + releasedAt);
		System.out.println ("X-Axis Selection:  (" + pressedAt + ", " + releasedAt + ")");
		System.out.println ();
		zoomPlot ();
	}
	private double releasedAt;

	/**
	 * compute the zoom graph domain
	 * @return list of values for the x-axis
	 */
	DisplayGraph.RealSeries getZoomDomain ()
	{
		double lo, hi, inc;
		if (releasedAt > pressedAt)
		{ lo = pressedAt; hi = releasedAt; }
		else  { hi = pressedAt; lo = releasedAt; }
		inc = (lo - hi) / 100; if (inc < 0) inc = -inc;
		return MultiFunctionPlot.domain (lo, hi, inc);
	}

	/**
	 * produce plot of zoom on x-axis
	 */
	void zoomPlot ()
	{
		MultiFunctionPlot.PlotDescriptors zoom = MultiFunctionPlot.newPlotDescriptor
			(700, "ZOOM " + pressedAt + " - " + releasedAt, getZoomDomain ());
		MultiFunctionPlot.addFunctionPlot (zoom, "WHITE", transform);
		MultiFunctionPlot.addFunctionPlot (zoom, "GREEN", null);
		zoom.setDerivative (true);
		MultiFunctionPlot.plot (zoom);
	}

	private static final long serialVersionUID = 8841447510771092357L;
}


