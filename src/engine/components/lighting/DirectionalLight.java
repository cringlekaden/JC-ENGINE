package engine.components.lighting;

import engine.core.Matrix4f;
import engine.core.Vector3f;
import engine.rendering.Shader;

public class DirectionalLight extends BaseLight {

    public DirectionalLight(Vector3f color, float intensity) {
        super(color, intensity);
        setShader(new Shader("fr-directional"));
        setShadowData(new ShadowData(new Matrix4f().orthographic(-40, 40, -40, 40, -40, 40), 3.0f, true));
    }

    public Vector3f getDirection() {
        return getTransform().getTransformedRotation().getForward();
    }
}
