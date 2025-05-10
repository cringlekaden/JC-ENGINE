package engine.components;

import engine.core.InputHandler;
import engine.core.Vector2f;
import engine.core.Vector3f;
import engine.rendering.Window;

import static org.lwjgl.glfw.GLFW.*;

public class FreeLook extends GameComponent {

    private static final Vector3f yAxis = new Vector3f(0,1,0);
    private static final InputHandler input = InputHandler.getInstance();
    private boolean mouseLocked = false;
    private float sensitivity;
    private float deadzone;
    private int unlockMouseKey;

    public FreeLook(float sensitivity) {
        this(sensitivity, 5, GLFW_KEY_SPACE);
    }

    public FreeLook(float sensitivity, float deadzone, int unlockMouseKey) {
        this.sensitivity = sensitivity;
        this.deadzone = deadzone;
        this.unlockMouseKey = unlockMouseKey;
    }

    @Override
    public void input(float delta) {
        Vector2f centerPosition = new Vector2f((float)Window.getWidth()/2, (float)Window.getHeight()/2);
        if(input.isKeyDown(unlockMouseKey)) {
            input.setCursor(true);
            mouseLocked = false;
        }
        if(input.wasMouseButtonDown(GLFW_MOUSE_BUTTON_1)) {
            input.setMousePosition(centerPosition);
            input.setCursor(false);
            mouseLocked = true;
        }
        if(mouseLocked) {
            Vector2f deltaPos = input.getMousePosition().sub(centerPosition);
            boolean rotY = Math.abs(deltaPos.getX()) > deadzone;
            boolean rotX = Math.abs(deltaPos.getY()) > deadzone;
            if(rotY)
                getTransform().rotate(yAxis, (float) Math.toRadians(deltaPos.getX() * sensitivity));
            if(rotX)
                getTransform().rotate(getTransform().getRotation().getRight(), (float) Math.toRadians(deltaPos.getY() * sensitivity));
            if(rotY || rotX)
                input.setMousePosition(centerPosition);
        }
    }
}
