package ca.dhlevi.libnoise.operator;

import ca.dhlevi.libnoise.Module;

public class Clamp extends Operator 
{
	private double max;
	private double min;
	
	public Clamp(Module input)
	{
		this.max = 1.0;
		this.min = -1.0;
		this.getModules().add(input);
	}
	
	public Clamp(Module input, double max, double min)
	{
		setMax(max);
		setMin(min);
		this.getModules().add(input);
	}
	
	public void setBounds(double max, double min)
    {
		setMax(max);
		setMin(min);
    }
	
	@Override
	public double getValue() 
	{
		return getValue(0, 0, 0, 1);
	}

	@Override
	public double getValue(double x, double y, double z, int scale) 
	{
		if (min > max)
        {
            double t = min;
            min = max;
            max = t;
        }

        double v = this.getModules().get(0).getValue(x, y, z, scale);
        
        if (v < min) return min;
        if (v > max) return max;
        return v;
	}

	public double getMax() 
	{
		return max;
	}

	public void setMax(double max) 
	{
		this.max = max;
	}

	public double getMin() 
	{
		return min;
	}

	public void setMin(double min) 
	{
		this.min = min;
	}
}
