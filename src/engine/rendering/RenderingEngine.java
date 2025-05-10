package engine.rendering;

import engine.components.BaseLight;
import engine.components.Camera;
import engine.core.GameObject;
import engine.core.Transform;
import engine.core.Vector3f;
import engine.rendering.resources.MappedValues;

import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;

public class RenderingEngine extends MappedValues {

    private HashMap<String, Integer> samplerMap;
    private ArrayList<BaseLight> lights;
    private BaseLight activeLight;
    private Camera mainCamera;
    private Shader forwardAmbient;

    public RenderingEngine() {
        super();
        glClearColor(0.05f, 0.05f, 0.05f, 1.0f);
        glFrontFace(GL_CW);
        glCullFace(GL_BACK);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_DEPTH_CLAMP);
        glEnable(GL_TEXTURE_2D);
        lights = new ArrayList<>();
        samplerMap = new HashMap<>();
        samplerMap.put("diffuse", 0);
        addVector("ambient", new Vector3f(0.1f, 0.1f, 0.1f));
    }

    public static String getOpenGLVersion() {
        return glGetString(GL_VERSION);
    }

    public void updateUniformStruct(Transform transform, Material material, Shader shader, String uniformName, String uniformType) {
        throw new IllegalArgumentException(uniformType + " is not a valid type in RenderingEngine...");
    }

    public BaseLight getActiveLight() {
        return activeLight;
    }

    public void render(GameObject object) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        forwardAmbient = new Shader("fr-ambient");
        object.renderAll(forwardAmbient, this);
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);
        glDepthMask(false);
        glDepthFunc(GL_EQUAL);
        for (BaseLight light : lights) {
            activeLight = light;
            object.renderAll(light.getShader(), this);
        }
        glDepthFunc(GL_LESS);
        glDepthMask(true);
        glDisable(GL_BLEND);
    }

    public int getSamplerSlot(String samplerName) {
        return samplerMap.get(samplerName);
    }

    public Camera getMainCamera() {
        return mainCamera;
    }

    public void setMainCamera(Camera mainCamera) {
        this.mainCamera = mainCamera;
    }

    public void addLight(BaseLight light) {
        lights.add(light);
    }
}
