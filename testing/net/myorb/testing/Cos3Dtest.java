
package net.myorb.testing;

import net.myorb.math.expressions.charting.Plot3DContour;
import net.myorb.charting.DisplayGraphTypes.Point;

import net.myorb.math.expressions.charting.colormappings.ContourColorSchemeRequest;
import net.myorb.charting.ColorSelection;

import net.myorb.math.MultiDimensional;
import net.myorb.math.SpaceManager;

import java.util.List;

/**
 * contour plot test
 */
public class Cos3Dtest extends Plot3DContour<Double>
{
	Cos3Dtest ()
	{
		//super (new Cos3D (), 1000.0, new Point (-1.6, -1.6), 3.2f, 3.2f);
		super (new Cos3D ());
		this.setMultiplier (1000);
		this.setLowCorner(new Point (-1.6, -1.6));
		this.setAltEdgeSize (3.2f);
		this.setEdgeSize (3.2f);
	}
	static void plotIt ()
	{
		new Cos3Dtest ().show ("cos (y * exp (x-y) / x)");
	}
	static class RequestForCos extends ContourColorSchemeRequest
	{
		public void setSelectedItem (ColorSelection.Factory item)
		{ super.setSelectedItem (item); Cos3Dtest.plotIt (); }
	}
	public static void main (String[] args) { new RequestForCos (); }
	private static final long serialVersionUID = 5853607803727484320L;
}

class Cos3D implements MultiDimensional.Function<Double>
{
	public Double f (Double... p) { return f (p[0], p[1]); }
	public Double f (List<Double> dataPoint) { return f (dataPoint.get (0), dataPoint.get (1)); }
	public double f (double x, double y) { return Math.cos (y * Math.exp (x-y) / x); }
	public SpaceManager<Double> getSpaceDescription () { return null; }
}

