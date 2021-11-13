
package net.myorb.math.expressions.gui;

/**
 * GUI component providing for number sequence data entry
 * @author Michael Druckman
 */
public class SequenceForm extends FormComponents implements FormComponents.Form
{


//	public static class TextItems extends TextItemList
//	{ private static final long serialVersionUID = 1L; }


	/**
	 * output of component guided to implementer of Publisher
	 */
	public interface Publisher
	{
		/**
		 * @param items sequence of items provided by data entry
		 */
		void publish (TextItemList items);
	}


	/**
	 * construct field and add to form
	 * @param form the form collecting values
	 * @param fieldWidth the width of each data field
	 * @param panel parent to add field to
	 * @return the new field object
	 */
	static Field newField (SequenceForm form, int fieldWidth, Panel panel)
	{
		Field t =
			newSelectedDefaultField
				(fieldWidth, form.defaultValue, panel);
		form.fields.add (t);
		return t;
	}
	protected String defaultValue = "0";


	/**
	 * construct sequence panel
	 * @param form the form collecting values
	 * @param labels text of labels that will separate fields
	 * @param fieldWidth the width of each data field in the form
	 * @return a swing disply panel of the sequence form
	 */
	static Panel getSequencePanel
	(SequenceForm form, String[] labels, int fieldWidth)
	{
		Panel p = new Panel ();
		newField (form, fieldWidth, p);
		for (int i=0; i<labels.length-1; i++)
		{
			addLabel (p, labels[i]);
			newField (form, fieldWidth, p);
		}
		addLabel (p, labels[labels.length-1]);
		return p;
	}


	/**
	 * construct full panel 
	 *  containing prompt, form, buttons
	 * @param labelText the text of prompt label
	 * @param form the form holding values of the sequence
	 * @param labels text of labels that will separate fields
	 * @param fieldWidth the width of each data field in the form
	 * @return a swing disply panel of the full form
	 */
	static Panel getPanel
	(String labelText, SequenceForm form, String[] labels, int fieldWidth)
	{
		Panel p;
		addLabel (p = new Panel (), labelText);
		p.add (getSequencePanel (form, labels, fieldWidth));
		p.add (getButtonPanel (form));
		return p;
	}


	/**
	 * format items as comma separated values
	 * @param items the list of items to be formatted
	 * @return the full text of the result
	 */
	static String toCommaSeparated (TextItemList items)
	{
		String text = items.get (0);
		for (int i = 1; i < items.size(); i++)
		{
			text += ", " + items.get (i);
		}
		return text;
	}


	/**
	 * collect data from form
	 * @param form the form to be processed
	 * @return list of values from form
	 */
	static TextItemList trim (SequenceForm form)
	{
		// remove trailing zero entries
		for (int i = form.fields.size()-1; i > 1; i--)
		{
			if (!getText (form.fields.get (i)).equals ("0")) break;
			form.fields.remove (i);
		}
		// collect remaining values
		TextItemList items = new TextItemList();
		for (int i = 0; i < form.fields.size(); i++)
		{
			items.add (getText (form.fields.get (i)));
		}
		return items;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.FormComponents.Form#publish()
	 */
	public void publish ()
	{
		TextItemList results = trim (this);
		if (publisher != null) publisher.publish (results);
		else System.out.println (results);
	}
	protected Publisher publisher;


	/**
	 * construct a sequence form and display
	 * @param prompt text of prompt to display on form
	 * @param title the text to be the title of the interval form
	 * @param publisher the object to receive the published data
	 * @param fieldWidth the width of each data field in the form
	 * @param labels the labels to use separating the fields
	 * @param defaultValue the default value for the fields
	 */
	public SequenceForm
	(String prompt, String title, Publisher publisher, int fieldWidth, String[] labels, String defaultValue)
	{
		this.fields = new ComponentList (); this.defaultValue = defaultValue; this.publisher = publisher;
		showFrame (getPanel (prompt, this, labels, fieldWidth), title);
	}
	protected ComponentList fields;


	/**
	 * alter term symbols to appear as function calls
	 * @param termSymbols the list of function symbol names
	 * @return the list of terms as function calls
	 */
	static String[] asTerms (String[] termSymbols)
	{
		String[] terms = new String[termSymbols.length];
		for (int i=0; i<terms.length; i++) terms[i] = " " + termSymbols[i] + " (x)";
		for (int i=0; i<terms.length-1; i++) terms[i] += "   + ";
		return terms;
	}


	/**
	 * show term symbols for series and request coefficients
	 * @param termSymbols the list of symbols to be used in each term
	 * @param publisher the object to receive the published data
	 */
	public static void requestCoefficientsForSeries (String[] termSymbols, Publisher publisher)
	{
		new SequenceForm ("Enter Coefficients ", "Request For Series Coefficients", publisher, 2, termSymbols, "1");
	}
	public static void requestCoefficientsForFunctionSeries (String[] termSymbols, Publisher publisher)
	{
		new SequenceForm ("Enter Coefficients ", "Request For Series Coefficients", publisher, 2, asTerms (termSymbols), "1");
	}
	public static void requestConstraintsForFunction (Publisher publisher)
	{
		new SequenceForm ("  ( Lo = ", "Constraints For Function", publisher, 5, new String[]{"    Hi = ", " ) * ", ""}, "1");
	}
	public static void requestArrayElements (Publisher publisher)
	{
		new SequenceForm ("Enter Array Elements ", "Create New Array", publisher, 5, ARRAY_LABELS, "0");
	}
	static final String[] ARRAY_LABELS = {", ", ", ", ", ", ", ", ", ", ""};

	/**
	 * basic range HI/LO
	 * @param publisher the object to receive the published data
	 */
	public static void requestRange (Publisher publisher)
	{
		new SequenceForm ("   Lo = ", "Enter Range", publisher, 5, new String[]{"    Hi = ", ""}, "");
	}


	/**
	 * a unit test for the form
	 * @param args not used
	 */
	public static void main (String... args)
	{
		new SequenceForm ("   Lo = ", "Enter Range", null, 5, new String[]{"    Hi = ", ""}, "0");
		//new SequenceForm ("   Lo = ", "Constraints For Function", null, 5, new String[]{"    Hi = ", ""}, "0");
		requestCoefficientsForSeries (new String[]{" a(x) + ", " b(x) + ", " c(x)"}, null);
		requestCoefficientsForFunctionSeries (new String[]{"a","b","c"}, null);
		requestConstraintsForFunction (null);
		requestArrayElements (null);
	}




}


