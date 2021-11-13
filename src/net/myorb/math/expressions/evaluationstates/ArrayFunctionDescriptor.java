
package net.myorb.math.expressions.evaluationstates;

import net.myorb.math.expressions.ExpressionSpaceManager;

/**
 * a descripor for arrays that describe functions
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class ArrayFunctionDescriptor<T> extends ArrayDescriptor<T>
{


	/**
	 * the data required to describe an array function
	 * @param identifier the name of the variable that represents the x-axis
	 * @param lo the lo value of the domain constraint for the function
	 * @param hi the hi value of the domain constraint for the function
	 * @param delta the x-axis increment between elements of the array
	 * @param manager a manager for the underlying data type
	 */
	public ArrayFunctionDescriptor
		(
			String identifier, T lo, T hi, T delta,
			ExpressionSpaceManager<T> manager
		)
	{
		super (lo, hi, delta, identifier, null, null, manager);
	}

	/**
	 * copy metadata from another
	 *  descriptor and alter the domain constraints
	 * @param descriptor the descriptor used as the basis for the parameters
	 * @param lo the new lo value for the constraint
	 * @param hi the new hi value for the constraint
	 * @param manager the manager for the type
	 */
	public ArrayFunctionDescriptor
	(Arrays.Descriptor<T> descriptor, T lo, T hi, ExpressionSpaceManager<T> manager)
	{ super (lo, hi, descriptor.getDelta (), descriptor.getVariable (), null, null, manager); }
	public ArrayFunctionDescriptor (Arrays.Descriptor<T> descriptor, ExpressionSpaceManager<T> manager)
	{ super (descriptor.getLo (), descriptor.getHi (), descriptor.getDelta (), descriptor.getVariable (), null, null, manager); }


	/**
	 * the macro is the subroutine form
	 *  that allows arrays to read as functions
	 * @param macro the macro object for this array function
	 */
	public void setExpressionMacro
	(ExpressionMacro<T> macro) { this.macro = macro; }
	protected ExpressionMacro<T> macro = null;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.ArrayDescriptor#genMacro(net.myorb.math.expressions.evaluationstates.Environment, boolean)
	 */
	public ExpressionMacro<T> genMacro (Environment<T> environment, boolean silent)
	{ if (silent) macro.supressErrorMessages (); return macro; }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.ArrayDescriptor#formatTitle()
	 */
	public String formatTitle ()
	{
		if (expressionText == null) return super.formatTitle ();
		return this.toString () + "  ( " + expressionText + " )";
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Arrays.Descriptor#getExpressionText()
	 */
	public String getExpressionText ()
	{
		if (expressionText == null)
			return super.getExpressionText ();
		return expressionText;
	}
	public void setExpressionText (String expressionText)
	{ this.expressionText = expressionText; }


	/**
	 * provide an updated title for displays of the function
	 * @param op the operation that was done on the array
	 * @param source the original array descriptor
	 */
	public void updateExpressionText (String op, Arrays.Descriptor<T> source)
	{
		String description = source.getExpressionText ();
		if (op != null) description = op + " ( " + description + " )";
		setExpressionText (description);
	}
	protected String expressionText = null;


}


