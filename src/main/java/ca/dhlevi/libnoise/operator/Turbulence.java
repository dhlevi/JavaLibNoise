package ca.dhlevi.libnoise.operator;

import ca.dhlevi.libnoise.Module;
import ca.dhlevi.libnoise.generator.Perlin;

public class Turbulence extends Operator
{
    private static double X0 = (12414.0 / 65536.0);
    private static double Y0 = (65124.0 / 65536.0);
    private static double Z0 = (31337.0 / 65536.0);
    private static double X1 = (26519.0 / 65536.0);
    private static double Y1 = (18128.0 / 65536.0);
    private static double Z1 = (60493.0 / 65536.0);
    private static double X2 = (53820.0 / 65536.0);
    private static double Y2 = (11213.0 / 65536.0);
    private static double Z2 = (44845.0 / 65536.0);

    private Perlin xDistort;
    private Perlin yDistort;
    private Perlin zDistort;

    private double frequency;
    private double power;
    private double roughness;
    private int seed;

    public Turbulence(Module input)
    {
        this.frequency = 1.0;
        this.power = 1.0;
        this.roughness = 1.0;
        this.xDistort = new Perlin();
        this.yDistort = new Perlin();
        this.zDistort = new Perlin();

        this.getModules().add(input);
    }

    public Turbulence(Module input, double frequency, double power, double roughness, int seed)
    {
        this.frequency = frequency;
        this.power = power;
        this.roughness = roughness;
        this.seed = seed;
        this.xDistort = new Perlin();
        this.yDistort = new Perlin();
        this.zDistort = new Perlin();

        this.getModules().add(input);
    }

    public Turbulence(Module input, Perlin x, Perlin y, Perlin z, double frequency, double power, double roughness, int seed)
    {
        this.frequency = frequency;
        this.power = power;
        this.roughness = roughness;
        this.seed = seed;
        this.xDistort = x;
        this.yDistort = y;
        this.zDistort = z;

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
        double xd = x + (xDistort.getValue(x + X0, y + Y0, z + Z0, scale) * power);
        double yd = y + (yDistort.getValue(x + X1, y + Y1, z + Z1, scale) * power);
        double zd = z + (zDistort.getValue(x + X2, y + Y2, z + Z2, scale) * power);

        return this.getModules().get(0).getValue(xd, yd, zd, scale);
    }

    public double getFrequency()
    {
        return frequency;
    }

    public void setFrequency(double frequency)
    {
        this.frequency = frequency;
    }

    public double getPower()
    {
        return power;
    }

    public void setPower(double power)
    {
        this.power = power;
    }

    public double getRoughness()
    {
        return roughness;
    }

    public void setRoughness(double roughness)
    {
        this.roughness = roughness;
    }

    public int getSeed()
    {
        return seed;
    }

    public void setSeed(int seed)
    {
        this.seed = seed;
    }
}
