
package net.myorb.math.expressions.tree;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.symbols.IterationConsumer;
import net.myorb.math.expressions.symbols.IterationConsumerImporter;
import net.myorb.math.expressions.symbols.IterationConsumerImplementations;
import net.myorb.math.expressions.tree.JsonBinding.JsonRepresentation;

import net.myorb.data.notations.json.JsonTools;
import net.myorb.data.notations.json.JsonSemantics;

import net.myorb.data.abstractions.CommonCommandParser.TokenDescriptor;
import net.myorb.data.abstractions.SimpleUtilities;

import java.util.List;
import java.util.Map;

/**
 * locate parenthetical sub-expressions in token sequence.
 *  replace found sequences with sub-expression node structure.
 * @param <T> type of values from expressions
 * @author Michael Druckman
 */
public class LexicalAnalysis<T>
{


	/**
	 * lexical analysis error for too-few/too-many parenthesis resulting in mis-match pair
	 */
	public static class ParenthesisNestingError extends Exception
	{
		public ParenthesisNestingError (String notation) { super (notation); }
		private static final long serialVersionUID = -2854770479994122091L;
	}


	/**
	 * expose symbol name and reference
	 */
	public static class SymbolProperties
	{
		/**
		 * @return name of symbol
		 */
		public String getName () { return name; }

		/**
		 * @param name the name of the symbol
		 */
		public void setName (String name) { this.name = name; }

		/**
		 * @param symbolReference the symbol table link
		 */
		public void setSymbolReference (SymbolMap.Named symbolReference) { this.symbolReference = symbolReference; }

		/**
		 * @return symbol table reference
		 */
		public SymbolMap.Named getSymbolReference () { return symbolReference; }

		/**
		 * @param symbolName the name of the symbol
		 * @param restoreManager the manager for the restore
		 * @return the symbol object for chaining
		 * @param <T> data type
		 */
		public <T> NamedSymbol restoreSymbol
		(String symbolName, JsonRestore<T> restoreManager)
		{
			setSymbolReference (restoreManager.lookup (symbolName));
			setName (symbolName); 
			return symbol;
		}

		/**
		 * @return name as a JSON string
		 */
		public JsonSemantics.JsonString getJsonName ()
		{
			return new JsonSemantics.JsonString (name);
		}

		protected SymbolMap.Named symbolReference;
		protected NamedSymbol symbol;
		protected String name;
	}


	/**
	 * symbols provide access to properties
	 */
	public interface NamedSymbol
	{
		/**
		 * @return the properties of the symbol
		 */
		SymbolProperties getSymbolProperties ();
	}


	/**
	 * generic factory for elements
	 */
	public interface ElementFactory<E extends Element>
	{
		/**
		 * @param name the name of the element
		 * @return access to the new element
		 */
		E newElementFor (String name);
	}


	/**
	 * verify the assumed cast did not fail
	 * @param object the object that has undergone casting
	 */
	public static void castingAssumptionCheck (Object object)
	{
		if (object == null) throw new RuntimeException ("Internal error: Casting Assumption");
	}


	/**
	 * description of Literals
	 * @param <T> value types held by Literals
	 */
	public static class Literal<T> extends ValuedElement<T>
	{
		public Types getElementType () { return ELEMENT_TYPE; }
		public boolean isOfType (Types type) { return type == ELEMENT_TYPE; }
		public static final Types ELEMENT_TYPE = Types.LITERAL;
	}
	public static class NumericConstant<T> extends Literal<T>
		implements JsonRepresentation<T>
	{
		public static final Types ELEMENT_TYPE = Types.CONSTANT;
		public NumericConstant (int value) { this.value = value; }
		public JsonSemantics.JsonValue toJson () { return new JsonSemantics.JsonNumber (value); }
		public Element fromJson (JsonSemantics.JsonValue context, JsonRestore<T> restoreManager) throws Exception { return null; }
		public String toString () { return Integer.toString (value); }
		public Types getElementType () { return ELEMENT_TYPE; }
		protected int value;
	}
	public static class NumericLiteral<T> extends Literal<T>
	{
		public NumericLiteral 
		(double value) { this.numericValue = value; }
		public NumericLiteral (T value, Number displayValue)
		{ setValue (value); this.numericValue = displayValue; }
		public String toString () { return numericValue.toString (); };

		/**
		 * cast Object to NumericLiteral when appropriate
		 * @param from source Object for the attempted cast
		 * @return Object cast to NumericLiteral
		 * @param <T> data type
		 */
		public static <T> NumericLiteral<T> cast (Object from)
		{
			@SuppressWarnings ("unchecked") NumericLiteral<T>
				literal = SimpleUtilities.verifyClass (from, NumericLiteral.class);
			// no assumption check, semantic layer uses null check for type verification
			return literal;
		}

		public double getLiteralValue () { return numericValue.doubleValue (); }
		protected Number numericValue;
	}
	public static class TextLiteral extends Literal<String>
	{
		public TextLiteral
		(String value) { setValue (value); }
		public String toString () { return '"' + value + '"'; };
		public Types getElementType () { return ELEMENT_TYPE; }
		public static final Types ELEMENT_TYPE = Types.TEXT;
	}
	public static <T> NumericConstant<T> getZero () { return new NumericConstant<T>(0); }
	public static <T> NumericConstant<T> getOne () { return new NumericConstant<T>(1); }


	/**
	 * manage the primitives surrounding type information
	 * @param <T> the enum of types
	 */
	public static class TypeManager<T>
	{
		TypeManager (T initially)
		{
			type = initially;
		}

		/**
		 * @return the current type value
		 */
		public T getType () { return type; }

		/**
		 * @return the name of the type wrapped as a JSON string
		 */
		public JsonSemantics.JsonString getJsonType ()
		{ return new JsonSemantics.JsonString (type.toString ()); }

		/**
		 * @param type the new value for the type
		 */
		public void setType (T type) { this.type = type; }

		/**
		 * @param toCheck value for comparison
		 * @return TRUE = match of values
		 */
		public boolean isType (T toCheck)
		{ return type == toCheck; }

		protected T type;
	}


	/*
	 * identifier representation
	 */


	/**
	 * name the members of the Identifier node type
	 */
	public enum IdentifierNodeMembers {Name, Operator, Kind, Symbol};

	/**
	 * enumeration of types of identifiers
	 */
	public enum IdentifierType
	{
		Unknown, Variable, Local, Function, Consumer, Keyword, NamedOperator
	}

	/**
	 * additional properties for Identifier symbols
	 */
	public static class IdentifierProperties extends SymbolProperties
	{

		IdentifierProperties ()
		{
			typeManager = new TypeManager<> (IdentifierType.Unknown);
		}
		private TypeManager<IdentifierType> typeManager;

		/**
		 * @return the type manager for identifiers
		 */
		public TypeManager<IdentifierType> getTypeManager () { return typeManager; }

		/**
		 * @return simple name of reference
		 */
		public String getReference ()
		{
			SymbolMap.Named sym = getSymbolReference ();
			if (sym != null) return sym.getClass ().getSimpleName ();
			return null;
		}

		/**
		 * @return name associated with named operator
		 */
		public String getOpName ()
		{ return op==null? null: op.getSymbolProperties ().getName (); }
		public IdentifierProperties setOperator (Operator op) { this.op = op; return this; }
		public Operator getOperator () { return op; }
		protected Operator op;

		/**
		 * @param type the value of type
		 * @return THIS object for SET chain calls
		 */
		public IdentifierProperties setType (IdentifierType type) { typeManager.setType (type); return this; }

		/**
		 * @param typeName the text name of the type
		 */
		public void setType (String typeName) { typeManager.setType (IdentifierType.valueOf (typeName)); }

		/**
		 * short-cut for setting identifier as LOCAL type
		 */
		public void setAsLocalType () { typeManager.setType (IdentifierType.Local); }

		/**
		 * @return a JSON description of the identifier
		 */
		public JsonSemantics.JsonValue toJson ()
		{
			JsonBinding.Node node =
				new JsonBinding.Node (JsonBinding.NodeTypes.Identifier);
			node.addMember (IdentifierNodeMembers.Symbol, JsonSemantics.stringOrNull (getReference ()));
			node.addMember (IdentifierNodeMembers.Operator, JsonSemantics.stringOrNull (getOpName ()));
			node.addMember (IdentifierNodeMembers.Kind, typeManager.getJsonType ());
			node.addMember (IdentifierNodeMembers.Name, getJsonName ());
			return node;
		}

	}

	/**
	 * description of Identifiers
	 * @param <T> value types held by Identifiers
	 */
	public static class Identifier<T> extends ValuedElement<T>
		implements NamedSymbol, JsonRepresentation<T>
	{

		public Identifier ()
		{ this.properties = new IdentifierProperties (); properties.symbol = this; }
		public Identifier (String name) { this (); properties.setName (name); }
		public String toString () { return properties.getName (); }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.LexicalAnalysis.NamedSymbol#getSymbolProperties()
		 */
		public SymbolProperties getSymbolProperties () { return properties; }
		public IdentifierProperties getIdentifierProperties () { return properties; }
		protected IdentifierProperties properties;

		/**
		 * @return the type manager for identifier type
		 */
		public TypeManager<IdentifierType> getTypeManager ()
		{ return properties.typeManager; }

		/**
		 * cast Object to Identifier when appropriate
		 * @param from source Object for the attempted cast
		 * @return Object cast to Identifier
		 * @param <T> data type
		 */
		public static <T> Identifier<T> cast (Object from)
		{
			@SuppressWarnings ("unchecked") Identifier<T>
				identifier = SimpleUtilities.verifyClass (from, Identifier.class);
			castingAssumptionCheck (identifier);
			return identifier;
		}

		/**
		 * @param name the name of the identifier
		 * @param restoreManager the manager for the restore
		 * @return a new copy of the description of the identifier
		 * @param <T> data type
		 */
		public static <T> Identifier<T> restore (String name, JsonRestore<T> restoreManager)
		{ return cast (new Identifier<> ().getSymbolProperties ().restoreSymbol (name, restoreManager)); }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.JsonBinding.JsonRepresentation#fromJson(net.myorb.data.notations.json.JsonSemantics.JsonValue, net.myorb.math.expressions.tree.JsonRestore)
		 */
		public Element fromJson (JsonSemantics.JsonValue context, JsonRestore<T> restoreManager) throws Exception
		{
			JsonBinding.Node node = new JsonBinding.Node (context);
			getSymbolProperties ().restoreSymbol (node.getMemberString (IdentifierNodeMembers.Name), restoreManager);
			getIdentifierProperties ().setType (node.getMemberString (IdentifierNodeMembers.Kind));
			return this;
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.JsonBinding.JsonRepresentation#getJson()
		 */
		public JsonSemantics.JsonValue toJson () { return getIdentifierProperties ().toJson (); }

		/**
		 * identification of this class
		 */
		public static final Types ELEMENT_TYPE = Types.IDENTIFIER;

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.Element#isOfType(net.myorb.math.expressions.tree.Element.Types)
		 */
		public boolean isOfType (Types type) { return type == ELEMENT_TYPE; }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.Element#getElementType()
		 */
		public Types getElementType () { return ELEMENT_TYPE; }
		
	}


	/*
	 * operator representation
	 */


	/**
	 * enumeration of types of operators
	 */
	public enum OperatorType {Unknown, PostFixUnary, PreFixUnary, Binary, Calculus}

	/**
	 * additional properties for Operator symbols
	 */
	public static class OperatorProperties extends SymbolProperties
	{

		OperatorProperties ()
		{
			typeManager = new TypeManager<> (OperatorType.Unknown);
		}
		private TypeManager<OperatorType> typeManager;

		/**
		 * @return the type manager for operators
		 */
		public TypeManager<OperatorType> getTypeManager () { return typeManager; }

		/**
		 * @param type the value of type
		 * @return THIS object for SET chain calls
		 */
		public OperatorProperties setType (OperatorType type) { typeManager.setType (type); return this; }

		/**
		 * @return the precedence specified for this operation in symbol table
		 */
		public int getPrecedence () { return symbolPrecedence; }
		public OperatorProperties setPrecedence (int symbolPrecedence)
		{ this.symbolPrecedence = symbolPrecedence; return this; }
		protected int symbolPrecedence;

	}

	/**
	 * description of operators
	 */
	public static class Operator implements Element, NamedSymbol
	{

		public Operator ()
		{ this.properties = new OperatorProperties (); properties.symbol = this; }
		public Operator (String name) { this (); properties.setName (name); }
		public String toString () { return properties.getName (); };

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.LexicalAnalysis.NamedSymbol#getSymbolProperties()
		 */
		public SymbolProperties getSymbolProperties () { return properties; }
		public OperatorProperties getOperatorProperties () { return properties; }
		protected OperatorProperties properties;

		/**
		 * @return the type manager for operator type
		 */
		public TypeManager<OperatorType> getTypeManager () { return properties.typeManager; }

		/**
		 * recognize Element as Operator
		 * @param e the element to treat as Operator
		 * @return the object cast to Operator
		 */
		public static Operator recognizedFrom (Element e) { return (Operator) e; }

		/**
		 * @param name the name of the operator
		 * @param restoreManager the manager for the restore
		 * @return a new copy of the description of the operator
		 * @param <T> data type
		 */
		public static <T> Operator restore (String name, JsonRestore<T> restoreManager)
		{ return (Operator) new Operator ().getSymbolProperties ().restoreSymbol (name, restoreManager); }

		/**
		 * identification of this class
		 */
		public static final Types ELEMENT_TYPE = Types.OPERATOR;

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.Element#isOfType(net.myorb.math.expressions.tree.Element.Types)
		 */
		public boolean isOfType (Types type) { return type == ELEMENT_TYPE; }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.Element#getElementType()
		 */
		public Types getElementType () { return ELEMENT_TYPE; }

	}


	/*
	 * range representation
	 */


	/**
	 * description of a range operation
	 * @param <T> the data type
	 */
	public static class RangeDescriptor<T>
		implements Element, JsonRepresentation<T>
	{

		public enum DescriptionType {UNDETERMINED, EVALUATION_POINT, RANGE_SPAN}

		public RangeDescriptor ()
		{
			this.iterationConsumer = null;
		}

		public RangeDescriptor (SubExpression<T> parent)
		{
			this (); setParent (parent);
		}

		/**
		 * identify parent expression
		 * @param parent the parent to be associated
		 */
		public void setParent (SubExpression<T> parent)
		{
			this.parent = parent;
			parent.root.descriptors.add (this);
			parent.add (this);
		}

		/**
		 * cast Object to RangeDescriptor when appropriate
		 * @param from source Object for the attempted cast
		 * @return Object cast to RangeDescriptor
		 * @param <T> data type
		 */
		public static <T> RangeDescriptor<T> cast (Object from)
		{
			@SuppressWarnings ("unchecked") RangeDescriptor<T>
				rangeDescriptor = SimpleUtilities.verifyClass (from, RangeDescriptor.class);
			castingAssumptionCheck (rangeDescriptor);
			return rangeDescriptor;
		}

		/**
		 * attach target expression
		 * @return the new target expression node
		 */
		public SubExpression<T> connectTarget ()
		{
			endpoints.components.add (endpoints);
			return (target = new TargetExpression<T> (this));
		}

		/**
		 * attach range expression
		 * @return the new range expression node
		 */
		public SubExpression<T> connectRange ()
		{
			return (endpoints = new EndpointExpression<T> (this));
		}

		/**
		 * construct expression from elements
		 * @param elements the elements of the expression
		 * @param parent the original source node
		 * @return new expression object
		 */
		public Expression<T> newSub (List<Element> elements, Expression<T> parent)
		{
			Expression<T> expr = new DescriptorExpression<T> (this);
			parent.duplicateContext (expr); expr.addAll (elements);
			return expr;
		}
		public void setEndpoints (List<Element> elements, Expression<T> parent)
		{ endpoints = newSub (elements, parent); }

		protected String variableName;
		protected Operator lbndOp, hbndOp;
		protected Expression<T> target, endpoints;
		public Expression<T> getTarget () { return target; }
		public Expression<T> getEndpoints () { return endpoints; }
		public void setTarget (Expression<T> target) { this.target = target; }
		public void setEndpoints (Expression<T> endpoints) { this.endpoints = endpoints; }
		protected Expression<T> delta = null, loExpr = null, hiExpr = null, evalExpr = null;
		protected DescriptionType descriptionType = DescriptionType.UNDETERMINED;
		protected IterationConsumer iterationConsumer;
		protected SubExpression<T> parent;

		/**
		 * @param descriptionType the identified type of descriptor
		 */
		public void setDescriptionType (DescriptionType descriptionType)
		{
			this.descriptionType = descriptionType;
		}

		/**
		 * @return TRUE if identified as a range
		 */
		public boolean describesRange ()
		{
			return this.descriptionType == DescriptionType.RANGE_SPAN;
		}

		/**
		 * @return identifier for consumer type
		 */
		public String getConsumerType ()
		{
			if (iterationConsumer == null) return null;
			else return iterationConsumer.getClass ().getSimpleName ();
		}

		/**
		 * @return display name for consumer
		 */
		public String getConsumerName ()
		{
			if (iterationConsumer == null) return "";
			else return iterationConsumer.toString ();
		}

		/**
		 * @return NULL or an object with the configuration
		 */
		public JsonSemantics.JsonValue getConsumerConfig ()
		{
			if (iterationConsumer instanceof NumericalAnalysis)
			{
				@SuppressWarnings("unchecked")
				NumericalAnalysis<T> na = ((NumericalAnalysis<T>) iterationConsumer).getAnalyzer ();
				SymbolMap.ImportedConsumer imported = (SymbolMap.ImportedConsumer) na;
				return JsonTools.toJsonObject (imported.getConfiguration ());
			} else return JsonSemantics.getNull ();
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.JsonBinding.JsonRepresentation#getJson()
		 */
		public JsonSemantics.JsonValue toJson ()
		{
			JsonBinding.Node node = new JsonBinding.Node (JsonBinding.NodeTypes.Range);
			node.addMember (RangeNodeMembers.Variable, new JsonSemantics.JsonString (variableName));
			node.addMember (RangeNodeMembers.Consumer, JsonSemantics.stringOrNull (getConsumerType ()));
			node.addMember (RangeNodeMembers.Target, target); node.addMember (RangeNodeMembers.Delta, delta);
			node.addMember (RangeNodeMembers.Lo, loExpr); node.addMember (RangeNodeMembers.Hi, hiExpr);
			node.addMember (RangeNodeMembers.Lbnd, lbndOp.getSymbolProperties ().getJsonName ());
			node.addMember (RangeNodeMembers.Hbnd, hbndOp.getSymbolProperties ().getJsonName ());
			node.addMember (RangeNodeMembers.Config, getConsumerConfig ());
			return node;
		}

		/**
		 * name the members of this node type
		 */
		public enum RangeNodeMembers {Variable, Consumer, Config, Hi, Lo, Delta, Lbnd, Hbnd, Target};

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.JsonBinding.JsonRepresentation#fromJson(net.myorb.data.notations.json.JsonSemantics.JsonValue, net.myorb.math.expressions.tree.JsonRestore)
		 */
		public Element fromJson (JsonSemantics.JsonValue context, JsonRestore<T> restoreManager) throws Exception
		{
			JsonBinding.Node node = new JsonBinding.Node (context);

			hiExpr = fromMember (RangeNodeMembers.Hi, node, restoreManager);
			delta  = fromMember (RangeNodeMembers.Delta, node, restoreManager);
			hbndOp = Operator.restore (node.getMemberString (RangeNodeMembers.Hbnd), restoreManager);
			lbndOp = Operator.restore (node.getMemberString (RangeNodeMembers.Lbnd), restoreManager);
			loExpr = fromMember (RangeNodeMembers.Lo, node, restoreManager);

			JsonSemantics.JsonValue config =
					node.getMember (RangeNodeMembers.Config);
			if (JsonSemantics.isNull (config))
			{
				iterationConsumer = IterationConsumerImplementations.getIterationConsumer
					(node.getMemberString (RangeNodeMembers.Consumer), restoreManager.spaceManager);
			}
			else { iterationConsumer = importFromNode ((JsonSemantics.JsonObject) config, restoreManager); }

			variableName = node.getMemberString (RangeNodeMembers.Variable);
			Identifier<T> id = restoreManager.getIdentifier (variableName);
			if (id == null)
			{
				id = Identifier.restore (variableName, restoreManager);
				id.getIdentifierProperties ().setAsLocalType ();
				restoreManager.newIdentifier (id);
			}
			target = fromMember (RangeNodeMembers.Target, node, restoreManager);
			target.identifiers.put (variableName, id);

			return this;
		}

		/**
		 * @param config the configuration object that identifies the consumer
		 * @param restoreManager a manager object for the restore process
		 * @return a new instance of the described consumer
		 */
		public IterationConsumer importFromNode
		(JsonSemantics.JsonObject config, JsonRestore<T> restoreManager)
		{
			SymbolMap.Named item;
			String sym = config.getMemberString ("SYMBOL");
			if ((item = (SymbolMap.Named) restoreManager.find (sym)) == null)
			{
				item = getFactory (config, restoreManager).importSymbolFrom
						(sym, JsonTools.toObjectMap (config));
			}
			return ((IterationConsumerImporter) item).getIterationConsumer ();
		}

		/**
		 * get an instance of the consumer factory
		 * @param config the configuration object that identifies the consumer
		 * @param restoreManager a manager object for the restore process
		 * @return the factory object for the consumer
		 */
		public SymbolMap.FactoryForImports getFactory
		(JsonSemantics.JsonObject config, JsonRestore<T> restoreManager)
		{
			String factoryPath = config.getMemberString ("FACTORY");
			return restoreManager.getFactoryBuilder ().getFactory (factoryPath);
		}

		/**
		 * from JSON Object restore element of a member
		 * @param member the name of the member to be restored
		 * @param context the JSON object containing the member
		 * @param restoreManager the manager in control of JSON restore
		 * @return an Expression restored from the named member
		 * @throws Exception for restore errors
		 */
		public Expression<T> fromMember
		(RangeNodeMembers member, JsonSemantics.JsonObject context, JsonRestore<T> restoreManager) throws Exception
		{ return restoreManager.toSubExpression (context.getMember (member)); }

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString ()
		{
			StringBuffer buf = new StringBuffer ();
			buf.append (getConsumerName ()).append (" [ ");
			
			if (descriptionType == DescriptionType.EVALUATION_POINT)
			{
				buf.append (variableName).append (" = ").append (evalExpr);
			}
			else
			{
				buf.append (loExpr)
				.append (lbndOp.getSymbolProperties ().getName ());
				buf.append (" ").append (variableName).append (" ");
				buf.append (hbndOp.getSymbolProperties ().getName ())
				.append (" ").append (hiExpr).append (" <> ")
				.append (delta);
			}

			buf.append (" ] ").append ("( ").append (target).append (" )");
			return buf.toString ();
		}

		/**
		 * identification of this class
		 */
		public static final Types ELEMENT_TYPE = Types.RANGE_DESCRIPTOR;

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.Element#isOfType(net.myorb.math.expressions.tree.Element.Types)
		 */
		public boolean isOfType (Types type) { return type == ELEMENT_TYPE; }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.Element#getElementType()
		 */
		public Types getElementType () { return ELEMENT_TYPE; }

	}


	/*
	 * general symbol processing
	 */


	/**
	 * verify element is named symbol
	 * @param element an element to be verified as a named symbol
	 * @return the symbol table reference found for the symbol
	 */
	public static SymbolMap.Named getSymbolFor (Element element)
	{
		NamedSymbol s = SimpleUtilities.verifyClass (element, NamedSymbol.class);
		if (s == null) throw new RuntimeException ("Element is not a Named Symbol");
		return getSymbol (s);
	}


	/**
	 * check for symbol table link
	 * @param from the named symbol identifying an operator
	 * @return the symbol table reference for the operator
	 */
	public static SymbolMap.Named getSymbol (NamedSymbol from)
	{
		SymbolMap.Named symbol = from.getSymbolProperties ().getSymbolReference ();
		if (symbol == null) throw new RuntimeException ("Symbol has no link established: " + from);
		return symbol;
	}


	/**
	 * lookup name for symbol mapped to element
	 * @param element for this element
	 * @return the name
	 */
	public static String getNameFor (Element element)
	{
		return getSymbolFor (element).getName ();
	}


	/**
	 * construct element describing symbol
	 * @param name the text name of the symbol
	 * @param symMap a hash of symbols of this type
	 * @param subEx a sub-expression that will refer to symbol
	 * @param factory an element factory for new elements
	 * @param <E> element map type
	 * @param <T> data type
	 */
	public static <T, E extends Element> void describe
		(
			String name, Map<String,E> symMap,
			SubExpression<T> subEx, ElementFactory<E> factory
		)
	{
		E element;
		if (symMap.containsKey (name)) element = symMap.get (name);
		else symMap.put (name, element = factory.newElementFor (name));
		subEx.add (element);
	}


	/*
	 * expression sub-types
	 */


	/**
	 * expressions used in descriptors
	 * @param <T> data type
	 */
	public static class DescriptorExpression<T> extends Expression<T>
	{
		private static final long serialVersionUID = -2069867795350521211L;
		public DescriptorExpression (RangeDescriptor<T> parent) { this.parentRange = parent; }
		protected RangeDescriptor<T> parentRange;
	}

	/**
	 * expression for range target
	 * @param <T> data type
	 */
	public static class TargetExpression<T> extends DescriptorExpression<T>
	{

		public TargetExpression (RangeDescriptor<T> parent) { super (parent); }
		private static final long serialVersionUID = -3078891401026990082L;

		/**
		 * cast Object to TargetExpression when appropriate
		 * @param from source Object for the attempted cast
		 * @return Object cast to TargetExpression
		 * @param <T> data type
		 */
		public static <T> TargetExpression<T> cast (Object from)
		{
			@SuppressWarnings ("unchecked") TargetExpression<T>
				targetExpression = SimpleUtilities.verifyClass (from, TargetExpression.class);
			return targetExpression;
		}

		/**
		 * target has artificial indent and a real indent layer.
		 *  must discard artificial indent layer to avoid confusion of parent.
		 * @return expression contained in target parenthesis
		 */
		public Expression<T> getChildExpression ()
		{
			SubExpression<T> subEx = getSubExpression ();
			if (size () != 1 || subEx == null) throw new RuntimeException ("Internal Error: Target artificial layer");
			Expression<T> child = parentRange.newSub (subEx, this);
			child.addThisToComponentsList ();
			return child;
		}

		/**
		 * check for empty expression and expect sub-expression
		 * @return first element sub-expression from target
		 */
		public SubExpression<T> getSubExpression ()
		{
			if (size () == 0)
				throw new RuntimeException ("Internal Error: Empty Target layer");
			return SubExpression.cast (get (0));
		}

	}

	/**
	 * expression for range endpoint (lo, hi)
	 * @param <T> data type
	 */
	public static class EndpointExpression<T> extends DescriptorExpression<T>
	{
		private static final long serialVersionUID = 7294477574198302116L;
		public EndpointExpression (RangeDescriptor<T> parent) { super (parent); }
	}


	/*
	 * lexical element processing
	 */


	/**
	 * mark child complete (matured)
	 * @param child the child node being matured
	 * @return the parent node of the child for next spawn
	 * @throws ParenthesisNestingError extra parenthesis found
	 * @param <T> data type
	 */
	public static <T>
		SubExpression<T> mature (SubExpression<T> child)
	throws ParenthesisNestingError
	{
		SubExpression<T> descriptorParent;
		if (child.isInAggregateNode ()) { child = child.parent; }
		if (child.parent == null) { throw new ParenthesisNestingError ("Excess closing parenthesis found"); }
		else if (child instanceof EndpointExpression) { throw new ParenthesisNestingError ("Range descriptor closing bracket missing"); }
		else if ((descriptorParent = checkForDescriptorParent (child.parent)) != null) return descriptorParent;
		else child.addThisToComponentsList ();
		return child.parent;
	}

	/**
	 * determine if end of sub-expression is end of range
	 * @param parentOfParenthesis parent of current sub-expression
	 * @return parent of the descriptor or NULL if not a descriptor
	 * @param <T> data type
	 */
	public static <T> SubExpression<T>
		checkForDescriptorParent (SubExpression<T> parentOfParenthesis)
	{ return processDescriptorParent (TargetExpression.cast (parentOfParenthesis)); }

	/**
	 * process parent of Target expression
	 * @param targetExpression the expression undergoing maturity processing
	 * @return the parent of the range if descriptor found, otherwise NULL
	 * @param <T> data type
	 */
	public static <T> SubExpression<T> processDescriptorParent
			(TargetExpression<T> targetExpression)
	{
		if (targetExpression == null) return null;					// NOT a TargetExpression, so flag caller
		RangeDescriptor<T> range = targetExpression.parentRange;	// TargetExpression links to a Range
		range.target = targetExpression.getChildExpression ();		// discard artificial indent layer
		return range.parent;										// exit range processing
	}


	/**
	 * treat expression as aggregated value
	 * @param expression the expression recognized as aggregated value
	 * @return the next aggregated sub-expression parent
	 * @param <T> data type
	 */
	public static <T> SubExpression<T> aggregate (SubExpression<T> expression)
	{
		if ( ! expression.isInAggregateNode () ) expression.changeToAggregate (); 
		else expression = expression.parent;
		return nextChild (expression);
	}

	/**
	 * spawn a child node
	 * @param expression the parent expression spawning the child
	 * @return the new child
	 * @param <T> data type
	 */
	public static <T> SubExpression<T> nextChild (SubExpression<T> expression)
	{
		SubExpression<T> child = expression.spawn ();
		child.addThisToComponentsList ();
		return child;
	}


	/**
	 * build range descriptor object
	 * @param subExpression parent expression to this descriptor
	 * @return the new end-points object for the descriptor
	 * @param <T> data type
	 */
	public static <T> SubExpression<T> startRangeDescriptor (SubExpression<T> subExpression)
	{
		return new RangeDescriptor<T>(subExpression).connectRange ();
	}

	/**
	 * verify descriptor closes properly
	 * @param subExpression the current expression section
	 * @return the parent of the current sub-expression collection object
	 * @throws ParenthesisNestingError for bracket mis-match
	 * @param <T> data type
	 */
	public static <T> SubExpression<T> endOfRangeDescriptor
		(SubExpression<T> subExpression)
	throws ParenthesisNestingError
	{
		if (subExpression instanceof DescriptorExpression)
		{ return ((DescriptorExpression<T>) subExpression).parentRange.connectTarget (); }
		throw new ParenthesisNestingError ("Mis-match of range descriptor brackets");
	}


	/**
	 * verify semantic completion of expression
	 * @param subExpression the last node in the sub-expression stack
	 * @return the last node as an expression object indicating semantic consistency
	 * @throws ParenthesisNestingError for too few closing parenthesis
	 * @param <T> data type
	 */
	public static <T> Expression<T>
		endOfExpression (SubExpression<T> subExpression)
	throws ParenthesisNestingError
	{
		if (subExpression.parent != null)
		{ throw new ParenthesisNestingError ("Too few closing parenthesis found"); }
		else subExpression.addThisToComponentsList ();
		return (Expression<T>) subExpression;
	}


	/*
	 * core token processing expansion into Expression form
	 */


	/**
	 * token-ized text is parsed to lexical tree format
	 * @param tokens the tokens to be converted to expression
	 * @return a lexical analysis tree expression (forked sequences of tokens)
	 * @throws ParenthesisNestingError nesting errors
	 * @param <T> data type
	 */
	@SuppressWarnings ({ "unchecked", "rawtypes" })
	public static <T> Expression<T> expand (List<TokenDescriptor> tokens) throws ParenthesisNestingError
	{
		ElementFactory
			idnFactory = new ElementFactory () { public Element newElementFor (String name) { return new Identifier (name); } },
			opFactory = new ElementFactory () { public Element newElementFor (String name) { return new Operator (name); } };
		Expression<T> expression = new Expression<T> (); SubExpression<T> subExpression = expression; String image;
		Expression<T> root = expression.root;

		for (TokenDescriptor token : tokens)
		{
			image = token.getTokenImage ();

			switch (token.getTokenType ())
			{
				case QOT:
					subExpression.add (new TextLiteral (image));
				break;

				case IDN:
					describe (image, root.identifiers, subExpression, idnFactory);
				break;

				case OPR:
					switch (image.charAt (0))  /* special case for parenthesis, brackets, and comma */
					{
						case OPEN:  subExpression = subExpression.spawn (); break;					// open new child sub-expression
						case CLOSE: subExpression = mature (subExpression); break;					// closing parenthesis ends sub-expression
						case COMMA: subExpression = aggregate (subExpression); break;				// mark value as aggregate child
						case OBRK:  subExpression = startRangeDescriptor (subExpression); break;	// start of range description
						case CBRK:  subExpression = endOfRangeDescriptor (subExpression); break;	// end of range description
						default:    describe (image, root.operators, subExpression, opFactory);		// describe operator
					}
					root = subExpression.root;
				break;

				default:
					subExpression.add (new NumericLiteral<Double> (token.getTokenValue ().doubleValue ()));
				break;
			}
		}

		return endOfExpression (subExpression);
	}
	public static final char OPEN = '(', CLOSE = ')', OBRK = '[', CBRK = ']', COMMA = ',';


}

