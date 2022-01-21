
package net.myorb.math.expressions.tree;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.OperatorNomenclature;
import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.expressions.symbols.GenericWrapper;
import net.myorb.math.expressions.symbols.IterationConsumer;
import net.myorb.math.expressions.symbols.AbstractVectorConsumer;

import net.myorb.math.expressions.tree.JsonBinding.JsonRepresentation;
import net.myorb.math.expressions.tree.LexicalAnalysis.RangeDescriptor;
import net.myorb.math.expressions.tree.LexicalAnalysis.Identifier;
import net.myorb.math.expressions.tree.LexicalAnalysis.Operator;

import net.myorb.data.notations.json.JsonLowLevel.JsonValue;
import net.myorb.data.notations.json.JsonStringRules;
import net.myorb.data.notations.json.JsonSemantics;

import net.myorb.data.abstractions.Parameters;

import java.util.HashMap;
import java.util.ArrayList;

import java.util.Comparator;

import java.util.List;
import java.util.Map;

/**
 * apply type information to expression tree.
 *  adjust tree node for sub-expressions identified by semantic symbol data.
 * @param <T> type of expression values
 * @author Michael Druckman
 */
public class SemanticAnalysis<T>
{


	/**
	 * SysOut trace of tree node generation
	 */
	public static final boolean DUMPING = false;


	/**
	 * general case of Semantic Error
	 */
	public static class SemanticError extends Exception
	{
		public SemanticError (String notation) { super (notation); }
		private static final long serialVersionUID = -431023142665926714L;
	}


	/**
	 * use generic value wrapper for literals
	 * @param <T> data type
	 */
	public static class UpdatedLiteral<T>
		extends LexicalAnalysis.NumericLiteral<T> implements JsonRepresentation<T>
	{

		public UpdatedLiteral () { super (0.0); }

		public UpdatedLiteral
		(ExpressionSpaceManager<T> spaceManager)
		{ this (); this.spaceManager = spaceManager; }

		public UpdatedLiteral (LexicalAnalysis.NumericLiteral<T> literal, ExpressionSpaceManager<T> spaceManager)
		{ this (spaceManager.convertFromDouble (literal.getLiteralValue ()), spaceManager); }

		public UpdatedLiteral (T literal, ExpressionSpaceManager<T> spaceManager)
		{
			super (literal, spaceManager.toNumber (literal));
			this.spaceManager = spaceManager;
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.JsonBinding.JsonRepresentation#getJson()
		 */
		public JsonSemantics.JsonValue toJson ()
		{
			return new JsonSemantics.JsonNumber (spaceManager.toNumber (value));
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.JsonBinding.JsonRepresentation#fromJson(net.myorb.data.notations.json.JsonSemantics.JsonValue, net.myorb.math.expressions.tree.JsonRestore)
		 */
		public Element fromJson (JsonSemantics.JsonValue context, JsonRestore<T> restoreManager) throws Exception
		{
			JsonSemantics.JsonNumber jsonRep = (JsonSemantics.JsonNumber) context;
			setValue (spaceManager.convertFromDouble (jsonRep.getNumber ().doubleValue ()));
			return this;
		}

		public void setSpaceManager (ExpressionSpaceManager<T> spaceManager) { this.spaceManager = spaceManager; }
		public String toString () { return spaceManager.format (value); }
		protected ExpressionSpaceManager<T> spaceManager;

	}


	/**
	 * descriptor for calculus reference
	 * @param <T> data type
	 */
	public static class CalculusDescriptor<T> implements Element, JsonRepresentation<T>
	{

		/**
		 * calculus operations found in calculus descriptors
		 */
		public enum CalculusOperations
		{
			Derivative, Interval, TSQuad, DCTQuad, TrapQuad, TrapAdjust
		}
		public static final Map<String,CalculusOperations> CalculusOperationsMap =
				new HashMap<String,CalculusOperations>();
		static
		{
			CalculusOperationsMap.put (OperatorNomenclature.TSQUAD_OPERATOR, CalculusOperations.TSQuad);
			CalculusOperationsMap.put (OperatorNomenclature.DCTQUAD_OPERATOR, CalculusOperations.DCTQuad);
			CalculusOperationsMap.put (OperatorNomenclature.TRAPQUAD_OPERATOR, CalculusOperations.TrapQuad);
			CalculusOperationsMap.put (OperatorNomenclature.TRAPADJUST_OPERATOR, CalculusOperations.TrapAdjust);
			CalculusOperationsMap.put (OperatorNomenclature.INTERVAL_EVAL_OPERATOR, CalculusOperations.Interval);
			CalculusOperationsMap.put (OperatorNomenclature.DPRIME_OPERATOR, CalculusOperations.Derivative);
			CalculusOperationsMap.put (OperatorNomenclature.PRIME_OPERATOR, CalculusOperations.Derivative);
		}

		CalculusDescriptor () {}

		CalculusDescriptor
			(
				LexicalAnalysis.Identifier<T> identifier,
				LexicalAnalysis.Operator operator,
				Element parameters
			)
		{
			this.underlyingFunction = null;
			this.identifier = identifier;
			this.parameters = parameters;
			this.operator = operator;
			identifyOperation ();
		}
		protected CalculusOperations calculusOperation;
		protected GenericWrapper.GenericFunction<T> underlyingFunction;
		protected LexicalAnalysis.Identifier<T> identifier;
		protected LexicalAnalysis.Operator operator;
		protected Element parameters;

		/**
		 * use operator name to identify calculus operation
		 */
		public void identifyOperation ()
		{
			calculusOperation = CalculusOperationsMap.get (operator.getSymbolProperties ().getName ());
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.JsonBinding.JsonRepresentation#fromJson(net.myorb.data.notations.json.JsonSemantics.JsonValue, net.myorb.math.expressions.tree.JsonRestore)
		 */
		public Element fromJson (JsonValue context, JsonRestore<T> restoreManager) throws Exception
		{
			JsonBinding.Node node = new JsonBinding.Node (context);
			operator = LexicalAnalysis.Operator.restore (node.getMemberString (CalculusNodeMembers.Operator), restoreManager);
			identifier = LexicalAnalysis.Identifier.restore (node.getMemberString (CalculusNodeMembers.Function), restoreManager);
			parameters = restoreManager.toElement (node.getMember (CalculusNodeMembers.Parameters));
			identifyOperation ();
			return this;
		}

		/**
		 * name the members of this node type
		 */
		public enum CalculusNodeMembers {Operator, Function, Parameters}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.JsonBinding.JsonRepresentation#getJson()
		 */
		public JsonValue toJson ()
		{
			JsonBinding.Node node = new JsonBinding.Node (JsonBinding.NodeTypes.Calculus);
			node.addMember (CalculusNodeMembers.Function, identifier.getSymbolProperties ().getJsonName ());
			node.addMember (CalculusNodeMembers.Operator, operator.getSymbolProperties ().getJsonName ());
			node.addMember (CalculusNodeMembers.Parameters, JsonBinding.toJson (parameters));
			return node;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString () { return identifier.getSymbolProperties ().getName () + " " + operator.getSymbolProperties ().getName () + " (" + parameters + ")"; }

		/**
		 * identification of this class
		 */
		public static final Types ELEMENT_TYPE = Types.CALCULUS_DESCRIPTOR;

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
	 * literal processing
	 */


	/**
	 * describe an aggregate literal
	 * @param <T> atomic data type
	 */
	public static class AggregateLiteral<T>
		implements Element, JsonRepresentation<T>
	{

		public AggregateLiteral () 
		{
			this.expression = new Expression<>();
		}

		public AggregateLiteral (SubExpression<T> expression)
		{
			this.expression = new SubExpression<T>(expression);
			this.expression.addAll (expression);
		}
		protected SubExpression<T> expression;

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString ()
		{
			int last = expression.size () - 1;
			StringBuffer buf = new StringBuffer ();
			for (int i = 0; i < last; i++) { buf.append (toString (i)).append (", "); }
			buf.append (toString (last));
			return buf.toString ();
		}
		public String toString (int item)
		{
			return expression.get (item).toString ().trim ();
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.JsonBinding.JsonRepresentation#getJson()
		 */
		public JsonSemantics.JsonValue toJson ()
		{
			JsonSemantics.JsonArray
				array = new JsonSemantics.JsonArray ();
			for (int i = 0; i < expression.size (); i++)
			{
				array.addElement (JsonBinding.toJson (expression.get (i)));
			}
			return array;
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.JsonBinding.JsonRepresentation#fromJson(net.myorb.data.notations.json.JsonSemantics.JsonValue, net.myorb.math.expressions.tree.JsonRestore)
		 */
		public Element fromJson (JsonSemantics.JsonValue context, JsonRestore<T> restoreManager) throws Exception
		{
			JsonSemantics.JsonArray
				array = (JsonSemantics.JsonArray) context;
			for (JsonSemantics.JsonValue value : array)
			{
				expression.add (restoreManager.toElement (value));
			}
			return this;
		}

		/**
		 * identification of this class
		 */
		public static final Types ELEMENT_TYPE = Types.AGGREGATE;

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.Element#isOfType(net.myorb.math.expressions.tree.Element.Types)
		 */
		public boolean isOfType (Types type) { return type == ELEMENT_TYPE; }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.Element#getElementType()
		 */
		public Types getElementType () { return ELEMENT_TYPE; }

	}

	/**
	 * update literals to use generic wrapper
	 * @param expression the expression tree to be updated
	 * @param spaceManager a value manager for the type
	 * @param <T> data type
	 */
	public static <T> void attributeLiteralNodes
		(
			Expression<T> expression,
			ExpressionSpaceManager<T> spaceManager
		)
	{
		for (SubExpression<T> subEx : expression.components)
		{
			if (subEx.isAggregateValueRoot ())
				subEx.replaceContents (new AggregateLiteral<T> (subEx));
			else updateLiteralNodes (subEx, spaceManager);
		}
	}


	/**
	 * change numeric literals to UpdatedLiteral
	 * @param subEx the sub-expression being updated
	 * @param spaceManager a value manager for the type
	 * @param <T> data type
	 */
	public static <T> void updateLiteralNodes
		(
			SubExpression<T> subEx,
			ExpressionSpaceManager<T> spaceManager
		)
	{
		for (int i = 0; i < subEx.size (); i++)
		{
			LexicalAnalysis.NumericLiteral<T>
				literal = LexicalAnalysis.NumericLiteral.cast (subEx.get (i));
			if (literal != null) subEx.set (i, new UpdatedLiteral<T> (literal, spaceManager));
		}
	}


	/*
	 * unary operator processing
	 */


	/**
	 * a tree node identifying symbol invocation
	 * @param <T> data type used in expression
	 */
	public static class Invocation<T> extends ValuedElement<T>
		implements JsonRepresentation<T>
	{
		public Invocation () {}

		public Invocation (Element identifier, Element parameter)
		{ this.identifier = identifier; this.parameter = parameter; setName (); }

		/**
		 * capture name from symbol
		 */
		public void setName ()
		{ simpleName = ((LexicalAnalysis.NamedSymbol) identifier).getSymbolProperties ().getName (); }
		public String getName () { return simpleName; }

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString ()
		{
			if (isPostFix)
				return parameter.toString () + " " + simpleName;
			else return simpleName + "(" + parameter.toString () + ")";
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.JsonBinding.JsonRepresentation#getJson()
		 */
		public JsonSemantics.JsonValue toJson ()
		{
			JsonBinding.Node node = new JsonBinding.Node (JsonBinding.NodeTypes.UnaryOP);
			node.addMember (InvocationNodeMembers.OpName, new JsonSemantics.JsonString (getName ()));
			node.addMember (InvocationNodeMembers.PostFix, JsonSemantics.getBoolean (isPostFix));
			node.addMember (InvocationNodeMembers.Parameter, JsonBinding.toJson (parameter));
			return node;
		}

		/**
		 * name the members of this node type
		 */
		public enum InvocationNodeMembers {OpName, PostFix, Parameter}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.JsonBinding.JsonRepresentation#fromJson(net.myorb.data.notations.json.JsonSemantics.JsonValue, net.myorb.math.expressions.tree.JsonRestore)
		 */
		public Element fromJson (JsonSemantics.JsonValue context, JsonRestore<T> restoreManager) throws Exception
		{
			JsonBinding.Node node = new JsonBinding.Node (context);
			simpleName = node.getMemberString (InvocationNodeMembers.OpName);
			identifier = LexicalAnalysis.Identifier.restore (simpleName, restoreManager);
			parameter = restoreManager.toElement (node.getMember (InvocationNodeMembers.Parameter));
			isPostFix = JsonSemantics.isTrue (node.getMember (InvocationNodeMembers.PostFix));
			return this;
		}

		/**
		 * identification of this class
		 */
		public static final Types ELEMENT_TYPE = Types.INVOCATION;

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.Element#isOfType(net.myorb.math.expressions.tree.Element.Types)
		 */
		public boolean isOfType (Types type) { return type == ELEMENT_TYPE; }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.Element#getElementType()
		 */
		public Types getElementType () { return ELEMENT_TYPE; }

		protected Element identifier, parameter;
		protected boolean isPostFix = false;
		protected String simpleName;
	}


	/**
	 * a tree node identifying a function call
	 * @param <T> data type used in expression
	 */
	public static class FunctionCall <T> extends Invocation <T>
	{

		/**
		 * definition of formal parameter lists.
		 * positional parameters line up with actual lists (ValueList in ValueManager).
		 * lists resolve in parallel to bind name to value in symbol table passed to function.
		 */
		public static class FormalParameterList extends Parameters
		{
			public FormalParameterList () {}
			public FormalParameterList (String name) { super (name); }
			public FormalParameterList (List <String> names) { super (names); }
			private static final long serialVersionUID = 96113412782772632L;
		}

		/**
		 * @param function the element that names the function symbol
		 * @param parameters the ValueList holding the positional actual parameter values
		 */
		public FunctionCall (Element function, Element parameters)
		{ super (function, parameters); }

	}
	public static class UnaryPrefixCall<T> extends Invocation<T>
	{
		public UnaryPrefixCall (Element function, Element parameters)
		{ super (function, parameters); }
	}
	public static class UnaryPostfixCall<T> extends Invocation<T>
	{
		public UnaryPostfixCall (Element function, Element parameters)
		{ super (function, parameters); this.isPostFix = true; }
	}


	/**
	 *processing interface for Function and Unary Pre/Post Fix operators
	 * @param <T> data type
	 */
	public interface InvocationProcessor<T>
	{
		Element process (SubExpression<T> expression, int node) throws SemanticError;
	}


	/**
	 * tree traversal for invocation processors
	 * @param expression the expression to be traversed
	 * @param processor the processor to be applied to tree nodes
	 * @throws SemanticError for semantic errors found
	 * @param <T> data type
	 */
	public static <T> void attributeInvocationNodes
		(
			Expression<T> expression, InvocationProcessor<T> processor
		)
	throws SemanticError
	{
		Element element;
		for (SubExpression<T> expr : expression.components)
		{
			for (int i = expr.size () - 1; i >= 0; i--)
			{
				if ((element = processor.process (expr, i)) != null)
				{
					expression.invocations.add (new SubExpression<T> (element));
				}
			}
		}
	}


	/**
	 * locate parameter node from expression.
	 *  verify parameter index is within expression range
	 * @param parameterNodeIndex the index in the element sequence
	 * @param fromExpression the expression containing the invocation
	 * @return the element representing the function parameter(s)
	 * @throws SemanticError for index outside expression range
	 * @param <T> data type
	 */
	public static <T> Element getParametersAt
		(int parameterNodeIndex, SubExpression<T> fromExpression)
	throws SemanticError
	{
		if (parameterNodeIndex < 0 || parameterNodeIndex >= fromExpression.size ())
		{ throw new SemanticError ("Parameter to operator is not present"); }
		return fromExpression.get (parameterNodeIndex);
	}


	/**
	 * node processor for Unary Operators
	 * @param <T> data type
	 */
	public static class UnaryOperatorProcessor<T> implements InvocationProcessor<T>
	{

		/**
		 * update unary operator nodes
		 * @param type the lexical type of the operator
		 * @param parameterIndexOffset -1 = postfix, +1 = prefix
		 */
		public UnaryOperatorProcessor
		(LexicalAnalysis.OperatorType type, int parameterIndexOffset)
		{ this.parameterIndexOffset = parameterIndexOffset; this.type = type; }

		protected LexicalAnalysis.OperatorType type; // post / pre Unary
		protected int parameterIndexOffset; // -1 = postfix, +1 = prefix
		
		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.SemanticAnalysis.InvocationProcessor#process(net.myorb.math.expressions.tree.SubExpression, int)
		 */
		public Element process (SubExpression<T> expression, int node) throws SemanticError
		{
			Element e; int parameterNodeIndex;
			if ( ! isCorrectType (e = expression.get (node)) ) return null;
			Element p = getParametersAt (parameterNodeIndex = node + parameterIndexOffset, expression);
			Element updated = parameterIndexOffset < 0 ? new UnaryPostfixCall<T> (e, p) : new UnaryPrefixCall<T> (e, p);
			expression.substituteInvocationNodes (node, parameterNodeIndex, updated);
			return updated;
		}

		/**
		 * check node type
		 * @param element node being examined
		 * @return TRUE = correct type
		 */
		public boolean isCorrectType (Element element)
		{
			if ( ! element.isOfType (Operator.ELEMENT_TYPE) ) return false;
			else return Operator.recognizedFrom (element).getTypeManager ().isType (type);
		}
	}


	/**
	 * node processor for named Operators
	 * @param <T> data type
	 */
	public static class NamedOperatorProcessor<T> implements InvocationProcessor<T>
	{

		/**
		 * update named operator nodes
		 */
		public NamedOperatorProcessor () {}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.SemanticAnalysis.InvocationProcessor#process(net.myorb.math.expressions.tree.SubExpression, int)
		 */
		public Element process (SubExpression<T> expression, int node) throws SemanticError
		{
			LexicalAnalysis.Operator op; int parameterNodeIndex;
			if ((op = identifyNamedOperator (expression.get (node))) == null) return null;
			Element updated = new UnaryPrefixCall<T> (op, getParametersAt (parameterNodeIndex = node + 1, expression));
			expression.substituteInvocationNodes (node, parameterNodeIndex, updated);
			return updated;
		}

		/**
		 * check node type
		 * @param element node being examined
		 * @return operator symbol if found, null if not found
		 */
		public LexicalAnalysis.Operator identifyNamedOperator (Element element)
		{
			if (element.isOfType (Identifier.ELEMENT_TYPE))
			{
				LexicalAnalysis.IdentifierProperties
					identifierProperties = Identifier.cast (element).getIdentifierProperties ();
				if (identifierProperties.getTypeManager ().isType (LexicalAnalysis.IdentifierType.NamedOperator))
				{ return identifierProperties.getOperator (); }
			}
			return null;
		}

	}


	/**
	 * processing for derivative (prime) notation
	 * @param <T> data type
	 */
	public static class CalculusOperatorProcessor<T> extends UnaryOperatorProcessor<T>
	{

		CalculusOperatorProcessor () { super (LexicalAnalysis.OperatorType.Calculus, +1); }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.SemanticAnalysis.UnaryOperatorProcessor#process(net.myorb.math.expressions.tree.SubExpression, int)
		 */
		public Element process (SubExpression<T> expression, int node) throws SemanticError
		{
			Element id, modifier;
			if ( ! isCorrectType (modifier = expression.get (node)) ) return null;
			else if ( node == 0 || ! ( id = expression.get (node - 1) ).isOfType (Identifier.ELEMENT_TYPE) )
			{ throw new SemanticError ("Calculus modifier must be post fix to a function identifier"); }

			CalculusDescriptor<T> descriptor = new CalculusDescriptor<T>
			(
				Identifier.cast (id),
				Operator.recognizedFrom (modifier),
				expression.get (node + 1)
			);

			expression.substituteInvocationNodes
			(node - 1, node + 1, descriptor);
			expression.remove (node);
			return null;
		}

	}


	/**
	 * update operator nodes that represent Unary Operators
	 * @param expression the expression tree to be updated
	 * @throws SemanticError for inconsistent semantics
	 * @param <T> data type
	 */
	public static <T> void attributeUnaryOperatorNodes
		(
			Expression<T> expression
		)
		throws SemanticError
	{
		attributeInvocationNodes
			(expression, new CalculusOperatorProcessor<T> ());
		attributeInvocationNodes (expression, new NamedOperatorProcessor<T> ());
		attributeInvocationNodes (expression, new UnaryOperatorProcessor<T> (LexicalAnalysis.OperatorType.PostFixUnary, -1));
		attributeInvocationNodes (expression, new UnaryOperatorProcessor<T> (LexicalAnalysis.OperatorType.PreFixUnary, +1));
	}


	/*
	 * binary operator processing
	 */


	/**
	 * binary operator description
	 */
	public static class BinaryOperatorNode<T>
		implements Element, JsonRepresentation<T>
	{

		public BinaryOperatorNode () {}

		public BinaryOperatorNode
			(
				Element left, Element right,
				LexicalAnalysis.Operator op
			)
		{
			this.opName = op.getSymbolProperties ().getName ();
			this.left = left; this.right = right;
			this.op = op;
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.JsonBinding.JsonRepresentation#getJson()
		 */
		public JsonSemantics.JsonValue toJson ()
		{
			String translatedName = JsonStringRules.escapeFor (opName);
			JsonBinding.Node node = new JsonBinding.Node (JsonBinding.NodeTypes.BinaryOP);
			node.addMember (BinaryOperatorNodeMembers.OpType, op.getTypeManager ().getJsonType ());
			node.addMember (BinaryOperatorNodeMembers.OpName, new JsonSemantics.JsonString (translatedName));
			node.addMember (BinaryOperatorNodeMembers.Right, JsonBinding.toJson (right));
			node.addMember (BinaryOperatorNodeMembers.Left, JsonBinding.toJson (left));
			return node;
		}

		/**
		 * name the members of this node type
		 */
		public enum BinaryOperatorNodeMembers {OpType, OpName, Left, Right}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.JsonBinding.JsonRepresentation#fromJson(net.myorb.data.notations.json.JsonSemantics.JsonValue, net.myorb.math.expressions.tree.JsonRestore)
		 */
		public Element fromJson (JsonSemantics.JsonValue context, JsonRestore<T> restoreManager) throws Exception
		{
			JsonBinding.Node node = new JsonBinding.Node (context);
			opName = node.getMemberString (BinaryOperatorNodeMembers.OpName);
			left = restoreManager.toElement (node.getMember (BinaryOperatorNodeMembers.Left));
			right = restoreManager.toElement (node.getMember (BinaryOperatorNodeMembers.Right));
			op = LexicalAnalysis.Operator.restore (opName, restoreManager);
			return this;
		}
		protected LexicalAnalysis.Operator op;

		/**
		 * identification of this class
		 */
		public static final Types ELEMENT_TYPE = Types.BINARY_OPERATION;

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.Element#getElementType()
		 */
		public Types getElementType () { return ELEMENT_TYPE; }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.Element#isOfType(net.myorb.math.expressions.tree.Element.Types)
		 */
		public boolean isOfType (Types type) { return type == ELEMENT_TYPE; }

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString ()
		{
			return left.toString () + " " + opName + " " + right.toString ();
		}

		public String getopName () { return opName; }
		protected String opName;

		public Element getLeftOperand () { return left; }
		public Element getRightOperand () { return right; }
		protected Element left, right;

	}
	public static class FencedBinaryOperatorNode<T> extends BinaryOperatorNode<T>
	{
		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.SubExpression#toString()
		 */
		public String toString ()
		{
			return "(" + super.toString () + ")";
		}
	}


	/**
	 * node processor for Binary Operators
	 */
	public static class BinaryOperatorProcessor<T> implements Comparator<LexicalAnalysis.Operator>
	{

		/**
		 * replace binary operators with binary node descriptors
		 * @param sequence the expression sequence being adjusted
		 * @throws SemanticError for identified semantic issues
		 */
		public void processSequence (SubExpression<T> sequence) throws SemanticError
		{
			if (isBinary (sequence.get (0)))
				sequence.add (0, LexicalAnalysis.getZero ());
			collectOperators (sequence); operators.sort (this);

			for (LexicalAnalysis.Operator op : operators)
			{ replaceOperatorWithNode (op, sequence); }

//			if (sequence.size () != 1)
//			{
//				throw new SemanticError ("Expression not properly reduced " + sequence);
//			}
		}

		/**
		 * verify proper operand for operation
		 * @param sequence the expression sequence being evaluated
		 * @param index the position if the operand within the sequence
		 * @return the operand element found at index position specified
		 * @throws SemanticError for identified semantic issues
		 */
		public Element getOperand (SubExpression<T> sequence, int index) throws SemanticError
		{
			Element operand;
			if ((operand = getParametersAt (index, sequence)).isOfType (Operator.ELEMENT_TYPE))
			{ throw new SemanticError ("Operand not of proper type"); }
			return operand;
		}

		/**
		 * replace operator in sequence with node describing operation
		 * @param op the operator to be replaced in the expression sequence
		 * @param sequence the expression sequence being adjusted
		 * @throws SemanticError for identified semantic issues
		 */
		public void replaceOperatorWithNode (LexicalAnalysis.Operator op, SubExpression<T> sequence) throws SemanticError
		{
			int opIdx = sequence.indexOf (op);
			Element l = getOperand (sequence, opIdx - 1), r = getOperand (sequence, opIdx + 1);
			replaceOperatorWithNode (new BinaryOperatorNode<T> (l, r, op), sequence);
			if (DUMPING) System.out.println (op + "\t" + l + " <> " + r);
		}

		/**
		 * replace operator in sequence with node describing operation
		 * @param n the binary operation node to be used in place of operator
		 * @param sequence the expression sequence being adjusted
		 */
		public void replaceOperatorWithNode (BinaryOperatorNode<T> n, SubExpression<T> sequence)
		{ sequence.set (sequence.indexOf (n.op), n); sequence.remove (n.left); sequence.remove (n.right); }

		/**
		 * build collection of operators in sequence
		 * @param sequence the expression being analyzed
		 */
		public void collectOperators (SubExpression<T> sequence)
		{ for (Element e : sequence) if (isBinary (e)) operators.add ( (Operator) e ); }
		protected List<LexicalAnalysis.Operator> operators = new ArrayList<> ();

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare (Operator left, Operator right)
		{
			return left.getOperatorProperties ().getPrecedence () > right.getOperatorProperties ().getPrecedence () ? -1 : +1;
		}

		/**
		 * construct processor for expression
		 * @param expression the expression being analyzed
		 * @throws SemanticError for identified semantic issues
		 * @param <T> data type
		 */
		public static <T> void process (SubExpression<T> expression) throws SemanticError
		{
			new BinaryOperatorProcessor<T> ().processSequence (expression);
		}

		/**
		 * check for Binary operator
		 * @param element node being examined
		 * @return TRUE = correct type
		 */
		public boolean isBinary (Element element)
		{
			if (element.isOfType (Operator.ELEMENT_TYPE))
			{
				return Operator.recognizedFrom (element).getTypeManager ()
						.isType (LexicalAnalysis.OperatorType.Binary);
			}
			return false;
		}

	}


	/**
	 * update operator nodes that represent Binary Operators
	 * @param expression the expression tree to be updated
	 * @throws SemanticError for inconsistent semantics
	 * @param <T> data type
	 */
	public static <T> void attributeBinaryOperatorNodes
		(
			Expression<T> expression
		)
	throws SemanticError
	{
		for (SubExpression<T> subEx : expression.components)
		{ if (subEx.size () > 1) BinaryOperatorProcessor.process (subEx); }
		if (expression.size () > 1) BinaryOperatorProcessor.process (expression);
	}


	/*
	 * range descriptor processing
	 */


	/**
	 * connect descriptor with a target
	 * @param expression the expression being modified
	 * @param spaceManager a space manager for the data type
	 * @throws SemanticError for inconsistent semantics
	 * @param <T> data type
	 */
	public static <T> void attributeRangeDescriptorLiterals
		(
			Expression<T> expression, ExpressionSpaceManager<T> spaceManager
		)
	throws SemanticError
	{
		for (Element e : expression.descriptors)
		{
			LexicalAnalysis.RangeDescriptor<T> dsc =
				LexicalAnalysis.RangeDescriptor.cast (e);

			if (dsc.delta == null)
			{
				dsc.delta = new Expression<T> ();
				dsc.delta.add (LexicalAnalysis.getOne ());
			}
			else attributeLiteralNodes (dsc.delta, spaceManager);
			attributeLiteralNodes (dsc.endpoints, spaceManager);
			attributeLiteralNodes (dsc.target, spaceManager);
		}
	}
	public static <T> void attributeRangeDescriptors
		(
			Expression<T> expression, ExpressionSpaceManager<T> spaceManager
		)
	throws SemanticError
	{
		for (Element e : expression.descriptors)
		{
			LexicalAnalysis.RangeDescriptor<T> dsc =
					LexicalAnalysis.RangeDescriptor.cast (e);
			RangeAttributes.attributeRangeDescriptor (dsc);
		}
	}


	/**
	 * process symbols in range descriptors
	 * @param expression the expression that contains descriptors
	 * @param symbols the symbol table that resolves the symbols
	 * @throws SemanticError for inconsistent semantics
	 * @param <T> data type
	 */
	public static <T> void attributeDescriptorSymbols
		(
			Expression<T> expression, SymbolMap symbols
		)
	throws SemanticError
	{
		for (Element e : expression.descriptors)
		{
			LexicalAnalysis.RangeDescriptor<T> dsc =
				LexicalAnalysis.RangeDescriptor.cast (e);
			if (dsc.delta != null) attributeSymbols (dsc.delta, symbols);
			attributeSymbols (dsc.endpoints, symbols);
			attributeSymbols (dsc.target, symbols);
		}
	}


	/*
	 * function symbol processing
	 */


	/**
	 * node processor for Function identifiers
	 * @param <T> data type
	 */
	public static class FunctionNodeProcessor<T> implements InvocationProcessor<T>
	{

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.tree.SemanticAnalysis.InvocationProcessor#process(net.myorb.math.expressions.tree.SubExpression, int)
		 */
		public Element process (SubExpression<T> expression, int callNodeIndex) throws SemanticError
		{
			Element functionIdentifier = expression.get (callNodeIndex), parameterNode; int parameterNodeIndex;
			if ( ! isFunctionCall (functionIdentifier) && ! isConsumerCall (functionIdentifier) ) return null;

			if ( ( parameterNodeIndex = callNodeIndex + 1 )  >=  expression.size () )
			{ throw new SemanticError ("Function call requires parameters"); }

			if ( isDescriptorModifier (parameterNode = expression.get (parameterNodeIndex), functionIdentifier) )
			{ identifyConsumer (parameterNode, functionIdentifier); expression.remove (callNodeIndex); return null; }

			FunctionCall<T> updated = new FunctionCall<T> (functionIdentifier, parameterNode);
			expression.substituteInvocationNodes (callNodeIndex, parameterNodeIndex, updated);
			return updated;
		}


		/**
		 * @param parameterNode the parameter to identified function
		 * @param identifierNode the identifier that may represent a consumer
		 * @return TRUE = pattern suggests descriptor modifier
		 */
		public static boolean isDescriptorModifier (Element parameterNode, Element identifierNode)
		{ return isConsumerCall (identifierNode) && parameterNode.isOfType (RangeDescriptor.ELEMENT_TYPE); }


		/**
		 * identify consumer specified as descriptor modifier
		 * @param parameter the node marked as parameter to consumer call
		 * @param functionIdentifier the name found to be a consumer
		 * @param <T> data type
		 */
		public static <T> void identifyConsumer (Element parameter, Element functionIdentifier)
		{
			LexicalAnalysis.RangeDescriptor<T> descriptor = LexicalAnalysis.RangeDescriptor.cast (parameter);
			SymbolMap.Named identifierReference = getIdentifierReference (LexicalAnalysis.Identifier.cast (functionIdentifier));
			if (identifierReference instanceof AbstractVectorConsumer) { descriptor.iterationConsumer = getIterationConsumerFrom (identifierReference); }
			else descriptor.iterationConsumer = (IterationConsumer) identifierReference;
		}
		static <T> SymbolMap.Named getIdentifierReference (LexicalAnalysis.Identifier<T> identifier)
		{
			LexicalAnalysis.SymbolProperties
			identifierProperties = identifier.getSymbolProperties ();
			return identifierProperties.getSymbolReference ();
		}
		static IterationConsumer getIterationConsumerFrom (SymbolMap.Named symbol)
		{
			return ( (AbstractVectorConsumer) symbol ).getIterationConsumer ();
		}

		/**
		 * @param element the element in question
		 * @return TRUE = semantic analysis identified symbol as Function
		 * @param <T> data type
		 */
		public static <T> boolean isFunctionCall (Element element)
		{ return isCall (element, LexicalAnalysis.IdentifierType.Function); }

		/**
		 * @param element the element in question
		 * @return TRUE = semantic analysis identified symbol as Consumer
		 * @param <T> data type
		 */
		public static <T> boolean isConsumerCall (Element element)
		{ return isCall (element, LexicalAnalysis.IdentifierType.Consumer); }

		/**
		 * check node type
		 * @param element node being examined
		 * @param type the type of the call
		 * @return TRUE = correct type
		 * @param <T> data type
		 */
		public static <T> boolean isCall (Element element, LexicalAnalysis.IdentifierType type)
		{
			if ( ! element.isOfType (Identifier.ELEMENT_TYPE) ) return false;
			return Identifier.cast (element).getTypeManager ().isType (type);
		}

	}


	/**
	 * update identifier nodes that represent function calls
	 * @param expression the expression tree to be updated
	 * @throws SemanticError for inconsistent semantics
	 * @param <T> data type
	 */
	public static <T> void attributeFunctionNodes
		(
			Expression<T> expression
		)
		throws SemanticError
	{
		attributeInvocationNodes (expression, new FunctionNodeProcessor<T>());
	}


	/*
	 * symbol processing
	 */


	/**
	 * set operator types
	 * @param expression the expression tree to be updated
	 * @param symbols a symbol table to use for operator identification
	 * @throws SemanticError for semantic errors found
	 * @param <T> data type
	 */
	public static <T> void attributeOperatorSymbols
		(
			Expression<T> expression,
			SymbolMap symbols
		)
	throws SemanticError
	{
		for (String name : expression.operators.keySet ())
		{
			trace ("OP: "+name); //TODO: OP
			SymbolMap.Named item = symbols.lookup (name);
			if (item == null) throw new SemanticError ("Operator not recognized: " + name);

			if (item instanceof SymbolMap.BinaryOperator)
			{
				set (expression, name, LexicalAnalysis.OperatorType.Binary, item);
			}
			else if (item instanceof SymbolMap.UnaryPostfixOperator)
			{
				set (expression, name, LexicalAnalysis.OperatorType.PostFixUnary, item);
			}
			else if (item instanceof SymbolMap.CalculusOperator)
			{
				set (expression, name, LexicalAnalysis.OperatorType.Calculus, item);
			}
			else if (item instanceof SymbolMap.UnaryOperator)
			{
				set (expression, name, LexicalAnalysis.OperatorType.PreFixUnary, item);
			}
			else throw new SemanticError ("Unrecognized operator type");
		}
	}
	public static <T> void set
		(
			Expression<T> expression, String operatorName,
			LexicalAnalysis.OperatorType type,
			SymbolMap.Named symbolRef
		)
	{
		SymbolMap.Operation op = (SymbolMap.Operation) symbolRef;
		expression.operators.get (operatorName).getOperatorProperties ()
		.setType (type).setPrecedence (op.getPrecedence ()).setSymbolReference (symbolRef);
	}


	public static void trace (String item)
	{
		// System.out.println (item);
	}


	/**
	 * set identifier types
	 * @param expression the expression tree to be updated
	 * @param symbols a symbol table to use for identifier recognition
	 * @throws SemanticError for semantic errors found
	 * @param <T> data type
	 */
	public static <T> void attributeIdentifierSymbols
		(
			Expression<T> expression,
			SymbolMap symbols
		)
	throws SemanticError
	{
		if (expression.identifiers == null) return;
		SymbolMap.Named item; LexicalAnalysis.Operator op;

		for (String name : expression.identifiers.keySet ())
		{
			trace ("ID: "+name); //TODO: ID
			if ((item = symbols.lookup (name)) == null)
			{
				trace (" - NULL");
				// mark as unknown, may be in declarative context, i.e. [0 < i < 9]
				set (expression, name, LexicalAnalysis.IdentifierType.Unknown, null, false, null);
			}
			else if (item instanceof SymbolMap.ImportedConsumer)
			{
				trace (" - IMPORTED CONSUMER"); //TODO: this is the CONSUMER hook
				set (expression, name, LexicalAnalysis.IdentifierType.Consumer, null, true, item);
			}
			else if (item instanceof IterationConsumer)
			{
				trace (" - CONSUMER");
				set (expression, name, LexicalAnalysis.IdentifierType.Consumer, null, false, item);
			}
			else if (item instanceof SymbolMap.ImportedFunction)
			{
				trace (" - IMPORTED FUNCTION"); //TODO: this is the IMPORT hook
				set (expression, name, LexicalAnalysis.IdentifierType.Function, null, true, item);
			}
			else if (item instanceof SymbolMap.ParameterizedFunction)
			{
				trace (" - FUN");
				set (expression, name, LexicalAnalysis.IdentifierType.Function, null, false, item);
			}
			else if (item instanceof SymbolMap.Operation)
			{
				trace (" - OP");
				expression.operators.put
					(name, op = new LexicalAnalysis.Operator (name));
				set (expression, name, LexicalAnalysis.OperatorType.PreFixUnary, item);
				set (expression, name, LexicalAnalysis.IdentifierType.NamedOperator, op, false, item);
			}
			else if (item instanceof SymbolMap.VariableLookup)
			{
				trace (" - VAR");
				set (expression, name, LexicalAnalysis.IdentifierType.Variable, null, false, item);
			}
			else throw new SemanticError ("Unrecognized identifier type");
		}
	}
	public static <T> void set
		(
			Expression<T> expression, String identifierName,
			LexicalAnalysis.IdentifierType type, LexicalAnalysis.Operator op,
			boolean imported, SymbolMap.Named symbolRef
		)
	{
		//TODO: imported symbols must provide JSON configuration
		if (imported) { expression.imports.put (identifierName, symbolRef); }
		expression.identifiers.get (identifierName).getIdentifierProperties ()
		.setType (type).setOperator (op).setSymbolReference (symbolRef);
	}


	/**
	 * apply semantic attributes to expression tree
	 * @param expression the expression tree to be updated
	 * @param symbols a symbol table to use for identifier recognition
	 * @throws SemanticError for semantic errors found
	 * @param <T> data type
	 */
	public static <T> void attributeSymbols
		(
			Expression<T> expression,
			SymbolMap symbols
		)
	throws SemanticError
	{
		attributeIdentifierSymbols (expression, symbols);
		attributeOperatorSymbols (expression, symbols);
		attributeDescriptorSymbols (expression, symbols);
	}


	/*
	 * expression reduction
	 */


	/**
	 * pull binary nodes out of 
	 *  expression until single node remains
	 * @param expression the expression to be reduced
	 * @throws SemanticError for semantic errors found
	 * @param <T> data type
	 */
	public static <T> void reduceExpression
		(
			Expression<T> expression
		)
	throws SemanticError
	{
		attributeFunctionNodes (expression);
		attributeUnaryOperatorNodes (expression);
		attributeBinaryOperatorNodes (expression);
		BinaryOperatorProcessor.process (expression);
	}


	/**
	 * reduce expression and child descriptors
	 * @param expression the expression to be reduced
	 * @param spaceManager a space manager for the data type
	 * @throws SemanticError for any errors
	 * @param <T> data type
	 */
	public static <T> void performSemanticReduction
	(Expression<T> expression, ExpressionSpaceManager<T> spaceManager) throws SemanticError
	{ reduceExpression (expression); attributeRangeDescriptors (expression, spaceManager); }


	/**
	 * attribute tree to prepare for reduction.
	 *  use symbol table to resolve symbols and do semantic attribution.
	 *  use node attributes to perform semantic reduction of expression tree.
	 * @param expression the tree to be modified with semantic attributes
	 * @param spaceManager the manager for the data type
	 * @param symbols a table of referenced symbols
	 * @throws SemanticError for any errors
	 * @param <T> data type
	 */
	public static <T> void attributeAndReduce
		(
			Expression<T> expression,
			ExpressionSpaceManager<T> spaceManager,
			SymbolMap symbols
		)
	throws SemanticError
	{
		attributeLiteralNodes
			(expression, spaceManager);
		attributeSymbols (expression, symbols);
		attributeRangeDescriptorLiterals (expression, spaceManager);
		performSemanticReduction (expression, spaceManager);
	}


}

