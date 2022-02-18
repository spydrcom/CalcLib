
package net.myorb.testing.linalg;

import net.myorb.math.expressions.managers.*;
import net.myorb.math.complexnumbers.*;
import net.myorb.math.computational.*;
import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

import net.myorb.data.abstractions.SpaceDescription;

public class ComplexSpline
{

	static double loRange = 0, hiRange = 20;

	public static void main (String[] a) throws Exception
	{
		VC31ComponentSpline<ComplexValue<Double>> s =
				new VC31ComponentSpline<ComplexValue<Double>> (new Gamma (), Gamma.mgr, null);
		s.addSegment (loRange, hiRange);

		System.out.println ();
		double tsqr = TSQ(new GammaPart(0)), tsqi = TSQ(new GammaPart(1));
		System.out.println ("TSQ f: " + C (tsqr, tsqi).toString () + " = GAMMA (5+5i)");

		ComplexValue<Double>
			lo = Gamma.lib.C (loRange, 0.0),
			hi = Gamma.lib.C (hiRange, 0.0);
		System.out.println ("CHEB Calculus spline: " + s.evalIntegral (lo, hi));
		// -0.97439524180523907 + i*2.0066898827226298  === 6.66 * ( -0.146 + 0.3j )
		// (-0.7899469746553125 + 0.15526327173426324*i)
		// (-0.4399712490253549 + 0.7095952274041064*i)

		tsqr = TSQ(new SplinePart(s,0)); tsqi = TSQ(new SplinePart(s,1));
		System.out.println ("TSQ spline: " + C (tsqr, tsqi).toString ());
	}

	static double TSQ (Function<Double> f)
	{
		return TanhSinhQuadratureAlgorithms.Integrate (f, loRange, hiRange, 0.0001, null);
	}

	static ComplexValue<Double> C (double r, double i)
	{
		return Gamma.lib.C (r, i);
	}

}

class SplinePart implements Function<Double>
{

	SplinePart (Function<ComplexValue<Double>> f, int part)
	{ this.part = part; this.f = f; }
	int part; Function<ComplexValue<Double>> f;

	public Double eval(Double x)
	{
		ComplexValue<Double> cy = f.eval (Gamma.mgr.convertFromDouble (x));

		switch (part)
		{
		case 0: return cy.Re();
		default: return cy.Im();
		}
	}

	public SpaceDescription<Double> getSpaceDescription() { return Gamma.realmgr; }
	public SpaceManager<Double> getSpaceManager() { return Gamma.realmgr; }

}

class GammaPart implements Function<Double>
{

	GammaPart (int part) { this.part = part; }
	int part;

	public Double eval(Double x)
	{
		ComplexValue<Double> cy = G.eval (Gamma.mgr.convertFromDouble (x));
		switch (part)
		{
		case 0: return cy.Re();
		default: return cy.Im();
		}
	}

	public SpaceDescription<Double> getSpaceDescription() { return Gamma.realmgr; }
	public SpaceManager<Double> getSpaceManager() { return Gamma.realmgr; }
	Gamma G = new Gamma ();
	
}

class Gamma implements Function<ComplexValue<Double>>
{

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public ComplexValue<Double> eval(ComplexValue<Double> x)
	{
		return mgr.multiply (lib.power (x, C), lib.exp (mgr.negate (x)));
	}

	public SpaceManager<ComplexValue<Double>> getSpaceManager() { return mgr; }
	public SpaceDescription<ComplexValue<Double>> getSpaceDescription() { return mgr; }

	static ExpressionFloatingFieldManager realmgr = new ExpressionFloatingFieldManager ();
	static ExpressionComplexFieldManager mgr = new ExpressionComplexFieldManager ();
	static ComplexLibrary<Double> lib = new ComplexLibrary<Double> (realmgr, mgr);

	ComplexValue<Double> C = lib.C (4.0, 5.0);

}

