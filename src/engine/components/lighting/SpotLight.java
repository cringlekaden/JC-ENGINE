package engine.components.lighting;

import engine.core.Matrix4f;
import engine.core.Vector3f;
import engine.rendering.Attenuation;
import engine.rendering.Shader;

public class SpotLight extends PointLight {

    private float cutoff;

    public SpotLight(Vector3f color, float intensity, Attenuation atten) {
        this(color, intensity, atten, (float)Math.toRadians(170.0f));
    }

    public SpotLight(Vector3f color, float intensity, Attenuation atten, float viewAngle) {
        this(color, intensity, atten, viewAngle, 10, 1.0f, 0.2f, 0.00002f);
    }

    public SpotLight(Vector3f color, float intensity, Attenuation atten, float viewAngle, int shadowMapSizeAsPowerOf2) {
        this(color, intensity, atten, viewAngle, shadowMapSizeAsPowerOf2, 1.0f, 0.2f, 0.00002f);
    }

    public SpotLight(Vector3f color, float intensity, Attenuation atten, float viewAngle, int shadowMapSizeAsPowerOf2, float shadowSoftness, float lightBleedReduction, float minVariance) {
        super(color, intensity, atten);
        this.cutoff = (float)Math.cos(viewAngle / 2.0f);
        setShader(new Shader("fr-spot"));
        if(shadowMapSizeAsPowerOf2 != 0)
            setShadowData(new ShadowData(new Matrix4f().perspective(viewAngle, 1.0f, 0.1f, getRange()), false, shadowMapSizeAsPowerOf2, shadowSoftness, lightBleedReduction, minVariance));
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
