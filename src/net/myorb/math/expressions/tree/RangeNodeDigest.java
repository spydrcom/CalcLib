
package net.myorb.math.expressions.tree;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.tree.LexicalAnalysis.Operator;
import net.myorb.math.expressions.symbols.IterationConsumer;

import net.myorb.data.abstractions.SimpleUtilities;

/**
 * provide digest of semantic analysis of range node
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class RangeNodeDigest<T>
{


	public RangeNodeDigest
		(
			LexicalAnalysis.RangeDescriptor<T> rangeDescriptor,
			CalculationEngine<T> calculator, SymbolMap symbols
		)
	{
		this.symbols = symbols;
		this.calculator = calculator;
		this.descriptor = rangeDescriptor;
	
		this.loBnd = calculator.evaluate (rangeDescriptor.loExpr);
		this.hiBnd = calculator.evaluate (rangeDescriptor.hiExpr);
		this.delta = calculator.evaluate (rangeDescriptor.delta);
	
		this.consumer = rangeDescriptor.iterationConsumer;
		this.hiCompare = symbolFor (rangeDescriptor.hbndOp);

		this.incrementation = symbolFor ("+");
		this.target = descriptor.target;
	}
	protected LexicalAnalysis.RangeDescriptor<T> descriptor;
	protected GenericValue loBnd, hiBnd, delta;
	protected CalculationEngine<T> calculator;
	protected SymbolMap symbols;


	/**
	 * @return value computed from lo-bound expression
	 */
	public GenericValue getLoBnd () { return loBnd; }

	/**
	 * @return value computed from hi-bound expression
	 */
	public GenericValue getHiBnd () { return hiBnd; }

	/**
	 * @return value computed from delta expression
	 */
	public GenericValue getDelta () { return delta; }


	/**
	 * @return TRUE when consumer implements NumericalAnalysis
	 */
	public boolean isNumericalAnalysisConsumer ()
	{
		return consumer instanceof NumericalAnalysis;
	}


	/**
	 * @return value calculated with Numerical Analysis algorithm
	 */
	public GenericValue applyNumericalAnalysis ()
	{
		@SuppressWarnings("unchecked")
		NumericalAnalysis<T> analysis = (NumericalAnalysis<T>) consumer;
		return analysis.evaluate (this);
	}


	/**
	 * @return consumer identified in node
	 */
	public IterationConsumer getConsumer ()
	{
		return consumer;
	}
	protected IterationConsumer consumer;


	/**
	 * iteration variable set to low bound of range
	 */
	public void initializeLocalVariable ()
	{
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
	protected LexicalAnalysis.Identifier<T> localIdentifier;
	protected LocalVariable deltaVariable;
	protected String variableName;


	/**
	 * @return computed value of target expression
	 */
	public GenericValue evaluateTarget ()
	{
		return calculator.evaluate (target);
	}

	/**
	 * @return the target object referenced by the digest
	 */
	public Expression<?> getTargetExpression ()
	{
		return target;
	}
	protected Expression<T> target;


	/**
	 * @param value new value to be assigned
	 */
	public void setLocalVariableValue (GenericValue value)
	{
		localVariable.setValue (value);
	}


	/**
	 * @return value currently in local variable
	 */
	public GenericValue getLocalVariableValue ()
	{
		return localVariable.getValue ();
	}
	protected LocalVariable localVariable;


	/**
	 * increment local variable with delta
	 */
	public void increment ()
	{
		GenericValue sum =
			incrementation.execute (getLocalVariableValue (), delta);
		localVariable.setValue (sum);
	}
	protected SymbolMap.BinaryOperator incrementation;


	/**
	 * check value of local variable
	 * @return TRUE = more iterations
	 */
	public boolean inRange ()
	{
		GenericValue varValue = getLocalVariableValue ();
		GenericValue comparison = hiCompare.execute (varValue, hiBnd);
		return ! calculator.isZero (comparison);
	}
	protected SymbolMap.BinaryOperator hiCompare;


	/**
	 * determine if low-bound indicates the low end of the range should be avoided
	 * @return TRUE when low-bound operator indicates not-equal
	 */
	public boolean incrementLoBound ()
	{
		return RangeAttributes.LT.equals (nameOf (descriptor.lbndOp));
	}


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
	 * retrieve name of operator
	 * @param op operator to examine
	 * @return the name of the operator
	 */
	public String nameOf (Operator op)
	{
		return op.getSymbolProperties ().getName ();
	}


	/**
	 * get symbol for operator
	 * @param op operator to examine
	 * @return the operator descriptor from the symbol map
	 */
	public SymbolMap.BinaryOperator symbolFor (Operator op)
	{
		return symbolFor (nameOf (op));
	}


	/**
	 * dump for local variable attributes
	 */
	public void dumpLocal ()
	{
		System.out.print ("local var: "); System.out.print (variableName); System.out.print (" = ");
		System.out.print (localVariable); System.out.print (" ; "); System.out.println (localVariable.getSymbolType ());
		System.out.println (localIdentifier + " " + localIdentifier.getTypeManager ().getType ());
	}


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


