package engine.rendering;

import engine.core.InputHandler;
import engine.core.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;
import engine.rendering.GLDisposer;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.opengl.GL30.*;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    public static long window;
    private static String title;
    private static Callback debugProc;

    public static void createWindow(int width, int height, String title) {
        Window.title = title;
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit())
            throw new IllegalStateException("GLFW failed to initialize...");
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_FOCUSED, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        // Platform-aware context version selection
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("mac")) {
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        } else {
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
        }
        // Request debug context where supported (ignored otherwise)
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("GLFW failed to create window...");
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
        }
        glfwSetKeyCallback(window, InputHandler.getInstance());
        glfwSetMouseButtonCallback(window, InputHandler.getInstance().mouseButtonCallback);
        glfwSetCursorPosCallback(window, InputHandler.getInstance().cursorPosCallback);
        glfwSetScrollCallback(window, InputHandler.getInstance().scrollCallback);
        if (glfwRawMouseMotionSupported())
            glfwSetInputMode(window, GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);
        glfwMakeContextCurrent(window);
        glfwSwapInterval(0);
        glfwShowWindow(window);
        glfwFocusWindow(window);
        GL.createCapabilities();

        // Setup debug message callback if available
        try {
            debugProc = GLUtil.setupDebugMessageCallback();
        } catch (Throwable ignored) {
            // Some drivers/contexts (notably older macOS) may not support KHR_debug; ignore silently.
        }
    }

    public static void bindAsRenderTarget() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
        glViewport(0, 0, getWidth(), getHeight());
    }

    public static void render() {
        glfwSwapBuffers(window);
        glfwPollEvents();
        // Drain any queued GL deletions while the context is current
        GLDisposer.drain();
    }

    public static void closeWindow() {
        // Ensure all GL deletions are performed before destroying the context
        GLDisposer.drainAll();
        if (debugProc != null) debugProc.free();
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public static boolean isCloseRequested() {
        return glfwWindowShouldClose(window);
    }

    public static int getWidth() {
        IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
        glfwGetFramebufferSize(window, widthBuffer, null);
        return widthBuffer.get(0);
    }

    public static int getHeight() {
        IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
        glfwGetFramebufferSize(window, null, heightBuffer);
        return heightBuffer.get(0);
    }

    public static float getAspectRatio() {
        return (float)getWidth() / (float)getHeight();
    }

    public static String getTitle() {
        return title;
    }

    public Vector2f getCenter() {
        return new Vector2f((float) getWidth() / 2, (float) getHeight() / 2);
    }
}