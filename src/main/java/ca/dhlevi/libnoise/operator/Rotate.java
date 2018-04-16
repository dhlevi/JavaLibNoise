package ca.dhlevi.libnoise.operator;

import ca.dhlevi.libnoise.Module;
import ca.dhlevi.libnoise.Utilities;

public class Rotate extends Operator 
{
	private double x;
    private double x1Matrix;
    private double x2Matrix;
    private double x3Matrix;
    private double y;
    private double y1Matrix;
    private double y2Matrix;
    private double y3Matrix;
    private double z;
    private double z1Matrix;
    private double z2Matrix;
    private double z3Matrix;
    
	public Rotate(Module input)
	{
		setAngles(0.0, 0.0, 0.0);
		this.getModules().add(input);
	}
	
	public Rotate(Module input, double x, double y, double z)
	{
		setAngles(x, y, x);
		this.getModules().add(input);
	}
	
	private void setAngles(double x, double y, double z)
    {
        double xc = Math.cos(x * Utilities.degreesToRadians());
        double yc = Math.cos(y * Utilities.degreesToRadians());
        double zc = Math.cos(z * Utilities.degreesToRadians());
        double xs = Math.sin(x * Utilities.degreesToRadians());
        double ys = Math.sin(y * Utilities.degreesToRadians());
        double zs = Math.sin(z * Utilities.degreesToRadians());

        x1Matrix = ys * xs * zs + yc * zc;
        y1Matrix = xc * zs;
        z1Matrix = ys * zc - yc * xs * zs;
        x2Matrix = ys * xs * zc - yc * zs;
        y2Matrix = xc * zc;
        z2Matrix = -yc * xs * zc - ys * zs;
        x3Matrix = -ys * xc;
        y3Matrix = xs;
        z3Matrix = yc * xc;

        this.x = x;
        this.y = y;
        this.z = z;
    }
	
	@Override
	public double getValue() 
	{
		return getValue(0, 0, 0, 1);
	}

	@Override
	public double getValue(double x, double y, double z, int scale) 
	{
		double nx = (x1Matrix * x) + (y1Matrix * y) + (z1Matrix * z);
        double ny = (x2Matrix * x) + (y2Matrix * y) + (z2Matrix * z);
        double nz = (x3Matrix * x) + (y3Matrix * y) + (z3Matrix * z);

        return this.getModules().get(0).getValue(nx, ny, nz, scale);
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
