package engine.components.lighting;

import engine.core.Matrix4f;
import engine.core.Quaternion;
import engine.core.Vector3f;
import engine.rendering.Shader;

public class DirectionalLight extends BaseLight {

    public float halfShadowArea;

    public DirectionalLight(Vector3f color, float intensity, int shadowMapSizeAsPowerOf2) {
        this(color, intensity, shadowMapSizeAsPowerOf2, 80, 1.0f, 0.2f, 0.00002f);
    }

    public DirectionalLight(Vector3f color, float intensity, int shadowMapSizeAsPowerOf2, float shadowArea, float shadowSoftness, float lightBleedReduction, float minVariance) {
        super(color, intensity);
        setShader(new Shader("fr-directional"));
        halfShadowArea = shadowArea / 2.0f;
        if(shadowMapSizeAsPowerOf2 != 0)
            setShadowData(new ShadowData(new Matrix4f().orthographic(-halfShadowArea, halfShadowArea, -halfShadowArea, halfShadowArea, -halfShadowArea, halfShadowArea), true, shadowMapSizeAsPowerOf2, shadowSoftness, lightBleedReduction, minVariance));
    }

    @Override
    public ShadowCameraTransform calculateShadowCameraTransform(Vector3f mainCameraPosition, Quaternion mainCameraRotation) {
        ShadowCameraTransform result = new ShadowCameraTransform();
        result.position = mainCameraPosition.add(mainCameraRotation.getForward().mul(halfShadowArea));
        result.rotation = getTransform().getTransformedRotation();
        float worldTexelSize = (halfShadowArea * 2) / ((float) (1 << getShadowData().getShadowMapSizeAsPowerOf2()));
        Vector3f lightSpaceCameraPosition = result.position.rotate(result.rotation.conjugate());
        lightSpaceCameraPosition.setX(worldTexelSize * (float)Math.floor(lightSpaceCameraPosition.getX() / worldTexelSize));
        lightSpaceCameraPosition.setY(worldTexelSize * (float)Math.floor(lightSpaceCameraPosition.getY() / worldTexelSize));
        result.position = lightSpaceCameraPosition.rotate(result.rotation);
        return result;
    }

    public Vector3f getDirection() {
        return getTransform().getTransformedRotation().getForward();
    }
}
