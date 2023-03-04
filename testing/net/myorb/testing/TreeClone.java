
package net.myorb.testing;

import net.myorb.math.polynomial.algebra.*;
import net.myorb.math.polynomial.algebra.Elements.Factor;
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

		Gardener <Double> G0, G1, G2;
		G0 = Gardener.loadFromJson ("FSE", environment);
		JsonSemantics.JsonValue tree0 = G0.getExpression ().toJson ();
		Elements.Equation eqn0 = RepresentationConversions.translate (tree0);

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

		System.out.println (eqn0);
		System.out.println (eqn1);
		System.out.println (eqn2);

//		System.out.println (Operations.productOf (new Elements.Constant ("654"), eqn2));
//		show (Operations.productOf (new Elements.Constant ("654"), eqn2));
//
//		System.out.println (Operations.productOf (new Elements.Variable ("xyz"), eqn2));
//		show (Operations.productOf (new Elements.Variable ("xyz"), eqn2));
//
//		System.out.println (Operations.productOf (eqn1, eqn2));
//		show (Operations.productOf (eqn1, eqn2));
//
//		Elements.Sum eqn3 = new Elements.Sum ();
//		Elements.add (new Elements.Variable ("x"), eqn3);
//		Elements.add (new Elements.Constant ("1"), eqn3);
//
//		System.out.println (Operations.productOf (eqn3, eqn2));
//		show (Operations.productOf (eqn3, eqn2));

//		!! J_n (x) = ( x^2 * FSE2(x) ) + ( x * FSE1(x) ) + ( (x^2-n^2) * FSE(x) )

		Factor
			x = Elements.powerFactor ("x", 1.0),
			x2 = Elements.powerFactor ("x", 2.0);
		Elements.Sum J0 = new Elements.Sum ();

		Elements.add (Operations.productOf (x, eqn1), J0);
		Elements.add (Operations.productOf (x2, eqn0), J0);
		Elements.add (Operations.productOf (x2, eqn2), J0);

		System.out.println ();
		System.out.println ("===");
		System.out.println ("= J0 =");
		System.out.println ("===");
		show (J0);

		Elements.Sum x2n2 = new Elements.Sum ();
		Elements.add (new Elements.Constant (-1.0), x2n2);
		Elements.add (x2, x2n2);

		Elements.Sum J1 = new Elements.Sum ();
		Elements.add (Operations.productOf (x, eqn1), J1);
		Elements.add (Operations.productOf (x2n2, eqn0), J1);
		Elements.add (Operations.productOf (x2, eqn2), J1);

		System.out.println ();
		System.out.println ("===");
		System.out.println ("= J1 =");
		System.out.println ("===");
		show (J1);

		x2n2 = new Elements.Sum ();
		Elements.add (new Elements.Constant (-4.0), x2n2);
		Elements.add (x2, x2n2);

		Elements.Sum J2 = new Elements.Sum ();
		Elements.add (Operations.productOf (x, eqn1), J2);
		Elements.add (Operations.productOf (x2n2, eqn0), J2);
		Elements.add (Operations.productOf (x2, eqn2), J2);

		System.out.println ();
		System.out.println ("===");
		System.out.println ("= J2 =");
		System.out.println ("===");
		show (J2);

	}

	public static void show (Elements.Factor eqn)
	{
		System.out.println
		(
			Manipulations.reduceAndCollectTerms
			(
				(Elements.Sum) eqn, "x"
			)
		);
		System.out.println ("===");
	}

}
