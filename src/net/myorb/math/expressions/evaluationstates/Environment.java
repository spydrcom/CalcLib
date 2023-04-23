
package net.myorb.math.expressions.evaluationstates;

import net.myorb.math.expressions.algorithms.CyclicAndPowerLibrary;
import net.myorb.gui.editor.SnipToolPropertyAccess;

import net.myorb.math.expressions.*;
import net.myorb.math.*;

/**
 * processing methods for states of the state machine
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class Environment<T> extends OperatorProcessing<T>
{


	/**
	 * access to the application core is required
	 * @param <T> data type for the access needs
	 */
	public interface AccessAcceptance <T>
	{
		/**
		 * provide access to implementing object
		 * @param environment access to the core application structures
		 */
		void setEnvironment (Environment<T> environment);
	}

	/**
	 * provide access to objects marked as in need
	 * @param object an object that may require access
	 * @param environment access to the core application structures
	 * @param <S> data type for the access needs
	 */
	@SuppressWarnings("unchecked")
	public static <S> void provideAccess (Object object, Environment<S> environment)
	{ if (object instanceof AccessAcceptance) ((AccessAcceptance<S>) object).setEnvironment (environment); }
	public static <S> void provideAccess (Object object, ExpressionSpaceManager<S> manager)
	{ provideAccess (object, manager.getEvaluationControl ().getEngine ().getEnvironment ()); }

	/**
	 * use THIS environment to provide access
	 * @param object the object that needs access
	 */
	public void provideAccessTo (Object object)
	{ provideAccess (object, this); }


	/**
	 * construct the environment for expression evaluation
	 * @param symbols the table of symbols available for use in evaluation
	 * @param spaceManager the type manager for computation atomics
	 */
	public Environment (SymbolMap symbols, ExpressionSpaceManager<T> spaceManager)
	{
		this.setDataSources (symbols, spaceManager);
	}
	public Environment (Environment<T> toBeForked)
	{ this (toBeForked.getSymbolMap (), toBeForked.getSpaceManager ()); connectFrom (toBeForked); }
	public Environment (ExpressionSpaceManager<T> spaceManager)
	{ this (new SymbolMap (), spaceManager); }

	/**
	 * construct environment with specific library
	 * @param spaceManager the type manager for computation atomics
	 * @param powerLibrary the library object to be used
	 */
	public Environment
	(ExpressionSpaceManager<T> spaceManager, ExtendedPowerLibrary<T> powerLibrary) { this (spaceManager); setLibrary (powerLibrary); }
	public Environment (SymbolMap symbols, ExpressionSpaceManager<T> spaceManager, ExtendedPowerLibrary<T> powerLibrary)
	{ this (symbols, spaceManager); setLibrary (powerLibrary); }


	/**
	 * establish a math library to be used
	 * @param lib library to be used
	 */
	public void setCyclicAndPowerLibrary (CyclicAndPowerLibrary<T> lib) { this.cyclicAndPowerLibrary = lib; }
	public CyclicAndPowerLibrary<T> getCyclicAndPowerLibrary () { return cyclicAndPowerLibrary; }
	protected CyclicAndPowerLibrary<T> cyclicAndPowerLibrary;


	/**
	 * retain snip tool properties for configuration of editors
	 * @param snipProperties  properties to use for snip tool
	 */
	public void setSnipProperties
	(SnipToolPropertyAccess snipProperties) { this.snipProperties = snipProperties; }
	public SnipToolPropertyAccess getSnipProperties () { return snipProperties; }
	protected SnipToolPropertyAccess snipProperties;


	/*
	 * interface between computation layer and control layer
	 */

	/**
	 * get the control object for this engine
	 * @return the control object
	 */
	public EvaluationControlI<T> getControl () { return control; }
	public void connectFrom (Environment<T> e) { this.control = e.getControl (); }
	public void connectControl (EvaluationControlI<T> control) { this.control = control; }
	protected EvaluationControlI<T> control;


	/**
	 * process an identifier token
	 */
	public void processIdentifier ()
	{
		if (assignmentIsPending ())
		{
			processPendingAssignment ();
		}
		else
		{
			SymbolMap.Named symbol = lookupImage ();
			if (symbol instanceof SymbolMap.Operation)
			{
				setTokenType (TokenParser.TokenType.OPR);
			}
			else if (symbol instanceof SymbolMap.VariableLookup)
			{
				pushVariableValue (getSymbolMap ().getValue (symbol));
			}
			else throw new SymbolMap.Unrecognized ();
		}
	}


	/**
	 * process group operations
	 * @param opPrec the precedence of the current operation
	 * @return TRUE = group operator found and processed
	 */
	public boolean groupItemProcessed (int opPrec)
	{
		switch (opPrec)
		{

			case SymbolMap.CONTINUE_GROUP_PRECEDENCE:

				setOperatorStatus (true);
				getValueStack ().continueArray ();
				return true;

			case SymbolMap.CLOSE_GROUP_PRECEDENCE:

				getValueStack ().closeArray ();
				setOperatorStatus (false);
				popOpStackToTos ();
				return true;

			default: return false;

		}
	}


	/**
	 * process array operations
	 * @param opPrec the precedence of the current operation
	 * @return TRUE = array operator found and processed
	 */
	public boolean arrayItemProcessed (int opPrec)
	{
		if (opPrec == SymbolMap.OPEN_ARRAY_PRECEDENCE)
		{
			arrayProcessingOpened = true;
			return true;
		}
		return false;
	}
	public boolean arrayProcessingIsOpen ()
	{ boolean result = arrayProcessingOpened; arrayProcessingOpened = false; return result; }
	protected boolean arrayProcessingOpened = false;


	/**
	 * process an operator token
	 */
	public void processOperator ()
	{
		SymbolMap.Operation op = (SymbolMap.Operation) lookupImage ();
		
		if (op instanceof SymbolMap.UnaryPostfixOperator)
		{
			execute (op);
			setOperatorStatus (false);
			dumpValueStack ();
		}
		else
		{
			int opPrec = op.getPrecedence ();
			if (arrayItemProcessed (opPrec)) return;
			if (assignmentProcessed (opPrec)) return;
			processBinaryOpSpecialCase (op, opPrec);		// turn 0 - x into -x

			if (opPrec == SymbolMap.OPEN_GROUP_PRECEDENCE)
			{
				getValueStack ().openArray ();
			}
			else
			{
				flushByPrecedence (opPrec);
			}

			if (groupItemProcessed (opPrec)) return;
			pushOpStack (op);
		}
	}


	/**
	 * while TOS operator has higher precedence pop the OP stack
	 * @param opPrec the precedence of the latest operation
	 */
	public void flushByPrecedence (int opPrec)
	{
		int tosPrec = getTosPrecedence ();
		while (tosPrec >= opPrec)
		{
			execute (popOpStackToTos ());
			tosPrec = getTosPrecedence ();
			dumpValueStack ();
		}
	}


	/**
	 * completely flush the operator stack
	 */
	public void flush ()
	{
		flushByPrecedence (SymbolMap.STACK_COMPLETION_PRECEDENCE + 1);
	}


	/**
	 * treat a token stream as a multi-parameter function
	 * @param parameterName the name of the function parameters
	 * @param functionTokens the token stream that provides the function logic
	 * @return the implementation of the MultiDimensional.Function interface
	 */
	public ExpressionMacro<T> constructMacro
		(String parameterName, TokenParser.TokenSequence functionTokens)
	{
		ExpressionMacro<T> macro =
			new ExpressionMacro<T> (parameterName, functionTokens);
		processSubroutine (macro);
		return macro;
	}


	/**
	 * simplify the interface to a MultiDimensional function
	 * @param function the MultiDimensional to treat as a simple unary function
	 * @return the implementation as F(x)
	 */
	public Function<T> reducedFunction (MultiDimensional.Function<T> function)
	{
		return new SimplificationWrapper<T> (function);
	}


	/**
	 * initialize state variables for new run
	 */
	public void init ()
	{
		initTos ();
		setTraceStatus ();
		initAssignmentProcessing ();
		clearStacks ();
	}


}


/**
 * treat MultiDimensional.Function as simple unary function
 * @param <T> type on which operations are to be executed
 */
class SimplificationWrapper<T> implements Function<T>
{

	/* (non-Javadoc)
	 * @see net.myorb.math.MultiDimensional.Function#f(T[])
	 */
	@SuppressWarnings("unchecked") public T eval (T x)
	{
		return multiDimensional.f (x);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.MultiDimensional.Function#getSpaceManager()
	 */
	public SpaceManager<T> getSpaceDescription ()
	{
		return (SpaceManager<T>) multiDimensional.getSpaceDescription ();
	}
	public SpaceManager<T> getSpaceManager ()
	{
		return (SpaceManager<T>) multiDimensional.getSpaceDescription ();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		return multiDimensional.toString ();
	}

	MultiDimensional.Function<T> multiDimensional;
	public SimplificationWrapper (MultiDimensional.Function<T> multiDimensional)
	{ this.multiDimensional = multiDimensional; }

}


