package engine.components;

import engine.core.Vector3f;
import engine.rendering.Attenuation;
import engine.rendering.Shader;

public class PointLight extends BaseLight {

    private static final int COLOR_DEPTH = 256;

    private Attenuation atten;
    private float range;

    public PointLight(Vector3f color, float intensity, Attenuation atten) {
        super(color, intensity);
        this.atten = atten;
        float a = atten.getExponent();
        float b = atten.getLinear();
        float c = atten.getConstant() - COLOR_DEPTH * getIntensity() * getColor().max();
        this.range = (float) (-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a);
        setShader(new Shader("fr-point"));
    }

    public Attenuation getAttenuation() {
        return atten;
    }

    public void setAttenuation(Attenuation atten) {
        this.atten = atten;
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }
}
