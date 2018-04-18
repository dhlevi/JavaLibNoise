package ca.dhlevi.libnoise;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

import javax.imageio.ImageIO;

import ca.dhlevi.libnoise.paint.Hillshader;
import ca.dhlevi.libnoise.paint.PainterUtilities;

public class BiomeGenerator
{
    // initial biome test
    // this "algorithm" is very weak at the moment, and will be replaced by
    // something else as I get around to it
    public static int[][] generateBiomes(double[][] data, int[][] rivers, int[][] basins, double seaLevel, int seed)
    {
        int width = data.length;
        int height = data[0].length;

        int[][] biomes = new int[width][height];

        /*
         * 1 - ice sheet/polar desert 
         * 2 - tundra 
         * 3 - taiga 
         * 4 - temperate steppe
         * 5 - temperate forest 
         * 6 - subtropical rainforest 
         * 7 - desert 
         * 8 - savannah 
         * 9 - jungle
         */

        // determine moisture levels for each pixel. We can derive temp and wind
        // direction from lon/lat, air pressure from height

        // wind belts
        // -90 to -55 = subpolar (sw)
        // -55 to -50 = subpolar low
        // -50 to -25 = westerlies (ne)
        // -25 to -20 = subtropic high
        // -20 to -5 = easterlies (sw)
        // -5 to 5 = convergence zone (doldrums)
        // 5 to 20 = easterlies (nw)
        // 20 to 25 = subtropic high
        // 30 to 50 = westerlies (se)
        // 50 to 55 = subpolar low
        // 55 to 90 = subpolar (nw)

        // set initial moisture levels
        double[][] moisture = new double[width][height];
        double defaultMoistureGain = 0.001;
        double defaultMoistureLoss = 1.0;

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                double gain = data[x][y];
                if (rivers[x][y] == 1)
                    gain += defaultMoistureGain;
                else if (data[x][y] <= seaLevel)
                    gain += defaultMoistureGain;
                else if (data[x][y] > seaLevel)
                {
                    Point p = getClosestWaterPoint(data, rivers, new Point(x, y), seaLevel, (width / 500));
                    if(p != null) 
                    {
                        double distance = Math.hypot(p.getX() - x, p.getY() - y);
                        double proximityToWaterGain = 1.0 - (distance / 10);
                        
                        if(proximityToWaterGain < 0) proximityToWaterGain = 0;
                        
                        moisture[x][y] += proximityToWaterGain;
                    }
                }

                moisture[x][y] = gain;
            }
        }

        boolean moistureComplete = false;
        int iterations = 0;
        int maxIterations = 5;

        while (!moistureComplete)
        {
            // easterlies
            for (int y = height - 1; y > 0; y--)
            {
                double availableMoisture = 0.0;
                for (int x = width - 1; x > 0; x--)
                {
                    double lat = Utilities.pixelsToLatLong(new Point(x, y), width, height).getY();
                    double diff = availableMoisture * (1.0 - data[x][y]);
                    availableMoisture += defaultMoistureGain * 10;
                    
                    if (data[x][y] > seaLevel)
                        moisture[x][y] -= defaultMoistureGain * 5;
                    
                    if (((lat <= -50) || (lat > -20 && lat <= 0)) && data[x][y] > seaLevel) // sw
                    {
                        availableMoisture -= diff;

                        if (availableMoisture > 0)
                        {
                            moisture[x == 0 ? width - 1 : x - 1][y == height - 1 ? height - 1 : y + 1] += diff / 2;
                            moisture[x == 0 ? width - 1 : x - 1][y] += diff / 2;
                            moisture[x][y == height - 1 ? height - 1 : y + 1] += diff / 2;
                        }
                    }
                    else if (((lat > 0 && lat <= 20) || (lat > 50)) && data[x][y] > seaLevel) // nw
                    {
                        availableMoisture -= diff;

                        if (availableMoisture > 0)
                        {
                            moisture[x == 0 ? width - 1 : x - 1][y == 0 ? 0 : y - 1] += diff / 2;
                            moisture[x == 0 ? width - 1 : x - 1][y] += diff / 2;
                            moisture[x][y == 0 ? 0 : y - 1] += diff / 2;
                        }
                    }
                }
            }
            // westerlies
            for (int x = 0; x < width; x++)
            {
                double availableMoisture = 0.0;
                for (int y = 0; y < height; y++)
                {
                    double lat = Utilities.pixelsToLatLong(new Point(x, y), width, height).getY();
                    double diff = availableMoisture * (1.0 - data[x][y]);
                    availableMoisture += defaultMoistureGain * 10;
                    
                    if (data[x][y] > seaLevel)
                        moisture[x][y] -= defaultMoistureGain * 5;

                    if ((lat > -50 && lat <= -20) && data[x][y] > seaLevel) // ne
                    {
                        availableMoisture -= diff;

                        if (availableMoisture > 0)
                        {
                            moisture[x == width - 1 ? width - 1 : x + 1][y == 0 ? 0 : y - 1] += diff / 2;
                            moisture[x == width - 1 ? width - 1 : x + 1][y] += diff / 2;
                            moisture[x][y == 0 ? 0 : y - 1] += diff / 2;
                        }
                    } 
                    else if ((lat > 20 && lat <= 50) && data[x][y] > seaLevel) // se
                    {
                        availableMoisture -= diff;

                        if (availableMoisture > 0)
                        {
                            moisture[x == width - 1 ? width - 1 : x + 1][y == height - 1 ? height - 1 : y + 1] += diff / 2;
                            moisture[x == width - 1 ? width - 1 : x + 1][y] += diff / 2;
                            moisture[x][y == height - 1 ? height - 1 : y + 1] += diff / 2;
                        }
                    }
                }
            }

            iterations++;
            moistureComplete = iterations >= maxIterations;
        }

        paintMoisture(moisture, width, height);

        // set biome based on temp + moisture
        double maxTemp = 35;
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                double lat = Utilities.pixelsToLatLong(new Point(x, y), width, height).getY();
                if (lat < 0)
                    lat = lat * -1;
                double temp = (maxTemp * ((90 - lat) / 90)) * (1.0 - (data[x][y] / 2.0));
                double rainfall = moisture[x][y] + rivers[x][y];

                if (temp < 5)
                {
                    if (rainfall < 0.3)
                        biomes[x][y] = 1;
                    else if (rainfall >= 0.3 && rainfall < 0.6)
                        biomes[x][y] = 2;
                    else
                        biomes[x][y] = 3;
                } else if (temp >= 5 && temp < 20)
                {
                    if (rainfall < 0.3)
                        biomes[x][y] = 4;
                    else if (rainfall >= 0.3 && rainfall < 0.6)
                        biomes[x][y] = 5;
                    else
                        biomes[x][y] = 6;
                } else if (temp >= 20)
                {
                    if (rainfall < 0.3)
                        biomes[x][y] = 7;
                    else if (rainfall >= 0.3 && rainfall < 0.6)
                        biomes[x][y] = 8;
                    else
                        biomes[x][y] = 9;
                }
            }
        }

        return biomes;
    }

    public static int[][] generateBiomesByRegion(double[][] data, int[][] rivers, int[][] basins, int[][] regions, double seaLevel, int seed)
    {
        int width = data.length;
        int height = data[0].length;
        int[][] biomes = generateBiomes(data, rivers, basins, seaLevel, seed);

        HashMap<Integer, Integer> regionalBiomes = new HashMap<Integer, Integer>();

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                int biome = biomes[x][y];
                int region = regions[x][y];

                if (!regionalBiomes.containsKey(region))
                {
                    regionalBiomes.put(region, biome);
                } 
                else
                {
                    biome = regionalBiomes.get(region);
                }

                biomes[x][y] = biome;
            }
        }

        return biomes;
    }

    public static void paintMoisture(double[][] moisture, int width, int height)
    {
        BufferedImage mapImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) mapImage.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                double noiseValue = moisture[x][y];
                if (noiseValue > 1)
                    noiseValue = 1;
                if (noiseValue < 0)
                    noiseValue = 0;

                int rgb = (int) Math.round(255 * noiseValue);

                Color c = new Color(0, 0, rgb, 255);

                mapImage.setRGB(x, y, c.getRGB());
            }
        }

        mapImage.flush();

        try
        {
            File outputfile = new File("c:/test/moisture.png");
            outputfile.createNewFile();
            boolean success = ImageIO.write(mapImage, "png", outputfile);
        } catch (Exception e)
        {
        }
    }
    
    private static Point getClosestWaterPoint(double[][] data, int[][] rivers, Point point, double seaLevel, int tolerance)
    {
        Point destination = null;
        boolean foundWater = false;

        int width = data.length;
        int height = data[0].length;

        int loops = 0;
        while (!foundWater)
        {
            if (loops > tolerance)
                break; // we've been searching for a while and never found a destination?

            for (int y = point.getY() - loops; y < point.getY() + loops && y < height - 1; y++)
            {
                for (int x = point.getX() - loops; x < point.getX() + loops && x < width - 1; x++)
                {
                    // make sure we're within array bounds
                    if (x < 0)
                        x = 0;
                    if (x >= width - 1)
                        x = width - 1;
                    if (y < 0)
                        y = 0;
                    if (y >= height - 1)
                        y = height - 1;

                    if (data[x][y] <= seaLevel || rivers[x][y] > 0)
                    {
                        destination = new Point(x, y);
                        foundWater = true;
                        break;
                    }

                    if (y > point.getY() - loops && y < point.getY() + loops - 1)
                    {
                        x = point.getX() + loops;
                    }
                }
            }

            loops++;
        }

        return destination;
    }
}
