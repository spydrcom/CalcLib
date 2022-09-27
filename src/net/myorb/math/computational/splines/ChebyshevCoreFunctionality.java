
package net.myorb.math.computational.splines;

import net.myorb.math.polynomial.families.ChebyshevPolynomial;
import net.myorb.math.polynomial.families.chebyshev.ChebyshevPolynomialCalculus;

import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.data.notations.json.JsonSemantics;

import net.myorb.math.computational.Spline;
import net.myorb.math.GeneratingFunctions;

/**
 * implement mechanisms of Chebyshev T-Polynomial spline algorithms
 * @author Michael Druckman
 */
public abstract class ChebyshevCoreFunctionality
	implements SplineMechanisms, Environment.AccessAcceptance <Double>
{


	/**
	 * data type manager for real number domain space
	 */
	public static ExpressionFloatingFieldManager realManager = new ExpressionFloatingFieldManager ();


	public ChebyshevCoreFunctionality ()
	{
		this.calculus = new ChebyshevPolynomialCalculus <Double> (realManager);
		this.polynomial = new ChebyshevPolynomial <Double> (realManager);
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


}

