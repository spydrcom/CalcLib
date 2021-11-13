
package net.myorb.math.expressions.gui;

import net.myorb.data.abstractions.PrimitiveRangeDescription;

/**
 * describe an interval taken from user input
 * @author Michael Druckman
 */
public class IntervalForm extends FormComponents implements FormComponents.Form
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

	/**
	 * add a relational operator to be selected by user
	 * @param fields the list of fields in the form
	 * @param panel the display panel being built
	 */
	static void addRelationalOperator (ComponentList fields, Panel panel)
	{
		fields.add (addCombo (panel, new String[]{"<=", "<"}));
	}


	/**
	 * add identifier choices to be selected by user
	 * @param choices the choices of identifier offered
	 * @param fields the list of fields in the form
	 * @param panel the display panel being built
	 */
	void addIdentifier
	(String[] choices, ComponentList fields, Panel panel)
	{
		identifierName = newSelectedDefaultField (5, choices[0], panel);
		fields.add (identifierName); addFieldMenu (identifierName, choices);
	}
	public String getSelectedIdentifier ()
	{ return identifierName.getText (); }
	protected Field identifierName;


	/**
	 * @param name new name for identifier
	 */
	public void setIdentifier (String name)
	{
		if (name != null) identifierName.setText (name);
	}


	/**
	 * add a data entry field
	 * @param fields the list of fields in the form
	 * @param panel the display panel being built
	 * @param defaultValue value to start with
	 */
	static void addField (ComponentList fields, Panel panel, String defaultValue)
	{
		fields.add (newSelectedDefaultField (5, defaultValue, panel));
	}


	/**
	 * add a text item to the form
	 * @param text the text to be displayed
	 * @param fields the list of fields in the form
	 * @param panel the display panel being built
	 */
	static void addLabel (String text, ComponentList fields, Panel panel)
	{
		fields.add (addLabel (panel, text));
	}


	/**
	 * add the components to build an interval form
	 * @param form the form object holding the fields
	 * @param valueDefaults the default values for the components
	 * @param idDefaults the identifier names for the components
	 * @return a swing display panel of the form
	 */
	static Panel getPanel
		(
			IntervalForm form,
			String[] valueDefaults,
			String[] idDefaults
		)
	{
		Panel
		p = new Panel ();

		addLabel (" [ ", form.fields, p);																						// [
		addField (form.fields, p, valueDefaults[0]); addRelationalOperator (form.fields, p);									// lo <=
		addLabel ("  ", form.fields, p); form.addIdentifier (idDefaults, form.fields, p);										//  id
		addLabel ("  ", form.fields, p); addRelationalOperator (form.fields, p); addField (form.fields, p, valueDefaults[1]);	// <= hi
		addLabel (" <> ", form.fields, p); addField (form.fields, p, valueDefaults[2]);											// <> inc
		addLabel (" ] ", form.fields, p);																						// ]

		p.add (getButtonPanel (form));

		return p;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.FormComponents.Form#publish()
	 */
	public void publish ()
	{
		String t = getText (fields);
		if (publisher != null) publisher.publish (t);
		else System.out.println (t);
	}
	protected ComponentList fields = null;
	protected Publisher publisher = null;


	/**
	 * get the values of the displayed components
	 * @return array of text strings
	 */
	public String[] getInputs ()
	{
		String[] inputs = new String[fields.size()];
		for (int i=0; i<inputs.length; i++) inputs[i] = getText (fields.get (i));
		return inputs;
	}
	public static final int INPUT_LO = 1, INPUT_ID = 4, INPUT_HI = 7, INPUT_INC = 9;


	/**
	 * construct an interval form from default values
	 * @param title the text to be the title of the interval form
	 * @param publisher the object to receive the published data of the form
	 * @param identifierDefaults  the list of identifier names for the form
	 * @param valueDefaults the list of default values for the form
	 */
	public IntervalForm
		(
			String title, Publisher publisher,
			String[] identifierDefaults, String[] valueDefaults
		)
	{
		this.fields = new ComponentList (); this.publisher = publisher;
		showFrame (getPanel (this, valueDefaults, identifierDefaults), title);
	}


	/**
	 * default values for SUMMATION and GRAPH uses of form
	 */
	public static String[]
	GRAPH_ID_DEFAULTS = {"x", "z", "alpha", "omega", "tau", "theta", "s", "t", "u", "v", "w"},
	SUMMATION_ID_DEFAULTS = {"i", "j", "k", "l", "m", "n"},
	SUMMATION_VALUE_DEFAULTS = {"0", "INFINITY", "1"},
	GRAPH_VALUE_DEFAULTS = {"-1", "1", "0.01"};


	/**
	 * a unit test for the form
	 * @param args not used
	 */
	public static void main (String... args)
	{
		new IntervalForm ("data entry test", null, SUMMATION_ID_DEFAULTS, SUMMATION_VALUE_DEFAULTS);
		//new IntervalForm ("data entry test", null, GRAPH_ID_DEFAULTS, GRAPH_VALUE_DEFAULTS);
	}


}


/**
 * POJO for transporting form values
 */
class FormValueCollection
	extends PrimitiveRangeDescription
{
	FormValueCollection (String[] values)
	{
		super
		(
			values[IntervalForm.INPUT_LO],
			values[IntervalForm.INPUT_HI],
			values[IntervalForm.INPUT_INC]
		);
	}
}

