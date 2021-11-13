
package net.myorb.math.complexnumbers;

import net.myorb.math.SpaceManager;

/**
 * quaternion arithmetic algorithms
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class QuaternionValue<T> extends Arithmetic<T>
{

	/**
	 * quaternion values are constructed based on type manager for components
	 * @param manager the manager for the component type
	 */
	public QuaternionValue
		(SpaceManager<T> manager)
	{
		super (manager);
	}


	/**
	 * construct a quaternion value
	 *  with real and imaginary components
	 * @param realpart the real part of the complex value
	 * @param iPart the imaginary part of the value (i coefficient)
	 * @param jPart the imaginary part of the value (j coefficient)
	 * @param kPart the imaginary part of the value (k coefficient)
	 * @param manager the manager for the component type
	 */
	public QuaternionValue
	(T realpart, T iPart, T jPart, T kPart, SpaceManager<T> manager)
	{
		this (manager);
		this.realpart = realpart;
		this.iPart = iPart; this.jPart = jPart; this.kPart = kPart;
	}
	T realpart, iPart, jPart, kPart;

	/**
	 * construct a quaternion value from integer scalar value
	 * @param scalar the integer value to convert to a quaternion
	 * @param manager the manager for the component type
	 */
	public QuaternionValue (int scalar, SpaceManager<T> manager)
	{
		this (manager.newScalar(scalar), manager.newScalar(0), manager.newScalar(0), manager.newScalar(0), manager);
	}

	/**
	 * construct a quaternion value from generic value
	 * @param value the generic value to convert to a quaternion
	 * @param manager the manager for the component type
	 */
	public QuaternionValue (T value, SpaceManager<T> manager)
	{
		this (value, manager.newScalar(0), manager.newScalar(0), manager.newScalar(0), manager);
	}

	/**
	 * construct new quaternion
	 * @param r the real part of THIS Quaternion
	 * @param i the i part of THIS Quaternion value
	 * @param j the j part of THIS Quaternion value
	 * @param k the k part of THIS Quaternion
	 * @return new quaternion object
	 */
	public QuaternionValue<T> Q (T r, T i, T j, T k)
	{
		return new QuaternionValue<T> (r, i, j, k, manager);
	}

	/**
	 * return conjugate value
	 * @return the computation result
	 */
	public QuaternionValue<T> conjugate ()
	{
		return Q (realpart, neg (iPart), neg (jPart), neg (kPart));
	}

	/**
	 * add this value with parameter term
	 * @param term the value of the term to be added
	 * @return the computation result
	 */
	@SuppressWarnings("unchecked")
	public QuaternionValue<T> plus (QuaternionValue<T> term)
	{
		return Q
		(
			sumOf (this.realpart, term.realpart),
			sumOf (this.iPart, term.iPart),
			sumOf (this.jPart, term.jPart),
			sumOf (this.kPart, term.kPart)
		);		
	}

	/**
	 * sum of squares of components is the norm value squared
	 * @return norm value squared
	 */
	@SuppressWarnings("unchecked")
	public T normSquared ()
	{
		T a = this.realpart, b = this.iPart, c = this.jPart, d = this.kPart;
		return sumOf (X (a, a), X (b, b), X (c, c), X (d, d));
	}

	/**
	 * compute the norm of THIS Quaternion
	 * @return computed norm
	 */
	public T norm ()
	{
		return sqt (normSquared ());
	}

	/**
	 * multiply by a scalar value
	 * @param real the scalar value
	 * @return the product
	 */
	public QuaternionValue<T> times (T real)
	{
		return Q
		(
			X (this.realpart, real), X (this.iPart, real), X (this.jPart, real), X (this.kPart, real)
		);
	}

	/**
	 * compute the versor of THIS Quaternion
	 * @return computed versor
	 */
	public QuaternionValue<T> versor ()
	{
		return this.times (inverted (norm ()));
	}

	/**
	 * compute multiplicitive inverse of THIS Quaternion
	 * @return computed result
	 */
	public QuaternionValue<T> inverted ()
	{
		return conjugate ().times (inverted (normSquared ()));
	}

	/*/
	 * 
		Choose two imaginary quaternions p = b1i + c1j + d1k and q = b2i + c2j + d2k. Their dot product is p * q = b_1*b_2 + c_1*c_2 + d_1*d_2.
		This is equal to the scalar parts of pq*, qp*, p*q, and q*p. (Note that the vector parts of these four products are different.) 
		The cross product of p and q relative to the orientation determined by the ordered basis i, j, and k is
		p X q = (c_1*d_2 - d_1*c_2)i + (d_1*b_2 - b_1*d_2)j + (b_1*c_2 - c_1*b_2)k.
	 * 
	/*/

	/**
	 * add this value with parameter term
	 * @param factor the value of the term to be added
	 * @return the computation result
	 */
	@SuppressWarnings("unchecked")
	public T dotProduct (QuaternionValue<T> factor)
	{
		//T a1 = this.realpart, a2 = factor.realpart;
		T b1 = this.iPart, c1 = this.jPart, d1 = this.kPart;
		T b2 = factor.iPart, c2 = factor.jPart, d2 = factor.kPart;
		return sumOf (X (b1, b2), X (c1, c2), X (d1, d2));		
	}

	/**
	 * add this value with parameter term
	 * @param factor the value of the term to be added
	 * @return the computation result
	 */
	@SuppressWarnings("unchecked")
	public QuaternionValue<T> crossProduct (QuaternionValue<T> factor)
	{
		//T a1 = this.realpart, a2 = factor.realpart;
		T b1 = this.iPart, c1 = this.jPart, d1 = this.kPart;
		T b2 = factor.iPart, c2 = factor.jPart, d2 = factor.kPart;
		return Q
		(
			discrete (0),
			sumOf (X (c1, d2), neg (X (d1, c2))),
			sumOf (X (d1, b2), neg (X (b1, d2))),
			sumOf (X (b1, c2), neg (X (c1, b2)))
		);
	}

	/*/
	 * Hamilton product
	 * (a1 + b1i + c1j + d1k) (a2 + b2i + c2j + d2k)
	 * 
		a_1*a_2 - b_1*b_2 - c_1*c_2 - d_1*d_2
		+ (a_1*b_2 + b_1*a_2 + c_1*d_2 - d_1&c_2)i
		+ (a_1*c_2 - b_1*d_2 + c_1*a_2 + d_1*b_2)j
		+ (a_1*d_2 + b_1*c_2 - c_1*b_2 + d_1*a_2)k.
	 * 
	/*/

	/**
	 * multiply this with the parameter value
	 * @param factor the value to multiply with this value
	 * @return the computation result
	 */
	@SuppressWarnings("unchecked")
	public QuaternionValue<T> hamiltonProduct (QuaternionValue<T> factor)
	{
		T a1 = this.realpart, b1 = this.iPart, c1 = this.jPart, d1 = this.kPart,
			a2 = factor.realpart, b2 = factor.iPart, c2 = factor.jPart, d2 = factor.kPart;
		return Q
		(
			sumOf
			(
				X (a1, a2), neg (X (b1, b2)),
				neg (X (c1, c2)), neg (X (d1, d2))
			),
			sumOf (X (a1, b2), X (b1, a2), X (c1, d2), neg (X (d1, c2))),
			sumOf (X (a1, c2), neg (X (b1, d2)), X (c1, a2), X (d1, b2)),
			sumOf (X (a1, d2), X (b1, c2), neg (X (c1, b2)), X (d1, a2))
		);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		StringBuffer buffer = new StringBuffer ();
		buffer.append (manager.toDecimalString (realpart));
		appendPart (iPart, buffer, "i");
		appendPart (jPart, buffer, "j");
		appendPart (kPart, buffer, "k");
		return buffer.toString ();
	}

}
