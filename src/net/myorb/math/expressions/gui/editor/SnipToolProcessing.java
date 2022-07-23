
package net.myorb.math.expressions.gui.editor;

/**
 * processing methods for execution of editor requests
 * @author Michael Druckman
 */
public class SnipToolProcessing extends SnipToolDisplays
{


	/**
	 * @return the name provided by screen request
	 * @throws Exception for errors in the input request
	 */
	String requestName () throws Exception
	{
		return requestTextInput (tabs, "Name for Tab", "", "NA");
	}


}
