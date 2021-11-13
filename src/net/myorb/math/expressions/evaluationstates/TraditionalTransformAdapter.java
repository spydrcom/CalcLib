
package net.myorb.math.expressions.evaluationstates;

import net.myorb.data.abstractions.Function;

import net.myorb.data.abstractions.CommonCommandParser.TokenDescriptor;

import java.util.List;

/**
 * adapter that will throw exception for unimplemented entries
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class TraditionalTransformAdapter<T> extends TraditionalTransforms<T>
{

	@Override
	public Function<T> abel(String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens) {
		throw new RuntimeException ("Transform type not implemented");
	}

	@Override
	public Function<T> chebyshev(String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens) {
		throw new RuntimeException ("Transform type not implemented");
	}

	@Override
	public Function<T> fourier(String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens) {
		throw new RuntimeException ("Transform type not implemented");
	}

	@Override
	public Function<T> hankel(String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens) {
		throw new RuntimeException ("Transform type not implemented");
	}

	@Override
	public Function<T> hilbert(String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens) {
		throw new RuntimeException ("Transform type not implemented");
	}

	@Override
	public Function<T> laplace(String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens) {
		throw new RuntimeException ("Transform type not implemented");
	}

	@Override
	public Function<T> legendre(String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens) {
		throw new RuntimeException ("Transform type not implemented");
	}

	@Override
	public Function<T> mellin(String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens) {
		throw new RuntimeException ("Transform type not implemented");
	}

	@Override
	public Function<T> stirling(String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens) {
		throw new RuntimeException ("Transform type not implemented");
	}

	@Override
	public Function<T> wavelet(String functionName, List<String> parameterNames, TokenStream transformTokens, TokenStream definitionTokens) {
		throw new RuntimeException ("Transform type not implemented");
	}

	public Function<T> apply(String functionName, List<String> parameterNames, List<TokenDescriptor> transformTokens, List<TokenDescriptor> definitionTokens) {
		throw new RuntimeException ("Transform type not implemented");
	}


	public TraditionalTransformAdapter (Environment<T> environment)
	{
		super (environment);
	}


}
