package ca.dhlevi.libnoise.generator;

public class Cylinder extends Generator 
{
	private double frequency;
	
	public Cylinder()
	{
		this.frequency = 1.0;
	}
	
	public Cylinder(double frequency)
	{
		this.frequency = frequency;
	}
	
	public double getFrequency()
	{
		return frequency;
	}
	
	public void setFrequency(double frequency)
	{
		this.frequency = frequency;
	}
	
	@Override
	public double getValue() 
	{
		return getValue(0, 0, 0, 1);
	}
	
	@Override
	public double getValue(double x, double y, double z, int scale)
    {
        x *= frequency;
        z *= frequency;

        double dfc = Math.sqrt(x * x + z * z);
        double dfss = dfc - Math.floor(dfc);
        double dfls = 1.0 - dfss;
        double nd = Math.min(dfss, dfls);
        
        return 1.0 - (nd * 4.0);
    }
}
