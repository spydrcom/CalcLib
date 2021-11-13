
package net.myorb.testing;

import net.myorb.math.expressions.gui.*;

public class EnvDisplay
{
	public static void main (String[] args)
	{
		EnvironmentCore.CoreMap coreMap =
				new EnvironmentCore.CoreMap ();
		DisplayEnvironment.showEnvironment(coreMap);
	}
}
