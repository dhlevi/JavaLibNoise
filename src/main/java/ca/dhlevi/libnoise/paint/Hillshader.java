package ca.dhlevi.libnoise.paint;

import java.awt.Color;

// Hillshader will apply a dark shading to a heightmap based on altitude and azimuth to simulate light on the map
// not good for a heightmap needed for rendering terrain, but looks good when applied to a terrain map.
public class Hillshader
{
    public static HSLColor shadePixel(double[][] noise, int width, int height, int x, int y, int buffer)
    {
        double zFactor = 40.0; // the higher the 'Z' factor, the more pronounced the hillshading

        return shadePixel(noise, width, height, x, y, zFactor, buffer);
    }

    public static HSLColor shadePixel(double[][] noise, int width, int height, int x, int y, double zFactor, int buffer)
    {
        // The default Altitude of 45 degrees and Azimuth of 315 degrees will be used.
        double Altitude = 80.0;
        double Azimuth = 315.0;
        double cellSize = 1.0;

        return shadePixel(noise, width, height, x, y, zFactor, Altitude, Azimuth, cellSize, buffer);
    }

    public static HSLColor shadePixel(double[][] noise, int width, int height, int x, int y, double zFactor, double Altitude, double Azimuth, double cellSize, int buffer)
    {
        double zenithDeg = 90.0 - Altitude;
        double zenithRad = zenithDeg * Math.PI / 180.0;

        double azimuthMath = 360.0 - Azimuth + 90.0;
        if (azimuthMath >= 360.0)
            azimuthMath = azimuthMath - 360.0;
        double azimuthRad = azimuthMath * Math.PI / 180.0;

        int bx = x + buffer;
        int by = y + buffer;

        double nw = bx == 0 || by == 0 ? 0 : noise[bx - 1][by - 1];
        double n = by == 0 ? 0 : noise[bx][by - 1];
        double ne = bx >= width - 1 || by == 0 ? 0 : noise[bx + 1][by - 1];
        double w = bx == 0 ? 0 : noise[bx - 1][by];
        double e = bx >= width - 1 ? 0 : noise[bx + 1][by];
        double sw = bx == 0 || by >= height - 1 ? 0 : noise[bx - 1][by + 1];
        double s = by >= height - 1 ? 0 : noise[bx][by + 1];
        double se = bx >= width - 1 || by >= height - 1 ? 0 : noise[bx + 1][by + 1];

        double xRateOfChange = ((ne + (2 * e) + se) - (nw + (2 * w) + sw)) / (8.0 * cellSize);
        double yRateOfChange = ((sw + (2 * s) + se) - (nw + (2 * n) + ne)) / (8.0 * cellSize);

        double slopeRad = Math.atan(zFactor * Math.sqrt((Math.pow(xRateOfChange, 2) + Math.pow(yRateOfChange, 2))));

        double aspectRad = 0.0f;

        if (xRateOfChange != 0)
            aspectRad = Math.atan2(yRateOfChange, -xRateOfChange);
        if (aspectRad < 0)
            aspectRad = (2 * Math.PI) + aspectRad;

        if (xRateOfChange == 0)
        {
            if (yRateOfChange > 0)
                aspectRad = Math.PI / 2.0;
            else if (yRateOfChange < 0)
                aspectRad = (2.0 * Math.PI) - (Math.PI / 2.0);
        }

        int hillshade = (int) Math.round((255.0 * ((Math.cos(zenithRad) * Math.cos(slopeRad)) + (Math.sin(zenithRad) * Math.sin(slopeRad) * Math.cos(azimuthRad - aspectRad)))));

        if (hillshade < 0)
            hillshade = 0;
        else if (hillshade > 255)
            hillshade = 255;

        // get pixel color
        Color col = new Color(hillshade, hillshade, hillshade, 255);

        return new HSLColor(col);
    }

    public static HSLColor[][] shadeAllPixels(double[][] noise, int width, int height, int buffer)
    {
        double zFactor = 40.0f; // the higher the 'Z' factor, the more pronounced the hillshading

        return shadeAllPixels(noise, width, height, zFactor, buffer);
    }

    public static HSLColor[][] shadeAllPixels(double[][] noise, int width, int height, double zFactor, int buffer)
    {
        // The default Altitude of 45 degrees and Azimuth of 315 degrees will be used.
        double Altitude = 80.0f;
        double Azimuth = 315.0f;
        double cellSize = 1.0f;

        return shadeAllPixels(noise, width, height, zFactor, Altitude, Azimuth, cellSize, buffer);
    }

    public static HSLColor[][] shadeAllPixels(double[][] noise, int width, int height, double zFactor, double Altitude, double Azimuth, double cellSize, int buffer)
    {
        HSLColor[][] colors = new HSLColor[width][height];

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < width; y++)
            {
                HSLColor color = shadePixel(noise, width, height, x, y, zFactor, Altitude, Azimuth, cellSize, buffer);
                colors[x][y] = color;
            }
        }

        return colors;
    }
}
