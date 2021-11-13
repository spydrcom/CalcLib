
package net.myorb.math.expressions.evaluationstates;

import net.myorb.math.expressions.ConventionalNotations;
import net.myorb.data.abstractions.Parameters;

import java.util.List;

/**
 * connect notation logic to parameter lists
 * @author Michael Druckman
 */
public class ParameterListDescription extends Parameters
{

	public ParameterListDescription () {}
	public ParameterListDescription (String name) { super (name); }
	public ParameterListDescription (List <String> names) { super (names); }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Parameters#formatParameter(java.lang.String, boolean)
	 */
	public String formatParameter (String name, boolean withNotations)
	{
		return withNotations? ConventionalNotations.determineNotationFor (name): name;
	}

	private static final long serialVersionUID = -4952642427508297281L;

}
