package ca.dhlevi.libnoise;

public class Coordinate
{
    private double x;
    private double y;

    public Coordinate()
    {
    }

    public Coordinate(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public double getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public boolean equals(Coordinate p)
    {
        if (p == null)
            return false;
        
        return (p.getX() == this.getX() && p.getY() == this.getY());
    }

    public int compareTo(Coordinate o)
    {
        return new Double(this.x).compareTo(new Double(((Coordinate) o).getX()));
    }
}
