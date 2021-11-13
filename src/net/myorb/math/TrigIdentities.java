
package net.myorb.math;

import java.util.List;

/**
 * provide full complement of trigonometric functions using identity equations
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class TrigIdentities<T> extends TrigLib<T>
{


	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 */
	public TrigIdentities
	(SpaceManager<T> manager)
	{ super (manager); setConstants (); }


	void setConstants ()
	{
		TWO = discrete (2);
		HALF = inverted (TWO);
		PI_OVER_2 = piOver (2);
		ONE = oneValue ();
	}
	protected T TWO, HALF, PI_OVER_2;
	protected Value<T> ONE;


	/**
	 * compute TANGENT of parameter angle
	 * @param x the angle used to compute result (radians)
	 * @return result of computation
	 */
	public T tan (T x) { return X (sin(x), inverted (cos (x))); }


	/**
	 * compute COTANGENT of parameter angle
	 * @param x the angle used to compute result (radians)
	 * @return result of computation
	 */
	public T cot (T x) { return X (cos (x), inverted (sin (x))); }


	/**
	 * compute SECANT of parameter angle
	 * @param x the angle used to compute result (radians)
	 * @return result of computation
	 */
	public T sec (T x) { return inverted (cos (x)); }


	/**
	 * compute COSECANT of parameter value
	 * @param x the angle used to compute result (radians)
	 * @return result of computation
	 */
	public T csc (T x) { return inverted (sin (x)); }


	/**
	 * compute ARC COS of parameter angle
	 * @param x the value to use in computation
	 * @return result of computation
	 */
	public T acos (T x) { return subtract (PI_OVER_2, asin (x)); }


	/**
	 * compute ARC COT of parameter value
	 * @param x the value to use in computation
	 * @return result of computation
	 */
	public T acot (T x) { return subtract (PI_OVER_2, atan (x)); }


	/**
	 * compute ARC CSC of parameter value
	 * @param x the value to use in computation
	 * @return result of computation
	 */
	public T acsc (T x) { return subtract (PI_OVER_2, asec (x)); }


	/**
	 * compute ARC SEC of parameter value
	 * @param x the value to use in computation
	 * @return result of computation
	 */
	public T asec (T x) { return acos (inverted (x)); }


	/**
	 * compute SINH of parameter angle
	 * @param x the angle used to compute result
	 * @return result of computation
	 */
	public T sinh (T x) { return X (subtract (exp (x), exp (neg (x))), HALF); }


	/**
	 * compute COSH of parameter angle
	 * @param x the angle used to compute result
	 * @return result of computation
	 */
	public T cosh (T x) { return subtract (exp (x), sinh (x)); }
//	{ return X (sumOf (exp (x), exp (neg (x))), HALF) }



	/**
	 * compute TANH of parameter angle
	 * @param x the angle used to compute result
	 * @return result of computation
	 */
	public T tanh (T x) { return X (sinh (x), inverted (cosh (x))); }


	/**
	 * compute COTH of parameter angle
	 * @param x the angle used to compute result
	 * @return result of computation
	 */
	public T coth (T x) { return X (cosh (x), inverted (sinh (x))); }


	/**
	 * compute SECH of parameter angle
	 * @param x the angle used to compute result
	 * @return result of computation
	 */
	public T sech (T x) { return inverted (cosh (x)); }


	/**
	 * compute CSCH of parameter angle
	 * @param x the angle used to compute result
	 * @return result of computation
	 */
	public T csch (T x) { return inverted (sinh (x)); }


	/**
	 * compute ARSINH of parameter value
	 * @param x the value to use in computation
	 * @return result of computation
	 */
	public T arsinh (T x) { return arh (x, ONE); }
	// ln (sumOf (x, sqrt (sumOf (squared (x), ONE))))


	T sqPlus (Value<T> v, Value<T> offset)
	{ return v.plus (sqrt (v.squared ().plus (offset))).getUnderlying (); }
	T arh (T t, Value<T> offset) { return ln (sqPlus (forValue (t), offset)); }


	/**
	 * compute ARCOSH of parameter value
	 * @param x the value to use in computation
	 * @return result of computation
	 */
	public T arcosh (T x) { return arh (x, ONE.negate ()); }
	// ln (sumOf (x, sqrt (subtract (squared (x), ONE))))


	/**
	 * compute ARTANH of parameter value
	 * @param x the value to use in computation (abs(x) LT 1)
	 * @return result of computation
	 */
	public T artanh (T x) { return X (ln (getRatio (forValue (x))), HALF); }
	T getRatio (Value<T> v) { return v.plus (ONE).over (v.minus (ONE)).getUnderlying (); }
	// X (subtract (ln (sumOf (x, ONE)), ln (subtract (x, ONE))), HALF)


	/**
	 * compute ARCOTH of parameter value
	 * @param x the value to use in computation (abs(x) GT 1)
	 * @return result of computation
	 */
	public T arcoth (T x) { return artanh (inverted (x)); }


	/**
	 * compute ARSECH of parameter value
	 * @param x the value to use in computation
	 * @return result of computation
	 */
	public T arsech (T x) { return arcosh (inverted (x)); }


	/**
	 * compute ARCSCH of parameter value
	 * @param x the value to use in computation
	 * @return result of computation
	 */
	public T arcsch (T x) { return arsinh (inverted (x)); }


	/**
	 * hook for use by expression evaluator
	 * @param parameters the list of parameters
	 * @return the computed result
	 */
	public T atan (List<T> parameters)
	{
		switch (parameters.size ())
		{
			case 1:  return atan (parameters.get (0));
			case 2:  return atan (parameters.get (0), parameters.get (1));
			default: throw new RuntimeException ("ATAN function requires 1 or 2 parameters");
		}
	}


}

