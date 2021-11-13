
package net.myorb.math.expressions.commands;

import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.data.abstractions.SimpleUtilities;

import java.util.List;

/**
 * collected information about commands
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class CommandAdministration<T> extends Utilities<T>
{


	public CommandAdministration
	(Environment<T> environment)
	{
		super (environment);
		commands = environment.getCommandDictionary ();
	}


	/**
	 * @return access to command symbols
	 */
	public CommandDictionary 
		getCommandDictionary () { return commands; }
	protected CommandDictionary commands;;


	/**
	 * examine first token of line
	 *  to determine if a command is requested
	 * @param tokens the full line of tokens being checked
	 * @return TRUE = command was identified and processed
	 */
	public boolean isKeywordCommand
	(List<TokenParser.TokenDescriptor> tokens)
	{
		Object found;
		if (tokens.size () == 0) return true;
		if ((found = commands.get (commandToken (tokens))) == null) return false;
		KeywordCommand cmd = SimpleUtilities.verifyClass (found, KeywordCommand.class);
		if (cmd == null) throw new RuntimeException ("Keyword command error");				// INTERNAL ERROR
		cmd.execute (new CommandSequence (tokens));
		environment.checkValueStack ();
		return true;
	}


	/**
	 * command names are forced to
	 *  lower case to force case insensitivity
	 * @param name the name of the command (case not important)
	 * @param cmd the command implementation object
	 */
	public void addAsLowerCase
	(String name, KeywordCommand cmd)
	{ commands.put (name.toLowerCase (), cmd); }


	/**
	 * for commands known not to have case issues
	 * @param name the name of the command (case not important)
	 * @param cmd the command implementation object
	 */
	public void add (String name, KeywordCommand cmd) { commands.put (name, cmd); }


	/**
	 * identify
	 * @param cmd a command keyword description
	 * @param as the specified name
	 */
	public void id (KeywordCommand cmd, String as) { addAsLowerCase (as, cmd); }


}

