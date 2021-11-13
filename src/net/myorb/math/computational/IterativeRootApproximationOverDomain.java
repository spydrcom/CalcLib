
package net.myorb.math.computational;

import net.myorb.math.polynomial.PolynomialFamilyManager;
import net.myorb.math.polynomial.families.LaguerrePolynomial;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.expressions.symbols.GenericWrapper;
import net.myorb.data.abstractions.Function;

import java.util.ArrayList;
import java.util.List;

/**
 * extend root approximation by using small delta search across domain
 * @author Michael Druckman
 */
public class IterativeRootApproximationOverDomain
	extends IterativeRootApproximation <Double>
{


	/**
	 * connect derivatives with function
	 * @param function the function describing a term
	 * @param derivative the derivative of the function
	 * @param secondDerivative 2nd derivative for 2nd order formula
	 */
	public IterativeRootApproximationOverDomain
		(
			Function<Double> function, GenericWrapper.GenericFunction<Double> derivative,
			GenericWrapper.GenericFunction<Double> secondDerivative
		)
	{ super (function, derivative, secondDerivative); }


	/**
	 * @param function the function describing a term
	 * @param delta the delta to use in derivative approximations
	 * @return an approximation engine
	 */
	public static IterativeRootApproximationOverDomain
		getApproximationIterator (Function<Double> function, Double delta)
	{
		DerivativeApproximation.Functions<Double> derivativeApproximations =
			DerivativeApproximation.getDerivativesFor (function, delta);
		return new IterativeRootApproximationOverDomain
			(
				function, derivativeApproximations.first (),
				derivativeApproximations.second ()
			);
	}


	/**
	 * @param function the function being analyzed
	 * @param lo the low of the domain of the function
	 * @param hi the high of the domain of the function
	 * @param delta the delta to use for steps of the domain
	 * @param iterationCount the count of iterations to use in the root approximation
	 * @return the list of roots found
	 */
	public static List <Double> locateRoots
		(
			Function<Double> function,
			Double lo, Double hi, Double delta,
			int iterationCount
		)
	{
		IterativeRootApproximationOverDomain
		approx = getApproximationIterator (function, delta / 100);
		boolean lastWasNeg = function.eval (lo) < 0;
		List <Double> roots = new ArrayList <> ();

		for (double x = lo+delta; x <= hi; x += delta)
		{
			boolean fIsNeg = function.eval (x) < 0;
			if (fIsNeg != lastWasNeg)
			{
				approx.executeIterations (x, iterationCount);
				roots.add (approx.currentApproximation);
			}
			lastWasNeg = fIsNeg;
		}

		return roots;
	}
	//public void showCurrentApproximation () {}
	//public void done () {}


	/**
	 * unit test
	 * @param args not used
	 */
	public static void main (String... args)
	{
		PolynomialFamilyManager.PowerFunctionList <Double> poly =
		new LaguerrePolynomial <> (new ExpressionFloatingFieldManager ()).recurrence (25);
		//System.out.println (locateRoots (poly.get (5), 0d, 14d, 0.01, 5));
		//System.out.println (locateRoots (poly.get (9), 0d, 30d, 0.01, 5));
		//System.out.println (locateRoots (poly.get (20), 0d, 30d, 0.01, 5));
		System.out.println (locateRoots (poly.get (20), 0d, 75d, 0.01, 5));
	}


}

