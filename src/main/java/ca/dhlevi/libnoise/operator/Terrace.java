package ca.dhlevi.libnoise.operator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.dhlevi.libnoise.Module;
import ca.dhlevi.libnoise.Utilities;

public class Terrace extends Operator
{
	private List<Double> controlPoints;
	private boolean isInverted;
	
	public Terrace(Module input)
	{
		this.controlPoints = new ArrayList<Double>();
		this.isInverted = false;
		this.getModules().add(input);
	}
	
	public Terrace(Module input, boolean isInverted)
	{
		this.controlPoints = new ArrayList<Double>();
		this.isInverted = isInverted;
		this.getModules().add(input);
	}
	
	public Terrace(Module input, boolean isInverted, List<Double> controlPoints)
	{
		this.controlPoints = controlPoints;
		this.isInverted = isInverted;
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
		double smv = this.getModules().get(0).getValue(x, y, z, scale);

        int ip = 0;

        for(double cp : getControlPoints())
        {
            if (smv < cp) break;
            ip++;
        }

        int i0 = Utilities.clamp(ip - 1, 0, getControlPoints().size() - 1);
        int i1 = Utilities.clamp(ip, 0, getControlPoints().size() - 1);
         
        if (i0 == i1) return getControlPoints().get(i1);

        double v0 = getControlPoints().get(i0);
        double v1 = getControlPoints().get(i1);
        double a = (smv - v0) / (v1 - v0);

        if (isInverted)
        {
            a = 1.0 - a;

            double t = v0;
             
            v0 = v1;
            v1 = t;
        }

        a *= a;

        return Utilities.interpolateLinear(v0, v1, a);
    }
	
	private List<Double> getControlPoints() 
	{
		if(controlPoints == null) controlPoints = new ArrayList<Double>();
		return controlPoints;
	}

	public void add(double input)
    {
        if (!getControlPoints().contains(input)) getControlPoints().add(input);
        Collections.sort(getControlPoints());
    }

    /// <summary>
    /// Clears the control points.
    /// </summary>
    public void Clear()
    {
        controlPoints.clear();
        controlPoints = new ArrayList<Double>();
    }

    /// <summary>
    /// Auto-generates a terrace curve.
    /// </summary>
    /// <param name="steps">The number of steps.</param>
    public void Generate(int steps) throws Exception
    {
        if (steps < 2) throw new Exception("A minimum of two Control Points are required to process the Terrace operation.");
        
        Clear();

        double ts = 2.0 / (steps - 1.0);
        double cv = -1.0;
        
        for (int i = 0; i < steps; i++)
        {
            add(cv);
            cv += ts;
        }
    }

	public boolean isInverted() 
	{
		return isInverted;
	}

	public void isInverted(boolean isInverted) 
	{
		this.isInverted = isInverted;
	}
}
