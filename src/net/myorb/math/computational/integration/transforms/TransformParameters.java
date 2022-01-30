
package net.myorb.math.computational.integration.transforms;

import java.util.Map;

/**
 * identify properties of transform consumer objects
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class TransformParameters
{


	public enum NucleusType {KERNEL, INVERSE}
	public enum TransformKind {FOURIER, HANKEL, HERMITE, HILBERT, JACOBI, LAPLACE, LEGENDRE, LAGUERRE}
	public enum TransformType {COMPLEX, SIN, COS, HARTLEY, BESSEL}


	public TransformParameters (Map<String, Object> configuration)
	{
		this.configuration = configuration;
		this.nucleusType = NucleusType.valueOf (getParameter ("nucleus").toUpperCase ());
		this.transformType = TransformType.valueOf (getParameter ("type").toUpperCase ());
	}


	public Number getValue (String named)
	{
		return Double.parseDouble (getParameter (named));
	}

	public String getParameter (String named)
	{
		Object parameter = configuration.get (named);
		if (parameter == null) return null;
		return parameter.toString ();
	}
	protected Map<String, Object> configuration;


	public TransformKind getKind ()
	{
		String kind =
			getParameter ("kind").toUpperCase ();
		return TransformKind.valueOf (kind);
	}

	public TransformType getTransformType () { return transformType; }
	protected TransformType transformType;

	public NucleusType getNucleusType () { return nucleusType; }
	protected NucleusType nucleusType;


	public String getBasis () { return getParameter ("basis"); }

	public boolean isInverse () { return nucleusType == NucleusType.INVERSE; }


}
