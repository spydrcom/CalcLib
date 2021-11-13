
package net.myorb.math.expressions.commands;

/**
 * link processing with a keyword
 * @author Michael Druckman
 */
public interface KeywordCommand
{
	/**
	 * invoke the processing appropriate to a keyword
	 * @param tokens the tokens of the parameters to the command
	 */
	void execute (CommandSequence tokens);

	/**
	 * construct a contribution to the HELP document for this command
	 * @return the text of the HELP statement for this command
	 */
	String describe ();
}
