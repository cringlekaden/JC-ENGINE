package engine.rendering;

import engine.components.BaseLight;
import engine.components.Camera;
import engine.core.*;
import engine.rendering.framebuffers.Framebuffer;
import engine.rendering.resources.MappedValues;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;

public class RenderingEngine extends MappedValues {

    private HashMap<String, Integer> samplerMap;
    private ArrayList<BaseLight> lights;
    private BaseLight activeLight;
    private Camera mainCamera;
    private Shader defaultShader;

    //Testing code
    private static Framebuffer tempTarget;
    private static Mesh tempMesh;
    private static Transform tempTransform;
    private static Material tempMaterial;
    private static Camera tempCamera;
    private static GameObject tempGameObject;

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
        addVector("ambient", new Vector3f(0.1f, 0.1f, 0.1f));

        //Testing code
        int width = Window.getWidth() / 3;
        int height = Window.getHeight() / 3;
        tempTarget = new Framebuffer(width, height, true);
        Vertex[] vertices = new Vertex[] {
                new Vertex(new Vector3f(-1, -1, 0), new Vector2f(1, 0)),
                new Vertex(new Vector3f(-1, 1, 0), new Vector2f(1, 1)),
                new Vertex(new Vector3f(1, 1, 0), new Vector2f(0, 1)),
                new Vertex(new Vector3f(1, -1, 0), new Vector2f(0, 0)),
        };
        int[] indices = new int[] { 2, 1, 0, 3, 2, 0 };
        tempMaterial = new Material(tempTarget.getColorTexture(), 1, 8);
        tempTransform = new Transform();
        tempTransform.setScale(0.9f);
        tempMesh = new Mesh(vertices, indices, true);
//        tempCamera = new Camera(new Matrix4f().orthographic(-1, 1, -1, 1, -1, 1));
        tempCamera = new Camera(new Matrix4f().identity());
        tempGameObject = new GameObject().addComponent(tempCamera);
    }

    public void updateUniformStruct(Transform transform, Material material, Shader shader, String uniformName, String uniformType) {
        throw new IllegalArgumentException(uniformType + " is not a valid type in RenderingEngine...");
    }

    public void render(GameObject object) {
        tempTarget.bindAsRenderTarget();
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
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

        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);

        Window.bindAsRenderTarget();
        Camera temp = mainCamera;
        mainCamera = tempCamera;
        glClearColor(1.0f, 0.0f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        defaultShader.bind();
        defaultShader.updateUniforms(tempTransform, tempMaterial, this);
        tempMesh.draw();
        mainCamera = temp;

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

//        glBindFramebuffer(GL_READ_FRAMEBUFFER, tempTarget.getID());
//        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
//        glBlitFramebuffer(0, 0, tempTarget.getWidth(), tempTarget.getHeight(), 0, 0, Window.getWidth(), Window.getHeight(), GL_COLOR_BUFFER_BIT, GL_NEAREST);
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
