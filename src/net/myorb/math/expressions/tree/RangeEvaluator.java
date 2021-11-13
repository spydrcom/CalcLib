
package net.myorb.math.expressions.tree;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.symbols.IterationConsumer;

import net.myorb.data.abstractions.SimpleUtilities;

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
		this.symbols = symbols;
		this.calculator = calculator;
		this.descriptor = rangeDescriptor;
		this.consumer = rangeDescriptor.iterationConsumer;

		this.loBnd = calculator.evaluate (rangeDescriptor.loExpr);
		this.hiBnd = calculator.evaluate (rangeDescriptor.hiExpr);
		this.delta = calculator.evaluate (rangeDescriptor.delta);

		this.incrementLoBound =
			RangeAttributes.LT.equals (rangeDescriptor.lbndOp.getSymbolProperties ().getName ());
		this.hiCompare = symbolFor (rangeDescriptor.hbndOp.getSymbolProperties ().getName ());
		this.incrementation = symbolFor ("+");
		this.target = descriptor.target;
	}
	protected LexicalAnalysis.RangeDescriptor<T> descriptor;
	protected GenericValue loBnd, hiBnd, delta;
	protected CalculationEngine<T> calculator;
	protected IterationConsumer consumer;
	protected Expression<T> target;
	protected SymbolMap symbols;


	/**
	 * find operator in symbol table
	 * @param name the name of the operator
	 * @return the symbol table entry object
	 */
	public SymbolMap.BinaryOperator symbolFor (String name)
	{
		SymbolMap.BinaryOperator op =
		SimpleUtilities.verifyClass (symbols.lookup (name), SymbolMap.BinaryOperator.class);
		if (op == null) throw new RuntimeException ("Internal error: BAD OPERATOR");
		return op;
	}


	/**
	 * @return aggregated value of all iterations consumed
	 */
	public GenericValue evaluateLoop ()
	{
		consumer.init ();
		GenericValue iterator, result;
		initializeLocalVariable ();
		if (incrementLoBound)
		{ increment (); }

		while (inRange ())
		{
			consumer.setIterationValue
				(iterator = localVariable.getValue ());
			showIteration (iterator, result = calculator.evaluate (target));
			consumer.accept (result);
			increment ();
		}

		return consumer.getCalculatedResult ();
	}
	protected boolean incrementLoBound = false;


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
	 * dump for local variable attributes
	 */
	public void dumpLocal ()
	{
		System.out.print ("local var: "); System.out.print (variableName); System.out.print (" = ");
		System.out.print (localVariable); System.out.print (" ; "); System.out.println (localVariable.getSymbolType ());
		System.out.println (localIdentifier + " " + localIdentifier.getTypeManager ().getType ());
	}


	/**
	 * iteration variable set to low bound of range
	 */
	public void initializeLocalVariable ()
	{
		// prep for stats
		iteration = 0; every = 10000;
		startTime = System.nanoTime ();

		// construct symbols
		variableName = descriptor.variableName;
		localVariable = new LocalVariable (variableName);
		deltaVariable = new LocalVariable ("\u2202" + variableName);

		// symbol table links
		localIdentifier = descriptor.target.identifiers.get (variableName);
		localIdentifier.getSymbolProperties ().setSymbolReference (localVariable);
		localIdentifier.getIdentifierProperties ().setAsLocalType ();

		// set identifier values
		localVariable.setValue (loBnd);
		deltaVariable.setValue (delta);

		// add to symbol table
		symbols.add (localVariable);
		symbols.add (deltaVariable);
	}
	protected long startTime, iteration, every;
	protected LexicalAnalysis.Identifier<T> localIdentifier;
	protected LocalVariable localVariable, deltaVariable;
	protected String variableName;


	/**
	 * check value of local variable
	 * @return TRUE = more iterations
	 */
	public boolean inRange ()
	{
		if (++iteration > every) { runStats (); }
		GenericValue varValue = localVariable.getValue ();
		GenericValue comparison = hiCompare.execute (varValue, hiBnd);
		return ! calculator.isZero (comparison);
	}
	protected SymbolMap.BinaryOperator hiCompare;


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


	/**
	 * increment local variable with delta
	 */
	public void increment ()
	{
		GenericValue sum =
			incrementation.execute (localVariable.getValue (), delta);
		localVariable.setValue (sum);
	}
	protected SymbolMap.BinaryOperator incrementation;


}


/**
 * symbol table representation of local variable
 */
class LocalVariable implements SymbolMap.VariableLookup
{

	/**
	 * @param name the name assigned to local variable
	 */
	LocalVariable (String name) { this.name = name; }
	protected String name;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Named#getName()
	 */
	public String getName () { return name; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Named#getSymbolType()
	 */
	public SymbolMap.SymbolType getSymbolType () { return SymbolMap.SymbolType.IDENTIFIER; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.VariableLookup#getValue()
	 */
	public GenericValue getValue () { return value; }
	public void setValue (GenericValue value) { this.value = value; }
	protected GenericValue value;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return value.toString (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.VariableLookup#rename(java.lang.String)
	 */
	public void rename (String to) {}

}

