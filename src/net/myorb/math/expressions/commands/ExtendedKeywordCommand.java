
package net.myorb.math.expressions.commands;

/**
 * allow for commands that require Subordinates
 * @author Michael Druckman
 */
public interface ExtendedKeywordCommand
	extends KeywordCommand
{
	/**
	 * @return an array holding the Subordinate keywords
	 */
	String[] includingSubordinateKeywords ();
}
