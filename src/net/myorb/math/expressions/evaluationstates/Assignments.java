
package net.myorb.math.expressions.evaluationstates;

import net.myorb.math.expressions.symbols.*;
import net.myorb.math.expressions.*;

import net.myorb.data.abstractions.ErrorHandling;

import java.util.List;

/**
 * process operators that implement symbol value assignment
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class Assignments<T> extends Primitives<T>
{


	/**
	 * initialize state controls for Assignment Processing
	 */
	public void initAssignmentProcessing ()
	{
		assignmentPending = false;
		setOperatorLastSeen ();
		assignTo = null;
	}


	/**
	 * process special case for "negate" operator
	 * @param op the operation being check for special processing needs
	 * @param opPrec the precedence of the operation
	 */
	public void processBinaryOpSpecialCase (SymbolMap.Operation op, int opPrec)
	{
		if  (
				operatorLastSeen &&
				op instanceof SymbolMap.BinaryOperator &&
				opPrec != SymbolMap.FUNCTTION_PRECEDENCE
			)
		{
			resetOperatorLastSeen ();
			getValueStack ().push (ZERO);
		}
		else setOperatorLastSeen ();
	}


	/**
	 * @param newStatus mark as seen TRUE or not seen FALSE
	 */
	protected void setOperatorStatus (boolean newStatus) { operatorLastSeen = newStatus; }
	protected void resetOperatorLastSeen () { setOperatorStatus (false); }
	protected void setOperatorLastSeen () { setOperatorStatus (true); }
	private boolean operatorLastSeen = true;


	/**
	 * push value onto stack
	 */
	public void processValue ()
	{
		resetOperatorLastSeen ();
		pushTokenOnValueStack ();
		dumpValueStack ();
	}


	/**
	 * complete the pending assignment setup.
	 * token image is stored and pending flag is reset
	 */
	public void processPendingAssignment ()
	{ assignTo = getTokenImage (); assignmentPending = false; }
	protected boolean assignmentPending = false;


	/**
	 * process assignment operations
	 * @param opPrec the precedence of the current operation
	 * @return TRUE = assignment operator found and processed
	 */
	public boolean assignmentProcessed (int opPrec)
	{
		switch (opPrec)
		{

			case SymbolMap.ASSIGNMENT_PRECEDENCE:
				assignmentPending = true;
				return true;

			case SymbolMap.STORAGE_PRECEDENCE:
				identifyAssignmentOperator ();
				return true;

			default:
				return false;

		}
	}

	/**
	 * check for indexed assignment
	 */
	public void identifyAssignmentOperator ()
	{
		setOperatorLastSeen ();
		if (assignTo != null || ! isIndexed (getTOSvalue ()))
		{ pushAssignmentOperator (assignTo); assignTo = null; }
	}

	/**
	 * pop top of stack and check for error
	 * @return the generic value popped from top of stack
	 * @throws ErrorHandling.Terminator for invalid assignment observed
	 */
	public ValueManager.GenericValue getTOSvalue () throws ErrorHandling.Terminator
	{
		try
		{
			return getValueStack ().pop ();
		}
		catch (Exception e)
		{
			throw new ErrorHandling.Terminator ("Assignment is invalid", e);
		}
	}

	/**
	 * determine if index operator should be pushed
	 * @param value prior top of stack which becomes assigned
	 * @return TRUE when value stack peek is not null
	 */
	public boolean isIndexed (ValueManager.GenericValue value)
	{
		if (getValueStack ().peek () == null)
		{
			assignTo = value.getName ();
			return false;
		}

		pushIndexedAssignmentOperator
		(
			getValueStack ().pop ().getName (), value
		);

		return true;
	}
	protected String assignTo = null;


	/**
	 * build indexed assignment operation
	 * @param variable name of variable being assigned
	 * @param index value of the index
	 */
	public void pushIndexedAssignmentOperator
		(
			String variable, ValueManager.GenericValue index
		)
	{
		popOpStackToTos ();								// eliminate unnecessary indexing operation

		pushOpStack
		(
			new IndexedAssignmentOperator<T>
			(
				variable, index, getSymbolMap ()
			)
		);
	}


	/**
	 * build assignment operation
	 * @param variable name of variable being assigned
	 */
	public void pushAssignmentOperator (String variable)
	{
		pushOpStack (new AssignmentOperator (variable, getSymbolMap ()));
	}


	/**
	 * push the value of a symbol
	 * @param v descriptor of the variable
	 */
	public void pushVariableValue (ValueManager.GenericValue v)
	{
		getValueStack ().push (v);
		resetOperatorLastSeen ();
		dumpValueStack ();
	}


	/**
	 * invoke variable assignment operation
	 * @param op SymbolMap.VariableAssignment object
	 */
	public void process (SymbolMap.VariableAssignment op)
	{
		op.getSymbolMap ().add (generateSymbolAssignment (op));
	}


	/**
	 * construct storage object for variable
	 * @param op SymbolMap.VariableAssignment object
	 * @return new variable storage object
	 */
	public NamedObject generateSymbolAssignment (SymbolMap.VariableAssignment op)
	{
		return assign (getValueStack ().pop (), op.getName ());
	}


	/**
	 * connect a variable to a value
	 * @param value the value being assigned
	 * @param toVariableName the name of the variable
	 * @return the named object holding the assignment
	 */
	public NamedObject assign (ValueManager.GenericValue value, String toVariableName)
	{
		ValueManager.setFormatter (value, getSpaceManager ());
		return generateSymbolStorage (toVariableName, value);
	}


	/**
	 * save assignment to symbol map
	 * @param value the value being assigned
	 * @param toVariableName the name of the variable
	 */
	public void postAssignment (ValueManager.GenericValue value, String toVariableName)
	{
		getSymbolMap ().add (assign (value, toVariableName));
	}


	/**
	 * invoke indexed variable assignment operation
	 * @param op SymbolMap.IndexedVariableAssignment object
	 */
	public void process (SymbolMap.IndexedVariableAssignment op)
	{
		SymbolMap.Named symbol;
		String name = op.getName ();
		SymbolMap symbols = op.getSymbolMap ();
		if ((symbol = (SymbolMap.Named) symbols.get (name)) == null) generatePreviouslyUndefined (op);
		else if (symbol instanceof SymbolMap.VariableLookup) previouslyDefined (op, (SymbolMap.VariableLookup) symbol);
		else throw new RuntimeException ("Assignment not consistent, symbol does not refer to a variable");
	}


	/**
	 * evaluate the assignment operator
	 * @param op the operator being processed
	 * @param symbol the symbol found in the symbol table
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void previouslyDefined (SymbolMap.IndexedVariableAssignment op, SymbolMap.VariableLookup symbol)
	{
		ValueManager.GenericValue value = symbol.getValue ();
		if (value instanceof ValueManager.UndefinedValue) generatePreviouslyUndefined (op);
		else if (value instanceof ValueManager.DimensionedValue) updatePreviouslySet (op, (ValueManager.DimensionedValue)value);
		else throw new RuntimeException ("Assignment not consistent, symbol does not refer to an array");
	}


	/**
	 * starting a new array
	 * @param op the operator being processed
	 */
	public void generatePreviouslyUndefined
		(SymbolMap.IndexedVariableAssignment op)
	{
		performAssignment (op, new ValueManager.RawValueList<T> ());
	}


	/**
	 * symbol being assigned shows up
	 *  in symbol table as previously existing array
	 * @param op the operator being processed causing update to array
	 * @param value the original symbol value
	 */
	public void updatePreviouslySet
		(
			SymbolMap.IndexedVariableAssignment op,
			ValueManager.DimensionedValue<T> value
		)
	{
		performAssignment (op, value.getValues ());
	}


	/**
	 * effect changes to the array
	 *  consistent with assignment being processed
	 * @param op the operator being processed
	 * @param array the array to update
	 */
	public void performAssignment
		(
			SymbolMap.IndexedVariableAssignment op,
			ValueManager.RawValueList<T> array
		)
	{
		int index;
		resizeArray (index = processIndicies (op), array);
		ValueManager.GenericValue elementValue = getValueStack ().pop ();
		array.set (index, getValueManager ().toDiscrete (elementValue));
		ValueManager.GenericValue arrayValue = getValueManager ().newDimensionedValue (array);
		op.getSymbolMap ().add (generateSymbolStorage (op.getName (), arrayValue));
	}


	/**
	 * ensure size of the array
	 * @param forLastElement index attempting to be written
	 * @param array the array to be sized
	 */
	public void resizeArray (int forLastElement, List<T> array)
	{
		int requiredSize = forLastElement + 1;
		while (array.size () < requiredSize) array.add (ZERO);
	}


	/**
	 * evaluate the index value(s) being used
	 * @param op the operator currently being processed
	 * @return the integer offset from start of array
	 */
	public int processIndicies (SymbolMap.IndexedVariableAssignment op)
	{
		T index = getValueManager ().toDiscrete (op.getIndexValues ());
		return getSpaceManager ().toNumber (index).intValue ();
	}


}

