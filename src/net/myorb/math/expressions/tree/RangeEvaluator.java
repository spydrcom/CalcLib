
package net.myorb.math.expressions.tree;

import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.symbols.IterationConsumer;
import net.myorb.math.expressions.SymbolMap;

/**
 * run calculation of aggregate of iterations over described range
 * @param <T> data type of expressions
 * @author Michael Druckman
 */
public class RangeEvaluator<T>
{


	/**
	 * @param rangeDescriptor the descriptor of the range
	 * @param calculator the calculator for the node values
	 * @param symbols the table of symbols currently recognized
	 */
	RangeEvaluator
		(
			LexicalAnalysis.RangeDescriptor<T> rangeDescriptor,
			CalculationEngine<T> calculator, SymbolMap symbols
		)
	{
		this.digest = new RangeNodeDigest<T> (rangeDescriptor, calculator, symbols);
	}
	protected RangeNodeDigest<T> digest;


	/**
	 * @return the value computed from the range description
	 */
	public GenericValue evaluateRangeExpression ()
	{
		IterationConsumer
			consumer = digest.getConsumer ();
		if (digest.isNumericalAnalysisConsumer ())
		{ return digest.applyNumericalAnalysis (); }
		return evaluateLoop (consumer);
	}


	/**
	 * @param consumer the object used to digest the iteration results
	 * @return aggregated value of all iterations consumed
	 */
	public GenericValue evaluateLoop (IterationConsumer consumer)
	{
		consumer.init ();
		doIterativeLoopEvaluation (consumer);
		GenericValue v = consumer.getCalculatedResult ();
		return v;
	}


	/**
	 * standard delta iterative loop
	 * @param consumer the object used to digest the iteration results
	 */
	public void doIterativeLoopEvaluation (IterationConsumer consumer)
	{
		GenericValue iterator, result;
		initializeLocalVariable ();

		if (digest.incrementLoBound ()) { digest.increment (); }

		while (inRange ())
		{
			consumer.setIterationValue
				(iterator = digest.getLocalVariableValue ());
			showIteration (iterator, result = digest.evaluateTarget ());
			consumer.accept (result);
			digest.increment ();
		}
	}


	/**
	 * trace loop as values are generated
	 * @param local the value of the local variable
	 * @param result the value generated for the iteration
	 */
	public static void showIteration (GenericValue local, GenericValue result)
	{
		if (! LOOP_TRACE) return;
		System.out.print (local); System.out.print (": ");
		System.out.println (result);
	}
	public static final boolean LOOP_TRACE = false;




	/**
	 * iteration variable set to low bound of range
	 */
	public void initializeLocalVariable ()
	{
		// prep for stats
		iteration = 0; every = 10000;
		startTime = System.nanoTime ();

		// perform initialization in digest
		digest.initializeLocalVariable ();
	}
	protected long startTime, iteration, every;


	/**
	 * check value of local variable
	 * @return TRUE = more iterations
	 */
	public boolean inRange ()
	{
		if (++iteration > every) { runStats (); }
		return digest.inRange ();
	}


	/**
	 * maintain average loop time for efficiency metric
	 */
	void runStats ()
	{
		iterations++;
		float iterationTime = (float) System.nanoTime ();
		float duration = iterationTime - startTime;
		long  count = iterations * every;
		average = duration / count;
		iteration = 0;

		if (displayMetrics)
		{
			System.out.print ("Average Loop Time (ns/iteration @ " + count + ") = ");
			System.out.println (average);
		}
	}
	public static boolean displayMetrics = true;
	protected long iterations = 0;
	protected float average;


}

