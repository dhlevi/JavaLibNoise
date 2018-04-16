package ca.dhlevi.libnoise.generator;

import ca.dhlevi.libnoise.Utilities;

public class Perlin extends Generator 
{
	private double frequency;
	private double lacunarity;
	private QualityMode quality;
	private int octave;
	private double persistence;
	private int seed;
	
	public Perlin()
	{
		this.frequency = 1.0;
		this.lacunarity = 1.0;
		this.persistence =  0.5;
		this.octave = 5;
		this.seed = 1;
		this.quality = QualityMode.Medium;
	}

	public Perlin(double frequency, double lacunarity, double persistence, int octaves, int seed, QualityMode quality)
	{
		this.frequency = frequency;
		this.lacunarity = lacunarity;
		this.persistence = persistence;
		this.octave = octaves;
		this.seed = seed;
		this.quality = quality;
	}
	
	@Override
	public double getValue() 
	{
		return getValue(0, 0, 0, 1);
	}
	
	@Override
	public double getValue(double x, double y, double z, int scale)
    {
        double value = 0.0;
        double cp = 1.0;

        x *= frequency;
        y *= frequency;
        z *= frequency;
        
        for (int i = 0; i < octave + scale; i++)
        {
            double nx = Utilities.makeInt32Range(x);
            double ny = Utilities.makeInt32Range(y);
            double nz = Utilities.makeInt32Range(z);

            long modSeed = (seed + i) & 0xffffffff;
            double signal = Utilities.gradientCoherentNoise3D(nx, ny, nz, modSeed, quality);
            
            value += signal * cp;

            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            
            cp *= persistence;
        }

        return value;
    }
	
	public double getFrequency() 
	{
		return frequency;
	}

	public void setFrequency(double frequency) 
	{
		this.frequency = frequency;
	}

	public double getLacunarity() 
	{
		return lacunarity;
	}

	public void setLacunarity(double lacunarity) 
	{
		this.lacunarity = lacunarity;
	}

	public QualityMode getQuality() 
	{
		return quality;
	}

	public void setQuality(QualityMode quality) 
	{
		this.quality = quality;
	}

	public int getOctave() 
	{
		return octave;
	}

	public void setOctave(int octave) 
	{
		this.octave = Utilities.clamp(octave, 1, Utilities.MAX_OCTAVE);
	}

	public double getPersistence() 
	{
		return persistence;
	}

	public void setPersistence(double persistence) 
	{
		this.persistence = persistence;
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
