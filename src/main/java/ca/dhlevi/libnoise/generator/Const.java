package ca.dhlevi.libnoise.generator;

public class Const extends Generator 
{
	private double value;
	
	public Const()
	{
		this.value = 0;
	}
	
	public Const(double value)
	{
		this.value = value;
	}
	
	@Override
	public double getValue()
	{
		return value;
	}

	@Override
	public double getValue(double x, double y, double z, int scale) 
	{
		return value;
	}
}
