
package net.myorb.testing;

import net.myorb.math.MultiDimensional;
import net.myorb.math.computational.RungeKutta;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.data.abstractions.SpaceDescription;

import java.util.List;

public class RK_Test
{

	/**
	 * unit test
	 * @param args not used
	 */
	public static void main (String... args)
	{
		System.out.println
		(
			new RungeKutta <Double> (new TestFunction (), 1.0, 0.0, 0.05, 30).doIterations ()
			//new RungeKutta <Double> (new RiccartiTestFunction (), 0.15, 0.0, 0.03, 30).doIterations ()
		);
	}

}


class TestFunction implements MultiDimensional.Function<Double>
{

	@Override public Double f (List<Double> dataPoint)
	{ return eval (dataPoint.get (0), dataPoint.get (1)); }
	@Override public Double f (Double... x) { return eval (x[0], x[1]); }

	public double eval (double t, double y) { return Math.pow (Math.sin (t), 2) * y; }

	@Override public SpaceDescription<Double> getSpaceDescription() { return mgr; }
	ExpressionFloatingFieldManager mgr = new ExpressionFloatingFieldManager ();

}


class RiccartiTestFunction implements MultiDimensional.Function<Double>
{

	//static double a = 3, b = -3; int n = 2;
	static double a = 10, b = -3; int n = 2;

	@Override public Double f (List<Double> dataPoint)
	{ return eval (dataPoint.get (0), dataPoint.get (1)); }
	@Override public Double f (Double... x) { return eval (x[0], x[1]); }

	public double eval (double t, double y) { return a * y*y + b * Math.pow(t,n); }

	@Override public SpaceDescription<Double> getSpaceDescription() { return mgr; }
	ExpressionFloatingFieldManager mgr = new ExpressionFloatingFieldManager ();

}

