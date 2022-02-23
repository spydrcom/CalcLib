
package net.myorb.testing.linalg;

import java.util.ArrayList;

import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.computational.VC31ComponentSpline;
import net.myorb.math.expressions.managers.ExpressionComplexFieldManager;

public class SplineTest
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

		double next = 1E-8;
		next = addSegments (s, next, 9, 1E-9);
		next = addSegments (s, next, 4, 1E-6);
		next = addSegments (s, next, 1, 5E-6);
		next = addSegments (s, next, 3, 0.00003);
		next = addSegments (s, next, 3, 0.0003);
		next = addSegments (s, next, 4, 0.001);
		next = addSegments (s, next, 2, 0.01);
		next = addSegments (s, next, 1, 0.025);
		next = addSegments (s, next, 9, 0.05);
		next = addSegments (s, next, 3, 0.5);
		next = addSegments (s, next, 2, 2);
		addSegments (s, next, 4, 6);

		System.out.println (s.evalIntegral ());

	}

	public static ComplexValue<Double> SIN (ComplexValue<Double> z)
	{
		return z;
	}

	public static ComplexValue<Double> GAMMA (ComplexValue<Double> z)
	{
		return z;
	}

	public static ComplexValue<Double> reflect (ComplexValue<Double> z)
	{
		ComplexValue<Double>
			PI = mgr.C (Math.PI, 0.0),
			zPI = mgr.multiply (PI, z),
			isinZPI = mgr.invert (SIN (zPI)),
			piCsc = mgr.multiply (PI, isinZPI),
			negzP1 = mgr.add (mgr.negate (z), mgr.getOne ());
		return mgr.multiply (piCsc, mgr.invert (GAMMA (negzP1)));
	}

	public static ComplexValue<Double> residue (ComplexValue<Double> z)
	{
		ComplexValue<Double>
			zp2 = mgr.add (z, mgr.newScalar (2)),
			zp1 = mgr.add (z, mgr.newScalar (1)),
			izzp1 = mgr.invert (mgr.multiply (z, zp1));
		return mgr.multiply (GAMMA (zp2), izzp1);
	}

	static ExpressionComplexFieldManager mgr = new ExpressionComplexFieldManager ();

// G(5+5j) = mpc(real='-0.97439524180523907', imag='2.0066898827226298')
// G(0.5+5j) = mpc(real='-0.00096948070526994953', imag='8.3630391299613721e-5')
//	next = addSegments (s, next, 10, 1E-19);
//	next = addSegments (s, next, 1, 9E-18);
//	next = addSegments (s, next, 1, 9E-17);
//	next = addSegments (s, next, 1, 9E-16);
//	next = addSegments (s, next, 1, 9E-15);
//	next = addSegments (s, next, 1, 9E-14);
//	next = addSegments (s, next, 1, 9E-13);
//	next = addSegments (s, next, 1, 9E-12);
//	next = addSegments (s, next, 1, 9E-11);
//	next = addSegments (s, next, 1, 9E-10);
//	next = addSegments (s, next, 1, 9E-9);
//	next = addSegments (s, next, 1, 9E-8);
//	next = addSegments (s, next, 1, 9E-7);
//	next = addSegments (s, next, 4, 1E-6);
//	next = addSegments (s, next, 1, 5E-6);
//	next = addSegments (s, next, 3, 0.00003);
//	next = addSegments (s, next, 3, 0.0003);
//	next = addSegments (s, next, 4, 0.001);
//	next = addSegments (s, next, 2, 0.01);
//	next = addSegments (s, next, 1, 0.025);
//	next = addSegments (s, next, 9, 0.05);
//	next = addSegments (s, next, 3, 0.5);
//	next = addSegments (s, next, 2, 2);
//	addSegments (s, next, 4, 6);
	
// G(2+5j) = mpc(real='0.0050929325932930836', imag='-0.0098568418893341513')
//	next = addSegments (s, next, 6, 1E-16);
//	next = addSegments (s, next, 6, 1E-14);
//	next = addSegments (s, next, 6, 1E-12);
//	next = addSegments (s, next, 6, 1E-10);
//	next = addSegments (s, next, 2, 1E-8);
//	next = addSegments (s, next, 2, 1E-4);
//	next = addSegments (s, next, 2, 1E-3);
//	next = addSegments (s, next, 2, 1E-2);
//	next = addSegments (s, next, 5, 1E-1);
//	next = addSegments (s, next, 10, 1);
//	next = addSegments (s, next, 10, 0.1);
//	next = addSegments (s, next, 1, 10);

}
