
package net.myorb.math.complexnumbers;

import net.myorb.math.expressions.PythonGenericTuplesParser;
import net.myorb.math.expressions.managers.ExpressionComplexFieldManager;

/**
 * issue commands to server.
 *  results passed back as JSON text.
 *  parse JSON arrays of array pairs which are RE/IM pairs.
 *  add Complex values to the list which compiles the values.
 * @author Michael Druckman
 */
public class PythonComplexTuplesParser
	extends PythonGenericTuplesParser<ComplexValue<Double>>
{
	public PythonComplexTuplesParser ()
	{
		super (new ExpressionComplexFieldManager ());
	}
}
