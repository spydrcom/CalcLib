
package net.myorb.math.specialfunctions;

import net.myorb.math.complexnumbers.ComplexFieldManager;
import net.myorb.math.complexnumbers.ComplexLibrary;
import net.myorb.math.complexnumbers.ComplexValue;

import net.myorb.math.expressions.managers.*;

public class Hurwitz
{

	public Hurwitz (ComplexLibrary<Double> complexLib)
	{
		complexLib.initializeGamma ();
		this.mgr = complexLib.getComplexFieldManager ();
		this.complexLib = complexLib;
		this.init (1, 1E-1, 200);
	}
	protected ComplexLibrary<Double> complexLib;
	protected ComplexFieldManager<Double> mgr;

	void init (double a, double dx, double inf)
	{
		this.M1 = mgr.newScalar (-1);
		this.INF = inf; this.dx = mgr.C (dx, 0.0);
		this.oneMinusA = mgr.negate (mgr.add (M1, mgr.C (a, 0.0)));
		this.aIsOne = oneMinusA.Re () == 0.0;
	}
	protected ComplexValue<Double> M1, dx, oneMinusA;
	protected boolean aIsOne;
	protected double INF;

	/*
	 * 
	 * zeta (s, a) = 1/GAMMA(s) *
	 * 		INTEGRAL [0..INF]
	 * ( x ^ (s-1)   *   exp ( (1-a) * x )   /   ( exp(x) - 1 ) )
	 *			* dx
	 * 
	 */

	public ComplexValue<Double> evalLog (ComplexValue<Double> sM1, ComplexValue<Double> x)
	{
		return mgr.multiply (sM1, complexLib.ln (x));
	}

	public ComplexValue<Double> evalForA (ComplexValue<Double> sM1, ComplexValue<Double> x)
	{
		ComplexValue<Double> oneMinusAx = mgr.multiply (oneMinusA, x);
		return complexLib.nativeExp (mgr.add (oneMinusAx, evalLog (sM1, x)));
	}

	public ComplexValue<Double> evalForNoA (ComplexValue<Double> sM1, ComplexValue<Double> x)
	{
		return complexLib.nativeExp (evalLog (sM1, x));
	}

	public ComplexValue<Double> evalFor (ComplexValue<Double> sM1, ComplexValue<Double> x)
	{
		ComplexValue<Double> numerator = !aIsOne? evalForA (sM1, x): evalForNoA (sM1, x);
		ComplexValue<Double> denominator = mgr.C (Math.exp (x.Re ()) - 1.0, 0.0);
		return mgr.multiply (numerator, mgr.invert (denominator));
	}

	public ComplexValue<Double> sum (ComplexValue<Double> sM1)
	{
		ComplexValue<Double> total = mgr.getZero (), x = dx;

		while (x.Re () < INF)
		{
			total = mgr.add (total, evalFor (sM1, x));
			x = mgr.add (x, dx);
		}

		return total;
	}

	public ComplexValue<Double> integeral (ComplexValue<Double> s)
	{
		return mgr.multiply (sum (mgr.add (s, M1)), dx);
	}

	public ComplexValue<Double> zeta (ComplexValue<Double> s)
	{
		return mgr.multiply (mgr.invert (complexLib.gamma (s)), integeral (s));
	}

	public static void main (String[] args)
	{
		ExpressionComplexFieldManager cmgr = new ExpressionComplexFieldManager ();
		Hurwitz H = new Hurwitz (new ComplexLibrary<Double> (cmgr.getComponentManager (), cmgr));
		
		ComplexValue<Double> x = cmgr.C (0.5, 10.0), xinc = cmgr.C (0.0, 1.0);
		for (int i=1; i<30; i++)
		{
			System.out.println (H.zeta (x));
			x = cmgr.add (x, xinc);
		}
	}

}
