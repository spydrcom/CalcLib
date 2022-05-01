
package net.myorb.math.expressions.evaluationstates;

import net.myorb.math.expressions.symbols.*;
import net.myorb.math.computational.*;
import net.myorb.math.expressions.*;

import net.myorb.data.abstractions.ErrorHandling;
import net.myorb.data.abstractions.Function;

import java.util.List;

/**
 * processing methods for each operator type
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
class OperatorProcessing<T> extends Assignments<T>
{


	/**
	 * Operator not recognized
	 */
	public static class UnknownOperator extends FatalError
	{
		public UnknownOperator (String name) { super ("Unrecognized operation: " + name); }
		private static final long serialVersionUID = -9178623687801173996L;
	}

	/**
	 * Illegal Meta-data in processing stream
	 */
	public static class IllegalMetadata extends FatalError
	{
		public IllegalMetadata () { super ("Meta-data error"); }
		private static final long serialVersionUID = -3954795635454493657L;
	}


	/*
	 * operation execution
	 */


	/**
	 * execute an operator effecting value stack
	 * @param opSymbol the symbol map descriptor of the operator
	 * @throws UnknownOperator for unrecognized operation
	 */
	public void execute (SymbolMap.Operation opSymbol) throws UnknownOperator
	{
		if (traceIsEnabled ())
		{
			System.out.println ("execute: " + opSymbol.getName ());
		}
		switch (opSymbol.getSymbolType ())
		{
			case ASSIGNMENT: processAssignment (opSymbol); break;
			case BINARY: process ((SymbolMap.BinaryOperator) opSymbol); break;
			case PARAMETERIZED: process ((SymbolMap.ParameterizedFunction) opSymbol); break;
			case POSTFIX: process ((SymbolMap.UnaryPostfixOperator) opSymbol); break;
			case UNARY: process ((SymbolMap.UnaryOperator) opSymbol); break;
			default: throw new UnknownOperator (opSymbol.getName ());
		}
	}


	/**
	 * process an operation carrying the type of Assignment
	 * @param opSymbol the symbol map descriptor of the operator
	 * @throws UnknownOperator for unrecognized assignment
	 */
	public void processAssignment (SymbolMap.Operation opSymbol) throws UnknownOperator
	{
		if (opSymbol instanceof SymbolMap.VariableAssignment)
		{ process ((SymbolMap.VariableAssignment) opSymbol); }
		else if (opSymbol instanceof SymbolMap.IndexedVariableAssignment)
		{ process ((SymbolMap.IndexedVariableAssignment) opSymbol); }
		else throw new UnknownOperator (opSymbol.getName ());
	}


	/**
	 * invoke unary post-fix operator
	 * @param op SymbolMap.UnaryPostfixOperator object
	 */
	public void process (SymbolMap.UnaryPostfixOperator op)
	{
		ValueStack<T> valueStack = getValueStack ();
		ValueManager.GenericValue v = op.execute (valueStack.pop ());
		if (v != null) valueStack.push (v);
	}


	/**
	 * invoke unary operator
	 * @param op SymbolMap.UnaryOperator object
	 */
	public void process (SymbolMap.UnaryOperator op)
	{
		SymbolMap.Named derivative = null;
		ValueStack<T> valueStack = getValueStack ();
		ValueManager.GenericValue parameter = valueStack.peek ();
		if ((derivative = lookupDerivative (op, parameter.getMetadata ())) != null)
		{
			parameter.setMetadata (null);
			execute ((SymbolMap.Operation) derivative);
			return;
		}
		valueStack.push (op.execute (valueStack.pop ()));
	}


	/**
	 * invoke binary operator
	 * @param op SymbolMap.BinaryOperator object
	 */
	public void process (SymbolMap.BinaryOperator op)
	{
		ValueManager.GenericValue right, left;
		ValueStack<T> valueStack = getValueStack ();
		ValueManager.GenericValue top = valueStack.pop ();

		if (top instanceof ValueManager.ValueList)
		{
			ValueManager.ValueList list = (ValueManager.ValueList)top;
			List<ValueManager.GenericValue> items = list.getValues ();
			left = items.get (0); right = items.get (1);
		}
		else
		{
			right = top;
			left = valueStack.pop ();
		}

		if (traceIsEnabled ()) System.out.println (op.getName() + " => left: " + left + " right: " + right);
		valueStack.push (op.execute (left, right));
	}


	/*
	 * function execution
	 */


	/**
	 * invoke parameterized function
	 * @param op SymbolMap.ParameterizedFunction object
	 */
	public void process (SymbolMap.ParameterizedFunction op)
	{
		ValueStack<T> valueStack = getValueStack ();
		ValueManager.GenericValue parameters = valueStack.pop ();
		if (traceIsEnabled ()) System.out.println ("parameters: " + parameters);
		if (op instanceof Subroutine) DefinedFunction.verifySubroutine (op).updateSymbolTable (getSymbolMap ());
		valueStack.push (computeResultBasedOnMetadataOfParameters (op, parameters));
	}


	/**
	 * compute result from parameter meta-data
	 * @param op the named operation for basis of computation
	 * @param parameters the parameters supplied to the call
	 * @return the computed value
	 */
	ValueManager.GenericValue computeResultBasedOnMetadataOfParameters
		(SymbolMap.ParameterizedFunction op, ValueManager.GenericValue parameters)
	{
		ValueManager.Metadata meta;
		if ((meta = parameters.getMetadata ()) instanceof CalculusMarkers.CalculusMetadata)
		{
			// check meta-data associated with parameters to operator for calculus requirements
			return calculus (op, parameters, (CalculusMarkers.CalculusMetadata) meta);
		}
		return op.execute (parameters);				// simple f(x) computation
	}


	/*
	 * roots of functions (and derivatives in particular)
	 */


	/**
	 * adjust approximation in attempts to improve
	 * @param functionName name of function seeking root
	 * @param variableName name of variable holding approximation
	 */
	public void findFunctionRoot (String functionName, String variableName)
	{
		findFunctionRoot (lookupFunction (functionName), variableName);
	}


	/**
	 * adjust approximation in attempts to improve
	 * @param functionName name of function seeking max/min
	 * @param variableName name of variable holding approximation
	 */
	public void findFunctionMaxMin (String functionName, String variableName)
	{
		DerivativeApproximation.Functions<T>
			derivatives = DerivativeApproximation.getDerivativesFor
				(
					lookupFunction (functionName), getRootsManager ().getDefaultDelta ()
				);
		findFunctionRoot (derivatives.first (), variableName);
		identifyType (derivatives.second (), variableName);
	}


	/**
	 * generate GUI message identifying type (max/min)
	 * @param secondDerivative the second derivative function
	 * @param variableName the variable holding the approximated root
	 */
	public void identifyType (Function<T> secondDerivative, String variableName)
	{
		T secondDerivativeValue = secondDerivative.eval (getDiscreteFrom (variableName));
		String type = getSpaceManager ().isNegative (secondDerivativeValue) ? "Local Maximum" : "Local Minimum";
		throw new ErrorHandling.Notification (type + " has been identified");
	}


	/*
	 * calculus operator implementation
	 */


	/**
	 * @return Quadrature object allocated on first reference
	 */
	Quadrature<T> getQuadrature ()
	{
		if (quadrature == null)
		{ quadrature = new Quadrature<T> (getValueManager (), getSpaceManager ()); }
		return quadrature;
	}
	Quadrature<T> quadrature = null;


	/**
	 * @param op a function with attached calculus meta-data
	 * @param parameters the parameters to the operation specified in meta-data
	 * @param calculusMetadata the calculus meta-data connected to the function
	 * @return the result of the operation
	 */
	ValueManager.GenericValue calculus
		(
			SymbolMap.ParameterizedFunction op, ValueManager.GenericValue parameters,
			CalculusMarkers.CalculusMetadata calculusMetadata
		)
	{
		if (calculusMetadata instanceof CalculusMarkers.IntervalEvaluationMarker)
		{
			return getQuadrature ().intervalEvaluation (op, parameters);						// f(hi) - f(lo)
		}
		else if (calculusMetadata instanceof CalculusMarkers.ClenshawCurtisEvaluationMarker)
		{
			return getQuadrature ().ccqApproximation (op, parameters);							// INTEGRAL f (cos t) | (0, pi)
		}
		else if (calculusMetadata instanceof CalculusMarkers.TanhSinhEvaluationMarker)
		{
			return getQuadrature ().quadratureApproximation (op, parameters);					// INTEGRAL f | (lo, hi, error)
		}
		else if (calculusMetadata instanceof CalculusMarkers.TrapezoidalEvaluationMarker)
		{
			return getQuadrature ().trapezoidalApproximation (op, parameters);					// INTEGRAL f | (lo, hi, dx)
		}
		else if (calculusMetadata instanceof CalculusMarkers.TrapezoidalAdjustmentMarker)
		{
			return getQuadrature ().trapezoidalAdjustmnet (op, parameters);						// INTEGRAL f | (lo, hi, dx)
		}
		else if (calculusMetadata instanceof CalculusMarkers.DerivativeMetadata)
		{
			return computeDerivative (op, parameters, calculusMetadata);						// f'(x)
		}
		else throw new IllegalMetadata ();
	}


	CalculusMarkers.DerivativeMetadata<T> toDerivativeMetadata
			(ValueManager.Metadata calculusMetadata)
	{
		@SuppressWarnings("unchecked")
		CalculusMarkers.DerivativeMetadata<T>
		derivativeMetadata = (CalculusMarkers.DerivativeMetadata<T>) calculusMetadata;
		return derivativeMetadata;
	}


	/**
	 * compute derivative.
	 *  approximation or real depending on meta-data.
	 * @param op the named operation for basis of computation
	 * @param parameters the parameters supplied to the call, must be singleton value
	 * @param calculusMetadata a derivative meta-data block
	 * @return the computed value
	 */
	ValueManager.GenericValue computeDerivative
		(
			SymbolMap.ParameterizedFunction op, ValueManager.GenericValue parameters,
			CalculusMarkers.CalculusMetadata calculusMetadata
		)
	{
		CalculusMarkers.DerivativeMetadata<T>
			derivativeMetadata = toDerivativeMetadata (calculusMetadata);
		// process calculus operations as prescribed in meta-data object
		OperatorSupport<T> ops = new OperatorSupport<T> (this);
		int count = derivativeMetadata.getCount ();
	
		if (derivativeMetadata.usesApproximation ())	// approximation or transform?
		{
			return ops.approximateDerivative			// approximation requested using supplied delta
			(
				op, parameters,							// f(x) is identified by operation and parameters to operation
				derivativeMetadata.getDelta (),			// delta was specified in derivative request syntax f'( x <> dx )
				count									// count is number of prime characters f'(x) => 1 or f''(x) => 2
			);
		}
	
		SymbolMap.Named derivative = null;
		if ((derivative = lookupDerivative (op, derivativeMetadata)) != null)
		{
			ValueStack<T> valueStack;
			parameters.setMetadata (null);
			(valueStack = getValueStack ()).push (parameters);
			execute ((SymbolMap.Operation) derivative);
			return valueStack.pop ();
		}

		return ops.getDerivativeFunction (op, count)	// invoked operation must be transform
		.execute (parameters);							// evaluate at specified parameter
	}


	/**
	 * @param op the function marked with the meta-data
	 * @param m the meta-data being checked for calculus markers
	 * @return the symbol table item for the function
	 */
	public SymbolMap.Named lookupDerivative (SymbolMap.Named op, ValueManager.Metadata m)
	{
		SymbolMap.Named derivative = null;
		if (m instanceof CalculusMarkers.DerivativeMetadata)
		{
			CalculusMarkers.DerivativeMetadata<T>
				derivativeMetadata = toDerivativeMetadata (m);
			if (!derivativeMetadata.usesApproximation ())
			{
				String derivativeName = getDerivativeName (op, derivativeMetadata.getCount ());
				derivative = getSymbolMap ().lookup (derivativeName);
			}
		}
		return derivative;
	}
	String getDerivativeName (SymbolMap.Named op, int count)
	{ return op.getName () + (count==2 ? "''" : "'"); }


}

