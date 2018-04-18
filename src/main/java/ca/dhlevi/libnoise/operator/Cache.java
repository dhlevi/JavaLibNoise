package ca.dhlevi.libnoise.operator;

import ca.dhlevi.libnoise.Module;

public class Cache extends Operator
{
    public Cache(Module input)
    {
        this.getModules().add(input);
    }

    @Override
    public double getValue()
    {
        return getValue(0, 0, 0, 1);
    }

    @Override
    public double getValue(double x, double y, double z, int scale)
    {
        return this.getCachedModule().getValue(x, y, z, scale);
    }

    public Module getCachedModule()
    {
        return this.getModules().get(0);
    }

    public void setCachedModule(Module input)
    {
        this.getModules().set(0, input);
    }
}
