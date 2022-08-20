
package net.myorb.math.expressions.gui.rendering;

import net.myorb.math.expressions.symbols.AbstractVectorReduction;

import net.myorb.math.expressions.GreekSymbols;
import net.myorb.math.expressions.SymbolMap;

import java.util.ArrayList;
import java.util.List;

/**
 * processing of nested sub-expressions using stacks
 * @author Michael Druckman
 */
public class SubExpressionProcessing
{


	/**
	 * identify node formatting object
	 * @param nodeFormater the node formatting object
	 */
	public void
	setNodeFormater (NodeFormatting nodeFormater) { this.nodeFormater = nodeFormater; }
	protected NodeFormatting nodeFormater;


	/*
	 * specialized uses of node formatter
	 */


	/**
	 * identify sub-expression
	 * @param subExpression the node of the sub-expression
	 * @return bracketed sub-expression
	 */
	public String bracket (String subExpression)
	{
		return nodeFormater.formatBracket (subExpression);
	}


	/**
	 * check for notation for identifier
	 * @param name the name of the symbol
	 * @return the translated identifier
	 */
	public String idFor (String name)
	{
		return nodeFormater.formatIdentifierReference
		(GreekSymbols.determineNotationFor (name));
	}


	/*
	 * sub-expression precedence stack
	 */

	private int lastLeafPrec = 0;
	public static final int ATOMIC_LEAF_PRECEDENCE = 100;
	private List<Integer> leafPrecStack = new ArrayList<Integer>();

	/**
	 * add current leaf precedence to top of stack
	 */
	public void pushLeafPrec () { leafPrecStack.add (lastLeafPrec); }

	/**
	 * pop leaf precedence from top of stack to current
	 * @return the value of the new current precedence
	 */
	public int popLeafPrec ()
	{
		lastLeafPrec = leafPrecStack.remove (leafPrecStack.size() - 1);
		return lastLeafPrec;
	}


	/*
	 * leaf stack
	 */

	private String lastLeaf = null;
	private List<String> leafStack = new ArrayList<String>();

	public void setLastLeaf(String lastLeaf) { this.lastLeaf = lastLeaf; }
	public String getLastLeaf () { return lastLeaf; }


	/**
	 * add current leaf mark-up to top of stack
	 */
	public void pushLeaf () { if (lastLeaf.length() != 0) leafStack.add (lastLeaf); pushLeafPrec (); }
	public void pushLeaf (String newLeaf) { pushLeaf (); lastLeaf = newLeaf; }
	public void pushLeaf (String newLeaf, int subExpressionPrecedence)
	{ pushLeaf (newLeaf); lastLeafPrec = subExpressionPrecedence; }

	/**
	 * pop leaf mark-up from top of stack to current
	 * @return the value of the new current leaf mark-up
	 */
	public String popLeaf ()
	{
		lastLeafPrec =
			ATOMIC_LEAF_PRECEDENCE;
		if (leafStack.size() == 0) return "";
		String leaf = leafStack.remove (leafStack.size() - 1);
		popLeafPrec ();
		return leaf;
	}


	/*
	 * parameter stack
	 */

	/**
	 * top-most parameter list is appended
	 * @return the index of TOS
	 */
	public int addToParameterTOS ()
	{
		int tosIndex;
		String tos = parameterStack.get (tosIndex = parameterStack.size () - 1);
		parameterStack.set (tosIndex, tos + bracket (lastLeaf)); lastLeaf = "";
		return tosIndex;
	}

	/**
	 * top-most parameter list becomes last leaf
	 */
	public void popParameterStack ()
	{
		int tosIndex = addToParameterTOS ();
		lastLeaf = parameterStack.remove (tosIndex);
	}

	/**
	 * new holding place for a parameter list being constructed
	 */
	public void pushParameterStack () { parameterStack.add (""); }
	private List<String> parameterStack = new ArrayList<String>();


	/*
	 * operator stack
	 */

	private String lastOp = null;
	public String getLastOp () { return lastOp; }
	public void setLastOp(String lastOp) { this.lastOp = lastOp; }


	private int prec = 0, lastPrec = 0;
	public int getPrec() { return prec; }
	public int getLastPrec() { return lastPrec; }

	public void setPrec(int prec) { this.prec = prec; }
	public void setLastPrec(int lastPrec) { this.lastPrec = lastPrec; }

	public void setPrecedenceTo (SymbolMap.Operation operator) { this.prec = operator.getPrecedence (); }
	public boolean hasLowerPrecedence () { return prec <= lastPrec; }
	public void savePrecedence () { lastPrec = prec; }


	private List<Integer> precStack = new ArrayList<Integer>();
	private List<String> opStack = new ArrayList<String>();


	/**
	 * push current operator name and precedence to top of stack
	 * @param newOp current operator to be pushed on stack
	 * @param opPrecedence precedence of operator
	 */
	public void pushOp (String newOp, int opPrecedence)
	{ pushOp (); lastOp = newOp; lastPrec = opPrecedence; }
	public void pushOp () { precStack.add (lastPrec); opStack.add (lastOp); }

	/**
	 * set initial values of precedence, leaf, and op
	 */
	public void initializeContexts () { prec = 0; lastPrec = 0; lastLeaf = ""; lastOp = ""; }


	/**
	 * pop operator name and precedence from top of stack to current
	 * @return TRUE = stack had entry that has now been popped
	 */
	public boolean popOp ()
	{
		if (MathML.DUMPING) System.out.println ("POP prec=" + precStack.size () + "  op=" + opStack.size ());
		if (precStack.size () == 0 || opStack.size () == 0) return false;
		lastPrec = precStack.remove (precStack.size () - 1);
		lastOp = opStack.remove (opStack.size () - 1);
		return true;
	}


	/*
	 * reduce stack relative to last operation
	 */


	/**
	 * apply operation reducing stack size
	 * @param op relative operation for reduction
	 */
	public void reduce (SymbolMap.Operation op)
	{
		int subExprPrec = lastLeafPrec;

		if (op instanceof AbstractVectorReduction)
		{
			String arg = lastLeaf; int argPrec = lastLeafPrec;
			arg = bracket (arg); argPrec = ATOMIC_LEAF_PRECEDENCE;
			AbstractVectorReduction reduce = (AbstractVectorReduction)op;
			lastLeaf = reduce.markupForDisplay (lastOp, rangeDescription, arg, nodeFormater);
			lastLeafPrec = argPrec;
		}
		else if (op instanceof SymbolMap.ParameterizedFunction)
		{
			String arg = lastLeaf; int argPrec = lastLeafPrec;
			arg = nodeFormater.formatParenthetical (arg); argPrec = ATOMIC_LEAF_PRECEDENCE;

			if (op instanceof SymbolMap.EnhancedFunctionFormattingRequirement)
			{
				SymbolMap.ParameterizedFunction enhancedFunction = (SymbolMap.ParameterizedFunction) op;
				lastLeaf = enhancedFunction.markupForDisplay (lastOp, bracket (lastLeaf), nodeFormater);
			} else lastLeaf = bracket (idFor (lastOp) + arg);

			lastLeafPrec = argPrec;
		}
		else if (op instanceof SymbolMap.BinaryOperator)
		{
			boolean lfence = false, rfence = false;
			String left = popLeaf (), right = lastLeaf;
			int opPrec = lastPrec, leftPrec = lastLeafPrec, rightPrec = subExprPrec;
			
			if (leftPrec < opPrec) { left = bracket (left); leftPrec = ATOMIC_LEAF_PRECEDENCE; lfence = true; }
			if (rightPrec < opPrec) { right = bracket (right); rightPrec = ATOMIC_LEAF_PRECEDENCE; rfence = true; }
			lastLeaf = ((SymbolMap.BinaryOperator)op).markupForDisplay (lastOp, left, right, lfence, rfence, nodeFormater);

			int argPrec = Math.min (leftPrec, rightPrec);
			lastLeafPrec = Math.min (opPrec, argPrec);
		}
		else if (op instanceof SymbolMap.UnaryPostfixOperator)
		{
			SymbolMap.UnaryPostfixOperator upop = (SymbolMap.UnaryPostfixOperator)op;
			lastLeaf = upop.markupForDisplay (bracket (lastLeaf), lastLeafPrec < lastPrec, lastOp, nodeFormater);
			lastLeafPrec = ATOMIC_LEAF_PRECEDENCE;
		}
		else if (op instanceof SymbolMap.UnaryOperator)
		{
			SymbolMap.UnaryOperator uop = (SymbolMap.UnaryOperator)op;
			lastLeaf = uop.markupForDisplay (lastOp, bracket (lastLeaf), nodeFormater);
			lastLeafPrec = ATOMIC_LEAF_PRECEDENCE;
		}
		else if (op instanceof SymbolMap.Assignment)
		{
			String destination = popLeaf (), source = lastLeaf;
			lastLeaf = nodeFormater.formatAssignment (destination, source);
		}
	}


	/**
	 * range descriptor is captured here for use by vector reduction objects
	 * @param rangeDescription a descriptor as described in vector reduction object
	 */
	public void setRangeDescription
	(AbstractVectorReduction.Range rangeDescription) { this.rangeDescription = rangeDescription; }
	private AbstractVectorReduction.Range rangeDescription = null;


	/*
	 * tracing of stack status
	 */


	/**
	 * trace stack and leaf changes
	 */
	public void dump ()
	{
		if (!MathML.DUMPING) return;

		System.out.println
		(
			"lastOp:" +
			"   symbol=" + lastOp +
			"   precedence=" + lastPrec
		);

		System.out.println ("lastLeaf  = " + lastLeaf);
		System.out.println ("LeafStack = " + leafStack);
		System.out.println ("PrecStack = " + precStack);
		System.out.println ("opStack   = " + opStack);
		System.out.println ("+++");
	}


}

