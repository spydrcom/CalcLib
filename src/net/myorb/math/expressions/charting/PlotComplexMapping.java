
package net.myorb.math.expressions.charting;

import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.complexnumbers.ComplexSpaceCore;

import net.myorb.math.computational.integration.QuadratureEntities;
import net.myorb.math.specialfunctions.LerchIdentities;
import net.myorb.math.specialfunctions.Lerch;

import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import java.awt.*;

/**
 * construct a plot of a function as a mapping
 * - plot lines connect the function parameter to the function result
 * @author Michael Druckman
 */
public class PlotComplexMapping
{


	/**
	 * a point described with Cartesian coordinates
	 */
	static class Point
	{
		public String toString ()
		{ return "(" + x + "," + y + ")"; }
		public Double x, y;
	}


	/**
	 * @param rasterSize number of edge pixels in square raster
	 * @param slices number of slices in a unit circle of 2PI radians
	 * @param portions number of parts dividing a segmented unit of radius
	 * @param segments the number of segments from each angle
	 */
	public PlotComplexMapping
	(int rasterSize, int slices, int portions, int segments)
	{
		this.slices = slices;
		this.segment = 1.0 / portions;
		this.portions = portions; this.segments = segments;
		this.radians = 2 * Math.PI / slices;
		this.raster = rasterSize;
		this.half = raster / 2;
	}
	protected int segments, portions, slices;
	protected double radians, segment;
	protected int raster, half;


	/**
	 * @param f the function as a Target Specification of a Quadrature integrand
	 */
	public void plot
		(
			QuadratureEntities.TargetSpecification < ComplexValue <Double> > f
		)
	{
		double theta = 0, r = segment;
		ComplexValue <Double> z, fOfZ;

		for (int j = segments; j > 0; j--)
		{
			for (int i = slices; i >=0; i--)
			{
				fOfZ = f.body (z = rect (r, theta));
				theta += radians;
				plot (z, fOfZ);
			}
			r += segment;
		}
	}


	/**
	 * translate polar coordinates to Cartesian complex value
	 * @param r the distance from the origin to the point described
	 * @param theta the angle from the x axis to the point described
	 * @return complex value as Cartesian point
	 */
	static ComplexValue <Double> rect (double r, double theta)
	{
		return ComplexSpaceCore.CV
			(
				r * Math.cos (theta), r * Math.sin (theta)
			);
	}


	/**
	 * draw line from parameter to result
	 * - both parameter and result represented as complex values
	 * @param from parameter to function
	 * @param to function result
	 */
	void plot (ComplexValue<Double> from, ComplexValue<Double> to)
	{
//		System.out.println (from + " - " + to);
		plot (map (from), map (to));
	}


	/**
	 * adjust plot origin
	 * @param from the unadjusted point coordinates as complex value
	 * @return the adjusted point
	 */
	Point map (ComplexValue<Double> from)
	{
		Point p = new Point ();
		p.x = from.Re () * half + half;
		p.y = from.Im () * half + half;
		return p;
	}


	/**
	 * draw line from parameter to result
	 * - both parameter and result represented as Cartesian coordinates
	 * @param from parameter to function
	 * @param to function result
	 */
	void plot (Point from, Point to)
	{
		g.setColor
		(
			Color.getHSBColor
			(
				from.x.floatValue (),
				from.y.floatValue (),
				to.x.floatValue ()
			)
		);

//		System.out.println (from + " - " + to);

		g.drawLine
		(
			from.x.intValue (), from.y.intValue (),
			to.x.intValue (), to.y.intValue ()
		);
	}
	protected Graphics2D g;


	/**
	 * prepare the raster objects
	 * @param title text of frame title
	 */
	public void init (String title)
	{
		MouseMotionHandler mouse = new MouseMotionHandler (5, 5, null);
		BufferedImage image = DisplayGraph3D.createBufferedImage (raster);
		c = DisplayGraph3D.showImage (image, title, mouse);
		g = image.createGraphics ();
	}
	protected JComponent c;


	/**
	 * unit test
	 * @param args not used
	 */
	public static void main (String[] args)
	{
		PlotComplexMapping plotter =
			new PlotComplexMapping (800, 12, 60, 20);
		plotter.init ("TEST");

		Lerch.Series LS = new Lerch.Series (Z, H);
		LerchIdentities ID = new LerchIdentities (LS);

		plotter.plot ( (z) -> ID.zeta (z, ComplexSpaceCore.RE (5)) );
	}
	static ComplexValue<Double>
	TWO = ComplexSpaceCore.RE (2),
	H = ComplexSpaceCore.RE (100),
	Z = ComplexSpaceCore.RE (0);


}

