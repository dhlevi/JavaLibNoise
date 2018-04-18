package ca.dhlevi.libnoise.operator;

import ca.dhlevi.libnoise.Module;

public class Scale extends Operator
{
    private double x;
    private double y;
    private double z;

    public Scale(Module input)
    {
        this.x = 1;
        this.y = 1;
        this.z = 1;
        this.getModules().add(input);
    }

    public Scale(Module input, double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
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
        return this.getModules().get(0).getValue(x * this.x, y * this.y, z * this.z, scale);
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

    public double getZ()
    {
        return z;
    }

    public void setZ(double z)
    {
        this.z = z;
    }
}
