
package net.myorb.math.expressions.evaluationstates;

import net.myorb.math.computational.DCT;
import net.myorb.math.computational.GeneralQuadrature;
import net.myorb.math.computational.ClenshawCurtisQuadrature;

import net.myorb.math.expressions.symbols.DefinedTransform;
import net.myorb.math.expressions.symbols.GenericWrapper;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.DataConversions;
import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.SymbolMap;

import net.myorb.data.abstractions.Function;
//import net.myorb.math.Function;
import net.myorb.math.SpaceManager;

import java.util.List;

/**
 * quadrature evaluation processing
 * @author Michael Druckman
 * @param <T> data type
 */
public class Quadrature<T>
{


	public Quadrature (ValueManager<T> valueManager, ExpressionSpaceManager<T> spaceManager)
	{
		this.spaceManager = spaceManager;
		conversion = new DataConversions<T> (spaceManager);
		generalQuadrature = new GeneralQuadrature<T> (spaceManager);
		this.valueManager = valueManager;
	}
	protected ExpressionSpaceManager<T> spaceManager;
	protected GeneralQuadrature<T> generalQuadrature;
	protected DataConversions<T> conversion;
	protected ValueManager<T> valueManager;


	/**
	 * wrapper for operations
	 */
	public class Invocation implements GenericWrapper.GenericFunction<T> 
	{

		public Invocation (SymbolMap.ExecutableUnaryOperator op) { this.op = op; }

		public GenericValue eval (SymbolMap.ExecutableUnaryOperator op, GenericValue parameter)
		{
			throw new RuntimeException ("Unimplemented calculus Invocation");
		}

		public GenericValue eval
		(GenericValue parameter) { return eval (op, parameter); }
		SymbolMap.ExecutableUnaryOperator op;

		public SpaceManager<T> getSpaceDescription () { return (SpaceManager<T>) spaceManager; }
		public SpaceManager<T> getSpaceManager () { return (SpaceManager<T>) spaceManager; }
		public T eval (T x) { throw new RuntimeException ("Internal error"); }

	}


	/**
	 * compute op(hi) - op(lo)
	 * @param op the function to be evaluated
	 * @param parameters an array of 2 values (lo, hi)
	 * @return the computed result
	 */
	public ValueManager.GenericValue intervalEvaluation
	(SymbolMap.ExecutableUnaryOperator op, ValueManager.GenericValue parameters)
	{
		List<T> range = valueManager.toArray (parameters);									// (lo, hi)
		return intervalEvaluation (conversion.toSimpleFunction (op), range);
	}
	public ValueManager.GenericValue intervalEvaluation
	(Function<T> f, List<T> range)
	{
		T fhi = f.eval (range.get (1)), flo = f.eval (range.get (0));						// function hi & function lo
		T dif = spaceManager.add (fhi, spaceManager.negate (flo));							// f(hi) - f(lo)
		return valueManager.newDiscreteValue (dif);
	}
	public GenericWrapper.GenericFunction<T> IntervalFor (SymbolMap.ExecutableUnaryOperator op)
	{
		return new Invocation (op)
		{
			public GenericValue eval (SymbolMap.ExecutableUnaryOperator op, GenericValue parameter)
			{
				return intervalEvaluation (op, parameter);
			}
		};
	}


	/**
	 * compute quadrature approximation
	 * @param op the function to be evaluated
	 * @param parameters an array of 3 values (lo, hi, error)
	 * @return the computed result
	 */
	public ValueManager.GenericValue quadratureApproximation
	(SymbolMap.ExecutableUnaryOperator op, ValueManager.GenericValue parameters)
	{
		List<T> array = valueManager.toArray (parameters);									// (lo, hi, error)
		T error = array.get (2), hi = array.get (1), lo = array.get (0);
		T result = generalQuadrature.integrate (op, lo, hi, error);
		return valueManager.newDiscreteValue (result);
	}
	public GenericWrapper.GenericFunction<T> quadApproxFor (SymbolMap.ExecutableUnaryOperator op)
	{
		return new Invocation (op)
		{
			public GenericValue eval (SymbolMap.ExecutableUnaryOperator op, GenericValue parameter)
			{
				return quadratureApproximation (op, parameter);
			}
		};
	}


	/**
	 * compute quadrature approximation
	 * @param op the function to be evaluated
	 * @param parameters an array of 3 values (lo, hi, dx)
	 * @return the computed result
	 */
	public ValueManager.GenericValue trapezoidalApproximation
	(SymbolMap.ExecutableUnaryOperator op, ValueManager.GenericValue parameters)
	{
		List<T> array = valueManager.toArray (parameters);									// (lo, hi, dx)
		T dx = array.get (2), hi = array.get (1), lo = array.get (0);
		T result = generalQuadrature.approximation (op, lo, hi, dx);
		return valueManager.newDiscreteValue (result);
	}
	public GenericWrapper.GenericFunction<T> trapApproxFor (SymbolMap.ExecutableUnaryOperator op)
	{
		return new Invocation (op)
		{
			public GenericValue eval (SymbolMap.ExecutableUnaryOperator op, GenericValue parameter)
			{
				return trapezoidalApproximation (op, parameter);
			}
		};
	}


	/**
	 * compute quadrature adjustment
	 * @param op the function to be evaluated
	 * @param parameters an array of 3 values (lo, hi, dx)
	 * @return the computed result
	 */
	public ValueManager.GenericValue trapezoidalAdjustmnet
	(SymbolMap.ExecutableUnaryOperator op, ValueManager.GenericValue parameters)
	{
		List<T> array = valueManager.toArray (parameters);									// (lo, hi, dx)
		T dx = array.get (2), hi = array.get (1), lo = array.get (0);
		T result = generalQuadrature.adjustment (op, lo, hi, dx);
		return valueManager.newDiscreteValue (result);
	}
	public GenericWrapper.GenericFunction<T> trapAdjustFor (SymbolMap.ExecutableUnaryOperator op)
	{
		return new Invocation (op)
		{
			public GenericValue eval (SymbolMap.ExecutableUnaryOperator op, GenericValue parameter)
			{
				return trapezoidalAdjustmnet (op, parameter);
			}
		};
	}


	/**
	 * compute Clenshaw-Curtis quadrature
	 * @param op the function to be evaluated (must be DCT)
	 * @param parameters an empty array (carries meta-data flag)
	 * @return the computed result
	 */
	public ValueManager.GenericValue ccqApproximation
	(SymbolMap.ExecutableUnaryOperator op, ValueManager.GenericValue parameters)
	{
		DCT.Transform<T> transform = toDCT (op);
		if (transform == null) throw new RuntimeException ("Function must be Discrete Cosine Transform");
		T integrationResult = ClenshawCurtisQuadrature.integrate (transform, spaceManager);
		return valueManager.newDiscreteValue (integrationResult);
	}
	public GenericWrapper.GenericFunction<T> ccqApproxFor (SymbolMap.ExecutableUnaryOperator op)
	{
		return new Invocation (op)
		{
			public GenericValue eval (SymbolMap.ExecutableUnaryOperator op, GenericValue parameter)
			{
				return ccqApproximation (op, parameter);
			}
		};
	}


	/**
	 * expecting DCT object
	 * @param f the function to cast as DCT
	 * @return the DCT object or NULL for all other cases
	 */
	public DCT.Transform<T> toDCT (SymbolMap.ExecutableUnaryOperator f)
	{
		Function<T> t = DefinedTransform.toTransform (f);
		if (t != null) return DCT.toTransform (t);
		return null;
	}



}
