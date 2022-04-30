
package net.myorb.math.expressions;

import net.myorb.data.abstractions.ErrorHandling;

import java.util.ArrayList;
import java.util.List;

/**
 * implementation of stack operations on expression intermediate values
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class ValueStack<T>
{

	public static class StackCycle extends ErrorHandling.Terminator
	{
		public StackCycle (String message) { super (message); }
		private static final long serialVersionUID = 3665018210579636331L;
	}

	public static class StackUnderflow extends StackCycle
	{
		public StackUnderflow () { super ("Stack underflow"); }
		private static final long serialVersionUID = 6211686482909558457L;
	}

	public static class StackOverflow extends StackCycle
	{
		public StackOverflow () { super ("Not all values used in calculation"); }
		private static final long serialVersionUID = 3414577202824036948L;
	}

	public static class ContextError extends ErrorHandling.Terminator
	{
		public ContextError (Exception exception) { super (CONTEXT_ERROR, exception); }
		static final String CONTEXT_ERROR = "Value list found to contain error";
		private static final long serialVersionUID = -534760667363238945L;
	}


	public interface StackStatusMonitor
	{
		/**
		 * monitors will be notified when stack size changes
		 * @param entries size of stack
		 */
		void stackSizeIs (int entries);
	}

	/**
	 * constructor builds helper objects
	 */
	public ValueStack ()
	{
		valueManager = new ValueManager<T>();
		stack = new ArrayList<ValueManager.GenericValue> ();
		stackMonitors = new ArrayList<StackStatusMonitor> ();
	}
	List<StackStatusMonitor> stackMonitors;
	List<ValueManager.GenericValue> stack;
	ValueManager<T> valueManager;


	/**
	 * new monitor is added to list of listeners
	 * @param monitor the new monitor to add
	 */
	public void addStackListener (StackStatusMonitor monitor)
	{
		stackMonitors.add (monitor);
	}


	/**
	 * make call to each monitor
	 */
	public void notifyMonitors ()
	{
		if (stackMonitors.size() > 0)
		{
			int size = stack.size ();
			for (StackStatusMonitor m : stackMonitors)
			{ m.stackSizeIs (size); }
		}
	}


	/**
	 * push a generic value onto stack
	 * @param value the descriptor of the value
	 */
	public void push (ValueManager.GenericValue value)
	{
		stack.add (value);
		notifyMonitors ();
	}
	public void push (ValueManager.GenericValue value, ValueManager.Metadata metadata)
	{
		value.setMetadata (metadata);
		push (value);
	}


	/**
	 * push a discrete value onto stack
	 * @param value discrete to be pushed
	 */
	public void push (T value)
	{
		push (valueManager.newDiscreteValue (value));
	}
	public void push (T value, ValueManager.Metadata metadata)
	{
		ValueManager.GenericValue discrete =
			valueManager.newDiscreteValue (value);
		push (discrete, metadata);
	}


	/**
	 * push array onto stack
	 * @param values array to be pushed
	 */
	public void push (ValueManager.RawValueList<T> values)
	{
		push (valueManager.newDimensionedValue (values));
	}
	public void push (ValueManager.RawValueList<T> values, ValueManager.Metadata metadata)
	{
		ValueManager.GenericValue dimensioned =
			valueManager.newDimensionedValue (values);
		push (dimensioned, metadata);
	}


	/**
	 * check for elements remaining
	 * @return TRUE = empty
	 */
	public boolean isEmpty ()
	{
		return stack.size () == 0;
	}


	/**
	 * check for stack underflow
	 * @throws StackUnderflow for stack empty on POP request
	 */
	public void checkSize () throws StackUnderflow
	{
		if (stack.isEmpty ()) throw new StackUnderflow ();
	}


	/**
	 * get a value descriptor from top of stack
	 * @return descriptor from top of stack
	 */
	public ValueManager.GenericValue pop ()
	{
		checkSize ();
		ValueManager.GenericValue entry = stack.remove (stack.size () - 1);
		notifyMonitors ();
		return entry;
	}


	/**
	 * get an array from top of stack
	 * @return array popped from top of stack
	 */
	public List<T> popArray ()
	{
		ValueManager.GenericValue top = pop ();
		if (valueManager.isSimpleDimensionedValue (top))
		{ return valueManager.toDiscreteValues (top); }

		T value = valueManager.toDiscreteValue (top).getValue ();
		List<T> array = new ArrayList<T>();
		array.add (value);
		return array;
	}


	/**
	 * get a discrete value from top of stack
	 * @return value popped from top of stack
	 */
	public T popValue ()
	{
		ValueManager.GenericValue top;
		if (valueManager.isDiscrete (top = pop ()))
		{ return valueManager.toDiscreteValue (top).getValue (); }
		return valueManager.toDiscreteValues (top).get (0);
	}


	/**
	 * peek at top of stack without pop
	 * @return the value descriptor at top of stack
	 */
	public ValueManager.GenericValue peek ()
	{
		if (stack.size () == 0) return null;
		return stack.get (stack.size () - 1);
	}


	/**
	 * get top entries of stack
	 * @param n the maximum number of entries
	 * @return top entries found
	 */
	public List<ValueManager.GenericValue> top (int n)
	{
		if (stack.size () == 0) return null;
		List<ValueManager.GenericValue> items = new ArrayList<ValueManager.GenericValue> ();
		for (int next = stack.size () - 1, remaining = n; next >= 0; next--)
		{
			items.add (stack.get (next));
			if (--remaining == 0) break;
		}
		return items;
	}


	/**
	 * open parenthesis processing
	 */
	public void openArray ()
	{
		push (valueManager.newValueList ());
	}


	/**
	 * comma processing
	 */
	public void continueArray ()
	{
		ValueManager.GenericValue top = pop ();
		ValueManager.GenericValue list = peek ();
		if (valueManager.isSimpleDimensionedValue (list))
		{
			valueManager.toDimensionedValue (list).getValues ().add
					(valueManager.toDiscrete (top));
		}
		if (list == null) return;
		if (!valueManager.isParameterList (list))
		{ throw new ValueManager.Expected ("Value list for array"); }
		valueManager.valuesOfList (list).add (top);
	}


	/**
	 * closing parenthesis processing
	 */
	public void closeArray ()
	{
		ValueManager.GenericValue top = peek ();
		if (!valueManager.isParameterList (top)) { continueArray (); }

		List<ValueManager.GenericValue> array = valueManager.toList (peek ());
		//if (array.size () == 1) { pop (); push (array.get (0)); }

		ValueManager.Metadata m = null;
		ValueManager.RawValueList<T> tlist = new ValueManager.RawValueList<T> ();
		for (ValueManager.GenericValue v : array)
		{
			if (!(valueManager.isDiscrete (v))) return;
			tlist.add (valueManager.toDiscrete (v));
			m = v.getMetadata ();
		}
		
		pop ();
		push (tlist, m);
	}


	/**
	 * exchange top two entries of stack
	 */
	public void exchangeTop ()
	{
		ValueManager.GenericValue top = pop ();
		ValueManager.GenericValue next = pop ();
		push (top); push (next);
	}


	/**
	 * clear the value stack
	 */
	public void clear ()
	{
		stack.clear ();
		notifyMonitors ();
	}


	/**
	 * process errors in values
	 * @param exception the exception caught as result
	 */
	public void recognizeContextError
		(Exception exception)
	throws ContextError
	{
		stack.clear ();
		throw new ContextError (exception);		
	}


	/**
	 * dump stack contents to system output
	 */
	public void dump ()
	{
		System.out.println (stack);
	}


}

