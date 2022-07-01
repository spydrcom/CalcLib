
package net.myorb.math.expressions.gui;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.symbols.NamedObject;
import net.myorb.math.expressions.gui.DisplayIO.CommandProcessor;
import net.myorb.math.primenumbers.sieves.SieveOfEratosthenes;
import net.myorb.math.primenumbers.ReportGenerators;
import net.myorb.math.primenumbers.Factorization;

import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.gui.components.MenuManager;
import net.myorb.gui.components.Alerts;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;

import java.util.Map;

/**
 * primitive support for tool-bar components
 * @author Michael Druckman
 */
public class ToolBarPrimitives
{

	ToolBarPrimitives (String command, DisplayIO.CommandProcessor processor, Component parent)
	{ this.processor = processor; this.command = command; this.commandName = command; this.parent = parent; }
	protected DisplayIO.CommandProcessor processor;
	protected String command, commandName;

	/**
	 * change text of command to be issued
	 * @param command the text of the command to use
	 */
	public void setCommandText (String command) { this.command = command + " "; }

	/**
	 * request text response from user
	 * @param request text of a request message
	 * @param title title for the request frame display
	 * @param defaultValue optional default value for response
	 * @return text from user of NULL for CANCEL
	 */
	Object getSimpleTextResponse (String request, String title, String defaultValue)
	{
		return JOptionPane.showInputDialog
		(
			parent, request, title, 
			JOptionPane.PLAIN_MESSAGE,
			null, null, defaultValue
		);
	}
	Object getSimpleTextResponse (String request, String title)
	{ return getSimpleTextResponse (request, title, ""); }
	protected Component parent;

	/**
	 * request selection response from user
	 * @param request text of a request message
	 * @param title title for the request frame display
	 * @param choices text list of choices to present in dialog
	 * @param defaultValue optional default value for response
	 * @return selected text from user of NULL for CANCEL
	 */
	Object chooseFromList (String request, String title, Object[] choices, String defaultValue)
	{
		return JOptionPane.showInputDialog
		(
			parent, request, title,
			JOptionPane.PLAIN_MESSAGE, null,
			choices, defaultValue
		);
	}
	Object chooseFromList (String request, String title, Object[] choices)
	{ return chooseFromList (request, title, choices, ""); }

	/**
	 * show message and throw exception
	 * @param message text of the message to display
	 */
	public void terminate (String message)
	{
		Alerts.warn (parent, message);
		throw new RuntimeException (message);
	}

	/**
	 * chech for NULL input
	 * @param input a source object 
	 */
	Object checkForCancel (Object input)
	{
		if (input == null)
			terminate ("Request Canceled");
		return input;
	}

	/**
	 * refresh display contents
	 */
	void refresh ()
	{
		if (processor == null) return;
		DisplayEnvironment.showEnvironment (processor.getMap ());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return commandName; }

}


/**
 * simplist form of menu item, text of command is sent to command processor
 */
class SimpleCommand extends ToolBarPrimitives implements ActionListener
{
	SimpleCommand (String command, DisplayIO.CommandProcessor processor) { super (command, processor, null); }
	SimpleCommand (String command, DisplayIO.CommandProcessor processor, Component parent) { super (command, processor, parent); }
	public void actionPerformed(ActionEvent e) { processor.execute (command); refresh (); }
}


/**
 * simplist for of command that presents request to user
 */
class DialogCommand extends SimpleCommand
{

	DialogCommand
		(
			String command, String commandName, String request, String title,
			DisplayIO.CommandProcessor processor, Component parent
		)
	{ this (command, request, title, processor, parent); this.commandName = commandName; }
	DialogCommand (String command, String request, String title, DisplayIO.CommandProcessor processor, Component parent)
	{
		super (command, processor, parent);
		this.request = request;
		this.title = title;
	}

	/**
	 * get simple input text
	 * @return text entered by user or NULL for cancel
	 */
	Object getResponse ()
	{
		return getSimpleTextResponse (request, title);
	}
	String request;
	String title;

	/**
	 * command + dialog parameter
	 */
	void simpleDialog ()
	{
		Object response;
		checkForCancel (response = getResponse ());
		processor.execute (command + response);
		refresh ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SimpleCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) { simpleDialog (); }

}


/**
 * add symbol or function with simple user dialog GUI
 */
class AddSymbol extends DialogCommand implements MenuManager.MnemonicAvailable
{
	AddSymbol (DisplayIO.CommandProcessor processor, Component parent)
	{ super ("", "Add", "Declare Symbol", "Add New Symbol", processor, parent); }
	public char getMnemonic () { return 'A'; }
}
class AddFunction extends DialogCommand implements MenuManager.MnemonicAvailable
{
	AddFunction (DisplayIO.CommandProcessor processor, Component parent)
	{ super ("!! ", "Add", "Declare Function", "Add New Function", processor, parent); }
	public char getMnemonic () { return 'A'; }
}


/**
 * initialize primes table
 */
class SieveCommand extends DialogCommand
{

	static final String PRIME_REPORT = "PrimeReport";

	public SieveCommand(String command, String request, String title, CommandProcessor processor, Component parent)
	{
		super(command, request, title, processor, parent);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.DialogCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		Object response;
		ReportGenerators rpt;
		checkForCancel (response = getResponse ());
		Factorization.setImplementation (rpt = new ReportGenerators (Integer.parseInt (response.toString ())));
		rpt.initFactorizationsWithStats (new SieveOfEratosthenes (rpt));
		processor.getMap ().put (PRIME_REPORT, rpt);
		refresh ();
	}

}


/**
 * dialog for generation of prime number reports
 */
class PrimesDialogCommand extends SieveCommand
{

	public PrimesDialogCommand
	(String command, String request, String title, CommandProcessor processor, Component parent)
	{ super(command, request, title, processor, parent); }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SieveCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) { simpleDialog (); }

}


/**
 * refresh the environment tables
 */
class ShowCommand extends SimpleCommand
	implements ActionListener, MenuManager.HotKeyAvailable
{

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Refresh"; }

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) { refresh (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.HotKeyAvailable#getHotKey()
	 */
	public KeyStroke getHotKey () { return KeyStroke.getKeyStroke (KeyEvent.VK_R, ActionEvent.CTRL_MASK); }

	ShowCommand (DisplayIO.CommandProcessor processor)
	{ super ("REFRESH", processor); }

}


/**
 * execute a command in the processor in a background thread
 */
class CommandThread extends SimpleCommand implements Runnable
{

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run () { processor.execute (command); }

	public CommandThread (CommandProcessor processor, String command)
	{ super (command, processor); SimpleScreenIO.startBackgroundTask (this); }

}


/**
 * provide choices for type of regression
 */
class PolynomialRegression extends SimpleCommand implements ActionListener
{

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SimpleCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		Object choice;
		checkForCancel (choice = chooseFromList ("Type of Solution", "Regression Parameters", choices));
		map.get (choice).actionPerformed (e);
		refresh ();
	}

	PolynomialRegression (Map<String,ActionListener> map)
	{ super ("REGRESSION", null); this.map = map; this.choices = map.keySet ().toArray (); }
	Map<String,ActionListener> map;
	Object[] choices;

}


/**
 * promote symbol from background table to active environment display table
 */
class SymbolPromotion extends DialogCommand implements ActionListener, MenuManager.HotKeyAvailable
{

	public SymbolPromotion (CommandProcessor processor, Component c)
	{
		super ("PROMOTE", "Symbol to promote", "Promote Background Symbol", processor, c);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.DialogCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		Object response;
		checkForCancel (response = getResponse ());
		SymbolMap mapOfSymbols = EnvironmentCore.getSymbolMap (processor);
		SymbolMap.Named n = mapOfSymbols.lookup (response.toString ());
		if (n == null) terminate ("No symbol found with specified name");
		if (n instanceof NamedObject) ( (NamedObject) n ).expose ();
		mapOfSymbols.addToExposedItems (n); refresh ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.HotKeyAvailable#getHotKey()
	 */
	public KeyStroke getHotKey () { return KeyStroke.getKeyStroke (KeyEvent.VK_P, ActionEvent.CTRL_MASK); }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Promote Parent Symbol"; }

}


/**
 * common methods for objects using sequence form
 */
class SequenceDecls extends ToolBarPrimitives
{

	SequenceDecls
		(
			String name, DisplayIO.CommandProcessor processor, Component parent
		)
	{ super (name, processor, parent); }

	/**
	 * construct and show sequence form
	 * @param prompt the text of the prompt on the form
	 * @param title the frame title for the form
	 */
	void showForm (String prompt, String title)
	{ new SequenceForm (prompt, title, publisher, 2, SequenceForm.ARRAY_LABELS, "0"); }

	/**
	 * identify sequence form publisher to be used
	 * @param publisher the sequence form publisher to be used
	 */
	void setPublisher (SequenceForm.Publisher publisher) { this.publisher = publisher; }
	protected SequenceForm.Publisher publisher; 

	/**
	 * request name to be assigned to sequence
	 * @return the name entered by user
	 */
	String getNewName ()
	{
		return checkForCancel
		(getSimpleTextResponse ("Enter Name", "Name For Declaration"))
		.toString ();
	}

	/**
	 * format command that will declare symbol
	 * @param name the name to become defined for sequence
	 * @param items the items of the sequence
	 */
	void setSymbol (String name, SequenceForm.TextItemList items)
	{
		String sequence = SequenceForm.toCommaSeparated (items);
		processor.execute (name + " = (" + sequence + ")");
	}

}


/**
 * use sequence form to construct new array symbol
 */
class ArrayDecl extends SequenceDecls implements ActionListener, SequenceForm.Publisher, MenuManager.MnemonicAvailable
{

	ArrayDecl
	(DisplayIO.CommandProcessor processor, Component parent)
	{ super ("New Array", processor, parent); setPublisher (this); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SequenceForm.Publisher#publish(java.util.List)
	 */
	public void publish (SequenceForm.TextItemList items)
	{
		String name = getNewName ();
		setSymbol (name, items);
		refresh ();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		showForm ("Enter Array Elements ", "Create New Array");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.IntervalGenerator#getMnemonic()
	 */
	public char getMnemonic () { return 'N'; }

}

