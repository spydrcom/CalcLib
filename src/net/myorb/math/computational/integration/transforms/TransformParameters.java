
package net.myorb.math.computational.integration.transforms;

import java.util.HashMap;
import java.util.Map;

/**
 * identify properties of transform consumer objects
 * @author Michael Druckman
 */
public class TransformParameters
{


	/**
	 * identify as transform or inverse
	 */
	public enum NucleusType {KERNEL, INVERSE}

	/**
	 * transform identity based on author
	 */
	public enum TransformKind {FOURIER, HANKEL, HERMITE, HILBERT, JACOBI, LAPLACE, LEGENDRE, LAGUERRE, MELLIN}


	/**
	 * @param configuration map supplied by source configuration statement
	 */
	public TransformParameters (Map<String, Object> configuration)
	{
		(this.configuration = new HashMap<String, Object>()).putAll (configuration);
		this.nucleusType = NucleusType.valueOf (getParameterUC ("nucleus"));
	}


	/**
	 * @param named the name of the parameter
	 * @return a numeric value supplied for the parameter
	 */
	public Number getValue (String named)
	{
		return Double.parseDouble (getParameter (named));
	}


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
	protected Map<String, Object> configuration;


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
		return getParameter ("type");
	}


	/**
	 * @return the kind of transform specified as KIND
	 */
	public TransformKind getKind ()
	{
		String kind = getParameterUC ("kind");
		return TransformKind.valueOf (kind);
	}


	/**
	 * @return KERNEL or INVERSE
	 */
	public NucleusType getNucleusType () { return nucleusType; }


	/**
	 * @return TRUE for inverse, otherwise FALSE
	 */
	public boolean isInverse ()
	{ return nucleusType == NucleusType.INVERSE; }
	protected NucleusType nucleusType;


	/**
	 * @return the name of the parameter of the transform
	 */
	public String getBasis () { return getParameter ("basis"); }


}

