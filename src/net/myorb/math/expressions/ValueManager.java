
package net.myorb.math.expressions;

import net.myorb.math.matrices.Matrix;
import net.myorb.math.GeneratingFunctions;

import net.myorb.math.expressions.evaluationstates.Subroutine;
import net.myorb.math.expressions.symbols.DefinedFunction;

import net.myorb.data.abstractions.SimpleUtilities;
import net.myorb.data.abstractions.ErrorHandling;

import java.util.List;

/**
 * the central representation for all value types supported
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class ValueManager<T>
{


	/**
	 * lists of values managed in the environment
	 * @param <T> the type of data in lists
	 */
	public static class RawValueList<T> extends java.util.ArrayList<T>
	{
		public RawValueList () {}
		public RawValueList (T value) { if (value != null) this.add (value); }
		public RawValueList (java.util.Collection<T> values) { if (values != null) this.addAll (values); }

		public double[] toDoubleFloatArray (ExpressionSpaceManager<T> manager)
		{
			double[] values = new double[this.size ()];
			for (int i = 0; i < values.length; i++) values[i] = manager.convertToDouble (get (i));
			return values;
		}
		public Double[] toDoubleArray (ExpressionSpaceManager<T> manager)
		{
			Double[] values = new Double[this.size ()];
			for (int i = 0; i < values.length; i++) values[i] = manager.convertToDouble (get (i));
			return values;
		}
		private static final long serialVersionUID = -7782053823650366575L;
	}


	/**
	 * generic hook for metadata
	 */
	public interface Metadata {}

	/**
	 * special formatting for value
	 */
	public interface Formatter<T>
	{
		String format (T value);
	}

	/**
	 * a common base for all value types
	 */
	public interface GenericValue
	{
		/**
		 * get name if named value.
		 *  null is returned if no name applies
		 * @return the name of the value
		 */
		String getName ();

		/**
		 * set the associated name
		 * @param name the name of the value
		 */
		void setName (String name);

		void setMetadata (Metadata metadata);
		Metadata getMetadata ();
	}

	/**
	 * manage lists of generic values
	 */
	public static class GenericValueList extends java.util.ArrayList<GenericValue>
	{ private static final long serialVersionUID = 1395649219122888859L; }

	/**
	 * uses generic type so may require special treatment
	 */
	public interface ManagedValue<T> extends GenericValue
	{
		void setFormatter (Formatter<T> formatter);
	}

	/**
	 * a holding place for an unrecognized symbol
	 */
	public interface UndefinedValue extends GenericValue {}


	/**
	 * a representation for discrete values
	 * @param <T> type on which operations are to be executed
	 */
	public interface DiscreteValue<T> extends ManagedValue<T>
	{
		/**
		 * get the related value
		 * @return the discrete value description
		 */
		T getValue ();
	}


	/**
	 * a representation for value arrays
	 * @param <T> type on which operations are to be executed
	 */
	public interface DimensionedValue<T> extends ManagedValue<T>
	{
		/**
		 * get lest of values of array
		 * @return the list of values
		 */
		RawValueList<T> getValues ();
	}


	/**
	 * a representation for matrix of values
	 * @param <T> type on which operations are to be executed
	 */
	public interface MatrixValue<T> extends GenericValue
	{
		/**
		 * get access to associated matrix
		 * @return matrix object
		 */
		Matrix<T> getMatrix ();
	}


	public interface TextValue extends GenericValue
	{
		/**
		 * get access to associated text
		 * @return text string
		 */
		String getText ();
	}


	/**
	 * tokens carried as raw text value to represent a lambda expression
	 */
	public interface CapturedValue extends TextValue
	{}


	/**
	 * a pointer carried as a value to implement object access
	 */
	public interface IndirectAccess extends GenericValue
	{
		Object getReferenced ();
	}


	/**
	 * an indirect reference to a function
	 */
	public interface Executable<T> extends GenericValue
	{
		Subroutine<T> getSubroutine ();
	}


	/**
	 * general object wrapper
	 */
	public interface StructuredValue extends GenericValue
	{
		/**
		 * get access to structure
		 * @return the object
		 */
		Object getStructure ();
	}


	/**
	 * a representation for parameter lists
	 */
	public interface ValueList extends GenericValue
	{
		/**
		 * get list of values that comprise a parameter list
		 * @return the list of values
		 */
		GenericValueList getValues ();
	}


	/*
	 * creation of value objects
	 */


	/**
	 * construct a discrete value object
	 * @param value an initial value for the object
	 * @return the discrete value object
	 */
	public DiscreteValue<T> newDiscreteValue (T value)
	{
		return new DiscreteValueStorage<T> (value);
	}


	/**
	 * construct a dimensioned value object
	 * @param values an array of initial values
	 * @return the dimensioned value object
	 */
	public DimensionedValue<T> newDimensionedValue (RawValueList<T> values)
	{
		return new DimensionedValueStorage<T> (values);
	}


	/**
	 * construct a dimensioned value object
	 * @param values an array of initial values
	 * @return the dimensioned value object
	 */
	public DimensionedValue<T> newDimensionedValue (java.util.List<T> values)
	{
		return newDimensionedValue (new RawValueList<T> (values));
	}


	/**
	 * @param coefficients a coefficients object to treat as array
	 * @return the dimensioned value object
	 */
	public DimensionedValue<T> newCoefficientList
	(GeneratingFunctions.Coefficients<T> coefficients)
	{
		return newDimensionedValue (coefficients);
	}


	/**
	 * construct a dimensioned value object
	 * @return the dimensioned value object
	 */
	public DimensionedValue<T> newDimensionedValue ()
	{
		return new DimensionedValueStorage<T> ();
	}


	/**
	 * construct storage link for matrix
	 * @param m the matrix being stored
	 * @return a storage object
	 */
	public MatrixValue<T> newMatrix (Matrix<T> m)
	{
		return new MatrixStorage<T> (m);
	}


	/**
	 * @param text the text to be referenced
	 * @return the new text object
	 */
	public TextValue newText (String text)
	{
		return new TextStorage (text);
	}


	/**
	 * @param structure object to be referenced
	 * @return new structure object
	 */
	public StructuredValue newStructure (Object structure)
	{
		return new StructureStorage (structure);
	}


	/*
	 * specific values for lambda expressions
	 * and Procedure Parameter implementations
	 * (Pointer types included)
	 */


	/**
	 * @param text the captured text
	 * @return the text storage object
	 */
	public CapturedValue newCapturedValue (String text)
	{
		return new TextStorage (text);
	}


	/**
	 * @param referenced the object referred to
	 * @return indirect access to the object
	 */
	public IndirectAccess newPointer (Object referenced)
	{
		return new Pointer ( (NamedValue) referenced );
	}


	/**
	 * build a generic value to hold a procedure parameter
	 * @param procedure the procedure to be referenced
	 * @return the procedure wrapped as an Executable
	 */
	public Executable<T> newProcedureParameter (DefinedFunction<T> procedure)
	{
		return new ExecutableReference<T> (procedure);
	}


	/*
	 * utilities for GenericValue content
	 */


	/**
	 * construct a value list object
	 * @return the value list object
	 */
	public ValueList newValueList ()
	{
		return new ValueListStorage ();
	}


	/**
	 * @param values initial values for list
	 * @return the value list object
	 */
	public ValueList newValueList (ValueManager.GenericValueList values)
	{
		return new ValueListStorage (values);
	}


	/*
	 * error state processing
	 */


	/**
	 * a value associated with an undefined symbol
	 * @param symbol the name of the symbol referenced
	 * @return an undefined value descriptor
	 */
	public UndefinedValue newUndefinedSymbolReference (String symbol)
	{
		return new UndefinedSymbolReference (symbol);
	}


	/**
	 * mark values undefined when produced in asymptotic conditions
	 * @return an undefined value descriptor
	 */
	public UndefinedValue newAsymptoticReference ()
	{
		return new UndefinedValue ()
		{
			public String getName ()
			{
				return "Asymptotic Value";
			}

			public void setName (String name) {}
			public void setMetadata (Metadata metadata) {}
			public Metadata getMetadata () { return null; }
		};
	}


	/**
	 * exception for error in context expectations
	 */
	public static class Expected extends ErrorHandling.Terminator
	{
		public Expected
			(
				String type, Exception cause
			)
		{ super (basedMessage (type, cause), cause); }
		public Expected (String type) { super (simpleMessage (type)); }
		private static String basedMessage (String forType, Exception source)
		{ return simpleMessage (forType) + " [" + source.getMessage () + "]"; }
		private static String simpleMessage (String forType) { return forType + " expected, not found"; }
		private static final long serialVersionUID = -2078780687620418547L;
	}


	/**
	 * inappropriate value found
	 */
	public static class UnableToConvert extends ErrorHandling.Terminator
	{
		public UnableToConvert ()
		{ super ("Value found was not appropriate for use in context"); }
		private static final long serialVersionUID = -4597079923608033657L;
	}


	/**
	 * identify undefined reference
	 */
	public static class UndefinedValueError extends ErrorHandling.Terminator
	{
		public UndefinedValueError (GenericValue value)
		{ super (UNDEFINED_VALUE_ERROR + value.getName ()); }
		static final String UNDEFINED_VALUE_ERROR = "Undefined value from unrecognized reference: ";
		private static final long serialVersionUID = -5262786940136520549L;
	}


	/**
	 * identify empty list
	 */
	public static class EmptyParameterList extends ErrorHandling.Terminator
	{
		public EmptyParameterList () { super ("Empty parameter list"); }
		private static final long serialVersionUID = -7662098745940318425L;
	}


	/*
	 * discrete values
	 */


	/**
	 * cast to discrete value
	 * @param value the value as a generic object
	 * @return the discrete value
	 */
	public DiscreteValue<T> toDiscreteValue (GenericValue value)
	{
		@SuppressWarnings ("unchecked") DiscreteValue<T>
		dv = SimpleUtilities.verifyClass (value, DiscreteValue.class);
		return dv;
	}


	/**
	 * does parameter represent a discrete value
	 * @param value the value as a generic object
	 * @return TRUE = value is discrete
	 */
	public boolean isDiscrete (GenericValue value)
	{
		return value instanceof DiscreteValue;
	}


	/**
	 * is the value an integer
	 * @param value the value to be checked
	 * @param manager an expression manager for the type
	 * @return TRUE = value is an integer
	 */
	public boolean isInt (GenericValue value, ExpressionSpaceManager<T> manager)
	{
		if (!isDiscrete (value)) return false;
		T v = toDiscreteValue (value).getValue ();
		T negIntVal = manager.newScalar (- manager.toNumber (v).intValue ());
		return manager.isZero (manager.add (v, negIntVal));
	}


	/*
	 * Simple Dimensioned Value (s)
	 */


	/**
	 * seeking instance of DimensionedValue
	 * @param value the value as a generic object
	 * @return TRUE : value is DimensionedValue
	 */
	public boolean isSimpleDimensionedValue (GenericValue value)
	{
		return value instanceof DimensionedValue;
	}


	/**
	 * cast to dimensioned object
	 * @param value the value as a generic object
	 * @return a dimensioned value
	 */
	public DimensionedValue<T> toDimensionedValue (GenericValue value)
	{
		@SuppressWarnings ("unchecked") DimensionedValue<T>
		dv = SimpleUtilities.verifyClass (value, DimensionedValue.class);
		return dv;
	}


	/**
	 * cast to managed object
	 * @param value the value as a generic object
	 * @return a managed value
	 * @param <T> data type
	 */
	public static <T> ManagedValue<T> toManagedValue (GenericValue value)
	{
		@SuppressWarnings ("unchecked") ManagedValue<T>
		mv = SimpleUtilities.verifyClass (value, ManagedValue.class);
		return mv;
	}


	/**
	 * connect a formatter to a value
	 * @param value the value to be formatted
	 * @param formatter a formatter to use
	 * @param <T> data type
	 */
	public static <T> void setFormatter (GenericValue value, Formatter<T> formatter)
	{
		ManagedValue<T> mv = toManagedValue (value);
		if (mv != null) { mv.setFormatter (formatter); }
	}


	/**
	 * @param value the value to be formatted
	 * @param formatter a formatter to use
	 * @return the formatted text
	 * @param <T> data type
	 */
	public static <T> String format (GenericValue value, Formatter<T> formatter)
	{
		setFormatter (value, formatter);
		return value.toString ();
	}


	/**
	 * convert to dimensioned value
	 * @param value a generic value from the stack
	 * @return converted dimensioned value
	 */
	public DimensionedValue<T> getDimensionedValue (GenericValue value)
	{
		DimensionedValue<T> dv = toDimensionedValue (value);
		if (dv == null)
		{
			throw new Expected ("Dimensioned value");
		}
		return dv;
	}


	/**
	 * cast to Dimensioned Value and return list
	 * @param value the value as a generic object
	 * @return the list of discrete values
	 */
	public RawValueList<T> toDiscreteValues (GenericValue value)
	{
		return getDimensionedValue (value).getValues ();
	}


	/**
	 * does parameter represent an array
	 * @param value the value as a generic object
	 * @return TRUE = value is an array
	 */
	public boolean isArray (GenericValue value)
	{
		DimensionedValue<T>
			dv = toDimensionedValue (value);
		if (dv != null) return dv.getValues ().size () > 1;
		else return false;
	}


	/*
	 * structured value types
	 */


	/**
	 * is the value able to be formatted
	 * @param value the value as a generic object
	 * @return TRUE = value can be formatted
	 */
	public boolean isManagedValue (GenericValue value)
	{
		return value instanceof ManagedValue;
	}


	/**
	 * does parameter represent a matrix
	 * @param value the value as a generic object
	 * @return TRUE = value is a matrix
	 */
	public boolean isMatrix (GenericValue value)
	{
		return value instanceof MatrixValue;
	}


	/**
	 * is value a matrix or array
	 * @param value the value as a generic object
	 * @return TRUE = value is a matrix or array
	 */
	public boolean isDimensioned (GenericValue value)
	{
		return isArray (value) || isMatrix (value);
	}


	/**
	 * apply an index to an array and return the value
	 * @param left a generic value that must be an array
	 * @param indexValue the index into the array to check
	 * @return the element of the array given by the index
	 * @throws RuntimeException for index out of range
	 */
	public GenericValue applyIndex
	(GenericValue left, int indexValue)
	throws RuntimeException
	{
		if (isParameterList (left))
		{ return elementOfList (valuesOfList (left), indexValue); }
		else return discreteElement (toArray (left), indexValue);
	}


	/**
	 * get item of array at given index
	 * @param array an array of discrete data values
	 * @param index the index into the array to check
	 * @return the element of the array given by the index
	 * @throws RuntimeException for index out of range
	 */
	public GenericValue
	discreteElement (List<T> array, int index) throws RuntimeException
	{ indexCheck (array, index); return newDiscreteValue (array.get (index)); }


	/**
	 * get item of list at given index
	 * @param items a list of generic values
	 * @param index the index into the array to check
	 * @return the element of the array given by the index
	 * @throws RuntimeException for index out of range
	 */
	public GenericValue
	elementOfList (List<GenericValue> items, int index) throws RuntimeException
	{ indexCheck (items, index); return items.get (index); }


	/**
	 * check index against array size
	 * @param list the list to treat as an array
	 * @param index the index into the array to check
	 * @throws RuntimeException for index out of range
	 */
	public void indexCheck (List<?> list, int index) throws RuntimeException
	{
		if (index < 0 || list.size () <= index)
		{
			throw new RuntimeException ("Index found to be beyond array bounds");
		}		
	}


	/**
	 * does parameter represent a list
	 * @param value the value as a generic object
	 * @return TRUE = value is a list
	 */
	public boolean isParameterList (GenericValue value)
	{
		return value instanceof ValueList;
	}


	/**
	 * allow structured discrete values
	 * @param value generic wrapper holding structure
	 * @return the wrapped structured value
	 */
	public boolean isStructured (GenericValue value)
	{
		return value instanceof StructureStorage;
	}
	/* bug fix added 7/23/2021 for complex values getting wrapped as structures */


	/**
	 * does parameter represent an undefined value
	 * @param value the value as a generic object
	 * @return TRUE = value is undefined
	 */
	public boolean isUndefinedValue (GenericValue value)
	{
		return value instanceof UndefinedValue;
	}


	/**
	 * interpret the value as a discrete
	 * @param value the value as a generic object
	 * @return the discrete type
	 */
	public T toDiscrete (GenericValue value)
	{
		try
		{
			if (isDiscrete (value = check (value))) return toDiscreteValue (value).getValue ();
			else if (isSimpleDimensionedValue (value)) return toDimensionedValue (value).getValues ().get (0);
			else if (isParameterList (value)) return toDiscreteValue (((ValueList) value).getValues ().get (0)).getValue ();
			else if (isStructured (value)) return toStructuredValue (value); // bug fix for complex discrete values
			else throw new UnableToConvert ();
		}
		catch (Exception e)
		{
			throw new Expected ("Discrete", e);				//TODO
		}
	}


	/**
	 * reduce value to integer
	 * @param value a generic value object
	 * @param manager an expression manager that manipulates the component type
	 * @return the value reduced to integer
	 */
	public int toInt (GenericValue value, ExpressionSpaceManager<T> manager)
	{
		return manager.convertToDouble (toDiscrete (value)).intValue ();
	}


	/**
	 * allow structured discrete values
	 *  (commonly used for complex values)
	 * @param value a values in a structured storage wrapper
	 * @return value as T object
	 */
	@SuppressWarnings("unchecked")
	public T toStructuredValue (GenericValue value)
	{
		StructureStorage s = (StructureStorage) value;
		return (T) s.getStructure ();
	}


	/**
	 * interpret the value as a value array
	 * @param value the value as a generic object
	 * @return a list of values
	 */
	public RawValueList<T> toArray (GenericValue value)
	{
		try
		{
			check (value);											// ignore value returned from check
			//value = check (value);//								// BUG 09/15/18 check forces discrete value returned
			if (isParameterList (value))
			{
				return valueListToArray ((ValueList) value);
			}
			else if (isSimpleDimensionedValue (value))
			{
				return toDimensionedValue (value).getValues ();
			}
			else if (isDiscrete (value))
			{
				T discrete = toDiscreteValue (value).getValue ();
				return new RawValueList<T> (discrete);
			}
			else throw new UnableToConvert ();
		}
		catch (EmptyParameterList emptyList) { return new RawValueList<T> (); }
		catch (Exception others) { throw new Expected ("Array", others); }
	}
	public RawValueList<T> valueListToArray (ValueList list)
	{
		RawValueList<T> raw = new RawValueList<T>();
		for (GenericValue v : list.getValues ())
		{
			if (isDimensioned (v))
				raw.addAll (toArray (v));							// covers case of array in value list
			else raw.add (toDiscrete (v));							// typical elements will be discrete
		}
		return raw;
	}


	/**
	 * verify parameter type and get list values
	 * @param parameters a generic value holding a value list
	 * @return the list of values
	 */
	public GenericValueList toList
		(GenericValue parameters)
	{
		//TODO: process null
		if (parameters == null)
		{ return new GenericValueList (); }
		if (isParameterList (parameters))
		{ return valuesOfList (parameters); }
		else throw new Expected ("Value list");
	}


	/**
	 * get values from list
	 * @param parameters a generic value holding a value list
	 * @return the list of values
	 */
	public GenericValueList
		valuesOfList (GenericValue parameters)
	{ return ((ValueList) parameters).getValues (); }
	//TODO: process ValueListStorage


	/**
	 * interpret the value as a matrix
	 * @param value the value as a generic object
	 * @return a matrix of values
	 */
	@SuppressWarnings("unchecked")
	public Matrix<T> toMatrix (GenericValue value)
	{
		try
		{
			if (isMatrix (value = check (value)))
			{ return ((MatrixValue<T>) value).getMatrix (); }
			else throw new UnableToConvert ();
		}
		catch (Exception e)
		{
			throw new Expected ("Matrix", e);
		}
	}


	/**
	 * check for undefined value
	 * @param value value to be checked
	 * @return de-listed value in absence of errors
	 * @throws UndefinedValueError for reference to undefined value
	 * @throws EmptyParameterList for empty list
	 */
	public GenericValue
		check (GenericValue value)
	throws UndefinedValueError, EmptyParameterList
	{
		if (isUndefinedValue (value))
		{ throw new UndefinedValueError (value); }
		return delist (value);
	}


	/**
	 * check for list node
	 * @param value the generic being checked
	 * @return first in list if found otherwise expect simple value
	 * @throws EmptyParameterList for any empty list
	 */
	public GenericValue delist (GenericValue value) throws EmptyParameterList
	{
		if (isParameterList (value))
		{
			GenericValueList list;
			//TODO: process ValueListStorage
			if ((list = valuesOfList (value)).size () == 0)
			{ throw new EmptyParameterList (); }
			value = list.get (0);
		}
		return value;
	}


}


/**
 * storage of name of constant
 */
class NamedValue implements ValueManager.GenericValue
{
	
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ValueManager.GenericValue#getName()
	 */
	public String getName () { return name; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ValueManager.GenericValue#setName(java.lang.String)
	 */
	public void setName (String name) { this.name = name; }
	String name = null;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ValueManager.GenericValue#getMetadata()
	 */
	public ValueManager.Metadata getMetadata() { return metadata; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ValueManager.GenericValue#setMetadata(net.myorb.math.expressions.ValueManager.Metadata)
	 */
	public void setMetadata(ValueManager.Metadata metadata) { this.metadata = metadata; }
	ValueManager.Metadata metadata = null;

}


/**
 * notation of a value introduced by an undefined symbol reference
 */
class UndefinedSymbolReference extends NamedValue
	implements ValueManager.UndefinedValue
{
	/**
	 * identify symbol referenced
	 * @param named the name of the symbol
	 */
	UndefinedSymbolReference (String named) { setName (named); }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Undefined symbol reference: " + name; }
}


/**
 * storage for parameter lists
 * @param <T> type on which operations are to be executed
 */
class ValueListStorage extends NamedValue
	implements ValueManager.ValueList,
		SimpleUtilities.Container
{
	ValueListStorage ()
	{
		this.values = new ValueManager.GenericValueList();
	}

	ValueListStorage (ValueManager.GenericValueList values)
	{
		this.values = values;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return values.toString (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ValueManager.ValueList#getValues()
	 */
	public ValueManager.GenericValueList getValues () { return values; }
	ValueManager.GenericValueList values;

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.SimpleUtilities.Container#contains(java.lang.Class)
	 */
	public boolean contains (Class<?> c)
	{
		if (values.size () != 1) return false;
		return c.isInstance (values.get (0));
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.SimpleUtilities.Container#getContents(java.lang.Class)
	 */
	public <T> T getContents (Class<T> c)
	{
		return SimpleUtilities.verifyClass (values.get (0), c);
	}

	//TODO: contains (DimensionedValue) ???
}


/**
 * special treatment operators
 */
class GenericManager<T> extends NamedValue
	implements ValueManager.ManagedValue<T>
{
	protected String format (T value)
	{
		if (formatter == null)
			if (value == null) return "NULL";
			else return value.toString ();
		else return formatter.format (value);
	}
	public void setFormatter (ValueManager.Formatter<T> formatter)
	{
		this.formatter = formatter;
	}
	protected ValueManager.Formatter<T> formatter = null;
}


/**
 * storage for individual values
 * @param <T> type on which operations are to be executed
 */
class DiscreteValueStorage<T> extends GenericManager<T>
	implements ValueManager.DiscreteValue<T>
{
	DiscreteValueStorage (T value)
	{
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return format (value); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ValueManager.DiscreteValue#getValue()
	 */
	public T getValue () { return value; }
	T value;
}


/**
 * storage for arrays
 * @param <T> type on which operations are to be executed
 */
class DimensionedValueStorage<T> extends GenericManager<T>
	implements ValueManager.DimensionedValue<T>
{
	DimensionedValueStorage ()
	{
		this.values = new ValueManager.RawValueList<T> ();
	}

	DimensionedValueStorage (ValueManager.RawValueList<T> values)
	{
		this.values = values;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		if (values == null) return "NULL";
		if (values.size() == 0) return "()";
		StringBuffer buffer = new StringBuffer (); String delimiter = "(";
		for (T v : values) { buffer.append (delimiter).append (format (v)); delimiter = ", "; }
		return buffer.append (")").toString ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ValueManager.DimensionedValue#getValues()
	 */
	public ValueManager.RawValueList<T> getValues () { return values; }
	ValueManager.RawValueList<T> values;
}


/**
 * storage for matrices
 * @param <T> type on which operations are to be executed
 */
class MatrixStorage<T> extends NamedValue
		implements ValueManager.MatrixValue<T>
{

	public MatrixStorage(Matrix<T> matrix)
	{
		this.matrix = matrix;
	}

	public String toString ()
	{
		return "MAT(" + matrix.rowCount() + "," + matrix.columnCount() + ")";
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ValueManager.MatrixValue#getMatrix()
	 */
	public Matrix<T> getMatrix () { return matrix; }
	Matrix<T> matrix;
}


/**
 * storage of any text
 */
class TextStorage extends NamedValue
	implements ValueManager.TextValue, ValueManager.CapturedValue
{

	public TextStorage(String text)
	{
		this.text = text;
	}
	public String getText() { return text; }
	public String toString () { return text; }
	String text;
	
}


/**
 * general pointer implementing indirect access
 */
class Pointer extends NamedValue
	implements ValueManager.IndirectAccess
{

	public String toString ()
	{ return OP + referenced.getName (); }
	public Object getReferenced () { return referenced; }
	public Pointer (NamedValue referenced)
	{ this.referenced = referenced; }
	NamedValue referenced;

	static final String OP =
		OperatorNomenclature.ADDRESS_OF_OPERATOR;

}


/**
 * procedure parameter reference
 */
class ExecutableReference<T> extends NamedValue
	implements ValueManager.Executable<T>
{

	public String toString ()
	{ return subroutine.getName (); }
	public Subroutine<T> getSubroutine () { return subroutine; }
	public ExecutableReference (DefinedFunction<T> subroutine)
	{ this.subroutine = subroutine; }
	DefinedFunction<T> subroutine;
	
}


/**
 * access to any Object treated as Structure
 */
class StructureStorage extends NamedValue
	implements ValueManager.StructuredValue
{

	public StructureStorage(Object object)
	{
		this.object = object;
	}
	public String toString () { return object.toString (); }
	public Object getStructure()
	{
		return object;
	}
	Object object;

}

