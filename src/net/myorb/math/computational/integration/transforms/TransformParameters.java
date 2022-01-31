
package net.myorb.math.computational.integration.transforms;

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
	public enum TransformKind {FOURIER, HANKEL, HERMITE, HILBERT, JACOBI, LAPLACE, LEGENDRE, LAGUERRE}

	/**
	 * Fourier transform sub-types
	 */
	public enum TransformType {COMPLEX, SIN, COS, HARTLEY, BESSEL}


	/**
	 * @param configuration map supplied by source configuration statement
	 */
	public TransformParameters (Map<String, Object> configuration)
	{
		this.configuration = configuration;
		this.nucleusType = NucleusType.valueOf (getParameter ("nucleus").toUpperCase ());
		this.transformType = TransformType.valueOf (getParameter ("type").toUpperCase ());
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
	 * @return the kind of transform specified as KIND
	 */
	public TransformKind getKind ()
	{
		String kind =
			getParameter ("kind").toUpperCase ();
		return TransformKind.valueOf (kind);
	}

	/**
	 * @return the Fourier transform sub-type
	 */
	public TransformType getTransformType () { return transformType; }
	protected TransformType transformType;

	/**
	 * @return KERNEL or INVERSE
	 */
	public NucleusType getNucleusType () { return nucleusType; }
	protected NucleusType nucleusType;


	/**
	 * @return the name of the parameter of the transform
	 */
	public String getBasis () { return getParameter ("basis"); }

	/**
	 * @return TRUE for inverse, otherwise FALSE
	 */
	public boolean isInverse () { return nucleusType == NucleusType.INVERSE; }


}

