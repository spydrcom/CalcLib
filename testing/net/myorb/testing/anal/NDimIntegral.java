
package net.myorb.testing.anal;

import net.myorb.math.computational.*;

import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.data.abstractions.SimpleUtilities;
import net.myorb.math.MultiDimensional;
import net.myorb.math.SpaceManager;

import java.util.List;

/**
 * unit test for MultiDimensionalIntegral implementers
 * @author Michael Druckman
 */
public class NDimIntegral implements MultiDimensional.Function<Double>
{

	static Double DELTA = 1E-4, DELTAS[] = new Double[]{DELTA, DELTA};
	static Double LO[] = new Double[]{0.0, 0.0}, HI[] = new Double[]{2.0, 2.0};
	static List<Double> LO_LIST = SimpleUtilities.toList (LO), HI_LIST = SimpleUtilities.toList (HI);

	public static void main (String[] args)
	{
		NDimIntegral integrand = new NDimIntegral ();
		test ("TS Quadrature", TSQIntegration.newInstance (integrand));
		test ("CC Quadrature", CCQIntegration.newInstance (integrand));
		test ("Double Integral", DoubleIntegral.newInstance  (integrand));
		test ("Generic", MultiDimensionalRealIntegral.newInstance (integrand));
	}

	public static void test (String name, MultiDimensionalIntegral<Double> integral)
	{
		System.out.println (name);
		long stamp = System.currentTimeMillis ();
		integral.setDeltas (SimpleUtilities.toList (DELTAS));
		System.out.println (integral.computeApproximation (LO_LIST, HI_LIST));
		long time = System.currentTimeMillis () - stamp;
		System.out.println (time+"ms");
		System.out.println ("===");
	}

	public SpaceManager<Double> getSpaceDescription () { return mgr; }
	public Double f (List<Double> p) { return f (p.get (0), p.get (1)); }
	public Double f (Double... x) { return 16 - x[0]*x[0] - 2 * x[1]*x[1]; }
	ExpressionFloatingFieldManager mgr = new ExpressionFloatingFieldManager ();

}
