package game;

import engine.components.*;
import engine.components.lighting.DirectionalLight;
import engine.components.lighting.PointLight;
import engine.components.lighting.SpotLight;
import engine.core.Game;
import engine.core.GameObject;
import engine.core.Matrix4f;
import engine.core.Quaternion;
import engine.core.Vector3f;
import engine.components.Camera;
import engine.components.MeshRenderer;
import engine.rendering.Attenuation;
import engine.rendering.Material;
import engine.rendering.Mesh;
import engine.rendering.Window;
import engine.rendering.textures.Texture;
import game.assets.AssetHelper;
import game.components.OrbitMotion;
import game.components.Rotator;

public class ShowcaseGame extends Game {

    @Override
    public void init() {
        // High-res bricks from ambientCG (if present); otherwise fallback to built-in bricks
        String hrDiffuse = AssetHelper.textureOrFallback("external/ambientCG/Bricks047_2K_Color.jpg", "bricks2.jpg");
        String hrNormal  = AssetHelper.textureOrFallback("external/ambientCG/Bricks047_2K_NormalGL.jpg", "bricks2_normal.png");
        String hrDisp    = AssetHelper.textureOrFallback("external/ambientCG/Bricks047_2K_Displacement.jpg", "bricks2_disp.jpg");
        // Additional alternative textures
        String metalDiffuse = AssetHelper.textureOrFallback("external/ambientCG/Metal031_2K_Color.jpg", "bricks.jpg");
        String metalNormal  = AssetHelper.textureOrFallback("external/ambientCG/Metal031_2K_NormalGL.jpg", "default_normal.jpg");
        String metalDisp    = AssetHelper.textureOrFallback("external/ambientCG/Metal031_2K_Displacement.jpg", "default_disp.png");
        String bunnyDiffuse = AssetHelper.textureOrFallback("external/ambientCG/Plaster001_2K_Color.jpg", "stallTexture.png");
        String bunnyNormal  = AssetHelper.textureOrFallback("external/ambientCG/Plaster001_2K_NormalGL.jpg", "default_normal.jpg");
        String bunnyDisp    = AssetHelper.textureOrFallback("external/ambientCG/Plaster001_2K_Displacement.jpg", "default_disp.png");

        // Make bricks less shiny/more realistic by reducing specular intensity and exponent
        Material groundMat = new Material(new Texture(hrDiffuse), 0.25f, 8.0f,
                new Texture(hrNormal), new Texture(hrDisp), 0.03f, -0.5f);

        // Metallic-looking cube: high specular, tight highlight; include metal normal/disp with subtle displacement
        Material metalMat = new Material(new Texture(metalDiffuse), 8.0f, 128.0f,
                new Texture(metalNormal), new Texture(metalDisp), 0.01f, -0.5f);

        // Bunny with plaster material; moderate specular; include plaster normal/disp with low displacement
        Material bunnyMat = new Material(new Texture(bunnyDiffuse), 0.6f, 16.0f,
                new Texture(bunnyNormal), new Texture(bunnyDisp), 0.015f, -0.5f);

        GameObject ground = new GameObject();
        ground.addComponent(new MeshRenderer(new Mesh("plane3.obj"), groundMat));
        ground.getTransform().setPosition(new Vector3f(0.0f, -1.0f, 0.0f));
        ground.getTransform().setScale(8.0f);

        // Optional external hero model (OBJ). If not found, fallback to cube.
        String heroModel = game.assets.AssetHelper.modelOrFallback("external/bunny.obj", "cube.obj");
        Material heroMat = new Material(new Texture(hrDiffuse), 0.8f, 16.0f,
                new Texture(hrNormal), new Texture(hrDisp), 0.02f, -0.5f);
        GameObject hero = new GameObject();
        hero.addComponent(new MeshRenderer(new Mesh(heroModel), bunnyMat));
        hero.getTransform().setPosition(new Vector3f(2.5f, -0.3f, 6.5f));
        hero.getTransform().setScale(14.0f);
        hero.getTransform().setRotation(new Quaternion(new Vector3f(0,1,0), (float)Math.toRadians(20)));
        // Animate the bunny: gentle orbit and slow spin
        hero.addComponent(new OrbitMotion(1.5f, 0.6f));
        hero.addComponent(new Rotator(new Vector3f(0,1,0), (float)Math.toRadians(20.0f)));

        // Add a second object (pillar) clearly separated from the bunny
        GameObject pillar = new GameObject();
        pillar.addComponent(new MeshRenderer(new Mesh("cube.obj"), metalMat));
        pillar.getTransform().setPosition(new Vector3f(-13.0f, 10.0f, 10.0f));
        pillar.getTransform().setScale(6f);

        // Lights
        GameObject sunObj = new GameObject();
        // Directional light with larger shadow map (12 -> 4096) for clarity
        sunObj.addComponent(new DirectionalLight(new Vector3f(1.0f, 0.98f, 0.93f), 0.22f, 12));
        sunObj.getTransform().setRotation(new Quaternion(new Vector3f(1,0,0), (float)Math.toRadians(-50))
                .mul(new Quaternion(new Vector3f(0,1,0), (float)Math.toRadians(25))));
        sunObj.addComponent(new Rotator(new Vector3f(0,1,0), (float)Math.toRadians(5.0f)));

        GameObject pointA = new GameObject();
        pointA.addComponent(new PointLight(new Vector3f(0.95f, 0.45f, 0.15f), 0.8f, new Attenuation(0, 0, 0.03f)));
        pointA.getTransform().setPosition(new Vector3f(6.0f, 2.0f, 10.0f));
        pointA.addComponent(new OrbitMotion(3.0f, 0.7f));

        GameObject pointB = new GameObject();
        pointB.addComponent(new PointLight(new Vector3f(0.2f, 0.6f, 1.0f), 0.7f, new Attenuation(0, 0, 0.025f)));
        pointB.getTransform().setPosition(new Vector3f(-8.0f, 2.0f, 6.0f));
        pointB.addComponent(new OrbitMotion(4.0f, 0.5f));

        GameObject spot = new GameObject();
        // Wider cutoff for soft cone; shadow map size exponent 11 (~2048)
        spot.addComponent(new SpotLight(new Vector3f(1.0f, 1.0f, 0.9f), 0.9f, new Attenuation(0, 0, 0.02f),
                (float)Math.toRadians(40.0f), 11, 0.8f, 0.5f, 0.00002f));
        spot.getTransform().setPosition(new Vector3f(0.0f, 6.0f, 4.0f));
        spot.getTransform().setRotation(new Quaternion(new Vector3f(1,0,0), (float)Math.toRadians(-75)));
        spot.addComponent(new OrbitMotion(2.0f, 1.1f));

        // Camera with free look/move
        GameObject cameraRig = new GameObject();
        cameraRig.addComponent(new Camera(new Matrix4f().perspective((float)Math.toRadians(75.0f), Window.getAspectRatio(), 0.1f, 1000.0f)))
                 .addComponent(new FreeLook())
                 .addComponent(new FreeMove());
        cameraRig.getTransform().setPosition(new Vector3f(0.0f, 2.0f, 0.0f));

        addObject(ground);
        addObject(hero);
        addObject(pillar);
        addObject(sunObj);
        addObject(pointA);
        addObject(pointB);
        addObject(spot);
        addObject(cameraRig);
    }
}
