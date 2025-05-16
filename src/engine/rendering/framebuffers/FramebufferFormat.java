package engine.rendering.framebuffers;

import engine.rendering.textures.TextureFormat;

import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;

public class FramebufferFormat {

    public final TextureFormat colorTextureFormat;
    public final TextureFormat depthTextureFormat;
    public final int colorAttachment;
    public final int depthAttachment;

    public FramebufferFormat(TextureFormat colorTextureFormat, TextureFormat depthTextureFormat, int colorAttachment, int depthAttachment) {
        this.colorTextureFormat = colorTextureFormat;
        this.depthTextureFormat = depthTextureFormat;
        this.colorAttachment = colorAttachment;
        this.depthAttachment = depthAttachment;
    }

    public static final FramebufferFormat VARIANCE_FORMAT =
            new FramebufferFormat(TextureFormat.VARIANCE_SHADOWMAP, TextureFormat.DEPTH_COMPONENT, GL_COLOR_ATTACHMENT0, GL_DEPTH_ATTACHMENT);

    public static final FramebufferFormat PCF_FORMAT =
            new FramebufferFormat(null, TextureFormat.DEPTH_COMPONENT, GL_COLOR_ATTACHMENT0, GL_DEPTH_ATTACHMENT);
}
