package engine.core;

import engine.rendering.Window;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler implements GLFWKeyCallbackI {

    private static final InputHandler instance = new InputHandler();

    private static final boolean[] keys = new boolean[GLFW_KEY_LAST];
    private static final boolean[] mouseButtons = new boolean[GLFW_MOUSE_BUTTON_LAST];
    private static double mouseX, mouseY;
    private static double scrollX, scrollY;
    public final GLFWMouseButtonCallbackI mouseButtonCallback = ((window, button, action, mods) -> {
        if (button >= 0 && button < mouseButtons.length)
            mouseButtons[button] = action != GLFW_RELEASE;
    });
    public final GLFWCursorPosCallbackI cursorPosCallback = ((window, xpos, ypos) -> {
        mouseX = xpos;
        mouseY = ypos;
    });
    public final GLFWScrollCallbackI scrollCallback = ((window, xoffset, yoffset) -> {
        scrollX += xoffset;
        scrollY += yoffset;
    });

    public static InputHandler getInstance() {
        return instance;
    }

    public boolean isKeyDown(int key) {
        return key >= 0 && key < keys.length && keys[key];
    }

    public boolean isMouseButtonDown(int mouseButton) {
        return mouseButton >= 0 && mouseButton < mouseButtons.length && mouseButtons[mouseButton];
    }

    public boolean wasMouseButtonDown(int mouseButton) {
        return glfwGetMouseButton(Window.window, mouseButton) == GLFW_PRESS;
    }

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        if (key >= 0 && key < keys.length) {
            keys[key] = action != GLFW_RELEASE;
            if (key == GLFW_KEY_ESCAPE)
                glfwSetWindowShouldClose(window, true);
        }
    }

    public void update() {
        scrollX = 0;
        scrollY = 0;
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }

    public Vector2f getMousePosition() {
        return new Vector2f((float) mouseX, (float) mouseY);
    }

    public void setMousePosition(Vector2f position) {
        glfwSetCursorPos(Window.window, (int) position.getX(), (int) position.getY());
    }

    public double getScrollX() {
        return scrollX;
    }

    public double getScrollY() {
        return scrollY;
    }

    public void setCursor(boolean enabled) {
        if (enabled)
            glfwSetInputMode(Window.window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        else
            glfwSetInputMode(Window.window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }
}