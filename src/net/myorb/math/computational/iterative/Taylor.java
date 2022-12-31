
package net.myorb.math.computational.iterative;

import net.myorb.math.GeneratingFunctions.Coefficients;
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
	 * compile coefficients for an equation
	 * @param computer the Derivative Computer for the equation
	 * @param n the number of the term being evaluated
	 * @return the computed coefficients
	 */
	public Coefficients <T> computeCoefficients
		(IterationTools.DerivativeComputer <T> computer, int n)
	{
		Coefficients <T> C = new Coefficients <T> ();
		for (int i = 0; i <= n; i++)
		{
			C.add
			(
				computeTermCoefficient
				(
					computer.nTHderivative (i), n
				)
			);
		}
		return C;
	}

	/**
	 * compute the Nth coefficient for an equation
	 * @param nTHderivative the value of the Nth derivative evaluated at zero
	 * @param n the number of the term being evaluated
	 * @return the computed Nth coefficient
	 */
	public T computeTermCoefficient
		(T nTHderivative, int n)
	{
		return manager.multiply
			(
				manager.invert
					(combo.factorial (n)),
				nTHderivative
			);
	}


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
			this.initializeSummation (computer.nTHderivative (0));

			for (int n = 1; n <= iterations; n++)
			{
				applyIteration (computer.nTHderivative (n));
			}
		}
		catch (ShortCircuitTermination SC)
		{
			System.out.println (SC.getMessage ());
		}
		return summation;
	}
	protected int atTermNumber;


	/**
	 * compute the specified term and add into the summation
	 * @param n the value of the summation index for this iteration
	 * @param nTHderivative Nth evaluated derivative for the algorithm
	 * @throws ShortCircuitTermination for Short Circuit condition found
	 */
	public void applyIteration (int n, T nTHderivative) throws ShortCircuitTermination
	{
		this.setX
		(
			manager.multiply
			(
				x, manager.pow (functionParameter, n - this.atTermNumber)
			)
		);
		this.setDerivativeAtX (nTHderivative);
		this.setDelta
		(
			manager.multiply
			(
				x, this.computeTermCoefficient (nTHderivative, n)
			)
		);
		this.atTermNumber = n;
		this.addTerm ();
	}


	/**
	 * add the current delta evaluation into the summation
	 * @return the value of the summation after the term is added
	 * @throws ShortCircuitTermination for Short Circuit condition found
	 */
	public T addTerm () throws ShortCircuitTermination
	{
		this.summation = manager.add (this.summation, this.getDelta ());
		this.testForShortCircuit (this.getDelta (), this.atTermNumber, manager);
		return this.summation;
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
		this.atTermNumber = 0; this.setX (manager.getOne ());
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

