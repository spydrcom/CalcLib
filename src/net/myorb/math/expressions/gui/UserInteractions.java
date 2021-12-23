
package net.myorb.math.expressions.gui;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.data.abstractions.CommonCommandParser;
import net.myorb.gui.components.SimpleScreenIO;

/**
 * user requests for data input
 * @author Michael Druckman
 */
public class UserInteractions
{

	/**
	 * request text from the user
	 * @param prompt the prompt to show the user
	 * @return the value entered
	 */
	public static String requestInput (String prompt)
	{
		Object scripts = DisplayFiles.scriptFiles ();
		return SimpleScreenIO.requestInput (scripts, prompt, "EnterValue").toString ();
	}

	/**
	 * request a value from the user
	 * @param prompt the prompt to show the user
	 * @param environment a description of the system environment
	 * @return the data entered parsed and wrapped as Generic Value
	 * @param <T> data type
	 */
	public static <T> ValueManager.GenericValue requestValue
			(String prompt, Environment<T> environment)
	{
		String value = UserInteractions.requestInput (prompt);
		ExpressionSpaceManager<T> sm = environment.getSpaceManager ();
		T v = sm.parseValueToken (CommonCommandParser.TokenType.FLT, value);
		return environment.getValueManager ().newDiscreteValue (v);
	}

}
