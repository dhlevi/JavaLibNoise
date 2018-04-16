package ca.dhlevi.libnoise.paint;

import java.awt.Color;

public class PainterUtilities 
{
	public static Color blend(Color c0, Color c1) 
	{
	    double totalAlpha = c0.getAlpha() + c1.getAlpha();
	    double weight0 = c0.getAlpha() / totalAlpha;
	    double weight1 = c1.getAlpha() / totalAlpha;

	    double r = weight0 * c0.getRed() + weight1 * c1.getRed();
	    double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
	    double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
	    double a = Math.max(c0.getAlpha(), c1.getAlpha());

	    return new Color((int) r, (int) g, (int) b, (int) a);
	}
	
	public static Color gradient(Color minColor, Color maxColor, double value)
	{
        int red = (int) (maxColor.getRed() * value + minColor.getRed() * (1 - value));
        int green = (int) (maxColor.getGreen() * value + minColor.getGreen() * (1 - value));
        int blue = (int) (maxColor.getBlue() * value + minColor.getBlue() * (1 - value));
        
        return new Color(red, green, blue);
	}
}
