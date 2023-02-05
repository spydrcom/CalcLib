
package net.myorb.testing;

import net.myorb.math.specialfunctions.Lerch;
import net.myorb.math.specialfunctions.LerchIdentities;

//import net.myorb.math.computational.integration.polylog.JonquierePolylog;
//import net.myorb.math.specialfunctions.Beta;

import net.myorb.math.expressions.charting.*;
import net.myorb.math.complexnumbers.*;

import javax.swing.JComponent;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;

public class PlotTest
{


	static int
		raster = 800, half = raster/2,
		segments = 40, portions = 30, slices = 24;
	static double radians = 2 * Math.PI / slices;
	static double segment = 1.0 / portions;


	static class Point
	{
		Double x, y;
		public String toString ()
		{
			return "(" + x + "," + y + ")";
		}
	}


	public static void main (String[] args)
	{
		MouseMotionHandler mouse = new MouseMotionHandler (5, 5, null);
		BufferedImage image = DisplayGraph3D.createBufferedImage (raster);
		c = DisplayGraph3D.showImage (image, "TEST", mouse);
		g = image.createGraphics ();
		plot ();
	}
	static JComponent c;


	static void plot ()
	{
		double theta = 0, r = segment;
		ComplexValue <Double> z, fOfZ;

		for (int j = segments; j > 0; j--)
		{
			for (int i = slices; i >=0; i--)
			{
				fOfZ = eval (z = rect (r, theta));
				theta += radians;
				plot (z, fOfZ);
			}
			r += segment;
		}
	}


	static ComplexValue<Double> eval (ComplexValue<Double> z)
	{
		//return Beta.eval (z, 100);
		//return JonquierePolylog.Li (1, 100).eval (z);
		//return JonquierePolylog.Li2 (z, 100);
		return ID.Ti (TWO, z);
	}
	static ComplexValue<Double>
		TWO = ComplexSpaceCore.RE (2),
		ORDER = ComplexSpaceCore.RE (2),
		H = ComplexSpaceCore.RE (100),
		Z = ComplexSpaceCore.RE (0);
	static Lerch.Series LS = new Lerch.Series (Z, H);
	static LerchIdentities ID = new LerchIdentities (LS);


	static ComplexValue<Double> rect (double r, double theta)
	{
		return ComplexSpaceCore.CV
			(
				r * Math.cos (theta), r * Math.sin (theta)
			);
	}


	static void plot (ComplexValue<Double> from, ComplexValue<Double> to)
	{
//		System.out.println (from + " - " + to);
		plot (map (from), map (to));
	}


	static Point map (ComplexValue<Double> from)
	{
		Point p = new Point ();
		p.x = from.Re () * half + half;
		p.y = from.Im () * half + half;
		return p;
	}


	static void plot (Point from, Point to)
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
	static Graphics2D g;


}

