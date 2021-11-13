
package net.myorb.math.expressions.gui;

import javax.swing.border.LineBorder;

import net.myorb.gui.components.SimpleScreenIO;

import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * tabulate formal/actual parameter alias links
 * @author Michael Druckman
 */
public class FormalActualParameters extends FormComponents
	implements FormComponents.Form, ActionListener
{


	/**
	 * calls back to caller
	 */
	public interface Callbacks
	{

		/**
		 * OK button has been pressed
		 * @throws Alert for processing errors
		 */
		void proceed () throws Alert;

		/**
		 * act on button request
		 * @param called identifier for action
		 * @throws Alert for any errors
		 */
		void takeAction (String called) throws Alert;

	}
	protected Callbacks callbacks;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.FormComponents.Form#publish()
	 */
	@Override public void publish ()
	{
		try { callbacks.proceed (); }
		catch (Alert alert) { alert.presentDialog (); }
		catch (Exception x) { nonAlert (x); }
	}


	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override public void actionPerformed (ActionEvent e)
	{
		try { callbacks.takeAction (((Button) e.getSource ()).getText ()); }
		catch (Alert alert) { alert.presentDialog (); }
		catch (Exception x) { nonAlert (x); }
	}


	/**
	 * dialog for runtime exceptions
	 * @param x the exception being processed
	 */
	public void nonAlert (Exception x)
	{
		JOptionPane.showMessageDialog
		(
			null, x.getMessage (), "Error Encountered", JOptionPane.ERROR_MESSAGE
		);
	}


	/**
	 * build display and present to user
	 * @param title a title for the frame
	 * @param actions names f buttons
	 */
	public void showFrame (String title, String[] actions)
	{
		Panel bp;
		p.add (bp = getButtonPanel (this));
		bp.setBorder (GRAY_LINE_BORDER);
		actionPanel (actions, p);
		showFrame (p, title);
	}
	public void actionPanel (String[] actions, Panel parent)
	{
		Panel p; Button b;
		parent.add (p = new Panel ());
		for (String button : actions)
		{
			p.add (b = new Button (button));
			b.addActionListener (this);
		}
		p.setBorder (GRAY_LINE_BORDER);
	}
	static final LineBorder GRAY_LINE_BORDER = new LineBorder (Color.lightGray, 5);


	/**
	 * @param formal text of formal parameter
	 * @param actual text of actual parameter
	 */
	public void addFormalActual (String formal, String actual)
	{
		Label
		l2 = new Label (pad (actual)),
		l1 = new Label (formal + "  ", Label.RIGHT);
		l1.setBorder (GRAY_LINE_BORDER);
		l2.setBorder (GRAY_LINE_BORDER);
		formMap.put (formal, l2);
		p.add (l1); p.add (l2);
	}
	Map <String,Label> formMap = new HashMap <String,Label> ();


	/**
	 * @param from values mapped as name/value pairs
	 */
	public void setItems (Map <String,String> from)
	{
		for (String name : from.keySet ())
		{
			formMap.get (name).setText (pad (from.get (name)));
		}
	}
	private String pad (String source) { return "  " + source + "  "; }


	/**
	 * add name/value pairs to form
	 * @param from values mapped as name/value pairs
	 */
	public void addItems (Map <String,String> from)
	{
		for (String name : from.keySet ())
		{
			addFormalActual (name, from.get (name));
		}
	}


	/**
	 * check for empty parameters
	 * @throws Alert for incomplete parameter list
	 */
	public void verify () throws Alert
	{
		for (String name : formMap.keySet ())
		{
			if (formMap.get (name).getText ().trim ().isEmpty ())
			{
				SimpleScreenIO.alertError ("Parameter list is incomplete");
			}
		}
	}


	/**
	 * @param callbacks the feedback to the caller
	 */
	public FormalActualParameters (Callbacks callbacks)
	{
		p = startGridPanel (null, 0, 2);
		this.callbacks = callbacks;
	}
	private Panel p;


}

