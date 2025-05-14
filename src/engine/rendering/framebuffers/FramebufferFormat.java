package engine.rendering.framebuffers;

import engine.rendering.textures.TextureFormat;

import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;

public class FramebufferFormat {

    public final TextureFormat textureFormat;
    public final int attachment;

    public FramebufferFormat(TextureFormat textureFormat, int attachment) {
        this.textureFormat = textureFormat;
        this.attachment = attachment;
    }

    public static final FramebufferFormat COLOR_FORMAT =
            new FramebufferFormat(TextureFormat.RGBA8, GL_COLOR_ATTACHMENT0);

    public static final FramebufferFormat DEPTH_FORMAT =
            new FramebufferFormat(TextureFormat.DEPTH_COMPONENT, GL_DEPTH_ATTACHMENT);
}
