
package net.myorb.math.expressions.evaluationstates;

import net.myorb.math.expressions.symbols.*;
import net.myorb.math.expressions.*;

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
		operatorLastSeen = true;
		assignTo = null;
	}


	/**
	 * process special case for "negate" operator
	 * @param op the operation being check for special processing needs
	 * @param opPrec the precedence of the operation
	 */
	public void processBinaryOpSpecialCase (SymbolMap.Operation op, int opPrec)
	{
		if (operatorLastSeen &&
				op instanceof SymbolMap.BinaryOperator &&
				opPrec != SymbolMap.FUNCTTION_PRECEDENCE)
		{
			operatorLastSeen = false;
			getValueStack ().push (ZERO);
		} else operatorLastSeen = true;
	}
	protected void setOperatorStatus (boolean newStatus) { operatorLastSeen = newStatus; }
	private boolean operatorLastSeen = true;


	/**
	 * push value onto stack
	 */
	public void processValue ()
	{
		operatorLastSeen = false;
		pushTokenOnValueStack ();
		dumpValueStack ();
	}


	/**
	 * complete the pending assignment setup.
	 * token image is stored and pending flag is reset
	 */
	public void processPendingAssignment ()
	{
		assignTo = getTokenImage ();
		assignmentPending = false;
	}
	protected boolean assignmentPending = false;


	/**
	 * process assignment operations
	 * @param opPrec the precedence of the current operation
	 * @return TRUE = assignment operator found and processed
	 */
	public boolean assignmentProcessed (int opPrec)
	{
		if (opPrec == SymbolMap.ASSIGNMENT_PRECEDENCE)
		{
			assignmentPending = true;
			return true;
		}
		else if (opPrec == SymbolMap.STORAGE_PRECEDENCE)
		{
			operatorLastSeen = true;
			if (assignTo == null)
			{
				ValueManager.GenericValue value = getValueStack ().pop ();
				if (getValueStack ().peek () == null)
					assignTo = value.getName ();
				else
				{
					pushIndexedAssignmentOperator
						(getValueStack ().pop ().getName (), value);
					return true;
				}
			}
			pushAssignmentOperator (assignTo);
			assignTo = null;
			return true;
		}
		return false;
	}
	protected String assignTo = null;


	/**
	 * build indexed assignment operation
	 * @param variable name of variable being assigned
	 * @param index value of the index
	 */
	public void pushIndexedAssignmentOperator
	(String variable, ValueManager.GenericValue index)
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
		operatorLastSeen = false;
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
		ValueManager.GenericValue value = getValueStack ().pop ();
		ValueManager.setFormatter (value, getSpaceManager ());
		return generateSymbolStorage (op.getName (), value);
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
	 * evaluate the indicies to be used
	 * @param op the operator being processed
	 * @return the integer offset from start of array
	 */
	public int processIndicies (SymbolMap.IndexedVariableAssignment op)
	{
		T index = getValueManager ().toDiscrete (op.getIndexValues ());
		return getSpaceManager ().toNumber (index).intValue ();
	}


}

