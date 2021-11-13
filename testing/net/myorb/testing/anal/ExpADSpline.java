
package net.myorb.testing.anal;

import net.myorb.math.computational.AntiDerivativeSpline;
import net.myorb.math.computational.TanhSinhQuadratureAlgorithms;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

public class ExpADSpline implements AntiDerivativeSpline.SegmentManager<Double>
{

	public static final double[]
	upTo = new double[]{-10, -1, -0.1, -0.000001, -0.0000000001, 0.000001, 0.1, 1, 10},
	area = new double[]
			{
					-4.15696892968E-6,
					-0.2193797774266,
					-1.603540023,
					-11.4153,
					-9.21,
					 9.21,
					 11.615481,
					 3.51793063,
					 2490.333858425
			};

	@Override
	public int getSegmentCount() {
		return area.length;
	}

	@Override
	public Double getFirstSegmentBase() {
		return -100d;
	}

	@Override
	public Double getSegmentHi(int forSegment) {
		return upTo[forSegment];
	}

	@Override
	public Double getSegmentArea(int forSegment) {
		return area[forSegment];
	}

	@Override
	public Double getAreaBetween(Double lo, Double hi) {
		 return TanhSinhQuadratureAlgorithms.Integrate(ei, lo, hi, 1E-4, null);
	}
	EI ei = new EI ();

	public static void main (String[] args)
	{
		AntiDerivativeSpline.SegmentManager<Double> segments = new ExpADSpline ();
		Function<Double> f = new AntiDerivativeSpline<Double> (segments, new ExpressionFloatingFieldManager ());
		for (double x = -2.97; x < 3; x += 0.05) System.out.println (f.eval (x));
	}

}

class EI implements Function<Double>
{
	public Double eval(Double x) { return Math.exp (x) / x; }
	public SpaceManager<Double> getSpaceManager() { return sm; }
	public SpaceManager<Double> getSpaceDescription() { return sm; }
	SpaceManager<Double> sm = new ExpressionFloatingFieldManager ();
}

