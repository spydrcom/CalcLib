
package net.myorb.math.expressions.tree;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.data.abstractions.SimpleStreamIO;
import net.myorb.data.notations.json.*;

import java.util.Map;

/**
 * restore expression from JSON saved version
 * @param <T> data type used in expressions
 * @author Michael Druckman
 */
public class JsonRestore <T>
{


	/**
	 * builder object for symbol factories
	 */
	public interface FactoryBuilder
	{
		/**
		 * @param path class-path to factory
		 * @return an import factory
		 */
		SymbolMap.FactoryForImports getFactory (String path);
	}


	public JsonRestore (ExpressionSpaceManager <T> spaceManager, SymbolMap symbols)
	{
		this.spaceManager = spaceManager; this.symbols = symbols;
	}
	protected SymbolMap symbols;


	/**
	 * @return a data type manager for the restore
	 */
	public ExpressionSpaceManager <T>
		getExpressionSpaceManager () { return spaceManager; }
	protected ExpressionSpaceManager <T> spaceManager;


	/**
	 * translate JSON value to expression element
	 * @param value the JSON value to be translated
	 * @return the translated expression element
	 * @throws Exception for errors
	 */
	public Element toElement (JsonSemantics.JsonValue value) throws Exception
	{
		return restored (value, getElementFor (value));
	}


	/**
	 * @param value the value being translated
	 * @return the Element that the value translates to
	 */
	public Element getElementFor (JsonSemantics.JsonValue value)
	{
		switch (value.getJsonValueType ())
		{
			case OBJECT:	return nodeToElement ((JsonSemantics.JsonObject) value);
			case NUMERIC:	return new SemanticAnalysis.UpdatedLiteral<> (spaceManager);
			case ARRAY:		return new SemanticAnalysis.AggregateLiteral<> ();
			default:		throw new RuntimeException ("Internal error");
		}
	}


	/**
	 * @param node JSON object with restore context data
	 * @return the Element to be translated from context
	 */
	public Element nodeToElement (JsonSemantics.JsonObject node)
	{
		switch (JsonBinding.getNodeTypeOf (node))
		{
			case UnaryOP:		return new SemanticAnalysis.Invocation<> ();
			case Identifier:	return elementForId (node.getMemberString ("Name"));
			case BinaryOP:		return new SemanticAnalysis.FencedBinaryOperatorNode<> ();
			case Calculus:		return new SemanticAnalysis.CalculusDescriptor<> ();
			case Range:			return new LexicalAnalysis.RangeDescriptor<> ();
			default:			throw new RuntimeException ("Internal error");
		}
	}


	/**
	 * translate Value to Element
	 * @param from the JSON Value representation
	 * @param to the tree Element translation of the Value
	 * @return the translated expression element
	 * @throws Exception for errors
	 */
	@SuppressWarnings ("unchecked") public Element restored
		(JsonSemantics.JsonValue from, Element to)
	throws Exception
	{
		((JsonBinding.JsonRepresentation <T>) to)
		.fromJson (from, this);
		return to;
	}


	/**
	 * maintain list of identifiers.
	 *  only one identifier per name should be allocated
	 * @param name the name of the identifier
	 * @return old version or new instance
	 */
	public Element elementForId (String name)
	{
		LexicalAnalysis.Identifier<T> id;
		if (identifiers.containsKey (name))
			id = identifiers.get (name);
		else
		{
			id = new LexicalAnalysis.Identifier<> ();
			id.getIdentifierProperties ().setAsLocalType ();
			identifiers.put (name, id);
		}
		return id;
	}


	/**
	 * treat sub expressions as denoting precedence
	 * @param value the JSON value object to treat as sub expression
	 * @return an expression object with formatter over-ridden
	 * @throws Exception for any errors
	 */
	public Expression<T> toSubExpression (JsonSemantics.JsonValue value) throws Exception
	{
		Expression<T> subEx = new FencedExpression<>();
		subEx.add (toElement (value));
		return subEx;
	}


	/**
	 * root wrapper for expression
	 * @param value the value to treat as expression
	 * @return expression wrapping value
	 * @throws Exception for any errors
	 */
	public Expression<T> toExpression
			(JsonSemantics.JsonValue value)
	throws Exception
	{
		Expression<T> expression = new Expression<>();
		this.identifiers = expression.identifiers;						// expression map used as recognition mechanism
		expression.components.add (expression);
		expression.add (toElement (value));
		return expression;
	}


	/**
	 * @param id identifier to add to local list
	 */
	public void newIdentifier (LexicalAnalysis.Identifier<T> id)
	{
		identifiers.put (id.getSymbolProperties ().getName (), id);
	}
	public LexicalAnalysis.Identifier<T> getIdentifier (String name)
	{
		return identifiers.get (name);
	}
	protected Map<String,LexicalAnalysis.Identifier<T>> identifiers;	// expression map used as recognition mechanism


	/**
	 * get symbol details
	 * @param name the name of the symbol
	 * @return the symbol found
	 */
	public SymbolMap.Named lookup (String name) { return symbols.lookup (name); }


	/**
	 * read expression tree from JSON source
	 * @param source a SimpleStreamIO text source object
	 * @return the expression read from the source
	 * @throws Exception for any errors
	 */
	public Expression<T>
		readFrom (SimpleStreamIO.TextSource source)
	throws Exception
	{
		setProfile (JsonReader.readFrom (source));
		if (profile == null) return null;
		return getExpression ();
	}


	/**
	 * @param value the JSON object holding the profile
	 */
	public void setProfile (JsonSemantics.JsonValue value)
	{
		profile = Profile.representing (value);
		restoreImportsFromProfile ();
	}
	public void setProfile (Profile profile)
	{
		this.profile = profile;
	}
	protected Profile profile = null;


	/**
	 * @return an expression tree from the Profile object
	 * @throws Exception for any errors
	 */
	public Expression<T> getExpression () throws Exception
	{
		return toExpression (profile.getExpression ());
	}


	/**
	 * construct imported symbols listed in profile
	 */
	public void restoreImportsFromProfile ()
	{
		JsonSemantics.JsonValue imports;
		if (!JsonSemantics.isNull (imports = profile.getImports ()))
		{
			JsonSemantics.JsonObject importHash =
					(JsonSemantics.JsonObject) imports;
			for (String name : importHash.getMemberNames ())
			{
				JsonSemantics.JsonObject config =
					(JsonSemantics.JsonObject) importHash.getMemberCalled (name);
				importFromProfile (name, JsonTools.toObjectMap (config));
			}
		}
	}


	/**
	 * identify factory and generate symbol
	 * @param symbol the name to be given to the symbol
	 * @param config the hash of configuration parameters for the symbol
	 */
	public void importFromProfile (String symbol, Map<String,Object> config)
	{
		String factoryPath = config.get ("FACTORY").toString ();
		SymbolMap.FactoryForImports factory = getFactoryBuilder ().getFactory (factoryPath);
		symbols.add (factory.importSymbolFrom (symbol, config));
	}


	/**
	 * @return a builder for the imported symbols factory
	 */
	public FactoryBuilder getFactoryBuilder ()
	{
		return new FactoryBuilder ()
		{
			public SymbolMap.FactoryForImports getFactory (String path)
			{
				SymbolMap.FactoryForImports factory = JsonRestore.getFactory (path);
				Environment.provideAccess (factory, spaceManager);
				return factory;
			}
		};
	}


	/**
	 * find a name in the symbol table
	 * @param name the name of the symbol
	 * @return the named object
	 */
	public SymbolMap.Named find (String name)
	{
		return (SymbolMap.Named) symbols.get (name);
	}


	/**
	 * @param from the class-path to the factory
	 * @return an imported symbols factory
	 */
	public static SymbolMap.FactoryForImports getFactory (String from)
	{
		try { return (SymbolMap.FactoryForImports) Class.forName (from).newInstance (); }
		catch (Exception e) { throw new RuntimeException ("Import factory instance error", e); }
	}


}

