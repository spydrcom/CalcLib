
package net.myorb.math.expressions.algorithms;

import net.myorb.math.primenumbers.Factorization;
import net.myorb.math.primenumbers.FactorAdjustment;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.symbols.AbstractParameterizedFunction;
import net.myorb.math.expressions.BuiltInArrayAbstractions;
import net.myorb.math.expressions.ValueManager;

/**
 * implementations of algorithms for prime factorization functions
 * @author Michael Druckman
 */
public class FactorizationPrimitives
{


	/**
	 * object depends on data structures of the environment
	 * @param environment the environment object holding value management objects
	 */
	public FactorizationPrimitives (Environment<Factorization> environment)
	{
		this.abstractions = new BuiltInArrayAbstractions<Factorization> (environment);
		this.valueManager = environment.getValueManager ();
		this.environment = environment;
	}
	protected BuiltInArrayAbstractions<Factorization> abstractions = null;
	ValueManager<Factorization> valueManager;
	Environment<Factorization> environment;


	/**
	 * implement function - PRIMES
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getPrimesAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{
				return abstractions.primes (parameters);
			}
		};
	}


	/**
	 * implement function - FACTORS
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getFactorsAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{
				return abstractions.factors (parameters);
			}
		};
	}


	/**
	 * implement function - GCF
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getGcfAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{
				return abstractions.gcf (parameters);
			}
		};
	}


	/**
	 * implement function - LCM
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getLcmAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{
				return abstractions.lcm (parameters);
			}
		};
	}


	/**
	 * implement function - FUDGE
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getFudgeAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{
				return valueManager.newDiscreteValue (fudge (environment.getValueManager ().toDiscrete (parameters)));
			}
		};
	}
	Factorization fudge (Factorization f)
	{
		return new FactorAdjustment ().substituteAndAnalyze (f);
	}


}
