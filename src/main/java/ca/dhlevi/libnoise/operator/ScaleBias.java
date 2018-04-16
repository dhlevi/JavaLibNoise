package ca.dhlevi.libnoise.operator;

import ca.dhlevi.libnoise.Module;

public class ScaleBias extends Operator
{
	private double scale;
	private double bias;
	
	public ScaleBias(Module input)
	{
		this.scale = 1.0;
		this.bias = 1.0;
		this.getModules().add(0, input);
	}
	
	public ScaleBias(Module input, double scale, double bias)
	{
		this.scale = scale;
		this.bias = bias;
		this.getModules().add(0, input);
	}
	
	@Override
	public double getValue() 
	{
		return getValue(0, 0, 0, 1);
	}
	
	@Override
	public double getValue(double x, double y, double z, int scale)
    {
        return this.getModules().get(0).getValue(x, y, z, scale) * scale + bias;
    }

	public double getScale() 
	{
		return scale;
	}

	public void setScale(double scale) 
	{
		this.scale = scale;
	}

	public double getBias() 
	{
		return bias;
	}

	public void setBias(double bias) 
	{
		this.bias = bias;
	}
}
