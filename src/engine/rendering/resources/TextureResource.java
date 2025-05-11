package engine.rendering.resources;

import engine.rendering.textures.TextureFormat;
import engine.rendering.textures.TextureLoader;

import java.lang.ref.Cleaner;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class TextureResource {

    private static final Cleaner cleaner = Cleaner.create();

    private final int id;
    private final int width;
    private final int height;

    private final Cleaner.Cleanable cleanable;
    private int refCount = 0;

    public TextureResource(String fileName) {
        TextureLoader.TextureData tex = TextureLoader.loadTexture(fileName);
        this.width = tex.width;
        this.height = tex.height;
        this.id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);
        if (tex.isHDR)
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, tex.width, tex.height, 0, GL_RGBA, GL_FLOAT, tex.dataF);
        else
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, tex.width, tex.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, tex.data);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);
        TextureLoader.free(tex);
        cleanable = cleaner.register(this, new GLTextureCleaner(id));
    }

    // Constructor for render targets
    public TextureResource(int width, int height, TextureFormat format) {
        this.width = width;
        this.height = height;
        this.id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);
        glTexImage2D(GL_TEXTURE_2D, 0, format.internalFormat, width, height, 0, format.format, format.type, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glBindTexture(GL_TEXTURE_2D, 0);
        cleanable = cleaner.register(this, new GLTextureCleaner(id));
    }

    public int getID() { return id; }

    public int getWidth() { return width; }

    public int getHeight() { return height; }

    public void addReference() {
        refCount++;
    }

    public boolean removeReference() {
        refCount--;
        return refCount == 0;
    }

    private static class GLTextureCleaner implements Runnable {

        private final int textureId;

        GLTextureCleaner(int textureId) {
            this.textureId = textureId;
        }

        @Override
        public void run() {
            glDeleteTextures(textureId);
        }
    }
}
