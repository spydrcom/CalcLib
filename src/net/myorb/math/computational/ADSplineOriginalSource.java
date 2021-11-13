
package net.myorb.math.computational;

public class ADSplineOriginalSource
{

	public static final double[]
	upTo = new double[]{-10, -1, -0.1, -0.000001, -0.0000000001, 0.000001, 0.1, 1, 10},
	area = new double[]
			{
					-4.15696892968E-6,
					-0.2193797774266,
					-1.603540023,
					-11.4153,
					-9.21,
					 9.21,
					 11.615481,
					 3.51793063,
					 2490.333858425
			};

	public double computeArea (double x)
	{
		double areaToPoint = 0;
		double coveredThru = -100, next;
		
		for (int i = 0; i < upTo.length; i++)
		{
			next = upTo[i];

			if (x < next)
			{
				if ( (next-x) < (x-coveredThru) )
				{
					coveredThru = next;
					areaToPoint += area[i];
				}
				break;
			}

			areaToPoint += area[i];

			if (x == next)
			{
				return areaToPoint;
			}

			coveredThru = next;
		}

		if (coveredThru > x)
		{
			areaToPoint -= areaBetween (x, coveredThru);
		}
		else
		{
			areaToPoint += areaBetween (coveredThru, x);
		}

		return areaToPoint;
	}

	public double areaBetween (double lo, double hi)
	{
		return 0;
	}

}
