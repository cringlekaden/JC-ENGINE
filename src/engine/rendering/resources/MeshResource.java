package engine.rendering.resources;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.*;

public class MeshResource implements Runnable {

    private int vao;
    private int vbo;
    private int ibo;
    private int size;
    private int refCount;

    public MeshResource(int size) {
        vao = glGenVertexArrays();
        glBindVertexArray(vao);
        vbo = glGenBuffers();
        ibo = glGenBuffers();
        this.size = size;
        refCount = 1;
    }

    public void addReference() {
        refCount++;
    }

    public boolean removeReference() {
        refCount--;
        return refCount == 0;
    }

    @Override
    public void run() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ibo);
    }

    public int getVAO() {
        return vao;
    }

    public int getVBO() {
        return vbo;
    }

    public int getIBO() {
        return ibo;
    }

    public int getSize() {
        return size;
    }
}
