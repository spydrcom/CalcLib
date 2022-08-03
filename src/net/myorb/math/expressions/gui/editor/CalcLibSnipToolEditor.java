
package net.myorb.math.expressions.gui.editor;

import net.myorb.gui.components.SimpleScreenIO;

import java.awt.Color;
import java.awt.Font;

/**
 * SnipEditor specific to CalcTools
 * @author Michael Druckman
 */
public class CalcLibSnipToolEditor extends SimpleScreenIO.SnipEditor
{

	public CalcLibSnipToolEditor (SnipProperties properties)
	{
		setEditorKitForContentType ("text/calc", properties.newKit ());
		setFont (new Font (properties.getFontFamily (), 0, properties.getFontSize ()));
		setContentType ("text/calc"); setBackground (Color.white);
		setEditable (true);
	}

	private static final long serialVersionUID = 1116124051020966007L;

}
