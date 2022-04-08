
package net.myorb.math.computational.splines;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.polynomial.PolynomialCalculus;

import net.myorb.data.notations.json.JsonSemantics;

import net.myorb.math.computational.Spline;
import net.myorb.math.GeneratingFunctions;

/**
 * implement mechanisms of Cubic Spline algorithms
 * @author Michael Druckman
 */
public class CubicSplineInterpreter implements SplineMechanisms, Environment.AccessAcceptance <Double>
{


	/**
	 * data type manager for real number domain space
	 */
	public static ExpressionFloatingFieldManager realManager = new ExpressionFloatingFieldManager ();
	public static PolynomialCalculus <Double> poly = new PolynomialCalculus <Double> (realManager);


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SplineMechanisms#getSplineOptimalLo()
	 */
	public double getSplineOptimalLo () { return 0.0; }


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SplineMechanisms#evalSplineAt(double, net.myorb.math.GeneratingFunctions.Coefficients)
	 */
	public double evalSplineAt (double x, GeneratingFunctions.Coefficients <Double> coefficients)
	{
		return poly.evaluatePolynomial (coefficients, x);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SplineMechanisms#evalIntegralOver(double, double, net.myorb.math.GeneratingFunctions.Coefficients)
	 */
	public double evalIntegralOver
		(
			double lo, double hi,
			GeneratingFunctions.Coefficients <Double> coefficients
		)
	{
		return poly.evaluatePolynomialIntegral
			(
				poly.getPolynomialFunction
					(coefficients),
				lo, hi
			);
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
		return CubicSplineInterpreter.class.getCanonicalName ();
	}


}

