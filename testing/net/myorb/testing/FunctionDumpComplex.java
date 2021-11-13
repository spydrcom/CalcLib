
package net.myorb.testing;

import net.myorb.math.expressions.charting.DisplayGraph;
import net.myorb.charting.DisplayGraphTypes.Point;

import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

import net.myorb.math.complexnumbers.ComplexFieldManager;
import net.myorb.math.complexnumbers.CoordinateSystems;
import net.myorb.math.complexnumbers.ComplexLibrary;
import net.myorb.math.complexnumbers.ComplexValue;

public class FunctionDumpComplex extends ComplexLibrary<Double>
{

	public FunctionDumpComplex ()
	{
		super (new DoubleFloatingFieldManager (), null);
	}

	protected ComplexFieldManager<Double> complexmanager =
		new ComplexFieldManager<Double> (manager);
	CoordinateSystems<Double> cs = new CoordinateSystems<Double> (manager);
	ComplexValue<Double> MINUS_ONE = complexmanager.newScalar (-1);

	double pi = 3.1415926;
	double delta = pi / 64; // pi / 3;

	public void run ()
	{
		float c = 0.4f;
		DisplayGraph.PlotCollection funcPlot = new DisplayGraph.PlotCollection ();
		DisplayGraph.Colors colors = new DisplayGraph.Colors ();

		for (double theta = delta; theta <= pi/2-delta; theta += delta)
		{
			Point.Series points = new Point.Series ();
			for (double r = -2; r < 2; r += 0.01)
			{
				ComplexValue<Double> z = cs.newPolarInstance (r, theta).toComplexValue ();
				ComplexValue<Double> zSq = complexmanager.negate (complexmanager.multiply (z, z));
				ComplexValue<Double> zSqM1 = complexmanager.add (zSq, MINUS_ONE);
				ComplexValue<Double> zSqM1Pz = complexmanager.add (zSqM1, z);
				
				points.add (new DisplayGraph.Point (zSqM1Pz.Re(), zSqM1Pz.Im()));
				//System.out.println (z + "\t" + zSqM1Pz);
			}
			colors.add (java.awt.Color.getHSBColor (c, c, c)); c += 0.01f;
			funcPlot.add (points);
		}

		DisplayGraph.plot (colors, funcPlot, "-x^2 + x - 1");
	}

	public static void main(String[] args)
	{
		new FunctionDumpComplex ().run ();
	}

}
