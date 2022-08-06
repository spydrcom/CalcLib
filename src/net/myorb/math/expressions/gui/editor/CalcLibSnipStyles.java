
package net.myorb.math.expressions.gui.editor;

import net.myorb.gui.editor.model.SnipToolContext;
import net.myorb.gui.editor.SnipToolPropertyAccess;

/**
 * extended style manager for styles imported by JXR script
 * @author Michael Druckman
 */
public class CalcLibSnipStyles extends SnipToolContext
{

	public static final String JXR_SCRIPT = "cfg/gui/SnipStyles.xml";

	/**
	 * initialize StyleManager and read configuration script
	 * @param properties the SnipTool property access object
	 */
	public CalcLibSnipStyles (SnipToolPropertyAccess properties)
	{ super (properties); readScript (JXR_SCRIPT); }

	private static final long serialVersionUID = -8698578465158030119L;

}
