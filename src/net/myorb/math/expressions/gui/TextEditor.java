
package net.myorb.math.expressions.gui;

import net.myorb.gui.components.DisplayFrame;
import net.myorb.gui.components.MenuManager;

import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.JScrollPane;
import javax.swing.JComponent;
import javax.swing.JTextArea;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Font;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;

import java.util.ArrayList;
import java.util.List;

/**
 * extend swing TextArea to provide save/execute functions
 * @author Michael Druckman
 */
public class TextEditor extends DisplayFrame
{

	public static final String END_OF_LINE = "\n";

	/**
	 * access to processor for text
	 */
	public interface TextProcessor
	{
		/**
		 * @param text the text to be processed
		 */
		void process (String text);
	}

	/**
	 * determine if text
	 *  contains new line character(s)
	 * @param text the text to be checked
	 * @return TRUE = new line is present
	 */
	public static boolean hasEndOfLine (String text)
	{
		return text.contains (END_OF_LINE);
	}

	/**
	 * break text into lines
	 * @param text the text being parsed
	 * @return the array of lines
	 */
	public static String [] linesFrom (String text)
	{
		return text.split (END_OF_LINE);
	}

	/**
	 * break text at new line character
	 * @param text the text being parsed
	 * @return list of parsed text lines
	 */
	public static List <String> parseTextLines (String text)
	{
		List <String> list = new ArrayList <String> ();
		String lines [] = linesFrom (text), l, line;
		for (int n = 0; n < lines.length; n++)
		{
			l = lines [n];
			if (l.length () == 0) continue;
			if (!l.endsWith (END_OF_LINE)) line = l;
			else line = l.substring (0, l.length () - 1);
			if (line.length () == 0) continue;
			list.add (line);
		}
		return list;
	}

	/**
	 * process text
	 *  with multiple EOL markers
	 * @param text the text being parsed
	 * @param processor the action to use on lines
	 */
	public static void processMultiLine (String text, TextProcessor processor)
	{
		for (String line : parseTextLines (text)) { processor.process (line); }
	}

	/**
	 * get text area from view port of scroll pane
	 * @param pane the scroll pane component
	 * @return the text area component
	 */
	public static JTextArea getAreaInScroll (JComponent pane)
	{
		JViewport v = (JViewport) pane.getComponents () [0];
		return (JTextArea) v.getComponents () [0];
	}

	/**
	 * show console in frame
	 * @param console the console to show
	 * @param title the title of the frame
	 * @param size the frame preferred size
	 */
	public static void showConsole
		(JScrollPane console, String title, int size)
	{
		JPanel panel = new JPanel ();
		panel.setLayout (new GridLayout ());
	
		panel.setPreferredSize
		(new Dimension (size, size));
		panel.add (console);
	
		new DisplayFrame (panel, title).show ();
	}

	/**
	 * build a scrolling text area with associated menu
	 * @param title the title for the frame that shows the console
	 * @param processor the processor being demonstrated
	 * @return the scroll pane for the console
	 */
	public static JScrollPane displayArea (String title, TextProcessor processor)
	{
		JTextArea console = new JTextArea ();
		console.setFont (new Font ("Arial Unicode MS", java.awt.Font.PLAIN, 14));
		console.addMouseListener (MenuManager.getMenu (getMenuActions (console, title, processor)));
		JScrollPane scrollPane = new JScrollPane (console);
		return scrollPane;
	}

	/**
	 * build a menu manager action list
	 * @param console the console that will associate with the menu
	 * @param title the title for the frame that shows the console
	 * @param processor the processor being demonstrated
	 * @return the action list for the menu
	 */
	public static MenuManager.ActionList getMenuActions
	(JTextArea console, String title, TextProcessor processor)
	{
		MenuManager.ActionList items = new MenuManager.ActionList ();
		if (processor != null) items.add (new UseSelectedCommand (console, processor));
		items.add (new SaveCommand (console, title));
		items.add (new ClearCommand (console));
		return items;
	}

	/**
	 * display a frame holding a console
	 * @param title the title for the frame that shows the console
	 * @param size the preferred size for the console panel
	 * @param processor the processor being demonstrated
	 */
	public static void showConsole (String title, int size, TextProcessor processor)
	{
		showConsole (displayArea (title, processor), title, size);
	}

	/**
	 * demonstrate script editing
	 * @param title the title for the frame that shows the console
	 * @param processor the processor being demonstrated
	 */
	public static void edit (String title, TextProcessor processor)
	{
		JScrollPane p;
		showConsole (p = displayArea (title, processor), title, DEFAULT_DISPLAY_AREA_SIZE);
		JTextArea area = getAreaInScroll (p);

		try
		{
			char[] c = new char[1000]; int read;
			File f = new File ("scripts/" + title);
			FileReader r = new FileReader (f);

			while (true)
			{
				read = r.read (c);
				if (read < 0) break;
				area.append (new String (c, 0, read));
			}

			r.close ();
		} catch (Exception x) {}
	}

	/**
	 * build a console to demonstrate a process activity
	 * @param title the title for the frame that shows the console
	 * @param processor the processor being demonstrated
	 */
	public static void create (String title, TextProcessor processor)
	{
		showConsole (title, DEFAULT_DISPLAY_AREA_SIZE, processor);
	}

	/**
	 * unit test
	 * @param args not used
	 */
	public static void main (String[] args)
	{
		edit ("sqrtIteration.txt", new TextListener ());
		//showConsole ("TestScript.txt", DEFAULT_DISPLAY_AREA_SIZE);
		//DisplayTests.main(new String[]{});
	}

}


/**
 * dump parsed lines
 */
class TextListener implements TextEditor.TextProcessor
{
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.TextEditor.TextProcessor#process(java.lang.String)
	 */
	public void process (String text)
	{
		String [] tokens = TextEditor.linesFrom (text);
		for (int n = 0; n < tokens.length; n++)
		{
			String token = tokens [n];
			System.out.println (token.substring (0, token.length () - 1));
			System.out.println ("---");
		}
	}
}

/**
 * menu item processing for Use Selected
 */
class UseSelectedCommand implements ActionListener
{

	UseSelectedCommand
	(JTextArea console, TextEditor.TextProcessor processor)
	{
		this.processor = processor;
		this.console = console;
	}
	TextEditor.TextProcessor processor;
	JTextArea console;

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		String selected = console.getSelectedText ();
		if (selected == null || selected.length () == 0) return;
		processor.process (selected);
	}

	public String toString () { return "Use Selected"; }

}

/**
 * menu item processing for Clear
 */
class ClearCommand implements ActionListener
{

	ClearCommand (JTextArea console)
	{
		this.console = console;
	}
	JTextArea console;

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		console.setText ("");
	}

	public String toString () { return "Clear"; }

}

/**
 * menu item processing for Save
 */
class SaveCommand implements ActionListener
{

	SaveCommand (JTextArea console, String title)
	{
		this.console = console;
		this.title = title;
	}
	JTextArea console;
	String title;

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		System.out.println ("Saving To:");
		System.out.println ("scripts/" + title);
		System.out.println ("-------"); System.out.println ();
		//System.out.println (console.getText ());

		try
		{
			File f = new File ("scripts/" + title);
			FileWriter w = new FileWriter (f);
			w.write (console.getText ());
			w.flush (); w.close ();
		} catch (Exception x) {}
	}
	
	public String toString () { return "Save"; }

}

