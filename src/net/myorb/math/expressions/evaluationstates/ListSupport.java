
package net.myorb.math.expressions.evaluationstates;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.TokenParser;

import net.myorb.data.abstractions.CommonCommandParser.TokenDescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * tools for processing lists
 * @author Michael Druckman
 */
public class ListSupport
{

	/**
	 * actions for processing items
	 */
	public interface ItemProcessor<T>
	{
		/**
		 * @param value the value found
		 */
		void process (T value);
	}

	/**
	 * actions for processing tokens
	 */
	public interface TokenProcessor
	{
		/**
		 * process otherwise unrecognized token
		 * @param type the type of a token found
		 * @param token the text of the token
		 */
		void process (TokenParser.TokenType type, String token);
	}

	/**
	 * process both tokens and value items
	 * @param <T> type of value items
	 */
	public interface FullTokenProcessor<T> extends TokenProcessor, ItemProcessor<T> {}

	/**
	 * allow growth of restricted lists
	 */
	public interface ChainedValueProcessor extends ItemProcessor<Double>
	{
		/**
		 * @return TRUE : list is full
		 */
		boolean atCapacity ();

		/**
		 * @return provide growth object
		 */
		ChainedValueProcessor overflow ();

		/**
		 * @return get ultimate array built
		 */
		double[] getValues ();
	}

	/**
	 * @param tokens the tokens of the list
	 * @param processor a processing object to use for results
	 */
	public static void processValueList
		(
			List<TokenParser.TokenDescriptor> tokens,
			ItemProcessor<Double> processor
		)
	{
		processItems (tokens, new TokensToValues (processor));
	}

	/**
	 * process values into typed list
	 * @param values the list collecting values
	 * @param sm a space manager for values of this type
	 * @return a value processor for the collection
	 * @param <T> type of values in list
	 */
	public static <T> ItemProcessor<Double> getTypedListValueProcessor
		(List<T> values, ExpressionSpaceManager<T> sm)
	{
		return new TypedListProcessor<T>(values, sm);
	}

	/**
	 * collect values to typed list
	 */
	protected static class TypedListProcessor<T> implements ItemProcessor<Double>
	{
		public TypedListProcessor
		(List<T> values, ExpressionSpaceManager<T> sm) { this.values = values; this.sm = sm; }
		public void process (Double value) { values.add (sm.convertFromDouble (value)); }
		protected ExpressionSpaceManager<T> sm;
		protected List<T> values;
	}

	/**
	 * collect values to raw array
	 */
	public static class RawValueProcessor implements ChainedValueProcessor
	{
		public RawValueProcessor () { this (1000); }
		public RawValueProcessor (int size) { this.values = new double[size]; }
		public RawValueProcessor (RawValueProcessor p) { this (2 * p.values.length); copy (p); }
		public void copy (RawValueProcessor p) { for (double v : p.values) process (v); }
		public void process (Double value) { values[next++] = value; }
		public double[] getValues ()
		{
			if (atCapacity ()) return values;
			return java.util.Arrays.copyOf (values, next);
		}
		public ChainedValueProcessor overflow ()
		{ throw new RuntimeException ("Chained growth of list not implemented"); }
		public boolean atCapacity () { return next == values.length; }
		protected double[] values;
		protected int next = 0;
	}

	/**
	 * chain to raw processor twice the size
	 */
	public static class RawValueProcessorGrownExponentially extends RawValueProcessor
	{
		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.evaluationstates.ListSupport.RawValueProcessor#overflow()
		 */
		public ChainedValueProcessor overflow ()
		{ return new RawValueProcessorGrownExponentially (this); }
		public RawValueProcessorGrownExponentially (RawValueProcessor p) { super (p); }
		public RawValueProcessorGrownExponentially (int initialSize) { super (initialSize); }
	}

	/**
	 * chain to list management
	 */
	public static class RawValueProcessorGrownUsingList extends RawValueProcessor
	{
		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.evaluationstates.ListSupport.RawValueProcessor#overflow()
		 */
		public ChainedValueProcessor overflow () { return new ObjectValueProcessor (this); }
		public RawValueProcessorGrownUsingList (int initialSize) { super (initialSize); }
	}

	/**
	 * collect values to object array
	 */
	public static class ObjectValueProcessor
		implements ChainedValueProcessor
	{
		public ObjectValueProcessor (ChainedValueProcessor processor)
		{ this (); for (double v : processor.getValues ()) process (v); }
		public ObjectValueProcessor () { values = new ArrayList<Double>(); }
		public void process (Double value) { values.add (value); }
		public double[] getValues () { return toArray (values); }
		public ChainedValueProcessor overflow () { return null; }
		public boolean atCapacity () { return false; }
		protected List<Double> values;
	}

	/**
	 * process type managed data
	 * @param <T> source data type
	 */
	public static class ConvertFromTyped<T> implements ItemProcessor<T>
	{
		public void process (T value)
		{ processor.process (typeManager.convertToDouble (value)); }
		public ConvertFromTyped (ItemProcessor<Double> processor, ExpressionSpaceManager<T> typeManager)
		{ this.processor = processor; this.typeManager = typeManager; }
		protected ExpressionSpaceManager<T> typeManager;
		protected ItemProcessor<Double> processor;
	}

	/**
	 * start with simple, small, and efficient.
	 * allow growth using more complex, larger, and less efficient.
	 */
	public static class HybridValueProcessor
		implements ItemProcessor<Double>
	{
		public HybridValueProcessor
		(ChainedValueProcessor processor)
		{
			this.processor = processor;
		}
		public void process (Double value)
		{
			if (processor.atCapacity ())
			{ processor = processor.overflow (); }
			processor.process (value);
		}
		public ChainedValueProcessor
		getProcessor () { return processor; }
		protected ChainedValueProcessor processor;
	}

	/**
	 * @param list a list of items to process
	 * @param handler the processor to use
	 * @param <T> data type of items
	 */
	public static <T> void processItems
		(
			List<T> list,
			ItemProcessor<T> handler
		)
	{
		for (T item : list) handler.process (item);
	}

	/**
	 * @param list a list of values
	 * @return the values in an array
	 */
	public static double[] toArray (List<Double> list)
	{
		RawValueProcessor p;
		processItems (list, p = new RawValueProcessor (list.size ()));
		return p.getValues ();
	}

	/**
	 * @param list a list of typed values
	 * @param typeManager a manager for the type
	 * @return an array of double values
	 * @param <T> data type of items
	 */
	public static <T> double[] toArray
		(List<T> list, ExpressionSpaceManager<T> typeManager)
	{
		RawValueProcessor p = new RawValueProcessor (list.size ());
		processItems (list, new ConvertFromTyped<T> (p, typeManager));
		return p.getValues ();
	}

}

/**
 * control flag for negation recognition
 */
class NegationControl
{

	/**
	 * @return TRUE : marker was previously set
	 */
	protected boolean negationMarkerHasBeenSet ()
	{
		if (!wasMarkedNegative) return false;
		resetNegationMarker ();
		return true;
	}

	/**
	 * @param token the token being evaluated
	 * @return TRUE : token implies negation
	 */
	protected boolean recognizedAsNegationSymbol (String token)
	{
		if (token.equals ("-"))
		{ markNextValueAsNegative (); return true; }
		return false;
	}

	/**
	 * invalidate marker
	 */
	protected void resetNegationMarker () { wasMarkedNegative = false; }

	/**
	 * negation sign recognized, next value is considered negative
	 */
	protected void markNextValueAsNegative () { wasMarkedNegative = true; }

	private boolean wasMarkedNegative = false;

}

/**
 * manage use of processor interfaces
 */
class ProcessorControl extends NegationControl
{

	/**
	 * @param number the value to be processed
	 */
	protected void process (Number number)
	{
		double value = number.doubleValue ();
		if (negationMarkerHasBeenSet ()) { value = -value; }
		valueProcessor.process (value);
	}

	/**
	 * @param type the token type
	 * @param image the token image
	 */
	protected void passToProcessor (TokenParser.TokenType type, String image)
	{ if (tokenProcessor != null) tokenProcessor.process (type, image); }

	/**
	 * @param processor the processing object for recognized values
	 */
	protected ProcessorControl
	(ListSupport.ItemProcessor<Double> processor)
	{
		if (processor instanceof ListSupport.FullTokenProcessor)
		{ this.tokenProcessor = (ListSupport.TokenProcessor) processor; }
		this.valueProcessor = processor;
	}
	private ListSupport.ItemProcessor<Double> valueProcessor = null;
	private ListSupport.TokenProcessor tokenProcessor = null;

}

/**
 * process values recognized in token stream
 */
class TokensToValues extends ProcessorControl
	implements ListSupport.ItemProcessor<TokenParser.TokenDescriptor>
{

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.ListSupport.ItemProcessor#process(java.lang.Object)
	 */
	public void process (TokenDescriptor token)
	{
		TokenParser.TokenType type;
		String image = token.getTokenImage ();

		switch (type = token.getTokenType ())
		{
			case INT:
				process (Integer.parseInt (image));
				break;
	
			case FLT: case DEC:
				process (Double.parseDouble (image));
				break;

			default:
				if (!recognizedAsNegationSymbol (image))
				{ passToProcessor (type, image); }
				break;
		}
	}

	/**
	 * @param processor the processing object for recognized values
	 */
	public TokensToValues (ListSupport.ItemProcessor<Double> processor) { super (processor); }
	
}

