
package net.myorb.math;

/**
 * use most efficient methods available to provide 
 *  highest possible precision computational approximations for irrational function
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class OptimizedMathLibrary<T> extends TrigIdentities<T>
{


	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 */
	public OptimizedMathLibrary (SpaceManager<T> manager)
	{ super (manager); establishConstants (); }


	/**
	 * construct a Taylor series object and compute the value of 'e'
	 */
	public void establishConstants ()
	{
		taylor = new TaylorPolynomials<T> (manager);
	}
	protected TaylorPolynomials<T> taylor;


	/**
	 * get access to the control object for Taylor expansions
	 * @return the TaylorPolynomials implementation object
	 */
	public TaylorPolynomials<T> getTaylorSeriesControler () { return taylor; }


	/* (non-Javadoc)
	 * @see net.myorb.math.TrigLib#sin(java.lang.Object)
	 */
	public T sin (T x) { return taylor.sin (x); } 
	public Value<T> sin (Value<T> x) { return forValue (taylor.sin (x.getUnderlying ())); } 


	/* (non-Javadoc)
	 * @see net.myorb.math.TrigLib#asin(java.lang.Object)
	 */
	public T asin (T x) { return taylor.asin (x); } 
	public Value<T> asin (Value<T> x) { return forValue (taylor.asin (x.getUnderlying ())); } 


	/* (non-Javadoc)
	 * @see net.myorb.math.TrigLib#cos(java.lang.Object)
	 */
	public T cos (T x) { return taylor.cos (x); } 
	public Value<T> cos (Value<T> x) { return forValue (taylor.cos (x.getUnderlying ())); } 
	public Value<T> acos (Value<T> x) { return forValue (acos (x.getUnderlying ())); } 
	public Value<T> tan (Value<T> x) { return forValue (tan (x.getUnderlying ())); } 
	public Value<T> atan (Value<T> x) { return forValue (atan (x.getUnderlying ())); } 
	public Value<T> cot (Value<T> x) { return forValue (cot (x.getUnderlying ())); } 
	public Value<T> acot (Value<T> x) { return forValue (acot (x.getUnderlying ())); } 


	/* (non-Javadoc)
	 * @see net.myorb.math.TrigIdentities#sec(java.lang.Object)
	 */
	public T sec (T x) { return inverted (taylor.cos (x)); } 
	public Value<T> sec (Value<T> x) { return forValue (sec (x.getUnderlying ())); } 
	public Value<T> asec (Value<T> x) { return forValue (asec (x.getUnderlying ())); } 
	public Value<T> csc (Value<T> x) { return forValue (csc (x.getUnderlying ())); } 
	public Value<T> acsc (Value<T> x) { return forValue (acsc (x.getUnderlying ())); } 


	/* (non-Javadoc)
	 * @see net.myorb.math.TrigIdentities#sinh(java.lang.Object)
	 */
	public T sinh (T x) { return taylor.sinh (x); } 
	public T artanh (T x) { return taylor.artanh (x); } 
	public Value<T> sinh (Value<T> x) { return forValue (taylor.sinh (x.getUnderlying ())); } 
	public Value<T> arsinh (Value<T> x) { return forValue (arsinh (x.getUnderlying ())); } 
	public Value<T> tanhh (Value<T> x) { return forValue (taylor.sinh (x.getUnderlying ())); } 
	public Value<T> artanh (Value<T> x) { return forValue (artanh (x.getUnderlying ())); } 


	/* (non-Javadoc)
	 * @see net.myorb.math.TrigIdentities#cosh(java.lang.Object)
	 */
	public T cosh (T x) { return taylor.cosh (x); } 
	public Value<T> cosh (Value<T> x) { return forValue (taylor.cosh (x.getUnderlying ())); } 
	public Value<T> arcosh (Value<T> x) { return forValue (arcosh (x.getUnderlying ())); } 


	/* (non-Javadoc)
	 * @see net.myorb.math.ExponentiationLib#exp(java.lang.Object)
	 */
	public Value<T> exp (Value<T> x)
	{ return forValue (exp (x.getUnderlying ())); }
	public T exp (T x){ return taylor.exp (x); } 

	public T expSeries (T x) { return taylor.getExpSeries (50).eval (x); }


	/* (non-Javadoc)
	 * @see net.myorb.math.ExponentiationLib#ln(java.lang.Object)
	 */
	public Value<T> ln (Value<T> v)
	{ return forValue (ln (v.getUnderlying())); }
	public T ln (T x) { return taylor.ln (x); }


	/**
	 * use identity log[base b](x) = log[base k](x) / log[base k](b).
	 *  the Taylor series computes natural logarithm, so this implies k=e
	 * @param value the value for which to compute the logarithm
	 * @param base the base of the logarithm to be computed
	 * @return computed logarithm with specified base
	 */
	public T log (T value, T base) { return X (ln (value), inverted (ln (base))); }


	/* (non-Javadoc)
	 * @see net.myorb.math.Arithmetic#sqrt(net.myorb.math.Arithmetic.Value)
	 */
	public Value<T> sqrt (Value<T> x) { return newtonSqrt (x); }


}

