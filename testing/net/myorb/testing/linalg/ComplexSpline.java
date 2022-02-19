
package net.myorb.testing.linalg;

import net.myorb.math.expressions.managers.*;
import net.myorb.math.complexnumbers.*;
import net.myorb.math.computational.*;
import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

import net.myorb.data.abstractions.SpaceDescription;

public class ComplexSpline extends CommonComplex
{

	static int segments = 7, segmentSize = 5, base = 0;

	public static void main (String[] a) throws Exception
	{

		Gamma G = new Gamma ();
		VC31ComponentSpline<ComplexValue<Double>> s;
		s = new VC31ComponentSpline<ComplexValue<Double>> (G, mgr, null);

		base = 0;
		for (int snum = 0; snum < segments; snum++)
		{
			s.addSegment (base, base + segmentSize);
			base += segmentSize;
		}


		// quadrature of GAMMA

		System.out.println ();
		System.out.println ("GAMMA (5+5i) = -0.97439524180523907 + 2.0066898827226298*i");
		// -0.97439524180523907 + i*2.0066898827226298 per MPMATH.GAMMA

		System.out.println ();
		System.out.println ("TSQ f: " + integralOf (G));


		// integral of spline polynomial using Chebyshev calculus

		System.out.println ();
		System.out.print ("CHEB Calculus spline: ");
		System.out.println (s.evalIntegral ());

	}

	static ComplexValue<Double> integralOf (Function<ComplexValue<Double>> f)
	{
		ComplexValue<Double> integralApprox = C (0, 0);

		base = 0;
		for (int snum = 0; snum < segments; snum++)
		{
			integralApprox =
				mgr.add
				(
					integralApprox,
					integralOf (f, base, base + segmentSize)
				);
			base += segmentSize;
		}

		return integralApprox;
	}

}

class SplineQuad extends CommonComplex
{

	SplineQuad (VC31ComponentSpline<ComplexValue<Double>> s, double lo)
	{
		scr = new SplineComponent (s, 0, lo);
		sci = new SplineComponent (s, 1, lo);
	}
	SplineComponent scr, sci;

	public ComplexValue<Double> integrate (double lo, double hi)
	{
		double
			tsqr = CommonReal.TSQ (scr, lo, hi),
			tsqi = CommonReal.TSQ (sci, lo, hi);
		return C (tsqr, tsqi);
	}

}

class SplineComponent extends CommonReal implements Function<Double>
{
	SplineComponent (VC31ComponentSpline<ComplexValue<Double>> s, int component, double point)
	{
		cs = s.getComponentSplineFor (component, point);
		model = cs.modelFor (component);
	}
	VC31ComponentSpline.ComponentSpline cs;
	Regression.Model<Double> model;
	
	public Double eval (Double x)
	{
		return model.eval (x);
	}
}

class Component extends CommonReal implements Function<Double>
{

	Component
	(Function<ComplexValue<Double>> f, int part)
	{ this.part = part; this.f = f; }

	public Double eval (Double x)
	{ return component (part, f.eval (cvt (x))); }
	Function<ComplexValue<Double>> f;
	int part;

}

class Gamma extends CommonComplex implements Function<ComplexValue<Double>>
{

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public ComplexValue<Double> eval(ComplexValue<Double> t)
	{
		return mgr.multiply (lib.power (t, Z), lib.exp (mgr.negate (t)));
	}

	ComplexValue<Double> Z = C (4.0, 5.0);

}

class CommonComplex
{
	static ExpressionComplexFieldManager mgr = new ExpressionComplexFieldManager ();
	public static ComplexValue<Double> C (double r, double i) { return mgr.C (r, i); }
	static ComplexLibrary<Double> lib = new ComplexLibrary<Double> (CommonReal.realmgr, mgr);
	public SpaceDescription<ComplexValue<Double>> getSpaceDescription() { return mgr; }
	public SpaceManager<ComplexValue<Double>> getSpaceManager() { return mgr; }

	static ComplexValue<Double> integralOf
	(Function<ComplexValue<Double>> f, double l, double h)
	{
		double
			tsqr = CommonReal.TSQ (new Component (f, 0), l, h),
			tsqi = CommonReal.TSQ (new Component (f, 1), l, h);
		return C (tsqr, tsqi);
	}
}

class CommonReal
{
	public SpaceManager<Double> getSpaceManager() { return realmgr; }
	public SpaceDescription<Double> getSpaceDescription() { return realmgr; }
	static ExpressionFloatingFieldManager realmgr = new ExpressionFloatingFieldManager ();
	
	public static double component (int number, ComplexValue<Double> from)
	{
		switch (number)
		{
			case 0: return from.Re ();
			default: return from.Im ();
		}
	}

	public static ComplexValue<Double> cvt (double x)
	{
		return CommonComplex.C (x, 0.0);
	}

	static double TSQ (Function<Double> f, double l, double h)
	{
		return TanhSinhQuadratureAlgorithms.Integrate (f, l, h, 0.0001, null);
	}
}
