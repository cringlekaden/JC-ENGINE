package engine.rendering;

import engine.components.lighting.BaseLight;
import engine.components.Camera;
import engine.components.lighting.ShadowCameraTransform;
import engine.components.lighting.ShadowData;
import engine.core.*;
import engine.rendering.framebuffers.Framebuffer;
import engine.rendering.framebuffers.FramebufferFormat;
import engine.rendering.resources.MappedValues;

import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;

public class RenderingEngine extends MappedValues {

    private static final Matrix4f shadowBiasMatrix = new Matrix4f().scale(0.5f, 0.5f, 0.5f).mul(new Matrix4f().translation(1.0f, 1.0f, 1.0f));
    private static final int NUM_SHADOW_MAPS = 12;

    private HashMap<String, Integer> samplerMap;
    private ArrayList<BaseLight> lights;
    private BaseLight activeLight;
    private Shader defaultShader;
    private Shader shadowMapShader;
    private Shader nullFilter;
    private Shader gausFilter;
    private Camera mainCamera;
    private Camera altCamera;
    private Matrix4f lightMatrix;
    private GameObject altCameraObject;
    private ArrayList<Framebuffer> shadowMaps;
    private ArrayList<Framebuffer> altShadowMaps;
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
        lights = new ArrayList<>();
        shadowMaps = new ArrayList<>();
        altShadowMaps = new ArrayList<>();
        samplerMap = new HashMap<>();
        samplerMap.put("diffuse", 0);
        samplerMap.put("normalMap", 1);
        samplerMap.put("dispMap", 2);
        samplerMap.put("shadowMap", 3);
        samplerMap.put("filterTexture", 0);
        setVector("ambient", new Vector3f(0.1f, 0.1f, 0.1f));
        for(int i = 0; i < NUM_SHADOW_MAPS; i++) {
            int shadowMapSize = 1 << (i + 1);
            shadowMaps.add(new Framebuffer(shadowMapSize, shadowMapSize, FramebufferFormat.SHADOW_FORMAT));
            altShadowMaps.add(new Framebuffer(shadowMapSize, shadowMapSize, FramebufferFormat.SHADOW_FORMAT));
        }
        planeMaterial = new Material(getTexture("shadowMap"), 1, 8);
        planeTransform = new Transform();
        planeTransform.rotate(new Vector3f(1, 0, 0), (float)Math.toRadians(90.0f));
        planeTransform.rotate(new Vector3f(0, 0, 1), (float)Math.toRadians(180.0f));
        planeMesh = new Mesh("plane.obj");
        altCamera = new Camera(new Matrix4f().identity());
        altCameraObject = new GameObject();
        altCameraObject.addComponent(altCamera);
        altCamera.getTransform().rotate(new Vector3f(0, 1, 0), (float)Math.toRadians(180));
        lightMatrix = new Matrix4f().scale(0, 0, 0);
    }

    public void updateUniformStruct(Transform transform, Material material, Shader shader, String uniformName, String uniformType) {
        // Handle any remaining struct uniforms not covered directly in Shader.updateUniforms.
        // Keep this conservative: support BaseLight explicitly, no-op for unknowns to avoid crashing.
        switch (uniformType) {
            case "BaseLight" -> shader.setUniformBaseLight(uniformName, getActiveLight());
            // Add more struct bindings here if custom engine-level structs are introduced.
            default -> {
                // Intentionally no-op to prevent hard failures on unrecognized struct uniforms.
                // If debugging is needed, consider logging once per shader/uniform.
            }
        }
    }

    public void render(GameObject object) {
        Window.bindAsRenderTarget();
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        object.renderAll(defaultShader, this);
        for (BaseLight light : lights) {
            activeLight = light;
            ShadowData shadowData = activeLight.getShadowData();
            int shadowMapIndex = 0;
            if(shadowData != null)
                shadowMapIndex = shadowData.getShadowMapSizeAsPowerOf2() - 1;
            setTexture("shadowMap", shadowMaps.get(shadowMapIndex).getColorTexture());
            shadowMaps.get(shadowMapIndex).bindAsRenderTarget();
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
            if(shadowData != null) {
                altCamera.setProjection(shadowData.getProjection());
                ShadowCameraTransform shadowCameraTransform = activeLight.calculateShadowCameraTransform(mainCamera.getTransform().getTransformedPosition(), mainCamera.getTransform().getTransformedRotation());
                altCamera.getTransform().setPosition(shadowCameraTransform.position);
                altCamera.getTransform().setRotation(shadowCameraTransform.rotation);
                lightMatrix = shadowBiasMatrix.mul(altCamera.getViewProjection());
                setFloat("shadowVariance", shadowData.getMinVariance());
                setFloat("shadowLightBleedReduction", shadowData.getLightBleedReduction());
                boolean flipFaces = shadowData.getFlipFaces();
                Camera temp = mainCamera;
                mainCamera = altCamera;
                // Avoid clipping light frustum during shadow map generation
                glEnable(GL_DEPTH_CLAMP);
                if(flipFaces)
                    glCullFace(GL_FRONT);
                object.renderAll(shadowMapShader, this);
                if(flipFaces)
                    glCullFace(GL_BACK);
                glDisable(GL_DEPTH_CLAMP);
                mainCamera = temp;
                float shadowSoftness = shadowData.getShadowSoftness();
                if(shadowSoftness != 0)
                    blurShadowMap(shadowMapIndex, shadowSoftness);
            } else {
                lightMatrix = new Matrix4f().scale(0, 0, 0);
                setFloat("shadowVariance", 0.00002f);
                setFloat("shadowLightBleedReduction", 0);
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
    }

    public void applyFilter(Shader filter, Framebuffer source, Framebuffer destination) {
        assert(source != null);
        if(destination == null)
            Window.bindAsRenderTarget();
        else
            destination.bindAsRenderTarget();
        setTexture("filterTexture", source.getColorTexture());
        altCamera.setProjection(new Matrix4f().identity());
        altCamera.getTransform().setPosition(new Vector3f(0, 0, 0));
        altCamera.getTransform().setRotation(new Quaternion(new Vector3f(0, 1, 0), (float)Math.toRadians(180)));
        Camera temp = mainCamera;
        setMainCamera(altCamera);
        glClear(GL_DEPTH_BUFFER_BIT);
        filter.bind();
        filter.updateUniforms(planeTransform, planeMaterial, this);
        planeMesh.draw();
        mainCamera = temp;
        setTexture("filterTexture", null);
    }

    public void blurShadowMap(int shadowMapIndex, float blurAmount) {
        setVector("blurScale", new Vector3f((blurAmount / shadowMaps.get(shadowMapIndex).getWidth()), 0, 0));
        applyFilter(gausFilter, shadowMaps.get(shadowMapIndex), altShadowMaps.get(shadowMapIndex));
        setVector("blurScale", new Vector3f(0, (blurAmount / shadowMaps.get(shadowMapIndex).getHeight()), 0));
        applyFilter(gausFilter, altShadowMaps.get(shadowMapIndex), shadowMaps.get(shadowMapIndex));
    }

    public void initializeShaders() {
        defaultShader = new Shader("fr-ambient");
        shadowMapShader = new Shader("shadowMapGenerator");
        nullFilter = new Shader("filter-null");
        gausFilter = new Shader("filter-gausBlur7x1");
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
