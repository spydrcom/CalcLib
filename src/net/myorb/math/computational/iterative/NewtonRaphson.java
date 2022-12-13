
package net.myorb.math.computational.iterative;

import net.myorb.math.GeneratingFunctions.Coefficients;
import net.myorb.math.polynomial.OrdinaryPolynomialCalculus;

import net.myorb.math.GeneratingFunctions;
import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;

/**
 * compute a root using the Newton-Raphson method
 * @author Michael Druckman
 */
public class NewtonRaphson <T> extends IterationFoundations <T>
{


	public NewtonRaphson (SpaceManager <T> manager)
	{
		this.evaluationEngine = new Polynomial <T> (manager);
		this.calculus = new OrdinaryPolynomialCalculus <T> (manager);
		this.G = new GeneratingFunctions <> (manager);
		this.manager = manager;
	}
	protected SpaceManager <T> manager;
	public GeneratingFunctions <T> G;


	/**
	 * @return an object that can be used to compute the Polynomial values
	 */
	public Polynomial <T>
		getEngine () { return evaluationEngine; }
	protected OrdinaryPolynomialCalculus <T> calculus;
	protected Polynomial <T> evaluationEngine;


	/**
	 * @param functionCoefficients the polynomial coefficients that describe the function
	 */
	public void establishFunction (Coefficients <T> functionCoefficients)
	{
		this.derivativeCoefficients =
			this.calculus.computeDerivativeCoefficients
				(functionCoefficients);
		this.functionCoefficients = functionCoefficients;
	}
	protected Coefficients <T> derivativeCoefficients;
	protected Coefficients <T> functionCoefficients;


	/**
	 * compute the values of the function and the derivative
	 * - the function and the derivative values will be used to compute the iteration offset
	 * @param x the current approximation of the root
	 */
	public void setApproximationOfX (T x)
	{
		this.setX (x);

		this.setFunctionOfX
		(
			this.evaluationEngine.evaluatePolynomial
				(this.functionCoefficients, x)
		);

		this.setDerivativeAtX
		(
			this.evaluationEngine.evaluatePolynomial
				(this.derivativeCoefficients, x)
		);
	}


	/**
	 * compute the offset to apply for the iteration
	 */
	public void applyIteration ()
	{
		T dXdF = manager.invert (this.getDerivativeAtX ());
		this.setDelta (manager.negate (manager.multiply (this.getFunctionOfX (), dXdF)));
		this.setApproximationOfX (manager.add (this.getX (), this.getDelta ()));
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.iterative.IterationFoundations#toString(java.lang.Object)
	 */
	public String toString (T x) { return manager.toDecimalString (x); }


}

