
package net.myorb.math.computational;

import net.myorb.math.computational.sampling.Calculus;
import net.myorb.math.GeneratingFunctions.Coefficients;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.Function;

/**
 * Taylor polynomial evaluations for functions
 * @param <T> data type used in Arithmetic operations
 * @author Michael Druckman
 */
public class TaylorSeriesEvaluations <T> extends Calculus <T>
{


	/*
	 * f(x) = SIGMA [ 0 <= n <= INFINITY ] ( f'(n)(a)/n! * (x - a)^n ) 
	 * where f'(N)(a) is Nth derivative of f evaluated at a
	 */


	public Coefficients <T> compute
		(
			Function <T> f, T a, int order,
			T dx, T proximity
		)
	{
		Coefficients <T> C = new Coefficients <> ();
		Derivatives derivatives = new Derivatives ();
		evaluate (f, a, order, dx, proximity, derivatives);
		compute (a, derivatives, C);
		return C;
	}


	public void compute
		(
			T a, Derivatives derivatives,
			Coefficients <T> C
		)
	{
		T invF = manager.newScalar (1);
	
		C.add (derivatives.get (0).computeYfor (a));
		C.add (derivatives.get (1).computeYfor (a));
	
		for (int n = 2; n < derivatives.size (); n++)
		{
			invF = manager.multiply
				(invF, manager.invert (manager.newScalar (n)));
			T ddxAtA = derivatives.get (n).computeYfor (a);
			C.add (manager.multiply (ddxAtA, invF));
		}
	}


	public TaylorSeriesEvaluations
	(ExpressionSpaceManager <T> manager)
	{ super (manager); }


}

