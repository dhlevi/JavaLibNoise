package ca.dhlevi.libnoise.operator;

public class ControlPoint implements Comparable<ControlPoint> 
{
	private double x;
	private double y;
	
	public ControlPoint()
	{
	}
	
	public ControlPoint(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public double getX() 
	{
		return x;
	}

	public void setX(double x) 
	{
		this.x = x;
	}

	public double getY() 
	{
		return y;
	}

	public void setY(double y) 
	{
		this.y = y;
	}

	public int compareTo(ControlPoint o) 
	{
		return new Double(this.x).compareTo(new Double(((ControlPoint)o).getX()));
	}
}
