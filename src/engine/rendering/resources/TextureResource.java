package engine.rendering.resources;

import engine.rendering.textures.TextureFormat;
import engine.rendering.textures.TextureLoader;

import java.lang.ref.Cleaner;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class TextureResource {

    private static final Cleaner cleaner = Cleaner.create();

    private final int id;
    private final int width;
    private final int height;
    private final int target;

    private final Cleaner.Cleanable cleanable;
    private int refCount = 0;

    public TextureResource(String fileName) {
        TextureLoader.TextureData tex = TextureLoader.loadTexture(fileName);
        this.width = tex.width;
        this.height = tex.height;
        this.id = glGenTextures();
        this.target = GL_TEXTURE_2D;
        glBindTexture(target, id);
        if (tex.isHDR)
            glTexImage2D(target, 0, GL_RGBA16F, tex.width, tex.height, 0, GL_RGBA, GL_FLOAT, tex.dataF);
        else
            glTexImage2D(target, 0, GL_RGBA8, tex.width, tex.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, tex.data);
        glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(target, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(target, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glGenerateMipmap(target);
        float maxAnisotropy = glGetFloat(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
        glTexParameterf(target, GL_TEXTURE_MAX_ANISOTROPY_EXT, maxAnisotropy);
        glBindTexture(target, 0);
        TextureLoader.free(tex);
        cleanable = cleaner.register(this, new GLTextureCleaner(id));
    }

    // Constructor for render targets
    public TextureResource(int width, int height, TextureFormat format) {
        this.width = width;
        this.height = height;
        this.id = glGenTextures();
        this.target = format.target;
        glBindTexture(target, id);
        if (target == GL_TEXTURE_CUBE_MAP) {
            // Allocate all 6 faces
            for (int i = 0; i < 6; i++) {
                glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, format.internalFormat, width, height, 0, format.format, format.type, (ByteBuffer) null);
            }
        } else {
            glTexImage2D(target, 0, format.internalFormat, width, height, 0, format.format, format.type, (ByteBuffer) null);
        }
        glTexParameteri(target, GL_TEXTURE_MIN_FILTER, format.minFilter);
        glTexParameteri(target, GL_TEXTURE_MAG_FILTER, format.magFilter);
        if(format.clamp) {
            glTexParameteri(target, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(target, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            if (target == GL_TEXTURE_CUBE_MAP) {
                glTexParameteri(target, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
            }
        } else {
            glTexParameteri(target, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(target, GL_TEXTURE_WRAP_T, GL_REPEAT);
            if (target == GL_TEXTURE_CUBE_MAP) {
                glTexParameteri(target, GL_TEXTURE_WRAP_R, GL_REPEAT);
            }
        }
        if(format.minFilter == GL_LINEAR_MIPMAP_LINEAR || format.magFilter == GL_LINEAR_MIPMAP_LINEAR ||
                format.minFilter == GL_LINEAR_MIPMAP_NEAREST || format.magFilter == GL_LINEAR_MIPMAP_NEAREST ||
                format.minFilter == GL_NEAREST_MIPMAP_LINEAR || format.magFilter == GL_NEAREST_MIPMAP_LINEAR ||
                format.minFilter == GL_NEAREST_MIPMAP_NEAREST || format.magFilter == GL_NEAREST_MIPMAP_NEAREST) {
            glGenerateMipmap(target);
            float maxAnisotropy = glGetFloat(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
            glTexParameterf(target, GL_TEXTURE_MAX_ANISOTROPY_EXT, maxAnisotropy);
        } else {
            glTexParameteri(target, GL_TEXTURE_BASE_LEVEL, 0);
            glTexParameteri(target, GL_TEXTURE_MAX_LEVEL, 0);
        }
        glBindTexture(target, 0);
        cleanable = cleaner.register(this, new GLTextureCleaner(id));
    }

    public int getID() { return id; }

    public int getWidth() { return width; }

    public int getHeight() { return height; }

    public int getTarget() { return target; }

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
