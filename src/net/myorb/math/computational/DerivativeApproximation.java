
package net.myorb.math.computational;

import net.myorb.math.expressions.symbols.GenericWrapper;

import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.data.abstractions.Function;
import net.myorb.math.SpaceManager;

/**
 * computational algorithms for Derivative Approximation
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class DerivativeApproximation<T>
	implements DerivativeApproximationEngine<T>
{


	/**
	 * wrapper for first two function derivatives
	 * @param <T> the data type
	 */
	public interface Functions<T>
	{
		/**
		 * @return the first derivative function
		 */
		GenericWrapper.GenericFunction<T> first ();

		/**
		 * @return the second derivative function
		 */
		GenericWrapper.GenericFunction<T> second ();

		/**
		 * @param order the order required, 1 = first, 2 = second
		 * @return the generic for of the derivative function
		 */
		GenericWrapper.GenericFunction<T> forOrder (int order);
	}


	/**
	 * @param f functions to base derivatives on
	 * @param delta the delta value for the approximation
	 * @return a wrapper for the first two function derivatives
	 * @param <T> type of data
	 */
	public static <T> Functions<T> getDerivativesFor (Function<T> f, T delta)
	{
		return new DerivativeApproximation<T> (f.getSpaceDescription ())
				.getDerivativeApproximationFunctions (f, delta);
	}


	public DerivativeApproximation (SpaceManager<T> sm)
	{ this.sm = sm; this.dimensionMultiplier = sm.getOne (); }
	public DerivativeApproximation (SpaceDescription<T> sm) { this ((SpaceManager<T>) sm);  }
	protected boolean dimensionMultiplierAdjusted = false;
	protected T dimensionMultiplier;
	protected SpaceManager<T> sm;


	/**
	 * @param newMultiplierValue the new value for the multiplier
	 */
	public void setDimensionMultiplier (T newMultiplierValue)
	{
		this.dimensionMultiplier = newMultiplierValue;
		this.dimensionMultiplierAdjusted = true;
	}

	/**
	 * reset to scalar one in T coordinates
	 */
	public void resetDimensionMultiplier ()
	{
		this.dimensionMultiplier = sm.getOne ();
		this.dimensionMultiplierAdjusted = false;
	}


	/**
	 * adjust offset for dimension partial
	 * @param x the value of the point in multi-dimensional space
	 * @param delta the delta to add into specified dimension
	 * @return the sum of the point and the offset
	 */
	public T deltaOffset (T x, T delta)
	{
		T dimensionOffset = delta;
		if (dimensionMultiplierAdjusted)
		{ dimensionOffset = sm.multiply (dimensionMultiplier, delta); }
		return sm.add (x, dimensionOffset);
	}


	/**
	 * compute first derivative
	 * @param op wrapper for the function
	 * @param x the parameter to the function
	 * @param delta the LIM value to use
	 * @return computed result
	 */
	public T firstOrderDerivative (Function<T> op, T x, T delta)
	{
		T halfDelta = sm.multiply (delta, sm.invert (sm.newScalar (2)));
		T xPlus = deltaOffset (x, halfDelta), xMinus = deltaOffset (x, sm.negate (halfDelta));
		return riseOverRun (op, xMinus, xPlus, delta);
	}


	/**
	 * compute second derivative
	 * @param op wrapper for the function
	 * @param x the parameter to the function
	 * @param delta the LIM value to use
	 * @return computed result
	 */
	public T secondOrderDerivative (Function<T> op, T x, T delta)
	{
		T xPlus = deltaOffset (x, delta), xMinus = deltaOffset (x, sm.negate (delta));
		T dlo = riseOverRun (op, xMinus, x, delta), dhi = riseOverRun (op, x, xPlus, delta);
		return riseOverRun (dlo, dhi, delta);
	}


	/**
	 * compute rise over run
	 * @param op the wrapper for the function
	 * @param lo the low value of the run range
	 * @param hi the high value of the run range
	 * @param delta the difference between lo and hi
	 * @return computed value
	 */
	public T riseOverRun (Function<T> op, T lo, T hi, T delta)
	{
		return riseOverRun (f (lo, op), f (hi, op), delta);
	}


	/**
	 * slope of difference between calls
	 * @param flo function computed at lo
	 * @param fhi function computed at hi
	 * @param delta the run value for the calculation
	 * @return difference of functions values over run
	 */
	public T riseOverRun (T flo, T fhi, T delta)
	{
		T dif = sm.add (fhi, sm.negate (flo));
		return sm.multiply (dif, sm.invert (delta));
	}


	/**
	 * evaluate function at point
	 * @param x the value within T of point to compute derivative
	 * @param f the function for which we approximate derivative
	 * @return the computed derivative value
	 */
	public T f (T x, Function<T> f) { return f.eval (x); }


	/**
	 * @param f the function for which we approximate derivative
	 * @param delta the run value for the derivative calculations being made
	 * @return the derivative approximation functions
	 */
	public Functions<T> getDerivativeApproximationFunctions (Function<T> f, T delta)
	{
		return new FunctionDerivatives<T> (f, delta, this);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.DerivativeApproximationEngine#approximateDerivativesFor(net.myorb.data.abstractions.Function, java.lang.Object)
	 */
	public Functions<T> approximateDerivativesFor (Function<T> f, T delta)
	{
		return getDerivativeApproximationFunctions (f, delta);
	}


}


/**
 * use instance of DerivativeApproximation
 *  to implement function instances of first two derivatives of a specified function
 * @param <T> type of component values on which operations are to be executed
 */
class FunctionDerivatives<T> implements DerivativeApproximation.Functions<T>
{

	FunctionDerivatives
		(
			Function<T> f, T delta,
			DerivativeApproximation<T> approx
		)
	{
		this.approx = approx; this.f = f; this.delta = delta;
		this.operatorWrapper = new GenericWrapper<T>(f.getSpaceDescription ());
	}
	protected GenericWrapper<T> operatorWrapper;
	protected DerivativeApproximation<T> approx;
	protected Function<T> f; T delta;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.DerivativeApproximation.Functions#first()
	 */
	public GenericWrapper.GenericFunction<T> first ()
	{ return operatorWrapper.functionFor (new FirstDerivative ()); }
	class FirstDerivative implements Function<T>
	{
		public T eval (T x)
		{ return approx.firstOrderDerivative (f, x, delta); }
		public SpaceDescription<T> getSpaceDescription ()
		{ return f.getSpaceDescription (); }
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.DerivativeApproximation.Functions#second()
	 */
	public GenericWrapper.GenericFunction<T> second ()
	{ return operatorWrapper.functionFor (new SecondDerivative ()); }
	class SecondDerivative implements Function<T>
	{
		public T eval (T x)
		{ return approx.secondOrderDerivative (f, x, delta); }
		public SpaceDescription<T> getSpaceDescription ()
		{ return f.getSpaceDescription (); }
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.DerivativeApproximation.Functions#forOrder(int)
	 */
	public GenericWrapper.GenericFunction<T> forOrder (int order)
	{
		switch (order)
		{
			case 1: return first ();
			case 2: return second ();
			default: 
		}
		throw new RuntimeException
		(
			"Approximations available for First and Second derivatives only"
		);
	}

}

