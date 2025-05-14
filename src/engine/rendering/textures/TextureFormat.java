package engine.rendering.textures;

import org.lwjgl.opengl.GL11;

public class TextureFormat {

    public final int internalFormat;
    public final int format;
    public final int type;
    public final int target;

    public TextureFormat(int internalFormat, int format, int type, int target) {
        this.internalFormat = internalFormat;
        this.format = format;
        this.type = type;
        this.target = target;
    }

    // Common predefined formats
    public static final TextureFormat RGBA8 = new TextureFormat(
            org.lwjgl.opengl.GL11.GL_RGBA8,
            org.lwjgl.opengl.GL11.GL_RGBA,
            org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE,
            GL11.GL_TEXTURE_2D
    );

    public static final TextureFormat RGB8 = new TextureFormat(
            org.lwjgl.opengl.GL11.GL_RGB8,
            org.lwjgl.opengl.GL11.GL_RGB,
            org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE,
            GL11.GL_TEXTURE_2D
    );

    public static final TextureFormat DEPTH_COMPONENT = new TextureFormat(
            org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24,
            org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT,
            org.lwjgl.opengl.GL11.GL_FLOAT,
            GL11.GL_TEXTURE_2D
    );

    public static final TextureFormat RGBA16F = new TextureFormat(
            org.lwjgl.opengl.GL30.GL_RGBA16F,
            org.lwjgl.opengl.GL11.GL_RGBA,
            org.lwjgl.opengl.GL30.GL_HALF_FLOAT,
            GL11.GL_TEXTURE_2D
    );

    public static final TextureFormat RGBA32F = new TextureFormat(
            org.lwjgl.opengl.GL30.GL_RGBA32F,
            org.lwjgl.opengl.GL11.GL_RGBA,
            org.lwjgl.opengl.GL11.GL_FLOAT,
            GL11.GL_TEXTURE_2D
    );
}