package ca.dhlevi.libnoise.operator;

import ca.dhlevi.libnoise.Module;

public class Displace extends Operator
{
    public Displace(Module input, Module x, Module y, Module z)
    {
        this.getModules().add(0, input);
        this.getModules().add(1, x);
        this.getModules().add(2, y);
        this.getModules().add(3, z);
    }

    @Override
    public double getValue()
    {
        return getValue(0, 0, 0, 1);
    }

    @Override
    public double getValue(double x, double y, double z, int scale)
    {
        double dx = x + this.getX().getValue(x, y, z, scale);
        double dy = y + this.getY().getValue(x, y, z, scale);
        double dz = z + this.getZ().getValue(x, y, z, scale);

        return this.getInput().getValue(dx, dy, dz, scale);
    }

    public Module getInput()
    {
        return this.getModules().get(0);
    }

    public Module getX()
    {
        return this.getModules().get(1);
    }

    public Module getY()
    {
        return this.getModules().get(2);
    }

    public Module getZ()
    {
        return this.getModules().get(3);
    }

    public void setInput(Module input)
    {
        this.getModules().set(0, input);
    }

    public void setX(Module input)
    {
        this.getModules().set(1, input);
    }

    public void setY(Module input)
    {
        this.getModules().set(2, input);
    }

    public void setZ(Module input)
    {
        this.getModules().set(3, input);
    }
}
