
package net.myorb.math.expressions;

import net.myorb.data.abstractions.SimpleStreamIO.TextSource;

import net.myorb.data.notations.json.JsonPrettyPrinter;
import net.myorb.data.notations.json.JsonSemantics;
import net.myorb.data.notations.json.JsonReader;

import java.util.List;

/**
 * issue commands to server.
 *  results passed back as JSON text.
 *  parse JSON arrays of arrays which are component tuples.
 *  add component to the list which compiles the result values.
 * @param <T> a compound value type with appropriate manager
 * @author Michael Druckman
 */
public class PythonGenericTuplesParser<T>
{


	/**
	 * @param componentSpaceManager must provide management for a structured data type
	 */
	public PythonGenericTuplesParser
		(ExpressionComponentSpaceManager<T> componentSpaceManager)
	{
		this.mgr = componentSpaceManager;
	}
	protected ExpressionComponentSpaceManager<T> mgr;


	/**
	 * source will read a JSON text stream holding array of array nodes
	 * @param source a SimpleStreamIO.TextSource object pointing to socket stream
	 * @param values the list being compiled of values passed back from server
	 * @throws Exception for any IO errors or JSON parser errors
	 */
	public void convert
		(
			TextSource source,
			List<T> values
		)
	throws Exception
	{
		int n;

		JsonSemantics.JsonNumber value;
		JsonSemantics.JsonArray arrayOfArray;
		JsonSemantics.JsonArray components;

		JsonSemantics.JsonValue v = JsonReader.readFrom (source);
		if (TRACE) JsonPrettyPrinter.sendTo (v, System.out);
		arrayOfArray = JsonSemantics.JsonArray.verify (v);
		double [] componentValues = new double[2];

		for (int i = 0; i < arrayOfArray.size (); i++)
		{
			components = JsonSemantics.JsonArray.verify (arrayOfArray.get (i));
			if (componentValues.length != (n = components.size ()))
			{ componentValues = new double [n]; }

			for (int j = 0; j < n; j++)
			{
				value = JsonSemantics.JsonNumber.verify (components.get (j));
				componentValues[j] = value.getNumber ().doubleValue ();
			}

			values.add (mgr.construct (componentValues));
		}
	}
	static boolean TRACE = false;


}

