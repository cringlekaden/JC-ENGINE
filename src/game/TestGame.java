package game;

import engine.components.*;
import engine.components.lighting.DirectionalLight;
import engine.components.lighting.PointLight;
import engine.components.lighting.SpotLight;
import engine.core.*;
import engine.rendering.*;
import engine.rendering.textures.Texture;


public class TestGame extends Game {

    public void init() {
        GameObject planeObject = new GameObject();
        GameObject pointLightObject = new GameObject();
        GameObject spotLightObject = new GameObject();
        GameObject directionalLightObject = new GameObject();
        planeObject.addComponent(new MeshRenderer(new Mesh("plane3.obj"), new Material(new Texture("bricks.jpg"), 0.5f, 4,
                new Texture("bricks_normal.jpg"),
                new Texture("bricks_disp.png"), 0.03f, -0.5f)));
        planeObject.getTransform().setPosition(new Vector3f(0, -1, 5));
        planeObject.getTransform().setScale(2.5f);
        pointLightObject.addComponent(new PointLight(new Vector3f(0,1,0),0.4f, new Attenuation(0,0,1)));
        pointLightObject.getTransform().setPosition(new Vector3f(7,0,7));
        spotLightObject.addComponent(new SpotLight(new Vector3f(0,1,1),0.4f, new Attenuation(0,0,0.1f),0.7f));
        spotLightObject.getTransform().setRotation(new Quaternion(new Vector3f(0,1,0), (float)Math.toRadians(90.0f)));
        directionalLightObject.addComponent(new DirectionalLight(new Vector3f(1,1,1), 0.4f));
        GameObject testMesh1 = new GameObject();
        GameObject testMesh2 = new GameObject();
        testMesh1.addComponent(new MeshRenderer(new Mesh("plane3.obj"), new Material(new Texture("bricks2.jpg"), 1, 8,
                new Texture("bricks2_normal.png"),
                new Texture("bricks2_disp.jpg"), 0.04f, -1.0f)));
        testMesh2.addComponent(new MeshRenderer(new Mesh("plane3.obj"), new Material(new Texture("bricks2.jpg"), 1, 8,
                new Texture("bricks2_normal.png"),
                new Texture("bricks2_disp.jpg"), 0.04f, -1.0f)));
        testMesh1.getTransform().setPosition(new Vector3f(0, 2, 0));
        testMesh1.getTransform().setRotation(new Quaternion(new Vector3f(0,1,0), 0.4f));
        testMesh1.getTransform().setScale(1.0f);
        testMesh2.getTransform().setPosition(new Vector3f(0, 0, 25));
        testMesh1.addChild(testMesh2);
        addObject(planeObject);
    //	addObject(pointLightObject);
    //	addObject(spotLightObject);
        addObject(directionalLightObject);
        addObject(testMesh1);
        testMesh2.addChild((new GameObject()).addComponent(new Camera(new Matrix4f().perspective((float)Math.toRadians(70.0f), Window.getAspectRatio(), 0.1f, 1000.0f))).addComponent(new FreeLook()).addComponent(new FreeMove()));
        directionalLightObject.getTransform().setRotation(new Quaternion(new Vector3f(1,0,0), (float)Math.toRadians(-45.0f)));
        GameObject box = new GameObject();
        box.addComponent(new MeshRenderer(new Mesh("cube.obj"), new Material(new Texture("bricks2.jpg"), 1, 8,
                new Texture("bricks2_normal.png"),
                new Texture("bricks2_disp.jpg"), 0.04f, -1.0f)));
        box.getTransform().setPosition(new Vector3f(14,0,5));
        box.getTransform().setRotation(new Quaternion(new Vector3f(0,1,0), (float)Math.toRadians(30.0f)));
        addObject(box);
    }
}
