package ca.dhlevi.libnoise;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ca.dhlevi.libnoise.spatial.Coordinate;
import ca.dhlevi.libnoise.spatial.Envelope;
import ca.dhlevi.libnoise.spatial.SpatialUtilities;

public class RegionGenerator
{
    // generates regions by spreading random points and "growing" them until all
    // possible pixels are owned by a region
    // regions should not cross rivers unless necessary
    public static int[][] generateRegions(double[][] data, int[][] basins, int[][] rivers, double seaLevel, int regionDensity, int oceanRegionDensity, Envelope bbox, int seed)
    {
        if (regionDensity == 0)
            regionDensity = 30;

        if (oceanRegionDensity == 0)
            oceanRegionDensity = 30;

        int width = data.length;
        int height = data[0].length;

        int oceanRegionCount = 0;

        int[][] regions = new int[width][height];

        int terrestrialPixelsCount = 0;
        // the lower the density, the lower the region count
        // this method will create nice uniform grids for regions over the water
        for (int x = width / oceanRegionDensity; x < width; x = x += (width / oceanRegionDensity))
        {
            for (int y = width / oceanRegionDensity; y < height; y += (width / oceanRegionDensity))
            {
                if (data[x][y] <= seaLevel)
                {
                    oceanRegionCount++;
                    regions[x][y] = oceanRegionCount;
                } 
                else
                {
                    terrestrialPixelsCount++;
                }
            }
        }

        // random point method
        // the lower the density the higher the region count, for terrestrial regions
        // try and spread the points so there's greater density in the middle
        int terrainRegionCount = 0;
        Random rand = new Random(seed);
        /*while (terrestrialPixelsCount > 0 && terrainRegionCount < (width / regionDensity) * (height / regionDensity))
        {
            int x = rand.nextInt(width - 1);
            int y = rand.nextInt(height - 1);

            if (rivers[x][y] != 1 && data[x][y] > seaLevel)
            {
                terrainRegionCount++;
                regions[x][y] = terrainRegionCount + oceanRegionCount;
            }
        }*/

        // "natural city" method.
        // Place locations of settlement in 16km intervals.
        // once all possible locations are placed, adjust each one to place them in
        // suitable locations (off high slopes, closer to river/water) remove regions if needed
        // if water is distant, no village can grow?
        // use this grid to generate provinces as usual
        int cityLevel = 5; // 1 = hamlet (16km apart), 5 = village, 10 = town, 15 = city, 20 = global centre
        int pixelDist = 16 * cityLevel;
        int row = 0;
        while(row < data[0].length - 1)
        {
            Coordinate c1 = SpatialUtilities.pixelsToLatLong(new Point(0, row), width, height, bbox);
            Coordinate c2 = SpatialUtilities.pixelsToLatLong(new Point(1, row), width, height, bbox);
            Coordinate c3 = SpatialUtilities.pixelsToLatLong(new Point(0, row + 1), width, height, bbox);
            
            double pixelLongitudeDistanceKms = SpatialUtilities.haversineDistance(c1, c2) / 1000;
            double pixelLatitudeDistanceKms = SpatialUtilities.haversineDistance(c1, c3) / 1000;
            
            // we have distances. Place points across this row at 16km distances.
            int column = 0;
            while(column < data.length - 1)
            {
                // drop a region if not on a river, ocean, or at a height
                if(rivers[column][row] < 1 && data[column][row] > seaLevel && data[column][row] < 0.8)
                {
                    terrainRegionCount++;
                    // is there a body of water or river close by? If so move point towards them
                    Point river = getClosestRegionPoint(rivers, data, new Point(column, row), seaLevel, bbox, pixelDist / 2);
                    
                    if(river != null) regions[river.getX()][river.getY()] = terrainRegionCount + oceanRegionCount;
                    else regions[column][row] = terrainRegionCount + oceanRegionCount;
                    
                    regions[column][row] = terrainRegionCount + oceanRegionCount;
                }
                
                double colCalc = Math.round(pixelDist / pixelLongitudeDistanceKms);
                column += colCalc <= 0 ? 1 : colCalc;
            }
            // find the next possible row
            double rowCalc = Math.round(pixelDist / pixelLatitudeDistanceKms);
            row += rowCalc <= 0 ? 1 : rowCalc;
        }

        // End City placement method
        
        int regionCount = oceanRegionCount + terrainRegionCount;

        if (terrestrialPixelsCount > 0 && terrainRegionCount == 0)
        {
            // uh oh, We either have no land at all, or none were able to get a
            // point needed for a region. So set all land to a region if possible.
            regionCount++;
            for (int x = width / regionDensity; x < width; x = x += (width / regionDensity))
            {
                for (int y = width / regionDensity; y < height; y += (width / regionDensity))
                {
                    if (data[x][y] > seaLevel)
                    {
                        regions[x][y] = regionCount;
                    }
                }
            }
        }

        System.out.println("Creating " + regionCount + " regions...");

        // we have a grid of regions established, now they need to "grow". ensure we stop at rivers and heights
        regions = generationPass(regions, width, height, data, seaLevel, rivers, 0.025, false, true, seed);
        // cleanup any little dangles, grow into any double-river spaces
        regions = generationPass(regions, width, height, data, seaLevel, rivers, 0.125, false, true, seed);
        // regions are now defined, but there may be some leftover space that isn't assigned yet, particularly random islands
        // if an island is close to another region, it'll "merge" with it. if there isn't any region nearby (at least 15 pixels)
        // a new region will be created
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                if (regions[x][y] == 0)
                {
                    if (data[x][y] <= seaLevel && basins[x][y] != 1)
                    {
                        regionCount++;
                        regions[x][y] = regionCount;
                        // re-running a gen pass to fill is slow... need to replace this
                        regions = generationPass(regions, width, height, data, seaLevel, rivers, 1.0, false, false, seed);
                    } 
                    else
                    {
                        Point closestRegionPoint = getClosestRegionPoint(regions, data, new Point(x, y), seaLevel, bbox, width / 500 > 15 ? width / 500 : 15);
                        if (closestRegionPoint != null)
                        {
                            int region = regions[closestRegionPoint.getX()][closestRegionPoint.getY()];
                            regions[x][y] = region;
                        }
                        else
                        {
                            regionCount++;
                            regions[x][y] = regionCount;
                        }
                    }
                }
            }
        }

        return regions;
    }

    private static int[][] generationPass(int[][] regions, int width, int height, double[][] data, double seaLevel, int[][] rivers, double heightTolerance, boolean blockOnRivers, boolean noisy, int seed)
    {
        boolean regionsGrown = true;
        Random rand = new Random(seed);

        while (regionsGrown)
        {
            int[][] tempRegions = new int[width][height];

            int regionGrowth = 0;
            for (int x = 0; x < width; x++)
            {
                for (int y = 0; y < height; y++)
                {
                    boolean s = y < height - 1 ? regions[x][y + 1] > 0 : true;
                    boolean n = y > 0 ? regions[x][y - 1] > 0 : true;
                    boolean e = x < width - 1 ? regions[x + 1][y] > 0 : true;
                    boolean w = x > 0 ? regions[x - 1][y] > 0 : true;

                    // test if we found a region chunk
                    if (regions[x][y] != 0 && !(s && n && e && w))
                    {
                        // grow into all availAble neighbours that are below a talus angle, and not water. 
                        // If ocean, just grow till you hit coastline or another region
                        boolean isOcean = data[x][y] <= seaLevel;

                        List<Point> neighbours = new ArrayList<Point>();

                        neighbours.add(y == 0 ? null : new Point(x, y - 1));
                        neighbours.add(x == width - 1 ? new Point(0, y) : new Point(x + 1, y));
                        neighbours.add(y == height - 1 ? null : new Point(x, y + 1));
                        neighbours.add(x == 0 ? new Point(width - 1, y) : new Point(x - 1, y));

                        for (Point p : neighbours)
                        {
                            if (p != null && regions[p.getX()][p.getY()] == 0 && tempRegions[p.getX()][p.getY()] == 0)
                            {
                                if (isOcean && data[p.getX()][p.getY()] <= seaLevel)
                                {
                                    tempRegions[p.getX()][p.getY()] = regions[x][y];
                                    regionGrowth++;
                                }
                                else if (!isOcean && data[p.getX()][p.getY()] > seaLevel)
                                {
                                    if (((blockOnRivers && rivers[p.getX()][p.getY()] == 0) || !blockOnRivers) && (!noisy || (noisy && rand.nextBoolean())))
                                    {
                                        // get the height difference between these points. If the difference is too big, we can't grow.
                                        double difference = data[p.getX()][p.getY()] - data[x][y];

                                        if (difference < heightTolerance)
                                        {
                                            tempRegions[p.getX()][p.getY()] = regions[x][y];
                                            // depending on the latitude, this should spread faster.
                                            // north/south should spread much more than equitorial regions.
                                            regionGrowth++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            for (int x = 0; x < width; x++)
            {
                for (int y = 0; y < height; y++)
                {
                    if (tempRegions[x][y] != 0)
                        regions[x][y] = tempRegions[x][y];
                }
            }

            regionsGrown = regionGrowth > 0;
        }

        return regions;
    }
   
    // technically, 'regionData' can be overloaded with any data that uses an int... so rivers, etc..?
    private static Point getClosestRegionPoint(int[][] regionData, double[][] noiseData, Point point, double seaLevel, Envelope bbox, int tolerance)
    {
        Point destination = null;
        boolean foundRegion = false;

        int width = regionData.length;
        int height = regionData[0].length;

        int loops = 0;
        while (!foundRegion)
        {
            if (loops > tolerance)
                break; // we've been searching for a while and never found a destination?

            List<Point> possibleLocations = new ArrayList<Point>();
            
            List<Point> circlePoints = new ArrayList<Point>();
            double angleInDegrees = 0;
            
            while(angleInDegrees <= 360)
            {
                // loops + 2 for radius, so you don't start at your inner point
                int x = (int) Math.round(((loops + 2) * Math.cos(angleInDegrees * Math.PI / 180F)) + point.getX());
                int y = (int) Math.round(((loops + 2) * Math.sin(angleInDegrees * Math.PI / 180F)) + point.getY());

                circlePoints.add(new Point(x, y));
                angleInDegrees += Math.round((90 / (loops + 1)));
            }
            
            for(Point p : circlePoints)
            {
                int x = p.getX();
                int y = p.getY();
                // make sure we're within array bounds
                if (x < 0)
                    x = 0;
                if (x >= width - 1)
                    x = width - 1;
                if (y < 0)
                    y = 0;
                if (y >= height - 1)
                    y = height - 1;

                if (regionData[x][y] != 0 && noiseData[x][y] > seaLevel)
                {
                    possibleLocations.add(new Point(x, y));
                }

                if (y > point.getY() - loops && y < point.getY() + loops - 1)
                {
                    x = point.getX() + loops;
                }
            }
            
            // all of the possible closest region points. 
            // find the actual closest...
            if(possibleLocations.size() > 0)
            {
                destination = null;
                double closestDistance = 0;
                
                for(Point p : possibleLocations)
                {
                    Coordinate c1 = SpatialUtilities.pixelsToLatLong(point, width, height, bbox);
                    Coordinate c2 = SpatialUtilities.pixelsToLatLong(p, width, height, bbox);
                    
                    double pixelDistance = SpatialUtilities.haversineDistance(c1, c2) / 1000;
                    
                    if(destination == null || pixelDistance < closestDistance)
                    {
                        destination = p;
                        closestDistance = pixelDistance;
                    }
                }             
            }

            loops++;
        }

        return destination;
    }
}
