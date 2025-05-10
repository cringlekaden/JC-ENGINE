package game;

import engine.components.GameComponent;
import engine.core.Quaternion;
import engine.core.Vector3f;
import engine.rendering.RenderingEngine;
import engine.rendering.Shader;

public class LookAtComponent extends GameComponent {
    RenderingEngine renderingEngine;

    @Override
    public void update(float delta) {
        if(renderingEngine != null) {
            Quaternion newRot = getTransform().getLookAtRotation(renderingEngine.getMainCamera().getTransform()
                            .getTransformedPosition(), new Vector3f(0,1,0));
            getTransform().setRotation(getTransform().getRotation().nlerp(newRot, delta * 5.0f, true));
            //getTransform().setRotation(getTransform().getRotation().slerp(newRot, delta * 5.0f, true));
        }
    }

    @Override
    public void render(Shader shader, RenderingEngine renderingEngine) {
        this.renderingEngine = renderingEngine;
    }
}