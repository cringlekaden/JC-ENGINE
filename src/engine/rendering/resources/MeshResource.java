package engine.rendering.resources;

import engine.rendering.Vertex;

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
        this.size = size;
        this.refCount = 1;
        this.vao = glGenVertexArrays();
        this.vbo = glGenBuffers();
        this.ibo = glGenBuffers();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex.SIZE * 4, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, Vertex.SIZE * 4, 12);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, Vertex.SIZE * 4, 20);
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
