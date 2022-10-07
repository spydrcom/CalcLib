
package net.myorb.math.complexnumbers;

import net.myorb.math.expressions.algorithms.CyclicAndPowerLibrary;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.OptimizedMathLibrary;
import net.myorb.math.PowerPrimitives;
import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

import net.myorb.data.abstractions.SpaceConversion;

/**
 * library of complex mathematics algorithms
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class ComplexLibrary<T> extends Arithmetic<T>
		implements CyclicAndPowerLibrary<ComplexValue<T>>
{

	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 * @param conversion a conversion object for double to T
	 */
	public ComplexLibrary
	(SpaceManager<T> manager, SpaceConversion<ComplexValue<T>> conversion)
	{
		super (manager);
		this.complexmanager = new ComplexFieldManager<T> (manager);
		this.conversion = conversion;
	}
	protected SpaceConversion<ComplexValue<T>> conversion;
	protected ComplexFieldManager<T> complexmanager;

	@SuppressWarnings("unchecked")
	public ComplexLibrary (Environment<ComplexValue<T>> environment)
	{
		this
		(
			environment.getSpaceManager ().getComponentManager (),
			environment.getSpaceManager ()
		);
	}

	/**
	 * build a complex value from
	 *  real and imaginary components
	 * @param r real component value
	 * @param i imaginary component
	 * @return new value object
	 */
	public ComplexValue<T> C (T r, T i)
	{
		return new ComplexValue<T> (r, i, manager);
	}

	/**
	 * build a complex value
	 *  from real only component
	 * @param r real component value
	 * @return new value object
	 */
	public ComplexValue<T> C (T r)
	{
		return new ComplexValue<T> (r, discrete (0), manager);
	}

	/**
	 * build a complex value from real only component
	 * @param r integral real component value
	 * @return new value object
	 */
	public ComplexValue<T> C (int r)
	{
		return new ComplexValue<T> (real (r), discrete (0), manager);
	}

	/**
	 * build a complex value from imaginary only component
	 * @param i factor of i to be used
	 * @return new value object
	 */
	public ComplexValue<T> I (T i)
	{
		return new ComplexValue<T> (discrete (0), i, manager);
	}

	/**
	 * represent 0 + 1i
	 * @return new value object
	 */
	public ComplexValue<T> getI () { return I (real (1)); }

	/**
	 * conjugate of z
	 * @param z the value to use for computation
	 * @return conjugate of z
	 */
	public ComplexValue<T> conjugate (ComplexValue<T> z)
	{ return complexmanager.conjugate(z); }

	/**
	 * compute cos(theta) + i*sin(theta) (CIS operator).
	 *  this is the foundation of polar to cartesian coordinate translation
	 * @param theta the angle to use as parameter
	 * @return new value object
	 */
	public ComplexValue<T> cis (T theta)
	{
		ComplexSupportLibrary<T> tlib = getMathLib ();
		return C (tlib.cos (theta), tlib.sin (theta));
	}

	/**
	 * equivalent to EXP(z) but more efficient
	 *  alternative to full Taylor expansion done on complex value
	 * @param z the complex parameter value
	 * @return the complex result
	 */
	public ComplexValue<T> rCis (ComplexValue<T> z)
	{
		T r = getMathLib ().exp (z.Re ());
		return cis (z.Im ()).multiplyBy (r);
	}

	/**
	 * inverse of CIS function
	 * @param z the complex parameter value
	 * @return the complex result
	 */
	public ComplexValue<T> arccis (ComplexValue<T> z)
	{
		return getI ().times (ln (z)).negate ();
	}

	/**
	 * get access to library for complex values
	 * @return library object based on complex values
	 */
	public OptimizedMathLibrary<ComplexValue<T>> getComplexMathLib ()
	{
		if (complexlib == null) complexlib =
			new OptimizedMathLibrary<ComplexValue<T>> (complexmanager);
		return complexlib;
	}
	OptimizedMathLibrary<ComplexValue<T>> complexlib;

	/**
	 * use real primitives in absence of complex values
	 * @return the primitives library
	 */
	public PowerPrimitives<T> getPrimitiveMathLib ()
	{
		if (primlib == null) primlib =
			new PowerPrimitives<T> (manager);
		return primlib;
	}
	PowerPrimitives<T> primlib;

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#exp(java.lang.Object)
	 */
	public ComplexValue<T> exp (ComplexValue<T> z)
	{
		return rCis (z);
	}
	public ComplexValue<T> nativeExp (ComplexValue<T> z)
	{
		double imag = manager.toNumber (z.Im ()).doubleValue ();
		double exp = Math.exp (manager.toNumber (z.Re ()).doubleValue ());
		ComplexValue<T> realPart = conversion.convertFromDouble (exp * Math.cos (imag));
		ComplexValue<T> imagPart = conversion.convertFromDouble (exp * Math.sin (imag));
		return complexmanager.add (realPart, complexmanager.multiply (imagPart, getI ()));
	}


	/*
	 * cos(ix) = cosh(x)
	 * sin(ix) = i sinh(x)
	 * 
	 * cos(x+y) = cos x cos y - sin x sin y
	 * sin(x+y) = sin x cos y + cos x sin y
	 * 
	 * cos(a+bi) = cos a cos bi - sin a sin bi
	 * 			 = cos a cosh b - i sinh b sin a
	 * 
	 * sin(a+bi) = sin a cos bi + cos a sin bi
	 *           = sin a cosh b + i sinh b cos a 
	 */
	
	/**
	 * complex sine
	 * @param z a complex value
	 * @return sin(z)
	 */
	public ComplexValue<T> sin (ComplexValue<T> z)
	{
		T r = z.realpart, i = z.imagpart;
		ComplexSupportLibrary<T> lib = getMathLib ();
		T realResult = manager.multiply (lib.sin (r), lib.cosh (i));
		T imagResult = manager.multiply (lib.cos (r), lib.sinh (i));
		return C (realResult, imagResult);
	}

	/**
	 * complex cosine
	 * @param z a complex value
	 * @return cos(z)
	 */
	public ComplexValue<T> cos (ComplexValue<T> z)
	{
		T r = z.realpart, i = z.imagpart;
		ComplexSupportLibrary<T> lib = getMathLib ();
		T realResult = manager.multiply (lib.cos (r), lib.cosh (i));
		T imagResult = manager.multiply (lib.sin (r), lib.sinh (i));
		return C (realResult, manager.negate (imagResult));
	}

	/**
	 * inverse sin
	 * @param z value of parameter
	 * @return asin(z)
	 */
	public ComplexValue<T> asin (ComplexValue<T> z)
	{
		ComplexValue<T> i = getI ();
		// -i * ln ( i*z + sqrt(1-z^2) )
		return i.negate ().times (ln (i.times (z).plus (circle (z))));
	}

	/**
	 * unit circle formula sqrt(1-x^2)
	 * @param z value of parameter
	 * @return sqrt (1 - z^2)
	 */
	public ComplexValue<T> circle (ComplexValue<T> z)
	{
		return sqrt (C (1).minus (z.squared ()));
	}

	/**
	 * inverse cos
	 * @param z value of parameter
	 * @return acos(z)
	 */
	public ComplexValue<T> acos (ComplexValue<T> z)
	{
		ComplexValue<T> i = getI ();
		// acos(z) = -i * ln ( z + i*sqrt(1-z^2) )
		return i.negate ().times (ln (z.plus (i.times (circle (z)))));
	}

	/**
	 * inverse tan
	 * @param z value of parameter
	 * @return atan(z)
	 */
	public ComplexValue<T> atan (ComplexValue<T> z)
	{
		ComplexValue<T> i = getI ();
		ComplexValue<T> xPlusI = z.plus (i), xNegPlusI = z.negate ().plus (i);
		ComplexValue<T> logRatio = ln (xNegPlusI.inverted ().times (xPlusI));
		return logRatio.times (i).times (C (2).inverted ());
	}

	/**
	 * inverse sinh
	 * @param z value of parameter
	 * @return sinh(z)
	 */
	public ComplexValue<T> sinh (ComplexValue<T> z)
	{
		ComplexValue<T> i = getI (); // sinh(z) = - i * sin (i*z)
		return i.negate ().times (sin (i.times (z)));
	}

	/**
	 * inverse cosh
	 * @param z value of parameter
	 * @return cosh(z)
	 */
	public ComplexValue<T> cosh (ComplexValue<T> z)
	{
		// cosh(z) = cos(i*z)
		ComplexValue<T> i = getI ();
		return cos (i.times (z));
	}

	/**
	 * inverse arsinh
	 * @param z value of parameter
	 * @return arsinh(z)
	 */
	public ComplexValue<T> arsinh (ComplexValue<T> z)
	{
		ComplexValue<T> zsqp1 = z.squared ().plus (C (1));
		// arsinh(z) = ln ( z + sqrt(z^2+1) )
		return ln (z.plus (sqrt (zsqp1)));
	}

	/**
	 * inverse arcosh
	 * @param z value of parameter
	 * @return arcosh(z)
	 */
	public ComplexValue<T> arcosh (ComplexValue<T> z)
	{
		ComplexValue<T> zsqm1 = z.squared ().plus (C (-1));
		// arcosh(z) = ln ( z + sqrt(z^2-1) )
		return ln (z.plus (sqrt (zsqm1)));
	}

	/**
	 * compute argument of complex value
	 * @param z the complex value to use in evaluation
	 * @return computed argument of parameter
	 */
	public T arg (ComplexValue<T> z)
	{
		return getMathLib ().atan (z.Im (), z.Re ());
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#ln(java.lang.Object)
	 */
	public ComplexValue<T> ln (ComplexValue<T> z)
	{
		if (isZeroMagnitude (z))
		{
			throw new RuntimeException ("Ln not defined for zero parameter, result would be negative infinity");
		}

		T mSquared = z.modSquared ();							// abs = sqrt (Re^2 + Im^2)
		T lnMagSq = getMathLib ().ln (mSquared);				// why use sqrt when next step is Ln
		T lnMagnitude = X (lnMagSq, inverted (real (2)));		// Ln (sqrt (x)) = Ln (x) / 2
		// ln(z) = ln( abssq z ) / 2 + i * arg z
		return C (lnMagnitude, arg (z));
	}

	/**
	 * compute x^y for complex base and exponent
	 * @param x the base value for the computation
	 * @param y the exponent value for the computation
	 * @return computation result object
	 */
	public ComplexValue<T> power
	(ComplexValue<T> x, ComplexValue<T> y)
	{
		if (isZeroMagnitude (y)) return C (1);					// z^0 = 1
		if (isZeroMagnitude (x)) return C (0);					// otherwise, 0^z = 0
		return exp (ln (x).times (y));							// and x^y = exp (Ln (x) * y)
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#pow(java.lang.Object, int)
	 */
	public ComplexValue<T> pow
	(ComplexValue<T> x, int exponent)
	{
		return x.toThe (exponent);
	}

	/**
	 * compute gamma for specified parameter
	 * @param x the parameter to the function call
	 * @return computation result object
	 */
	public ComplexValue<T> gamma (ComplexValue<T> x)
	{
		return gammaFunctionImplementation.eval (x);
	}
	public void initializeGamma ()
	{
		if (gammaFunctionImplementation != null) return;
		gammaFunctionImplementation = new GammaLanczos<T> (this);
	}
	public void initializeGamma (int precision)
	{ initializeGamma (); gammaFunctionImplementation.setPrecision (precision); }
	public Function <ComplexValue<T>> getGammaFunction () { return gammaFunctionImplementation; }
	protected GammaLanczos<T> gammaFunctionImplementation = null;

	/* (non-Javadoc)
	 * @see net.myorb.math.ExtendedPowerLibrary#GAMMA(java.lang.Object)
	 */
	public ComplexValue<T> GAMMA (ComplexValue<T> value)
	{
		return gamma (value);
	}

	/**
	 * test imaginary part of value is real
	 * @param x complex value to be checked for imaginary component
	 * @return TRUE = imaginary part is real
	 */
	public boolean isReal (ComplexValue<T> x)
	{
		return manager.isZero (x.imagpart);
	}

	/**
	 * test imaginary is zero and real GE 0
	 * @param x complex value to be checked for positive real
	 * @return TRUE = positive real
	 */
	public boolean isNonNegativeReal (ComplexValue<T> x)
	{
		return isReal (x) && !manager.isNegative (x.realpart);
	}

	/**
	 * is a zero magnitude complex value
	 * @param x the complex parameter to be evaluated
	 * @return TRUE for zero value
	 */
	public boolean isZeroMagnitude (ComplexValue<T> x)
	{
		return isZro (x.realpart) && isZro (x.imagpart);
	}

	/**
	 * compute e^( ln(x) / y ) for any complex x AND y
	 * @param x complex base value for computation (real and/or imaginary)
	 * @param y complex root value for computation (real or imaginary)
	 * @return computation result object
	 */
	public ComplexValue<T> root (ComplexValue<T> x, ComplexValue<T> y)
	{
		if (isZeroMagnitude (x)) return C (0);
		return exp (ln (x).divideBy (y));
	}

	/**
	 * compute any root value for any complex x
	 * @param x any complex generic value, including imaginary component
	 * @param scalar number of the root (2 for square, 3 for cube, ...)
	 * @return computation result object
	 */
	public ComplexValue<T> root (ComplexValue<T> x, int scalar)
	{
		// nRoot(z,n) = modulus( z )^( 1 / n ) * cis( arg z / n )
		return cis (manager.multiply (arg (x), manager.invert (manager.newScalar (scalar)))).multiplyBy
		(getPrimitiveMathLib ().nThRoot (x.modulus (), scalar));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#sqrt(java.lang.Object)
	 */
	public ComplexValue<T> sqrt (ComplexValue<T> z)
	{
		return root (z, 2);
	}

	/**
	 * compute SQRT(x) for any real x
	 * @param x any real generic value, negative allowed
	 * @return computation result object
	 */
	public ComplexValue<T> isqrt (T x)
	{
		return root (C (x), 2);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.ExtendedPowerLibrary#nThRoot(java.lang.Object, int)
	 */
	public ComplexValue<T> nThRoot (ComplexValue<T> x, int root)
	{
		return exp (ln (x).times (C (root).inverted ()));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#factorial(java.lang.Object)
	 */
	public ComplexValue<T> factorial (ComplexValue<T> n)
	{
		if (isNonNegativeReal (n))
			return C (getPrimitiveMathLib ().factorial (n.realpart));
		throw new RuntimeException ("Factorial of imaginary not meaningful");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#dFactorial(java.lang.Object)
	 */
	public ComplexValue<T> dFactorial (ComplexValue<T> n)
	{
		if (isNonNegativeReal (n))
			return C (getPrimitiveMathLib ().dFactorial (n.realpart));
		throw new RuntimeException ("Factorial of imaginary not meaningful");
	}

	/**
	 * @return the manager for the complex data type conversions
	 */
	public SpaceConversion<ComplexValue<T>> getComplexSpaceConversion ()
	{
		return conversion;
	}
	
	/**
	 * @return the manager for the complex data type representation
	 */
	public ComplexFieldManager<T> getComplexFieldManager ()
	{
		return complexmanager;
	}

	/**
	 * @return the manager for base types of real/imag components
	 */
	public SpaceManager<T> getComponentManager ()
	{
		return manager;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.ExtendedPowerLibrary#magnitude(java.lang.Object)
	 */
	public ComplexValue<T> magnitude (ComplexValue<T> x)
	{
		return x.times (x.conjugate ());
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.TrigAtomic.Quarks#atan(java.lang.Object, java.lang.Object)
	 */
	public ComplexValue<T> atan (ComplexValue<T> x, ComplexValue<T> y)
	{
		throw new RuntimeException ("Atan2 not implemented for Complex domain");
	}

	/**
	 * Euler reflection formula
	 *  GAMMA(z) * GAMMA(1-z) = PI / sin (PI * z)
	 * @param z the parameter to GAMMA being sought
	 * @return GAMMA(z)
	 */
	public ComplexValue<T> reflect (ComplexValue<T> z)
	{
		ComplexValue<T>
			PI = conversion.convertFromDouble (Math.PI), zPI = complexmanager.multiply (PI, z),
			isinZPI = complexmanager.invert (sin (zPI)), piCsc = complexmanager.multiply (PI, isinZPI),
			negzP1 = complexmanager.add (complexmanager.negate (z), complexmanager.getOne ());
		return complexmanager.multiply (piCsc, complexmanager.invert (gamma (negzP1)));
	}

	/**
	 * Gamma recurrence formula
	 *  GAMMA(z) = GAMMA(z+2) / ( z * (z+1) ) ...
	 *  useful for eval of GAMMA for z LT 1 avoiding t^(z-1) integral
	 * @param z the parameter to GAMMA being sought
	 * @return GAMMA(z)
	 */
	public ComplexValue<T> recurrence (ComplexValue<T> z)
	{
		ComplexValue<T>
			zp2 = complexmanager.add (z, complexmanager.newScalar (2)),
			zp1 = complexmanager.add (z, complexmanager.newScalar (1)),
			izzp1 = complexmanager.invert (complexmanager.multiply (z, zp1));
		return complexmanager.multiply (gamma (zp2), izzp1);
	}

}
