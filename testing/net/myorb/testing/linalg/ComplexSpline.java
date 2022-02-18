
package net.myorb.testing.linalg;

import net.myorb.math.expressions.managers.*;
import net.myorb.math.complexnumbers.*;
import net.myorb.math.computational.*;
import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

import net.myorb.data.abstractions.SpaceDescription;

public class ComplexSpline extends CommonComplex
{

	static double LO = 0, HI = 20;

	public static void main (String[] a) throws Exception
	{
		Gamma G = new Gamma ();
		VC31ComponentSpline<ComplexValue<Double>> s =
				new VC31ComponentSpline<ComplexValue<Double>> (G, mgr, null);
		s.addSegment (LO, HI);

		System.out.println ();
		ComplexValue<Double> integralApprox = integralOf (G);
		System.out.println ("TSQ f: " + integralApprox.toString () + " = GAMMA (5+5i)");
		// -0.97439524180523907 + i*2.0066898827226298  === 6.66 * ( -0.146 + 0.3j )
		// (-0.7899469746553125 + 0.15526327173426324*i)
		// (-0.4399712490253549 + 0.7095952274041064*i)

		System.out.println ();
		ComplexValue<Double> lo = C (LO, 0.0), hi = C (HI, 0.0);
		ComplexValue<Double> integralEvaluation = s.evalIntegral (hi);
		ComplexValue<Double> integralEvaluation2 = s.evalIntegral (lo, hi);
		System.out.println ("CHEB Calculus spline (hi): " + integralEvaluation);
		System.out.println ("CHEB Calculus spline (lo,hi): " + integralEvaluation2);

		System.out.println ();
		SplineQuad sq = new SplineQuad (s, LO);
		ComplexValue<Double> integral = sq.integrate
				(
					VC31ComponentSpline.ComponentSpline.SPLINE_LO,
					VC31ComponentSpline.ComponentSpline.SPLINE_HI
				);
		System.out.println ("spline TSQ ( -1.5 .. 1.5 ) = " + integral);
		ComplexValue<Double> timesSlope = mgr.multiply (integral, C (20.0/3, 0.0));
		System.out.println (" * 6.66 : " + timesSlope);

		System.out.println ();
		System.out.println ("TSQ spline ( 0 .. 20 ): " + integralOf (s).toString ());
	}

	static ComplexValue<Double> integralOf (Function<ComplexValue<Double>> f)
	{
		return integralOf (f, LO, HI);
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
