
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.computational.TanhSinhQuadratureAlgorithms;
import net.myorb.math.computational.CCQIntegration;
import net.myorb.math.specialfunctions.Library;

import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

import java.util.Map;

/**
 * function implementations for use as 
 * 	integrands for quadrature operations
 * @author Michael Druckman
 */
public abstract class RealIntegrandFunctionBase implements Function<Double>
{

	public RealIntegrandFunctionBase (double a) { this (0, a); }
	public RealIntegrandFunctionBase (double x, double a)
	{ this.a = a; setParameter (x); }
	protected double x, a;

	/**
	 * @param x the parameter value to run
	 */
	public void setParameter (double x) { this.x = x; }

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager<Double> getSpaceManager () { return manager; }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceManager<Double> getSpaceDescription () { return manager; }

	public static final ExpressionSpaceManager<Double> manager = new ExpressionFloatingFieldManager ();


}


/**
 * implementation of algorithms with forms 
 *  of two sections each computed with an integral
 */
class SectionedAlgorithm
{

	SectionedAlgorithm
		(
			double a, int infinity,
			RealIntegrandFunctionBase section1,
			RealIntegrandFunctionBase section2,
			Map<String,Object> parameters
		)
	{
		this.a = a; this.parameters = parameters;
		this.pi = Math.PI; this.infinity = infinity;
		this.nonIntegerAlpha = ! Library.isInteger (a);
		this.constructIntegrals (section1, section2);
		this.sinAlphaPi = - Math.sin (a * pi);
	}
	int infinity; boolean nonIntegerAlpha;
	double pi, a, sinAlphaPi;

	void constructIntegrals
		(
			RealIntegrandFunctionBase section1,
			RealIntegrandFunctionBase section2
		)
	{
		I1 = new Quadrature (section1, parameters).getIntegral ();
		I2 = new Quadrature (section2, parameters).getIntegral ();
	}
	Map<String,Object> parameters;
	Quadrature.Integral I1, I2;

	/**
	 * Integral form of Ia
	 * @param x parameter to Ia function
	 * @return calculated result
	 */
	double eval (double x)
	{
		double sum = 0.0;
		if (nonIntegerAlpha)
		{ sum += sinAlphaPi * I2.eval (x, 0.0, infinity); }
		sum += I1.eval (x, 0.0, Math.PI);
		return sum / pi;
	}

}


/**
 * quadrature implementation as described by parameters
 */
class Quadrature
{
	public interface Integral
	{
		public double eval (double x, double lo, double hi);
	}
	Quadrature
		(
			RealIntegrandFunctionBase integrand,
			Map<String,Object> parameters
		)
	{
		this.integrand = integrand;
		this.parameters = parameters;
	}
	Integral getIntegral ()
	{
		return new CCQuadrature (integrand, parameters);
	}
	RealIntegrandFunctionBase integrand;
	Map<String,Object> parameters;
}


/**
 * quadrature using Clenshaw-Curtis algorithm
 */
class CCQuadrature implements Quadrature.Integral
{
	public double eval (double x, double lo, double hi)
	{
		integrand.setParameter (x);
		return ccq.computeApproximation (lo, hi);
	}
	CCQuadrature
		(
			RealIntegrandFunctionBase integrand,
			Map<String,Object> parameters
		)
	{
		ccq = new CCQIntegration (integrand);
		this.integrand = integrand;
	}
	RealIntegrandFunctionBase integrand;
	CCQIntegration ccq;
}


/**
 * quadrature using Tanh-Sinh algorithm
 */
class TSQuadrature implements Quadrature.Integral
{
	public double eval (double x, double lo, double hi)
	{
		integrand.setParameter (x);
		return TanhSinhQuadratureAlgorithms.Integrate
		(integrand, lo, hi, targetAbsoluteError, null);
	}
	TSQuadrature
		(
			RealIntegrandFunctionBase integrand,
			Map<String,Object> parameters
		)
	{
		this.targetAbsoluteError =
			UnderlyingOperators.getPrecision (parameters);
		this.integrand = integrand;
	}
	RealIntegrandFunctionBase integrand;
	double targetAbsoluteError;
}

