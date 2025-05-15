package engine.rendering;

import engine.components.BaseLight;
import engine.components.Camera;
import engine.core.*;
import engine.rendering.framebuffers.Framebuffer;
import engine.rendering.resources.MappedValues;

import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;

public class RenderingEngine extends MappedValues {

    private HashMap<String, Integer> samplerMap;
    private ArrayList<BaseLight> lights;
    private BaseLight activeLight;
    private Shader defaultShader;
    private Camera mainCamera;
    private Camera altCamera;
    private GameObject altCameraObject;
    private Framebuffer tempTarget;
    private Mesh planeMesh;
    private Material planeMaterial;
    private Transform planeTransform;

    //Testing code

    public RenderingEngine() {
        super();
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glFrontFace(GL_CW);
        glCullFace(GL_BACK);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        lights = new ArrayList<>();
        samplerMap = new HashMap<>();
        samplerMap.put("diffuse", 0);
        samplerMap.put("normalMap", 1);
        samplerMap.put("dispMap", 2);
        setVector("ambient", new Vector3f(0.1f, 0.1f, 0.1f));

        //Testing code
        int width = Window.getWidth() / 5;
        int height = Window.getHeight()/ 5;
        tempTarget = new Framebuffer(width, height, true);
        planeMaterial = new Material(tempTarget.getColorTexture(), 1, 8);
        planeTransform = new Transform();
        planeTransform.setScale(0.9f);
        planeTransform.rotate(new Vector3f(1, 0, 0), (float)Math.toRadians(90.0f));
        planeTransform.rotate(new Vector3f(0, 0, 1), (float)Math.toRadians(180.0f));
        planeMesh = new Mesh("plane.obj");
        altCamera = new Camera(new Matrix4f().identity());
        altCameraObject = new GameObject().addComponent(altCamera);
        altCamera.getTransform().rotate(new Vector3f(0, 1, 0), (float)Math.toRadians(180));
    }

    public void updateUniformStruct(Transform transform, Material material, Shader shader, String uniformName, String uniformType) {
        throw new IllegalArgumentException(uniformType + " is not a valid type in RenderingEngine...");
    }

    public void render(GameObject object) {
        Window.bindAsRenderTarget();
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        object.renderAll(defaultShader, this);
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

//        Window.bindAsRenderTarget();
//        Camera temp = mainCamera;
//        setMainCamera(altCamera);
//        glClearColor(0, 0, 0.5f, 1.0f);
//        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//        defaultShader.bind();
//        defaultShader.updateUniforms(planeTransform, planeMaterial, this);
//        planeMesh.draw();
//        setMainCamera(temp);
    }

    public void setDefaultShader() {
        defaultShader = new Shader("fr-ambient");
    }

    public static String getOpenGLVersion() {
        return glGetString(GL_VERSION);
    }

    public BaseLight getActiveLight() {
        return activeLight;
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
