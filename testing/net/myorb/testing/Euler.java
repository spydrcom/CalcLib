
package net.myorb.testing;


public class Euler
{

	public static void main (String[] a)
	{
		for (int n=0; n<20; n++)
		{
		double En = net.myorb.math.specialfunctions.Euler.numberN (n);
		System.out.println (En);
		}
	}
	// E = (1,0,-1,0,5,0,-61,0,1385,0,-50521,0,2702765,0)

}
