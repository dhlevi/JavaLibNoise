package ca.dhlevi.libnoise;

import java.util.HashMap;

public class BiomeGenerator 
{
	// initial biome test
	// this "algorithm" is very weak at the moment, and will be replaced by something else as I get around to it
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
        
        // determine moisture levels for each pixel. We can derive temp and wind direction from lon/lat, air pressure from height

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
        double defaultMoistureGain = 0.1;
        double defaultMoistureLoss = 0.01;
        for(int x = 0; x < width; x++)
    	{
    		for(int y = 0; y < height; y++)
        	{
    			double gain = data[x][y];
    			if(rivers[x][y] == 1) gain += defaultMoistureGain;
    			if(data[x][y] <= seaLevel) gain += defaultMoistureGain;
    			
    			moisture[x][y] = gain;
        	}
    	}

        boolean moistureComplete = false;
        int iterations = 0;
        int maxIterations = 5;
        
        while(!moistureComplete)
        {
	        for(int x = 0; x < width; x++)
	    	{
	    		for(int y = 0; y < height; y++)
	        	{
	    			double lat = Utilities.pixelsToLatLong(new Point(x, y), width, height).getY();
	    			double diff = (defaultMoistureLoss * (1.0 - data[x][y]));
	    			moisture[x][y] -= diff;
	    			
	    			if((lat <= -50) || (lat > -20 && lat <= 0)) //sw
	    			{
	    				moisture[x == 0 ? width - 1 : x - 1][y == height - 1 ? height - 1 : y + 1] += diff;
	    			}
	    			else if(lat > -50 && lat <= -20) //ne
	    			{
	    				moisture[x == width - 1 ? width - 1 : x + 1][y == 0 ? 0 : y - 1] += diff;
	    			}
	    			else if((lat > 0 && lat <= 20) || (lat > 50)) //nw
	    			{
	    				moisture[x == 0 ? width - 1 : x - 1][y == 0 ? 0 : y - 1] += diff;
	    			}
	    			else if(lat > 20 && lat <= 50) //se
	    			{
	    				moisture[x == width - 1 ? width - 1 : x + 1][y == height - 1 ? height - 1 : y + 1] += diff;
	    			}
	    			
	    			double gain = 0.0;
	    			if(data[x][y] <= seaLevel) gain += defaultMoistureGain;
	    			
	    			 moisture[x][y] += gain;
	        	}
	    	}
	        
	        iterations++;
	        moistureComplete = iterations >= maxIterations;
        }
        
        // set biome based on temp + moisture
        double maxTemp = 35;
        for(int x = 0; x < width; x++)
    	{
    		for(int y = 0; y < height; y++)
        	{
    			double lat = Utilities.pixelsToLatLong(new Point(x, y), width, height).getY();
    			if(lat < 0) lat = lat * -1;
    			double temp = (maxTemp * ((90 - lat) / 90)) * (1.0 - (data[x][y] / 2.0));
    			double rainfall = moisture[x][y] + rivers[x][y];
    			
    			if(temp < 5)
    			{
    				if(rainfall < 0.3) biomes[x][y] = 1;
    				else if(rainfall >= 0.3 && rainfall < 0.6) biomes[x][y] = 2;
    				else biomes[x][y] = 3;
    			}
    			else if(temp >= 5 && temp < 20)
    			{
    				if(rainfall < 0.3) biomes[x][y] = 4;
    				else if(rainfall >= 0.3 && rainfall < 0.6) biomes[x][y] = 5;
    				else biomes[x][y] = 6;
    			}
    			else if(temp >= 20)
    			{
    				if(rainfall < 0.3) biomes[x][y] = 7;
    				else if(rainfall >= 0.3 && rainfall < 0.6) biomes[x][y] = 8;
    				else biomes[x][y] = 9;
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
        
        for(int x = 0; x < width; x++)
    	{
    		for(int y = 0; y < height; y++)
        	{
    			int biome = biomes[x][y];
    			int region = regions[x][y];
    			
    			if(!regionalBiomes.containsKey(regionalBiomes))
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
}
