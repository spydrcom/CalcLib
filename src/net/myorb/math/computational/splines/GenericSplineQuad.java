
package net.myorb.math.computational.splines;

import net.myorb.math.computational.integration.RealDomainIntegration;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.tree.RangeNodeDigest;
import net.myorb.math.expressions.SymbolMap;

import java.util.Set;

/**
 * a helper for building quadrature consumer objects 
 * @author Michael Druckman
 */
public class GenericSplineQuad
{


	/**
	 * allow integrand access to integral implementer
	 * @param <T> data type being processed
	 */
	public interface AccessToTarget<T>
	{
		/**
		 * @return the digest describing the integrand
		 */
		RangeNodeDigest<T> getTargetAccess ();
	}


	/**
	 * identify the spline symbol in the digest
	 * @param ids the set of identifiers connected to the target
	 * @param environment session descriptor for this application instance
	 * @return the spline object found to be the target of the integration request
	 * @param <T> data type being processed
	 */
	public static <T> RealDomainIntegration<T> findSymbol (Set<String> ids, Environment<T> environment)
	{
		RealDomainIntegration<T> quad = findSymbol (ids, environment.getSymbolMap ());
		if (quad == null) throw new RuntimeException ("No spline found for integral");
		return quad;
	}


	/**
	 * search the environment symbol map for the IDs in the digest.
	 *  any spline object found in the named objects list will be used
	 * @param ids the set of identifiers found to be connected to that target
	 * @param symbols the symbol map for the session found in the environment object
	 * @return the spline object found to be the target of the integration request
	 * @param <T> data type being processed
	 */
	@SuppressWarnings("unchecked")
	public static <T> RealDomainIntegration<T> findSymbol (Set<String> ids, SymbolMap symbols)
	{
		for (String id : ids)
		{
			Object symbol = symbols.get (id);
			if (symbol instanceof RealDomainIntegration)
			{
				return (RealDomainIntegration<T>) symbol;
			}
		}
		return null;
	}


	/**
	 * collect the identifiers used in the target of the integration request
	 * @param digest the digest describing the integrand of the integration request
	 * @return the set of identifiers connected to that target
	 * @param <T> data type being processed
	 */
	public static <T> Set<String> connectIntegral (RangeNodeDigest<T> digest)
	{ return digest.getTargetExpression ().getIdentifiers (); }


	/**
	 * process the target node of the integral request
	 * @param access object with access to target containing spline reference
	 * @return the set of identifiers connected to that target
	 * @param <T> data type being processed
	 */
	public static <T> Set<String> connect (AccessToTarget<T> access)
	{ return connectIntegral (access.getTargetAccess ()); }


}

