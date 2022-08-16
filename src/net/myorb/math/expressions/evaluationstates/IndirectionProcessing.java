
package net.myorb.math.expressions.evaluationstates;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.SymbolMap;

/**
 * indirect access processor
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class IndirectionProcessing <T>
{


	public IndirectionProcessing
	(Environment<T> environment) { this.environment = environment; }
	protected Environment<T> environment;


	/**
	 * process the object of the dereference
	 * @param token the token expected to act as a pointer
	 */
	public void processDereference (TokenParser.TokenDescriptor token)
	{
		environment.setToken (token);
		SymbolMap.Named symbol = environment.lookupImage ();

		if (symbol instanceof SymbolMap.VariableLookup)
		{
			ValueManager.GenericValue value = environment.getSymbolMap ().getValue (symbol);
			
			if (value instanceof ValueManager.IndirectAccess)
			{
				processPointer
				(
					(ValueManager.IndirectAccess) value
				);
				return;
			}
		}

		throw new RuntimeException ("Target of a dereference must be a pointer");
	}


	/**
	 * process procedure parameter
	 * @param pointer the object acting as a pointer
	 */
	public void processPointer (ValueManager.IndirectAccess pointer)
	{
		Object referenced = pointer.getReferenced ();

		if (referenced instanceof ValueManager.Executable)
		{
			processReferenced
			(
				(ValueManager.Executable <?>) referenced
			);
			return;
		}

		throw new RuntimeException ("Lambda function expected but not found");
	}


	/**
	 * process the symbol accessed by a pointer
	 * @param executable the procedure parameter
	 */
	public void processReferenced
	(ValueManager.Executable <?> executable)
	{
		SymbolMap.Named objectReferenced =
			(SymbolMap.Named) executable.getSubroutine ();
		environment.setToken (TokenParser.TokenType.IDN, objectReferenced.getName ());
	}


}
