package engine.rendering;
import engine.rendering.textures.Texture;
import engine.rendering.textures.TextureFormat;

import static org.lwjgl.opengl.GL30.*;

import java.lang.ref.Cleaner;

public class Framebuffer implements Runnable {

    private static final Cleaner CLEANER = Cleaner.create();
    private final Cleaner.Cleanable cleanable;

    private final int fboID;
    private final Texture colorTexture;
    private final int depthBufferID;
    private final int width, height;

    public Framebuffer(int width, int height) {
        this.width = width;
        this.height = height;
        fboID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);
        // Create color texture attachment
        colorTexture = new Texture(width, height, TextureFormat.RGBA8);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorTexture.getID(), 0);
        // Create depth buffer attachment
        depthBufferID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, depthBufferID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBufferID);
        // Check framebuffer completeness
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw new RuntimeException("Framebuffer is not complete!");
        // Unbind framebuffer
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        this.cleanable = CLEANER.register(this, this);
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);
        glViewport(0, 0, width, height);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void clear() {
        bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public Texture getColorTexture() {
        return colorTexture;
    }

    public int getID() {
        return fboID;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public void run() {
        glDeleteFramebuffers(fboID);
        glDeleteRenderbuffers(depthBufferID);
        colorTexture.dispose(); // Manually dispose texture (calls Cleaner)
    }

    public void dispose() {
        cleanable.clean();
    }
}