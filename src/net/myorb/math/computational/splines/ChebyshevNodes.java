
package net.myorb.math.computational.splines;

import net.myorb.math.polynomial.families.ChebyshevPolynomial;
import net.myorb.math.polynomial.families.chebyshev.ChebyshevPolynomialCalculus;

import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.data.notations.json.JsonSemantics;

import net.myorb.math.computational.Spline;
import net.myorb.math.GeneratingFunctions;

/**
 * implement mechanisms of Chebyshev spline
 *  using approximation theory algorithms of points
 * @author Michael Druckman
 */
public class ChebyshevNodes implements SplineMechanisms, Environment.AccessAcceptance <Double>
{


	/**
	 * data type manager for real number domain space
	 */
	public static ExpressionFloatingFieldManager realManager = new ExpressionFloatingFieldManager ();


	/*
	 * constants that describe the optimal Chebyshev T-Polynomial Spline based on points
	 */

	public static final double SPLINE_LO = -1, SPLINE_HI = 1;
	public static final double SPLINE_RANGE = SPLINE_HI - SPLINE_LO;
	public static final int SPLINE_TICKS = 23, SPLINE_SPACES = SPLINE_TICKS - 1;


	/*
	 * 

		N = 22 ; d = pi/N
		!! f(x) = - cos x

		// matrix order is 1 larger than point count
		ORDER = N + 1

		// collect array of the points curve
		points = ARRAY [ 0 <= i <= N ]   (    f (  i * d  )    )

	 *
	 */
	public static final double[] CHEBYSHEV_POINTS = new double[]
		{
				-1,
				-0.9898214418809327,  -0.9594929736144974,  -0.9096319953545184,
				-0.8412535328311812,  -0.7557495743542583,  -0.6548607339452851,  -0.5406408174555977,
				-0.41541501300188644, -0.2817325568414298,  -0.14231483827328512,

				-6.123233995736766E-17,

				 0.142314838273285,    0.28173255684142967,  0.4154150130018863,   0.5406408174555977,
				 0.654860733945285,    0.7557495743542582,   0.8412535328311811,   0.9096319953545184,
				 0.9594929736144974,   0.9898214418809327,
				 1
		};


	/**
	 * construct objects implementing Chebyshev Polynomial functionalities
	 */
	public ChebyshevNodes ()
	{
		this.calculus = new ChebyshevPolynomialCalculus <Double> (realManager);
		this.polynomial = new ChebyshevPolynomial <Double> (realManager);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SplineMechanisms#getSplineOptimalLo()
	 */
	public double getSplineOptimalLo ()
	{
		return SPLINE_LO;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SplineMechanisms#evalSplineAt(double, net.myorb.math.GeneratingFunctions.Coefficients)
	 */
	public double evalSplineAt (double x, GeneratingFunctions.Coefficients <Double> coefficients)
	{
		return polynomial.evaluatePolynomialV
				(
					coefficients, 
					polynomial.forValue (x)
				).getUnderlying ();
	}
	protected ChebyshevPolynomial <Double> polynomial;


	/*
	 * polynomial calculus implementation
	 */

	public double evaluatePolynomialIntegral
		(
			GeneratingFunctions.Coefficients <Double> coefficients, 
			double at
		)
	{
		return calculus.evaluatePolynomialIntegral (coefficients, at);
	}
	public double evaluatePolynomialIntegral
		(
			GeneratingFunctions.Coefficients <Double> coefficients, 
			double lo, double hi
		)
	{
		return calculus.evaluatePolynomialIntegral (coefficients, lo, hi);
	}
	protected ChebyshevPolynomialCalculus <Double> calculus;


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SplineMechanisms#evalIntegralOver(double, double, net.myorb.math.GeneratingFunctions.Coefficients)
	 */
	public double evalIntegralOver
		(
			double lo, double hi,
			GeneratingFunctions.Coefficients <Double> coefficients
		)
	{
		return evaluatePolynomialIntegral (coefficients, lo, hi);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SplineMechanisms#constructSplineFrom(net.myorb.data.notations.json.JsonSemantics.JsonObject)
	 */
	@SuppressWarnings("unchecked")
	public Spline.Operations <Double> constructSplineFrom
			(JsonSemantics.JsonObject json)
	{
		spline.processSplineDescription (json);
		return spline;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Environment.AccessAcceptance#setEnvironment(net.myorb.math.expressions.evaluationstates.Environment)
	 */
	public void setEnvironment (Environment<Double> environment)
	{ spline = new FittedFunction <Double> (realManager, this); }
	protected FittedFunction <Double> spline;


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SplineMechanisms#getInterpreterPath()
	 */
	public String getInterpreterPath ()
	{
		return ChebyshevNodes.class.getCanonicalName ();
	}


}

