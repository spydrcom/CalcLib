
package net.myorb.math.expressions;

import net.myorb.data.abstractions.PrimitiveRangeDescription;

/**
 * extend range description with generic type
 * @author Michael Druckman
 */
public class TypedRangeDescription
{

	/**
	 * read values cast as generic type
	 * @param <T> the type for the data
	 */
	public interface TypedRangeProperties<T>
	{
		T getTypedLo ();
		T getTypedIncrement ();
		T getTypedHi ();
	}

	/**
	 * @param <T> the type of range data
	 * @param description a primitive description
	 * @param mgr the type manager for the generic type
	 * @return the typed version of the descriptor
	 */
	public static <T> TypedRangeProperties<T>
	getTypedRangeProperties (PrimitiveRangeDescription description, ExpressionSpaceManager<T> mgr)
	{
		return new TypedFormValueCollection<T>(description, mgr);
	}

	/**
	 * @param <T> data type
	 * @param lo the lo bound
	 * @param hi the hi bound
	 * @param increment the increment
	 * @param mgr the type manager for the generic type
	 * @return the typed version of the descriptor
	 */
	public static <T> TypedRangeProperties<T>
	getTypedRangeProperties (String lo, String hi, String increment, ExpressionSpaceManager<T> mgr)
	{
		return new TypedFormValueCollection<T>(lo, hi, increment, mgr);
	}

	/**
	 * @param <T> data type
	 * @param range a Primitive Range Description
	 * @param mgr the type manager for the generic type
	 * @return the primitive description of the range
	 */
	public static <T> PrimitiveRangeDescription toPrimitiveRangeDescription
	(TypedRangeProperties<T> range, ExpressionSpaceManager<T> mgr)
	{
		return new PrimitiveRangeDescription
		(
			mgr.toNumber (range.getTypedLo ()), mgr.toNumber (range.getTypedHi ()),
			mgr.toNumber (range.getTypedIncrement ())
		);
	}

}

/**
 * provide conversion to generic type
 * @param <T> the type of data in description
 */
class TypedFormValueCollection<T> extends PrimitiveRangeDescription
	implements TypedRangeDescription.TypedRangeProperties<T>
{

	public T getTypedLo () { return mgr.convertFromDouble (getLo ().doubleValue ()); }
	public T getTypedIncrement () { return mgr.convertFromDouble (getIncrement ().doubleValue ()); }
	public T getTypedHi () { return mgr.convertFromDouble (getHi ().doubleValue ()); }
	
	TypedFormValueCollection
	(PrimitiveRangeDescription description, ExpressionSpaceManager<T> mgr)
	{ super (description); this.mgr = mgr; }

	TypedFormValueCollection
	(String lo, String hi, String increment, ExpressionSpaceManager<T> mgr)
	{ super (lo, hi, increment); this.mgr = mgr; }
	protected ExpressionSpaceManager<T> mgr;

}

