
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.OperatorNomenclature;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;

import net.myorb.math.expressions.symbols.AbstractVectorConsumer;
import net.myorb.math.expressions.symbols.IterationConsumerImplementations;
import net.myorb.math.expressions.symbols.IterationConsumerImporter;
import net.myorb.math.expressions.symbols.IterationConsumer;

import net.myorb.math.expressions.tree.NumericalAnalysis;
import net.myorb.math.expressions.tree.RangeNodeDigest;

/**
 * the root of objects that substitute quadrature for integral references
 * @param <T> data type used in expressions
 * @author Michael Druckman
 */
public class QuadratureBase<T> extends AbstractVectorConsumer
			implements NumericalAnalysis<T>, IterationConsumerImporter
{


	public QuadratureBase (String name, Environment<T> environment)
	{ super (name); v = new VectorPrimitives<T> (environment); }
	protected VectorPrimitives<T> v;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.ExecutableUnaryOperator#execute(net.myorb.math.expressions.ValueManager.GenericValue)
	 */
	public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
	{
		return v.sigma (parameters);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.AbstractVectorConsumer#getIterationConsumer()
	 */
	public IterationConsumer getIterationConsumer ()
	{
		IterationConsumer consumer = IterationConsumerImplementations.getQuadratureConsumer ();
		@SuppressWarnings("unchecked") NumericalAnalysis<T> link = (NumericalAnalysis<T>) consumer;
		link.setAnalyzer (this);
		return consumer;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.tree.NumericalAnalysis#getAnalyzer()
	 */
	public NumericalAnalysis<T> getAnalyzer ()
	{
		return this;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.AbstractVectorReduction#markupForDisplay(java.lang.String, net.myorb.math.expressions.symbols.AbstractVectorReduction.Range, java.lang.String, net.myorb.math.expressions.gui.rendering.NodeFormatting)
	 */
	public String markupForDisplay (String operator, Range range, String parameters, NodeFormatting using)
	{
		return using.rangeSpecificationNotation
			(
				using.integralRange
				(
					OperatorNomenclature.INTEGRAL_OPERATOR,
					range
				),
				parameters
			);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.AbstractVectorConsumer#setCurrentValue(net.myorb.math.expressions.ValueManager.GenericValue)
	 */
	public void setCurrentValue (GenericValue currentValue) {}

	
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.tree.NumericalAnalysis#evaluate(net.myorb.math.expressions.tree.RangeNodeDigest)
	 */
	public GenericValue evaluate (RangeNodeDigest<T> digest) { throw new RuntimeException ("Numerical analysis agorithm not provided"); }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.tree.NumericalAnalysis#setAnalyzer(net.myorb.math.expressions.tree.NumericalAnalysis)
	 */
	public void setAnalyzer (NumericalAnalysis<T> analyzer) { throw new RuntimeException ("Unable to change Analyzer"); }


}

