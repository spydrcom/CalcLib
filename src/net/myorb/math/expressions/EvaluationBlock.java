
package net.myorb.math.expressions;

import net.myorb.math.expressions.commands.Utilities;
import net.myorb.math.expressions.commands.CommandSequence;
import net.myorb.math.expressions.commands.EnvironmentalUtilities;

import net.myorb.math.expressions.evaluationstates.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * processing cache for grouped elements
 * @author Michael Druckman
 */
public class EvaluationBlock <T>
{


	/**
	 * identify status of processor
	 */
	public enum ProcessingStates
	{
		NORMAL_EXECUTION,			// no active block or condition indicated execution
		BLOCK_QUEUE_ACTIVE,			// a loop block is being queued for counted execution
		CONDITIONALLY_DISCARDING	// conditional block is active and processing is disabled
	}
	public static final ProcessingStates
		ENABLED = ProcessingStates.NORMAL_EXECUTION,
		DISABLED = ProcessingStates.CONDITIONALLY_DISCARDING,
		COMPILE = ProcessingStates.BLOCK_QUEUE_ACTIVE;
	protected ProcessingStates currently = ENABLED;

	/**
	 * status is to change back to normal
	 */
	public void resetToNormal () { this.currently = ENABLED; }


	/**
	 * default to no GUI support
	 */
	public EvaluationBlock () {}

	/**
	 * called for GUI supported command processor
	 * @param environment the application core data access
	 * @param access special access to the value stack
	 */
	public EvaluationBlock
		(
			Environment<T> environment,
			EnvironmentalUtilities.AccessToTopOfStack access
		)
	{
		this.environment = environment;
		this.access = access;
	}
	protected EnvironmentalUtilities.AccessToTopOfStack access;
	protected Environment<T> environment;


	/**
	 * compute the block ID expression
	 * @param tokens the tokens of the command line
	 * @param sym the symbol associated with this block
	 * @return the value computed from the tokens
	 */
	public int getBlockIDValue (CommandSequence tokens, String sym)
	{
		if ((this.loopVariable = environment.getSymbolMap ().get (sym)) == null) return 0;
		T t = environment.getValueManager ().toDiscrete (access.getValue (tokens));
		return environment.getSpaceManager ().toNumber (t).intValue ();
	}


	/**
	 * set block ID symbol to loop value
	 * @param value the current loop value
	 */
	public void setSym (int value)
	{
		//TODO: set symbol table item value
	}
	protected Object loopVariable;


	/**
	 * treat first token of the expression as the ID of the block
	 * @param tokens the tokens of the command line
	 * @return the text of the first token
	 */
	public String getBlockID (CommandSequence tokens)
	{
		String sym = Utilities.imageOf (tokens, 1, null);
		if (sym == null) throw new RuntimeException ("Block ID not found");
		return sym;
	}


	/**
	 * process a start of a conditional block
	 * @param tokens the tokens of the command line
	 */
	public void startConditionalBlock (CommandSequence tokens)
	{
		String sym = getBlockID (tokens);
		int value = getBlockIDValue (tokens, sym) == 0 ? 0 : 1;
		if (value == 0) { this.currently = DISABLED; }
	}
	public void endConditionalBlock () { resetToNormal (); }


	/**
	 * process a start of a counted loop block
	 * @param tokens the tokens of the command line
	 */
	public void startLoopBlock (CommandSequence tokens)
	{
		String sym = getBlockID (tokens);
		this.blockContents = new ArrayList <> ();
		int value = getBlockIDValue (tokens, sym);
		if (value == 0) { this.currently = DISABLED; }
		else if (value > 1) { loop (sym, value); }
	}
	public void endLoopBlock (EvaluationEngine <T> engine)
	{ executeLoop (engine); }


	/**
	 * capture the parameters for a loop block
	 * @param sym name to associate with the new loop block
	 * @param value the repeat count taken from the start command
	 */
	public void loop (String sym, int value)
	{
		this.currently = COMPILE;
		this.loopBlockCount = value;
		this.loopBlockID = sym;
	}
	protected String loopBlockID;
	protected int loopBlockCount;


	/**
	 * process iterations of the loop
	 * @param engine the execution engine to use
	 */
	public void executeLoop (EvaluationEngine <T> engine)
	{
//		System.out.println
//		(
//			"Loop " + loopBlockID +
//			" to execute " + loopBlockCount + " times"
//		);
		resetToNormal ();
		loop (engine);
	}


	/**
	 * check for change of status
	 * @param expected token that would change state
	 * @param tokens current line of tokens
	 * @return TRUE for expected found
	 */
	public boolean checkFor (String expected, List<TokenParser.TokenDescriptor> tokens)
	{
		return expected.equals (tokens.get (0).getTokenImage ());
	}


	/**
	 * given processing state invoke actions
	 * @param tokens the tokens of the command line
	 * @return TRUE for command to be processed
	 */
	public boolean isToBeprocessed (List<TokenParser.TokenDescriptor> tokens)
	{
		if (tokens.size () == 0) return false;

		switch (currently)
		{

			case NORMAL_EXECUTION:

				return true;

			case CONDITIONALLY_DISCARDING:

				return checkFor (END_CONDITION, tokens);

			case BLOCK_QUEUE_ACTIVE:

				if ( ! checkFor (END_LOOP, tokens) )
				{ this.blockContents.add (tokens); }
				else { return true; }

			default:

				break;

		}

		return false;
	}


	/**
	 * execute the queued commands in loop
	 * @param engine the execution engine to use
	 */
	public void loop (EvaluationEngine <T> engine)
	{
		for (int i = this.loopBlockCount; i > 0; i++)
		{
			setSym (i);
			for (int j = 0; j < this.blockContents.size (); j++)
			{ engine.processEnabled (this.blockContents.get (j)); }
		}
	}
	protected ArrayList <List<TokenParser.TokenDescriptor>> blockContents;


	public static final String
		END_LOOP = OperatorNomenclature.END_OF_LOOP_BLOCK_DELIMITER,
		END_CONDITION = OperatorNomenclature.END_OF_CONDITIONAL_BLOCK_DELIMITER
	;


}

