package engine.components.lighting;

import engine.core.Matrix4f;

public class ShadowData {

    private Matrix4f projection;
    private int shadowMapSizeAsPowerOf2;
    private float minVariance;
    private float lightBleedReduction;
    private float shadowSoftness;
    private boolean flipFaces;

    public ShadowData(Matrix4f projection, boolean flipFaces, int shadowMapSizeAsPowerOf2) {
        this(projection, flipFaces, shadowMapSizeAsPowerOf2, 1.0f, 0.2f, 0.00002f);
    }

    public ShadowData(Matrix4f projection, boolean flipFaces, int shadowMapSizeAsPowerOf2, float shadowSoftness) {
        this(projection, flipFaces, shadowMapSizeAsPowerOf2, shadowSoftness, 0.2f, 0.00002f);
    }

    public ShadowData(Matrix4f projection, boolean flipFaces, int shadowMapSizeAsPowerOf2, float shadowSoftness, float lightBleedReduction, float minVariance) {
        this.projection = projection;
        this.flipFaces = flipFaces;
        this.shadowMapSizeAsPowerOf2 = shadowMapSizeAsPowerOf2;
        this.shadowSoftness = shadowSoftness;
        this.lightBleedReduction = lightBleedReduction;
        this.minVariance = minVariance;
    }

    public Matrix4f getProjection() {
        return projection;
    }

    public boolean getFlipFaces() {
        return flipFaces;
    }

    public int getShadowMapSizeAsPowerOf2() {
        return shadowMapSizeAsPowerOf2;
    }

    public float getMinVariance() {
        return minVariance;
    }

    public float getLightBleedReduction() {
        return lightBleedReduction;
    }

    public float getShadowSoftness() {
        return shadowSoftness;
    }
}
