package ca.dhlevi.libnoise.operator;

import ca.dhlevi.libnoise.Module;

public class Min extends Operator 
{
	public Min(Module leftHand, Module rightHand)
	{
		this.getModules().add(0, leftHand);
		this.getModules().add(1, rightHand);
	}
	
	@Override
	public double getValue() 
	{
		return getValue(0, 0, 0, 1);
	}

	@Override
	public double getValue(double x, double y, double z, int scale)
	{
		double a = getLeftHand().getValue(x, y, z, scale);
        double b = getRightHand().getValue(x, y, z, scale);

        return Math.min(a, b);
	}

	public Module getLeftHand()
	{
		return this.getModules().get(0);
	}
	
	public void setLeftHand(Module input)
	{
		this.getModules().add(0, input);
	}
	
	public Module getRightHand()
	{
		return this.getModules().get(1);
	}
	
	public void setRightHand(Module input)
	{
		this.getModules().add(1, input);
	}
}
