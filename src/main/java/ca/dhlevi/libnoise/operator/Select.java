package ca.dhlevi.libnoise.operator;

import ca.dhlevi.libnoise.Module;
import ca.dhlevi.libnoise.Utilities;

public class Select extends Operator
{
    private double fallOff;
    private double raw;
    private double min = -1.0;
    private double max = 1.0;

    public Select(Module inputA, Module inputB, Module controller)
    {
        this.min = 0.0;
        this.max = 0.0;
        this.fallOff = 0.0;

        this.getModules().add(0, inputA);
        this.getModules().add(1, inputB);
        this.getModules().add(2, controller);
    }

    public Select(Module inputA, Module inputB, Module controller, double min, double max, double fallOff)
    {
        this.min = min;
        this.max = max;
        this.fallOff = fallOff;

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
        double cv = this.getController().getValue(x, y, z, scale);

        if (fallOff > 0.0)
        {
            double a;

            if (cv < (min - fallOff))
                return this.getInputA().getValue(x, y, z, scale);

            if (cv < (min + fallOff))
            {
                double lc = (min - fallOff);
                double uc = (min + fallOff);

                a = Utilities.mapCubicSCurve((cv - lc) / (uc - lc));

                return Utilities.interpolateLinear(this.getInputA().getValue(x, y, z, scale), this.getInputB().getValue(x, y, z, scale), a);
            }

            if (cv < (max - fallOff))
                return this.getInputB().getValue(x, y, z, scale);

            if (cv < (max + fallOff))
            {
                double lc = (max - fallOff);
                double uc = (max + fallOff);

                a = Utilities.mapCubicSCurve((cv - lc) / (uc - lc));

                return Utilities.interpolateLinear(this.getInputB().getValue(x, y, z, scale), this.getInputA().getValue(x, y, z, scale), a);
            }

            return this.getInputA().getValue(x, y, z, scale);
        }

        if (cv < min || cv > max)
            return this.getInputA().getValue(x, y, z, scale);

        return this.getInputB().getValue(x, y, z, scale);
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

    public double getFallOff()
    {
        return fallOff;
    }

    public void setFallOff(double fallOff)
    {
        double bs = max - min;
        raw = fallOff;
        this.fallOff = (fallOff > bs / 2) ? bs / 2 : fallOff;
    }

    public double getRaw()
    {
        return raw;
    }

    public void setRaw(double raw)
    {
        this.raw = raw;
    }

    public double getMin()
    {
        return min;
    }

    public void setMin(double min)
    {
        this.min = min;
        this.fallOff = raw;
    }

    public double getMax()
    {
        return max;
    }

    public void setMax(double max)
    {
        this.max = max;
        this.fallOff = raw;
    }
}
