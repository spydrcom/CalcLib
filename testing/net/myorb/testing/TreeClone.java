
package net.myorb.testing;

import net.myorb.math.polynomial.algebra.*;

import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.expressions.tree.*;

import net.myorb.data.notations.json.*;

public class TreeClone
{

	public static void main (String [] args) throws Exception
	{
		ExpressionSpaceManager <Double>
			mgr = new ExpressionFloatingFieldManager ();
		Environment <Double> environment = new Environment <> (mgr);

		Gardener <Double> G1, G2;
		G1 = Gardener.loadFromJson ("FSE1", environment);
		JsonSemantics.JsonValue tree1 = G1.getExpression ().toJson ();
		Elements.Equation eqn1 = RepresentationConversions.translate (tree1);
//		JsonPrettyPrinter.sendTo (tree, System.out);

		G2 = Gardener.loadFromJson ("FSE2", environment);
		JsonSemantics.JsonValue tree2 = G2.getExpression ().toJson ();
		Elements.Equation eqn2 = RepresentationConversions.translate (tree2);
//		JsonPrettyPrinter.sendTo (tree, System.out);

//		System.out.println (G.getExpressionName ());
//		System.out.println (G.getExpression ());

		System.out.println (eqn1);
		System.out.println (eqn2);

		System.out.println (Operations.productOf (new Elements.Constant ("654"), eqn2));
		System.out.println (Operations.productOf (new Elements.Variable ("xyz"), eqn2));
		System.out.println (Operations.productOf (eqn1, eqn2));

		Elements.Sum eqn3 = new Elements.Sum ();
		Elements.add (new Elements.Constant ("x"), eqn3);
		Elements.add (new Elements.Variable ("1"), eqn3);
		System.out.println (Operations.productOf (eqn3, eqn2));

	}

}
