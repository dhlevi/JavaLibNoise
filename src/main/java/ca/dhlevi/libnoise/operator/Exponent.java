package ca.dhlevi.libnoise.operator;

import ca.dhlevi.libnoise.Module;

public class Exponent extends Operator
{
    private double value;

    public Exponent(Module input, double value)
    {
        this.value = value;
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
        double v = this.getModules().get(0).getValue(x, y, z, scale);

        return (Math.pow(Math.abs((v + 1.0) / 2.0), this.value) * 2.0 - 1.0);
    }

    public double getExponentValue()
    {
        return value;
    }

    public void setExponentValue(double value)
    {
        this.value = value;
    }
}
