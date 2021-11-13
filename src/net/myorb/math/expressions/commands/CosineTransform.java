
package net.myorb.math.expressions.commands;

import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.expressions.symbols.AbstractFunction;

import net.myorb.math.expressions.evaluationstates.DeclarationSupport;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.computational.ClenshawCurtisQuadrature;
import net.myorb.math.computational.DCT;

/**
 * support for commands handling DCT calculation
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class CosineTransform<T> extends Utilities<T>
{


	public CosineTransform
	(Environment<T> environment)
	{ super (environment); }


	/**
	 * prepare and run DCT analysis
	 * @param source the list of tokens from the command
	 */
	public void analyze (CommandSequence source)
	{
		source.remove (0);
		int N = Integer.parseInt (source.remove (0).getTokenImage ());
		ExpressionSpaceManager<T> smgr = environment.getSpaceManager ();
		DeclarationSupport<T> decls = new DeclarationSupport<T> (environment);
		DeclarationSupport.Analysis<T> analysis = decls.analyzeFunction (source);
		AbstractFunction.DomainConstraints<T> c = analysis.getDomainConstraints ();
		ClenshawCurtisQuadrature<T> CCQ = new ClenshawCurtisQuadrature<T> (smgr);
		DCT.Type t = getType (source);

		DCT.Transform<T> dct = analysis.isStandard ()?										// STD implies [-1,1]
				dct = CCQ.getTransform (decls.getFunction (analysis), N, t):				// no interval change if STD
		CCQ.getTransform (decls.getFunction (analysis), c.getLo (), c.getHi (), N, t);		// otherwise need [lo,hi] for interval change

		StringBuffer buffer = new StringBuffer ()
		.append (analysis.getFunctionSymbol ()).append (" (cos (x) )");						// DCT results in f ( cos(theta) )
		analysis.defineTransform (dct, buffer).setSymbolicDomainConstraints					// use symbolic constraints
		(smgr.getZero (), smgr.getOne (), decls.getPi (), environment);						// will show [0,pi]
	}


	/**
	 * determine requested DCT type
	 * @param tokens the list of tokens from the command
	 * @return type of DCT to compute
	 */
	static DCT.Type getType (CommandSequence tokens)
	{
		try
		{
			return tokens.size () > 0 ?														// optional DCT 
			DCT.Type.valueOf (tokens.remove (0).getTokenImage ()) :							// type specification
			DCT.Type.I;																		// default is type I
		}
		catch (Exception e)
		{
			throw new RuntimeException ("DCT type not recognized");
		}
	}


}

