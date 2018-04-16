package ca.dhlevi.libnoise.operator;

import ca.dhlevi.libnoise.Module;
import ca.dhlevi.libnoise.Utilities;

public class Blend extends Operator
{
	public Blend(Module inputA, Module inputB, Module controller)
	{
		this.getModules().add(0, inputA);
		this.getModules().add(1, inputB);
		this.getModules().add(2, controller);
	}
	
	@Override
	public double getValue() 
	{
		return getValue(0, 0, 0, 1);
	}

	@Override
	public double getValue(double x, double y, double z, int scale) 
	{
		double a = getInputA().getValue(x, y, z, scale);
        double b = getInputB().getValue(x, y, z, scale);
        double c = (getController().getValue(x, y, z, scale) + 1.0) / 2.0;

        return Utilities.interpolateLinear(a, b, c);
	}
	
	public Module getInputA()
	{
		return this.getModules().get(0);
	}
	
	public void setInputA(Module inputA)
	{
		this.getModules().set(0, inputA);
	}
	
	public Module getInputB()
	{
		return this.getModules().get(1);
	}
	
	public void setInputB(Module inputB)
	{
		this.getModules().set(1, inputB);
	}
	
	public Module getController()
	{
		return this.getModules().get(2);
	}
	
	public void setController(Module controller)
	{
		this.getModules().set(2, controller);
	}
}
