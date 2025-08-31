package engine.components.lighting;

import engine.core.Matrix4f;
import engine.core.Vector3f;
import engine.rendering.Shader;

public class DirectionalLight extends BaseLight {

    public DirectionalLight(Vector3f color, float intensity, int shadowMapSizeAsPowerOf2) {
        this(color, intensity, shadowMapSizeAsPowerOf2, 80, 1.0f, 0.2f, 0.00002f);
    }

    public DirectionalLight(Vector3f color, float intensity, int shadowMapSizeAsPowerOf2, float shadowArea, float shadowSoftness, float lightBleedReduction, float minVariance) {
        super(color, intensity);
        setShader(new Shader("fr-directional"));
        float halfShadowArea = shadowArea / 2.0f;
        if(shadowMapSizeAsPowerOf2 != 0)
            setShadowData(new ShadowData(new Matrix4f().orthographic(-halfShadowArea, halfShadowArea, -halfShadowArea, halfShadowArea, -halfShadowArea, halfShadowArea), true, shadowMapSizeAsPowerOf2, shadowSoftness, lightBleedReduction, minVariance));
    }

    public Vector3f getDirection() {
        return getTransform().getTransformedRotation().getForward();
    }
}
