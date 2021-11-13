
package net.myorb.math;

/**
 * provide full complement of trigonometric function using identity equations.
 *  alternative to TrigIdentities using library conforming to interface
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class TrigAtomic<T> extends TrigIdentities<T>
{


	/**
	 * operations required to build atoms of trig complement
	 * @param <T> type on which operations are to be executed
	 */
	public interface Quarks<T>
	{
		
		public T pow (T x, int n);
		public T asin (T x);
		public T atan (T x);
		public T atan (T x, T y);
		public T sqrt (T x);
		public T exp (T x);
		public T sin (T x);
		public T cos (T x);
		public T ln (T x);

	}


	public TrigAtomic
	(Quarks<T> quarks, SpaceManager<T> manager)
	{ super (manager); this.quarks = quarks; }
	protected Quarks<T> quarks;


	/**
	 * compute logarithm of parameter 
	 * @param x the value for the logarithm calculation
	 * @return result of computation
	 */
	public T ln (T x) { return quarks.ln (x); }


	/**
	 * compute square root of parameter 
	 * @param x the value for the SQRT calculation
	 * @return result of computation
	 */
	public T sqrt (T x) { return quarks.sqrt (x); }


	/**
	 * compute exponential of parameter 
	 * @param x the value for the exponential calculation
	 * @return result of computation
	 */
	public T exp (T x) { return quarks.exp (x); }


	/**
	 * compute SINE of parameter angle
	 * @param x the angle used to compute result (radians)
	 * @return result of computation
	 */
	public T sin (T x) { return quarks.sin (x); }


	/**
	 * compute COSINE of parameter angle
	 * @param x the angle used to compute result (radians)
	 * @return result of computation
	 */
	public T cos (T x) { return quarks.cos (x); }


	/**
	 * compute ARC SINE of parameter angle
	 * @param x the SINE used to compute result
	 * @return result of computation (radians)
	 */
	public T asin (T x) { return quarks.asin (x); }


	/**
	 * compute ARC TAN of parameter angle
	 * @param x the tangent used to compute result
	 * @return result of computation (radians)
	 */
	public T atan (T x) { return quarks.atan (x); }


	/**
	 * compute ARC TAN of parameter angle
	 * @param x the x-axis used to compute result
	 * @param y the y-axis used to compute result
	 * @return result of computation (radians)
	 */
	public T atan (T x, T y) { return quarks.atan (x, y); }


}

