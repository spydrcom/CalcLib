
package net.myorb.testing;

import cern.jet.math.*;

import java.lang.reflect.*;

public class ExternalFunction {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) throws Exception
	{
		Double d = 7d;
		System.out.println (Bessel.j0(d));
		Class c = Class.forName ("cern.jet.math.Bessel");
		for (Method m : c.getMethods())
		{
			System.out.println (m.getName());
		}
		Method j0 = c.getMethod("j0", new Class[]{double.class});
		System.out.println (j0.invoke (null, new Object[]{d}));
		//java.lang.Math.
	}

}
