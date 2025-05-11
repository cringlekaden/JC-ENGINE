package engine.rendering.textures;

import engine.rendering.resources.TextureResource;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.ref.Cleaner;
import java.nio.ByteBuffer;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Texture {

    private final TextureResource resource;

    public Texture(String fileName) {
        this.resource = new TextureResource(fileName);
        this.resource.addReference();
    }

    public Texture(int width, int height, int internalFormat, int format, int type) {
        this.resource = new TextureResource(width, height, internalFormat, format, type);
        this.resource.addReference();
    }

    public void bind(int samplerSlot) {
        glActiveTexture(GL_TEXTURE0 + samplerSlot);
        glBindTexture(GL_TEXTURE_2D, resource.getId());
    }

    public int getId() {
        return resource.getId();
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
