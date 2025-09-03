package engine.rendering.framebuffers;
import engine.rendering.textures.Texture;
import engine.rendering.GLDisposer;

import static org.lwjgl.opengl.GL30.*;

import java.lang.ref.Cleaner;

public class Framebuffer implements Runnable {

    private static final Cleaner CLEANER = Cleaner.create();
    private final Cleaner.Cleanable cleanable;

    private final int fboID;
    private final Texture colorTexture;
    private final Texture depthTexture;
    private final int width, height;

    public Framebuffer(int width, int height, FramebufferFormat framebufferFormat) {
        this.width = width;
        this.height = height;
        fboID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);
        // Create color texture attachment
        if(framebufferFormat.colorTextureFormat != null) {
            colorTexture = new Texture(width, height, framebufferFormat.colorTextureFormat);
            glFramebufferTexture2D(GL_FRAMEBUFFER, framebufferFormat.colorAttachment, framebufferFormat.colorTextureFormat.target, colorTexture.getID(), 0);
        } else {
            colorTexture = null;
        }
        // Create depth buffer attachment
        depthTexture = new Texture(width, height, framebufferFormat.depthTextureFormat);
        glFramebufferTexture2D(GL_FRAMEBUFFER, framebufferFormat.depthAttachment, framebufferFormat.depthTextureFormat.target, depthTexture.getID(), 0);
        if(framebufferFormat.colorTextureFormat != null) {
            glDrawBuffer(GL_COLOR_ATTACHMENT0);
        } else {
            glDrawBuffer(GL_NONE);
            glReadBuffer(GL_NONE);
        }
        // Check framebuffer completeness
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw new RuntimeException("Framebuffer is not complete!");
        // Unbind framebuffer
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        this.cleanable = CLEANER.register(this, this);
    }

    public void blitToScreen(int screenWidth, int screenHeight) {
        blitToFramebuffer(screenWidth, screenHeight, 0);
    }

    public void blitToFramebuffer(int screenWidth, int screenHeight, int framebufferToBlit) {
        bindForReading();
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, framebufferToBlit); // Default framebuffer (screen)
        glBlitFramebuffer(0, 0, width, height, 0, 0, screenWidth, screenHeight, GL_COLOR_BUFFER_BIT, GL_NEAREST);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bindAsRenderTarget() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fboID);
        glViewport(0, 0, width, height);
    }

    public void bindForReading() {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fboID);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void clear() {
        bindAsRenderTarget();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public Texture getColorTexture() {
        return colorTexture;
    }

    public Texture getDepthTexture() {
        return depthTexture;
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
        GLDisposer.enqueue(() -> glDeleteFramebuffers(fboID));
        // Dispose attached textures; their cleaners will enqueue GL deletes as well
        colorTexture.dispose();
        depthTexture.dispose();
    }

    public void dispose() {
        cleanable.clean();
    }
}