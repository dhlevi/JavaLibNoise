package ca.dhlevi.libnoise.generator;

import ca.dhlevi.libnoise.Utilities;

public class Checker extends Generator 
{
	public Checker()
	{
	}
	
	@Override
	public double getValue() 
	{
		return getValue(0, 0, 0, 1);
	}
	
	@Override
    public double getValue(double x, double y, double z, int scale)
    {
        int ix = (int)(Math.floor(Utilities.makeInt32Range(x)));
        int iy = (int)(Math.floor(Utilities.makeInt32Range(y)));
        int iz = (int)(Math.floor(Utilities.makeInt32Range(z)));

        return (ix & 1 ^ iy & 1 ^ iz & 1) != 0 ? -1.0 : 1.0;
    }
}
