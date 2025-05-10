package engine.components;

import engine.core.Vector3f;
import engine.rendering.Shader;

public class DirectionalLight extends BaseLight {

    public DirectionalLight(Vector3f color, float intensity) {
        super(color, intensity);
        setShader(new Shader("fr-directional"));
    }

    public Vector3f getDirection() {
        return getTransform().getTransformedRotation().getForward();
    }
}
