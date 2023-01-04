
package net.myorb.math.computational.iterative;

import net.myorb.math.GeneratingFunctions.Coefficients;
import net.myorb.math.computational.Combinatorics;

import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;

/**
 * approximate a function using series expansion methods of Brook Taylor
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class Taylor <T> extends IterationFoundations <T>
{


	//  f(x) = SIGMA [ 0 <= n <= INFINITY ] ( f'n(0)/n! * x^n )


	/**
	 * multiple passed as procedure parameter
	 * @param <T> data type
	 */
	public interface TermMultipleOfX <T>
	{
		/**
		 * @return the multiplier for the term
		 */
		T getMultiple ();
	}


	public Taylor (SpaceManager <T> manager)
	{ this.combo = new Combinatorics <> (this.manager = manager, null); }
	protected SpaceManager <T> manager; protected Combinatorics <T> combo;


	/**
	 * build a Polynomial Power Function for a Taylor series
	 * @param computer the DerivativeComputer for the Taylor series
	 * @param withOrder the order of the Polynomial to build
	 * @return the Polynomial Power Function for the series
	 */
	public Polynomial.PowerFunction <T> seriesFor
		(IterationTools.DerivativeComputer <T> computer, int withOrder)
	{
		return new Polynomial <> (manager).getPolynomialFunction
			(computeCoefficients (computer, withOrder));
	}


	/**
	 * compile coefficients for an equation
	 * @param computer the Derivative Computer for the equation
	 * @param n the number of the terms being evaluated
	 * @return the computed coefficients
	 */
	public Coefficients <T> computeCoefficients
		(IterationTools.DerivativeComputer <T> computer, int n)
	{
		Coefficients <T>
			C = new Coefficients <T> ();
		C.add (computer.nTHderivative (0));		// optimize out Factorial
		C.add (computer.nTHderivative (1));		// 0 and 1 have factorial 1
		computeCoefficients
		(
			computer, C,						// the computer and coefficients
			manager.getOne (), n				// initial factorial and order
		);
		return C;
	}
	public void computeCoefficients
		(
			IterationTools.DerivativeComputer <T> computer, 
			Coefficients <T> C, T F, int order
		)
	{
		for (int i = 2; i <= order; i++)
		{

			F = manager.multiply					// optimized factorial
				(									// reduce multiplications count
					F, manager.newScalar (i)		// single multiply per term
				);
			T iTHderivative = computer.nTHderivative (i);

			C.add
			(
				! manager.isZero (iTHderivative)	// short-circuit for zero derivative

				? manager.multiply
					(
						iTHderivative,				// iTH derivative, non-zero, as computed
						manager.invert (F)			// divided by factorial of term number
					)

				: iTHderivative						// zero derivative eliminates multiply
			);

		}
	}


	/**
	 * raw logic of the building of the coefficient series
	 * - computation of term coefficients disregarding computation of factorial
	 * - this will cause the full factorial computation for each coefficient
	 * @param computer the Derivative Computer for the equation
	 * @param n the number of the terms being evaluated
	 * @return the computed coefficients
	 */
	public Coefficients <T> computeCoefficientsUnoptimized
		(IterationTools.DerivativeComputer <T> computer, int n)
	{
		Coefficients <T> C = new Coefficients <T> ();
		for (int i = 0; i <= n; i++)
		{
			C.add
			(
				computeTermCoefficient
				(
					computer.nTHderivative (i), i
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
		T result = nTHderivative;
		if ( ! manager.isZero (result) )
		{
			result = manager.multiply
				(
					manager.invert
						(combo.factorial (n)),
					result
				);
		}
		return result;
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


	/*
	 * components of series term evaluations
	 */

	/**
	 * short-circuit for zero derivative
	 * @param derivative value of the derivative
	 * @return TRUE when zero
	 */
	public boolean isZeroDerivative (T derivative)
	{
		this.setDerivativeAtX (derivative);
		if (manager.isZero (derivative))
		{
			this.setDelta (derivative);
			return true;
		}
		return false;
	}

	/**
	 * maintain power of x
	 * - terms with zero derivatives will have been skipped
	 * - power of x will be maintained by determination of power needed to catch-up
	 * @param n number of term being evaluated
	 */
	public void setPowerOfX (int n)
	{
		if ( n == 0 )
		{
			this.setX ( manager.getOne () );
			return;
		}

		this.setX
		(
			manager.multiply
			(
				this.getX (), manager.pow ( functionParameter, n - this.atTermNumber )
			)
		);
	}

	/**
	 * set the value of delta as the product of x and its multiplier
	 * @param comutedFrom computer for the multiple of x appropriate to this term
	 */
	public void setDeltaMultipleOfX (TermMultipleOfX <T> comutedFrom)
	{
		this.setDelta
		(
			manager.multiply
			(
				this.getX (), comutedFrom.getMultiple ()
			)
		);
	}

	/**
	 * identify components x and delta and adjust the summation
	 * @param n the value of the summation index for this iteration
	 * @param nTHderivative Nth evaluated derivative for the algorithm
	 * @param comutedFrom computer for the multiple of x appropriate to term n
	 * @throws ShortCircuitTermination signal for ShortCircuit condition
	 */
	public void processTerm
		(int n, T nTHderivative, TermMultipleOfX <T> comutedFrom)
	throws ShortCircuitTermination
	{
		if ( ! isZeroDerivative (nTHderivative) )
		{
			this.setPowerOfX (n);
			this.setDeltaMultipleOfX (comutedFrom);
			this.atTermNumber = n;
			this.addTerm ();
		}
	}
	protected int atTermNumber;

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


	/*
	 * iterative term processing with in-line term coefficient computation
	 */

	/**
	 * compute the specified term and add into the summation
	 * @param n the value of the summation index for this iteration
	 * @param nTHderivative Nth evaluated derivative for the algorithm
	 * @throws ShortCircuitTermination for Short Circuit condition found
	 */
	public void applyIteration (int n, T nTHderivative) throws ShortCircuitTermination
	{ this.processTerm ( n, nTHderivative, () -> this.computeTermCoefficient (nTHderivative, n) ); }

	/**
	 * run the next iteration in the series
	 * @param nTHderivative the next derivative value
	 * @throws ShortCircuitTermination for Short Circuit condition found
	 */
	public void applyIteration
		(T nTHderivative)
	throws ShortCircuitTermination
	{
		applyIteration
			( n += 1, nTHderivative );
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
		this.initializeSummation ();
		this.applyIteration ( n = 0, derivative0 );
	}


	/*
	 * evaluate polynomial using iteration foundation processing
	 */

	/**
	 * evaluate series from coefficients
	 * - precision management is applied term by term
	 * @param C list of coefficients
	 * @return computed value
	 */
	public T sumOfSeries (Coefficients <T> C)
	{
		for (int i = 0; i < C.size (); i++)
		{
			T c = C.get (i);
			try { this.processTerm ( i, c, () -> c ); }
			catch ( ShortCircuitTermination  SC )
			{ trace (SC); return summation; }
		}
		return summation;
	}

	/**
	 * evaluate polynomial using iteration foundation processing
	 * - precision management including short circuit detection applied
	 * - trade-off between efficiency of monitor VS full precision evaluation
	 * - some additional overhead for the precision monitor
	 * @param x the parameter to the function
	 * @param C list of coefficients
	 * @return computed value
	 */
	public T eval
		(
			T x,
			Coefficients <T> C
		)
	{
		this.initializeFunction (x);
		this.initializeSummation ();
		return sumOfSeries (C);
	}


	/*
	 * common processing for function evaluation
	 */

	/**
	 * identify the parameter to a function evaluation
	 * @param functionParameter the parameter to the function evaluation
	 */
	public void initializeFunction (T functionParameter)
	{ this.functionParameter = functionParameter; }
	protected T functionParameter;

	/**
	 * prepare a summation
	 * - initial value of summation set to zero
	 * - initial value of power of x set to one assuming zero power of x
	 * - initial value of term number set to zero
	 */
	public void initializeSummation ()
	{
		this.atTermNumber = 0; this.setX (manager.getOne ());
		this.summation = manager.getZero ();
	}


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

