
package net.myorb.math.computational;

import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.math.MultiDimensional;

import java.util.ArrayList;
import java.util.List;

/**
 * Runge-Kutta ODE solution methods
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class RungeKutta <T>
{


	/**
	 * variables used in RK4 definition
	 * @param <T> data type
	 */
	public static class FormulaParameters <T>
	{
		protected T
			T0,			// initial domain value
			Y0,			// value of solution Y at T0
			h;			// delta t between approximations 
		protected int
			N;			// number of approximations
	}


	/*
	 * constructors using common RK4 formula parameters
	 */

	public RungeKutta
		(
			MultiDimensional.Function <T> subject,
			FormulaParameters <T> parameters
		)
	{
		this (subject, parameters.Y0, parameters.T0, parameters.h, parameters.N);
	}

	public RungeKutta
		(
			MultiDimensional.Function <T> subject,
			T y0, T t0, T h, int N
		)
	{
		this.mgr = subject.getSpaceDescription ();
		this.TWO = mgr.newScalar (2); this.HALF = mgr.invert (TWO);
		this.subject = subject; this.h = h; this.N = N;
		this.addIteration (y0, t0);
	}
	protected MultiDimensional.Function<T> subject;
	protected SpaceDescription<T> mgr;
	protected T h;


	/*
	 * arithmetic for half values
	 */

	public T half (T x) { return mgr.multiply (x, HALF); }
	public T halfPlus (T x, T y) { return mgr.add (half (x), y); }
	protected T TWO, HALF;


	/*
	 * the solution
	 */

	protected List <T>
	Y = new ArrayList <> (),
	T = new ArrayList <> ();


	/**
	 * call derivative function
	 * @param t Tn value for function parameter
	 * @param y Yn value for function parameter
	 * @return computed value
	 */
	public T f (T t, T y)
	{
		List<T> parms = new ArrayList<T>();
		parms.add (t); parms.add (y);
		return subject.f (parms);
	}


	/**
	 * @param y Yn value
	 * @param t Tn value
	 */
	public void doIteration (T y, T t)
	{
		T halfHPlusT = halfPlus (h, t);

		T k1 = mgr.multiply (h, f (t, y));
		T k2 = mgr.multiply (h, f (halfHPlusT, halfPlus (k1, y)));
		T k3 = mgr.multiply (h, f (halfHPlusT, halfPlus (k2, y)));
		T k4 = mgr.multiply (h, f (mgr.add (t, h), mgr.add (y, k3)));

		addIteration (k1, mgr.multiply (k2, TWO), mgr.multiply (k3, TWO), k4, y, t);
	}
	public void addIteration (T k1, T twoK2, T twoK3, T k4, T y, T t)
	{
		T SIXTH = mgr.invert (mgr.newScalar (6));
		T sum = mgr.add (k1, k4); sum = mgr.add (sum, twoK2); sum = mgr.add (sum, twoK3);
		addIteration (mgr.add (mgr.multiply (sum, SIXTH), y), mgr.add (t, h));
	}
	public void doIteration (int n) { doIteration (Y.get (n), T.get (n)); }
	public void addIteration (T y, T t)
	{ Y.add (y); T.add (t); }


	/**
	 * @return Y vector of N iterations
	 */
	public List<T> doIterations ()
	{
		for (int n = 0; n < N; n++)
		{ doIteration (n); }
		return Y;
	}
	protected int N;


}

