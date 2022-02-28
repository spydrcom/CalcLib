
package net.myorb.math.computational;

import java.util.HashMap;
import java.util.Map;

/**
 * general base class for configuration parameter management
 * @author Michael Druckman
 */
public class Parameterization
{


	/**
	 * the description of the means of construction of an object
	 */
	public static class Hash extends HashMap<String, Object>
	{

		public Hash () {}

		public Hash
			(
				String symbol, String type,
				Class<?> representing, Map<String, Object> using
			)
		{
			processConfiguration (symbol, type, representing, using);
		}

		/**
		 * @param symbol the name of this described object
		 * @param type the name for the type of structure being described
		 * @param representing the class-path to the implementing object
		 * @param using the hash of associated parameters
		 */
		public void processConfiguration
			(
				String symbol, String type, Class<?> representing,
				Map<String, Object> using
			)
		{
			put (type, representing.getCanonicalName ());
			put ("SYMBOL", symbol);
			putAll (using);
		}

		private static final long serialVersionUID = -3302171696728243794L;
	}

	/**
	 * @param configuration the parameter hash for the transform
	 * @return a new hash with elements copied
	 */
	public static Hash copy (Map<String, Object> configuration)
	{
		Hash copied = new Hash();
		copied.putAll (configuration);
		return copied;
	}


	/**
	 * @param configuration map supplied by source configuration statement
	 */
	public Parameterization (Map<String, Object> configuration)
	{
		(this.configuration = new HashMap<String, Object>()).putAll (configuration);
	}
	public Parameterization () { this.configuration = new HashMap<String, Object>(); }
	protected Map<String, Object> configuration;


	public void set (String parameter, Object value) { configuration.put (parameter, value); }


	/**
	 * @param named the name of the parameter
	 * @return NULL if not found, otherwise text of specified
	 */
	public String getParameter (String named)
	{
		Object parameter = configuration.get (named);
		if (parameter == null) return null;
		return parameter.toString ();
	}


	/**
	 * @param named the name of the parameter
	 * @return UC text of named parameter or NULL if null
	 */
	public String getParameterUC (String named)
	{
		String p = getParameter (named);
		return p==null? null: p.toUpperCase ();
	}


	/**
	 * @return value of type parameter
	 */
	public String getType ()
	{
		return getParameterUC ("type");
	}


	/**
	 * @return the kind of transform specified as KIND
	 */
	public String getKind ()
	{
		return getParameterUC ("kind");
	}


	/**
	 * @return the kind of transform specified as METHOD
	 */
	public String getMethood ()
	{
		return getParameterUC ("method");
	}


	/**
	 * @param named the name of the parameter
	 * @return a numeric value supplied for the parameter
	 */
	public Number getValue (String named)
	{
		String p = getParameter (named);
		if (p == null) return null;
		return Double.parseDouble (p);
	}


	/**
	 * @param named the name of the parameter
	 * @param defaultValue a default to use absent specification
	 * @return the value chosen for use
	 */
	public Number getValue (String named, Number defaultValue)
	{
		Number specified = getValue (named);
		if (specified == null) return defaultValue;
		return specified;
	}


}

