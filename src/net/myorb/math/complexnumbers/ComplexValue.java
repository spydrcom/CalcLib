
package net.myorb.math.complexnumbers;

import net.myorb.math.SpaceManager;

/**
 * complex arithmetic algorithms
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class ComplexValue<T> extends Arithmetic<T>
	implements ComplexMarker
{

	/**
	 * complex values are constructed based on type manager for components
	 * @param manager the manager for the component type
	 */
	public ComplexValue
		(SpaceManager<T> manager)
	{
		super (manager);
	}

	/**
	 * construct a complex value with real and imaginary components
	 * @param realpart the real part of the complex value
	 * @param imagpart the imaginary part of the value
	 * @param manager the component type manager
	 */
	public ComplexValue (T realpart, T imagpart, SpaceManager<T> manager)
	{
		this (manager);
		this.realpart = realpart;
		this.imagpart = imagpart;
	}
	protected T realpart, imagpart;

	/**
	 * construct a complex value from integer scalar value
	 * @param scalar the integer value to convert to a complex pair
	 * @param manager the manager for the component type
	 */
	public ComplexValue (int scalar, SpaceManager<T> manager)
	{
		this (manager.newScalar (scalar), manager.newScalar (0), manager);
	}

	/**
	 * construct a complex value from generic value
	 * @param value the generic value to convert to a complex pair
	 * @param manager the manager for the component type
	 */
	public ComplexValue (T value, SpaceManager<T> manager)
	{
		this (value, manager.newScalar(0), manager);
	}

	public T Re () { return realpart; }
	public T Im () { return imagpart; }

	/**
	 * is this value zero
	 * @return TRUE if this has zero value
	 */
	public boolean isZero ()
	{ return isZro (realpart) && isZro (imagpart); }

	/**
	 * determine if THIS value is complex or real
	 * @return TRUE = complex
	 */
	public boolean isImaginary ()
	{
		if (manager.isZero (realpart))
		{
			if (manager.isZero (imagpart)) return false;
		}
		else
		{
			T tangent = manager.multiply (imagpart, manager.invert (realpart));
			if (forValue (tangent).isLessThan (forValue (10000000).inverted ())) return false;
		}
		return true;
	}

	/**
	 * return value with opposite sign
	 * @return the computation result
	 */
	public ComplexValue<T> negate ()
	{
		return C
		(
			neg (Re ()),
			neg (Im ())
		);
	}

	/**
	 * absolute value
	 * @return abs(Re) for Im==0, otherwise magnitude
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ComplexValue<T> abs ()
	{
		if (!manager.isZero (imagpart))
			return new ComplexValue (magnitude (), 0.0, manager);
		else if (manager.isNegative (realpart))
			return new ComplexValue (manager.negate (realpart), 0.0, manager);
		else return this;
	}

	/**
	 * return conjugate value 
	 * @return the computation result
	 */
	public ComplexValue<T> conjugate ()
	{
		return C
		(
			Re (),
			neg (Im ())
		);
	}

	/**
	 * add this value with parameter term
	 * @param term the value of the term to be added
	 * @return the computed result
	 */
	@SuppressWarnings("unchecked")
	public ComplexValue<T> plus (ComplexValue<T> term)
	{
		return C
		(
			sumOf (this.Re (), term.Re ()),
			sumOf (this.Im (), term.Im ())
		);		
	}

	/**
	 * add this value with negated parameter term
	 * @param term the value of the term to be subtracted
	 * @return the computed result
	 */
	public ComplexValue<T> minus (ComplexValue<T> term)
	{
		return this.plus (term.negate ());
	}

	/**
	 * raise this to integer power
	 * @param n the value of the exponent
	 * @return the computed result
	 */
	public ComplexValue<T> toThe (int n)
	{
		if (n == 0) return new ComplexValue<T> (1, manager);
		else if (n < 0) return this.toThe (-n).inverted ();
		else if (n == 2) return this.squared ();
		else if (n == 1) return this;
		else
		{
			ComplexValue<T> product = this;
			for (int i = n; i > 1; i--) product = product.times (this);
			return product;
		}
	}

	/**
	 * simple case for x^2 = x * x
	 * @return the computed result
	 */
	public ComplexValue<T> squared ()
	{
		return this.times (this);
	}

	/**
	 * compute modulus squared (Re^2 + Im^2) of THIS value.
	 *  keeping squared function separate from sqrt version is useful.
	 *  i.e. ln(sqrt(x)) = ln(x)/2 and division is cheaper than sqrt (see arg(z))
	 * @return computed magnitude squared
	 */
	public T modSquared ()
	{
		return this.times (conjugate ()).Re ();
	}

	/**
	 * compute modulus of THIS value
	 * @return computed modulus
	 */
	public T magnitude ()
	{
		return sqt (modSquared ());
	}

	/**
	 * magnitude and modulus are equivalent
	 * @return the magnitude value
	 */
	public T modulus () { return magnitude (); }

	/**
	 * invert this value (1/this)
	 * @return the computation result
	 */
	public ComplexValue<T> inverted ()
	{
		return conjugate ().multiplyBy (inverted (modSquared ()));
	}

	/**
	 * multiply this with the scalar parameter value
	 * @param factor the scalar parameter value
	 * @return the computation result
	 */
	public ComplexValue<T> multiplyBy (T factor)
	{
		return C
		(
			X (Re (), factor),
			X (Im (), factor)
		);
	}

	/**
	 * multiply this with the parameter value
	 * @param factor the value to multiply with this value
	 * @return the computation result
	 */
	@SuppressWarnings("unchecked")
	public ComplexValue<T> times (ComplexValue<T> factor)
	{
		T a =  this.Re (),  b = this.Im ();
		T c = factor.Re (), d = factor.Im ();

		/*
		 * (a+bi) * (c+di)
		 * a*c - b*d + (b*c + a*d)*i
		 */
		T r = sumOf
			(
				X (a, c),
				neg (X (b, d))
			);
		T i = sumOf
			(
				X (b, c),
				X (a, d)
			);
		return C (r, i);
	}

	/**
	 * multiply complex value by real scalar
	 * @param factor scalar to use as real factor
	 * @return multiplication product
	 */
	public ComplexValue<T> times (T factor)
	{
		T r =  this.Re (),  i = this.Im ();
		return C ( X (r, factor), X (i, factor) );
	}

	/**
	 * multiply this with the inversion of the parameter
	 * @param divisor the value to use as the divisor of the computation
	 * @return the computation result
	 */
	@SuppressWarnings("unchecked")
	public ComplexValue<T> divideBy
		(ComplexValue<T> divisor)
	{
		T a =   this.Re (),  b = this.Im ();
		T c = divisor.Re (), d = divisor.Im ();

		ComplexValue<T> z =
			C (sumOf (X (a, c), X (b, d)), sumOf (X (b, c), neg (X (a, d))));
		return z.multiplyBy (inverted (divisor.modSquared ()));
	}

	/**
	 * compute SQRT of THIS complex value
	 * @return computed result
	 */
	@SuppressWarnings("unchecked")
	public ComplexValue<T> csqrt ()
	{
		T half = inverted (real (2));
		T a = this.Re (), b = this.Im (), m = modulus ();
		T real = X (sumOf (m, a), half), imag = X (sumOf (m, neg (a)), half);
		// sqrt(z) = (sqrt((abs(z) + Re(z)) / 2), sqn (Im(z)) * sqrt((abs(z) - Re(z)) / 2))
		// this is protection against round-off (from sqt(abs(x))) error which has caused the parameter to SQT to be negative
		real = isNeg (real)? manager.getZero (): sqt (real); imag = isNeg (imag)? manager.getZero (): sqt (imag);
		return C (real, X (signum (b), imag));
	}
	T signum (T source) { return !manager.isNegative(source)? manager.getOne (): manager.newScalar (-1); }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		T r = Re (), i = Im ();
		StringBuffer buffer = new StringBuffer ();
		if (manager.isZero (i))
		{
			buffer.append (manager.toDecimalString (r));
		}
		else if (manager.isZero (r))
		{
			appendPart (i, buffer, "i");
		}
		else
		{
			buffer.append ("(");
			buffer.append (manager.toDecimalString (r));
			appendPart (i, buffer, "i");
			buffer.append (")");
		}
		return buffer.toString ();
	}

	public String toPrecisionString (int digits)
	{
		manager.setDisplayPrecision (digits); String result = toString ();
		manager.resetDisplayPrecision (); return result;
	}

}

