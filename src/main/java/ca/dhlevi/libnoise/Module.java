package ca.dhlevi.libnoise;

import java.util.ArrayList;
import java.util.List;

import ca.dhlevi.libnoise.generator.Perlin;
import ca.dhlevi.libnoise.operator.Abs;
import ca.dhlevi.libnoise.operator.Add;
import ca.dhlevi.libnoise.operator.Cache;
import ca.dhlevi.libnoise.operator.Clamp;
import ca.dhlevi.libnoise.operator.ControlPoint;
import ca.dhlevi.libnoise.operator.Curve;
import ca.dhlevi.libnoise.operator.Exponent;
import ca.dhlevi.libnoise.operator.Invert;
import ca.dhlevi.libnoise.operator.Max;
import ca.dhlevi.libnoise.operator.Min;
import ca.dhlevi.libnoise.operator.Multiply;
import ca.dhlevi.libnoise.operator.Power;
import ca.dhlevi.libnoise.operator.Rotate;
import ca.dhlevi.libnoise.operator.Scale;
import ca.dhlevi.libnoise.operator.ScaleBias;
import ca.dhlevi.libnoise.operator.Subtract;
import ca.dhlevi.libnoise.operator.Terrace;
import ca.dhlevi.libnoise.operator.Translate;
import ca.dhlevi.libnoise.operator.Turbulence;

public abstract class Module
{
    public enum QualityMode
    {
        Low, Medium, High
    }

    private List<Module> modules;

    public Module()
    {

    }

    public List<Module> getModules()
    {
        if (modules == null)
            modules = new ArrayList<Module>();
        return modules;
    }

    public void setModules(List<Module> operators)
    {
        this.modules = operators;
    }

    public abstract double getValue();

    public abstract double getValue(double x, double y, double z, int scale);

    // Simple operators

    public Abs abs()
    {
        return new Abs(this);
    }

    public Add add(Module inputB)
    {
        return new Add(this, inputB);
    }

    public Cache cache()
    {
        return new Cache(this);
    }

    public Clamp clamp(double max, double min)
    {
        return new Clamp(this, max, min);
    }

    public Curve curve(List<ControlPoint> controlPoints)
    {
        return new Curve(this, controlPoints);
    }

    public Exponent exponent(double value)
    {
        return new Exponent(this, value);
    }

    public Invert invert()
    {
        return new Invert(this);
    }

    public Max max(Module rightHand)
    {
        return new Max(this, rightHand);
    }

    public Min min(Module rightHand)
    {
        return new Min(this, rightHand);
    }

    public Multiply multiply(Module rightHand)
    {
        return new Multiply(this, rightHand);
    }

    public Power power(Module rightHand)
    {
        return new Power(this, rightHand);
    }

    public Rotate rotate(double x, double y, double z)
    {
        return new Rotate(this, x, y, z);
    }

    public Scale scale(double x, double y, double z)
    {
        return new Scale(this, x, y, z);
    }

    public ScaleBias scaleBias(double scale, double bias)
    {
        return new ScaleBias(this, scale, bias);
    }

    public Subtract subtract(Module rightHand)
    {
        return new Subtract(this, rightHand);
    }

    public Terrace terrace(boolean isInverted, List<Double> controlPoints)
    {
        return new Terrace(this, isInverted, controlPoints);
    }

    public Translate translate(double x, double y, double z)
    {
        return new Translate(this, x, y, z);
    }

    public Turbulence turbulence(double frequency, double power, double roughness, int seed)
    {
        return new Turbulence(this, frequency, power, roughness, seed);
    }

    public Turbulence turbulence(Perlin x, Perlin y, Perlin z, double frequency, double power, double roughness, int seed)
    {
        return new Turbulence(this, x, y, z, frequency, power, roughness, seed);
    }
}