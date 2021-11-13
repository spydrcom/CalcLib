
package net.myorb.math.expressions.gui.rpn;

import net.myorb.math.expressions.*;
import net.myorb.gui.components.DisplayTable;
import net.myorb.gui.components.Alerts;

import javax.swing.AbstractButton;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;

import java.awt.Dimension;
import java.awt.GridLayout;

import java.util.Map;

/**
 * describe operations that allow RPN to interace
 *  with user defined symbols/variables and functions
 * @author Michael Druckman
 */
public class UserDefined extends ButtonManager
{


	/**
	 * build and return the panel holing the user def toolbar
	 * @param map the map of GUI components of the RPN calculator
	 * @return generated panel
	 */
	public static JPanel getPanel (Map<String,Object> map)
	{
		JPanel panel = new JPanel ();
		panel.setLayout (new GridLayout(4,2));

		AbstractButton
		saveNew = newButton (new CommonButton (map, "Save New"), "Save value to new user symbol"),
		add = newButton (new CommonButton (map, "Add Into"), "Add value into selected user symbol"),
		sym = newButton (new CommonButton (map, "Read Symbol"), "Get value from selected user symbol"),
		saveExisting = newButton (new CommonButton (map, "Save To"), "Save value to existing user symbol"),
		mul = newButton (new CommonButton (map, "Multiply Into"), "Multiply value into selected user symbol"),
		def = newButton (new CommonButton (map, "Define Function"), "Define new user function from coded expression"),
		addFrom = newButton (new CommonButton (map, "Add From"), "Add into value from selected user symbol"),
		fun = newButton (new CommonButton (map, "Use Function"), "Use selected user function");

		mul.setEnabled (false); def.setEnabled (false); addFrom.setEnabled (false); fun.setEnabled (false);
		saveNew.setEnabled (false); add.setEnabled (false); saveExisting.setEnabled (false);

		panel.add (addFrom); panel.add (sym);
		panel.add (saveNew); panel.add (saveExisting);
		panel.add (add);     panel.add (mul);
		panel.add (def);     panel.add (fun);

		panel.setPreferredSize (new Dimension (300, 100));
		buildActionMap (map);
		return panel;
	}


	/**
	 * map action names to implementations of those actions
	 * @param calculatorComponentMap map of components of the application
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void buildActionMap (Map<String,Object> calculatorComponentMap)
	{
		Map<String,Runnable> keyActionMap =
			(Map<String,Runnable>) calculatorComponentMap.get ("keyActionMap");
		ValueStack valueStack = (ValueStack) calculatorComponentMap.get ("valueStack");
		valueStack.addStackListener (new UserDefMonitor (calculatorComponentMap));

		keyActionMap.put ("Read Symbol", new SymbolReader (calculatorComponentMap));
		keyActionMap.put ("Use Function", new FunctionFinder (calculatorComponentMap));

		keyActionMap.put ("Save New", new SaveNew (calculatorComponentMap));
		keyActionMap.put ("Save To", new SaveTo (calculatorComponentMap));
	}


}


/**
 * monitor the stack to allow function only when available
 */
class UserDefMonitor implements ValueStack.StackStatusMonitor
{
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ValueStack.StackStatusMonitor#stackSizeIs(int)
	 */
	public void stackSizeIs (int entries)
	{
		fun.setEnabled (entries > 0);
		sto.setEnabled (entries > 0);
		sav.setEnabled (entries > 0);
	}
	UserDefMonitor (Map<String,Object> guiMap)
	{
		fun   = (AbstractButton) guiMap.get ("Use Function");	// functions
		sav   = (AbstractButton) guiMap.get ("Save New");		// Save New
		sto   = (AbstractButton) guiMap.get ("Save To");		// Save To
	}
	AbstractButton fun, sav, sto;
}


/**
 * support for symbol based operations
 */
class SymbolBased
{

	/**
	 * find GUI table from name
	 * @param tableName the name to lookup
	 * @return the table object, or NULL for error
	 */
	JTable getTable (String tableName)
	{
		JScrollPane pane = (JScrollPane)guiSymbolMap.get (tableName);
		if (pane != null) return DisplayTable.getTableInScroll (pane);
		else return null;
	}
	
	/**
	 * get the selected line of the table
	 * @param t the table object to be queried
	 * @param col the column of the table holding the value
	 * @param required TRUE => error for lack of selection
	 * @return the value from the table entry
	 */
	String getValueAt (JTable t, int col, boolean required)
	{
		int row = t==null?
				-1: t.getSelectedRow ();
		if (row < 0) if (!required) return "";
		else { Alerts.warn (null, "Table selection required"); return null; }
		return t.getValueAt (row, col).toString ();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SymbolBased (Map<String,Object> calculatorComponentMap)
	{
		this.guiSymbolMap = (Map<String,Object>) calculatorComponentMap.get ("guiSymbolMap");
		this.c = (Calculator) calculatorComponentMap.get ("calculator");
	}
	Map<String,Object> guiSymbolMap;
	@SuppressWarnings("rawtypes")
	Calculator c;

}


/**
 * process the selected symbol
 */
class SymbolReader extends SymbolBased implements Runnable
{
	public SymbolReader(Map<String,Object> calculatorComponentMap)
	{
		super (calculatorComponentMap);
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run ()
	{
		String symbol = getValueAt (getTable ("SymbolTable"), 0, true);
		if (symbol == null) return;

		//System.out.println ("user sym - " + symbol);
		c.processSymbol (symbol);
	}
}


/**
 * execute selected function
 */
class FunctionFinder extends SymbolBased implements Runnable
{
	public FunctionFinder(Map<String,Object> calculatorComponentMap)
	{
		super (calculatorComponentMap);
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run ()
	{
		String symbol = getValueAt (getTable ("FunctionTable"), 0, true);
		if (symbol == null) return;

		c.adjustStackForFunctionCall (symbol);

		//System.out.println ("user function - " + symbol);
		c.processSymbol (symbol);
	}
}


/**
 * save top of stack value into selected symbol
 */
class SaveTo extends SymbolBased implements Runnable
{
	public SaveTo (Map<String,Object> calculatorComponentMap)
	{
		super (calculatorComponentMap);
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run ()
	{
		String symbol = getValueAt (getTable ("SymbolTable"), 0, true);
		if (symbol != null) c.setSymbol (symbol, c.getValueStack ().pop ());
	}
}


/**
 * save top of stack value into new symbol
 */
class SaveNew extends SymbolBased implements Runnable
{
	public SaveNew (Map<String,Object> calculatorComponentMap)
	{
		super (calculatorComponentMap);
	}

	/**
	 * request symbol name from user
	 * @return the name, or NULL for cancel
	 */
	private Object getSymbolName ()
	{
		Object name = JOptionPane.showInputDialog
		(
			null, "Specify name of symbol", "Save to new symbol",
			JOptionPane.PLAIN_MESSAGE, null, null, ""
		);
		return name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run ()
	{
		Object symbol = getSymbolName ();
		if (symbol != null) c.setSymbol (symbol.toString (), c.getValueStack ().pop ());
	}
}


