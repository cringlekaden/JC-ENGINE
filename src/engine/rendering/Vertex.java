package engine.rendering;

import engine.core.Vector2f;
import engine.core.Vector3f;

public class Vertex {

    public static final int SIZE = 11;

    private Vector3f position;
    private Vector2f texCoord;
    private Vector3f normal;
    private Vector3f tangent;

    public Vertex(Vector3f position) {
        this(position, new Vector2f(0, 0));
    }

    public Vertex(Vector3f position, Vector2f texCoord) {
        this(position, texCoord, new Vector3f(0, 0, 0));
    }

    public Vertex(Vector3f position, Vector2f texCoord, Vector3f normal) {
        this(position, texCoord, normal, new Vector3f(0, 0, 0));
    }

    public Vertex(Vector3f position, Vector2f texCoord, Vector3f normal, Vector3f tangent) {
        this.position = position;
        this.texCoord = texCoord;
        this.normal = normal;
        this.tangent = tangent;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector2f getTexCoord() {
        return texCoord;
    }

    public void setTexCoord(Vector2f texCoord) {
        this.texCoord = texCoord;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public void setNormal(Vector3f normal) {
        this.normal = normal;
    }

    public Vector3f getTangent() {
        return tangent;
    }

    public void setTangent(Vector3f tangent) {
        this.tangent = tangent;
    }
}
