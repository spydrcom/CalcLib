
package net.myorb.math.expressions.evaluationstates;

import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.TypedRangeDescription;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.ConventionalNotations;

import net.myorb.data.abstractions.CommonCommandParser.TokenDescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * a value meta-data descriptor for arrays
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class ArrayDescriptor <T>
	implements Arrays.Descriptor <T>, TypedRangeDescription.TypedRangeProperties <T>
{


	/**
	 * a generic description for an array
	 * @param lo the lo value of the domain constraint for the array
	 * @param hi the hi value of the domain constraint for the array
	 * @param delta the value increment between elements of the array
	 * @param variable the name of the variable that represents the index value
	 * @param arrayNotation the tokens that comprise the description of the range
	 * @param expression the tokens that comprise the expression that produced this array
	 * @param manager a manager for the underlying data type
	 */
	public ArrayDescriptor
		(
			T lo, T hi,
			T delta, String variable,
			TokenParser.TokenSequence arrayNotation,
			TokenParser.TokenSequence expression,
			ExpressionSpaceManager<T> manager
		)
	{
		this.delta = delta;
		this.lo = lo; this.hi = hi;
		this.arrayNotation = arrayNotation;
		this.expression = expression;
		this.variable = variable;
		this.manager = manager;
	}
	protected List<TokenDescriptor> arrayNotation;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Arrays.ConstrainedDomain#getDelta()
	 */
	public T getDelta() { return delta; }
	public void setDelta(T delta) { this.delta = delta; }
	public TokenParser.TokenSequence getExpression() { return expression; }
	public void setExpression (TokenParser.TokenSequence expression) { this.expression = expression; }
	protected TokenParser.TokenSequence expression;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Arrays.Descriptor#getVariable()
	 */
	public String getVariable() { return variable; }
	public void setVariable(String variable) { this.variable = variable; }
	protected String variable;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Arrays.ConstrainedDomain#getHi()
	 */
	public T getHi() { return hi; }
	public void setHi(T hi) { this.hi = hi; }
	public void setLo(T lo) { this.lo = lo; }
	public T getTypedIncrement() { return delta; }
	public T getTypedHi() { return hi; }
	public T getTypedLo() { return lo; }
	public T getLo() { return lo; }
	protected T delta, lo, hi;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Arrays.Descriptor#genMacro(net.myorb.math.expressions.evaluationstates.Environment, boolean)
	 */
	public ExpressionMacro<T> genMacro (Environment<T> environment, boolean silent)
	{
		ExpressionMacro<T> macro =
			environment.constructMacro
				(getVariable (), getExpression ());
		if (silent) macro.supressErrorMessages ();
		return macro;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Arrays.Descriptor#enumerateDomain(int)
	 */
	public List<T> enumerateDomain (int count)
	{
		T x = lo;
		List<T> list = new ArrayList<T> ();
		for (int i = 0; i < count; i++)
		{
			list.add (x);
			x = manager.add (x, delta);
		}
		return list;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Arrays.Descriptor#getDomainConstraints()
	 */
	public Arrays.ConstrainedDomain<T> getDomainConstraints ()
	{
		return new ConstraintDescriptor<T> (this);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Arrays.ConstrainedDomain#checkConstraintsAgainst(java.lang.Object)
	 */
	public void checkConstraintsAgainst (T x)
	{
		if (manager.lessThan (x, lo) || manager.lessThan (hi, x))
		{ throw new RuntimeException ("Value not within domain constraints: " + x + " not in " + lo + ":" + hi); }
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Arrays.ConstrainedDomain#describeReducedInterval(java.lang.Object, java.lang.Object, java.lang.String)
	 */
	public ArrayFunctionDescriptor<T> describeReducedInterval (T lo, T hi, String op)
	{
		ArrayFunctionDescriptor<T> afd =
			new ArrayFunctionDescriptor<T> (this, lo, hi, manager);
		afd.updateExpressionText (op, this);
		return afd;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Arrays.ConstrainedDomain#describeSimilarInterval(java.lang.String)
	 */
	public ArrayFunctionDescriptor<T> describeSimilarInterval (String op)
	{
		ArrayFunctionDescriptor<T> afd =
			new ArrayFunctionDescriptor<T> (this, manager);
		afd.updateExpressionText (op, this);
		return afd;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Arrays.Descriptor#getExpressionText()
	 */
	public String getExpressionText ()
	{
		return TokenParser.toPrettyText (getExpression ());
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Arrays.Descriptor#formatTitle()
	 */
	public String formatTitle ()
	{
		String control = null;
		if (arrayNotation != null) control = "[ " + TokenParser.toPrettyText (arrayNotation) + " ] ";
		return ((control==null)? toString (): control) + " " + TokenParser.toPrettyText (getExpression ());
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		manager.setDisplayPrecision (3);
		StringBuffer buffer = new StringBuffer ()
				.append ("[").append (manager.toDecimalString (lo)).append (" <= ")
				.append (formattedVariable ()).append (" <= ").append (manager.toDecimalString (hi))
				.append (" <> ").append (manager.toDecimalString (delta)).append ("]");
		manager.resetDisplayPrecision ();
		return buffer.toString ();
	}
	String formattedVariable () { return ConventionalNotations.determineNotationFor (variable); }
	protected ExpressionSpaceManager<T> manager;


}


/**
 * wrapper for array descriptor only exporting constraint methods
 * @param <T> type on which operations are to be executed
 */
class ConstraintDescriptor<T> implements Arrays.ConstrainedDomain<T>
{

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Arrays.ConstrainedDomain#getHi()
	 */
	public T getHi() { return array.getHi (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Arrays.ConstrainedDomain#getLo()
	 */
	public T getLo() { return array.getLo (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Arrays.ConstrainedDomain#getDelta()
	 */
	public T getDelta () { return array.getDelta (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Arrays.ConstrainedDomain#checkConstraintsAgainst(java.lang.Object)
	 */
	public void checkConstraintsAgainst (T x)
	{
		array.checkConstraintsAgainst (x);
	}

	/**
	 * wrap an array descriptor
	 * @param array the descriptor being wrapped
	 */
	public ConstraintDescriptor (Arrays.Descriptor<T> array)
	{
		this.array = array;
	}
	protected Arrays.Descriptor<T> array;

}

