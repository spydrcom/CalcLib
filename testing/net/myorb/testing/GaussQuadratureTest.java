
package net.myorb.testing;

import net.myorb.math.Polynomial;

import net.myorb.math.computational.GaussQuadrature;
import net.myorb.math.computational.LagrangeInterpolation;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.charting.RegressionCharts;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

import net.myorb.data.abstractions.DataSequence2D;

public class GaussQuadratureTest extends GaussQuadrature<Double>
{

	/*
	 * 
		x = (-3.0, -2.0, 1.0, 4.0, 5.0)
		y = (4, -7.1, 9.5, -13.5, 16.2)
	 *
	 */

	GaussQuadratureTest (ExpressionSpaceManager<Double> sm)
	{
		super (sm);
	}

	static GaussQuadratureTest newInstance ()
	{
		ExpressionSpaceManager<Double> sm =
			new ExpressionFloatingFieldManager ();
		return new GaussQuadratureTest (sm);
	}

	static void test ()
	{
		DataSequence2D<Double>
			dataSet = new DataSequence2D<Double> ();
		dataSet.addSample ( -3.0,   4.0 );
		dataSet.addSample ( -2.0,  -7.1 );
		dataSet.addSample (  1.0,   9.5 );
		dataSet.addSample (  4.0, -13.5 );
		dataSet.addSample (  5.0,  16.2 );

		GaussQuadratureTest t = newInstance ();

		System.out.println ("Q = " + t.evaluateIntegral (dataSet, t.spaceManager.newScalar (-1), t.spaceManager.newScalar (2)));

		test ("Calculus", dataSet, t.lagrangeInterpolation (dataSet));

		test ("Algebra", dataSet, new LagrangeInterpolation<Double> (t.spaceManager).forSequence(dataSet));
	}


	static void test (String title, DataSequence2D<Double> dataSet, Polynomial.PowerFunction<Double> f)
	{
		System.out.println ("=================================");
		System.out.println (title); System.out.println (f);
		System.out.println ("=================================");
		
		for (int i = 0; i < dataSet.xAxis.size (); i++)
		{
			double y = f.eval (dataSet.xAxis.get (i));
			double error = dataSet.yAxis.get (i) - y;
			System.out.println (error);
		}
		System.out.println ("=================================");

		new RegressionCharts<Double>(f.getSpaceManager()).chartRegression (dataSet, f, title);
	}


	public static void main (String[] args)
	{
		test ();
	}

}
