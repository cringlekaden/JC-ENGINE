package engine.rendering.textures;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

public class TextureFormat {

    public final int internalFormat;
    public final int format;
    public final int type;
    public final int target;
    public final boolean clamp;

    public TextureFormat(int internalFormat, int format, int type, int target, boolean clamp) {
        this.internalFormat = internalFormat;
        this.format = format;
        this.type = type;
        this.target = target;
        this.clamp = clamp;
    }

    // Common predefined formats
    public static final TextureFormat RGBA8 = new TextureFormat(
            GL11.GL_RGBA8,
            GL11.GL_RGBA,
            GL11.GL_UNSIGNED_BYTE,
            GL11.GL_TEXTURE_2D,
            false
    );

    public static final TextureFormat RGB8 = new TextureFormat(
            GL11.GL_RGB8,
            GL11.GL_RGB,
            GL11.GL_UNSIGNED_BYTE,
            GL11.GL_TEXTURE_2D,
            false
    );

    public static final TextureFormat DEPTH_COMPONENT = new TextureFormat(
            GL14.GL_DEPTH_COMPONENT24,
            GL11.GL_DEPTH_COMPONENT,
            GL11.GL_FLOAT,
            GL11.GL_TEXTURE_2D,
            false
    );

    public static final TextureFormat RGBA16F = new TextureFormat(
            GL30.GL_RGBA16F,
            GL11.GL_RGBA,
            GL30.GL_HALF_FLOAT,
            GL11.GL_TEXTURE_2D,
            false
    );

    public static final TextureFormat RGBA32F = new TextureFormat(
            GL30.GL_RGBA32F,
            GL11.GL_RGBA,
            GL11.GL_FLOAT,
            GL11.GL_TEXTURE_2D,
            false
    );
}