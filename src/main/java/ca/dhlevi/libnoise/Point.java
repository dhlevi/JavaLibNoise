package ca.dhlevi.libnoise;

public class Point
{
	private int x;
	private int y;

	public Point()
	{
	}

	public Point(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public boolean equals(Point p)
	{
		if(p == null) return false;
		return (p.getX() == this.getX() && p.getY() == this.getY());
	}

	public int compareTo(Point o)
	{
		return new Integer(this.x).compareTo(new Integer(((Point)o).getX()));
	}
}
