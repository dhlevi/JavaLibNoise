package ca.dhlevi.libnoise.operator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.dhlevi.libnoise.Module;
import ca.dhlevi.libnoise.Utilities;

public class Curve extends Operator
{
    private List<ControlPoint> controlPoints;

    public Curve(Module input)
    {
        controlPoints = new ArrayList<ControlPoint>();
        this.getModules().add(input);
    }

    public Curve(Module input, List<ControlPoint> controlPoints)
    {
        this.controlPoints = controlPoints;
        this.getModules().add(input);
    }

    public void add(double input, double output)
    {
        add(new ControlPoint(input, output));
    }

    public void add(ControlPoint controlPoint)
    {
        if (!controlPoints.contains(controlPoint))
        {
            controlPoints.add(controlPoint);
        }

        Collections.sort(controlPoints);
    }

    public void clear()
    {
        getControlPoints().clear();
        controlPoints = new ArrayList<ControlPoint>();
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
        for (int i = 0; i < controlPoints.size(); i++)
        {
            if (smv < controlPoints.get(i).getX())
                break;
            ip++;
        }

        int i0 = Utilities.clamp(ip - 2, 0, controlPoints.size() - 1);
        int i1 = Utilities.clamp(ip - 1, 0, controlPoints.size() - 1);
        int i2 = Utilities.clamp(ip, 0, controlPoints.size() - 1);
        int i3 = Utilities.clamp(ip + 1, 0, controlPoints.size() - 1);

        if (i1 == i2)
            return controlPoints.get(i1).getY();

        double ip0 = controlPoints.get(i1).getX();
        double ip1 = controlPoints.get(i2).getX();
        double a = (smv - ip0) / (ip1 - ip0);

        return Utilities.interpolateCubic(controlPoints.get(i0).getY(), controlPoints.get(i1).getY(), controlPoints.get(i2).getY(), controlPoints.get(i3).getY(), a);
    }

    public List<ControlPoint> getControlPoints()
    {
        if (controlPoints == null)
            controlPoints = new ArrayList<ControlPoint>();
        return controlPoints;
    }

    public void setControlPoints(List<ControlPoint> controlPoints)
    {
        this.controlPoints = controlPoints;
    }
}
