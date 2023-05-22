
package net.myorb.math.expressions.charting;

import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

/**
 * perform coordinate translations for mouse events
 * @author Michael Druckman
 */
public class OffAxisHandler extends MouseMotionHandler
	implements MouseListener, MouseMotionListener
{


	public static final int COMPONENT_EDGE_MARGIN = DisplayGraph3D.MARGIN;						// 20
	public static final int FULL_COMPONENT_EDGE = DisplayGraph3D.DEFAULT_PLOT_SIZE;				// 700
	public static final int FULL_PLOT_EDGE = FULL_COMPONENT_EDGE - COMPONENT_EDGE_MARGIN;		// 680
	public static final int MARGIN_PER_EDGE = COMPONENT_EDGE_MARGIN / 2;						// 10


	/**
	 * @param lowCorner the low corner coordinates of the plot
	 * @param axisLength the length of the axis for specified resolution
	 */
	public OffAxisHandler (DisplayGraph.Point lowCorner, float axisLength)
	{
		setLowCorner (lowCorner); setEdgeSize (axisLength);
	}


	/*
	 * establishing parameters describing plot
	 */


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.MouseMotionHandler#setWidth(int)
	 */
	public void setWidth (int width)
	{
		plotEdgePixels = width;
		plotEdgeUnit = getEdgeSize () / width;
		rasterEdgeUnit = getEdgeSize () / FULL_PLOT_EDGE;
	}
	protected int plotEdgePixels; protected float plotEdgeUnit, rasterEdgeUnit;


	/*
	 * 2D point abstraction (x,y) for plot coordinates, both mouse and plot based
	 */


	/**
	 * convert mouse event to graph point
	 * @param e the mouse event from the runtime
	 * @return a point holding X/Y coordinates
	 */
	DisplayGraph.Point pointOfEvent (MouseEvent e)
	{
		return new DisplayGraph.Point (e.getX (), e.getY ());
	}


	/*
	 * coordinate translation between mouse position and plot axis values
	 */


	/**
	 * translate mouse coordinates to plot coordinates
	 * @param p a point object with the mouse coordinates
	 * @return a point with the translated graph coordinates
	 */
	public DisplayGraph.Point translate (DisplayGraph.Point p)
	{
		check (p.x); check (p.y);
		DisplayGraph.Point lowCorner = getLowCorner ();
		float x = (float)(lowCorner.x + horizontalOffset (p));
		float y = (float)(lowCorner.y + verticalOffset (p));
		return new DisplayGraph.Point (x, y);
	}
	double verticalOffset (DisplayGraph.Point p)
	{
		return rasterEdgeUnit * verticalAdjust (p.y);
	}
	double verticalAdjust (double coordinate)
	{
		return marginAdjust (FULL_COMPONENT_EDGE - coordinate);
	}
	double horizontalOffset (DisplayGraph.Point p)
	{
		return rasterEdgeUnit * marginAdjust (p.x);
	}
	double marginAdjust (double coordinate)
	{
		return coordinate - MARGIN_PER_EDGE;
	}


	/**
	 * discard events where mouse is in plot margin areas
	 * @param v a coordinate of the mouse position
	 */
	void check (double v)
	{
		if
			(
				v < MARGIN_PER_EDGE ||
				v > FULL_PLOT_EDGE+MARGIN_PER_EDGE
			)
		{ throw new RuntimeException (); }
	}


	/*
	 * tool tip mechanism for mouse movement
	 */


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.MouseMotionHandler#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved (MouseEvent e)
	{ fieldEvent (pointOfEvent (e)); }


	/**
	 * mouse motion is used to provide a tooltip with coordinates
	 * @param p the mouse coordinates point from the event
	 */
	void fieldEvent (DisplayGraph.Point p)
	{
		if (c == null) return;
		try { c.setToolTipText (formatTip (p)); }
		catch (Exception e) { c.setToolTipText (""); }
	}


	/**
	 * @param p the location of the mouse
	 * @return text to display for tool tip
	 */
	String formatTip (DisplayGraph.Point p)
	{
		StringBuffer b = new StringBuffer ();
		if (INCLUDE_NATIVE) b.append (p).append (" --- ");
		b.append (translate (p));
		return b.toString ();
	}
	static final boolean INCLUDE_NATIVE = false;


	/*
	 * capture parameters for zoom event
	 */


	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed (MouseEvent e)
	{
		if ( e.isAltDown () ) pressedAt = null;
		else pressedAt = pointOfEvent (e);
	}
	protected DisplayGraph.Point pressedAt;


	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased (MouseEvent e)
	{
		try
		{
			if ( pressedAt == null ) return;

			releasedAt = pointOfEvent (e);
			
			if (TRACE_ZOOM)
			{
				System.out.println ();
				System.out.println ("Area Selection:  " + pressedAt + " - " + releasedAt);
				System.out.println ("Area Selection:  " + translate (pressedAt) + " - " + translate (releasedAt));
			}

			computeArea ();

			if (TRACE_ZOOM)
			{
				System.out.println ("    Normalized:  " + getLowCorner() + "  Edge:  " + getEdgeSize ());
				System.out.println ();
			}

			processArea ();
		}
		catch (Exception ex) {}
	}
	boolean TRACE_ZOOM = false;


	/**
	 * translate a mouse drag to a selected area
	 */
	public void computeArea ()
	{
		try
		{
			DisplayGraph.Point
			start = translate (pressedAt),
			stop = translate (releasedAt);

			// lowest x and lowest y determine lowCorner
			double loX = start.x < stop.x ? start.x : stop.x;
			double loY = start.y < stop.y ? start.y : stop.y;

			// difference of x and y coordinates 
			//		determine edge size
			double xLen = start.x - stop.x;
			double yLen = start.y - stop.y;

			// choose larger of edge computations
			xLen = xLen < 0 ? -xLen : xLen;
			yLen = yLen < 0 ? -yLen : yLen;

			// lowCorner and edge size specify plot view area
			setLowCorner ( new DisplayGraph.Point (loX, loY) );
			setEdgeSize ( (float) (xLen > yLen ? xLen : yLen) );
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	protected DisplayGraph.Point releasedAt;


	/**
	 * the hook to be used by an extender of this class
	 */
	public void processArea () {}


	/*
	 * irrelevant mouse handler interface events
	 */


	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked (MouseEvent e) {}
	public void mouseDragged (MouseEvent e) {}
	public void mouseEntered (MouseEvent e) {}
	public void mouseExited  (MouseEvent e) {}


	private static final long serialVersionUID = -8856078053841566718L;
}

