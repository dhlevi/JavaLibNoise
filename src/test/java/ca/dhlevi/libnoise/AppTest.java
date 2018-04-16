package ca.dhlevi.libnoise;

import ca.dhlevi.libnoise.DefaultModules;
import ca.dhlevi.libnoise.Erosion;
import ca.dhlevi.libnoise.Module;
import ca.dhlevi.libnoise.NoiseFactory;
import ca.dhlevi.libnoise.NoiseNormalizer;
import ca.dhlevi.libnoise.RegionGenerator;
import ca.dhlevi.libnoise.RiverGenerator;
import ca.dhlevi.libnoise.paint.Painter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     * @throws Exception 
     */
    public void testApp() throws Exception
    {
    	int seed = 432; //new Date().getDay() + new Date().getMonth() + new Date().getSeconds();

    	int size = 1000;
    	
    	int width = size;
    	int height = size / 2;
    	int buffer = 10;
    	double seaLevel = 0.25;

    	Module module = DefaultModules.getContinentNoise(seed); //getDetailedNoise(seed); //getSimpleNoise(seed); //getStandardNoise(seed);

    	long startTime = System.currentTimeMillis();
    	System.out.println("Generating initial noise...");
    	// whole world
    	//double[][] noise = NoiseFactory.generateSpherical(module, width + buffer, height + (buffer / 2), -90, 90, -180, 180, true, 1);
    	// just the centre 50%
    	//double[][] noise = NoiseFactory.generateSpherical(module, width + buffer, height + (buffer / 2), -45, 45, -90, 90, true, 1);
    	// and even more zoomed in!
    	double[][] noise = NoiseFactory.generateSpherical(module, width + buffer, height + (buffer / 2), -22.5, 22.5, -45, 45, true, 1);
    	
    	long endTime = System.currentTimeMillis();
    	System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");
    	
    	startTime = System.currentTimeMillis();
    	System.out.println("Thermal Erosion...");
    	Erosion.thermalErosion(noise, 0.125, 50);
    	endTime = System.currentTimeMillis();
    	System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");
    	
    	startTime = System.currentTimeMillis();
    	System.out.println("Hydraulic Erosion...");
    	Erosion.advancedHydraulicErosion(noise, 0.0001, 0.01, seaLevel, 20, true, 200);
    	endTime = System.currentTimeMillis();
    	System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");
    	
    	startTime = System.currentTimeMillis();
    	System.out.println("Noise normalizing...");
    	NoiseNormalizer.Normalize(noise, seaLevel);
    	endTime = System.currentTimeMillis();
    	System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");
    	
    	startTime = System.currentTimeMillis();
    	System.out.println("Basin detection...");
    	int[][] basinData = NoiseNormalizer.DetectBasins(noise, (int)Math.round(width * 0.5), seaLevel, true, true, seed);
    	endTime = System.currentTimeMillis();
    	System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");
    	
    	startTime = System.currentTimeMillis();
    	System.out.println("Creating river paths...");
    	int[][] riverData = RiverGenerator.createRiversAStar(noise, seaLevel, size, seed); //.createRiversFlowMethod(noise, seaLevel, 25, size, seed);
    	endTime = System.currentTimeMillis();
    	System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");
    	
    	startTime = System.currentTimeMillis();
    	System.out.println("Generating regions...");
    	int[][] regionData = RegionGenerator.generateRegions(noise, riverData, seaLevel, seed);
    	endTime = System.currentTimeMillis();
    	System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");
    	
    	startTime = System.currentTimeMillis();
    	System.out.println("Trimming buffers...");
    	
    	double[][] noiseTrimmed = new double[noise.length - buffer][noise[0].length - (buffer / 2)];
    	int[][] basinTrimmed = new int[noise.length - buffer][noise[0].length - (buffer / 2)];
    	int[][] riverDataTrimmed = new int[noise.length - buffer][noise[0].length - (buffer / 2)];
    	int[][] regionDataTrimmed = new int[noise.length - buffer][noise[0].length - (buffer / 2)];
    	
    	//trim out the buffer
    	for(int x = (buffer / 2); x < width + (buffer / 2); x++)
    	{
    		for(int y = (buffer / 4); y < height + (buffer / 4); y++)
        	{
    			noiseTrimmed[x - (buffer / 2)][y - (buffer / 4)] = noise[x][y];
    			basinTrimmed[x - (buffer / 2)][y - (buffer / 4)] = basinData[x][y];
    			riverDataTrimmed[x - (buffer / 2)][y - (buffer / 4)] = riverData[x][y];
    			regionDataTrimmed[x - (buffer / 2)][y - (buffer / 4)] = regionData[x][y];
        	}
    	}
    	noise = noiseTrimmed;
    	basinData = basinTrimmed;
    	riverData = riverDataTrimmed;
    	regionData = regionDataTrimmed;
    	
    	noiseTrimmed = null;
    	riverDataTrimmed = null;
    	regionDataTrimmed = null;
    	basinTrimmed = null;

    	endTime = System.currentTimeMillis();
    	System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");
    	
    	startTime = System.currentTimeMillis();
    	System.out.println("Painting images...");
    	// paint the heightmap
    	assertTrue(Painter.paintHeightMap(noise, "c:/test/"));
    	assertTrue(Painter.paintRegionMap(regionData, "c:/test/", seed));
    	assertTrue(Painter.paintTerrainMap(noise, riverData, seaLevel, "c:/test/", true, true));
    	
    	endTime = System.currentTimeMillis();
    	System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");
    }
}
