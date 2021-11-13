
package net.myorb.math.expressions.gui.rendering;

import net.myorb.math.expressions.PrettyPrinter;
import net.myorb.data.abstractions.SimplePropertiesManager.PropertiesMap;
import net.myorb.data.abstractions.SimpleStreamIO;
import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.utilities.ApplicationShell;

/**
 * display mark-up text in Internet Explorer
 * @author Michael Druckman
 */
public class MmlDisplayFormatter extends SimpleScreenIO
	implements PrettyPrinter.MathMarkupRendering
{

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.PrettyPrinter.MathMarkupRendering#render(java.lang.String)
	 */
	public Widget render (String mathMl) throws Exception
	{
		renderAsXml (mathMl);
		return null;
	}

	/**
	 * show mark-up in Internet Explorer
	 * @param mathMl the text of the mark-up language
	 * @throws Exception for any error
	 */
	public static void renderAsXml (String mathMl) throws Exception
	{
		String path =
			SimpleStreamIO.saveToTempFile
				(mathMl, "MML", ".xml").getAbsolutePath ();
		ApplicationShell.showForType (path);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.PrettyPrinter.MathMarkupRendering#configureWith(net.myorb.data.abstractions.SimplePropertiesManager.PropertiesMap)
	 */
	@Override
	public void configureWith (PropertiesMap map)
	{
		// no properties to recognize
	}

}

