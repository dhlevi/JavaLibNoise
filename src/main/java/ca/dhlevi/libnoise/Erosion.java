package ca.dhlevi.libnoise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Erosion 
{
	 // Thermal erosion "collapses" cliffs and evens out heights based
    // from the difference in height between a point and its neighbours
    // This difference is called the "Talus Angle". The lower the value of
    // the talus angle, the more erosion will occur. Higher values will 
    // collapse extreme cliffs, but keep the terrain more jagged
    public static void thermalErosion(double[][] data, double talusAngle, int iterations)
    {
        int width = data.length;
        int height = data[0].length;

        for (int i = 0; i < iterations; i++)
        {
            for(int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    // this pixel height value
                    double heightValue = data[x][y];

                    // neighbouring height values
                    // if we're on an e/w edge, loop around the map. North and south do not loop.

                    double nw = y == 0 ? -1 : x == 0 ? data[width - 1][y - 1] : data[x - 1][y - 1];
                    double n = y == 0 ? -1 : data[x][y - 1];
                    double ne = y == 0 ? -1 : x == width - 1 ? data[0][y - 1] : data[x + 1][y - 1];
                    double e = x == width - 1 ? data[0][y] : data[x + 1][y];
                    double se = y == height - 1 ? -1 : x == width - 1 ? data[0][y + 1] : data[x + 1][y + 1];
                    double s = y == height - 1 ? -1 : data[x][y + 1];
                    double sw = y == height - 1 ? -1 : x == 0 ? data[width - 1][y + 1] : data[x - 1][y + 1];
                    double w = x == 0 ? data[width - 1][y] : data[x - 1][y];

                    List<Pair<Integer, Double>> flows = new ArrayList<Pair<Integer, Double>>();

                    flows.add(new Pair<Integer, Double>(1, nw));
                    flows.add(new Pair<Integer, Double>(2, n));
                    flows.add(new Pair<Integer, Double>(3, ne));
                    flows.add(new Pair<Integer, Double>(4, w));
                    flows.add(new Pair<Integer, Double>(5, e));
                    flows.add(new Pair<Integer, Double>(6, sw));
                    flows.add(new Pair<Integer, Double>(7, s));
                    flows.add(new Pair<Integer, Double>(8, se));

                    // order slopes by highest to lowest

                    flows.sort(Comparator.comparing(Pair::getValue)); // ordered by value, lowest
                    Collections.reverse(flows); // flip to highest
                    
                    for(Pair<Integer, Double> slope : flows)
                    {
                        if (slope.getValue() != -1 && heightValue > slope.getValue())
                        {
                            double difference = heightValue - slope.getValue();

                            if (difference >= talusAngle)
                            {
                                heightValue = heightValue - (difference / 2);
                                // apply the difference that moved from the height to the correct neighbour
                                if (slope.getKey() == 1) data[x > 0 ? x - 1 : width - 1][y - 1] += difference / 2;
                                else if (slope.getKey() == 2) data[x][y - 1] += difference / 2;
                                else if (slope.getKey() == 3) data[x < width - 1 ? x + 1 : 0][y - 1] += difference / 2;
                                else if (slope.getKey() == 4) data[x > 0 ? x - 1 : width - 1][y] += difference / 2;
                                else if (slope.getKey() == 5) data[x < width - 1 ? x + 1 : 0][y] += difference / 2;
                                else if (slope.getKey() == 6) data[x > 0 ? x - 1 : width - 1][y + 1] += difference / 2;
                                else if (slope.getKey() == 7) data[x][y + 1] += difference / 2;
                                else if (slope.getKey() == 8) data[x < width - 1 ? x + 1 : 0][y + 1] += difference / 2;
                            }
                        }
                    }

                    // we're done, update the main pixel height data
                    data[x][y] = heightValue;
                }
            }
        }
    }

    
    // updated hydraulic erosion algorithm
    // note that this will not "draw" rivers, only establish where water has flowed and settled
    // and where sediment has been redeposited. Only height will be adjusted
    // the array of pooled water is returned in case it provides some use for lake and river generation in the future.
    public static double[][] advancedHydraulicErosion(double[][] data, double erosionAmount, double waterFloodAmount, double seaLevel, int iterations, boolean resetFloodAfterEachIteration, int maxPathLength)
    {
    	int width = data.length;
        int height = data[0].length;

        // This is the map of pooled water, direction of flow doesn't really matter.
        // we can probably return this for a "Lake" overview
        double[][] waterBuildup = new double[width][height];

        for (int i = 0; i < iterations; i++)
        {
            if(resetFloodAfterEachIteration)
            {
                waterBuildup = new double[width][height];
            }
            else if(i > 0)
            {
                // evaporation step
                for (int y = 0; y < height; y++)
                {
                    for (int x = 0; x < width; x++)
                    {
                    	double val = waterBuildup[x][y];
                        if (val > 0) waterBuildup[x][y] = val / 2.0;
                        else if (val < 0) waterBuildup[x][y] = 0.0;
                    }
                }

                // water distributions step
                for (int y = 0; y < height; y++)
                {
                    for (int x = 0; x < width; x++)
                    {
                        double val = waterBuildup[x][y];

                        //get neighbours, and spread the water around to balance them out as much as possible
                        
                        double n = y == 0 ? 99.0 : data[x][y - 1] + waterBuildup[x][y - 1];
                        double e = x == width - 1 ? 99.0 : data[x + 1][y] + waterBuildup[x + 1][y];
                        double s = y == height - 1 ? 99.0 : data[x][y + 1] + waterBuildup[x][y + 1];
                        double w = x == 0 ? 99.0 : data[x - 1][y] + waterBuildup[x - 1][y];

                        List<Pair<Integer, Double>> flows = new ArrayList<Pair<Integer, Double>>();

                        // add all neightbours to a list (marked 1-8 so we can identify them easy later)
                        flows.add(new Pair<Integer, Double>(1, n));
                        flows.add(new Pair<Integer, Double>(2, e));
                        flows.add(new Pair<Integer, Double>(3, s));
                        flows.add(new Pair<Integer, Double>(4, w));

                        // order these neighbour slopes by lowest to highest. We mainly care about the lowest
                        // also, strip out the invalid points (val 99)
                        flows.sort(Comparator.comparing(Pair::getValue));
                        List<Pair<Integer, Double>> finalFlows = new ArrayList<Pair<Integer, Double>>();
                        
                        for(Pair<Integer, Double> pair : flows)
                        {
                        	if(pair.getValue() != 99.0 && pair.getValue() < (val + data[x][y]))
                        	{
                        		finalFlows.add(pair);
                        	}
                        }

                        flows = finalFlows;
                        
                        if(flows.size() > 0)
                        {
                            double diff = (val + data[x][y]) - flows.get(0).getValue();
                            double thisWater = waterBuildup[x][y] - (diff / 2.0f);

                            waterBuildup[x][y] = thisWater > 0 ? thisWater : 0.0;

                            if(flows.get(0).getKey() == 1) waterBuildup[x][y - 1] = waterBuildup[x][y - 1] + (diff / 2.0);
                            else if(flows.get(0).getKey() == 2) waterBuildup[x + 1][y] = waterBuildup[x + 1][y] + (diff / 2.0);
                            else if(flows.get(0).getKey() == 3) waterBuildup[x][y + 1] = waterBuildup[x][y + 1] + (diff / 2.0);
                            else if(flows.get(0).getKey() == 4) waterBuildup[x - 1][y] = waterBuildup[x - 1][y] + (diff / 2.0);
                        }
                    }
                }
            }

            // cycle through each point in the heightmap
            // if the point is on land (above sea level)
            // then we'll process it
            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    boolean processing = true;

                    int currentX = x;
                    int currentY = y;
                    double dataHeight = data[x][y] + waterBuildup[x][y];
                    double sediment = 0.0;

                    int loopCount = 0;

                    while (processing)
                    {
                        loopCount++;

                        if (dataHeight > seaLevel)
                        {
                            // point is valid, so some water "drops" or "flows" at this spot
                            // this water will now move down-hill, until it settles.
                            // as it moves downhill, it will take some sediment with it

                            // get all neighbouring heights. Any points off the map will be flagged as 99
                            // also, make sure to add any pooled water that's built up
                        	double nw = currentY == 0 ? 99.0f : currentX == 0 ? data[width - 1][currentY - 1] + waterBuildup[width - 1][currentY - 1] : data[currentX - 1][currentY - 1] + waterBuildup[currentX - 1][currentY - 1];
                            double n = currentY == 0 ? 99.0f : data[currentX][currentY - 1] + waterBuildup[currentX][currentY - 1];
                            double ne = currentY == 0 ? 99.0f : currentX == width - 1 ? data[0][currentY - 1] + waterBuildup[0][currentY - 1] : data[currentX + 1][currentY - 1] + waterBuildup[currentX + 1][currentY - 1];
                            double e = currentX == width - 1 ? 99.0f : data[currentX + 1][currentY] + waterBuildup[currentX + 1][currentY];
                            double se = currentY == height - 1 ? 99.0f : currentX == width - 1 ? data[0][currentY + 1] + waterBuildup[0][currentY + 1] : data[currentX + 1][currentY + 1] + waterBuildup[currentX + 1][currentY + 1];
                            double s = currentY == height - 1 ? 99.0f : data[currentX][currentY + 1] + waterBuildup[currentX][currentY + 1];
                            double sw = currentY == height - 1 ? 99.0f : currentX == 0 ? data[width - 1][currentY + 1] + waterBuildup[width - 1][currentY + 1] : data[currentX - 1][currentY + 1] + waterBuildup[currentX - 1][currentY + 1];
                            double w = currentX == 0 ? 99.0f : data[currentX - 1][currentY] + waterBuildup[currentX - 1][currentY];

                            List<Pair<Integer, Double>> flows = new ArrayList<Pair<Integer, Double>>();

                            // add all neightbours to a list (marked 1-8 so we can identify them easy later)
                            flows.add(new Pair<Integer, Double>(1, nw));
                            flows.add(new Pair<Integer, Double>(2, n));
                            flows.add(new Pair<Integer, Double>(3, ne));
                            flows.add(new Pair<Integer, Double>(4, w));
                            flows.add(new Pair<Integer, Double>(5, e));
                            flows.add(new Pair<Integer, Double>(6, sw));
                            flows.add(new Pair<Integer, Double>(7, s));
                            flows.add(new Pair<Integer, Double>(8, se));

                            // order these neighbour slopes by lowest to highest. We mainly care about the lowest
                            // also, strip out the invalid points (val 99)
                            flows.sort(Comparator.comparing(Pair::getValue));
                            List<Pair<Integer, Double>> finalFlows = new ArrayList<Pair<Integer, Double>>();
                            
                            for(Pair<Integer, Double> pair : flows)
                            {
                            	if(pair.getValue() != 99.0)
                            	{
                            		finalFlows.add(pair);
                            	}
                            }
                            
                            flows = finalFlows;

                            // it should never occur that we are in a place with no neighbours, but still... error checking is good
                            // so make sure we at least have a value here
                            if (flows.size() > 0)
                            {
                                // get the lowest neighbouring value
                            	Pair<Integer, Double> lowestNeighbour = flows.get(0);

                                // can we flow? if so, pick up some sediment and lets move to the next location
                                // wait, we can't? Then we drop sediment and pool the water.

                                // we can flow if the nieghbour height is less than our hieght, plus the height of pooled water
                                if (lowestNeighbour.getValue() <= dataHeight)
                                {
                                    // lower the current height (sediment move) and add that sediment to the flow
                                    data[currentX][currentY] = data[currentX][currentY] - erosionAmount;
                                    sediment += erosionAmount;

                                    // set the new "current" X,Y
                                    if (lowestNeighbour.getKey() == 1) { currentX = currentX > 0 ? currentX - 1 : width - 1; currentY = currentY - 1; }
                                    else if (lowestNeighbour.getKey() == 2) { currentY = currentY - 1; }
                                    else if (lowestNeighbour.getKey() == 3) { currentX = currentX < width - 1 ? currentX + 1 : 0; currentY = currentY - 1; }
                                    else if (lowestNeighbour.getKey() == 4) { currentX = currentX > 0 ? currentX - 1 : width - 1; }
                                    else if (lowestNeighbour.getKey() == 5) { currentX = currentX < width - 1 ? currentX + 1 : 0; }
                                    else if (lowestNeighbour.getKey() == 6) { currentX = currentX > 0 ? currentX - 1 : width - 1; currentY = currentY + 1; }
                                    else if (lowestNeighbour.getKey() == 7) { currentY = currentY + 1; }
                                    else if (lowestNeighbour.getKey() == 8) { currentX = currentX < width - 1 ? currentX + 1 : 0; currentY = currentY + 1; }

                                    // get the new data height
                                    dataHeight = data[currentX][currentY];

                                    // carry on, wayward droplet
                                }
                                else
                                {
                                    // we've hit a wall and can't flow. Sediment drops, and now we pool.
                                    data[currentX][currentY] = data[currentX][currentY] + sediment;
                                    waterBuildup[currentX][currentY] = waterBuildup[currentX][currentY] + waterFloodAmount;
                                    processing = false;
                                }
                            }
                            else
                            {
                                processing = false;
                            }
                        }
                        else
                        {
                            // we hit a spot at or below sea level, stop the process
                            // drop the sediment (build up coastal areas) but water will not pool
                            data[currentX][currentY] = data[currentX][currentY] + (sediment / 2); // some sediment will be distributed into the sea, so we keep only a portion.
                            processing = false;
                        }

                        if (loopCount > maxPathLength)
                        {
                            // we've been at it for a while... could be a problem?
                            data[currentX][currentY] = data[currentX][currentY] + sediment;
                            waterBuildup[currentX][currentY] = waterBuildup[currentX][currentY] + waterFloodAmount;
                            processing = false;
                        }
                    }
                }
            }
        }

        // were done with the erosion, but the water chart might be useful
        // lets process it a bit by distributing (maybe add evaporation?)

        // water distributions step
        waterDistribution(data, waterBuildup, height, width);

        return waterBuildup;
    }

    public static void waterDistribution(double[][] data, double[][] water, int height, int width)
    {
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                double val = water[x][y];

                //get neighbours, and spread the water around to balance them out as much as possible

                double n = y == 0 ? 99.0f : data[x][y - 1] + water[x][y - 1];
                double e = x == width - 1 ? 99.0f : data[x + 1][y] + water[x + 1][y];
                double s = y == height - 1 ? 99.0f : data[x][y + 1] + water[x][y + 1];
                double w = x == 0 ? 99.0f : data[x - 1][y] + water[x - 1][y];

                List<Pair<Integer, Double>> flows = new ArrayList<Pair<Integer, Double>>();

                // add all neightbours to a list (marked 1-8 so we can identify them easy later)
                flows.add(new Pair<Integer, Double>(1, n));
                flows.add(new Pair<Integer, Double>(2, e));
                flows.add(new Pair<Integer, Double>(3, s));
                flows.add(new Pair<Integer, Double>(4, w));

                flows.sort(Comparator.comparing(Pair::getValue));
                List<Pair<Integer, Double>> finalFlows = new ArrayList<Pair<Integer, Double>>();
                // order these neighbour slopes by lowest to highest. We mainly care about the lowest
                // also, strip out the invalid points (val 99)
                for(Pair<Integer, Double> pair : flows)
                {
                	if(pair.getValue() != 99.0 && pair.getValue() < (val + data[x][y]))
                	{
                		finalFlows.add(pair);
                	}
                }
                
                flows = finalFlows;

                if (flows.size() > 0)
                {
                    double diff = (val + data[x][y]) - flows.get(0).getValue();
                    double thisWater = water[x][y] - (diff / 2.0);

                    water[x][y] = thisWater > 0 ? thisWater : 0.0;

                    if (flows.get(0).getKey() == 1) water[x][y - 1] = water[x][y - 1] + (diff / 2.0);
                    else if (flows.get(0).getKey() == 2) water[x + 1][y] = water[x + 1][y] + (diff / 2.0);
                    else if (flows.get(0).getKey() == 3) water[x][y + 1] = water[x][y + 1] + (diff / 2.0);
                    else if (flows.get(0).getKey() == 4) water[x - 1][y] = water[x - 1][ y] + (diff / 2.0);
                }
            }
        }
    }
}
