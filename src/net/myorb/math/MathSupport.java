
package net.myorb.math;

import net.myorb.math.polynomial.PolynomialCalculusSupport;
import net.myorb.math.polynomial.families.chebyshev.ChebyshevPolynomialCalculus;
import net.myorb.math.polynomial.OrdinaryPolynomialCalculus;

import net.myorb.data.abstractions.Function;

/**
 * support for relationships of basic math objects
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class MathSupport<T> extends GeneratingFunctions<T>
{


	public MathSupport
		(SpaceManager<T> manager)
	{ super (manager); }


	/**
	 * determine that function supports calculus
	 * @param f the function requesting derivative or integral support
	 * @return the support object if available
	 */
	//@SuppressWarnings("unchecked")
	public PolynomialCalculusSupport<T> getSupportFor (Function<T> f)
	{
		Polynomial<T> p = ((Polynomial.PowerFunction<T>) f).getPolynomial ();
		//if (p instanceof PolynomialCalculusSupport) return (PolynomialCalculusSupport<T>) p;
		if (p instanceof ExponentialPolynomialCalculus) return (ExponentialPolynomialCalculus<T>) p;
		if (p instanceof ChebyshevPolynomialCalculus) return (ChebyshevPolynomialCalculus<T>) p;
		if (p instanceof OrdinaryPolynomialCalculus) return (OrdinaryPolynomialCalculus<T>) p;
		throw new RuntimeException ("No support for calculus");
	}


}
