
package net.myorb.math.computational.integration.polylog;

import net.myorb.math.computational.TanhSinhQuadratureAlgorithms;
import net.myorb.math.computational.TanhSinhQuadratureTables;

import net.myorb.math.realnumbers.RealFunctionWrapper;
import net.myorb.math.Function;

import java.util.ArrayList;
import java.util.List;

/**
 * identify maximum and minimum points for a function over an interval
 * @author Michael Druckman
 */
public class CyclicAspects
{


	/**
	 * enable trace output
	 */
	public static boolean TRC = false;


	/**
	 * simple description of a function
	 */
	public interface FunctionBody extends RealFunctionWrapper.RealFunctionBodyWrapper {}


	/*
	 * step thru function values seeking changes in sign of derivative
	 */


	/**
	 * determine derivative zero being crossed
	 * @param positiveDerivative TRUE for positive derivative when prev computed
	 * @param prev the last of the function evaluation
	 * @param next the next function evaluation
	 * @return TRUE for change detected
	 */
	public static boolean detectChange
		(
			boolean positiveDerivative,
			double prev, double next
		)
	{
		if (positiveDerivative)
		{
			if (next < prev)
			{
				return true;
			}
		}
		else
		{
			if (next > prev)
			{
				return true;
			}
		}
		return false;
	}


	/**
	 * find a non-trivial function section
	 * @param seqSized required length of segment
	 * @param lo the low end of the domain of interest
	 * @param hi the high end of the domain of interest
	 * @param delta the tick size between tests
	 * @return the start of the sequence found
	 */
	public double findFirst
	(int seqSized, double lo, double hi, double delta)
	{
		double cur = lo,
			prev = f.eval (cur), next = f.eval (cur+=delta);
		boolean upSlope = next > prev, changeSeen = false;
		double changeFoundAt = 0;
		int seqLen = 0;

		while (cur < hi)
		{
			prev = next;
			next = f.eval (cur+=delta);
			if (detectChange (upSlope, prev, next))
			{ upSlope = ! upSlope; changeFoundAt = cur; changeSeen = true; seqLen = 0; }
			else if (changeSeen && ++seqLen == seqSized) return changeFoundAt;
		}

		throw new RuntimeException ("No sequences found");
	}


	/**
	 * enumerate derivative shifts over domain
	 * @param found a list to compile the results
	 * @param x the low end value to start the scan at
	 * @param hi the high value of the interval to test
	 * @param delta the difference between test points
	 */
	public void scan (List <Double> found, double x, double hi, double delta)
	{
		double prev = f.eval (x), next = f.eval (x+=delta);
		boolean positiveDerivative = next > prev;
		double lastFound = Double.NaN;

		while (x < hi)
		{
			prev = next; next = f.eval (x+=delta);

			if (detectChange (positiveDerivative, prev, next))
			{
				if (positiveDerivative)
				{
					positiveDerivative = false;
					if (TRC) System.out.println ("x="+x+" \t f(x)="+next);
					found.add (lastFound = x);
				}
				else
				{
					positiveDerivative = true;
					if (TRC) System.out.println ("x="+x+" \t f(x)="+next);
					found.add (lastFound = x);
				}
			}
		}

		if (lastFound < hi) found.add (hi);
	}


	/**
	 * scan starts at the identified low
	 * @param lo the low value of the interval
	 * @param hi the high value of the interval to test
	 * @param delta the difference between test points
	 * @return the list of max and min points found
	 */
	public List <Double> find (double lo, double hi, double delta)
	{
		List <Double> found = new ArrayList <Double> ();
		scan (found, lo, hi, delta);
		return found;
	}


	/**
	 * scan starts at the first found sequence
	 * @param sequenceLength the number of points identifying a sequence
	 * @param lo the starting point to look for first change
	 * @param hi the high value of the interval to test
	 * @param delta the difference between test points
	 * @return the list of max and min points found
	 */
	public List <Double> find (int sequenceLength, double lo, double hi, double delta)
	{
		double start =
			findFirst (sequenceLength, lo, hi, delta);
		List <Double> found = new ArrayList <Double> ();
		found.add (start); scan (found, start, hi, delta);
		return found;
	}


	/*
	 * [ sigma log t ] the common cycle parameter in polylog integrals
	 */

	
	/**
	 * compute k * ln(t)
	 * @param k multiple of ln t
	 * @param t distance on the real axis
	 * @return computed product
	 */
	public static double kLnT (double k, double t)
	{
		return CauchySchlomilch.kLnT (k, t);
	}


	/*
	 * cycle steps based on works by Kaz Watten
	 */


	/**
	 * for forms k * ln t in a cyclic function.
	 * - this shows up in complex polylog integrals.
	 * - t^s where s has imaginary part presents this form.
	 * @param t distance on the real axis
	 * @param k multiple of ln t
	 * @return real axis step
	 */
	public static double cycleStep (double t, double k)
	{
		return t * ( Math.exp (Math.PI / k) - 1 );
	}


	/**
	 * generate step-cycle sync points
	 * @param f function being evaluated
	 * @param startingAt the starting low value of the range
	 * @param multiple number of steps to use per increment
	 * @param upTo the high value of the range
	 * @param k the multiple of ln t
	 * @return the list of points
	 */
	public List <Double> computeCycleSyncPoints
		(
			FunctionBody f,
			double startingAt,
			double multiple,
			double upTo,
			double k
		)
	{
		List <Double> domain =
			new ArrayList <Double> ();
		setFunction (f);

		for
			(
				double x = startingAt; x <= upTo;
				x += multiple * cycleStep (x, k)
			)
		{
			domain.add (x);
		}

		return domain;
	}


	/*
	 * half-cycles with sub-segments of PI radians
	 */


	/**
	 * alternate form for cycles:
	 * - multiples of PI / k as exponent
	 * @param n multiple of PI as domain point
	 * @param segments segments of the half cycle
	 * @param k multiple of ln t
	 * @return real axis point
	 */
	public static double halfCycle (int n, int segments, double k)
	{
		return Math.exp (n * Math.PI / (k * segments));
	}


	/**
	 * generate half-cycle sync points
	 * @param f function being evaluated
	 * @param k the imag part multiple of ln t
	 * @param s the count of half cycle segment divisions
	 * @param N max multiple of PI as domain point
	 * @return the list of points
	 */
	public List <Double> computeCycleSyncPoints
		(
			FunctionBody f, double k,
			int s, int N
		)
	{
		setFunction (f);
		return computeCycleSyncPoints (k, s, N);
	}

	/**
	 * get list of points for a sigma value
	 * @param k the imag part multiple of log t called sigma
	 * @param s the count of half cycle segment divisions
	 * @param N max multiple of PI as domain point
	 * @return the list of points
	 */
	public List <Double> computeCycleSyncPoints
		(
			double k, int s, int N
		)
	{
		List <Double> domain =
			new ArrayList <Double> ();
		computeCycleSyncPoints (k, s, N, domain);
		return domain;
	}


	/**
	 * sync points compiled into list
	 * @param k the imag part multiple of log t
	 * @param s the count of half cycle segments
	 * @param N max multiple of PI as domain point
	 * @param points the list of points being built
	 */
	public void computeCycleSyncPoints
		(
			double k, int s, int N, List <Double> points
		)
	{
		for (int n = N; n >= 0; n--)
		{ points.add ( halfCycle (-n, s, k) ); }
	}


	/**
	 * get list of sync points
	 * - the number of points less than 1 is N
	 * - the upTo limit is the upper extension high end
	 * @param upTo the upper limit of the domain to evaluate
	 * @param k the imaginary sigma part multiple of log t
	 * @param s the section count of half cycle breaks
	 * @param N max multiple of PI as domain point
	 * @return the list of points
	 */
	public List <Double> computeCycleSyncPoints
		(
			double upTo, double k,
			int s, int N
		)
	{
		double last = 1.0; int n = 1;
		List <Double> points = computeCycleSyncPoints (k, s, N);
		while (last < upTo) points.add ( last = halfCycle (n++, s, k) );
		return points;
	}


	/**
	 * compute function integral
	 * @param lo the low end of an integration range
	 * @param hi the high end of an integration range
	 * @return the calculated integral
	 */
	public double eval (double lo, double hi)
	{
		double result =
			TanhSinhQuadratureAlgorithms.Integrate
				(f, lo, hi, targetAbsoluteError, stats);
		if (TRC)
		{
			System.out.println (lo + ".." + hi);
			System.out.println ("\t : " + stats);
			System.out.println ("\t I=" + result);
		}
		return result;
	}


	/*
	 * polynomial calculus
	 */


	/**
	 * compute series of integrals over domain points
	 * @param points the list of domain points to establish the series
	 * @return the calculated integral
	 */
	public double integralOver (List <Double> points)
	{
		aggregateError = 0; evaluations = 0;
		double h, result = 0, l = points.get (0);
		for (int i = 1; i < points.size (); i++)
		{
			result += eval (l, h = points.get (i));
			evaluations += stats.numFunctionEvaluations;
			aggregateError += stats.errorEstimate;

			if (TRC)
			{
				System.out.println ("\t AGG=" + result);
				System.out.println ();
			}

			l = h;
		}
		return result;
	}


	public double getAggregateError () { return aggregateError; }
	public int getEvaluationCount () { return evaluations; }
	// the aggregate error and count of evaluations
	protected double aggregateError;
	protected int evaluations;


	/*
	 * function representing the integrand
	 */

	public void setFunction (FunctionBody f)
	{ setFunction (new RealFunctionWrapper (f).toCommonFunction ()); }
	public void setFunction (Function <Double> f) { this.f = f; }
	protected Function <Double> f;


	/**
	 * initialize quadrature structures
	 */
	public CyclicAspects ()
	{
		this.stats = new TanhSinhQuadratureTables.ErrorEvaluation ();
		this.setTargetError (1E-10); // setting a default
	}
	protected TanhSinhQuadratureTables.ErrorEvaluation stats;


	/**
	 * identify a target error value.
	 * - the smaller the target the more work done
	 * - too small and the algorithm falls apart and results go crazy
	 * @param to use this value for target error
	 */
	public void setTargetError (double to)
	{
		this.targetAbsoluteError = to;
	}
	protected double targetAbsoluteError;


}

