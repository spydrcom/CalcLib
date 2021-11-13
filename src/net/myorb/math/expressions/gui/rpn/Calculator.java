
package net.myorb.math.expressions.gui.rpn;

import net.myorb.math.expressions.*;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.symbols.AbstractFunction;
import net.myorb.math.expressions.gui.*;

import net.myorb.gui.components.*;

import java.util.HashMap;
import java.util.Map;

/**
 * main driver of Reverse Polish Notation calculator
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class Calculator<T> extends Environment<T>
	implements MultiPanel.RequestProcessor, Runnable
{


	/**
	 * uses a CALCLIB environment object
	 * @param original the environment from a CALCLIB session
	 */
	public Calculator (Environment<T> original)
	{
		super (original);
		accumulator = new Accumulator<T> (this);
		keyActionMap = new HashMap<String,Runnable>();
		guiSymbolMap = original.getControl ().getGuiMap ();
	}
	protected Map<String,Object> calculatorComponentMap;
	protected Map<String,Runnable> keyActionMap;
	protected Map<String,Object> guiSymbolMap;
	protected Accumulator<T> accumulator;


	/**
	 * component map collects GUI objects as they are built
	 */
	public void buildCalculatorComponentMap ()
	{
		calculatorComponentMap = new HashMap<String,Object> ();
		calculatorComponentMap.put ("valueStack", getValueStack ());
		calculatorComponentMap.put ("guiSymbolMap", guiSymbolMap);
		calculatorComponentMap.put ("keyActionMap", keyActionMap);
		calculatorComponentMap.put ("ACCUMULATOR", accumulator);
		calculatorComponentMap.put ("calculator", this);
		calculatorComponentMap.put ("REQUEST", this);
	}


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run ()
	{
		buildCalculatorComponentMap ();
		new DisplayFrame (MultiPanel.getPanel (calculatorComponentMap), "RPN").show ();
		stackDisplay = (TextLineDisplay) calculatorComponentMap.get ("StackDisplay");
	}
	TextLineDisplay stackDisplay;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rpn.ButtonManager.OperationRequest#perform(java.lang.String)
	 */
	public void perform (String operation)
	{
		//System.out.println (operation + " - " + calculatorComponentMap.get ("RADIX"));

		if (accumulator.isActionable (operation, calculatorComponentMap.get ("RADIX").toString ()))
		{
			refreshTosDisplay ();
			return;
		}

		if (keyActionMap.containsKey (operation))
		{
			keyActionMap.get (operation).run ();
			refreshTosDisplay ();
			return;
		}

		processSymbol (operation);
		refreshTosDisplay ();
	}


	/**
	 * utilize functionality associated with named symbol
	 * @param name the name of the symbol being utilized
	 */
	public void processSymbol (String name)
	{
		SymbolMap.Named symbol = getSymbolMap().lookup (name);

		if (symbol == null)
		{
			System.out.println ("Symbol not found: " + name);
			return;
		}

		if (symbol instanceof SymbolMap.Operation)
		{
			SymbolMap.Operation op = (SymbolMap.Operation)symbol;
			execute (op);
		}
		else if (symbol instanceof SymbolMap.VariableLookup)
		{
			pushVariableValue (getSymbolMap ().getValue (symbol));
		}
		else throw new RuntimeException ("Symbol type not recognized");
	}


	/**
	 * processing required for multi-parameter functions
	 * @param symbol the name of the function
	 */
	public void adjustStackForFunctionCall (String symbol)
	{
		ValueStack<T> valueStack = getValueStack ();
		int count = AbstractFunction.cast (lookup (symbol)).parameterCount ();
		ValueManager.GenericValueList parameters = new ValueManager.GenericValueList ();
		for (int i = 0; i < count; i++) parameters.add (0, valueStack.pop ());
		valueStack.push (getValueManager ().newValueList (parameters));
	}


	/**
	 * display current value of top of stack
	 */
	public void refreshTosDisplay ()
	{
		stackDisplay.fill (getValueStack ().top (stackDisplay.getSize ()));
	}


}

