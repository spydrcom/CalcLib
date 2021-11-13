
package net.myorb.math.expressions.gui;

import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.gui.components.SimpleCallback;

import javax.swing.text.JTextComponent;
import javax.swing.JComponent;

/**
 * common components for forms
 * @author Michael Druckman
 */
public class FormComponents extends SimpleScreenIO
{


	/**
	 * component interface to frame
	 */
	public interface Form
	{
		/**
		 * frame can be closed
		 */
		void dispose ();

		/**
		 * process data entered on form
		 */
		void publish ();
		
		/**
		 * establish default button for form
		 * @param button the button to set as default
		 */
		void setDefaultButton (Button button);
		Button getDefaultButton ();
	}


	/**
	 * OK and Cancel buttons
	 * @param form connections to frame
	 * @return panel of buttons
	 */
	static Panel getButtonPanel (Form form)
	{
		Panel p = new Panel ();
		Button ok = addButton (p, "OK", new OkCallback (form));
		ok.setDefaultCapable (true); form.setDefaultButton (ok);
		addButton (p, "Cancel", new CancelCallback (form));
		return p;
	}
	static class CancelCallback extends SimpleCallback.Adapter
	{
		Form form;
		CancelCallback (Form form) { this.form = form; }
		public void executeAction () throws Exception
		{ form.dispose (); }
	}
	static class OkCallback extends CancelCallback
	{
		OkCallback (Form form) { super (form); }
		public void executeAction () throws Exception
		{ form.publish (); form.dispose (); }
	}


	/**
	 * collect field data from form
	 * @param fields the list of fields in form
	 * @return concatenated list of values
	 */
	public static String getText (ComponentList fields)
	{
		StringBuffer buffer = new StringBuffer ();
		for (JComponent c : fields)
		{
			buffer.append (getText (c));
			buffer.append (" ");
		}
		return buffer.toString ();
	}


	/**
	 * get text from components
	 * @param c the component to read
	 * @return the text value
	 */
	public static String getText (JComponent c)
	{
		if (c instanceof Readable) return ((Readable)c).getTextRepresentation ();
		else if (c instanceof JTextComponent) return (((JTextComponent)c).getText ());
		else return c.toString ();
	}


	/**
	 * right justified field with data selected
	 * @param size the number of columns of data in the field
	 * @param defaultValue the default value for the field
	 * @param panel parent to add field to
	 * @return the new field component
	 */
	static Field newSelectedDefaultField (int size, String defaultValue, Panel panel)
	{
		Field f = addField (panel, size);
		f.setHorizontalAlignment (Field.RIGHT);
		f.setText (defaultValue);
		f.selectAll ();
		return f;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.FormComponents.Form#setDefaultButton(javax.swing.JButton)
	 */
	public void setDefaultButton (Button button)
	{ this.defaultButton = button; }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.FormComponents.Form#getDefaultButton()
	 */
	public Button getDefaultButton ()
	{ return defaultButton; }
	Button defaultButton;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.FormComponents.Form#dispose()
	 */
	public void dispose () { displayFrame.dispose (); }


	/**
	 * @param panel main panel for form
	 * @param title the title for the form frame
	 */
	public void showFrame (Panel panel, String title)
	{
		(this.displayFrame = new WidgetFrame (panel, title)).show ();
		this.displayFrame.setDefaultButton (getDefaultButton ());
	}
	protected Frame displayFrame;


}

