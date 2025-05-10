package engine.rendering.resources;

import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;

public class TextureResource implements Runnable {

    private int id;
    private int refCount;

    public TextureResource() {
        this.id = glGenTextures();
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
        glDeleteBuffers(id);
    }

    public int getID() {
        return id;
    }
}
