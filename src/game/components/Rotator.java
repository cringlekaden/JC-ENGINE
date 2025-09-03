package game.components;

import engine.components.GameComponent;
import engine.core.Vector3f;

public class Rotator extends GameComponent {
    private final Vector3f axis;
    private final float speedRadiansPerSec;

    public Rotator(Vector3f axis, float speedRadiansPerSec) {
        this.axis = axis;
        this.speedRadiansPerSec = speedRadiansPerSec;
    }

    @Override
    public void update(float delta) {
        if (delta == 0.0f) return;
        getTransform().rotate(axis, speedRadiansPerSec * delta);
    }
}