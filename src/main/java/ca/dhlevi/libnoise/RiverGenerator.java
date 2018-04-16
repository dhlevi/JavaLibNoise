package ca.dhlevi.libnoise;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class RiverGenerator 
{
	// River flow algorithm by starting from a random point and moving "down" until they hit water, a river, or can no longer move
	// works with some noise, but not as reliable as the a* approach
	public static int[][] createRiversFlowMethod(double[][] data, double seaLevel, int minimumLength, int iterations, int seed)
    {
        int width = data.length;
        int height = data[0].length;

        int[][] rivers = new int[width][height];
        
        Random rand = new Random(seed);
        
        for (int i = 0; i < iterations; i++)
        {
        	// get a random starting point for a river. It must be on land, obviously
        	Point currentPoint = null;
        	boolean riverComplete = false; 
        	boolean tributary = false;
        	int riverLength = 0;
        	List<Point> riverPoints = new ArrayList<Point>();
        	
			while(currentPoint == null)
			{
				int x = rand.nextInt(width);
				int y = rand.nextInt(height);
				
				if(data[x][y] > seaLevel && rivers[x][y] == 0)
				{
					currentPoint = new Point(x, y);
				}
			}
        	
			while(!riverComplete)
			{
				int x = currentPoint.getX();
				int y = currentPoint.getY();
				
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

                flows.sort(Comparator.comparing(Pair::getValue));
                
                boolean riverGrew = false;
                
                for(Pair<Integer, Double> slope : flows)
                {
                	Point nextPoint = null;
                	
                	if (slope.getKey() == 1) nextPoint = new Point(x > 0 ? x - 1 : width - 1, y > 0 ? y - 1 : 0);
                    else if (slope.getKey() == 2) nextPoint = new Point(x, y > 0 ? y - 1 : 0);
                    else if (slope.getKey() == 3) nextPoint = new Point(x < width - 1 ? x + 1 : 0, y > 0 ? y - 1 : 0);
                    else if (slope.getKey() == 4) nextPoint = new Point(x > 0 ? x - 1 : width - 1, y);
                    else if (slope.getKey() == 5) nextPoint = new Point(x < width - 1 ? x + 1 : 0, y);
                    else if (slope.getKey() == 6) nextPoint = new Point(x > 0 ? x - 1 : width - 1, y < height - 1 ? y + 1 : height - 1);
                    else if (slope.getKey() == 7) nextPoint = new Point(x, y < height - 1 ? y + 1 : height - 1);
                    else if (slope.getKey() == 8) nextPoint = new Point(x < width - 1 ? x + 1 : 0, y < height - 1 ? y + 1 : height - 1);

	            	if(nextPoint != null & nextPoint.getX() != currentPoint.getX() && nextPoint.getY() != currentPoint.getY())
	            	{
	            		rivers[currentPoint.getX()][currentPoint.getY()] = 1;
	            		riverPoints.add(new Point(currentPoint.getX(), currentPoint.getY()));		
	            		riverLength++;
	            		riverGrew = true;
	            		currentPoint = nextPoint;
	            		
	            		if(rivers[currentPoint.getX()][currentPoint.getY()] == 1) 
            			{
	            			riverComplete = true;
	            			tributary = true;
            			}
	            		
	            		break;
	            	}
                }

                // we iterated through all possible growth areas, but the river failed. Kill it, and refund the iter
                if(!riverGrew)
                {
                	riverComplete = true;
                	for(Point p : riverPoints)
                	{
                		rivers[p.getX()][p.getY()] = 0;
                	}
                	i--;
                }
                else
                {
                	//Did we hit water?
                	if(data[currentPoint.getX()][currentPoint.getY()] <= seaLevel)
                	{
                		riverComplete = true;
                	}
                }
			}
			
            if(riverLength < minimumLength && tributary == false)
            {
         	   // not a river, toss the data?
            	for(Point p : riverPoints)
            	{
            		rivers[p.getX()][p.getY()] = 0;
            	}
            	i--;
            }
        }
        
        return rivers;
    }
	
	// Not a true a*, but a close approach. Generates rivers by targeting a random start point and finding the closest
	// water source. Because the algorith and heuristic is a little looser than a* would allow for, some extra checks
	// are included to cancel out any rivers that would be excessive in length
	public static int[][] createRiversAStar(double[][] grid, double seaLevel, int maxRivers, int seed)
	{
		int width = grid.length;
        int height = grid[0].length;

        Random rand = new Random(seed);
        int[][] rivers = new int[width][height];
        
        for(int i = 0; i < maxRivers; i++)
        {
        	Point startPoint = null;
        	Point currentPoint = null;
        	List<Point> riverPoints = new ArrayList<Point>();
        	
        	// get a random starting point
			while(startPoint == null)
			{
				int x = rand.nextInt(width);
				int y = rand.nextInt(height);
				
				if(grid[x][y] > seaLevel && rivers[x][y] == 0)
				{
					startPoint = new Point(x, y);
					currentPoint = new Point(x, y);
				}
			}
			
			// get a destination point (the closest sea level)
			Point destinationPoint = getClosestWaterPoint(grid, startPoint, seaLevel, 100000);
			boolean destinationFound = false;
			
			while(!destinationFound)
			{
				if(currentPoint == null)
				{
					riverPoints.clear();
					break;
				}
				riverPoints.add(currentPoint);
				// if we're at the destination, we're done!
				if(currentPoint.equals(destinationPoint))
				{
					destinationFound = true;
					break;
				}
				
				// if we're not, we need to find the next best path
				// get all neighbours. The closest neighbour with the 
				int x = currentPoint.getX();
				int y = currentPoint.getY();
				List<Point> neighbours = new ArrayList<Point>();
				
				neighbours.add(y == 0 ? null : x == 0 ? new Point(width - 1, y - 1) : new Point(x - 1,y - 1));
				neighbours.add(y == 0 ? null : new Point(x,y - 1));
				neighbours.add(y == 0 ? null : x == width - 1 ? new Point(0,y - 1) : new Point(x + 1,y - 1));
				neighbours.add(x == width - 1 ? new Point(0,y) : new Point(x + 1,y));
				neighbours.add(y == height - 1 ? null : x == width - 1 ? new Point(0,y + 1) : new Point(x + 1,y + 1));
				neighbours.add(y == height - 1 ? null : new Point(x,y + 1));
				neighbours.add(y == height - 1 ? null : x == 0 ? new Point(width - 1,y + 1) : new Point(x - 1,y + 1));
				neighbours.add(x == 0 ? new Point(width - 1,y) : new Point(x - 1, y));                
				
				// get the valid points to test
				Point bestPoint = null;
				double bestScore = 0;
                for(Point p : neighbours)
                {
                	if(p != null && !riverPoints.contains(p))
                	{
                		// get the points score
                		double distance = Math.hypot(p.getX() - destinationPoint.getX(), p.getY() - destinationPoint.getY());
                		double heightValue = grid[p.getX()][p.getY()];
                		double score = heightValue + (distance / 200); // distance + heightValue;
                		
                		if(bestPoint == null || score < bestScore)
                		{
                			bestScore = score;
                			bestPoint = p;
                		}
                	}
                }
                
                currentPoint = bestPoint;
                
                // have we inadvertently hit another waterbody?
                if(grid[currentPoint.getX()][currentPoint.getY()] <= seaLevel)
                {
                	destinationFound = true;
                	riverPoints.add(currentPoint);
					break;
                }
                
                // have we collided with an existing river?
                if(rivers[currentPoint.getX()][currentPoint.getY()] == 1)
                {
                	destinationFound = true;
                	riverPoints.add(currentPoint);
					break;
                }
                		
                // are we seemingly lost in space?
                if(riverPoints.size() > width / 2)
                {
                	currentPoint = null;
                }
			}
			
			if(riverPoints.size() > 25)
			{
				for(Point p : riverPoints)
            	{
            		rivers[p.getX()][p.getY()] = 1;
            	}
			}
        }
        
        return rivers;
	}
	
	private static Point getClosestWaterPoint(double[][] grid, Point point, double seaLevel, int tolerance)
	{
		Point destination = null;
		boolean foundWater = false;
		
		int width = grid.length;
        int height = grid[0].length;
        
        int loops = 0;
		while(!foundWater)
		{
			if(loops > tolerance) break; // we've been searching for a while and never found a destination?
			
			for(int y = point.getY() - loops; y < point.getY() + loops && y < height - 1; y++)
			{
				for(int x = point.getX() - loops; x < point.getX() + loops && x < width - 1; x++)
				{
					// make sure we're within array bounds
					if(x < 0) x = 0;
					if(x >= width - 1) x = width - 1;
					if(y < 0) y = 0;
					if(y >= height - 1) y = height - 1;
					
					if(grid[x][y] <= seaLevel)
					{
						destination = new Point(x, y);
						foundWater = true;
						break;
					}
					
					if(y > point.getY() - loops && y < point.getY() + loops - 1)
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
