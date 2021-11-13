
package net.myorb.math.computational;

import net.myorb.data.abstractions.Function;
import net.myorb.math.*;

/**
 * algorithms for computations of roots of functions
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class FunctionRoots<T> extends Tolerances<T>
{


	/**
	 * provide for inspection of data output by iteration
	 * @param <T> data type
	 */
	public interface IterationStatusMonitor<T>
	{
		void post (T lo, T hi, T mid, T fmid);
	}
	public void setIterationStatus
	(IterationStatusMonitor<T> iterationStatus) { this.iterationStatus = iterationStatus; }
	protected IterationStatusMonitor<T> iterationStatus;


	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 * @param lib an implementation of the power library
	 */
	public FunctionRoots
		(SpaceManager<T> manager, PowerLibrary<T> lib)
	{
		super (manager); setToleranceDefaults (lib);
	}


	/**
	 * @param lo current lo
	 * @param hi current hi value
	 * @param mid the midpoint between lo and hi
	 * @param fmid function of midpoint
	 */
	protected void postToMonitor
	(Value<T> lo, Value<T> hi, Value<T> mid, Value<T> fmid)
	{
		if (iterationStatus == null) return;

		iterationStatus.post
		(
			lo.getUnderlying (), hi.getUnderlying (), 
			mid.getInternal (), fmid.getInternal ()
		);
	}


	/**
	 * @param f the function seeking root
	 * @param from the lo end of interval containing the root
	 * @param to the hi end of interval containing the root
	 * @return TRUE : lo is negative and hi is positive
	 * @throws RuntimeException for initial conditions
	 */
	public boolean checkSign
		(
			Function<T> f, T from, T to
		)
	throws RuntimeException
	{
		T flo = f.eval (from), fhi = f.eval (to);
		boolean loNeg = isNeg (flo), hiNeg = isNeg (fhi);
		if (loNeg == hiNeg) raiseException ("LO and HI function values have same sign");
		return loNeg;
	}


	/**
	 * apply iteration formula seeking threshold proximity to root
	 * @param <T> the data type of the function
	 */
	interface Iterator<T>
	{
		/**
		 * @param lo the lo end of interval containing root
		 * @param hi the hi end of interval containing the root
		 * @return the value found within threshold proximity to root
		 */
		Value<T> next (Value<T> lo, Value<T> hi);
	}


	/**
	 * @param function the function seeking root
	 * @param iterator formula to iteratively apply
	 * @param lo the lo end of interval containing root
	 * @param hi the hi end of interval containing the root
	 * @param loNeg TRUE : lo is negative and hi is positive
	 * @return the value found within threshold proximity to root
	 * @throws RuntimeException for failed convergence
	 */
	public T iterate
		(
			Function<T> function,
			Iterator<T> iterator,
			Value<T> lo, Value<T> hi,
			boolean loNeg
		)
	throws RuntimeException
	{
		for (int i = maxIterations; i > 0; i--)
		{
			Value<T> mid = iterator.next (lo, hi), fmid = call (function, mid);
			if (iterationStatus != null) postToMonitor (lo, hi, mid, fmid);
			if (withinTolerance (fmid)) return mid.getUnderlying ();
			boolean midNeg = fmid.isNegative ();
			if (midNeg == loNeg) lo = mid;
			else hi = mid;
		}
		return raiseException ("Failed to converge");
	}
	public T iterate
		(
			Function<T> f,
			Iterator<T> iterator,
			T lo, T hi
		)
	{
		return iterate
		(
			f, iterator, forValue (lo), forValue (hi),
			checkSign (f, lo, hi)
		);
	}


	/**
	 * find function root using bisection
	 * @param f the function being evaluated
	 * @param from low end of range on x axis to look for root
	 * @param to high end of range on x axis to look for root
	 * @return the value of the root found
	 */
	public T bisectionMethod
		(
			Function<T> f, T from, T to
		)
	{
		Iterator<T> iterator =
			new BisectionMethodIterator<T>((SpaceManager<T>)f.getSpaceDescription ());
		return iterate (f, iterator, from, to);
	}
	public static class BisectionMethodIterator<T>
		extends Tolerances<T> implements Iterator<T>
	{
		public BisectionMethodIterator (SpaceManager<T> mgr) { super (mgr); }
		public Value<T> next (Value<T> lo, Value<T> hi)
		{ return hi.plus (lo).over (TWO); }
		Value<T> TWO = forValue (2);
	}


	/**
	 * find function root
	 *  using Linear Interpolation Method
	 * @param f the function being evaluated seeking root
	 * @param from low end of range on x axis to look for root
	 * @param to high end of range on x axis to look for root
	 * @return the value of the root found
	 */
	public T linearInterpolationMethod
		(
			Function<T> f, T from, T to
		)
	{
		Iterator<T> iterator =
			new LinearInterpolationMethodIterator<T> (f);
		return iterate (f, iterator, from, to);
	}
	public static class LinearInterpolationMethodIterator<T>
		extends Tolerances<T> implements Iterator<T>
	{
		public LinearInterpolationMethodIterator (Function<T> f)
		{ super ((SpaceManager<T>)f.getSpaceDescription ()); this.f = f; }
		protected Function<T> f;

		/* (non-Javadoc)
		 * @see net.myorb.math.computational.FunctionRoots.Iterator#next(net.myorb.math.Arithmetic.Value, net.myorb.math.Arithmetic.Value)
		 */
		public Value<T> next (Value<T> lo, Value<T> hi) { return midpoint (lo, hi); }

		/**
		 * midpoint formula used for linear Interpolation Method
		 * @param x1 low end of range on x axis to look for the root
		 * @param x2 high end of range on x axis to look for root
		 * @return the next value of the root approximation
		 */
		public Value<T> midpoint
			(
				Value<T> x1, Value<T> x2
			)
		{
			Value<T> fX1 = call (f, x1), fX2 = call (f, x2);
			// x3  =  [ x1*f(x2) - x2*f(x1) ]  /  [ f(x2) - f(x1) ]
			return x1.times (fX2).minus (x2.times (fX1)).over (fX2.minus (fX1));
		}
	}


	/**
	 * find function root using False Positive Method
	 * @param f the function being evaluated seeking root
	 * @param from low end of range on x axis to look for root
	 * @param to high end of range on x axis to look for root
	 * @return the value of the root found
	 */
	public T secantMethod
		(
			Function<T> f, T from, T to
		)
	{
		Iterator<T> iterator =
			new SecantMethodIterator<T>(f);
		return iterate (f, iterator, from, to);
	}
	public static class SecantMethodIterator<T>
		extends LinearInterpolationMethodIterator<T>
	{
		public SecantMethodIterator (Function<T> f) { super (f); }

		/* (non-Javadoc)
		 * @see net.myorb.math.computational.FunctionRoots.LinearInterpolationMethodIterator#next(net.myorb.math.Arithmetic.Value, net.myorb.math.Arithmetic.Value)
		 */
		public Value<T> next (Value<T> x1, Value<T> x2)
		{
			Value<T> x3 = midpoint (x1, x2);
			return midpoint (x2, x3);
		}
	}


	/*
	 * pseudo code for newton's method
	 * 
		%These choices depend on the problem being solved
		x0 = 1                      %The initial value
		f = @(x) x^2 - 2            %The function whose root we are trying to find
		fprime = @(x) 2*x           %The derivative of f(x)
		tolerance = 10^(-7)         %7 digit accuracy is desired
		epsilon = 10^(-14)          %Don't want to divide by a number smaller than this
		maxIterations = 20          %Don't allow the iterations to continue indefinitely
		haveWeFoundSolution = false %Have not converged to a solution yet
		
		for i = 1 : maxIterations
		
		    y = f(x0)
		    yprime = fprime(x0)
		
		    if(abs(yprime) < epsilon)                         %Don't want to divide by too small of a number
		        % denominator is too small
		        break;                                        %Leave the loop
		    end
		
		    x1 = x0 - y/yprime                                %Do Newton's computation
		
		    if(abs(x1 - x0)/abs(x1) < tolerance)              %If the result is within the desired tolerance
		        haveWeFoundSolution = true
		        break;                                        %Done, so leave the loop
		    end
		
		    x0 = x1                                           %Update x0 to start the process again
		
		end
		
		if (haveWeFoundSolution)
		   ... % x1 is a solution within tolerance and maximum number of iterations
		else
		   ... % did not converge
		end
	 * 
	 */


	/**
	 * use Newton's method to determine function root
	 * @param f the function being evaluated seeking for roots
	 * @param fPrime the slope calculator for the function being evaluated
	 * @param x the value of X to use for the evaluation, best approximation of root
	 * @return the value of the root found upon convergence indication
	 * @throws RuntimeException Failure to converge
	 */
	protected T newtonRaphsonMethodImplementation
		(
			Function<T> f, Function<T> fPrime, T x
		)
	throws RuntimeException
	{
		Value<T> xn, xnp1,
			y, slope, iterationRatio;
		for (int i = maxIterations; i > 0; i--)
		{
			y = forValue (f.eval (x));					// value of the function at the approximated root
			slope = forValue (fPrime.eval (x));			// value of the derivative at the approximated root
	
			xn = forValue (x);							// convert to arithmetic value
			xnp1 = xn.minus (y.over (slope));			// compute x(n+1), next iteration of approximation
			x = xnp1.getUnderlying ();					// convert back to underlying type

			postToMonitor (xn, xn, xnp1, y);

			iterationRatio =
				abs (xnp1.minus (xn)).over (abs (xnp1));
			if (withinTolerance (iterationRatio))
			{ return x; }
		}
		return raiseException ("Failure to converge");
	}


	/**
	 * use Newton's method to determine function root
	 * @param f the function being evaluated seeking roots
	 * @param derivative the first derivative of the function to be evaluated
	 * @param x the value of X to use for the evaluation, best approximation of root
	 * @return the value of the root found upon convergence indication
	 * @throws RuntimeException Failure to converge
	 */
	public T newtonRaphsonMethod
		(
			Function<T> f, Function<T> derivative, T x
		)
	throws RuntimeException
	{
		SlopeCalculator<T> slopeCalculator =
				new SlopeCalculator<T> (derivative);
		return newtonRaphsonMethodImplementation (f, slopeCalculator, x);
	}


	/**
	 * use second order of Newton's method
	 * @param f the function being evaluated for roots
	 * @param derivative the first derivative of the function to be evaluated
	 * @param secondDerivative the second derivative of the function to be evaluated
	 * @param x the value of X to use for the evaluation, best approximation of root
	 * @return the value of the root found upon convergence indication
	 * @throws RuntimeException Failure to converge
	 */
	public T newtonSecondOrderMethod 
		(
			Function<T> f, Function<T> derivative, Function<T> secondDerivative, T x
		)
	throws RuntimeException
	{
		SecondOrderAdjustment<T> slopeCalculator =
			new SecondOrderAdjustment<T> (f, derivative, secondDerivative);
		return newtonRaphsonMethodImplementation (f, slopeCalculator, x);
	}


	/**
	 * apply adjustment to slope calculation
	 *   given second derivative function
	 * @param <T> data type
	 */
	public static class SecondOrderAdjustment<T>
		extends Tolerances<T> implements Function<T>
	{
		/* (non-Javadoc)
		 * @see net.myorb.math.Function#eval(java.lang.Object)
		 */
		public T eval (T x)
		{
			Value<T>
				fx = forValue (f.eval (x)),
				d1 = forValue (fPrime.eval (x)),
				d2 = forValue (fPrime2.eval (x)),
			adjust = d2.times (fx).over (TWO.times (d1));
			return d1.minus (adjust).getUnderlying ();
		}
//		public SpaceManager<T> getSpaceManager() { return f.getSpaceManager (); }
//		public SpaceManager<T> getSpaceDescription() { return f.getSpaceManager (); }
		public SecondOrderAdjustment (Function<T> f, Function<T> fPrime, Function<T> fPrime2)
		{
			super ((SpaceManager<T>)f.getSpaceDescription ());
			this.fPrime = new SlopeCalculator<T>(fPrime); 
			this.f = f; this.fPrime2 = fPrime2;
		}
		protected Function<T> f, fPrime, fPrime2;
		final Value<T> TWO = forValue (2);
	}


	/**
	 * use approximations of derivatives
	 * @param f the function seeking root
	 * @param x the approximation of the root
	 * @param delta the delta value to use for derivative approximations
	 * @return an approximation of the root from iterations applied
	 * @throws RuntimeException for too-small a derivative value
	 */
	public T newtonMethodApproximated
		(
			Function<T> f, T x, T delta
		)
	throws RuntimeException
	{
		DerivativeApproximation.Functions<T> functions =
				DerivativeApproximation.getDerivativesFor (f, delta);
		return newtonSecondOrderMethod (f, functions.first (), functions.second (), x);
	}


	/**
	 * @return a useful delta for appoximations
	 */
	public T getDefaultDelta ()
	{
		return manager.invert (manager.newScalar (DEFAULT_DELTA_SCALE));
	}
	public static final int DEFAULT_DELTA_SCALE = 1000;


	/**
	 * protection for zero division.
	 *  slope of zero occurs at max / min / inflection
	 * @param <T> data type
	 */
	public static class SlopeCalculator<T>
		extends Tolerances<T> implements Function<T>
	{

		/**
		 * prevent zero divide.
		 *  derivative may come to max / min.
		 *  translate to meaningful error message.
		 * @param value the value to check for zero approach
		 * @return value same value returned for further calculation
		 * @throws RuntimeException too-small derivative
		 */
		public T zeroCheck (T value) throws RuntimeException
		{
			if (epsilon == null) return value;
			if (withinRange (forValue (value), epsilon))
			{ raiseException ("Derivative too small, local max/min/inflection found"); }
			return value;
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.Function#eval(java.lang.Object)
		 */
		public T eval (T x) { return zeroCheck (f.eval (x)); }
		//public SpaceDescription<T> getSpaceDescription() { return f.getSpaceDescription (); }
		public SlopeCalculator (Function<T> f) { super ((SpaceManager<T>)f.getSpaceDescription ()); this.f = f; }
		protected Function<T> f;

	}


}

