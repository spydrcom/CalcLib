
package net.myorb.math.computational.iterative;

import net.myorb.math.computational.Combinatorics;
import net.myorb.math.SpaceManager;

/**
 * approximate a function using series expansion methods of Brook Taylor
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class Taylor <T> extends IterationFoundations <T>
{


	//  f(x) = SIGMA [ 0 <= n <= INFINITY ] ( f'n(0)/n! * x^n )


	public Taylor
	(SpaceManager <T> manager) { this.manager = manager; }
	protected SpaceManager <T> manager;


	/**
	 * run the computation using computer and initial value
	 * @param iterations the number to be run in this evaluation
	 * @param computer a DerivativeComputer for the function
	 * @param parameter function parameter
	 * @return the computed result
	 */
	public T run
		(
			int iterations,
			IterationTools.DerivativeComputer <T> computer,
			T parameter
		)
	{
		initializeFunction (parameter);
		return run (iterations, computer);
	}


	/**
	 * process a full bulk run of term evaluations
	 * @param iterations number of iterations to run
	 * @param computer a DerivativeComputer for the function
	 * @return the computed sum after specified iterations
	 */
	public T run
		(
			int iterations,
			IterationTools.DerivativeComputer <T> computer
		)
	{
		try
		{
			initializeSummation
			(computer.nTHderivative (0));
			for (int n = 1; n <= iterations; n++)
			{ applyIteration (computer.nTHderivative (n)); }
		}
		catch (ShortCircuitTermination SC)
		{
			System.out.println (SC.getMessage ());
		}
		return summation;
	}


	/**
	 * compute the specified term and add into the summation
	 * @param n the value of the summation index for this iteration
	 * @param nTHderivative Nth evaluated derivative for the algorithm
	 * @throws ShortCircuitTermination for Short Circuit condition found
	 */
	public void applyIteration
			(int n, T nTHderivative)
	throws ShortCircuitTermination
	{
		this.setX (manager.pow
			(functionParameter, n));
		this.setDerivativeAtX (nTHderivative);
		T xNfP = manager.multiply (x, nTHderivative);
		T nF = combo.factorial (manager.newScalar (n));
		this.setDelta (manager.multiply (xNfP, manager.invert (nF)));
		this.summation = manager.add (this.summation, this.getDelta ());
		this.testForShortCircuit (this.getDelta (), n, manager);
	}
	public T summation;


	/**
	 * run the next iteration in the series
	 * @param nTHderivative the next derivative value
	 * @throws ShortCircuitTermination for Short Circuit condition found
	 */
	public void applyIteration
		(T nTHderivative)
	throws ShortCircuitTermination
	{
		applyIteration ( n += 1, nTHderivative );
		trace ();
	}
	protected int n;


	/**
	 * iteration index starts at zero
	 * - summation is also initialized to zero
	 * @param derivative0 the function derivative for term 0
	 * @throws ShortCircuitTermination for Short Circuit condition found
	 */
	public void initializeSummation
			(T derivative0)
	throws ShortCircuitTermination
	{
		this.summation = manager.getZero ();
		this.combo = new Combinatorics <> (manager, null);
		this.applyIteration ( n = 0, derivative0 );
	}
	protected Combinatorics <T> combo;


	/**
	 * @param functionParameter the parameter to the function evaluation
	 */
	public void initializeFunction (T functionParameter)
	{ this.functionParameter = functionParameter; }
	protected T functionParameter;


	/*
	 * contribution to the trace display
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.iterative.IterationFoundations#add(java.lang.StringBuffer)
	 */
	public void add (StringBuffer buffer)
	{
		buffer.append ("Sigma = ").append (toString (summation));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.iterative.IterationFoundations#toString(java.lang.Object)
	 */
	public String toString (T x) { return manager.toDecimalString (x); }


}

