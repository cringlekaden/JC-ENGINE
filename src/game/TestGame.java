package game;

import engine.components.*;
import engine.core.Game;
import engine.core.GameObject;
import engine.core.Quaternion;
import engine.core.Vector3f;
import engine.rendering.*;


public class TestGame extends Game {

    public void init()
    {
        Mesh mesh = new Mesh("plane3.obj");
        Material material = new Material();
        material.addTexture("diffuse", new Texture("bricks.jpg"));
        material.addTexture("normalMap", new Texture("bricks_normal.jpg"));
        material.addFloat("specularIntensity", 1);
        material.addFloat("specularPower", 8);

        Material material2 = new Material();
        material2.addTexture("diffuse", new Texture("bricks2.jpg"));
        material2.addTexture("normalMap", new Texture("bricks2_normal.jpg"));
        material2.addFloat("specularIntensity", 1);
        material2.addFloat("specularPower", 8);

        Mesh tempMesh = new Mesh("monkey3.obj");

        MeshRenderer meshRenderer = new MeshRenderer(mesh, material);

        GameObject planeObject = new GameObject();
        planeObject.addComponent(meshRenderer);
        planeObject.getTransform().getPosition().set(0, -1, 5);

        GameObject directionalLightObject = new GameObject();
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(0,0,1), 0.4f);

        directionalLightObject.addComponent(directionalLight);

        GameObject pointLightObject = new GameObject();
        pointLightObject.addComponent(new PointLight(new Vector3f(0,1,0), 0.4f, new Attenuation(0,0,1)));

        SpotLight spotLight = new SpotLight(new Vector3f(0,1,1), 0.4f,
                new Attenuation(0,0,0.1f), 0.7f);

        GameObject spotLightObject = new GameObject();
        spotLightObject.addComponent(spotLight);

        spotLightObject.getTransform().getPosition().set(5, 0, 5);
        spotLightObject.getTransform().setRotation(new Quaternion(new Vector3f(0,1,0), (float)Math.toRadians(90.0f)));

        addObject(planeObject);
        addObject(directionalLightObject);
        addObject(pointLightObject);
        addObject(spotLightObject);

        GameObject testMesh3 = new GameObject().addComponent(new LookAtComponent()).addComponent(new MeshRenderer(tempMesh, material));

        addObject(
                //addObject(
                new GameObject().addComponent(new FreeLook(0.5f)).addComponent(new FreeMove(10.0f)).addComponent(new Camera((float) Math.toRadians(70.0f), (float) Window.getWidth() / (float) Window.getHeight(), 0.01f, 1000.0f)));

        addObject(testMesh3);

        testMesh3.getTransform().getPosition().set(5,5,5);
        testMesh3.getTransform().setRotation(new Quaternion(new Vector3f(0,1,0), (float)Math.toRadians(-70.0f)));

        addObject(new GameObject().addComponent(new MeshRenderer(new Mesh("monkey3.obj"), material2)));

        directionalLight.getTransform().setRotation(new Quaternion(new Vector3f(1, 0, 0), (float) Math.toRadians(-45)));
    }
}
