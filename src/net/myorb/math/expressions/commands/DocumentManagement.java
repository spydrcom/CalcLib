
package net.myorb.math.expressions.commands;

// CalcLib expressions
import net.myorb.math.expressions.PrettyPrinter;
import net.myorb.math.expressions.evaluationstates.Environment;

// IOlib
import net.myorb.gui.components.RenderingDisplay;
import net.myorb.data.abstractions.HelpTableCompiler;
import net.myorb.data.abstractions.HtmlTable;

// JRE
import java.util.Set;

/**
 * support for display of HTML documents in swing displays
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class DocumentManagement<T> extends Utilities<T>
{


	public DocumentManagement
	(Environment<T> environment)
	{ super (environment); }


	/**
	 * display HELP table
	 * @param commands the command dictionary
	 */
	public void help (CommandDictionary commands)
	{
		RenderingDisplay opsHelp = prepareDocument
		(environment.getSymbolMap ().getOperatorHelpDocument ());
		RenderingDisplay cmdHelp = prepareDocument (getKeywordHelpDocument (commands));
		PrettyPrinter.showHelp (cmdHelp, opsHelp);
	}


	/**
	 * prepare an HTML document for
	 *  inclusion in a rendering display with Open With menu functions
	 * @param html the HTML object containing the document content
	 * @return a rendering display containing the document
	 */
	public RenderingDisplay prepareDocument (HtmlTable html)
	{
		RenderingDisplay renderingDisplay =
			RenderingDisplay.newRenderingDisplayPanel ();
		renderingDisplay.addComponentWithMenu (html);
		return renderingDisplay;
	}


	/**
	 * generate help document for commands
	 * @param commands the command dictionary
	 * @return HTML for keyword help document
	 */
	public HtmlTable getKeywordHelpDocument (CommandDictionary commands)
	{
		String V = environment.getSymbolMap ().getVersion ();
		return new HelpTableCompiler ("CalcLib Command Help", "Active Commands in CalcLib Build " + V)
		.setColumnHeaders ("Command", "A simple description of the actions of each command").buildFrom
		(
			new HelpTableCompiler.TableCompilationAccess ()
			{
				public String descriptionFor (String name) { return commands.get (name).describe (); }
				public Set<String> getElementNames () { return commands.keySet (); }
			}
		);
	}


}

