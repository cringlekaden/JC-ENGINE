package engine.components;

import engine.core.CoreEngine;
import engine.core.GameObject;
import engine.core.Transform;
import engine.rendering.RenderingEngine;
import engine.rendering.Shader;

public abstract class GameComponent {

    private GameObject parent;

    public void input(float delta) {
    }

    public void update(float delta) {
    }

    public void render(Shader shader, RenderingEngine renderingEngine) {
    }

    public void setParent(GameObject parent) {
        this.parent = parent;
    }

    public Transform getTransform() {
        return parent.getTransform();
    }

    public void addToEngine(CoreEngine engine) {
    }
}
