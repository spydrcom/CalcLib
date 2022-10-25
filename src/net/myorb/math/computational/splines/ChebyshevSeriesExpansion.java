
package net.myorb.math.computational.splines;

import net.myorb.math.computational.TanhSinhQuadratureTables;
import net.myorb.math.computational.TanhSinhQuadratureAlgorithms;

import net.myorb.math.polynomial.families.chebyshev.ChebyshevSplineFunction;
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
		this.chebyshev = new ChebyshevPolynomial <Double> (RealFunctionWrapper.manager);
		this.stats = new TanhSinhQuadratureTables.ErrorEvaluation ();
	}
	protected TanhSinhQuadratureTables.ErrorEvaluation stats;
	protected ChebyshevPolynomial <Double> chebyshev;
	protected ChebyshevSeriesIntegrand integrand;


	/**
	 * use a non-default target error
	 * - CalcLib default is 1E-4 for target error
	 * @param targetAbsoluteError a request for the Quadrature object
	 */
	public ChebyshevSeriesExpansion (double targetAbsoluteError)
	{ this (); this.targetAbsoluteError = targetAbsoluteError; }
	protected double targetAbsoluteError = 1E-4;


	/**
	 * compute the integral for the specified polynomial
	 * @param Tk the Kth polynomial of the Chebyshev series
	 * @return the computed value
	 */
	public double eval (Function <Double> Tk)
	{
		this.integrand.setTk (Tk);
		double result = 2.0/Math.PI *
			TanhSinhQuadratureAlgorithms.Integrate
			(integrand, -1, 1, targetAbsoluteError, stats);
		System.out.println (stats);
		return result;
	}


	/**
	 * apply algorithm to specified function
	 * @param f the function being approximated
	 * @param order the order of the polynomial to build
	 * @return the computed Coefficients
	 */
	public Coefficients <Double> computeSeries (Function <Double> f, int order)
	{
		Coefficients <Double> c = new Coefficients <Double> ();
		this.T = chebyshev.getT (order); this.integrand.setFunction (f);

		// loop over list of T polynomials
		for (int k = 0; k <= order; k++)
		{ c.add (eval (T.get (k))); }

		// coefficient of first term is 1/PI instead of 2/PI
		c.set (0, c.get (0) / 2.0);
		return c;
	}
	protected PolynomialFamilyManager.PowerFunctionList <Double> T;


	/**
	 * compute the series for a function
	 * @param f the function being approximated
	 * @param order the order of the polynomial to build
	 * @param targetError a request for the Quadrature object
	 * @return the computed Coefficients
	 */
	public static Coefficients <Double> computeSeries
		(RealFunctionWrapper f, int order, double targetError)
	{
		ChebyshevSeriesExpansion series =
				new ChebyshevSeriesExpansion (targetError);
		return series.computeSeries (f.toCommonFunction (), order);
	}


	/**
	 * build an approximation
	 * @param f the function being approximated
	 * @param order the order of the polynomial to build
	 * @param targetError a request for the Quadrature object
	 * @return the approximation function
	 */
	public static Function <Double> approximate
		(RealFunctionWrapper f, int order, double targetError)
	{
		Coefficients <Double> c = computeSeries (f, order, targetError);
		return new ChebyshevSplineFunction <Double> (c, RealFunctionWrapper.manager);
	}


}


/**
 * the function providing the integrand for the Quadrature algorithm
 */
class ChebyshevSeriesIntegrand implements Function <Double>
{

	/**
	 * @param f the function being approximated
	 */
	void setFunction (Function <Double> f) { this.f = f; }
	void setTk (Function <Double> Tk) { this.Tk = Tk; }
	Function <Double> f, Tk;

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	@Override
	public Double eval (Double x) {
		// the equation of the Chebyshev series integral
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

