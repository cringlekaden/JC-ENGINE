package engine.rendering;

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

public class Texture implements Runnable {

    private static final Cleaner cleaner = Cleaner.create();
    private static HashMap<String, TextureResource> loadedTextures = new HashMap<>();
    private TextureResource resource;
    private String fileName;
    private Cleaner.Cleanable cleanable;
    private Cleaner.Cleanable cleanable2;

    public Texture(String fileName) {
        this.fileName = fileName;
        TextureResource oldResource = loadedTextures.get(fileName);
        if (oldResource != null) {
            resource = oldResource;
            resource.addReference();
        } else {
            resource = loadTexture(fileName);
            loadedTextures.put(fileName, resource);
        }
        cleanable = cleaner.register(this, resource);
        cleanable2 = cleaner.register(this, this);
    }

    private static TextureResource loadTexture(String fileName) {
        try {
            BufferedImage image = ImageIO.read(new File("./res/textures/" + fileName));
            int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
            ByteBuffer buffer = BufferUtils.createByteBuffer(image.getHeight() * image.getWidth() * 4);
            boolean hasAlpha = image.getColorModel().hasAlpha();
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int pixel = pixels[y * image.getWidth() + x];
                    buffer.put((byte) ((pixel >> 16) & 0xFF));
                    buffer.put((byte) ((pixel >> 8) & 0xFF));
                    buffer.put((byte) (pixel & 0xFF));
                    buffer.put(hasAlpha ? (byte) ((pixel >> 24) & 0xFF) : (byte) 0xFF);
                }
            }
            buffer.flip();
            TextureResource resource = new TextureResource();
            glBindTexture(GL_TEXTURE_2D, resource.getID());
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
            return resource;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    @Override
    public void run() {
        if (resource.removeReference() && !fileName.isEmpty()) {
            loadedTextures.remove(fileName);
            destroy();
        }
    }

    public void destroy() {
        cleanable.clean();
        cleanable2.clean();
    }

    public void bind() {
        bind(0);
    }

    public void bind(int samplerSlot) {
        assert (samplerSlot >= 0 && samplerSlot <= 31);
        glActiveTexture(GL_TEXTURE0 + samplerSlot);
        glBindTexture(GL_TEXTURE_2D, resource.getID());
    }

    public int getID() {
        return resource.getID();
    }
}
