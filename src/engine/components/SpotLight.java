package engine.components;

import engine.core.Vector3f;
import engine.rendering.Attenuation;
import engine.rendering.Shader;

public class SpotLight extends PointLight {

    float cutoff;

    public SpotLight(Vector3f color, float intensity, Attenuation atten, float cutoff) {
        super(color, intensity, atten);
        this.cutoff = cutoff;
        setShader(new Shader("fr-spot"));
    }

    public Vector3f getDirection() {
        return getTransform().getTransformedRotation().getForward();
    }

    public float getCutoff() {
        return cutoff;
    }

    public void setCutoff(float cutoff) {
        this.cutoff = cutoff;
    }
}
