package engine.rendering.textures;

import engine.rendering.resources.TextureResource;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Texture {

    private final TextureResource resource;

    public Texture(String fileName) {
        this.resource = new TextureResource(fileName);
        this.resource.addReference();
    }

    public Texture(int width, int height, TextureFormat format) {
        this.resource = new TextureResource(width, height, format);
        this.resource.addReference();
    }

    public void bind(int samplerSlot) {
        glActiveTexture(GL_TEXTURE0 + samplerSlot);
        glBindTexture(resource.getTarget(), resource.getID());
    }

    public int getID() {
        return resource.getID();
    }

    public int getWidth() {
        return resource.getWidth();
    }

    public int getHeight() {
        return resource.getHeight();
    }

    public void dispose() {
        if (resource.removeReference()) {
            // Cleaner handles the OpenGL deletion
        }
    }
}
