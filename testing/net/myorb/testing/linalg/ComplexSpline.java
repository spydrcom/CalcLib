
package net.myorb.testing.linalg;

import net.myorb.math.expressions.managers.*;
import net.myorb.math.complexnumbers.*;
import net.myorb.math.computational.*;
import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

import net.myorb.data.abstractions.SpaceDescription;

import java.util.ArrayList;

public class ComplexSpline extends CommonComplex
{


	static class Segment
	{
		Segment (double base, double size)
		{ this.base = base; this.size = size; }
		double base, size;
	}
	static ArrayList<Segment> segments = new ArrayList<Segment>();


	public static double addSegments
		(
			VC31ComponentSpline<ComplexValue<Double>> s,
			double starting, int count, double size
		)
	{
		double base = starting;
		for (int snum = 0; snum < count; snum++)
		{
			segments.add (new Segment (base, size));
			s.addSegment (base, base + size);
			base += size;
		}
		return base;
	}


	public static void main (String[] a) throws Exception
	{

		Gamma G = new Gamma ();
		VC31ComponentSpline<ComplexValue<Double>> s;
		s = new VC31ComponentSpline<ComplexValue<Double>> (G, mgr, null);

		double next = 0.0;
		next = addSegments (s, next, 10, 1E-19);
		next = addSegments (s, next, 1, 9E-18);
		next = addSegments (s, next, 1, 9E-17);
		next = addSegments (s, next, 1, 9E-16);
		next = addSegments (s, next, 1, 9E-15);
		next = addSegments (s, next, 1, 9E-14);
		next = addSegments (s, next, 1, 9E-13);
		next = addSegments (s, next, 1, 9E-12);
		next = addSegments (s, next, 1, 9E-11);
		next = addSegments (s, next, 1, 9E-10);
		next = addSegments (s, next, 1, 9E-9);
		next = addSegments (s, next, 1, 9E-8);
		next = addSegments (s, next, 1, 9E-7);
		next = addSegments (s, next, 4, 1E-6);
		next = addSegments (s, next, 1, 5E-6);
//		next = addSegments (s, next, 1, 0.0000001);
//		next = addSegments (s, next, 1, 0.0000009);
//		next = addSegments (s, next, 1, 0.000009);
		next = addSegments (s, next, 3, 0.00003);
//		next = addSegments (s, next, 1, 0.00009);
		next = addSegments (s, next, 3, 0.0003);
		next = addSegments (s, next, 4, 0.001);
		next = addSegments (s, next, 2, 0.01);
		next = addSegments (s, next, 1, 0.025);
		next = addSegments (s, next, 9, 0.05);
//		next = addSegments (s, next, 1, 0.95);
		next = addSegments (s, next, 3, 0.5);
		next = addSegments (s, next, 2, 2);
		addSegments (s, next, 4, 6);


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

		for (Segment s : segments)
		{
			integralApprox =
				mgr.add
				(
					integralApprox,
					integralOf (f, s.base, s.base + s.size)
				);
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

//	ComplexValue<Double> Z = C (4.0, 5.0);		// G(5+5j) = mpc(real='-0.97439524180523907', imag='2.0066898827226298')
//	ComplexValue<Double> Z = C (1.0, 5.0);		// G(2+5j) = mpc(real='0.0050929325932930836', imag='-0.0098568418893341513')
//	ComplexValue<Double> Z = C (-0.5, 5.0);		// G(0.5+5j) = mpc(real='-0.00096948070526994953', imag='8.3630391299613721e-5')
//	ComplexValue<Double> Z = C (-0.5, 10.0);	// G(0.5+10j) = 3.378724376234236e-7 + 1.689369839038919e-7
	ComplexValue<Double> Z = C (1.5, 5.0);		// G(2.5+5j) = mpc(real='0.022673603189800138', imag='-0.011722844041715128')
//	ComplexValue<Double> Z = C (-0.5, 14.0);	// G(0.5+14j) = -4.0537030780372815e-10 -i*5.7732998345536051e-10
//	ComplexValue<Double> Z = C (-0.5, 28.0);	// G(0.5+28j) = mpc(real='-1.5558175425990789e-19', imag='1.2331886177162815e-19')

//	mpmath.gamma(0.5+5j)
//	Out[7]: mpc(real='-0.00096948070526994953', imag='8.3630391299613721e-5')
//
//	mpmath.gamma(2.5+5j)
//	Out[8]: mpc(real='0.022673603189800138', imag='-0.011722844041715128')
//
//	mpmath.gamma(2.5+5j) / ((0.5+5j) * (1.5+5j))
//	Out[10]: mpc(real='-0.00096948070526994953', imag='8.3630391299613721e-5')
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
