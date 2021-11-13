
package net.myorb.math.expressions.gui.rpn;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.*;

import javax.swing.JOptionPane;

/**
 * collect digits of numbers as entered in preparation for stack push
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class Accumulator<T>
{


	/**
	 * notify listener of status change
	 */
	public interface AccumulatorStatusMonitor
	{
		/**
		 * method called to notify listener
		 * @param accumulatorIsActive TRUE = staus changed to active
		 */
		void updateAccumulatorStatus (boolean accumulatorIsActive);

		/**
		 * changes to accumulator value are sent to the monitor
		 * @param newValue the new value in the accumulator
		 */
		void updateAccumulatorValue (String newValue);
	}


	/**
	 * management objects supplied by environment
	 * @param environment the environment object containing active control objects
	 */
	public Accumulator (Environment<T> environment)
	{
		this.valueManager = environment.getValueManager ();
		this.valueStack = environment.getValueStack ();
		this.manager = environment.getSpaceManager ();
		this.accumulatedValue = new StringBuffer ();
	}
	protected StringBuffer accumulatedValue;
	protected ExpressionSpaceManager<T> manager;
	protected ValueManager<T> valueManager;
	protected ValueStack<T> valueStack;
	protected int radix;


	/**
	 * change the status of the accumulator
	 * @param newStatus the new status, TRUE = currently active
	 */
	public void setStatus (boolean newStatus)
	{
		if (newStatus)
		{
			if (currentlyActive) return;
			currentlyActive = true;
		}
		else
		{
			if (!currentlyActive) return;
			currentlyActive = false;
		}

		if (monitor != null)
		{
			monitor.updateAccumulatorStatus (currentlyActive);
		}
	}
	protected boolean currentlyActive = false;


	/**
	 * identify monitor of status changes
	 * @param monitor the monitor object
	 */
	public void setAccumulatorStatusMonitor
	(AccumulatorStatusMonitor monitor) { this.monitor = monitor; }
	protected AccumulatorStatusMonitor monitor = null;


	/**
	 * determine if a key stroke should impact the accumulated value
	 * @param item the name of the key stroke being processed
	 * @param radix the currently selected radix
	 * @return TRUE = action was taken
	 */
	public boolean isActionable (String item, String radix)
	{
		boolean processed = false;
		int position = ACTIONABLE.indexOf (item);

		if (position >= 0)
		{
			if (!currentlyActive)
			{
				this.radix = Integer.parseInt (radix);
			}

			processed = process (position);
			if (processed) return true;
		}

		pushAccumulator ();
		return processed;
	}


	/**
	 * push accumulated value to stack
	 */
	public void pushAccumulator ()
	{
		if (!currentlyActive) return;
		valueStack.push (getAccumulatorValue ());
		accumulatedValue = new StringBuffer ();
		monitor.updateAccumulatorValue ("");
		setStatus (false);
	}


	/**
	 * compute the managed representation for the accumulated value
	 * @return the generic 'T' representation for the accumulated value
	 */
	public T getAccumulatorValue ()
	{
		T result = manager.getZero ();
		String value = accumulatedValue.toString ();
		String characteristic = value, mantissa = null;
		int point = value.indexOf ('.');
		
		if (point >= 0)
		{
			characteristic = point == 0? null: value.substring (0, point);
			mantissa = value.substring (point + 1);
		}

		if (characteristic != null)
			result = manager.newScalar (Integer.parseInt (characteristic, radix));
		if (mantissa != null) result = manager.add (result, fraction (mantissa));
		return result;
	}


	/**
	 * convert the matissa portion of the accumulation
	 *  to the fractional part of the accumulation total
	 * @param digits the digits of the mantissa
	 * @return the fraction as T
	 */
	private T fraction (String digits)
	{
		T result = manager.getZero ();
		T multiplier = manager.invert (manager.newScalar (radix));
		T iterativeMultiplier = multiplier;
		
		for (int position = 0; position < digits.length(); position++)
		{
			T nextDigit = manager.multiply (iterativeMultiplier,
					manager.newScalar (ACTIONABLE.indexOf (digits.charAt (position))));
			iterativeMultiplier = manager.multiply (iterativeMultiplier, multiplier);
			result = manager.add (result, nextDigit);
		}

		return result;
	}


	/**
	 * having identified the key stroke as actionable,
	 *  process the key stroke implementing appropriate change
	 * @param index the index within the actionable string
	 * @return TRUE = action has been taken
	 */
	public boolean process (int index)
	{
		if (index < 18)
		{
			accumulatedValue.append (ACTIONABLE.charAt (index));
			monitor.updateAccumulatorValue (accumulatedValue.toString ());
			setStatus (true);
		}
		else
		{
//			System.out.println (index);

			switch (index)
			{
			case 37:				// PUSH
				pushAccumulator ();
				break;

			case 30:				// BS
				int len = accumulatedValue.length ();
				accumulatedValue.deleteCharAt (len - 1);
				monitor.updateAccumulatorValue (accumulatedValue.toString ());
				setStatus (len > 1);
				break;

			case 27:				// CE
				accumulatedValue = new StringBuffer ();
				monitor.updateAccumulatorValue ("");
				setStatus (false);
				break;

			case 22:				// *10^

				Object response;
				if ((response = getShiftCount ()) == null) return true;
				else processScientificNotationOperator (response);

			default: return false;	// any other items should be passed on

			}
		}

//		System.out.println (accumulatedValue);
//		valueStack.dump ();
		return true;
	}


	/*
	 * scientific notation implementation
	 */


	/**
	 * process user response to shift count request
	 * @param dialogResponse the object returned from the dialog
	 */
	private void processScientificNotationOperator (Object dialogResponse)
	{
		int shiftCount = Integer.parseInt (dialogResponse.toString ());
		pushAccumulator (); pushShiftParameters (shiftCount);
	}


	/**
	 * push the parameters to the *10^ operator
	 * @param shiftCount the number of decimal places to be moved
	 */
	private void pushShiftParameters (int shiftCount)
	{
		valueStack.push (valueManager.newValueList (getShiftParameters (shiftCount)));
	}


	/**
	 * construct the parameter list to the *10^ operator
	 * @param shiftCount the number of decimal places to be moved
	 * @return the list of generic values
	 */
	private ValueManager.GenericValueList getShiftParameters (int shiftCount)
	{
		ValueManager.GenericValueList values = new ValueManager.GenericValueList ();
		values.add (valueManager.newDiscreteValue (manager.newScalar (shiftCount)));
		values.add (valueManager.newDiscreteValue (manager.newScalar (radix)));
		values.add (valueStack.pop ());
		return values;
	}


	/**
	 * open dialog with user to aquire shift count to be used
	 * @return null => cancel operation, otherwise count value
	 */
	private Object getShiftCount ()
	{
		Object shiftCount = JOptionPane.showInputDialog
		(
			null, "Specify Decimal Point Shift Count", "Scientific Notation Exponent",
			JOptionPane.PLAIN_MESSAGE, null, null, ""
		);
		return shiftCount;
	}


	/*
	 * keys that are recognized in the accumulator
	 */

	public static
	final String ACTIONABLE =
	"0123456789ABCDEF. * ^ *10^ CE KEY$BS KEY$ENTER";
	
}
