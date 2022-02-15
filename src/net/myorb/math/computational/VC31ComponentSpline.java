
package net.myorb.math.computational;

import net.myorb.math.GeneratingFunctions;
import net.myorb.math.expressions.ExpressionComponentSpaceManager;
import net.myorb.math.matrices.Vector;
import net.myorb.math.Function;

import java.util.ArrayList;

public class VC31ComponentSpline<T>
{

	public VC31ComponentSpline
		(
			Function<T> f,
			ExpressionComponentSpaceManager<T> mgr,
			Parameterization configuration
		)
	{
		this.f = f;
		this.mgr = mgr;
		this.configuration = configuration;
		this.lud = new VC31LUD (configuration);
	}
	Function<T> f;
	Parameterization configuration;
	ExpressionComponentSpaceManager<T> mgr;
	VC31LUD lud;

	public void addSegment (double lo, double hi)
	{
		double range = hi - lo;
		double delta = range / 20;
		double bottom = lo - 5*delta;
		double x = bottom;

		ArrayList<GeneratingFunctions.Coefficients<Double>> coefficients =
			new ArrayList<GeneratingFunctions.Coefficients<Double>>();
		ArrayList<T> points = new ArrayList<T>();

		for (int i = 0; i < 31; i++)
		{
			points.add (f.eval (mgr.convertFromDouble (x)));
			x += delta;
		}

		int components = mgr.getComponentCount ();
		Vector<Double> v = new Vector<Double>(31, VC31LUD.mgr);

		for (int c = 0; c < components; c++)
		{
			for (int i = 1; i < 32; i++)
			{ v.set (i, mgr.component (points.get (i-1), c)); }
			coefficients.add (lud.solve (v));
		}
	}

}
