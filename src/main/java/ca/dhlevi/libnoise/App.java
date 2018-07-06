package ca.dhlevi.libnoise;

import ca.dhlevi.libnoise.paint.Painter;
import ca.dhlevi.libnoise.spatial.Envelope;

public class App
{
    // args: Seed, size, sea level, minX, maxX, minY, maxY, path
    public static void main(String[] args) throws Exception
    {
        int seed = Integer.parseInt(args[0]);
        int size = Integer.parseInt(args[1]);
        String path = args[7];

        int width = size;
        int height = size / 2;
        int buffer = 5;
        double seaLevel = Double.parseDouble(args[2]);

        Module module = DefaultModules.getContinentNoise(seed);

        Envelope bbox = new Envelope(Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]));
        
        long startTime = System.currentTimeMillis();
        System.out.println("Generating initial noise...");
        double[][] noise = NoiseFactory.generateSpherical(module, width + buffer, height + (buffer / 2), Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]), true, 1);

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
        NoiseNormalizer.normalize(noise, seaLevel);
        endTime = System.currentTimeMillis();
        System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");

        startTime = System.currentTimeMillis();
        System.out.println("Basin detection...");
        int[][] basinData = NoiseNormalizer.detectBasins(noise, (int) Math.round(width * 0.5), seaLevel, true, true, seed);
        endTime = System.currentTimeMillis();
        System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");

        startTime = System.currentTimeMillis();
        System.out.println("Creating river paths...");
        int[][] riverData = RiverGenerator.createRiversAStar(noise, basinData, null, seaLevel, size, false, bbox, 1, seed);
        endTime = System.currentTimeMillis();
        System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");

        startTime = System.currentTimeMillis();
        System.out.println("Generating regions...");
        int[][] regionData = RegionGenerator.generateRegions(noise, basinData, riverData, seaLevel, 30, 30, bbox, seed);
        endTime = System.currentTimeMillis();
        System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");

        startTime = System.currentTimeMillis();
        System.out.println("Trimming buffers...");

        double[][] noiseTrimmed = new double[noise.length - buffer][noise[0].length - (buffer / 2)];
        int[][] basinTrimmed = new int[noise.length - buffer][noise[0].length - (buffer / 2)];
        int[][] riverDataTrimmed = new int[noise.length - buffer][noise[0].length - (buffer / 2)];
        int[][] regionDataTrimmed = new int[noise.length - buffer][noise[0].length - (buffer / 2)];

        // trim out the buffer
        for (int x = (buffer / 2); x < width + (buffer / 2); x++)
        {
            for (int y = (buffer / 4); y < height + (buffer / 4); y++)
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
        Painter.paintHeightMap(noise, path);
        Painter.paintRegionMap(regionData, path, seed);
        Painter.paintTerrainMap(noise, riverData, seaLevel, path, true, true);

        endTime = System.currentTimeMillis();
        System.out.println("Complete: " + ((endTime - startTime) / 1000) + " seconds");
    }
}
