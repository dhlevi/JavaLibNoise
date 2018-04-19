package ca.dhlevi.libnoise;

import ca.dhlevi.libnoise.DefaultModules;
import ca.dhlevi.libnoise.Erosion;
import ca.dhlevi.libnoise.Module;
import ca.dhlevi.libnoise.NoiseFactory;
import ca.dhlevi.libnoise.NoiseNormalizer;
import ca.dhlevi.libnoise.RegionGenerator;
import ca.dhlevi.libnoise.RiverGenerator;
import ca.dhlevi.libnoise.paint.Painter;
import ca.dhlevi.libnoise.spatial.Envelope;
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

        int seed = 88978728;

        System.out.println("Starting generation. Seed is: " + seed + "...");

        int size = 2000;
        int width = size;
        int height = size / 2;
        int buffer = 4;
        double seaLevel = 0.25;

        double minY = -90;
        double maxY = 90;
        double minX = -180;
        double maxX = 180;

        Module module = DefaultModules.getContinentNoise(seed);

        long startTime = System.currentTimeMillis();
        System.out.println("Generating initial noise...");
        double[][] noise = NoiseFactory.generateSpherical(module, width + buffer, height + (buffer / 2), minY, maxY, minX, maxX, true, 1);

        long endTime = System.currentTimeMillis();
        System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");

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
        NoiseNormalizer.Normalize(noise, seaLevel);
        endTime = System.currentTimeMillis();
        System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");

        startTime = System.currentTimeMillis();
        System.out.println("Basin detection...");
        int[][] basinData = NoiseNormalizer.DetectBasins(noise, (int) Math.round(width * 0.5), seaLevel, false, false, seed);
        endTime = System.currentTimeMillis();
        System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");

        startTime = System.currentTimeMillis();
        System.out.println("Creating river paths...");
        int[][] riverData = RiverGenerator.createRiversAStar(noise, seaLevel, size, seed);
        endTime = System.currentTimeMillis();
        System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");

        startTime = System.currentTimeMillis();
        System.out.println("Generating regions...");
        int[][] regionData = RegionGenerator.generateRegions(noise, basinData, riverData, seaLevel, 40, 40, seed);
        endTime = System.currentTimeMillis();
        System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");

        startTime = System.currentTimeMillis();
        System.out.println("Generating biomes...");
        int[][] biomeData = BiomeGenerator.generateBiomes(noise, riverData, basinData, new Envelope(minX, minY, maxX, maxY), seaLevel, 0.0, 0.0, seed);
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