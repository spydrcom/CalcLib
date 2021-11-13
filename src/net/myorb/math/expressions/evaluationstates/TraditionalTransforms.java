
package net.myorb.math.expressions.evaluationstates;

import net.myorb.data.abstractions.Function;

import java.util.HashMap;
import java.util.List;

/**
 * Laplace, Fourier, etc.
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public abstract class TraditionalTransforms<T> extends DeclarationSupport<T> implements GenericTransform<T>
{


	/*
	 * recognized transform type names
	 */

	public abstract Function<T> legendre
	(
		String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens
	);

	public abstract Function<T> wavelet
	(
		String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens
	);

	public abstract Function<T> chebyshev
	(
		String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens
	);

	public abstract Function<T> stirling
	(
		String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens
	);

	/*
	 * integration transforms specifically of the form
	 * 
	 * 					 (Tf)(u) = INTEGRAL K(t,u) * f(t) dt || (t1, t2)
	 * with inverse
	 * 						f(t) = INTEGRAL Kinv(u,t) * Tf(u) du || (u1, u2)
	 */

	public abstract Function<T> fourier			// K = exp (-2 * PI * i * u * t)	// Kinv = exp (2 * PI * i * u * t)
	(
		String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens
	);

	public abstract Function<T> abel			// K = 2 * t / sqrt (t^2 - u^2)		// Kinv = -1/PI * 1 / sqrt (u^2 - t^2) * d/du
	(
		String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens
	);

	public abstract Function<T> laplace			// K = exp (- u * t)				// Kinv = exp (u * t) / (2*PI*i)
	(
		String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens
	);

	public abstract Function<T> hilbert			// K = 1/PI * 1 / (u - t)			// Kinv = 1/PI * 1 / (u - t)
	(
		String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens
	);

	public abstract Function<T> mellin			// K = t ^ (u - 1)					// Kinv = t^(-u) / (2*PI*i)
	(
		String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens
	);

	public abstract Function<T> hankel			// K = t * J (u * t)				// Kinv = u * J (u * t)
	(
		String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens
	);


	public TraditionalTransforms (Environment<T> environment)
	{
		super (environment);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.GenericTransform#apply(java.lang.String, java.util.List, java.util.List, java.util.List)
	 */
	public Function<T> apply
		(
			String functionName, List<String> parameterNames,
			TokenStream transformTokens, TokenStream definitionTokens
		)
	{
		if (transformTokens.size () < 1)
			throw new RuntimeException ("No transform declaration found");
		TRANSFORM_TYPE type = TRANFORM_MAP.get (transformTokens.get (1).getTokenImage ());
		return apply (type, functionName, parameterNames, transformTokens, definitionTokens);
	}
	public Function<T> apply
		(
			TRANSFORM_TYPE type,
			String functionName, List<String> parameterNames,
			TokenStream transformTokens, TokenStream definitionTokens
		)
	{
		if (type == null) throw new RuntimeException ("Transform type not recognized");
		
		switch (type)
		{
			case FOURIER:	return fourier (functionName, parameterNames, transformTokens, definitionTokens);
			case LAPLACE:	return laplace (functionName, parameterNames, transformTokens, definitionTokens);
			case CHEBYSHEV:	return chebyshev (functionName, parameterNames, transformTokens, definitionTokens);
			case STIRLING:	return stirling (functionName, parameterNames, transformTokens, definitionTokens);
			case LEGENDRE:	return legendre (functionName, parameterNames, transformTokens, definitionTokens);
			case HANKEL:	return hankel (functionName, parameterNames, transformTokens, definitionTokens);
			case WAVELET:	return wavelet (functionName, parameterNames, transformTokens, definitionTokens);
			case HILBERT:	return hilbert (functionName, parameterNames, transformTokens, definitionTokens);
			case MELLIN:	return mellin (functionName, parameterNames, transformTokens, definitionTokens);
			case ABEL:		return abel (functionName, parameterNames, transformTokens, definitionTokens);
			default:		throw new RuntimeException ("Transform type not implemented");
		}
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.Function#f(java.lang.Object)
	 */
	public T eval(T x)
	{
		return transform.eval (x);
	}
	protected Function<T> transform;


	/**
	 * recognized transform types
	 */
	public enum TRANSFORM_TYPE
	{
		LAPLACE, FOURIER, CHEBYSHEV, STIRLING, LEGENDRE, HANKEL, WAVELET, HILBERT, MELLIN, ABEL
	}

	/**
	 * map identifiers to enumeration
	 */
	public static final HashMap<String, TRANSFORM_TYPE> TRANFORM_MAP = new HashMap<String, TRANSFORM_TYPE> ();


	/**
	 * populate TRANFORM_MAP
	 */

	static
	{
		TRANFORM_MAP.put ("LAPLACE", TRANSFORM_TYPE.LAPLACE);			// use Laplace transform
		TRANFORM_MAP.put ("FOURIER", TRANSFORM_TYPE.FOURIER);			// use Fourier transform
		TRANFORM_MAP.put ("CHEBYSHEV", TRANSFORM_TYPE.CHEBYSHEV);		// use Chebyshev transform
		TRANFORM_MAP.put ("STIRLING", TRANSFORM_TYPE.STIRLING);			// use Stirling transform
		TRANFORM_MAP.put ("LEGENDRE", TRANSFORM_TYPE.LEGENDRE);			// use Legendre transform
		TRANFORM_MAP.put ("HANKEL", TRANSFORM_TYPE.HANKEL);				// use Hankel transform
		TRANFORM_MAP.put ("WAVELET", TRANSFORM_TYPE.WAVELET);			// use Wavelet transform
		TRANFORM_MAP.put ("HILBERT", TRANSFORM_TYPE.HILBERT);			// use Hilbert transform
		TRANFORM_MAP.put ("MELLIN", TRANSFORM_TYPE.MELLIN);				// use Mellin transform
		TRANFORM_MAP.put ("ABEL", TRANSFORM_TYPE.ABEL);					// use Abel transform
	}

	public static String[] listOfTransforms ()
	{
		String[] list = TRANFORM_MAP.keySet ().toArray (new String[]{});
		java.util.Arrays.sort (list);
		return list;

	}


}

