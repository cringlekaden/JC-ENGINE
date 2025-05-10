package engine.components;

import engine.core.*;
import engine.rendering.Window;

import static org.lwjgl.glfw.GLFW.*;

public class Camera extends GameComponent {

    private final Matrix4f projection;

    public Camera(float fov, float aspect, float zNear, float zFar) {
        this.projection = new Matrix4f().perspective(fov, aspect, zNear, zFar);
    }

    public Matrix4f getViewProjection() {
        Matrix4f cameraRotation = getTransform().getTransformedRotation().conjugate().toRotationMatrix();
        Vector3f cameraPos = getTransform().getTransformedPosition().mul(-1);
        Matrix4f cameraTranslation = new Matrix4f().translation(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());
        return projection.mul(cameraRotation.mul(cameraTranslation));
    }

    @Override
    public void addToEngine(CoreEngine engine) {
        engine.getRenderingEngine().setMainCamera(this);
    }
}