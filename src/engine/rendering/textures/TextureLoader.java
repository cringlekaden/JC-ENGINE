package engine.rendering.textures;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.*;

public class TextureLoader {

    public static class TextureData {
        public final ByteBuffer data;
        public final FloatBuffer dataF;
        public final int width, height, channels;
        public final boolean isHDR;

        private TextureData(ByteBuffer data, FloatBuffer dataF, int width, int height, int channels, boolean isHDR) {
            this.data = data;
            this.dataF = dataF;
            this.width = width;
            this.height = height;
            this.channels = channels;
            this.isHDR = isHDR;
        }
    }

    public static TextureData loadTexture(String fileName) {
        stbi_set_flip_vertically_on_load(true);
        fileName = "./res/textures/" + fileName;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);
            if (stbi_is_hdr(fileName)) {
                FloatBuffer data = stbi_loadf(fileName, w, h, comp, 4);
                if (data == null)
                    throw new RuntimeException("Failed to load HDR image: " + fileName + " Reason: " + stbi_failure_reason());
                return new TextureData(null, data, w.get(), h.get(), 4, true);
            } else {
                ByteBuffer data = stbi_load(fileName, w, h, comp, 4);
                if (data == null)
                    throw new RuntimeException("Failed to load image: " + fileName + " Reason: " + stbi_failure_reason());
                return new TextureData(data, null, w.get(), h.get(), 4, false);
            }
        }
    }

    public static void free(TextureData data) {
        if (data.isHDR) {
            stbi_image_free(data.dataF);
        } else {
            stbi_image_free(data.data);
        }
    }
}
