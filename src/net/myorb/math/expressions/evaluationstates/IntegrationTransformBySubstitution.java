
package net.myorb.math.expressions.evaluationstates;

import net.myorb.data.abstractions.Function;

import java.util.List;

/**
 * macro text substitution used for building integral approximations
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class IntegrationTransformBySubstitution<T> extends TraditionalTransformAdapter<T>
{

	/*
	 * token stream for invocation:
	 * 
	 * 			function TYPE FORM lo hi inc
	 * 
	 * 			where:
	 * 					TYPE = FOURIER | LAPLACE | ...
	 * 					FORM = KER (kernel form) | INV (kernel inverse form)
	 * 					lo   = lo end of range
	 * 					hi   = hi end of range
	 * 					inc  = increment value
	 */

	void formatIntegral
		(
			String functionName, List<String> parameterNames,
			TokenStream transformTokens, TokenStream definitionTokens,
			String kernel, String kernelInverse
		)
	{
		if (transformTokens.size () < 6)
		{
			throw new RuntimeException ("Transform declaration requires 6 items (function TYPE FORM lo hi delta)");
		}
		StringBuffer buffer = new StringBuffer ();
		String	p  = parameterNames.get (0), inc = transformTokens.get (5).getTokenImage (),
				lo = transformTokens.get (3).getTokenImage (), hi = transformTokens.get (4).getTokenImage ();
		if (transformTokens.get (2).getTokenImage ().startsWith ("K"))
		{
			buffer.append ("INTEGRAL [")
			.append (lo).append (" <= t <= ").append (hi).append (" <> ").append (inc).append ("] (")
			.append (functionName).append ("(t) * ").append (kernel.replaceAll ("u", p)).append (")");
		}
		else
		{
			buffer.append ("INTEGRAL [")
			.append (lo).append (" <= u <= ").append (hi).append (" <> ").append (inc).append ("] (")
			.append (functionName).append ("(u) * ").append (kernelInverse.replaceAll ("t", p)).append (")");
		}
		describeDefinition (buffer.toString (), definitionTokens);
	}

	/*
	 * integration transforms specifically of the form
	 * 
	 * 					 (Tf)(u) = INTEGRAL K(t,u) * f(t) dt || (t1, t2)
	 * with inverse
	 * 						f(t) = INTEGRAL Kinv(u,t) * Tf(u) du || (u1, u2)
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.TraditionalTransformAdapter#fourier(java.lang.String, java.util.List, net.myorb.math.expressions.evaluationstates.DeclarationSupport.TokenStream, net.myorb.math.expressions.evaluationstates.DeclarationSupport.TokenStream)
	 */
	public Function<T> fourier			// K = exp (-2 * PI * i * u * t)	// Kinv = exp (2 * PI * i * u * t)
		(
			String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens
		)
	{
		formatIntegral
		(
			functionName, parameterNames, transformTokens, definitionTokens,
			"exp (-2 * pi * i * u * t)", "exp (2 * pi * i * u * t)"
		);
		return null;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.TraditionalTransformAdapter#abel(java.lang.String, java.util.List, net.myorb.math.expressions.evaluationstates.DeclarationSupport.TokenStream, net.myorb.math.expressions.evaluationstates.DeclarationSupport.TokenStream)
	 */
	public Function<T> abel				// K = 2 * t / sqrt (t^2 - u^2)		// Kinv = -1/PI * 1 / sqrt (u^2 - t^2) * d/du
		(
			String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens
		)
	{
		formatIntegral
		(
			functionName, parameterNames, transformTokens, definitionTokens,
			"(2 * t / 2\\ (t^2 - u^2))", "(-1/pi * 1 / 2\\ (u^2 - t^2) * d/du)"
		);
		return null;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.TraditionalTransformAdapter#laplace(java.lang.String, java.util.List, net.myorb.math.expressions.evaluationstates.DeclarationSupport.TokenStream, net.myorb.math.expressions.evaluationstates.DeclarationSupport.TokenStream)
	 */
	public Function<T> laplace			// K = exp (- u * t)				// Kinv = exp (u * t) / (2*PI*i)
		(
			String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens
		)
	{
		formatIntegral
		(
			functionName, parameterNames, transformTokens, definitionTokens,
			"exp (- u * t)", "(exp (u * t) / (2*pi*i))"
		);
		return null;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.TraditionalTransformAdapter#hilbert(java.lang.String, java.util.List, net.myorb.math.expressions.evaluationstates.DeclarationSupport.TokenStream, net.myorb.math.expressions.evaluationstates.DeclarationSupport.TokenStream)
	 */
	public Function<T> hilbert			// K = 1/PI * 1 / (u - t)			// Kinv = 1/PI * 1 / (u - t)
		(
			String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens
		)
	{
		formatIntegral
		(
			functionName, parameterNames, transformTokens, definitionTokens,
			"(1/pi * 1 / (u - t))", "(1/pi * 1 / (u - t))"
		);
		return null;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.TraditionalTransformAdapter#mellin(java.lang.String, java.util.List, net.myorb.math.expressions.evaluationstates.DeclarationSupport.TokenStream, net.myorb.math.expressions.evaluationstates.DeclarationSupport.TokenStream)
	 */
	public Function<T> mellin			// K = t ^ (u - 1)					// Kinv = t^(-u) / (2*PI*i)
		(
			String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens
		)
	{
		formatIntegral
		(
			functionName, parameterNames, transformTokens, definitionTokens,
			"(t ^ (u - 1))", "(t^(-u) / (2*pi*i))"
		);
		return null;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.TraditionalTransformAdapter#hankel(java.lang.String, java.util.List, net.myorb.math.expressions.evaluationstates.DeclarationSupport.TokenStream, net.myorb.math.expressions.evaluationstates.DeclarationSupport.TokenStream)
	 */
	public Function<T> hankel			// K = t * J (u * t)				// Kinv = u * J (u * t)
		(
			String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens
		)
	{
		formatIntegral
		(
			functionName, parameterNames, transformTokens, definitionTokens,
			"(t * J (u * t))", "(u * J (u * t))"
		);
		return null;
	}


	public IntegrationTransformBySubstitution (Environment<T> environment, Function<T> f)
	{
		super (environment); transform = f;
	}


}

