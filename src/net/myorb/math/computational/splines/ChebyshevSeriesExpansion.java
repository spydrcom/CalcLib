
package net.myorb.math.computational.splines;

import net.myorb.math.computational.TanhSinhQuadratureTables;
import net.myorb.math.computational.TanhSinhQuadratureAlgorithms;

import net.myorb.math.polynomial.families.ChebyshevPolynomial;
import net.myorb.math.polynomial.PolynomialFamilyManager;

import net.myorb.math.GeneratingFunctions.Coefficients;
import net.myorb.math.realnumbers.RealFunctionWrapper;
import net.myorb.data.abstractions.SpaceDescription;

import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

/**
 * implement Chebyshev T-Polynomial series expansion.
 * - coefficients to be computed using the integration formula.
 * @author Michael Druckman
 */
public class ChebyshevSeriesExpansion
{


	//	!! c(k) = 2/pi * INTEGRAL [ -1 <= x <= 1 ] ( f(x) * T_k(x) / sqrt (1-x^2) * <*> x )
	//  !! f(x) = SIGMA [ 0 <= k <= N ] ( c_k * T_k (x) )


	public ChebyshevSeriesExpansion ()
	{
		this.integrand = new ChebyshevSeriesIntegrand ();
		this.cPoly = new ChebyshevPolynomial <Double> (RealFunctionWrapper.manager);
	}
	ChebyshevSeriesIntegrand integrand;
	ChebyshevPolynomial <Double> cPoly;


	public double eval ()
	{
		double result = 2.0/Math.PI *
			TanhSinhQuadratureAlgorithms.Integrate
			(integrand, -1, 1, targetAbsoluteError, stats);
		System.out.println (stats);
		return result;
	}
	TanhSinhQuadratureTables.ErrorEvaluation stats = new TanhSinhQuadratureTables.ErrorEvaluation ();
	protected double targetAbsoluteError = 1E-5; // CalcLib default is 1E-4


	public Coefficients <Double> computeSeries (Function <Double> f, int order)
	{
		this.T = cPoly.getT (order);
		this.integrand.setFunction (f);
		Coefficients <Double> c = new Coefficients <Double> ();

		for (int k = 0; k <= order; k++)
		{
			this.integrand.setTk (T.get (k));
			c.add (eval ());
		}

		// coefficient of first term is 1/PI instead of 2/PI
		c.set (0, c.get (0) / 2.0);
		return c;
	}
	PolynomialFamilyManager.PowerFunctionList <Double> T;


	public static void main (String[] args)
	{
		int order = 22;
		RealFunctionWrapper f =
			new RealFunctionWrapper
			(
				(x) -> 2*Math.pow(x,3) - 3*Math.pow(x,2)
			);
		ChebyshevSeriesExpansion series = new ChebyshevSeriesExpansion ();
		Coefficients <Double> c = series.computeSeries (f.toCommonFunction (), order);
		System.out.println (c);
	}


}


class ChebyshevSeriesIntegrand implements Function <Double>
{

	void setFunction (Function <Double> f)
	{
		this.f = f;
	}
	Function <Double> f;

	void setTk (Function <Double> Tk)
	{
		this.Tk = Tk;
	}
	Function <Double> Tk;

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	@Override
	public Double eval (Double x) {
		return f.eval (x) * Tk.eval (x) / Math.sqrt (1 - x*x);
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	@Override
	public SpaceDescription<Double> getSpaceDescription() {
		return RealFunctionWrapper.manager;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	@Override
	public SpaceManager<Double> getSpaceManager() {
		return RealFunctionWrapper.manager;
	}

}

