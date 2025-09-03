package game.components;

import engine.components.GameComponent;
import engine.core.Vector3f;

public class OrbitMotion extends GameComponent {
    private Vector3f center;
    private final float radius;
    private final float speed;
    private float angle;
    private final float heightOffset;

    public OrbitMotion(float radius, float speed) {
        this(radius, speed, 0.0f);
    }

    public OrbitMotion(float radius, float speed, float heightOffset) {
        this.radius = radius;
        this.speed = speed;
        this.heightOffset = heightOffset;
        this.angle = 0.0f;
    }

    @Override
    public void addToEngine(engine.core.CoreEngine engine) {
        // Capture initial position as center
        this.center = getTransform().getPosition();
    }

    @Override
    public void update(float delta) {
        if (center == null) {
            center = getTransform().getPosition();
        }
        angle += speed * delta;
        float x = center.getX() + (float)Math.cos(angle) * radius;
        float z = center.getZ() + (float)Math.sin(angle) * radius;
        float y = center.getY() + heightOffset;
        getTransform().setPosition(new Vector3f(x, y, z));
    }
}