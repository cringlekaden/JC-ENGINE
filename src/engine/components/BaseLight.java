package engine.components;

import engine.core.CoreEngine;
import engine.core.Vector3f;
import engine.rendering.Shader;

public class BaseLight extends GameComponent {

    private Shader shader;
    private Vector3f color;
    private float intensity;

    public BaseLight(Vector3f color, float intensity) {
        this.color = color;
        this.intensity = intensity;
    }

    @Override
    public void addToEngine(CoreEngine engine) {
        engine.getRenderingEngine().addLight(this);
    }

    public Shader getShader() {
        return shader;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
