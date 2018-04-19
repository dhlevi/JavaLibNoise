package ca.dhlevi.libnoise.spatial;

public class Envelope
{
    private Coordinate min;
    private Coordinate max;

    public Envelope(double minX, double minY, double maxX, double maxY)
    {
        this.min = new Coordinate(minX, minY);
        this.max = new Coordinate(maxX, maxY);
    }
    
    public Envelope(Coordinate inMin, Coordinate inMax)
    {
        this.min = inMin;
        this.max = inMax;
    }

    public double getMinX()
    {
        return min.getX();
    }
    
    public double getMaxX()
    {
        return max.getX();
    }
    
    public double getMinY()
    {
        return min.getY();
    }
    
    public double getMaxY()
    {
        return max.getY();
    }
    
    public Coordinate getMin()
    {
        return min;
    }

    public void setMin(Coordinate min)
    {
        this.min = min;
    }

    public Coordinate getMax()
    {
        return max;
    }

    public void setMax(Coordinate max)
    {
        this.max = max;
    }
}
