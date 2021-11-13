
package net.myorb.math;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.polynomial.PolynomialCalculus;
import net.myorb.math.polynomial.PolynomialCalculusSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * calculus for the exponential form a * e ^ (b * x)
 * @param <T> the types of coefficients in the polynomial terms
 * @author Michael Druckman
 */
public class ExponentialPolynomialCalculus<T> extends PolynomialCalculus<T>
	implements PolynomialCalculusSupport<T>
{


	public ExponentialPolynomialCalculus
	(ExpressionSpaceManager<T> manager, PowerLibrary<T> lib)
	{ super (manager); this.lib = lib; }
	PowerLibrary<T> lib;


	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialCalculusSupport#evaluatePolynomialDerivative(net.myorb.math.GeneratingFunctions.Coefficients, java.lang.Object)
	 */
	public T evaluatePolynomialDerivative (GeneratingFunctions.Coefficients<T> a, T atX)
	{
		return getFunctionDerivative (getPolynomialFunction (a)).eval (atX);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialCalculusSupport#evaluatePolynomialIntegral(net.myorb.math.GeneratingFunctions.Coefficients, java.lang.Object, java.util.List)
	 */
	public List<T> evaluatePolynomialIntegral(GeneratingFunctions.Coefficients<T> a, T fromX, List<T> toX)
	{
		List<T> results = new ArrayList<T>();
		Function<T> integral = getFunctionIntegral (getPolynomialFunction (a));
		T lower = integral.eval (fromX);
		for (T t : toX)
		{
			results.add (manager.add (integral.eval (t), manager.negate (lower)));
		}
		return results;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialCalculusSupport#evaluatePolynomialIntegral(net.myorb.math.GeneratingFunctions.Coefficients, java.lang.Object, java.lang.Object)
	 */
	public T evaluatePolynomialIntegral(GeneratingFunctions.Coefficients<T> a, T fromX, T toX)
	{
		Function<T> integral = getFunctionIntegral (getPolynomialFunction (a));
		T upper = integral.eval (toX), lower = integral.eval (fromX);
		return manager.add (upper, manager.negate (lower));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialCalculusSupport#evaluatePolynomialIntegral(net.myorb.math.GeneratingFunctions.Coefficients, java.lang.Object)
	 */
	public T evaluatePolynomialIntegral(GeneratingFunctions.Coefficients<T> a, T atX)
	{
		return getFunctionIntegral (getPolynomialFunction (a)).eval (atX);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialCalculus#getFunctionDerivative(net.myorb.math.Function)
	 */
	public Function<T> getFunctionDerivative(Function<T> function)
	{
		Coefficients<T> coefficients =
			((PowerFunction<T>)function).getCoefficients ();
		T a = coefficients.get (0), b = coefficients.get (1);
		return getPolynomialFunction
		(
			newCoefficients (manager.multiply (a, b), b)
		);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialCalculus#getFunctionIntegral(net.myorb.math.Function)
	 */
	public Function<T> getFunctionIntegral(Function<T> function)
	{
		Coefficients<T> coefficients =
			((PowerFunction<T>)function).getCoefficients ();
		T a = coefficients.get (0), b = coefficients.get (1);
		return getPolynomialFunction
		(
			newCoefficients (manager.multiply (a, manager.invert (b)), b)
		);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.Polynomial#evaluatePolynomialV(net.myorb.math.GeneratingFunctions.Coefficients, net.myorb.math.Arithmetic.Value)
	 */
	public Value<T> evaluatePolynomialV (Coefficients<T> coefficients, Value<T> x)
	{ return forValue (evaluatePolynomial (coefficients, x.getUnderlying ())); }

	/* (non-Javadoc)
	 * @see net.myorb.math.Polynomial#evaluatePolynomial(net.myorb.math.GeneratingFunctions.Coefficients, java.lang.Object)
	 */
	public T evaluatePolynomial (Coefficients<T> coefficients, T x)
	{
		T a = coefficients.get (0), b = coefficients.get (1);
		T result = manager.multiply (a, lib.exp (manager.multiply (b, x)));
		return result;
	}

}


