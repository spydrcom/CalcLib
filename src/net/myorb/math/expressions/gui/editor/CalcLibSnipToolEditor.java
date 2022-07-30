
package net.myorb.math.expressions.gui.editor;

import net.myorb.gui.components.SimpleScreenIO;

import java.awt.Color;
import java.awt.Font;

public class CalcLibSnipToolEditor extends SimpleScreenIO.SnipEditor
{

	public CalcLibSnipToolEditor (SnipProperties properties)
	{
		setEditorKitForContentType ("text/calc", properties.newKit ());
		setFont (new Font ("Courier", 0, 12));
		setContentType ("text/calc");
		setBackground (Color.white);
		setEditable (true);
	}

	private static final long serialVersionUID = 1116124051020966007L;

}
