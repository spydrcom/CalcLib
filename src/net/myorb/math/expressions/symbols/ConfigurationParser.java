
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.TokenParser;

import java.util.List;
import java.util.Map;

/**
 * parse configuration parameters from command to map
 * @author Michael Druckman
 */
public class ConfigurationParser
{

	/**
	 * parse configuration parameters from a command line
	 * @param tokens a token list from the command line
	 * @param parameters the configuration map to build
	 */
	public static void configure
		(
			List<TokenParser.TokenDescriptor> tokens,
			Map<String, Object> parameters
		)
	{
		int n = 2;
		String sym, val;
		while (n+1 < tokens.size())
		{
			sym = tokens.get (n++).getTokenImage ();
			val = strip (tokens.get (n++).getTokenImage ());
			parameters.put (sym, val);
		}
	}
	public static String strip (String text) { return TokenParser.stripQuotes (text); }

}
