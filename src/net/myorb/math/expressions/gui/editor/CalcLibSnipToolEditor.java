
package net.myorb.math.expressions.gui.editor;

import net.myorb.gui.editor.model.SnipToolEditor;

import java.awt.Color;

/**
 * SnipEditor specific to CalcTools
 * @author Michael Druckman
 */
public class CalcLibSnipToolEditor extends SnipToolEditor
{

	public CalcLibSnipToolEditor (SnipProperties properties)
	{
		setEditorKitForContentType ("text/calc", properties.newKit ());
		setContentType ("text/calc"); setBackground (Color.white);
		//setEditable (true);
	}

	private static final long serialVersionUID = 1116124051020966007L;

}
