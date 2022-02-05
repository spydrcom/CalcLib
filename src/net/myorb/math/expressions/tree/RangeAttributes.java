
package net.myorb.math.expressions.tree;

import net.myorb.math.expressions.tree.LexicalAnalysis.Identifier;
import net.myorb.math.expressions.tree.SemanticAnalysis.SemanticError;
import net.myorb.math.expressions.tree.LexicalAnalysis.Operator;

import net.myorb.math.expressions.OperatorNomenclature;

import java.util.List;

/**
 * pre-execution semantic analysis of range descriptors
 * @author Michael Druckman
 */
public class RangeAttributes
{


	/**
	 * @param dsc descriptor to dump
	 * @param <T> data type
	 */
	public static <T> void dumpRangeDescriptor
		(
			LexicalAnalysis.RangeDescriptor<T> dsc
		)
	{
		System.out.println
			("found dsc: " + dsc.endpoints);
		dumpComponents (dsc.endpoints.root.components);
		System.out.println ("found target: " + dsc.target);
		dumpComponents (dsc.target.root.components);

		System.out.print   ("Lbound: " + dsc.loExpr);
		System.out.println ("    op: " + dsc.lbndOp);
		System.out.println ("   var: " + dsc.variableName);
		System.out.print   ("Ubound: " + dsc.hiExpr);
		System.out.println ("    op: " + dsc.hbndOp);
		System.out.println (" delta: " + dsc.delta);
	}
	public static <T> void dumpComponents (List<SubExpression<T>> subs)
	{
		for (SubExpression<T> se : subs)
		{ System.out.println ("\t" + se); }
	}


	/**
	 * completion of semantic analysis before evaluation
	 * @param rngDesc range descriptor to be evaluated
	 * @throws SemanticError for semantic issues
	 * @param <T> data type
	 */
	public static <T> void attributeRangeDescriptor
		(
			LexicalAnalysis.RangeDescriptor<T> rngDesc
		)
		throws SemanticError
	{
		performEvaluationAssignmentCheck (rngDesc);

		if (rngDesc.describesRange ())
		{

			attributeRangeDescriptorDelta (rngDesc);
			attributeRangeDescriptorLbound (rngDesc);
			attributeRangeDescriptorHbound (rngDesc);

			SemanticAnalysis.reduceExpression (rngDesc.delta);
			SemanticAnalysis.reduceExpression (rngDesc.loExpr);
			SemanticAnalysis.reduceExpression (rngDesc.hiExpr);

		}

		SemanticAnalysis.reduceExpression (rngDesc.target);
		//System.out.println (rngDesc);
	}


	/**
	 * add local symbols to table
	 * @param rngDesc range descriptor to be evaluated
	 * @param <T> data type
	 */
	public static <T> void attributeLocalVariable
		(
			LexicalAnalysis.RangeDescriptor<T> rngDesc
		)
	{
		LexicalAnalysis.Identifier<T> id =
			rngDesc.endpoints.identifiers.get (rngDesc.variableName);
		id.getIdentifierProperties ().setAsLocalType ();
	}


	/**
	 * check for evaluation point description syntax
	 * @param rngDesc range descriptor to be evaluated
	 * @param <T> data type to be used processing range
	 * @throws SemanticError for incomplete descriptor
	 */
	public static <T> void performEvaluationAssignmentCheck
		(
			LexicalAnalysis.RangeDescriptor<T> rngDesc
		)
		throws SemanticError
	{
		Expression<T> endpoints = rngDesc.endpoints;
		if (endpoints.size () < 3) throw new SemanticError ("Descriptor is incomplete");

		if (ASSIGNS.equals (endpoints.get (1)))
		{
			rngDesc.variableName = attributeRangeDescriptorVar (endpoints.get (0));
			rngDesc.setDescriptionType (LexicalAnalysis.RangeDescriptor.DescriptionType.EVALUATION_POINT);
			captureEvalPoint (rngDesc); SemanticAnalysis.reduceExpression (rngDesc.evalExpr);
			rngDesc.endpoints.clear ();
			return;
		}

		rngDesc.setDescriptionType (LexicalAnalysis.RangeDescriptor.DescriptionType.RANGE_SPAN);
	}
	public static <T> void captureEvalPoint
		(
			LexicalAnalysis.RangeDescriptor<T> rngDesc
		)
		throws SemanticError
	{
		rngDesc.endpoints.remove (0); rngDesc.endpoints.remove (0);
		rngDesc.evalExpr = rngDesc.newSub (rngDesc.endpoints, rngDesc.endpoints);
		rngDesc.endpoints.clear ();
	}
	public static final String ASSIGNS = OperatorNomenclature.ASSIGNMENT_DELIMITER;


	/**
	 * process delta expression
	 * @param rngDesc range descriptor to be evaluated
	 * @param <T> data type
	 */
	public static <T> void attributeRangeDescriptorDelta
		(
			LexicalAnalysis.RangeDescriptor<T> rngDesc
		)
	{
		boolean found = false;
		Expression<T> endpoints = rngDesc.endpoints;
		LexicalAnalysis.Operator op = null;
	
		for (Element e : endpoints)
		{
			if (e instanceof LexicalAnalysis.Operator)
			{
				op =  Operator.recognizedFrom (e);
				if (DELTA.equals (op.getSymbolProperties ().getName ()))
				{ found = true; break; }
			}
		}
	
		if (found)
		{
			rngDesc.delta = rngDesc.newSub
				(endpoints.subList (endpoints.indexOf (op) + 1, endpoints.size ()), endpoints);
			rngDesc.setEndpoints (endpoints.subList (0, endpoints.indexOf (op)), endpoints);
		}
	}
	public static final String DELTA = OperatorNomenclature.DELTA_INCREMENT_OPERATOR;


	/**
	 * process LO bound expression
	 * @param rngDesc range descriptor to be evaluated
	 * @throws SemanticError LO bound of range not identifiable
	 * @param <T> data type
	 */
	public static <T> void attributeRangeDescriptorLbound
		(
			LexicalAnalysis.RangeDescriptor<T> rngDesc
		)
		throws SemanticError
	{
		boolean found = false;
		Expression<T> endpoints = rngDesc.endpoints;
		LexicalAnalysis.Operator op = null;
	
		for (Element e : endpoints)
		{
			if (isBoundOp (e))
			{
				op =  (LexicalAnalysis.Operator) e;
				found = true;
				break;
			}
		}
	
		if (! found) throw new SemanticError ("LO bound of range not identifiable");

		rngDesc.loExpr = rngDesc.newSub (endpoints.subList (0, endpoints.indexOf (op)), endpoints);
		rngDesc.setEndpoints (endpoints.subList (endpoints.indexOf (op) + 1, endpoints.size ()), endpoints);
		rngDesc.lbndOp = op;
	}


	/**
	 * process HI bound expression
	 * @param rngDesc range descriptor to be evaluated
	 * @throws SemanticError Descriptor is incomplete
	 * @param <T> data type
	 */
	public static <T> void attributeRangeDescriptorHbound
		(
			LexicalAnalysis.RangeDescriptor<T> rngDesc
		)
		throws SemanticError
	{
		SubExpression<T> endpoints = rngDesc.endpoints;
		if (endpoints.size () < 3) throw new SemanticError ("Descriptor is incomplete");
		rngDesc.variableName = attributeRangeDescriptorVar (endpoints.get (0));
		rngDesc.hbndOp = attributeRangeDescriptorHboundOp (endpoints.get (1));
		rngDesc.endpoints.remove (0); rngDesc.endpoints.remove (0);
		captureHbound (rngDesc);
	}
	public static <T> void captureHbound
		(
			LexicalAnalysis.RangeDescriptor<T> rngDesc
		)
		throws SemanticError
	{
		rngDesc.hiExpr = rngDesc.newSub (rngDesc.endpoints, rngDesc.endpoints);
		rngDesc.endpoints.clear ();
	}


	/**
	 * @param element process local identifier
	 * @return the name of the local identifier
	 * @throws SemanticError Range variable not identifiable
	 * @param <T> data type
	 */
	public static <T> String attributeRangeDescriptorVar
		(
			Element element
		)
		throws SemanticError
	{
		if (element.isOfType (Identifier.ELEMENT_TYPE))
		{
			Identifier<T> id = Identifier.cast (element);
			id.getIdentifierProperties ().setAsLocalType ();
			return id.getSymbolProperties ().getName (); 
		}
		throw new SemanticError ("Range variable not identifiable");
	}


	/**
	 * @param element checking for op
	 * @return description of operator found
	 * @throws SemanticError HI bound of range not identifiable
	 * @param <T> data type
	 */
	public static <T> Operator attributeRangeDescriptorHboundOp
		(
			Element element
		)
		throws SemanticError
	{
		if (isBoundOp (element)) { return Operator.recognizedFrom (element); }
		throw new SemanticError ("HI bound of range not identifiable");
	}


	/**
	 * @param elementToCheck element that may be operator
	 * @return TRUE = element is bound op
	 */
	public static boolean isBoundOp (Element elementToCheck)
	{
		if (elementToCheck.isOfType (Operator.ELEMENT_TYPE))
		{
			String name = Operator.recognizedFrom (elementToCheck)
					.getSymbolProperties ().getName ();
			if (LT.equals (name) || LE.equals (name)) return true;
		}
		return false;
	}
	public static final String
	LT = OperatorNomenclature.LT_OPERATOR,
	LE = OperatorNomenclature.LE_OPERATOR;


}

