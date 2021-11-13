
package net.myorb.math.expressions.gui;

/**
 * alias selected (actual) function to (formal) function in differential equation definition.
 *  this is a preparation for running the range of the equation to calculate the error.
 *  the error function is typically displayed or used to find the max error value.
 * @author Michael Druckman
 */
public class TestPrepForm extends FormComponents implements FormComponents.Form
{


	/**
	 * output of component guided to implementer of Publisher
	 */
	public interface Publisher
	{
		/**
		 * @param descriptor array description provided by data entry
		 */
		void publish (String descriptor);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.FormComponents.Form#publish()
	 */
	public void publish ()
	{
		String t = command + getText (fields);
		if (publisher != null) publisher.publish (t);
		else System.out.println (t);
	}
	protected ComponentList fields = null;
	protected Publisher publisher = null;


	/**
	 * change to polynomial
	 */
	public void prepPoly ()
	{
		command = "PREPPOLY ";
		dField.setEnabled (false);
		dField.setText ("POLYDER");
	}
	protected String command = "PREPARE ";


	/**
	 * construct field and add to form
	 * @param form the form collecting values
	 * @param fieldWidth the width of each data field
	 * @param defaultValue the default to use
	 * @param panel parent to add field to
	 * @return the new field object
	 */
	static Field newField
	(TestPrepForm form, int fieldWidth, String defaultValue, Panel panel)
	{
		Field t =
			newSelectedDefaultField
				(fieldWidth, defaultValue, panel);
		form.fields.add (t);
		return t;
	}


	/**
	 * identify components to display
	 * @param form the form collecting the components
	 * @param function the name of the selected actual function
	 * @return a panel ready for display
	 */
	Panel getPanel
		(
			TestPrepForm form, String function
		)
	{
		Panel p = new Panel ();
		addLabel (p, "Actual");  aField = newField (form, 7, function, p);
		addLabel (p, "Formal");  fField = newField (form, 7, "f", p);
		addLabel (p, "Delta");   dField = newField (form, 7, "0.001", p);
		p.add (getButtonPanel (form));
		return p;
	}
	Field aField, fField, dField;


	/**
	 * @param title a title for the for
	 * @param function the name of the selected actual function
	 * @param publisher the callback object for the result
	 */
	public TestPrepForm
		(
			String title, String function, Publisher publisher
		)
	{
		this.fields = new ComponentList (); this.publisher = publisher;
		showFrame (getPanel (this, function), title);
	}


	/**
	 * a unit test for the form
	 * @param args not used
	 */
	public static void main (String... args)
	{
		new TestPrepForm ("Differential Equation Solution Test", "FUNC", null);
	}


}

