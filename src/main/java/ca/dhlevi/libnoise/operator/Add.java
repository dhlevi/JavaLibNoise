package ca.dhlevi.libnoise.operator;

import ca.dhlevi.libnoise.Module;

public class Add extends Operator
{
    public Add(Module inputA, Module inputB)
    {
        this.getModules().add(0, inputA);
        this.getModules().add(1, inputB);
    }

    @Override
    public double getValue()
    {
        return getValue(0, 0, 0, 1);
    }

    @Override
    public double getValue(double x, double y, double z, int scale)
    {
        return getInputA().getValue(x, y, z, scale) + getInputB().getValue(x, y, z, scale);
    }

    public Module getInputA()
    {
        return this.getModules().get(0);
    }

    public Module getInputB()
    {
        return this.getModules().get(1);
    }

    public void setInputA(Module input)
    {
        this.getModules().set(0, input);
    }

    public void setInputB(Module input)
    {
        this.getModules().set(1, input);
    }
}
