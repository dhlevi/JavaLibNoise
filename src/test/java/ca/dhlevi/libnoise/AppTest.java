package ca.dhlevi.libnoise;

import ca.dhlevi.libnoise.DefaultModules;
import ca.dhlevi.libnoise.Erosion;
import ca.dhlevi.libnoise.Module;
import ca.dhlevi.libnoise.NoiseFactory;
import ca.dhlevi.libnoise.NoiseNormalizer;
import ca.dhlevi.libnoise.RegionGenerator;
import ca.dhlevi.libnoise.RiverGenerator;
import ca.dhlevi.libnoise.paint.Painter;
import ca.dhlevi.libnoise.spatial.Coordinate;
import ca.dhlevi.libnoise.spatial.Envelope;
import ca.dhlevi.libnoise.spatial.SpatialUtilities;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName
     *            name of the test case
     */
    public AppTest(String testName)
    {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     * 
     * @throws Exception
     */
    public void testApp() throws Exception
    {
        long processStartTime = System.currentTimeMillis();
        boolean reproject = false;
        // -2147483648 to 2147483648 (make a cap so we can ensure seed +/- functions still work.
        // maybe -2147483000 to 2147483000
        int seed = 9988565; 

        System.out.println("Starting generation. Seed is: " + seed + "...");

        int size = 1000;
        int width = size;
        int height = size / 2;
        int buffer = 6;
        double seaLevel = 0.3;

        double minY = -67;
        double maxY = -22;
        double minX = -10;
        double maxX = 80;
        
        if(reproject) // reprojection currently doesn't work on bbox elements and must be global, need to add that in!
        {
            minY = -90;
            maxY = 90;
            minX = -180;
            maxX = 180;
        }
        
        Envelope bbox = new Envelope(minX, minY, maxX, maxY);
        
        Module module = DefaultModules.getContinentNoise(seed);

        long startTime = System.currentTimeMillis();
        System.out.println("Generating initial noise...");
        double[][] noise = NoiseFactory.generateSpherical(module, width + buffer, height + (buffer / 2), minY, maxY, minX, maxX, true, 1);
        long endTime = System.currentTimeMillis();
        System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");
        
        if(reproject)
        {
            startTime = System.currentTimeMillis();
            System.out.println("Reprojecting to Mercator...");
            double[][] projectedNoise = new double[height][height];
            for(int x = 0; x < width; x++)
            {
                for(int y = 0; y < height; y++)
                {
                    Coordinate coord = SpatialUtilities.pixelsToLatLong(new Point(x, y), width, height, bbox);
                    Coordinate projectedCoord2 = SpatialUtilities.MercatorProjection(height, height, coord, bbox);
                    
                    if((int)projectedCoord2.getX() > 0 && (int)projectedCoord2.getX() < height && (int)projectedCoord2.getY() > 0 && (int)projectedCoord2.getY() < height)
                    {
                        projectedNoise[(int)projectedCoord2.getX()][height - 1 - (int)projectedCoord2.getY()] = noise[x][y];
                    }
                }
            }
            
            for(int x = 0; x < height; x++)
            {
                for(int y = 0; y < height; y++)
                {
                    if(y > 0)
                        if(projectedNoise[x][y] == 0.0) projectedNoise[x][y] = projectedNoise[x][y - 1];
                }
                
                for(int y = 0; y < height; y++)
                {
                    if(x > 0)
                        if(projectedNoise[x][y] == 0.0) projectedNoise[x][y] = projectedNoise[x - 1][y];
                }
            }
            
            noise = projectedNoise;
            width = height;
            size = height;
            
            endTime = System.currentTimeMillis();
            System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");
        }
        
        startTime = System.currentTimeMillis();
        System.out.println("Thermal Erosion...");
        Erosion.thermalErosion(noise, 0.125, 7);
        endTime = System.currentTimeMillis();
        System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");

        startTime = System.currentTimeMillis();
        System.out.println("Hydraulic Erosion...");
        Erosion.advancedHydraulicErosion(noise, 0.0001, 0.01, seaLevel, 7, true, 200);
        endTime = System.currentTimeMillis();
        System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");

        startTime = System.currentTimeMillis();
        System.out.println("Noise normalizing...");
        NoiseNormalizer.normalize(noise, seaLevel);
        endTime = System.currentTimeMillis();
        System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");

        startTime = System.currentTimeMillis();
        System.out.println("Basin detection...");
        int[][] basinData = NoiseNormalizer.detectBasins(noise, (int) Math.round(width * 0.5), seaLevel, false, false, seed);
        endTime = System.currentTimeMillis();
        System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");

        startTime = System.currentTimeMillis();
        System.out.println("Creating river paths...");
        int[][] riverData = RiverGenerator.createRiversAStar(noise, basinData, null, seaLevel, size / 100, false, bbox, 1, seed);
        riverData = RiverGenerator.createRiversAStar(noise, basinData, riverData, seaLevel, size / 20, true, bbox, 2, seed + 1);
        riverData = RiverGenerator.createRiversAStar(noise, basinData, riverData, seaLevel, size / 100, true, bbox, 3, seed + 2);
        endTime = System.currentTimeMillis();
        System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");

        startTime = System.currentTimeMillis();
        System.out.println("Generating regions...");
        int[][] regionData = RegionGenerator.generateRegions(noise, basinData, riverData, seaLevel, 60, 50, bbox, seed);
        endTime = System.currentTimeMillis();
        System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");

        startTime = System.currentTimeMillis();
        System.out.println("Generating biomes...");
        int[][] biomeData = BiomeGenerator.generateBiomes(noise, riverData, basinData, bbox, seaLevel, -10.0, 0.0, seed);
        endTime = System.currentTimeMillis();
        System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");

        startTime = System.currentTimeMillis();
        System.out.println("Trimming buffers...");

        double[][] noiseTrimmed = new double[noise.length - buffer][noise[0].length - (buffer / 2)];
        int[][] basinTrimmed = new int[noise.length - buffer][noise[0].length - (buffer / 2)];
        int[][] riverDataTrimmed = new int[noise.length - buffer][noise[0].length - (buffer / 2)];
        int[][] regionDataTrimmed = new int[noise.length - buffer][noise[0].length - (buffer / 2)];
        int[][] biomeDataTrimmed = new int[noise.length - buffer][noise[0].length - (buffer / 2)];

        // trim out the buffer
        for (int x = (buffer / 2); x < width + (buffer / 2); x++)
        {
            for (int y = (buffer / 4); y < height + (buffer / 4); y++)
            {
                noiseTrimmed[x - (buffer / 2)][y - (buffer / 4)] = noise[x][y];
                basinTrimmed[x - (buffer / 2)][y - (buffer / 4)] = basinData[x][y];
                riverDataTrimmed[x - (buffer / 2)][y - (buffer / 4)] = riverData[x][y];
                regionDataTrimmed[x - (buffer / 2)][y - (buffer / 4)] = regionData[x][y];
                biomeDataTrimmed[x - (buffer / 2)][y - (buffer / 4)] = biomeData[x][y];
            }
        }
        
        noise = noiseTrimmed;
        basinData = basinTrimmed;
        riverData = riverDataTrimmed;
        regionData = regionDataTrimmed;
        biomeData = biomeDataTrimmed;

        noiseTrimmed = null;
        riverDataTrimmed = null;
        regionDataTrimmed = null;
        basinTrimmed = null;
        biomeDataTrimmed = null;

        endTime = System.currentTimeMillis();
        System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");

        startTime = System.currentTimeMillis();
        System.out.println("Painting images...");
        // paint the heightmap
        assertTrue(Painter.paintHeightMap(noise, "c:/test/"));
        assertTrue(Painter.paintRegionMap(regionData, "c:/test/", seed));
        assertTrue(Painter.paintTerrainMap(noise, riverData, seaLevel, "c:/test/", true, true));
        assertTrue(Painter.paintBiomeMap(noise, riverData, biomeData, seaLevel, "c:/test/", true, false));

        Painter.paintTempuratureBands(noise, BiomeGenerator.MAX_TEMP, "c:/test/", new Envelope(minX, minY, maxX, maxY));
        
        endTime = System.currentTimeMillis();
        System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");
        
        long processEndTime = System.currentTimeMillis();
        System.out.println("Generation complete: " + ((processEndTime - processStartTime) / 1000) + " seconds");
    }
}