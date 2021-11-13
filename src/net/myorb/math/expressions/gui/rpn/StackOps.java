
package net.myorb.math.expressions.gui.rpn;

import net.myorb.math.expressions.ValueStack;

import javax.swing.AbstractButton;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.GridLayout;

import java.util.Map;

/**
 * a panel of buttons requesting stack operations
 * @author Michael Druckman
 */
public class StackOps extends ButtonManager
{

	/**
	 * build and return the panel
	 * @param map the map of GUI objects in the system
	 * @return the new panel
	 */
	public static JPanel getPanel (Map<String,Object> map)
	{
		JPanel panel = new JPanel ();
		panel.setLayout (new GridLayout(2,4));

		AbstractButton
		clear = newButton (new CommonButton (map, "Clear", "Pop"), "Clear top entry"),
		push = newButton (new CommonButton (map, "Push", "KEY$ENTER"), "Push value on stack"),
		clearAll = newButton (new CommonButton (map, "ALL"), "Clear all stack items"),
		xch = newButton (new CommonButton (map, "Xch"), "Exchange top two entries"),
		flip = newButton (new CommonButton (map, "Flip"), "Flip bottom to top"),
		save = newButton (new CommonButton (map, "Save"), "Save stack to file"),
		rest = newButton (new CommonButton (map, "Restore"), "Restore stack from file"),
		pop = newButton (new CommonButton (map, "Pop"), "Pop value from stack");

		clear.setEnabled (false); clearAll.setEnabled (false); xch.setEnabled (false);
		flip.setEnabled (false); save.setEnabled (false); pop.setEnabled (false);
		rest.setEnabled (false); xch.setEnabled (false);

		panel.add (clear);     panel.add (save);  panel.add (xch);   panel.add (push);
		panel.add (clearAll);  panel.add (rest);  panel.add (flip);  panel.add (pop);

		panel.setPreferredSize (new Dimension (300, 100));
		buildActionMap (map);
		return panel;
	}

	/**
	 * map action names to implementations of those actions
	 * @param calculatorComponentMap  map of components of the application
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void buildActionMap (Map<String,Object> calculatorComponentMap)
	{
		Map<String,Runnable> keyActionMap =
			(Map<String,Runnable>) calculatorComponentMap.get ("keyActionMap");
		ValueStack valueStack = (ValueStack) calculatorComponentMap.get ("valueStack");
		valueStack.addStackListener (new StackMonitor (calculatorComponentMap));

		keyActionMap.put ("ALL", new CleanStack (valueStack));
		keyActionMap.put ("Xch", new XchStack (valueStack));
		keyActionMap.put ("Pop", new PopStack (valueStack));
	}

}

/**
 * monitor the stack to allow function only when available
 */
class StackMonitor implements ValueStack.StackStatusMonitor
{
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ValueStack.StackStatusMonitor#stackSizeIs(int)
	 */
	public void stackSizeIs (int entries)
	{
		all.setEnabled (entries > 0);
		pop.setEnabled (entries > 0);
		xch.setEnabled (entries > 1);
	}
	StackMonitor (Map<String,Object> guiMap)
	{
		all   = (AbstractButton) guiMap.get ("ALL");	// all
		xch   = (AbstractButton) guiMap.get ("Xch");	// xch
		pop   = (AbstractButton) guiMap.get ("Pop");	// pop
	}
	AbstractButton all, xch, pop;
}

/**
 * empty stack of all items
 */
class CleanStack implements Runnable
{
	public void run ()
	{
		stack.clear ();
	}
	@SuppressWarnings("rawtypes")
	CleanStack (ValueStack stack)
	{
		this.stack = stack;
	}
	@SuppressWarnings("rawtypes")
	ValueStack stack;
}

/**
* exchange top two items on stack
*/
class XchStack implements Runnable
{
	public void run ()
	{
		stack.exchangeTop ();
	}
	@SuppressWarnings("rawtypes")
	XchStack (ValueStack stack)
	{
		this.stack = stack;
	}
	@SuppressWarnings("rawtypes")
	ValueStack stack;
}

/**
* pop top entry off stack
*/
class PopStack implements Runnable
{
	public void run ()
	{
		stack.pop ();
	}
	@SuppressWarnings("rawtypes")
	PopStack (ValueStack stack)
	{
		this.stack = stack;
	}
	@SuppressWarnings("rawtypes")
	ValueStack stack;
}

