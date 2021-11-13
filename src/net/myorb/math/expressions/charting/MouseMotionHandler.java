
package net.myorb.math.expressions.charting;

import net.myorb.gui.components.DisplayFrame;

import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

/**
 * translate mouse events into tool tips on the chart
 * @author Michael Druckman
 */
public class MouseMotionHandler extends ContourPlotProperties implements MouseMotionListener
{


	MouseMotionHandler () { super (-5); }


	/**
	 * capture scaling data the translates the coordinates
	 * @param scalingX the x-axis multiplier for number of bits on axis
	 * @param xAxisWidth the length of the axis in bits (offset to zero)
	 * @param f access to the function being plotted
	 */
	public MouseMotionHandler (double scalingX, int xAxisWidth, DisplayGraph.RealFunction f)
	{
		this ();
		setScale (scalingX, xAxisWidth);
		this.transform = f;
	}
	protected DisplayGraph.RealFunction transform;


	public void setScale (double scalingX, int xAxisWidth)
	{
		this.scalingX = scalingX;
		this.xAxisOffset = (xAxisWidth + DisplayFrame.MARGIN) / 2;
		this.xAxisWidth = xAxisWidth / 2;
	}
	protected double scalingX, xAxisOffset, xAxisWidth;


	/**
	 * hold the width of the component to use in coordinate translation
	 * @param width the width of the component
	 */
	public void setWidth (int width) { xAxisWidth = width; }


	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent e) {}


	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved (MouseEvent e)
	{ fieldEvent (e.getX (), e.getY ()); }


	/**
	 * field a mouse event and translate to tool tip
	 * @param x the x-axis coordinate from the GUI
	 * @param y y-axis coordinate
	 */
	void fieldEvent (int x, int y)
	{
		if (c != null)
		{
			double xValue;
			if (tooSoon ()) return;
			String text = "x = " + (xValue = translateXAxis (x));

			try
			{
				if (transform != null)
				{
					Double f = transform.eval (xValue);
					if (f != null) text += ", F(x) = " + f;
				}
			}
			catch (Exception e)
			{ if (firstSeen) { firstSeen = false; e.printStackTrace (); } }
			c.setToolTipText (text);
		}
	}
	boolean tooSoon ()
	{
		long current =
			System.currentTimeMillis ();
		if (current < nextAllowed) return true;  // mouse events coming too quickly
		nextAllowed = current + DELAY;
		return false;
	}
	static final int DELAY = 50;
	protected boolean firstSeen = true;
	protected long nextAllowed = 0;


	/**
	 * apply scale factors to adjust x-axis value
	 * @param x value on x-axis to be translated to function coordinates
	 * @return the x-axis value equivalent
	 */
	protected double translateXAxis (double x) { return scalingX * (x - xAxisOffset) / xAxisWidth; }


	/**
	 * capture the display component
	 * @param c the swing component holding the chart
	 */
	public void set (JComponent c) { this.c = c; }
	protected JComponent c;


	private static final long serialVersionUID = -1647997745272525124L;
}

