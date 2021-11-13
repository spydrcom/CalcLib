
package net.myorb.math.computational;

import net.myorb.math.Polynomial;
import net.myorb.math.polynomial.families.ChebyshevPolynomial;
import net.myorb.math.SpaceManager;

import net.myorb.data.abstractions.DataSequence;

import java.util.List;

/**
 * Vandermonde matrix for interpolation producing Chebyshev polynomials
 * @param <T> the types of coefficients in the polynomial terms
 * @author Michael Druckman
 */
public class VandermondeChebyshev<T> extends Vandermonde<T>
{

	/**
	 * prepare matrix based on data sequence source
	 * @param dataSequence the data sequence to be crossed with T functions
	 * @param manager the data type manager
	 */
	public VandermondeChebyshev
	(DataSequence<T> dataSequence, SpaceManager<T> manager)
	{
		super (dataSequence, manager);
		populate (dataSequence, new ChebyshevPolynomial<T> (manager));
	}

	/**
	 * building a cross product of T functions against the data sequence
	 * @param dataSequence the data sequence being interpolated
	 * @param p a Chebyshev polynomial manager
	 */
	public void populate
	(DataSequence<T> dataSequence, ChebyshevPolynomial<T> p)
	{
		populate (dataSequence, p.getT (dataSequence.size () - 1));
	}

	/**
	 * building a cross product of T functions against the data sequence
	 * @param dataSequence the data sequence being interpolated
	 * @param Ts the Chebyshev T functions ordered by index
	 */
	public void populate
	(DataSequence<T> dataSequence, List<Polynomial.PowerFunction<T>> Ts)
	{
		int r = 1;
		for (T x : dataSequence)
		{
			int c = 1;
			for (Polynomial.PowerFunction<T> Tn : Ts)
			{ set (r, c++, Tn.eval (x)); }
			r++;
		}
	}

}

