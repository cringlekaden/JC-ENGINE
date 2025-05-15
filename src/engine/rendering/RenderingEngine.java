package engine.rendering;

import engine.components.lighting.BaseLight;
import engine.components.Camera;
import engine.components.lighting.ShadowData;
import engine.core.*;
import engine.rendering.framebuffers.Framebuffer;
import engine.rendering.resources.MappedValues;

import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;

public class RenderingEngine extends MappedValues {

    private static final Matrix4f shadowBiasMatrix = new Matrix4f().scale(0.5f, 0.5f, 0.5f).mul(new Matrix4f().translation(1.0f, 1.0f, 1.0f));

    private HashMap<String, Integer> samplerMap;
    private ArrayList<BaseLight> lights;
    private BaseLight activeLight;
    private Shader defaultShader;
    private Shader shadowMapShader;
    private Camera mainCamera;
    private Camera altCamera;
    private Matrix4f lightMatrix;
    private GameObject altCameraObject;
    private Framebuffer shadowMapFramebuffer;
    private Mesh planeMesh;
    private Material planeMaterial;
    private Transform planeTransform;

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
        samplerMap.put("shadowMap", 3);
        setVector("ambient", new Vector3f(0.1f, 0.1f, 0.1f));

        shadowMapFramebuffer = new Framebuffer(1024, 1024, false);
        setTexture("shadowMap", shadowMapFramebuffer.getDepthTexture());
        planeMaterial = new Material(shadowMapFramebuffer.getDepthTexture(), 1, 8);
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
        System.out.println("Uniform name: " + uniformName + ". Uniform type: " + uniformType + ". Shader: " + shader.getFileName());
        throw new IllegalArgumentException(uniformType + " is not a valid type in RenderingEngine...");
    }

    public void render(GameObject object) {
        Window.bindAsRenderTarget();
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        object.renderAll(defaultShader, this);
        for (BaseLight light : lights) {
            activeLight = light;
            ShadowData shadowData = activeLight.getShadowData();
            shadowMapFramebuffer.bindAsRenderTarget();
            glClear(GL_DEPTH_BUFFER_BIT);
            if(shadowData != null) {
                altCamera.setProjection(shadowData.getProjection());
                altCamera.getTransform().setPosition(activeLight.getTransform().getTransformedPosition());
                altCamera.getTransform().setRotation(activeLight.getTransform().getTransformedRotation());
                lightMatrix = shadowBiasMatrix.mul(altCamera.getViewProjection());
                setVector("shadowTexelSize", new Vector3f(1.0f / 1024.0f, 1.0f / 1024.0f, 0.0f));
                setFloat("shadowBias", shadowData.getBias() / 1024.0f);
                boolean flipFaces = shadowData.getFlipFaces();
                Camera temp = mainCamera;
                mainCamera = altCamera;
                if(flipFaces)
                    glCullFace(GL_FRONT);
                object.renderAll(shadowMapShader, this);
                if(flipFaces)
                    glCullFace(GL_BACK);
                mainCamera = temp;
            }
            Window.bindAsRenderTarget();
            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE, GL_ONE);
            glDepthMask(false);
            glDepthFunc(GL_EQUAL);
            object.renderAll(activeLight.getShader(), this);
            glDepthMask(true);
            glDepthFunc(GL_LESS);
            glDisable(GL_BLEND);
        }

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

    public void initializeShaders() {
        defaultShader = new Shader("fr-ambient");
        shadowMapShader = new Shader("shadowMapGenerator");
    }

    public static String getOpenGLVersion() {
        return glGetString(GL_VERSION);
    }

    public BaseLight getActiveLight() {
        return activeLight;
    }

    public Matrix4f getLightMatrix() {
        return lightMatrix;
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
