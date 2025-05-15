package engine.components;

import engine.core.CoreEngine;
import engine.core.Matrix4f;
import engine.core.Vector3f;

public class Camera extends GameComponent {

    private Matrix4f projection;

    public Camera(Matrix4f projection) {
        this.projection = projection;
    }

    public Camera(float fov, float aspect, float zNear, float zFar) {
        this.projection = new Matrix4f().perspective(fov, aspect, zNear, zFar);
    }

    public Matrix4f getViewProjection() {
        Matrix4f cameraRotation = getTransform().getTransformedRotation().conjugate().toRotationMatrix();
        Vector3f cameraPos = getTransform().getTransformedPosition().mul(-1);
        Matrix4f cameraTranslation = new Matrix4f().translation(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());
        return projection.mul(cameraRotation.mul(cameraTranslation));
    }

    public void setProjection(Matrix4f projection) {
        this.projection = projection;
    }

    @Override
    public void addToEngine(CoreEngine engine) {
        engine.getRenderingEngine().setMainCamera(this);
    }
}