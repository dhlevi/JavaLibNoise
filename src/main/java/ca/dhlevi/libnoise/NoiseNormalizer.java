package ca.dhlevi.libnoise;

import java.util.List;
import java.util.Random;

public class NoiseNormalizer 
{
	// This is used to clear out any single pixel dangles and define the coastline areas
    // of a heightmap. Sea level is required to remove any single pixel lakes
    public static void Normalize(double[][] noise, double SeaLevel)
    {
    	int Width = noise.length;
        int Height = noise[0].length;
    	
        for (int x = 0; x < Width; x++)
        {
            for (int y = 0; y < Height; y++)
            {
            	double val = noise[x][y];

                // get the values N,S,E,W
            	double n = y > 0 ? noise[x][y - 1] : -99.0;
            	double s = y < Height - 1 ? noise[x][y + 1] : -99.0;
            	double e = x < Width - 1 ? noise[x + 1][y] : noise[0][y]; // wrap to the other edge
            	double w = x > 0 ? noise[x - 1][y] : noise[Width - 1][y]; // wrap to the other edge

                int waterNeighbours = 0;
                int landNeighbours = 0;

                if (n <= SeaLevel) waterNeighbours++;
                else landNeighbours++;
                if (e <= SeaLevel) waterNeighbours++;
                else landNeighbours++;
                if (s <= SeaLevel) waterNeighbours++;
                else landNeighbours++;
                if (w <= SeaLevel) waterNeighbours++;
                else landNeighbours++;

                // If this is a dangle (single pixel surrounded by water or land) then fill it in or sink it down
                if (val <= SeaLevel && waterNeighbours < 2) noise[x][y] = SeaLevel + 0.05f;
                else if (val > SeaLevel && landNeighbours < 2) noise[x][y] = SeaLevel - 0.05f;
            }
        }
    }

    // scan the noise data and detect any basin areas by a tolerance. Basins can then be filled in removing holes from a heightmap.
    // if desired, you can keep small basins as lakes. Will return calculated basin areas if needed for other calculations later.
    public static int[][] DetectBasins(double[][] noise, int tolerance, double SeaLevel, boolean fillBasins, boolean keepSmallLakes, int seed)
    {
    	int Width = noise.length;
        int Height = noise[0].length;
        int[][] setPoints = new int[Width][Height];
        Random rand = new Random(seed);
        // create a temporary array for water levels and land
        // 1 = land, 2 = lake, 3 = large waterbody (ocean/sea), 4 = Unfilled lake, 0 is unassigned
        // unfilled lakes are small lakes that are underneath the tolerance for a basin fill
        // these lakes will remain as water after the basin fill process.
        // 
        for (int x = 0; x < Width; x++)
        {
            for (int y = 0; y < Height; y++)
            {
                // only process unassigned points
                if (setPoints[x][y] == 0)
                {
                	double val = noise[x][y];

                    if (val <= SeaLevel)
                    {
                        // check if neighbouring an "ocean" pixel first, before walking points
                        int n = y > 0 ? setPoints[x][y - 1] : -1;
                        int s = y < Height - 1 ? setPoints[x][y + 1] : -1;
                        int e = x < Width - 1 ? setPoints[x + 1][y] : setPoints[0][y]; // wrap to the other edge
                        int w = x > 0 ? setPoints[x - 1][y] : setPoints[Width - 1][y]; // wrap to the other edge

                        int nw = x > 0 && y > 0 ? setPoints[x - 1][y - 1] : -1;
                        int ne = x < Width - 1 && y > 0 ? setPoints[x + 1][y - 1] : -1;
                        int sw = x > 0 && y < Height - 1 ? setPoints[x - 1][y + 1] : -1;
                        int se = x < Width - 1 && y < Height - 1 ? setPoints[x + 1][y + 1] : -1;

                        // neighbour is an ocean, and this is water, so it too must be part of the ocean.
                        // otherwise, this is unassigned and next to land or a small waterbody. We need to scan the points
                        if (n == 3 || s == 3 || e == 3 || w == 3 || nw == 3 || ne == 3 || sw == 3 || se == 3) setPoints[x][y] = 3;
                        else if (n == 2 || s == 2 || e == 2 || w == 2 || nw == 2 || ne == 2 || sw == 2 || se == 2)
                        {
                            setPoints[x][y] = 2;
                        }
                        else if (n == 4 || s == 4 || e == 4 || w == 4 || nw == 4 || ne == 4 || sw == 4 || se == 4)
                        {
                            setPoints[x][y] = 4;
                        }
                        else
                        {
                            List<Point> scannedPoints = FeatureTracer.traceEqualOrBelowValue(noise, x, y, Width, Height, SeaLevel, true);
                            for(Point p : scannedPoints)
                            {
                                setPoints[p.getX()][p.getY()] = scannedPoints.size() <= tolerance ? 2 : 3;
                                if (keepSmallLakes && scannedPoints.size() < tolerance / 4) setPoints[p.getX()][p.getY()] = 4;
                            }
                        }
                    }
                    else setPoints[x][y] = 1; // set as "land"
                }
            }
        }

        //second pass, sw to ne this time, to clean up any missed internal areas
        for (int x = 0; x < Width; x++)
        {
            for (int y = 0; y < Height; y++)
            {
            	double val = noise[x][y];

                if (val <= SeaLevel)
                {
                    int n = y > 0 ? setPoints[x][y - 1] : -1;
                    int s = y < Height - 1 ? setPoints[x][y + 1] : -1;
                    int e = x < Width - 1 ? setPoints[x + 1][y] : setPoints[0][y]; // wrap to the other edge
                    int w = x > 0 ? setPoints[x - 1][y] : setPoints[Width - 1][y]; // wrap to the other edge

                    int nw = x > 0 && y > 0 ? setPoints[x - 1][y - 1] : -1;
                    int ne = x < Width - 1 && y > 0 ? setPoints[x + 1][y - 1] : -1;
                    int sw = x > 0 && y < Height - 1 ? setPoints[x - 1][y + 1] : -1;
                    int se = x < Width - 1 && y < Height - 1 ? setPoints[x + 1][y + 1] : -1;

                    // neighbour is an ocean, and this is water, so it too must be part of the ocean.
                    // otherwise, this is unassigned and next to land or a small waterbody. We need to scan the points
                    if (n == 3 || s == 3 || e == 3 || w == 3 || nw == 3 || ne == 3 || sw == 3 || se == 3) setPoints[x][y] = 3;
                }
            }
        }

        //third and final pass, there shouldn't be any corrections needed
        //but we'll ensure the ocean/lake values are set, just in case
        //at this point, we'll also fill in the basins, if required.
        for (int x = Width - 1; x >= 0; x--)
        {
            for (int y = Height - 1; y >= 0; y--)
            {
                double val = noise[x][y];

                if (val <= SeaLevel)
                {
                    int n = y > 0 ? setPoints[x][y - 1] : -1;
                    int s = y < Height - 1 ? setPoints[x][y + 1] : -1;
                    int e = x < Width - 1 ? setPoints[x + 1][y] : setPoints[0][y]; // wrap to the other edge
                    int w = x > 0 ? setPoints[x - 1][y] : setPoints[Width - 1][y]; // wrap to the other edge

                    int nw = x > 0 && y > 0 ? setPoints[x - 1][y - 1] : -1;
                    int ne = x < Width - 1 && y > 0 ? setPoints[x + 1][y - 1] : -1;
                    int sw = x > 0 && y < Height - 1 ? setPoints[x - 1][y + 1] : -1;
                    int se = x < Width - 1 && y < Height - 1 ? setPoints[x + 1][y + 1] : -1;

                    // neighbour is an ocean, and this is water, so it too must be part of the ocean.
                    // otherwise, this is unassigned and next to land or a small waterbody. We need to scan the points
                    if (n == 3 || s == 3 || e == 3 || w == 3 || nw == 3 || ne == 3 || sw == 3 || se == 3) setPoints[x][y] = 3;
                    else if (setPoints[x][y] == 2 && fillBasins) noise[x][y] = SeaLevel + (rand.nextDouble() / 100.0);// lower the value = heavier noise in fill
                }
            }
        }
        return setPoints;
    }
}
