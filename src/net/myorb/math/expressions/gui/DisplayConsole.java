
package net.myorb.math.expressions.gui;

import net.myorb.gui.components.StreamDisplay;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * extended text area component.
 *  display can be accessed as stream or writer
 * @author Michael Druckman
 */
public class DisplayConsole extends StreamDisplay
{

	/**
	 * @param name the name of the console
	 * @param map the properties map for console
	 * @param title a title for the frame being displayed
	 * @param processor a processing engine for the text
	 * @return the scroll pane for the display area
	 */
	public static JScrollPane displayArea
		(
			String name,
			StreamProperties map, String title,
			TextEditor.TextProcessor processor
		)
	{
		JScrollPane scrollPane =
				TextEditor.displayArea (title, processor);
		JTextArea textArea = TextEditor.getAreaInScroll (scrollPane);
		if (name != null) setProperties (map, name, textArea, scrollPane);
		return scrollPane;
	}

}

