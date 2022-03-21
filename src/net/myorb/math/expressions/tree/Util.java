
package net.myorb.math.expressions.tree;

import net.myorb.data.notations.json.JsonPrettyPrinter;
import net.myorb.data.notations.json.JsonLowLevel;

public class Util
{

	public static void dump (JsonLowLevel.JsonValue json)
	{
		try
		{
			JsonPrettyPrinter.sendTo
			(
				json, System.out
			);
		} catch (Exception e) {}
	}

}
