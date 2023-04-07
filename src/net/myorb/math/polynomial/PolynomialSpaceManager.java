
package net.myorb.math.polynomial;

import net.myorb.math.Polynomial;

import net.myorb.math.FieldStructure;
import net.myorb.math.SpaceManager;

import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

import net.myorb.data.notations.json.JsonLowLevel.JsonValue;

/**
 * provide description of polynomials as types for algebraic transform
 * @param <T> the types of coefficients in the polynomial terms
 * @author Michael Druckman
 */
public class PolynomialSpaceManager<T> extends OrdinaryPolynomialCalculus<T>
	implements SpaceManager <Polynomial.PowerFunction<T>>
{

	/**
	 * build a management object based on type manager for real/imag fields
	 * @param manager the manager for the type being manipulated
	 */
	public PolynomialSpaceManager (SpaceManager<T> manager)
	{
		super (manager);
		NEGATIVE_ONE = discrete (-1);
		ZERO = discrete (0);
		ONE = discrete (1);
	}
	final T ZERO, NEGATIVE_ONE, ONE;


	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getDataType()
	 */
	public DataType getDataType () { return DataType.System; }


	/**
	 * multiply coefficients list by scalar
	 * @param scalar the value of the scalar
	 * @param coefficients the source list of coefficients
	 * @return the resulting product list
	 */
	public Coefficients<T> times (T scalar, Coefficients<T> coefficients)
	{
		Coefficients<T> newCoefficients = new Coefficients<T> ();
		multiply (scalar, coefficients, newCoefficients);
		return newCoefficients;
	}


	/**
	 * sum two lists of coefficients
	 * @param x left side of the summing equation
	 * @param y right side of the summing equation
	 * @return computed sum
	 */
	public Coefficients<T> add (Coefficients<T> x, Coefficients<T> y)
	{
		Coefficients<T> newCoefficients = new Coefficients<T> ();
		add (x, y, newCoefficients);
		return newCoefficients;
	}


	/**
	 * multiply terms by negative one
	 * @param x the coefficients of a set of polynomial terms
	 * @return the negated coefficients
	 */
	public Coefficients<T> negate (Coefficients<T> x)
	{
		return times (NEGATIVE_ONE, x);
	}


	/**
	 * negate right side of expression and add
	 * @param x the left side of the expression
	 * @param y the right side of the expression
	 * @return the difference
	 */
	public Coefficients<T> subtract (Coefficients<T> x, Coefficients<T> y)
	{
		return add (x, negate (y));
	}


	/**
	 * multiply polynomials based on coefficients
	 * @param x left side coefficients of operation
	 * @param y right side coefficients of operation
	 * @return coefficients of product
	 */
	public Coefficients<T> multiply (Coefficients<T> x, Coefficients<T> y)
	{
		Coefficients<T> intermediate, result = new Coefficients<T> ();
		(intermediate = new Coefficients<T> ()).addAll (x);

		for (T c : y)
		{
			result = add (result, times (c, intermediate));
			intermediate.add (0, ZERO);
		}

		return result;
	}


	/**
	 * power of x
	 * @param x the base polynomial
	 * @param exponent the exponent
	 * @return x^exponent
	 */
	public Coefficients<T> pow (Coefficients<T> x, int exponent)
	{
		if (exponent == 0)
			return newCoefficients (ONE);
		Coefficients<T> results = x;
		for (int n=exponent-1; n>0; n--)
		{
			results = multiply (results, x);
		}
		return results;
	}
	public Polynomial.PowerFunction<T> pow (Polynomial.PowerFunction<T> x, int exponent)
	{
		return getPolynomialFunction (pow (x.getCoefficients(), exponent));
	}


	/**
	 * multiply polynomial by scalar
	 * @param scalar the value of the scalar
	 * @param f the function being multiplied
	 * @return the resulting function
	 */
	public Polynomial.PowerFunction<T> times (T scalar, Polynomial.PowerFunction<T> f)
	{
		return getPolynomialFunction (times (scalar, f.getCoefficients ()));
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#negate(java.lang.Object)
	 */
	public Polynomial.PowerFunction<T> negate (Polynomial.PowerFunction<T> x) 
	{ return times (NEGATIVE_ONE, x); }


	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#add(java.lang.Object, java.lang.Object)
	 */
	public Polynomial.PowerFunction<T> add (Polynomial.PowerFunction<T> x, Polynomial.PowerFunction<T> y)
	{
		Coefficients<T> newCoefficients =
			add (x.getCoefficients (), y.getCoefficients ());
		return getPolynomialFunction (newCoefficients);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#multiply(java.lang.Object, java.lang.Object)
	 */
	public Polynomial.PowerFunction<T> multiply (Polynomial.PowerFunction<T> x, Polynomial.PowerFunction<T> y)
	{ return getPolynomialFunction (multiply (x.getCoefficients (), y.getCoefficients ())); }


	/**
	 * @param coefficient the coefficient for this term
	 * @param x the function that represents the polynomial variable
	 * @param degree the degree (exponent) for this term
	 * @return the computed term function
	 */
	public Polynomial.PowerFunction<T> termFor (T coefficient, Polynomial.PowerFunction<T> x, int degree)
	{
		return times (coefficient, pow (x, degree));
	}


	/**
	 * @param coefficient the coefficient for this term
	 * @param x the function that represents the polynomial variable
	 * @param degree the degree (exponent) for this term
	 * @param to the running sum of the terms
	 * @return sum with new term added
	 */
	public Polynomial.PowerFunction<T> addTermFor
	(T coefficient, Polynomial.PowerFunction<T> x, int degree, Polynomial.PowerFunction<T> to)
	{
		return add (to, termFor (coefficient, x, degree));
	}


	/**
	 * compute quotient
	 *  of division of two polynomials
	 * @param x numerator of the division
	 * @param y denominator of the division
	 * @param returnedRemainder numerator of the remainder
	 * @return the quotient function
	 */
	public Polynomial.PowerFunction<T> divide
		(
			Polynomial.PowerFunction<T> x, Polynomial.PowerFunction<T> y,	// computation will be x/y
			Polynomial.PowerFunction<T> returnedRemainder					// parameter will be updated to reflect remainder of division
		)
	{
		Coefficients<T> qot = new Coefficients<T> (),						// building list of coefficients as quotient result
			num = x.getCoefficients (), den = y.getCoefficients (),			// numerator and denominator coefficients taken from parameters
			rem = new Coefficients<T> (), mul = new Coefficients<T> ();		// remainder and multiplier coefficient lists, lists begin as empty
		int numOrder = num.size () - 1, denOrder = den.size () - 1;			// order of numerator and denominator, highest exponent of X in polynomial
		int qotOrder = buildMultiplier (numOrder - denOrder, mul);			// polynomial order of the quotient, difference between num & denom
		T denomMagnitude = manager.invert (den.get (denOrder));				// denominator magnitude is constant, always the high coefficient
		rem.addAll (num);													// remainder starts equivalent to numerator
		
		int mLast, rLast; T ratio;
		for (int i = qotOrder; i >= 0; i--)
		{
			mLast = mul.size () - 1; rLast = rem.size () - 1;
			ratio = nextQuotientTerm (qot, rem, denomMagnitude, rLast);		// compute next quotient term
			rem = reduceRemainder (mul, mLast, rem, rLast, den, ratio);		// reduce remainder by term value
		}

		returnRemainder (returnedRemainder, rem);							// pass back remainder
		return getPolynomialFunction (qot);									// quotient function
	}


	/**
	 * modify coefficients object of output parameter
	 *  to match value of coefficients of remainder of division
	 * @param outputParameter the function passed in to be made to represent remainder
	 * @param remainderCoefficients the computed remainder
	 */
	public void returnRemainder
		(
			Polynomial.PowerFunction<T> outputParameter,
			Coefficients<T> remainderCoefficients
		)
	{
		Coefficients<T>
			r = outputParameter.getCoefficients ();							// coefficients connected to output parameter function
		r.clear (); r.addAll (remainderCoefficients);						// set to match remainder of all term reductions
	}


	/**
	 * build the multiplier polynomial object
	 * @param order the order of the quotient polynomial
	 * @param coefficients the coefficients list to be used as multiplier
	 * @return the order of the quotient
	 */
	public int buildMultiplier
	(int order, Coefficients<T> coefficients)
	{
		for (int i = 0; i <= order; i++)									// zero out multiplier object
			coefficients.add (ZERO);										// size is set to match quotient order
		return order;														// size always maintained appropriate to quotient
	}


	/**
	 * compute value that will reduce high order term
	 * @param qot the coefficients list collecting quotient terms
	 * @param rem the coefficients list collecting remainder terms
	 * @param denomMagnitude the high order term magnitude of the denominator
	 * @param rLast the element number of the last remainder term
	 * @return the ratio of high order remainder to denominator
	 */
	public T nextQuotientTerm
		(
			Coefficients<T> qot,
			Coefficients<T> rem,
			T denomMagnitude,
			int rLast
		)
	{
		T ratio = manager.multiply
			(rem.get (rLast), denomMagnitude);							// compute ratio of remainder to denominator
		qot.add (0, ratio);												// capture ratio as lead portion of quotient
		return ratio;
	}


	/**
	 * reduce remainder by factor of denominator
	 * @param mul the multiplier sized to appropriate term
	 * @param mLast the last element number in the multiplier
	 * @param rem the full remainder having been reduced by previous terms
	 * @param rLast the last element number in the remainder to be removed
	 * @param den the denominator to be multiplied for the reduction
	 * @param ratio the most recent quotient term value
	 * @return the new remainder value
	 */
	public Coefficients<T> reduceRemainder
		(
			Coefficients<T> mul, int mLast,								// the multiplier and its last element number
			Coefficients<T> rem, int rLast,								// the remainder and its last element number
			Coefficients<T> den,										// the denominator of the division
			T ratio														// the most recent quotient term
		)
	{
		mul.set (mLast, ratio);											// ratio becomes high order multiplier term
		Coefficients<T> newRemainder =
			subtract (rem, multiply (mul, den));						// subtract off multiple of denominator
		mul.remove (mLast); newRemainder.remove (rLast); 				// reduce multiplier and remainder
		return newRemainder;
	}



	/*
	 * pretty print for polynomial representations
	 * using standard software expression conventions for
	 * addition{+}, multiplication{*}, and exponentiation {^}
	 * attempts are made to reduce constants to smallest type
	 */


	/**
	 * build standard polynomial description
	 * @param function the function being described
	 * @return a text representation of the function
	 */
	public String toString
	(Polynomial.PowerFunction<T> function)
	{
		int termNo = 0;
		manager.setDisplayPrecision (5);				// reduce constant display size so term relevance is obvious
		StringBuffer buffer = new StringBuffer ();
		for (T c : function.getCoefficients ())
		{
			if (!isConstantZero (c))					// term with 0 coefficient is kept from display as having been canceled
			{ formatTerm (termNo, c, buffer); }
			termNo++;
		}
		manager.resetDisplayPrecision ();				// return to full precision
		if (buffer.length() == 0) return "0";			// for constant zero
		return buffer.toString ();
	}
	boolean isConstantZero (T c)
	{
		if (isZro (c)) return true;
		if (isNeg (c)) c = neg (c);
		return "0".equals (format (c));
	}


	/**
	 * format multiplication and exponentiation portion of term
	 * @param termNo number of the term (same as exponent of X on term)
	 * @param c value of coefficient of the term being formatted
	 * @param buffer the buffer collecting the formatted text
	 */
	public void formatTerm (int termNo, T c, StringBuffer buffer)
	{
		boolean cIs1 =
			formatTermOperation (c, termNo, buffer);
		if (termNo > 0)
		{
			if (!cIs1) buffer.append (" * "); buffer.append ("x");
			if (termNo > 1) buffer.append ("^").append (termNo);
		}
	}


	/**
	 * optimize term operator display
	 *  addition of negative multiplier suppressed
	 *  (... + -3 * x same as ... - 3 * x), optimal form used
	 * @param c value of coefficient of the term being formatted
	 * @param termNo number of the term (same as exponent of X on term)
	 * @param buffer the buffer collecting the formatted text
	 * @return TRUE = constant found to be 1
	 */
	public boolean formatTermOperation (T c, int termNo, StringBuffer buffer)
	{
		boolean cIs1 = false, expIs0 = termNo == 0; // exp==0 || c==1 => optimization
		if (isNeg (c)) { buffer.append (" - "); cIs1 = formatValue (neg (c), expIs0, buffer); }				// change operator for sign
		else { if (buffer.length () > 0) buffer.append (" + "); cIs1 = formatValue (c, expIs0, buffer); }	// standard addition, no signed constant
		return cIs1; // TRUE => constant found to be 1
	}


	/**
	 * prevent display of one (1) as multiplier
	 * @param c the value of the constant to be formatted
	 * @param expIs0 TRUE = value of exponent of term is 0 (x^0 = 1, so c=1 must be displayed)
	 * @param buffer the buffer collecting the formatted text
	 * @return TRUE = constant is 1 and should be optimized
	 */
	public boolean formatValue (T c, boolean expIs0, StringBuffer buffer)
	{
		boolean cIs1 = isZro (subtract (c, ONE));
		if (expIs0 || !cIs1) { buffer.append (format (c)); }
		return cIs1;
	}


	/**
	 * reduce constant from float to integer where possible
	 * @param c the value of the constant to be formatted
	 * @return the text of the formatted constant
	 */
	public String format (T c)
	{
		try
		{
			Number n = manager.toNumber (c);
			double d = n.doubleValue (); int i = n.intValue ();
			if (d == i) return Integer.toString (i);
		} catch (Exception e) { /* errors ignored */ }
		return manager.toDecimalString (c);
	}


	/**
	 * display polynomial to console
	 * @param function the polynomial encapsulated as a function
	 */
	public void show (Polynomial.PowerFunction<T> function)
	{ System.out.println (toString (function)); }

	public String dump (Polynomial.PowerFunction<T> function)
	{ return function.getCoefficients ().toString (); }



	/*
	 * space manager interface implementation
	 */


	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#isZero(java.lang.Object)
	 */
	public boolean isZero (Polynomial.PowerFunction<T> x)
	{
		// check all coefficients for zero
		return isConstant (x.getCoefficients (), ZERO);							// all coefficients are zero
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#newScalar(int)
	 */
	public Polynomial.PowerFunction<T> newScalar (int x)
	{
		return constantFunction (discrete (x));									// constant function, returns identified value
	}

	public Polynomial.PowerFunction<T> constantFunction (T c)
	{
		return getPolynomialFunction (newCoefficients (c));
	}

	/**
	 * @return function where y = x
	 */
	public Polynomial.PowerFunction<T> newVariable ()
	{
		return getPolynomialFunction (newCoefficients (ZERO, ONE));
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#getZero()
	 */
	public Polynomial.PowerFunction<T> getZero () { return newScalar (0); }		// constant function, returns 0


	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getOne()
	 */
	public Polynomial.PowerFunction<T> getOne () { return newScalar (1); }		// constant function, returns 1


	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getName()
	 */
	public String getName () { return "Polynomial Equations"; }


	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#getComponentManager()
	 */
	@SuppressWarnings("rawtypes")
	public SpaceManager getComponentManager () { return manager; }				// a manager for the coefficient type



	/*
	 * the function representation is unable to describe polynomial multiplicative inverses,
	 * for this reason no implementation of the field description is offered
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getFieldStructure()
	 */
	public FieldStructure<Polynomial.PowerFunction<T>> getFieldStructure ()
	{ raiseException ("PowerFunction does not describe a field"); return null; }



	/*
	 * polynomials not being direct representatives of number systems
	 * are not meaningful for certain portions of the space interface
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#conjugate(java.lang.Object)
	 */
	public Polynomial.PowerFunction<T> conjugate (Polynomial.PowerFunction<T> x) { return x; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#isNegative(java.lang.Object)
	 */
	public boolean isNegative (Polynomial.PowerFunction<T> x) { return false; } // order not meaningful

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#invert(java.lang.Object)
	 */
	public Polynomial.PowerFunction<T> invert (Polynomial.PowerFunction<T> x) 
	{ throw new RuntimeException ("inversion requested for power function"); }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#lessThan(java.lang.Object, java.lang.Object)
	 */
	public boolean lessThan (Polynomial.PowerFunction<T> x, Polynomial.PowerFunction<T> y)
	{ throw new RuntimeException ("lessThan requested for power function"); }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#toDecimalString(java.lang.Object)
	 */
	public String toDecimalString (Polynomial.PowerFunction<T> x) { return null; }

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#setDisplayPrecision(int)
	 */
	public void setDisplayPrecision (int digits) {}

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#resetDisplayPrecision()
	 */
	public void resetDisplayPrecision () {}

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#toInternalString(java.lang.Object)
	 */
	public String toInternalString (Polynomial.PowerFunction<T> x) { return null; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#getPi()
	 */
	public Polynomial.PowerFunction<T> getPi ()
	{ throw new RuntimeException ("PI requested for power function"); }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#toNumber(java.lang.Object)
	 */
	public Number toNumber (Polynomial.PowerFunction<T> x)
	{ throw new RuntimeException ("polynomial not representable as Number"); }


	/**
	 * build a basic polynomial manager based on double floating real components
	 * @return a Polynomial field manager based on Double components
	 */
	public static PolynomialSpaceManager<Double> newInstance ()
	{ return new PolynomialSpaceManager<Double> (new DoubleFloatingFieldManager ()); }


	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getEmptyArray()
	 */
	@SuppressWarnings("unchecked")
	public Polynomial.PowerFunction<T>[] getEmptyArray ()
	{
		return new Polynomial.PowerFunction[]{};
	}


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Portable.AsJson#toJson(java.lang.Object)
	 */
	public JsonValue toJson (Polynomial.PowerFunction<T> from)
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Portable.AsJson#fromJson(net.myorb.data.notations.json.JsonLowLevel.JsonValue)
	 */
	public Polynomial.PowerFunction<T> fromJson (JsonValue representation)
	{
		return null;
	}

}

