
package net.myorb.math.expressions.symbols;

import net.myorb.data.abstractions.SimpleUtilities;

import net.myorb.math.expressions.evaluationstates.Subroutine;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.expressions.SymbolMap.SymbolType;

import net.myorb.math.expressions.GreekSymbols;
import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.SymbolMap;

import net.myorb.math.SpaceManager;

import java.util.List;

/**
 * common parent for User Defined and Imported functions
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public abstract class AbstractFunction <T> extends Subroutine <T>
	implements SymbolMap.ParameterizedFunction
{


	/**
	 * describe constraints placed on function domains
	 * @param <T> type on which operations are to be executed
	 */
	public interface DomainConstraints <T>
	{
		/**
		 * the value pair
		 *  making the constraint
		 * @param lo the lo bound
		 * @param hi the hi bound
		 */
		void set (T lo, T hi);

		/**
		 * copy the constraint
		 *  expressions out for external use
		 * @param values a string array with [0]=lo, [1]=hi
		 */
		void copyConstraints (String[] values);

		/**
		 * @return the lo bound value
		 */
		T getLo ();

		/**
		 * @return the hi bound value
		 */
		T getHi ();
	}


	/**
	 * create a symbol table entry for a function
	 * @param name the name of the function to be created
	 * @param parameterNames the names of the parameters defined for the function
	 * @param functionTokens the token stream that defines the function behavior
	 */
	public AbstractFunction
		(
			String name, List<String> parameterNames,
			TokenParser.TokenSequence functionTokens
		)
	{
		super (parameterNames, functionTokens);
		setName (name);
	}


	/**
	 * cast Object to
	 *  AbstractFunction when appropriate
	 * @param from source Object for the cast
	 * @return Object cast to AbstractFunction
	 * @param <T> data type
	 */
	public static <T> AbstractFunction<T> cast (Object from)
	{
		@SuppressWarnings ("unchecked") AbstractFunction<T>
		f = SimpleUtilities.verifyClass (from, AbstractFunction.class);
		return f;
	}


	/**
	 * @return a DomainConstraints object
	 */
	public DomainConstraints<T>
	getDomainConstraints () { return domainConstraints; }
	protected DomainConstraints<T> domainConstraints = null;
	public void setDomainConstraints (DomainConstraints<T> domainConstraints)
	{ this.domainConstraints = domainConstraints; }

	/**
	 * @return formatted text describing constraints
	 */
	public String getConstraintsForDisplay () { return domainConstraints==null? "": domainConstraints.toString (); }

	/**
	 * describe constraints as (lo, hi)
	 * @param lo the lo bound value of the constraint
	 * @param hi the hi bound value of the constraint
	 * @param environment common access to primitives
	 */
	public void setDomainConstraints (T lo, T hi, Environment<T> environment)
	{
		this.domainConstraints = new DomainStorage<T> (lo, hi, environment);
	}

	/**
	 * describe constraints as (lo, hi) * symbol
	 * @param lo the lo bound multiplier of the symbol
	 * @param hi the hi bound multiplier of the symbol
	 * @param symbol the symbol that scales the bounds
	 * @param environment common access to primitives
	 */
	public void setSymbolicDomainConstraints
	(T lo, T hi, SymbolMap.VariableLookup symbol, Environment<T> environment)
	{
		this.domainConstraints = new DomainStorage<T> (lo, hi, symbol, environment);
	}

	/**
	 * copy the constraint
	 *  expressions out for external use
	 * @param values a string array with [0]=lo, [1]=hi
	 */
	public void copyConstraints (String[] values)
	{
		if (domainConstraints == null) return;
		domainConstraints.copyConstraints (values);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.ParameterizedFunction#getParameterList()
	 */
	public String getParameterList () { return getParameterNameList ().getAnnotatedNameList () + "  " + getConstraintsForDisplay (); }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Operation#getPrecedence()
	 */
	public int getPrecedence () { return SymbolMap.FUNCTTION_PRECEDENCE; }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Named#formatPretty()
	 */
	public String formatPretty () { return toFormatted (true); }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Named#getSymbolType()
	 */
	public SymbolType getSymbolType () { return SymbolType.PARAMETERIZED; }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.ParameterizedFunction#markupForDisplay(java.lang.String, java.lang.String, net.myorb.math.expressions.gui.rendering.NodeFormatting)
	 */
	public String markupForDisplay (String operator, String parameters, NodeFormatting using)
	{
		return "";
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Named#getName()
	 */
	public String getName () { return name; }
	public void setName (String name) { this.name = name; }
	protected String name;


}


/**
 * storage for function Domain Constraints
 * @param <T> type on which operations are to be executed
 */
class DomainStorage<T> implements AbstractFunction.DomainConstraints<T>
{


	void setDisplay (String lo, String hi)
	{ this.displayLo = lo; this.displayHi = hi; }
	protected String displayLo, displayHi;														// ANSI versions for displays

	void setDisplayUC (String lo, String hi)
	{ this.displayLoUC = lo; this.displayHiUC = hi; }
	protected String displayLoUC, displayHiUC;													// unicode versions for displays


	/**
	 * retain information for symbol
	 * @param symbol the symbol passed from creator as multiplier
	 */
	void setSymbol (SymbolMap.VariableLookup symbol)
	{
		this.symbol = symbol; 
		sNameUC = sName = symbol.getName ();													// set both names to non-unicode
		String notation = GreekSymbols.findNotationFor (sName);									// check for Greek notation (for unicode display)
		if (notation != null) sNameUC = notation;												// found the greek version for unicode display
	}
	protected SymbolMap.VariableLookup symbol = null;										// variable contains scalar value for evaluation
	protected String sName = null, sNameUC = null;												// ANSI text, unicode text


	/**
	 * set display properties
	 *  for symbolic versions of bounds
	 * @param lo the lo bound value of the range
	 * @param hi the hi bound value of the range
	 * @param environment access to primitives
	 */
	void setSymbolic (T lo, T hi)
	{
		SpaceManager<T> sm = environment.getSpaceManager ();
		setDisplay (displayFor (lo, sName, sm), displayFor (hi, sName, sm));
		setDisplayUC (displayFor (lo, sNameUC, sm), displayFor (hi, sNameUC, sm));
		calcBounds (lo, hi);
	}
	String displayFor (T value, String name, SpaceManager<T> manager)
	{																							// examples:
		if (manager.isZero (value)) return "0";													// 0 (zero means zero)
		if (manager.isZero (manager.add (value, manager.newScalar (1)))) return "-" + name;		// -pi {eliminate 1 as multiplier)
		if (manager.isZero (manager.add (value, manager.newScalar (-1)))) return name;			// pi (both plus and minus)
		return intTruncate (value, manager) + " * " + name;										// 2 * pi
	}
	String intTruncate (T value, SpaceManager<T> manager)										// choose to display as
	{																							// real or integer depending
		int intVal = manager.toNumber (value).intValue ();										// on value mantissa
		if (manager.isZero (manager.add (value, manager.newScalar (-intVal))))
		{ return Integer.toString (intVal); }
		else return value.toString ();
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		return "[ " + displayLoUC + ", " + displayHiUC + " ]";
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.AbstractFunction.DomainConstraints#copyConstraints(java.lang.String[])
	 */
	public void copyConstraints (String[] values)
	{ values[0] = displayLo; values[1] = displayHi; }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.AbstractFunction.DomainConstraints#set(java.lang.Object, java.lang.Object)
	 */
	public void set(T lo, T hi)
	{
		String l, h;
		SpaceManager<T> sm = environment.getSpaceManager ();
		setDisplay (l = intTruncate (lo, sm), h = intTruncate (hi, sm));								// select display format int|decimal
		this.lo = lo; this.hi = hi;																		// save values of lo and hi for getters
		setDisplayUC (l, h);																			// save formatted text for display
	}
	void calcBounds (T lo, T hi)
	{
		T scalar = getScalar ();
		SpaceManager<T> sm = environment.getSpaceManager ();
		this.lo = sm.multiply (scalar, lo); this.hi = sm.multiply (scalar, hi);							// multiply symbol value into bounds
	}
	T getScalar ()
	{
		ValueManager.GenericValue symbolValue = symbol.getValue ();										// get value from symbol and convert
		return environment.getValueManager ().toDiscrete (symbolValue);									// to generic representation
	}
	protected T lo, hi;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.AbstractFunction.DomainConstraints#getHi()
	 */
	public T getHi() { return hi; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.AbstractFunction.DomainConstraints#getLo()
	 */
	public T getLo() { return lo; }


	DomainStorage (T lo, T hi, SymbolMap.VariableLookup symbol, Environment<T> environment)
	{ this.setSymbol (symbol); this.environment = environment; setSymbolic (lo, hi); }

	DomainStorage (T lo, T hi, Environment<T> environment)
	{ this.environment = environment; set (lo, hi); }
	protected Environment<T> environment = null;


}

