

package net.myorb.testing;

public class IntegrationRefinedOriginal
{

	public static double f(double x) { return Math.sqrt (1-x*x); }

	public static void main(String[] args)
	{
		double pieces = 2, sum = 0;
		double lo = -1, hi = 1, mid = (hi-lo) / 2;

		long start = System.currentTimeMillis();

		for (int m = 1; m <= 7; m++)
		{
			for (int n = 1; n <= 5; n++)
			{
				double halfMid = mid / 2, x = lo + halfMid;

				for (long i = 1; i <= pieces; i++)
				{
					sum = sum + f(x);
					x += mid;
				}

				if (pieces > 1000000)
				{
					double area = sum / pieces, pi = area * 2;
					long time = System.currentTimeMillis() - start;
					System.out.println (time + "\t" + pieces + "\t" + pi);
				}

				mid = halfMid;
				pieces *= 2;
			}
		}
	}

}
