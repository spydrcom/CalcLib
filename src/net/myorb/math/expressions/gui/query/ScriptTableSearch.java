
package net.myorb.math.expressions.gui.query;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.editor.CalcLibSnipTool;

import java.io.File;

/**
 * GUI component to search for a script file
 * @author Michael Druckman
 */
public class ScriptTableSearch extends TableSearch
	implements TableSearch.SelectionAction
{


	/**
	 * prepare search specific to script files
	 * - action connected will be to open a snip editor
	 * @param environment access to display components
	 */
	public ScriptTableSearch  (Environment <?> environment)
	{
		super ("Matching Scripts");				// header title in search display
		this.setSelectionAction (this);			// point to SelectionAction processor
		this.environment = environment;			// environment used by editor
	}
	protected Environment <?> environment;


	/* (non-Javadoc)
	 * @see net.myorb.gui.components.FindWidget.SelectionAction#process(java.lang.String)
	 */
	public void process (String text)
	{
		CalcLibSnipTool.initializeSnip
			(environment);						// on first reference properties must be initialized
		CalcLibSnipTool.addSnip
		(
			new File ("scripts/" + text),		// now script file can be opened
			environment.getSnipProperties ()	// using properties
		);
	}


	private static final long serialVersionUID = -3989005302308141720L;
}

