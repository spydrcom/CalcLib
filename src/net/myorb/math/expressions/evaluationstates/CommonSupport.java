
package net.myorb.math.expressions.evaluationstates;

import net.myorb.math.computational.*;
import net.myorb.math.expressions.*;

import net.myorb.data.abstractions.Function;
import net.myorb.math.*;

/**
 * support for transform processing
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class CommonSupport<T> extends MathSupport<T>
{


	public CommonSupport
		(ExpressionSpaceManager<T> manager)
	{ super (manager); expressionManager = manager; }
	protected ExpressionSpaceManager<T> expressionManager;


	/**
	 * select calculus processing objet for specified function
	 * @param f the function requesting calculus transform processing
	 * @return an implementation of the transform calculus interface
	 * @throws RuntimeException for unrecognized transform
	 */
	public TransformCalculus<T> calculusFor (Function<T> f) throws RuntimeException
	{
		TransformCalculus<T> calculus;

		if (f instanceof Polynomial.PowerFunction) calculus = getSupportFor (f);									// transform object must be selected from type of polynomial
		else if (f instanceof Fourier.Transform) calculus = new FourierSeriesCalculus<T> (expressionManager);
		else throw new RuntimeException ("Unrecognized transform");

		return calculus;
	}


}

