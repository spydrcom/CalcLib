
package net.myorb.math.expressions.controls;

import net.myorb.math.*;

@SuppressWarnings("rawtypes")
public class EvaluationControlDriver implements Runnable
{


	public EvaluationControlDriver (Class controlClass, SpaceManager manager)
	{ this.controlClass = controlClass; this.manager = manager; }
	Class controlClass; SpaceManager manager;


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run ()
	{
		try { controlClass.newInstance (); }
		catch (Exception x) { x.getStackTrace (); }
	}


	public String getTitle ()
	{ return "CALCLIB - " + toString () + " Domain"; }


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return manager.getName (); }


}
