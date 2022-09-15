
package net.myorb.math.expressions.gui.query;

import net.myorb.math.expressions.evaluationstates.Environment;
//import net.myorb.math.expressions.gui.editor.CalcLibSnipTool;
import net.myorb.gui.editor.SnipToolPropertyAccess;
import net.myorb.gui.editor.SnipTool;

import java.io.File;

/**
 * GUI component to search for a script file
 * @author Michael Druckman
 */
public class ScriptTableSearch extends TableSearch
	implements TableSearch.SelectionAction
{


	public ScriptTableSearch  (Environment <?> environment)
	{
		super ("Matching Scripts");
		this.setSelectionAction (this);
		this.environment = environment;
	}
	Environment <?> environment;


	/* (non-Javadoc)
	 * @see net.myorb.gui.components.FindWidget.SelectionAction#process(java.lang.String)
	 */
	public void process (String text)
	{
		//TODO: fix bug
		SnipToolPropertyAccess access = environment.getSnipProperties ();
		SnipTool.addSnip (new File ("scripts/" + text), access);
	}


	private static final long serialVersionUID = -3989005302308141720L;

}
