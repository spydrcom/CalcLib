
package net.myorb.math.complexnumbers;

import net.myorb.math.Polynomial;
import net.myorb.math.SpaceManager;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

import java.util.ArrayList;
import java.util.List;

/**
 * implementation of the quartic equation solution
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class QuarticEquation<T> extends Arithmetic<ComplexValue<T>>
	implements FunctionAnalyzer<ComplexValue<T>>
{

	static final int TRACE_LEVEL = 5;

	/**
	 * managers required for the complex type and the component type
	 * @param manager a complex field manager object reference
	 */
	@SuppressWarnings("unchecked")
	public QuarticEquation (SpaceManager<ComplexValue<T>> manager)
	{ this (manager, new ComplexLibrary<T> (manager.getComponentManager (), null)); }
	@SuppressWarnings("unchecked") public QuarticEquation (SpaceManager<ComplexValue<T>> manager, ComplexLibrary<T> lib)
	{
		super (manager);
		this.scalar = manager.getComponentManager ();
		this.generateConstants (lib);
	}
	SpaceManager<T> scalar;


	/**
	 * constant objects built for value with multiple references
	 * @param lib  root library to be used
	 */
	public void generateConstants (ComplexLibrary<T> lib)
	{
		this.setLibrary (this.clib = lib);
		ONE = real (1); TWO = real (2); THREE = real (3);
		FOUR = real (4); NINE = real (9); T7 = real (27);
	}
	ComplexValue<T> ONE, TWO, THREE, FOUR, NINE, T7;
	ComplexLibrary<T> clib;


	/**
	 * compute the discriminant for the polynomial
	 * @param a the (possible complex) coefficient of x^4
	 * @param b the (possible complex) coefficient of x^3
	 * @param c the (possible complex) coefficient of x^2
	 * @param d the (possible complex) coefficient of x
	 * @param e the Y-intercept (constant term)
	 * @return the computed discriminant value
	 */
	@SuppressWarnings("unchecked")
	public ComplexValue<T> discriminant
		(
			ComplexValue<T> a, ComplexValue<T> b,
			ComplexValue<T> c, ComplexValue<T> d,
			ComplexValue<T> e
		)
	{
		return sigma
		(
			real (256).times (a.toThe (3)).times (e.toThe (3)),
			real (-192).times (a.squared ()).times (e.squared ()).times (b).times (d),
			real (-128).times (a.squared ()).times (c.squared ()).times (e.squared ()),
			real (144).times (a.squared ()).times (c).times (d.squared ()).times (e),
			real (-27).times (a.squared ()).times (d.toThe (4)),
			real (144).times (a).times (b.squared ()).times (c).times (e.squared ()),
			real (-6).times (a).times (b.squared ()).times (d.squared ()).times (e),
			real (-80).times (a).times (b).times (c.squared ()).times (d).times (e),
			real (18).times (a).times (b).times (c).times (d.toThe (3)),
			real (16).times (a).times (c.toThe (4)).times (e),
			real (-4).times (a).times (c.toThe (3)).times (d.squared ()),
			real (-27).times (b.toThe (4)).times (e.squared ()),
			real (18).times (b.toThe (3)).times (c).times (d).times (e),
			real (-4).times (b.toThe (3)).times (d.toThe (3)),
			real (-4).times (b.toThe (2)).times (c.toThe (3)).times (e),
			b.squared ().times (c.squared ()).times (d.squared ())
		);
	}


	/**
	 * compute D for the polynomial
	 * @param a the (possible complex) coefficient of x^4
	 * @param b the (possible complex) coefficient of x^3
	 * @param c the (possible complex) coefficient of x^2
	 * @param d the (possible complex) coefficient of x
	 * @param e the Y-intercept (constant term)
	 * @return the computed value of D
	 */
	public ComplexValue<T> getD
		(
			ComplexValue<T> a, ComplexValue<T> b,
			ComplexValue<T> c, ComplexValue<T> d,
			ComplexValue<T> e
		)
	{
		return real (64).times (a.toThe (3)).times (e)
			.minus (real (16).times (a.squared ()).times (c.squared ()))
			.plus (real (16).times (a).times (b.squared ()).times (c))
			.minus (real (16).times (a.squared ())).times (b).times (d)
			.minus (real (3).times (b.toThe (4)));
	}


	/**
	 * compute P for the polynomial
	 * @param a the (possible complex) coefficient of x^4
	 * @param b the (possible complex) coefficient of x^3
	 * @param c the (possible complex) coefficient of x^2
	 * @param d the (possible complex) coefficient of x
	 * @param e the Y-intercept (constant term)
	 * @return the computed value of P
	 */
	public ComplexValue<T> getP
		(
			ComplexValue<T> a, ComplexValue<T> b,
			ComplexValue<T> c, ComplexValue<T> d,
			ComplexValue<T> e
		)
	{
		return real (8).times (a).times (c).minus (real (3).times (b.squared ()));
	}
	public ComplexValue<T> getLittleP
		(
			ComplexValue<T> a, ComplexValue<T> b,
			ComplexValue<T> c, ComplexValue<T> d,
			ComplexValue<T> e
		)
	{
		return getP (a, b, c, d, e).divideBy (real (8).times (a.squared ()));
	}


	/**
	 * compute Q for the polynomial
	 * @param a the (possible complex) coefficient of x^4
	 * @param b the (possible complex) coefficient of x^3
	 * @param c the (possible complex) coefficient of x^2
	 * @param d the (possible complex) coefficient of x
	 * @param e the Y-intercept (constant term)
	 * @return the computed value of Q
	 */
	@SuppressWarnings("unchecked")
	public ComplexValue<T> getQ
		(
			ComplexValue<T> a, ComplexValue<T> b,
			ComplexValue<T> c, ComplexValue<T> d,
			ComplexValue<T> e
		)
	{
		return sigma
		(
			b.toThe (3),
			real (8).times (d).times (a.squared ()),
			real (-4).times (a).times (b).times (c)
		);
	}
	public ComplexValue<T> getLittleQ
		(
			ComplexValue<T> a, ComplexValue<T> b,
			ComplexValue<T> c, ComplexValue<T> d,
			ComplexValue<T> e
		)
	{
		return getQ (a, b, c, d, e).divideBy (real (8).times (a.toThe (3)));
	}


	/**
	 * compute the delta values
	 *  from the polynomial coefficients
	 * @param a the (possible complex) coefficient of x^4
	 * @param b the (possible complex) coefficient of x^3
	 * @param c the (possible complex) coefficient of x^2
	 * @param d the (possible complex) coefficient of x
	 * @param e the Y-intercept (constant term)
	 * @return the computed delta values
	 */
	public List<ComplexValue<T>> getDelta
		(
			ComplexValue<T> a, ComplexValue<T> b,
			ComplexValue<T> c, ComplexValue<T> d,
			ComplexValue<T> e
		)
	{
		ComplexValue<T> d0, d1, disc = discriminant (a, b, c, d, e);
		List<ComplexValue<T>> deltas = new ArrayList<ComplexValue<T>> ();

		deltas.add
		(
			d0 = c.squared ().minus (THREE.times (b).times (d)).plus (real (12).times (a).times (e))
		);
		deltas.add
		(
			d1 = TWO.times (c.toThe (3))
			.minus (NINE.times (b).times (c).times (d))
			.plus (T7.times (b.squared ()).times (e))
			.plus (T7.times (d.squared ()).times (a))
			.minus (real (72).times (a).times (c).times (e))
		);

		deltas.add
		(d1.squared ().minus (FOUR.times (d0.toThe (3))));							//  d#2 = d#1^2 - 4d#0^3
		deltas.add (neg (T7).times (disc));									// d#3 is a sanity check, should be same as d#2

		deltas.add (disc);													  // save a copy of core discriminant as d#4
		deltas.add (getD (a, b, c, d, e));						// save a copy of D as d#5, this discriminant showing two double roots
		dumpDeltaList (deltas, null, 4);
		return deltas;
	}


	/**
	 * compute Q from delta values.
	 *  Q = root3 ((d#1 + sqrt (d#1^2 - 4d#0^3)) / 2).
	 *  this is a different Q value, more like C of the cubic
	 * @param delta the list of delta values
	 * @return value of C
	 */
	public ComplexValue<T> getQ (List<ComplexValue<T>> delta)
	{
		ComplexValue<T> d2, cCube;
		if ((d2 = delta.get (2)).isZero ()) cCube = delta.get (1);
		else cCube = delta.get (1).plus (clib.sqrt (d2)).divideBy (TWO);
		return clib.root (cCube, 3);
	}


	/**
	 * convert the list of
	 *  polynomial coefficients to a delta list
	 * @param a the (possible complex) coefficient of x^4
	 * @param b the (possible complex) coefficient of x^3
	 * @param c the (possible complex) coefficient of x^2
	 * @param d the (possible complex) coefficient of x
	 * @param e the Y-intercept (constant term)
	 * @return the computed roots
	 */
	public List<ComplexValue<T>> getRoots
		(
				ComplexValue<T> a, ComplexValue<T> b,
				ComplexValue<T> c, ComplexValue<T> d,
				ComplexValue<T> e
		)
	{
		List<ComplexValue<T>> delta = getDelta (a, b, c, d, e);
		if (TRACE_LEVEL != 0) dumpDeltaList (delta, natureOfRoots (a, b, c, d, e), 6);
		ComplexValue<T> p = getLittleP (a, b, c, d, e), q = getLittleQ (a, b, c, d, e);
		extendedDelta (delta, p, q, THREE.times (a), neg (b).divideBy (FOUR.times (a)));
		List<ComplexValue<T>> roots = getRoots (delta);
		dumpDeltaList (delta, null, 5);
		return roots;
	}


	/**
	 * store additional key items in the delta array.
	 *  this allows a portable object to contain all data needs to solve for roots.
	 *  this is also an ideal object to use for debugging purposes.
	 * @param delta the previous list of 5 delta values
	 * @param p the computed value of p from P										// p and q, these are the 2nd AND 1st degree
	 * @param q the computed value of q												//   coefficients of the associated depressed quartic
	 * @param threeTimesA 3 * a														// this is used to compute S of the algorithm
	 * @param negBover4a -b / 4a													// this is the common real root term
	 */
	public void extendedDelta
		(
			List<ComplexValue<T>> delta,
			ComplexValue<T> p, ComplexValue<T> q,
			ComplexValue<T> threeTimesA, ComplexValue<T> negBover4a
		)
	{
		ComplexValue<T> deltaZero = delta.get (0), Q = getQ (delta),					// d#0 & Q
			d0PlusQ = Q.plus (deltaZero.divideBy (Q)).divideBy (threeTimesA);			// ( Q + d#0/Q ) / 3a
		delta.add (p); delta.add (q);													// p and q, items # 6 & 7

		delta.add (negBover4a);															// -b / 4a is the common real root, d#8
		delta.add (d0PlusQ);															// key component of S, item #9
		delta.add (Q);																	// Q is d#10

		dumpDeltaList (delta, null, 2);
	}


	/**
	 * output a dump of name-value pairs contained in the delta list
	 * @param delta the list of computed delta values
	 * @param nature characterization of roots
	 * @param dumpLevel the level of detail
	 */
	@SuppressWarnings("unused")
	public void dumpDeltaList (List<ComplexValue<T>> delta, RootNature nature, int dumpLevel)
	{
		if (TRACE_LEVEL == 0) return;
		System.out.println ("*LEVEL " + dumpLevel + "*");
		if (nature != null) System.out.println ("characterization = " + nature);
		if (TRACE_LEVEL < dumpLevel) return;

		for (int i = 0; i < delta.size(); i++)
		{
			System.out.println (names[i] + " = " + delta.get (i));
		}
		System.out.println ("===");
	}
	String[] names = new String[]
	    {
				"d0", "d1", "d#1^2 - 4d#0^3",			// the true delta values d(0) and d(1) as prescribed by algorithm
				"sanity", "discriminant", "D",			// the capital delta values, the discriminants used to characterize the roots
				"p", "q",								// the values computed as part of the algorithm including p, q
				"- b / (4 * a)",						// -b/4a is the common real term of all roots
				"(Q + d#0/Q) / 3a",						// the computed value of Q plus with delta 0
				"Q", "S",								// Q and S
				"X1", "X2", "X3", "X4"					// roots
		};


	/**
	 * compute the list of roots
	 * @param delta the list of computed delta values
	 * @return the list of computed roots
	 */
	public List<ComplexValue<T>> getRoots (List<ComplexValue<T>> delta)
	{
		ComplexValue<T> d0PlusQ = delta.get (9), b4a = delta.get (8),			// -b / 4a (the common real root)
			neg2p = neg (TWO).times (delta.get (6)), q = delta.get (7);			// p & q from d#6 and d#7
		List<ComplexValue<T>> X = new ArrayList<ComplexValue<T>>();

		ComplexValue<T> S = lib.sqrt
			(
				d0PlusQ.plus (neg2p.divideBy (THREE))							// ( Q + d#0/Q ) / 3a - 2p / 3
			).divideBy (TWO);
		delta.add (S);															// d#11

		ComplexValue<T> qOverS = q.divideBy (S),								// q / S
			s2M2p = neg (FOUR).times (S.squared ()).plus (neg2p);				// -4S^2 - 2p
		add (X, b4a, neg (S), lib.sqrt (s2M2p.plus (qOverS)).divideBy (TWO));	// X#1 & X#2
		add (X, b4a, S, lib.sqrt (s2M2p.minus (qOverS)).divideBy (TWO));		// X#3 & X#4

		delta.addAll (X);
		dumpDeltaList (delta, null, 1);
		return X;
	}


	/**
	 * add conjugate pair to list of roots.
	 *  SQRT may be real so conjugate may not mean complex
	 * @param X the list accumulating the computed roots
	 * @param b4a the common real root term
	 * @param S the computed S value
	 * @param plusMinus +/- SQRT
	 */
	void add
	(List<ComplexValue<T>> X, ComplexValue<T> b4a, ComplexValue<T> S, ComplexValue<T> plusMinus)
	{ X.add (b4a.plus (S).plus (plusMinus)); X.add (b4a.plus (S).minus (plusMinus)); }


	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.FunctionAnalyzer#analyze(net.myorb.math.Polynomial.PowerFunction)
	 */
	public List<ComplexValue<T>> analyze (Polynomial.PowerFunction<ComplexValue<T>> polynomial)
	{
		Polynomial.Coefficients<ComplexValue<T>> coefficients = polynomial.getCoefficients ();
		ComplexValue<T> a = coefficients.get (4), b = coefficients.get (3), c = coefficients.get (2),
				d = coefficients.get (1), e = coefficients.get (0);
		List<ComplexValue<T>> roots = getRoots (a, b, c, d, e);
		return roots;
	}


	/**
	 * dump a roots list
	 *  with function value validations
	 * @param root the list of computed roots
	 * @param validator a function to use for the test
	 */
	public void dumpRootsList (List<ComplexValue<T>> root, Polynomial.PowerFunction<ComplexValue<T>> validator)
	{
		for (int i = 0; i < root.size(); i++)
		{
			ComplexValue<T> rn = root.get (i);
			System.out.println ("root " + (i+1) + " = " + rn + "   f(root(i)) = " + validator.eval(rn));
		}
	}


	/**
	 * a description of the type of roots
	 */
	public enum RootNature
	{
		TWO_COMPLEX_CONJUGATE_PAIRS, DOUBLE_COMPLEX_CONJUGATE_PAIR,
		COMPLEX_CONJUGATE_PAIR_AND_TWO_REAL, COMPLEX_CONJUGATE_PAIR_AND_DOUBLE_REAL,
		DOUBLE_REAL_AND_TWO_SIMPLE_REAL, FOUR_DISTINCT_REAL, FOUR_IDENTICAL_REAL_ROOTS,
		TWO_REAL_DOUBLE_ROOTS, TRIPLE_REAL_AND_SINGLE_REAL
	}
	public RootNature natureOfRoots
		(
		ComplexValue<T> a, ComplexValue<T> b,
		ComplexValue<T> c, ComplexValue<T> d,
		ComplexValue<T> e
		)
	{
		ComplexValue<T>
		P = getP (a, b, c, d, e), Q = getQ (a, b, c, d, e);
		List<ComplexValue<T>> delta = getDelta (a, b, c, d, e);
		return natureOfRoots (delta, P, Q);
	}
	public RootNature natureOfRoots (List<ComplexValue<T>> delta, ComplexValue<T> P, ComplexValue<T> Q)
	{
		ComplexValue<T> deltaZero = delta.get (0), discriminant = delta.get (4), D = delta.get (5);

		if (isNeg (discriminant))
		{
			return RootNature.COMPLEX_CONJUGATE_PAIR_AND_TWO_REAL;
		}
		else if (discriminant.isZero ())
		{
			if ((!isNeg (D) && !D.isZero ()) || ((!isNeg (P) && !P.isZero ()) && (!D.isZero () || !Q.isZero ())))
			{
				return RootNature.COMPLEX_CONJUGATE_PAIR_AND_DOUBLE_REAL;
			}
			if (D.isZero ())
			{
				if (deltaZero.isZero ())
					return RootNature.FOUR_IDENTICAL_REAL_ROOTS;
				else if (isNeg (P)) return RootNature.TWO_REAL_DOUBLE_ROOTS;
				else if (!isNeg (P) && !P.isZero () && Q.isZero ()) return RootNature.DOUBLE_COMPLEX_CONJUGATE_PAIR;
			}
			else
			{
				if (deltaZero.isZero ()) return RootNature.TRIPLE_REAL_AND_SINGLE_REAL;
				if (isNeg (P) && isNeg (D)) return RootNature.DOUBLE_REAL_AND_TWO_SIMPLE_REAL;
			}
		}
		else
		{
			if (isNeg (P) && isNeg (D)) return RootNature.FOUR_DISTINCT_REAL;
			if (!isNeg (P) || !isNeg (D)) return RootNature.TWO_COMPLEX_CONJUGATE_PAIRS;
		}
		return null;
	}


	/**
	 * unit test
	 */
	void test ()
	{
		//execute ("polynomial (4, -7, -9, -1, 1)");
		//ComplexValue<T> a = discrete(1), b = discrete(-1), c = discrete(-9), d = discrete(-7), e = discrete(4);
		ComplexValue<T> a = discrete(1), b = discrete(0), c = discrete(-5), d = discrete(0), e = discrete(4);
		Polynomial.Coefficients<ComplexValue<T>> co = new Polynomial.Coefficients<ComplexValue<T>>();
		co.add (e); co.add (d); co.add (c); co.add (b); co.add (a);

		ComplexFieldManager<T> cfmt = new ComplexFieldManager<T> (scalar);
		Polynomial<ComplexValue<T>> p = new Polynomial<ComplexValue<T>> (cfmt);
		Polynomial.PowerFunction<ComplexValue<T>> f = p.getPolynomialFunction (co);
		
		//System.out.println (natureOfRoots (a, b, c, d, e));
		dumpRootsList (getRoots (a, b, c, d, e), f);
	}
	public static void main(String[] args)
	{
		ComplexFieldManager<Double> cmgr =
			new ComplexFieldManager<Double>(new DoubleFloatingFieldManager ());
		QuarticEquation<Double> equation = new QuarticEquation<Double> (cmgr);
		equation.test ();
	}


}


