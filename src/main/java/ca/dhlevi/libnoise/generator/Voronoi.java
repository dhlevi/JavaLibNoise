package ca.dhlevi.libnoise.generator;

import ca.dhlevi.libnoise.Utilities;

public class Voronoi extends Generator
{
    private double displacement;
    private double frequency;
    private int seed;
    private boolean useDistance;

    public Voronoi()
    {
        this.displacement = 1.0;
        this.frequency = 1.0;
        this.seed = 0;
        this.useDistance = false;
    }

    public Voronoi(double frequency, double displacement, int seed, boolean distance)
    {
        this.displacement = frequency;
        this.frequency = displacement;
        this.seed = seed;
        this.useDistance = distance;
    }

    @Override
    public double getValue()
    {
        return getValue(0, 0, 0, 1);
    }

    @Override
    public double getValue(double x, double y, double z, int scale)
    {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        int xi = (x > 0.0 ? (int) x : (int) x - 1);
        int iy = (y > 0.0 ? (int) y : (int) y - 1);
        int iz = (z > 0.0 ? (int) z : (int) z - 1);

        double md = 2147483647.0;

        double xc = 0;
        double yc = 0;
        double zc = 0;

        for (int zcu = iz - 2; zcu <= iz + 2; zcu++)
        {
            for (int ycu = iy - 2; ycu <= iy + 2; ycu++)
            {
                for (int xcu = xi - 2; xcu <= xi + 2; xcu++)
                {
                    double xp = xcu + Utilities.valueNoise3D(xcu, ycu, zcu, seed);
                    double yp = ycu + Utilities.valueNoise3D(xcu, ycu, zcu, seed + 1);
                    double zp = zcu + Utilities.valueNoise3D(xcu, ycu, zcu, seed + 2);
                    double xd = xp - x;
                    double yd = yp - y;
                    double zd = zp - z;
                    double d = xd * xd + yd * yd + zd * zd;

                    if (d < md)
                    {
                        md = d;
                        xc = xp;
                        yc = yp;
                        zc = zp;
                    }
                }
            }
        }

        double v;

        if (useDistance)
        {
            double xd = xc - x;
            double yd = yc - y;
            double zd = zc - z;

            v = (Math.sqrt(xd * xd + yd * yd + zd * zd)) * Utilities.SQRT3 - 1.0;
        } else
        {
            v = 0.0;
        }

        return v + (displacement * Utilities.valueNoise3D((int) (Math.floor(xc)), (int) (Math.floor(yc)), (int) (Math.floor(zc)), 0));
    }

    public double getDisplacement()
    {
        return displacement;
    }

    public void setDisplacement(double displacement)
    {
        this.displacement = displacement;
    }

    public double getFrequency()
    {
        return frequency;
    }

    public void setFrequency(double frequency)
    {
        this.frequency = frequency;
    }

    public int getSeed()
    {
        return seed;
    }

    public void setSeed(int seed)
    {
        this.seed = seed;
    }

    public boolean useDistance()
    {
        return useDistance;
    }

    public void useDistance(boolean useDistance)
    {
        this.useDistance = useDistance;
    }
}
