
package net.myorb.testing;

import net.myorb.math.SpaceManager;
import net.myorb.math.MultiDimensional;

import net.myorb.math.expressions.charting.Plot3DContour;
import net.myorb.charting.DisplayGraphTypes.Point;
import net.myorb.charting.ColorSelection;

import net.myorb.math.expressions.charting.colormappings.ContourColorSchemeRequest;

import net.myorb.math.specialfunctions.Gamma;

import java.util.List;

/**
 * contour plot test
 */
public class Beta3Dtest extends Plot3DContour<Double>
{
	Beta3Dtest ()
	{
		//super (new BETA (), 10.0, new Point (1, 1), 4);
		//super (new BETA (), 10.0, new Point (0.5, 0.5), 2);
		//super (new BETA (), 10.0, new Point (-2.9, -2.9), 0.8f);
		//super (new BETA (), 10.0, new Point (-1.9, -1.9), 4.8f);
		//super (new BETA (), 10.0, new Point (-0.9, -0.9), 0.8f);
		//  super (new BETA (), 10.0, new Point (-2.9, -2.9), 5.8f, 5.8f);
		super (new BETA ());
		this.setMultiplier (10);
		this.setLowCorner(new Point (-2.9, -2.9));
		this.setAltEdgeSize (5.8f);
		this.setEdgeSize (5.8f);

	}
	static void plotIt () { new Beta3Dtest ().show ("Beta"); }
	static class RequestForBeta extends ContourColorSchemeRequest
	{
		public void setSelectedItem (ColorSelection.Factory item)
		{ super.setSelectedItem (item); Beta3Dtest.plotIt (); }
	}
	public static void main (String[] args) { new RequestForBeta (); }
	private static final long serialVersionUID = 7203367856655369644L;
}

class BETA implements MultiDimensional.Function<Double>
{
	public Double f (Double... p) { return f (p[0], p[1]); }
	public Double f (List<Double> dataPoint) { return f (dataPoint.get (0), dataPoint.get (1)); }
	public double f (double x, double y) { return (gamma.eval (x) * gamma.eval (y) / gamma.eval (x+y)); }
	public SpaceManager<Double> getSpaceDescription () { return null; }
	private Gamma gamma = new Gamma ();
}
