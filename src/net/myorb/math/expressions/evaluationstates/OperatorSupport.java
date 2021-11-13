
package net.myorb.math.expressions.evaluationstates;

import net.myorb.data.abstractions.Function;
import net.myorb.math.*;

import net.myorb.math.expressions.*;
import net.myorb.math.computational.*;
import net.myorb.math.expressions.symbols.*;

import java.util.List;

/**
 * special processing methods related to calculus operators
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class OperatorSupport<T> extends CommonSupport<T>
{


	public OperatorSupport (Primitives<T> p)
	{
		super (p.getSpaceManager ());
		this.derivatives = new DerivativeApproximation<T> (expressionManager);
		this.p = p;
	}
	protected DerivativeApproximation<T> derivatives;
	protected Primitives<T> p;


	/**
	 * get derivative of function
	 * @param function functions description that implements calculus support
	 * @param count number of derivatives first=1 or second=2
	 * @return function that is derivative of parameter
	 */
	public SymbolMap.ParameterizedFunction getDerivativeFunction
		(
			SymbolMap.ParameterizedFunction function, int count
		)
	{
		switch (count)
		{
			case 1: return getFirstDerivativeFor (function);
			case 2: return getSecondDerivativeFor (function);
		}
		return null;
	}


	/**
	 * find symbol in table and verify type,
	 *  symbol must represent a parameterized function
	 * @param name name of symbol to be found
	 * @return the function with the name
	 */
	public SymbolMap.ParameterizedFunction getFuntionFor (String name)
	{
		SymbolMap.Named sym;
		if ((sym = p.getSymbolMap ().lookup (name)) == null) return null;
		return AbstractParameterizedFunction.verifyFunction (sym);
	}


	/**
	 * apply derivative transform.
	 *  function must be defined transform.
	 *  function object must implement polynomial calculus.
	 * @param f the source function (must be DefinedTransform)
	 * @param toBeNamed the name to apply for use as cache in symbol table
	 * @return the derivative function to be used as source for transform
	 * @throws RuntimeException when source not acceptable
	 */
	public SymbolMap.ParameterizedFunction getDerivativeFor
			(SymbolMap.ParameterizedFunction f, String toBeNamed)
	throws RuntimeException
	{
		if (f instanceof DefinedTransform)
		{
			Function<T> t = DefinedTransform.toTransform (f);
			return getDerivativeFor (toBeNamed, calculusFor (t), t);
		}
		// function must have been defined as transform to include implementation of calculus
		throw new RuntimeException ("Derivative requires defined transform source: " + toBeNamed);
	}
	SymbolMap.ParameterizedFunction getDerivativeFor (String name, TransformCalculus<T> transform, Function<T> source)
	{
		AbstractFunction<T> result;
		Function<T> function = transform.getFunctionDerivative (source);
		// function will be added to symbol table as defined transform identified using prime (') syntax
		p.processDefinedFunction (result = newDefinedTransform (name, function));
		// function is now in symbol table and ready to be invoked
		return result;
	}
	DefinedTransform<T> newDefinedTransform (String name, Function<T> f)
	{
		String baseName = name.replaceAll ("'", "");
		List<String> parameters = Subroutine.listOfNames ("x");
		String exp = (name.length () - baseName.length() == 2)? "^2": "";
		// defined transform object collects name, parameter list, declaration tokens, and function object
		DefinedTransform<T> transform = new DefinedTransform<T> (name, parameters, null, f);
		transform.setDescription ("delta" + exp + " " + baseName + "(x) / delta x" + exp);
		return transform;
	}


	/**
	 * find first derivative in symbol table or compute
	 * @param function the source function
	 * @return the derivative function
	 */
	public SymbolMap.ParameterizedFunction getFirstDerivativeFor
		(
			SymbolMap.ParameterizedFunction function
		)
	{
		String named = function.getName () + "'";
		// look in symbol table to see if function was previously posted
		SymbolMap.ParameterizedFunction f = getFuntionFor (named);
		// if not previously posted then it must be constructed
		if (f == null) f = getDerivativeFor (function, named);
		// either way, return derivative
		return f;
	}


	/**
	 * find second derivative in symbol table or compute
	 * @param function the source function
	 * @return the derivative function
	 */
	public SymbolMap.ParameterizedFunction getSecondDerivativeFor
		(
			SymbolMap.ParameterizedFunction function
		)
	{
		SymbolMap.ParameterizedFunction f;
		String named = function.getName () + "''";
		if ((f = getFuntionFor (named)) != null) return f;
		// if not found in symbol table, compute derivative of first order function
		return getDerivativeFor (getFirstDerivativeFor (function), named);
	}


	/**
	 * compute function derivative
	 * @param op wrapper for the function
	 * @param parameters the parameter to the function
	 * @param delta value of delta to use for approximation
	 * @param count number of derivatives first=1 or second=2
	 * @return computed result
	 */
	public ValueManager.GenericValue approximateDerivative
		(
			SymbolMap.ParameterizedFunction op,
			ValueManager.GenericValue parameters,
			T delta, int count
		)
	{
		T x = p.getValueManager ().toDiscrete (parameters);

		if (p.traceIsEnabled ()) System.out.println
		(
			"f'" + count +
			",   delta = " + delta
		);

		switch (count)
		{
			case 1: return firstOrderDerivative (op, x, delta);
			case 2: return secondOrderDerivative (op, x, delta);
		}
		return p.internalError ();
	}


	/**
	 * compute first derivative
	 * @param op wrapper for the function
	 * @param x the parameter to the function
	 * @param delta the LIM value to use
	 * @return computed result
	 */
	public ValueManager.GenericValue firstOrderDerivative
	(SymbolMap.ParameterizedFunction op, T x, T delta)
	{
		if (p.traceIsEnabled ())
		{
			System.out.println ("Derivative: " + op.getName () + "'(" + x + ") <> " + delta);
		}
		T result = derivatives.firstOrderDerivative
				(p.getConversionManager ().toSimpleFunction (op), x, delta);
		return p.getValueManager ().newDiscreteValue (result);
	}


	/**
	 * compute second derivative
	 * @param op wrapper for the function
	 * @param x the parameter to the function
	 * @param delta the LIM value to use
	 * @return computed result
	 */
	public ValueManager.GenericValue secondOrderDerivative
	(SymbolMap.ParameterizedFunction op, T x, T delta)
	{
		if (p.traceIsEnabled ())
		{
			System.out.println ("Derivative: " + op.getName () + "''(" + x + ") <> " + delta);
		}
		T result = derivatives.secondOrderDerivative
				(p.getConversionManager ().toSimpleFunction (op), x, delta);
		return p.getValueManager ().newDiscreteValue (result);
	}


}

