
package net.myorb.math.expressions.charting.colormappings;

public class LegacyColorSchemeConfiguration
{

	public static void postColorSchemeFactories ()
	{
		ContourColorSchemeRequest.addScheme ("PointCountWeighted", PointCountWeighted.getColorSchemeFactory ());
		ContourColorSchemeRequest.addScheme ("LegacyAlternativeAlgorithm", LegacyAlternativeAlgorithmColorScheme.getColorSchemeFactory ());
		ContourColorSchemeRequest.addScheme ("PointCountWeightedWithValueSaturation", PointCountWeightedWithValueSaturation.getColorSchemeFactory ());
		ContourColorSchemeRequest.addScheme ("PointCountWeightedWithSaturation", PointCountWeightedWithSaturation.getColorSchemeFactory ());
		ContourColorSchemeRequest.addScheme ("ValueWeightedWithAggregation", ValueWeightedWithAggregation.getColorSchemeFactory ());
		ContourColorSchemeRequest.addScheme ("IterationCrossRefWeighted", IterationCrossRefWeighted.getColorSchemeFactory ());
		ContourColorSchemeRequest.addScheme ("IterationWeighted", IterationWeighted.getColorSchemeFactory ());
	}

}
