package engine.core;

import engine.components.GameComponent;
import engine.rendering.RenderingEngine;
import engine.rendering.Shader;

import java.util.ArrayList;

public class GameObject {

    private ArrayList<GameObject> children;
    private ArrayList<GameComponent> components;
    private Transform transform;
    private CoreEngine engine;

    public GameObject() {
        children = new ArrayList<>();
        components = new ArrayList<>();
        transform = new Transform();
        engine = null;
    }

    public void inputAll(float delta) {
        input(delta);
        for (GameObject child : children)
            child.inputAll(delta);
    }

    public void updateAll(float delta) {
        update(delta);
        for (GameObject child : children)
            child.updateAll(delta);
    }

    public void renderAll(Shader shader, RenderingEngine renderingEngine) {
        render(shader, renderingEngine);
        for (GameObject child : children)
            child.renderAll(shader, renderingEngine);
    }

    public void input(float delta) {
        transform.update();
        for (GameComponent component : components)
            component.input(delta);
    }

    public void update(float delta) {
        for (GameComponent component : components)
            component.update(delta);
    }

    public void render(Shader shader, RenderingEngine renderingEngine) {
        for (GameComponent component : components)
            component.render(shader, renderingEngine);
    }

    public void addChild(GameObject child) {
        children.add(child);
        child.setEngine(engine);
        child.getTransform().setParent(transform);
    }

    public GameObject addComponent(GameComponent component) {
        components.add(component);
        component.setParent(this);
        return this;
    }

    public Transform getTransform() {
        return transform;
    }

    public ArrayList<GameObject> getAllAttached() {
        ArrayList<GameObject> result = new ArrayList<>();
        for(GameObject child : children)
            result.addAll(child.getAllAttached());
        result.add(this);
        return result;
    }

    public void setEngine(CoreEngine engine) {
        if (this.engine != engine) {
            this.engine = engine;
            for (GameComponent component : components)
                component.addToEngine(engine);
            for (GameObject child : children)
                child.setEngine(engine);
        }
    }
}
