package engine.components;

import engine.core.InputHandler;
import engine.core.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class FreeMove extends GameComponent {

    private static final InputHandler input = InputHandler.getInstance();
    private float speed;
    private int forwardKey;
    private int backKey;
    private int leftKey;
    private int rightKey;

    public FreeMove(float speed) {
        this(speed, GLFW_KEY_W, GLFW_KEY_S, GLFW_KEY_A, GLFW_KEY_D);
    }

    public FreeMove(float speed, int forwardKey, int backKey, int leftKey, int rightKey) {
        this.speed = speed;
        this.forwardKey = forwardKey;
        this.backKey = backKey;
        this.leftKey = leftKey;
        this.rightKey = rightKey;
    }

    @Override
    public void input(float delta) {
        float movAmt = speed * delta;
        if(input.isKeyDown(forwardKey))
            move(getTransform().getRotation().getForward(), movAmt);
        if(input.isKeyDown(backKey))
            move(getTransform().getRotation().getForward(), -movAmt);
        if(input.isKeyDown(leftKey))
            move(getTransform().getRotation().getLeft(), movAmt);
        if(input.isKeyDown(rightKey))
            move(getTransform().getRotation().getRight(), movAmt);
    }

    private void move(Vector3f dir, float amt) {
        getTransform().setPosition(getTransform().getPosition().add(dir.mul(amt)));
    }
}
