
package net.myorb.math.computational;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.DataConversions;
import net.myorb.math.expressions.TypedRangeDescription;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.symbols.DefinedTransform;
import net.myorb.math.expressions.symbols.GenericWrapper;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;
import net.myorb.math.Polynomial.PowerFunction;
import net.myorb.data.abstractions.Function;
import net.myorb.math.*;

import net.myorb.data.abstractions.PrimitiveRangeDescription;

/**
 * calculate numerical approximation of function integral using one of various quadrature algorithms
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class GeneralQuadrature<T>
{

	public GeneralQuadrature
	(ExpressionSpaceManager<T> manager)
	{ this.manager = manager; cvt = new DataConversions<T> (manager); this.operatorWrapper = new GenericWrapper<T> (manager); }
	protected GenericWrapper<T> operatorWrapper = null;
	protected ExpressionSpaceManager<T> manager = null;
	protected DataConversions<T> cvt = null;
	protected boolean trace = false;

	/**
	 * condition raised for functions unable to support quadrature
	 */
	public static class Unsupported extends RuntimeException
	{
		Unsupported () { super ("Quadrature not supported for function specified"); }
		private static final long serialVersionUID = -5995117341904406254L;
	}

	/**
	 * computed integral of function
	 *  over specified range with error limit
	 * @param function the function to evaluate
	 * @param lo lower bound of evaluation range
	 * @param hi upper bound of evaluation range
	 * @param resolution requested error limit
	 * @return calculated integral
	 */
	public T integrate (SymbolMap.ExecutableUnaryOperator function, T lo, T hi, T resolution)
	{
		if (function instanceof DefinedTransform)
		{
			return integrate (DefinedTransform.toTransform (function), lo, hi, resolution);
		}
		else if (function instanceof MultiDimensional.Function)
		{
			return integrateOverDomain (function, lo, hi, resolution);
		}
		else
		{
			return integrate (operatorWrapper.functionFor (function), lo, hi, resolution);
		}
	}

	/**
	 * computed integral of function
	 *  over specified range with x-axis delta
	 * @param function the function to evaluate
	 * @param lo lower bound of evaluation range
	 * @param hi upper bound of evaluation range
	 * @param delta requested x-axis delta
	 * @return calculated integral
	 */
	public T approximation (SymbolMap.ExecutableUnaryOperator function, T lo, T hi, T delta)
	{
		if (function instanceof MultiDimensional.Function)
		{
			Function<T> f = cvt.toGeneric1DFunction (function);
			PrimitiveRangeDescription range = new PrimitiveRangeDescription (manager.toNumber (lo), manager.toNumber (hi), manager.toNumber (delta));
			TypedRangeDescription.TypedRangeProperties<T> typedRange = TypedRangeDescription.getTypedRangeProperties (range, manager);
			IterativeIntegralApproximation<T> engine = new IterativeIntegralApproximation<T>(typedRange, f, true);
			engine.execute (- (int) (Math.log (manager.convertToDouble (delta)) / Math.log (2)), 1);
			return engine.getResult ();
		}
		else throw new Unsupported ();
	}

	/**
	 * computed integral adjustment
	 *  over specified range with x-axis delta
	 * @param function the function to evaluate
	 * @param lo lower bound of evaluation range
	 * @param hi upper bound of evaluation range
	 * @param delta requested x-axis delta
	 * @return calculated adjustment
	 */
	public T adjustment (SymbolMap.ExecutableUnaryOperator function, T lo, T hi, T delta)
	{
		if (function instanceof MultiDimensional.Function)
		{
			Function<T> f = cvt.toGeneric1DFunction (function);
			T flo = f.eval (lo), fhi = f.eval (hi), HALF = manager.invert (manager.newScalar (-2));
			return manager.multiply (HALF, manager.multiply (manager.add (flo, fhi), delta));
		}
		else throw new Unsupported ();
	}

	/**
	 * TSQ for MultiDimensional functions
	 * @param function the function to evaluate
	 * @param lo lower bound of evaluation range
	 * @param hi upper bound of evaluation range
	 * @param resolution requested error limit
	 * @return calculated integral
	 */
	public T integrateOverDomain (SymbolMap.ExecutableUnaryOperator function, T lo, T hi, T resolution)
	{
		if (trace) System.out.println
			("MultiDimensional.Function [" + lo + "," + hi + "]");
		Function<Double> realFunction = cvt.toReal1DFunction (function);
		return integrate (new FunctionWrapper (realFunction), lo, hi, resolution);
	}

	/**
	 * CCQ for transform
	 *  otherwise TSQ for function
	 * @param transform possibly DCT
	 * @param lo lower bound of evaluation range
	 * @param hi upper bound of evaluation range
	 * @param resolution requested error limit
	 * @return calculated integral
	 */
	public T integrateTransform (Function<T> transform, T lo, T hi, T resolution)
	{
		if (trace) System.out.println ("DefinedTransform [" + lo + "," + hi + "]");
		if (transform instanceof DCT.Transform)
		{
			DCT.Transform<T> t = (DCT.Transform<T>) transform;
			double integral = ClenshawCurtisQuadrature.integrate (t.getCoefficients ());
			T result = manager.multiply (t.getSlope (), manager.convertFromDouble (integral));
			if (trace) System.out.println ("CCQ Integral: " + result);
			return result;
		}
		return integrate (transform, lo, hi, resolution);
	}

	/**
	 * Tanh-Sinh Quadrature for general function
	 * @param function the function to evaluate
	 * @param lo lower bound of evaluation range
	 * @param hi upper bound of evaluation range
	 * @param resolution requested error limit
	 * @return calculated integral
	 */
	public T integrate (Function<T> function, T lo, T hi, T resolution)
	{
		Function<Double> realFunction = cvt.toRealFunction (function);
		return integrate (new FunctionWrapper (realFunction), lo, hi, resolution);
	}

	/**
	 * Tanh-Sinh Quadrature for wrapped function
	 * @param function the function to evaluate
	 * @param lo lower bound of evaluation range
	 * @param hi upper bound of evaluation range
	 * @param resolution requested error limit
	 * @return calculated integral
	 */
	public T integrate (FunctionWrapper function, T lo, T hi, T resolution)
	{
		if (trace) System.out.println ("TSQ [" + lo + "," + hi + "]");
		double a = manager.convertToDouble (lo), b = manager.convertToDouble (hi), error = manager.convertToDouble (resolution);
		TanhSinhQuadratureTables.ErrorEvaluation eEval = new TanhSinhQuadratureTables.ErrorEvaluation ();
		double result = TanhSinhQuadratureAlgorithms.Integrate
		(
			function,		//!< [in] integrand
			a,				//!< [in] left limit of integration
			b,				//!< [in] right limit of integration
			error,			//!< [in] desired bound on error
			eEval
		);
		if (eEval.errorEstimate > error)
		{ throw new RuntimeException ("Estimate of error was greater than requested"); }
		if (trace) System.out.println ("ErrorEstimate="+eEval.errorEstimate + "  Evaluations="+eEval.numFunctionEvaluations);
		T computedRresult = manager.convertFromDouble (result);
		if (trace) System.out.println ("Integral: " + result);
		return computedRresult;
	}

	/**
	 * wrapper for Function implementing DoubleExponential function interface
	 */
	static class FunctionWrapper implements LinearCoordinateChange.StdFunction<Double>
	{

		/**
		 * @param realFunction function being wrapped
		 */
		FunctionWrapper (Function<Double> realFunction)
		{
			this.realFunction = realFunction;
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.Function#eval(java.lang.Object)
		 */
		public Double eval (Double x)
		{
			return realFunction.eval (x);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.Function#getSpaceManager()
		 */
		public SpaceManager<Double> getSpaceDescription () { return new DoubleFloatingFieldManager (); }
		public SpaceManager<Double> getSpaceManager () { return new DoubleFloatingFieldManager (); }

		/* (non-Javadoc)
		 * @see net.myorb.math.computational.LinearCoordinateChange.StdFunction#getSlope()
		 */
		public Double getSlope () { return 1.0; }
		public PowerFunction<Double> describeLine() { return null; }
		public Double getIntercept() { return null; }
		Function<Double> realFunction;
	}

}
