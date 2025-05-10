package engine.core;

public class Transform {

    private Transform parent;
    private Matrix4f parentMatrix;
    private Vector3f position;
    private Quaternion rotation;
    private Vector3f scale;
    private Vector3f oldPosition;
    private Quaternion oldRotation;
    private Vector3f oldScale;

    public Transform() {
        position = new Vector3f(0, 0, 0);
        rotation = new Quaternion(0, 0, 0, 1);
        scale = new Vector3f(1, 1, 1);
        parentMatrix = new Matrix4f().identity();
    }

    public void update() {
        if (oldPosition != null) {
            oldPosition.set(position);
            oldRotation.set(rotation);
            oldScale.set(scale);
        } else {
            oldPosition = new Vector3f(0, 0, 0).set(position).add(1.0f);
            oldRotation = new Quaternion(0, 0, 0, 0).set(rotation).mul(0.5f);
            oldScale = new Vector3f(0, 0, 0).set(scale).add(1.0f);
        }
    }

    public void rotate(Vector3f axis, float angle) {
        rotation = new Quaternion(axis, angle).mul(rotation).normalized();
    }

    public void lookAt(Vector3f point, Vector3f up) {
        rotation = getLookAtRotation(point, up);
    }

    public Quaternion getLookAtRotation(Vector3f point, Vector3f up)
    {
        return new Quaternion(new Matrix4f().rotation(point.sub(position).normalized(), up));
    }

    public boolean hasChanged() {
        if (parent != null && parent.hasChanged())
            return true;
        if (!position.equals(oldPosition))
            return true;
        if (!rotation.equals(oldRotation))
            return true;
        return !scale.equals(oldScale);
    }

    public Matrix4f getTransformation() {
        Matrix4f translationMatrix = new Matrix4f().translation(position.getX(), position.getY(), position.getZ());
        Matrix4f rotationMatrix = rotation.toRotationMatrix();
        Matrix4f scaleMatrix = new Matrix4f().scale(scale.getX(), scale.getY(), scale.getZ());
        return getParentMatrix().mul(translationMatrix.mul(rotationMatrix.mul(scaleMatrix)));
    }

    private Matrix4f getParentMatrix() {
        if (parent != null && parent.hasChanged())
            parentMatrix = parent.getTransformation();
        return parentMatrix;
    }

    public void setParent(Transform parent) {
        this.parent = parent;
    }

    public Vector3f getTransformedPosition() {
        return getParentMatrix().transform(position);
    }

    public Quaternion getTransformedRotation() {
        Quaternion parentRotation = new Quaternion(0, 0, 0, 1);
        if (parent != null)
            parentRotation = parent.getTransformedRotation();
        return parentRotation.mul(rotation);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }
}
