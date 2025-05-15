package engine.components.lighting;

import engine.core.Matrix4f;

public class ShadowData {

    private Matrix4f projection;
    private float bias;
    private boolean flipFaces;

    public ShadowData(Matrix4f projection, float bias, boolean flipFaces) {
        this.projection = projection;
        this.bias = bias;
        this.flipFaces = flipFaces;
    }

    public Matrix4f getProjection() {
        return projection;
    }

    public float getBias() {
        return bias;
    }

    public boolean getFlipFaces() {
        return flipFaces;
    }
}
