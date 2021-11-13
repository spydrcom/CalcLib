
package net.myorb.math;

/**
 * library of trigonometry algorithms
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class TrigLib<T> extends ExponentiationLib<T>
{


	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 */
	public TrigLib
	(SpaceManager<T> manager)
	{ super (manager); }


	/**
	 * compute ARC TAN of y/x using tangent half angle
	 * @param y numerator of the computed parameter ratio
	 * @param x denominator of the computed parameter ratio
	 * @return ARC TAN value for result
	 */
	public Value<T> atan2 (Value<T> y, Value<T> x)
	{
		if (y.isZero ())
		{
			if (x.isNegative ())
			{ return getPiValue (); }
			else return forValue (0);
		}
		Value<T> root = sqrt (x.squared ().plus (y.squared ()));
		Value<T> atan = arctan (root.minus (x).over (y));
		return forValue (2).times (atan);
	}
	public T atan2 (T y, T x) { return atan2 (forValue (x), forValue (y)).getUnderlying (); }


	/**
	 * compute ARC TAN using ASIN relationship
	 * @param x value to use in computation of ARC TAN
	 * @return arctan (x) computed result
	 */
	public Value<T> arctan (Value<T> x)
	{
		return asin (x.over (sqrt (x.squared ().plus (forValue (1)))));
	}
	public T atan (T x) { return arctan (forValue (x)).getUnderlying (); }


	/**
	 * compute ARC SIN of parameter
	 * @param x the value used to compute result
	 * @return result of computation
	 */
	public T asin (T x)
	{
		Value<T>
		v = forValue (x), xPower = v;
		boolean r = reductionMechanism != null;
		boolean d = isSelected (DUMP_ITERATIVE_TERM_VALUES);
		Value<T> coef = oneValue (), xSquared = v.times (v);
		Value<T> sum = xPower, termValue;
		int termNumber = 1;

		for (int i=getTermCount(ASIN); i>0; i--)
		{
			coef = coef.times (forValue (termNumber)).over (forValue (termNumber+1));
			// asin(x) = [(2n)! * X^(2n+1)] / [2^(2n) * (n!)^2 * (2n+1)]  n >= 0 && abs(x) < 1
			termValue = coef.times (xPower = xPower.times (xSquared)).over (forValue (termNumber += 2));
			reduceAndDump (termValue, sum = sum.plus (termValue), r, d);
		}

		return sum.getUnderlying();
	}
	public Value<T> asin (Value<T> x) { return forValue (asin (x.getUnderlying ())); }


	/**
	 * compute COS of parameter angle
	 * @param x the angle used to compute result (radians)
	 * @return result of computation
	 */
	public T cos (T x)
	{
		int termNumber = 0;
		SignManager manager = new SignManager (true);
		Value<T> v = forValue (x), ONE = oneValue (), termValue;
		Value<T> sum = ONE, factorial = oneValue (), xPower = ONE;
		boolean d = isSelected (DUMP_ITERATIVE_TERM_VALUES);
		boolean r = reductionMechanism != null;
		Value<T> xSquared = v.times (v);
		
		for (int i=getTermCount(COS); i>0; i--)
		{
			termNumber = factorialExtended (termNumber, 2, factorial);
			// cos(x) = 1 - x^2/2! + x^4/4! - x^6/6! + x^8/8! - x^10/10! + ...    all real x
			manager.toggleSign (termValue = (xPower = xPower.times (xSquared)).over (factorial));
			reduceAndDump (termValue, sum = sum.plus (termValue), r, d);
		}

		return sum.getUnderlying();
	}


	/**
	 * compute SIN of parameter angle
	 * @param x the angle used to compute result (radians)
	 * @return result of computation
	 */
	public T sin (T x)
	{
		int termNumber = 1;
		Value<T> v = forValue (x), termValue;
		SignManager manager = new SignManager (true);
		Value<T> sum = v, factorial = oneValue (), xPower = v;
		boolean d = isSelected (DUMP_ITERATIVE_TERM_VALUES);
		boolean r = reductionMechanism != null;
		Value<T> xSquared = v.times (v);

		for (int i=getTermCount(SIN); i>0; i--)
		{
			termNumber = factorialExtended (termNumber, 2, factorial);
			// sin(x) = x - x^3/3! + x^5/5! - x^7/7! + x^9/9! - x^11/11! + ...  all real x
			manager.toggleSign (termValue = (xPower = xPower.times (xSquared)).over (factorial));
			reduceAndDump (termValue, sum = sum.plus (termValue), r, d);
		}

		return sum.getUnderlying ();
	}


	/**
	 * compute ARC TAN of parameter
	 * @param x the value used to compute result -1 LE x LE 1
	 * @return result of computation
	 */
	public Value<T> atanInRange (Value<T> x)
	{
		int termNumber = 1;
		Value<T> xPower = x, termValue;
		SignManager manager = new SignManager (true);
		Value<T> xSquared = x.times (x), sum = xPower;
		boolean d = isSelected (DUMP_ITERATIVE_TERM_VALUES);
		boolean r = reductionMechanism != null;

		for (int i=getTermCount(ATAN); i>0; i--)
		{
			xPower = xPower.times (xSquared);
			// atan(x) = x - x^3/3 + x^5/5 - x^7/7 + ...     -1 <= x <= 1
			manager.toggleSign (termValue = xPower.over (forValue (termNumber += 2)));
			reduceAndDump (termValue, sum = sum.plus (termValue), r, d);
		}

		return sum;
	}
	public T atanHook (T x) { return atanInRange (forValue (x)).getUnderlying (); }

	/**
	 * force atan domain to 0 LT x LT 1
	 * @param x angle reduced in domain to 0 LT x LT 1
	 * @return atan computed result
	 */
	public Value<T> atanHalfQuad (Value<T> x)
	{
		if (forValue (1).isLessThan (x))
		{
			return forValue (piTimes (1, 2))
			.minus (forValue (atanHook (x.inverted ().getUnderlying ())));
		}
		return forValue (atanHook (x.getUnderlying ()));
	}

	/**
	 * compute ARC TAN of parameter using Taylor series
	 * @param x the value used to compute result, no domain restriction
	 * @return result of computation
	 */
	public Value<T> arctangent (Value<T> x)
	{
		if (x.isZero ()) return zeroValue ();
		if (x.isNegative ()) return atanHalfQuad (x.negate ()).negate ();
		return atanHalfQuad (x);
	}

	/**
	 * compute ARC TAN of parameter using Taylor series
	 * @param x the value used to compute result
	 * @return result of computation
	 */
	public T arctangent (T x)
	{
		Value<T> result = arctangent (forValue (x));
		return result.getUnderlying ();
	}

	/**
	 * ARC TAN of ratio of non-zero parameters
	 * @param y numerator (sin) of ratio x != 0
	 * @param x denominator (cos) of ratio
	 * @return computed ATAN result
	 */
	public T atanNonZero (Value<T> y, Value<T> x)
	{
		Value<T> result = null, ratio = y.over (x);
		switch (signHash (x, y))
		{
			case NEITHER_BIT:
				result = atanHalfQuad (ratio);							// +sin / +cos => Q1
				break;
			case LEFT_BIT_ONLY:
				result = arctangent (ratio).plus (getPiValue ());		// +sin / -cos => Q2
				break;
			case BOTH_BITS:
				result = arctangent (ratio).minus (getPiValue ());		// -sin / -cos => Q3
				break;
			case RIGHT_BIT_ONLY:
				result = arctangent (ratio);							// -sin / +cos => Q4
				break;
			default:
				internalError ();										// bit hash has 0-3 range
		}
		return result.getUnderlying ();
	}

	/**
	 * compute ARC TAN of y/x
	 * @param y numerator of the computed parameter ratio
	 * @param x denominator of the computed parameter ratio
	 * @return ARC TAN value for result
	 */
	public T atan (T y, T x)
	{
		Value<T>
			yval = forValue (y),
			xval = forValue (x);
		T result = null;
		switch (zeroHash (xval, yval))
		{
			case LEFT_BIT_ONLY:											// horizontal axis
				if (yval.isNegative ()) result = piTimes (-1, 2);
				else result = piTimes (1, 2);				
				break;
			case RIGHT_BIT_ONLY:										// vertical axis
				if (xval.isNegative ()) result = PI ();
				else result = discrete (0);
				break;
			case NEITHER_BIT:											// determine quadrant
				result = atanNonZero (yval, xval);
				break;
			case BOTH_BITS: default:									// implied division by zero
				internalError ();
		}
		return result;
	}
	public Value<T> getPiValue () { return forValue (PI ()); }

}
