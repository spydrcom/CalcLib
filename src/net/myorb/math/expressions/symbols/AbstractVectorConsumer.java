
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.symbols.IterationConsumer;

/**
 * allow a vector reduction to also act as a consumer
 * @author Michael Druckman
 */
public abstract class AbstractVectorConsumer
	extends AbstractVectorReduction
	implements IterationConsumer
{

	public AbstractVectorConsumer (String name)
	{
		super (name); iterationConsumer = getIterationConsumer ();
	}

	/*
	 * implementation of consumer wrapper
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.IterationConsumer#init()
	 */
	public void init () { iterationConsumer.init (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.IterationConsumer#accept(net.myorb.math.expressions.ValueManager.GenericValue)
	 */
	public void accept (GenericValue value) { iterationConsumer.accept (value); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.IterationConsumer#setIterationValue(net.myorb.math.expressions.ValueManager.GenericValue)
	 */
	public void setIterationValue (GenericValue value) { iterationConsumer.setIterationValue (value); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.IterationConsumer#getCalculatedResult()
	 */
	public GenericValue getCalculatedResult () { return iterationConsumer.getCalculatedResult (); }

	/**
	 * @return the consumer algorithm to be used
	 */
	public abstract IterationConsumer getIterationConsumer ();

	protected IterationConsumer iterationConsumer;

}
