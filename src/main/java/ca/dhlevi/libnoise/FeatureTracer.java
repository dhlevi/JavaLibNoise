package ca.dhlevi.libnoise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FeatureTracer
{
    private enum Direction
    {
        North, NorthEast, East, SouthEast, South, SouthWest, West, NorthWest
    }

    // traces all points that are rqual or below a given value. Useful for contour tracing
    public static List<Point> traceEqualOrBelowValue(double[][] noise, int x, int y, int width, int height, double value, boolean wrap)
    {
        List<Point> points = new ArrayList<Point>();

        try
        {
            boolean traceComplete = false;
            boolean firstRun = true;
            Point firstPoint = new Point(x, y);
            Point lastPoint = firstPoint;
            Direction lastDirection = Direction.East;

            while (!traceComplete)
            {
                if (!firstRun && lastPoint.getX() == x && lastPoint.getY() == y)
                {
                    traceComplete = true;
                } 
                else
                {
                    if (firstRun)
                        firstRun = false;
                    points.add(lastPoint);
                    switch (lastDirection)
                    {
                    case NorthWest:
                        // check SW W NW N NE E SE S
                        // first hit that is <= value is the direction to move
                        if (lastPoint.getX() > 0 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() - 1][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && lastPoint.getY() < height - 1 && noise[width - 1][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (lastPoint.getX() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && noise[width - 1][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (lastPoint.getX() > 0 && lastPoint.getY() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        }
                        else if (wrap && lastPoint.getX() == 0 && lastPoint.getY() > 0 && noise[width - 1][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (lastPoint.getY() > 0 && noise[lastPoint.getX()][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() - 1);
                            lastDirection = Direction.North;
                        } 
                        else if (lastPoint.getX() < width - 1 && lastPoint.getY() > 0 && noise[lastPoint.getX() + 1][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && lastPoint.getY() > 0 && noise[0][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (lastPoint.getX() < width - 1 && noise[lastPoint.getX() + 1][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && noise[0][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(0, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (lastPoint.getX() < width - 1 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() + 1][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && lastPoint.getY() < height - 1 && noise[0][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (lastPoint.getY() < height - 1 && noise[lastPoint.getX()][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() + 1);
                            lastDirection = Direction.South;
                        } 
                        else
                            traceComplete = true;
                        break;
                    case North:
                        // check W NW N NE E SE S SW
                        // first hit that is <= value is the direction to move
                        if (lastPoint.getX() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && noise[width - 1][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (lastPoint.getX() > 0 && lastPoint.getY() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && lastPoint.getY() > 0 && noise[width - 1][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (lastPoint.getY() > 0 && noise[lastPoint.getX()][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() - 1);
                            lastDirection = Direction.North;
                        } 
                        else if (lastPoint.getX() < width - 1 && lastPoint.getY() > 0 && noise[lastPoint.getX() + 1][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && lastPoint.getY() > 0 && noise[0][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (lastPoint.getX() < width - 1 && noise[lastPoint.getX() + 1][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && noise[0][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(0, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (lastPoint.getX() < width - 1 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() + 1][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && lastPoint.getY() < height - 1 && noise[0][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (lastPoint.getY() < height - 1 && noise[lastPoint.getX()][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() + 1);
                            lastDirection = Direction.South;
                        } 
                        else if (lastPoint.getX() > 0 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() - 1][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && lastPoint.getY() < height - 1 && noise[width - 1][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else
                            traceComplete = true;
                        break;
                    case NorthEast:
                        // check NW N NE E SE S SW W
                        // first hit that is <= value is the direction to move
                        if (lastPoint.getX() > 0 && lastPoint.getY() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && lastPoint.getY() > 0 && noise[width - 1][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (lastPoint.getY() > 0 && noise[lastPoint.getX()][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() - 1);
                            lastDirection = Direction.North;
                        } 
                        else if (lastPoint.getX() < width - 1 && lastPoint.getY() > 0 && noise[lastPoint.getX() + 1][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && lastPoint.getY() > 0 && noise[0][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (lastPoint.getX() < width - 1 && noise[lastPoint.getX() + 1][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && noise[0][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(0, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (lastPoint.getX() < width - 1 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() + 1][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && lastPoint.getY() < height - 1 && noise[0][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (lastPoint.getY() < height - 1 && noise[lastPoint.getX()][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() + 1);
                            lastDirection = Direction.South;
                        } 
                        else if (lastPoint.getX() > 0 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() - 1][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && lastPoint.getY() < height - 1 && noise[width - 1][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (lastPoint.getX() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && noise[width - 1][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else
                            traceComplete = true;
                        break;
                    case East:
                        // check N NE E SE S SW W NW
                        // first hit that is <= value is the direction to move
                        if (lastPoint.getY() > 0 && noise[lastPoint.getX()][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() - 1);
                            lastDirection = Direction.North;
                        } 
                        else if (lastPoint.getX() < width - 1 && lastPoint.getY() > 0 && noise[lastPoint.getX() + 1][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && lastPoint.getY() > 0 && noise[0][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (lastPoint.getX() < width - 1 && noise[lastPoint.getX() + 1][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && noise[0][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(0, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (lastPoint.getX() < width - 1 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() + 1][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && lastPoint.getY() < height - 1 && noise[0][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (lastPoint.getY() < height - 1 && noise[lastPoint.getX()][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() + 1);
                            lastDirection = Direction.South;
                        } 
                        else if (lastPoint.getX() > 0 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() - 1][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && lastPoint.getY() < height - 1 && noise[width - 1][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (lastPoint.getX() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && noise[width - 1][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (lastPoint.getX() > 0 && lastPoint.getY() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && lastPoint.getY() > 0 && noise[width - 1][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else
                            traceComplete = true;
                        break;
                    case SouthEast:
                        // check NE E SE S SW W NW N
                        // first hit that is <= value is the direction to move
                        if (lastPoint.getX() < width - 1 && lastPoint.getY() > 0 && noise[lastPoint.getX() + 1][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && lastPoint.getY() > 0 && noise[0][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (lastPoint.getX() < width - 1 && noise[lastPoint.getX() + 1][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && noise[0][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(0, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (lastPoint.getX() < width - 1 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() + 1][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && lastPoint.getY() < height - 1 && noise[0][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (lastPoint.getY() < height - 1 && noise[lastPoint.getX()][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() + 1);
                            lastDirection = Direction.South;
                        } 
                        else if (lastPoint.getX() > 0 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() - 1][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && lastPoint.getY() < height - 1 && noise[width - 1][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (lastPoint.getX() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && noise[width - 1][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (lastPoint.getX() > 0 && lastPoint.getY() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        }
                        else if (wrap && lastPoint.getX() == 0 && lastPoint.getY() > 0 && noise[width - 1][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (lastPoint.getY() > 0 && noise[lastPoint.getX()][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() - 1);
                            lastDirection = Direction.North;
                        } 
                        else
                            traceComplete = true;
                        break;
                    case South:
                        // check E SE S SW W NW N NE
                        // first hit that is <= value is the direction to move
                        if (lastPoint.getX() < width - 1 && noise[lastPoint.getX() + 1][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && noise[0][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(0, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (lastPoint.getX() < width - 1 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() + 1][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && lastPoint.getY() < height - 1 && noise[0][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (lastPoint.getY() < height - 1 && noise[lastPoint.getX()][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() + 1);
                            lastDirection = Direction.South;
                        } 
                        else if (lastPoint.getX() > 0 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() - 1][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && lastPoint.getY() < height - 1 && noise[width - 1][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (lastPoint.getX() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        }
                        else if (wrap && lastPoint.getX() == 0 && noise[width - 1][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        }
                        else if (lastPoint.getX() > 0 && lastPoint.getY() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && lastPoint.getY() > 0 && noise[width - 1][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (lastPoint.getY() > 0 && noise[lastPoint.getX()][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() - 1);
                            lastDirection = Direction.North;
                        } 
                        else if (lastPoint.getX() < width - 1 && lastPoint.getY() > 0 && noise[lastPoint.getX() + 1][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        }
                        else if (wrap && lastPoint.getX() == width - 1 && lastPoint.getY() > 0 && noise[0][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        }
                        else
                            traceComplete = true;
                        break;
                    case SouthWest:
                        // check SE S SW W NW N NE E
                        // first hit that is <= value is the direction to move
                        if (lastPoint.getX() < width - 1 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() + 1][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        }
                        else if (wrap && lastPoint.getX() == width - 1 && lastPoint.getY() < height - 1 && noise[0][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        }
                        else if (lastPoint.getY() < height - 1 && noise[lastPoint.getX()][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() + 1);
                            lastDirection = Direction.South;
                        } 
                        else if (lastPoint.getX() > 0 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() - 1][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && lastPoint.getY() < height - 1 && noise[width - 1][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (lastPoint.getX() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && noise[width - 1][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (lastPoint.getX() > 0 && lastPoint.getY() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && lastPoint.getY() > 0 && noise[width - 1][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (lastPoint.getY() > 0 && noise[lastPoint.getX()][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() - 1);
                            lastDirection = Direction.North;
                        }
                        else if (lastPoint.getX() < width - 1 && lastPoint.getY() > 0 && noise[lastPoint.getX() + 1][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && lastPoint.getY() > 0 && noise[0][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (lastPoint.getX() < width - 1 && noise[lastPoint.getX() + 1][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && noise[0][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(0, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else
                            traceComplete = true;
                        break;
                    case West:
                        // check S SW W NW N NE E SE
                        // first hit that is <= value is the direction to move
                        if (lastPoint.getY() < height - 1 && noise[lastPoint.getX()][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() + 1);
                            lastDirection = Direction.South;
                        } 
                        else if (lastPoint.getX() > 0 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() - 1][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && lastPoint.getY() < height - 1 && noise[width - 1][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (lastPoint.getX() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        }
                        else if (wrap && lastPoint.getX() == 0 && noise[width - 1][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        }
                        else if (lastPoint.getX() > 0 && lastPoint.getY() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && lastPoint.getY() > 0 && noise[width - 1][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (lastPoint.getY() > 0 && noise[lastPoint.getX()][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() - 1);
                            lastDirection = Direction.North;
                        } 
                        else if (lastPoint.getX() < width - 1 && lastPoint.getY() > 0 && noise[lastPoint.getX() + 1][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && lastPoint.getY() > 0 && noise[0][lastPoint.getY() - 1] <= value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (lastPoint.getX() < width - 1 && noise[lastPoint.getX() + 1][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && noise[0][lastPoint.getY()] <= value)
                        {
                            lastPoint = new Point(0, lastPoint.getY());
                            lastDirection = Direction.East;
                        }
                        else if (lastPoint.getX() < width - 1 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() + 1][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && lastPoint.getY() < height - 1 && noise[0][lastPoint.getY() + 1] <= value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else
                            traceComplete = true;
                        break;
                    }
                }
            }
        } catch (Exception e)
        {
            points = new ArrayList<Point>();
        }

        return points;
    }

    // tracing points by a given value. Useful for generating polygons from
    // region data
    public static List<Point> traceEqualValue(double[][] noise, int x, int y, int width, int height, double value, boolean wrap)
    {
        boolean traceAngles = false;

        List<Point> points = new ArrayList<Point>();

        try
        {
            boolean traceComplete = false;
            boolean firstRun = true;
            Point firstPoint = new Point(x, y);
            Point lastPoint = firstPoint;
            Direction lastDirection = Direction.East;

            while (!traceComplete)
            {
                if (!firstRun && lastPoint.getX() == x && lastPoint.getY() == y)
                {
                    traceComplete = true;
                } else
                {
                    if (firstRun)
                        firstRun = false;
                    points.add(lastPoint);
                    switch (lastDirection)
                    {
                    case NorthWest:
                        // check SW W NW N NE E SE S
                        // first hit that is <= value is the direction to move
                        if (traceAngles && lastPoint.getX() > 0 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() - 1][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == 0 && lastPoint.getY() < height - 1 && noise[width - 1][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (lastPoint.getX() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && noise[width - 1][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (traceAngles && lastPoint.getX() > 0 && lastPoint.getY() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == 0 && lastPoint.getY() > 0 && noise[width - 1][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (lastPoint.getY() > 0 && noise[lastPoint.getX()][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() - 1);
                            lastDirection = Direction.North;
                        } 
                        else if (traceAngles && lastPoint.getX() < width - 1 && lastPoint.getY() > 0 && noise[lastPoint.getX() + 1][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == width - 1 && lastPoint.getY() > 0 && noise[0][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (lastPoint.getX() < width - 1 && noise[lastPoint.getX() + 1][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && noise[0][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(0, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (traceAngles && lastPoint.getX() < width - 1 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() + 1][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == width - 1 && lastPoint.getY() < height - 1 && noise[0][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (lastPoint.getY() < height - 1 && noise[lastPoint.getX()][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() + 1);
                            lastDirection = Direction.South;
                        } 
                        else
                            traceComplete = true;
                        break;
                    case North:
                        // check W NW N NE E SE S SW
                        // first hit that is <= value is the direction to move
                        if (lastPoint.getX() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && noise[width - 1][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (traceAngles && lastPoint.getX() > 0 && lastPoint.getY() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == 0 && lastPoint.getY() > 0 && noise[width - 1][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (lastPoint.getY() > 0 && noise[lastPoint.getX()][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() - 1);
                            lastDirection = Direction.North;
                        } 
                        else if (traceAngles && lastPoint.getX() < width - 1 && lastPoint.getY() > 0 && noise[lastPoint.getX() + 1][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == width - 1 && lastPoint.getY() > 0 && noise[0][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (lastPoint.getX() < width - 1 && noise[lastPoint.getX() + 1][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && noise[0][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(0, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (traceAngles && lastPoint.getX() < width - 1 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() + 1][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == width - 1 && lastPoint.getY() < height - 1 && noise[0][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (lastPoint.getY() < height - 1 && noise[lastPoint.getX()][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() + 1);
                            lastDirection = Direction.South;
                        } 
                        else if (traceAngles && lastPoint.getX() > 0 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() - 1][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == 0 && lastPoint.getY() < height - 1 && noise[width - 1][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else
                            traceComplete = true;
                        break;
                    case NorthEast:
                        // check NW N NE E SE S SW W
                        // first hit that is <= value is the direction to move
                        if (traceAngles && lastPoint.getX() > 0 && lastPoint.getY() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == 0 && lastPoint.getY() > 0 && noise[width - 1][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (lastPoint.getY() > 0 && noise[lastPoint.getX()][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() - 1);
                            lastDirection = Direction.North;
                        } 
                        else if (traceAngles && lastPoint.getX() < width - 1 && lastPoint.getY() > 0 && noise[lastPoint.getX() + 1][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        }
                        else if (traceAngles && wrap && lastPoint.getX() == width - 1 && lastPoint.getY() > 0 && noise[0][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (lastPoint.getX() < width - 1 && noise[lastPoint.getX() + 1][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && noise[0][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(0, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (traceAngles && lastPoint.getX() < width - 1 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() + 1][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        }
                        else if (traceAngles && wrap && lastPoint.getX() == width - 1 && lastPoint.getY() < height - 1 && noise[0][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (lastPoint.getY() < height - 1 && noise[lastPoint.getX()][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() + 1);
                            lastDirection = Direction.South;
                        }
                        else if (traceAngles && lastPoint.getX() > 0 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() - 1][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == 0 && lastPoint.getY() < height - 1 && noise[width - 1][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (lastPoint.getX() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && noise[width - 1][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else
                            traceComplete = true;
                        break;
                    case East:
                        // check N NE E SE S SW W NW
                        // first hit that is <= value is the direction to move
                        if (lastPoint.getY() > 0 && noise[lastPoint.getX()][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() - 1);
                            lastDirection = Direction.North;
                        } 
                        else if (traceAngles && lastPoint.getX() < width - 1 && lastPoint.getY() > 0 && noise[lastPoint.getX() + 1][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == width - 1 && lastPoint.getY() > 0 && noise[0][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (lastPoint.getX() < width - 1 && noise[lastPoint.getX() + 1][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && noise[0][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(0, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (traceAngles && lastPoint.getX() < width - 1 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() + 1][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == width - 1 && lastPoint.getY() < height - 1 && noise[0][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (lastPoint.getY() < height - 1 && noise[lastPoint.getX()][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() + 1);
                            lastDirection = Direction.South;
                        } 
                        else if (traceAngles && lastPoint.getX() > 0 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() - 1][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == 0 && lastPoint.getY() < height - 1 && noise[width - 1][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (lastPoint.getX() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && noise[width - 1][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (traceAngles && lastPoint.getX() > 0 && lastPoint.getY() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == 0 && lastPoint.getY() > 0 && noise[width - 1][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else
                            traceComplete = true;
                        break;
                    case SouthEast:
                        // check NE E SE S SW W NW N
                        // first hit that is <= value is the direction to move
                        if (traceAngles && lastPoint.getX() < width - 1 && lastPoint.getY() > 0 && noise[lastPoint.getX() + 1][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == width - 1 && lastPoint.getY() > 0 && noise[0][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (lastPoint.getX() < width - 1 && noise[lastPoint.getX() + 1][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && noise[0][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(0, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (traceAngles && lastPoint.getX() < width - 1 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() + 1][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == width - 1 && lastPoint.getY() < height - 1 && noise[0][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (lastPoint.getY() < height - 1 && noise[lastPoint.getX()][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() + 1);
                            lastDirection = Direction.South;
                        } 
                        else if (traceAngles && lastPoint.getX() > 0 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() - 1][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == 0 && lastPoint.getY() < height - 1 && noise[width - 1][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (lastPoint.getX() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && noise[width - 1][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (traceAngles && lastPoint.getX() > 0 && lastPoint.getY() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == 0 && lastPoint.getY() > 0 && noise[width - 1][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (lastPoint.getY() > 0 && noise[lastPoint.getX()][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() - 1);
                            lastDirection = Direction.North;
                        } 
                        else
                            traceComplete = true;
                        break;
                    case South:
                        // check E SE S SW W NW N NE
                        // first hit that is <= value is the direction to move
                        if (lastPoint.getX() < width - 1 && noise[lastPoint.getX() + 1][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && noise[0][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(0, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (traceAngles && lastPoint.getX() < width - 1 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() + 1][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == width - 1 && lastPoint.getY() < height - 1 && noise[0][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (lastPoint.getY() < height - 1 && noise[lastPoint.getX()][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() + 1);
                            lastDirection = Direction.South;
                        } 
                        else if (traceAngles && lastPoint.getX() > 0 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() - 1][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == 0 && lastPoint.getY() < height - 1 && noise[width - 1][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (lastPoint.getX() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && noise[width - 1][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (traceAngles && lastPoint.getX() > 0 && lastPoint.getY() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == 0 && lastPoint.getY() > 0 && noise[width - 1][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (lastPoint.getY() > 0 && noise[lastPoint.getX()][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() - 1);
                            lastDirection = Direction.North;
                        } 
                        else if (traceAngles && lastPoint.getX() < width - 1 && lastPoint.getY() > 0 && noise[lastPoint.getX() + 1][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == width - 1 && lastPoint.getY() > 0 && noise[0][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else
                            traceComplete = true;
                        break;
                    case SouthWest:
                        // check SE S SW W NW N NE E
                        // first hit that is <= value is the direction to move
                        if (traceAngles && lastPoint.getX() < width - 1 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() + 1][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == width - 1 && lastPoint.getY() < height - 1 && noise[0][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (lastPoint.getY() < height - 1 && noise[lastPoint.getX()][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() + 1);
                            lastDirection = Direction.South;
                        } 
                        else if (traceAngles && lastPoint.getX() > 0 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() - 1][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == 0 && lastPoint.getY() < height - 1 && noise[width - 1][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (lastPoint.getX() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && noise[width - 1][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (traceAngles && lastPoint.getX() > 0 && lastPoint.getY() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == 0 && lastPoint.getY() > 0 && noise[width - 1][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (lastPoint.getY() > 0 && noise[lastPoint.getX()][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() - 1);
                            lastDirection = Direction.North;
                        } 
                        else if (traceAngles && lastPoint.getX() < width - 1 && lastPoint.getY() > 0 && noise[lastPoint.getX() + 1][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == width - 1 && lastPoint.getY() > 0 && noise[0][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (lastPoint.getX() < width - 1 && noise[lastPoint.getX() + 1][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && noise[0][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(0, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else
                            traceComplete = true;
                        break;
                    case West:
                        // check S SW W NW N NE E SE
                        // first hit that is <= value is the direction to move
                        if (lastPoint.getY() < height - 1 && noise[lastPoint.getX()][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() + 1);
                            lastDirection = Direction.South;
                        } 
                        else if (traceAngles && lastPoint.getX() > 0 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() - 1][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == 0 && lastPoint.getY() < height - 1 && noise[width - 1][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthWest;
                        } 
                        else if (lastPoint.getX() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (wrap && lastPoint.getX() == 0 && noise[width - 1][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY());
                            lastDirection = Direction.West;
                        } 
                        else if (traceAngles && lastPoint.getX() > 0 && lastPoint.getY() > 0 && noise[lastPoint.getX() - 1][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == 0 && lastPoint.getY() > 0 && noise[width - 1][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(width - 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthWest;
                        }
                        else if (lastPoint.getY() > 0 && noise[lastPoint.getX()][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX(), lastPoint.getY() - 1);
                            lastDirection = Direction.North;
                        } 
                        else if (traceAngles && lastPoint.getX() < width - 1 && lastPoint.getY() > 0 && noise[lastPoint.getX() + 1][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == width - 1 && lastPoint.getY() > 0 && noise[0][lastPoint.getY() - 1] == value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() - 1);
                            lastDirection = Direction.NorthEast;
                        } 
                        else if (lastPoint.getX() < width - 1 && noise[lastPoint.getX() + 1][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (wrap && lastPoint.getX() == width - 1 && noise[0][lastPoint.getY()] == value)
                        {
                            lastPoint = new Point(0, lastPoint.getY());
                            lastDirection = Direction.East;
                        } 
                        else if (traceAngles && lastPoint.getX() < width - 1 && lastPoint.getY() < height - 1 && noise[lastPoint.getX() + 1][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(lastPoint.getX() + 1, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else if (traceAngles && wrap && lastPoint.getX() == width - 1 && lastPoint.getY() < height - 1 && noise[0][lastPoint.getY() + 1] == value)
                        {
                            lastPoint = new Point(0, lastPoint.getY() + 1);
                            lastDirection = Direction.SouthEast;
                        } 
                        else
                            traceComplete = true;
                        break;
                    }
                }
            }
        } catch (Exception e)
        {
            points = new ArrayList<Point>();
        }

        return points;
    }

    // method of finding unsorted points for boundaries given a value
    public static List<Point> valueBoundaries(double[][] noise, double value)
    {
        List<Point> points = new ArrayList<Point>();

        for (int x = 0; x < noise.length; x++)
        {
            for (int y = 0; y < noise[x].length; y++)
            {
                double noiseValue = noise[x][y];
                if (noiseValue == value)
                {
                    double n = y > 0 ? noise[x][y - 1] : Double.NaN;
                    double s = y < noise[x].length - 1 ? noise[x][y + 1] : Double.NaN;
                    double e = x < noise.length - 1 ? noise[x + 1][y] : noise[0][y];
                    double w = x > 0 ? noise[x - 1][y] : noise[noise.length - 1][y];
                    double nw = x > 0 && y > 0 ? noise[x - 1][y - 1] : Double.NaN;
                    double ne = x < noise.length - 1 && y > 0 ? noise[x + 1][y - 1] : Double.NaN;
                    double sw = x > 0 && y < noise[x].length - 1 ? noise[x - 1][y + 1] : Double.NaN;
                    double se = x < noise.length - 1 && y < noise[x].length - 1 ? noise[x + 1][y + 1] : Double.NaN;

                    if (n != value || s != value || e != value || w != value || nw != value || ne != value || sw != value || se != value)
                    {
                        points.add(new Point(x, y));
                    }
                }
            }
        }

        Collections.sort(points, new Comparator<Point>()
        {
            public int compare(Point x1, Point x2)
            {
                int result = Integer.compare(x1.getX(), x2.getX());
                if (result == 0)
                {
                    // both X are equal -> compare Y too
                    result = Integer.compare(x1.getY(), x2.getY());
                }
                return result;
            }
        });

        return points;
    }
}
