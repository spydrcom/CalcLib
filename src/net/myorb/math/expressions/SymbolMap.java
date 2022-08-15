
package net.myorb.math.expressions;

import net.myorb.math.matrices.Matrix;

import net.myorb.math.expressions.symbols.*;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.expressions.evaluationstates.Subroutine;

import net.myorb.data.abstractions.HelpTableCompiler;
import net.myorb.data.abstractions.SimpleUtilities;
import net.myorb.data.abstractions.ErrorHandling;
import net.myorb.data.abstractions.Parameters;
import net.myorb.data.abstractions.HtmlTable;

import java.util.Set;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.PrintWriter;
import java.io.PrintStream;
import java.io.File;

import java.lang.reflect.Method;

/**
 * provide symbol collection and recognition mechanisms
 * @author Michael Druckman
 */
public class SymbolMap extends HashMap <String, Object>
{


	public enum SymbolType
	{
		DELIMITER,		// a delimiter
		ASSIGNMENT,		// VariableAssignment | IndexedVariableAssignment
		POSTFIX,		// UnaryPostfixOperator
		PARAMETERIZED,	// ParameterizedFunction
		BINARY,			// BinaryOperator
		UNARY,			// UnaryOperator
		IDENTIFIER,		// Identifier
		LIBRARY
	}


	/**
	 * Symbol type not recognized
	 */
	public static class Unrecognized extends ErrorHandling.Terminator
	{
		public Unrecognized () { super ("Symbol type not recognized"); }
		private static final long serialVersionUID = 6542030733553905564L;
	}


	/**
	 * collection for lists of symbol names
	 */
	public static class SymbolNameList extends SimpleUtilities.ListOfNames
	{ private static final long serialVersionUID = -5895775067298989745L; }


	/**
	 * mappings of symbol lists with named type association
	 */
	public static class TypeMap extends HashMap <String, SymbolNameList>
	{ private static final long serialVersionUID = -2726458576759185895L; }


	/**
	 * a symbol must have an associated name
	 */
	public interface Named
	{
		/**
		 * get the name of the symbol
		 * @return the name of the symbol
		 */
		String getName ();

		/**
		 * get type of symbol
		 * @return identification of symbol type
		 */
		SymbolType getSymbolType ();
	}

	/**
	 * a library of imported functions
	 */
	public interface Library extends Named
	{
		/**
		 * get a mapping of methods available from this library
		 * @return map of name to method
		 */
		Map<String, Method> getMethods ();

		/**
		 * @return map of parameter objects posted to library
		 */
		Map<String, Object> getParameterization ();
	}

	/**
	 * a variable is a named value or
	 *  a named space capable of being assigned a value
	 */
	public interface VariableLookup extends Named
	{
		/**
		 * get the value assigned to this variable
		 * @return the value assigned to this variable
		 */
		ValueManager.GenericValue getValue ();

		/**
		 * allow user defined symbols to be renamed
		 * @param to the new name for the symbol
		 */
		void rename (String to);
	}

	/**
	 * an operation must be assigned a precedence
	 *  to identify the execution order of the expression operators
	 */
	public interface Operation extends Named
	{
		/**
		 * get the assigned precedence value
		 * @return the assigned precedence value
		 */
		int getPrecedence ();
	}

	/**
	 * delimiters carry precedence with common type
	 */
	public static abstract class Delimiter implements Operation
	{
		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.SymbolMap.Named#getSymbolType()
		 */
		public SymbolType getSymbolType () { return SymbolType.DELIMITER; }
	}

	public static abstract class Assignment extends Delimiter
	{
		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.SymbolMap.Named#getSymbolType()
		 */
		public SymbolType getSymbolType () { return SymbolType.ASSIGNMENT; }
	}

	/**
	 * an operator providing for variable assignment
	 */
	public interface VariableAssignment extends Operation
	{
		/**
		 * identify the symbol map to hold the assigned value
		 * @return the symbol table object
		 */
		SymbolMap getSymbolMap ();
	}

	/**
	 * an operator providing for indexed variable assignment
	 */
	public interface IndexedVariableAssignment extends Operation
	{
		/**
		 * identify the symbol map to hold the assigned value
		 * @return the symbol table object
		 */
		SymbolMap getSymbolMap ();

		/**
		 * retrieve the list of values of the indicies
		 * @return the values of the indicies
		 */
		ValueManager.GenericValue getIndexValues ();
	}

	/**
	 * single parameter executable operation
	 */
	public interface ExecutableUnaryOperator extends Operation
	{
		/**
		 * execute the unary operation
		 * @param parameter the value popped from the value stack
		 * @return the operation result to be pushed back on the stack
		 */
		ValueManager.GenericValue execute (ValueManager.GenericValue parameter);
	}

	/**
	 * one level of the stack will be popped, the operator will be executed,
	 *  and one level of the stack will be pushed with the operation result.
	 *  the operator will come before the value in the token stream
	 */
	public interface UnaryOperator extends ExecutableUnaryOperator
	{
		/**
		 * format markup for operation
		 * @param operator the operator symbol
		 * @param operand the text of the operand
		 * @param fenceOperand TRUE = operand must be enclosed in parenthesis
		 * @param using markup formatting object for display
		 * @return text of the formatted operation
		 */
		String markupForDisplay (String operator, String operand, boolean fenceOperand, NodeFormatting using);
		String markupForDisplay (String operator, String operand, NodeFormatting using);
	}
	public interface CalculusOperator extends UnaryOperator {}

	/**
	 * one level of the value stack will be popped, the operator will be executed,
	 *  and one level of the stack will be pushed with the operation result.
	 *  the operator will come after the value in the token stream
	 */
	public interface UnaryPostfixOperator extends ExecutableUnaryOperator
	{
		/**
		 * format markup for operation
		 * @param operand the text of the operand
		 * @param fenceOperand TRUE = operand must be enclosed in parenthesis
		 * @param operator the operator symbol or identifier name
		 * @param using markup formatting object to be used
		 * @return text of the formatted operation
		 */
		String markupForDisplay (String operand, boolean fenceOperand, String operator, NodeFormatting using);
	}

	/**
	 * two levels of the value stack will be popped, the operator will be executed,
	 *  and one level of the stack will be pushed with the operation result.
	 *  the operator will come between the values in the token stream
	 */
	public interface BinaryOperator extends Operation
	{
		/**
		 * execute the binary operation
		 * @param left the first of the values pushed
		 * @param right the latter of the values pushed
		 * @return the operation result to be pushed back on the stack
		 */
		ValueManager.GenericValue execute (ValueManager.GenericValue left, ValueManager.GenericValue right);

		/**
		 * format markup for operation
		 * @param operator the operator symbol
		 * @param firstOperand the text of the first operand
		 * @param secondOperand the text of the second operand
		 * @param fenceFirst left side should be made parenthetical
		 * @param fenceSecond right side should be made parenthetical
		 * @param using markup formatting object for display
		 * @return text of the formatted operation
		 */
		String markupForDisplay
		(
			String operator,
			String firstOperand, String secondOperand,
			boolean fenceFirst, boolean fenceSecond,
			NodeFormatting using
		);
	}

	/**
	 * parameters are popped from the value stack,
	 *  the function is evaluated, and the result is pushed back on the stack
	 */
	public interface ParameterizedFunction extends ExecutableUnaryOperator
	{
		/**
		 * format markup for operation
		 * @param operator the operator symbol
		 * @param parameters the text of the parameter set
		 * @param using markup formatting object for display
		 * @return text of the formatted operation
		 */
		String markupForDisplay (String operator, String parameters, NodeFormatting using);

		/**
		 * format the formal parameter list for display
		 * @return the text of the display
		 */
		String getParameterList ();

		/**
		 * translate notations for improved display quality
		 * @return the text with notations substituted
		 */
		String formatPretty ();
	}


	/**
	 * description of symbols that are in-line configured imports from libraries
	 */
	public interface ConfiguredImport
	{
		/**
		 * @return hash of parameters that specify the symbol
		 */
		public Map<String, Object> getConfiguration ();
	}
	public interface ImportedConsumer extends ConfiguredImport {}
	public interface ImportedFunction extends ConfiguredImport {}

	
	/**
	 * provide a factory for importing symbols
	 */
	public interface FactoryForImports
	{
		/**
		 * @param named the name to assign to the symbol
		 * @param configuration the hash of parameters that specify the symbol
		 * @return a Named symbol configured as specified
		 */
		public Named importSymbolFrom (String named, Map<String, Object> configuration);
	}


	/**
	 * force render to use markupForDisplay for function
	 */
	public interface EnhancedFunctionFormattingRequirement {}


	/**
	 * connect child to parent
	 * @param parent the parent for this table
	 */
	public void setParent (SymbolMap parent)
	{
		this.parent = parent;
	}
	public SymbolMap getParent () { return parent; }
	SymbolMap parent = null;


	/**
	 * add an item to the symbol table
	 * @param item the object to be added
	 */
	public void add (Named item)
	{
		put (item.getName (), item);
		if (parent != null) parent.add (item);
	}
	public void addToExposedItems (Named item)
	{
		if (item instanceof ParameterizedFunction) add (item);
		else exposedItems.add (item.getName ());
	}
	SymbolNameList exposedItems = new SymbolNameList ();


	/**
	 * include a description in the help table
	 * @param item the item being added
	 * @param description help text
	 */
	public void add (Named item, String description)
	{
		String name;
		put (name = item.getName (), item);
		addDescription (name, description);
	}

	/**
	 * add description of function to help table
	 * @param name the name of the function being described
	 * @param description the text of the description
	 */
	public void addDescription (String name, String description)
	{
		helpTable.put (name, description);
	}
	public String getDescription (String name)
	{
		SymbolMap s = this;
		while (s != null)
		{
			String d = s.helpTable.get (name);
			if (d != null) return d;
			s = s.parent;
		}
		return null;
	}
	protected HashMap<String, String> helpTable = new HashMap<String, String> ();
	


	/**
	 * find an item in the symbol table
	 * @param name the name of the item to be found
	 * @return the object found with specified name
	 */
	public Named lookup (String name)
	{
		Named n;
		for (SymbolMap s = this; s != null; s = s.getParent ())
		{ if ((n = (Named) s.get (name)) != null ) return n; }
		return null;
	}


	/**
	 * determine that symbol is available
	 * @param name the name of the item to be found
	 * @return the named object found
	 */
	public Named verify (String name)
	{
		Named n;
		if ((n = lookup (name)) == null)
		{ throw new ErrorHandling.Terminator ("Symbol '" + name + "' is not available"); }
		return n;
	}


	/**
	 * get value associated with name
	 * @param name the name of a symbol
	 * @return the value for this name
	 */
	public ValueManager.GenericValue getValue (String name)
	{
		return getValue (verify (name));
	}


	/**
	 * get value of variable
	 * @param item must be a VariableLookup object
	 * @return value of the variable represented by name
	 * @throws RuntimeException for incorrect object type
	 */
	public ValueManager.GenericValue getValue (Named item) throws RuntimeException
	{
		if (item instanceof VariableLookup)
		{ return ((VariableLookup) item).getValue (); }
		throw new RuntimeException ("Value expected, symbol " + item.getName () + " found");
	}


	/*
	 * a list of ordered precedence values
	 */

	public static final int
	OPEN_ARRAY_PRECEDENCE = -2,
	CLOSE_ARRAY_PRECEDENCE = -1,
	STACK_COMPLETION_PRECEDENCE = 0,
	OPEN_GROUP_PRECEDENCE = 1,
	CLOSE_GROUP_PRECEDENCE = 2,
	CONTINUE_GROUP_PRECEDENCE = 3,
	DEFINITION_PRECEDENCE = 4,
	ASSIGNMENT_PRECEDENCE = 5,
	STORAGE_PRECEDENCE = 6,
	ADDITION_PRECEDENCE = 7,
	MULTIPLICATION_PRECEDENCE = 8,
	EXPONENTIATION_PRECEDENCE = 9,
	FUNCTTION_PRECEDENCE = 10,
	CALCULUS_PRECEDENCE = 11,
	INDEX_PRECEDENCE = 12;
	

	public static Operation getOpenOperator ()
	{
		return new Delimiter ()
		{
			public int getPrecedence () { return OPEN_GROUP_PRECEDENCE; }
			public String getName () { return OperatorNomenclature.START_OF_GROUP_DELIMITER; }
		};
	}

	public static Operation getGroupOperator ()
	{
		return new Delimiter ()
		{
			public int getPrecedence () { return CONTINUE_GROUP_PRECEDENCE; }
			public String getName () { return OperatorNomenclature.GROUP_CONTINUATION_DELIMITER; }
		};
	}

	public static Operation getCloseOperator ()
	{
		return new Delimiter ()
		{
			public int getPrecedence () { return CLOSE_GROUP_PRECEDENCE; }
			public String getName () { return OperatorNomenclature.END_OF_GROUP_DELIMITER; }
		};
	}

	public static Operation getArrayStartOperator ()
	{
		return new Delimiter ()
		{
			public int getPrecedence () { return OPEN_ARRAY_PRECEDENCE; }
			public String getName () { return OperatorNomenclature.START_OF_ARRAY_DELIMITER; }
		};
	}

	public static Operation getArrayCloseOperator ()
	{
		return new Delimiter ()
		{
			public int getPrecedence () { return CLOSE_ARRAY_PRECEDENCE; }
			public String getName () { return OperatorNomenclature.END_OF_ARRAY_DELIMITER; }
		};
	}

	public static Operation getAssignmentOperator ()
	{
		return new Assignment ()
		{
			public int getPrecedence () { return ASSIGNMENT_PRECEDENCE; }
			public String getName () { return OperatorNomenclature.ASSIGNMENT_KEYWORD; }
		};
	}

	public static Operation getAssignmentPrefix ()
	{
		return new Assignment ()
		{
			public int getPrecedence () { return ASSIGNMENT_PRECEDENCE; }
			public String getName () { return OperatorNomenclature.ASSIGNMENT_PREFIX; }
		};
	}

	public static Operation getEqualsOperator ()
	{
		return new Assignment ()
		{
			public int getPrecedence () { return STORAGE_PRECEDENCE; }
			public String getName () { return OperatorNomenclature.ASSIGNMENT_DELIMITER; }
		};
	}

	public static Operation getDefinitionOperator ()
	{
		return new Assignment ()
		{
			public int getPrecedence () { return DEFINITION_PRECEDENCE; }
			public String getName () { return OperatorNomenclature.DEFINITION_KEYWORD; }
		};
	}

	/**
	 * describe the built-in operators
	 *  that drive the expression interpretation
	 */
	public void addCoreOperators ()
	{
		add (getOpenOperator ());
		add (getGroupOperator ());
		add (getAssignmentPrefix ());
		add (getDefinitionOperator ());
		add (getAssignmentOperator ());
		add (getArrayStartOperator ());
		add (getArrayCloseOperator ());
		add (getEqualsOperator ());
		add (getCloseOperator ());
	}


	/**
	 * an operator stack marker showing the anaylsis is complete
	 * @return the object that serves as the marker
	 */
	public static Operation getTerminator ()
	{
		return new Operation ()
		{
			public int getPrecedence ()
			{ return STACK_COMPLETION_PRECEDENCE; }
			public String getName () { return ""; }
			public SymbolType getSymbolType ()
			{ return SymbolType.DELIMITER; }
		};
	}


	/**
	 * save symbols and functions to a file
	 * @param filename the name of the file to write
	 * @param formatter a space manager to use as formatter
	 * @param exporter a dataIO object to use for export
	 * @param notify the print stream to notify results
	 */
	@SuppressWarnings ({"rawtypes", "unchecked"})
	public void save
		(
			String filename, ExpressionSpaceManager formatter,
			DataIO exporter, PrintStream notify
		)
	{
		try
		{
			File f =
				new File ("scripts/" + filename);
			PrintWriter out = new PrintWriter (f);
			notify.println ("Saving... " + f.getAbsolutePath ());
			formatSymbols (getSymbolList (), formatter, exporter, out);
			formatFunctions (getFunctionList (), false, out);
			out.close ();
		}
		catch (Exception e)
		{
			throw new RuntimeException ("File save failed");
		}
	}


	/**
	 * @return list of function names
	 */
	public SymbolNameList getFunctionList ()
	{
		SymbolNameList seg = getTypeMap ().get (typeTable.get ("Splines"));
		SymbolNameList list = getTypeMap ().get (typeTable.get ("Functions"));
		if (seg != null) if (list != null) list.addAll (seg); else list = seg;
		if (list != null) list.addAll (exposedItems); else list = exposedItems;
		return list;
	}

	/**
	 * @return list of variable names
	 */
	public SymbolNameList getSymbolList ()
	{
		return getTypeMap ().get (typeTable.get ("Symbols"));
	}


	/**
	 * access to list builders
	 */
	public interface ListFactory
	{
		/**
		 * @return the list appropriate to this factory
		 */
		SymbolNameList getList ();

		/**
		 * @return the symbol map holding the named items
		 */
		SymbolMap getMap ();
	}
	public ListFactory getSymbolListFactory ()
	{
		return new ListFactory ()
		{
			public SymbolNameList
				getList () { return getSymbolList (); }
			public SymbolMap getMap () { return SymbolMap.this; }
		};
	}
	public ListFactory getFunctionListFactory ()
	{
		return new ListFactory ()
		{
			public SymbolNameList
				getList () { return getFunctionList (); }
			public SymbolMap getMap () { return SymbolMap.this; }
		};
	}


	/**
	 * write function descriptions to file
	 * @param symbols list of names of symbols
	 * @param formatter expression manager holds value formatter
	 * @param exporter a dataIO object to use for export
	 * @param out print writer for file
	 * @param <T> data type
	 */
	@SuppressWarnings ("unchecked")
	public <T> void formatSymbols
		(
			SymbolNameList symbols,
			ExpressionSpaceManager<T> formatter,
			DataIO<T> exporter, PrintWriter out
		)
	{
		if (symbols == null) return;
		out.println (); out.println ();
		for (String symbol : symbols)
		{
			AbstractVariableLookup v =
				(AbstractVariableLookup) get (symbol);
			if (v instanceof AbstractBuiltinVariableLookup) continue;

			ValueManager.GenericValue generic = v.getValue ();
			ValueManager.MatrixValue<T> matrixValue = SimpleUtilities.verifyClass
					(generic, ValueManager.MatrixValue.class);
			if (matrixValue != null)
			{
				String filename = symbol + ".TDF";
				Matrix<T> m = matrixValue.getMatrix ();
				out.print ("IMPORT "); out.print (symbol);
				out.print (" "); out.print (filename);
				exporter.write (filename, m);
			}
			else
			{
				out.print (symbol); out.print (" = ");
				out.print (ValueManager.format (generic, formatter));
			}
			out.println ();
		}
	}


	/**
	 * write function descriptions to file
	 * @param functions list of names of functions
	 * @param pretty attempt to improve display
	 * @param out print writer for file
	 * @param <T> data type
	 */
	public <T> void formatFunctions
	(SymbolNameList functions, boolean pretty, PrintWriter out)
	{
		if (functions == null) return;
		out.println (); out.println ();
		for (String function : functions)
		{
			Subroutine<T> s = Subroutine.cast (get (function));
			Parameters parameters = s.getParameterNames ();
			out.print ("!! "); out.print (function);

			if (parameters.size () == 0)
				out.print ("()");
			else
			{
				String delimiter = "(";
				for (String p : parameters)
				{ out.print (delimiter); out.print (p); delimiter = ","; }
				out.print (")");
			}

			out.print (" = ");
			out.print (s.toFormatted (pretty));
			out.println ();
		}
	}


	/**
	 * dump THIS symbol table to system output
	 * @param limitedToType type to be dumped, null = ALL
	 * @param formatter expression manager holds value formatter
	 * @param out the print writer to use for output
	 * @param <T> data type
	 */
	public <T> void dump (String limitedToType, ExpressionSpaceManager<T> formatter, PrintWriter out)
	{
		out.println ();
		out.println ("===");
		out.println ("=  Symbol Directory");
		out.println ("===");
		out.println ();

		int n = 0; TypeMap typeMap = getTypeMap ();
		if (limitedToType != null) limitedToType = typeTable.get (limitedToType);
		for (String type : SimpleUtilities.orderedKeys (typeMap))
		{
			if (limitedToType == null || limitedToType.equals (type))
			{
				out.println (typeTable.get (type));									// each type name is a header

				for (String name : typeMap.get (type))
				{
					out.println (); out.print ("\t");
					String desc = helpTable.get (name);
					String notation = GreekSymbols.findNotationFor (name);
					if (notation != null) out.print (notation);
					out.print ("\t"); out.print (name);
					if (desc != null) { out.print ("\t"); out.print (desc); }
					printType (name, formatter, out);
					n++;
				}
			}
		}

		out.println ();
		if (n == 0) out.println ("No symbols to show");
		else out.println (n + " symbols found and displayed");
		out.println ();
	}


	/**
	 * format a description of the symbol type
	 * @param forName the name of the object being described
	 * @param formatter the space manager for the data type
	 * @param out print writer for file
	 * @param <T> data type
	 */
	@SuppressWarnings("unchecked")
	public <T> void printType
		(
			String forName,
			ExpressionSpaceManager<T> formatter,
			PrintWriter out
		)
	{
		Object description, o;											// each symbol of that type is enumerated
		Subroutine<T> s = Subroutine.cast (o = get (forName));

		if (s != null)													// for subroutines the token list is dumped
		{
			out.println ();
			out.print ("\t\t");
			out.print (s.getParameterNameList ().getAnnotatedProfile ());
			out.print ("\t"); out.print (s.toPrettyText ());
		}
		else if (o instanceof AbstractVariableLookup &&
				!(o instanceof AbstractBuiltinVariableLookup))			// for variables the value is dumped
		{
			out.print ("\t");
			AbstractVariableLookup v = (AbstractVariableLookup)o;
			if (v instanceof AbstractBuiltinVariableLookup) return;
			out.print (ValueManager.format (v.getValue (), formatter));
		}
		else if (o instanceof LibraryObject)							// for Library the classpath is dumped
		{
			out.print ("\t");
			out.print (((LibraryObject<T>)o).formatPretty ());
		}
		else if ((description = helpTable.get (forName)) != null)
		{
			out.print ("\t");
			out.print (description);
		}
		else
		{																// other types just show the simple class name
			out.print ("\t");
			out.print (whatIs (o));
		}
		out.println ();
	}


	/**
	 * map type name to objects of that type
	 * @return a hash map of type name to array of symbol name
	 */
	public TypeMap getTypeMap ()
	{
		List <String> names =
				SimpleUtilities.orderedKeys (this);
		TypeMap typeMap; addNamesTo (typeMap = new TypeMap (), names);
		return typeMap;
	}


	/**
	 * add named symbols to type map
	 * @param typeMap the map being constructed
	 * @param names the list of names to be added
	 */
	public void addNamesTo (TypeMap typeMap, List <String> names)
	{
		for (String name : names)
		{
			if (name == null) continue;
			Object symbolFoundForName = get (name); // symbol name added to type name list
			String typeName = symbolFoundForName.getClass ().getSuperclass ().getSimpleName ();
			getTypeList (typeMap, typeName).add (name);
		}
	}


	/**
	 * get list of symbols with named type
	 * @param fromMap the map of typed symbols
	 * @param named the name of the type to be found
	 * @return the symbol list for the type name
	 */
	public SymbolNameList getTypeList (TypeMap fromMap, String named)
	{
		SymbolNameList items = fromMap.get (named);
		if (items != null) return items;
		items = new SymbolNameList ();
		fromMap.put (named, items);
		return items;
	}


	/**
	 * display table of operators with descriptions
	 * @return HTML for Operator help document
	 */
	public HtmlTable getOperatorHelpDocument ()
	{
		return new HelpTableCompiler ("CalcLib Operator Help", "Active Operators in CalcLib Build " + getVersion ())
		.setColumnHeaders ("Operator", "A simple description of the actions of each operator").buildFrom
		(
			new HelpTableCompiler.TableCompilationAccess ()
			{
				public Set<String> getElementNames () { return helpTable.keySet (); }
				public String descriptionFor (String name) { return helpTable.get (name); }
			}
		);
	}


	/**
	 * @return the version of the CalcLib build
	 */
	public String getVersion () { return lookup ("VERSION").toString (); }


	/*
	 * map class names to meaningful mathematical abstractions
	 */

	static final HashMap<String, String> typeTable = new HashMap<String, String> ();

	static
	{
		typeTable.put ("Library", "OperationObject");
		typeTable.put ("SplineDescriptor", "Splines");
		typeTable.put ("Splines", "SplineDescriptor");
		typeTable.put ("Functions", "AbstractFunction");
		typeTable.put ("Symbols", "AbstractVariableLookup");
		typeTable.put ("BuiltIn", "AbstractBuiltinVariableLookup");
		typeTable.put ("AbstractParameterizedFunction", "Built-In Functions");
		typeTable.put ("AbstractUnaryPostfixOperator", "Unary Post-Fix Operators");
		typeTable.put ("AbstractBuiltinVariableLookup", "Built-In Symbols");
		typeTable.put ("AbstractBinaryOperator", "Binary Operators");
		typeTable.put ("AbstractModifiedOperator", "Operator Modifiers");
		typeTable.put ("AbstractUnaryOperator", "Unary Operators");
		typeTable.put ("Assignment", "Assignment Operators");
		typeTable.put ("AbstractVariableLookup", "Symbols");
		typeTable.put ("Delimiter", "Group Delimiters");
		typeTable.put ("AbstractFunction", "Functions");
		typeTable.put ("Object", "Built-In Delimiter");
		typeTable.put ("OperationObject", "Library");
	}


	/**
	 * give a name to the type of an object
	 * @param o the object being typed
	 * @return name of the type
	 */
	@SuppressWarnings ("rawtypes")
	public String whatIs (Object o)
	{
		Class c = o.getClass ().getSuperclass ();					// the superclass is associated with the symbol type
		String n = typeTable.get (c.getSimpleName ());
		if (n == null) return "Unrecognized";
		else return n;
	}


	static final long serialVersionUID = 1;


}

