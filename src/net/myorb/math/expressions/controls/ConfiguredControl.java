
package net.myorb.math.expressions.controls;

import net.myorb.math.expressions.algorithms.*;

import javax.swing.JOptionPane;
import java.io.File;

/**
 * main driver for system that uses serial (file or stream based) symbol configuration
 * @param <T> manager for data type
 * @author Michael Druckman
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ConfiguredControl<T> extends ConfiguredEvaluationControl<T>
{

	public static String CONFIG_HOME = "cfg\\testbed\\", DEFAULT_CONFIG_FILE = "default.txt";

	ConfiguredControl (ConfigurationManager cm)
	{ super (cm.getEnvironment (), true); cm.addCommands (engine.getKeywordMap ()); }
	ConfiguredControl (String path) throws Exception { this (new ConfigurationManager (path)); }

	public static void main (String... args) throws Exception
	{
		try
		{
			Object choice = null;
			File cfg = new File (CONFIG_HOME), files[] = cfg.listFiles ();
			if (files.length == 0) throw new Exception ("No configuration available");
			else if (files.length == 1) choice = files[0].getAbsolutePath ();
			else
			{
				choice = JOptionPane.showInputDialog		// swing dialog implements selection
					(
						null, "Select Configuration",
						"CALCLIB", JOptionPane.PLAIN_MESSAGE, null, files,
						new File (CONFIG_HOME + DEFAULT_CONFIG_FILE)
					);
				if (choice == null) return;
			}
			new ConfiguredControl (choice.toString ());
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog
			(null, e.getMessage (), "Error Encountered", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace ();
		}
	}

}

