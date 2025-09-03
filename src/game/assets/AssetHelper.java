package game.assets;

import java.io.File;

public final class AssetHelper {
    private AssetHelper() {}

    public static String textureOrFallback(String candidateRelativePath, String fallbackFileName) {
        // candidateRelativePath may contain subdirectories like "external/pack/tex.png"
        File f = new File("./res/textures/" + candidateRelativePath);
        if (f.exists() && f.isFile()) {
            return candidateRelativePath;
        }
        return fallbackFileName;
    }

    public static String modelOrFallback(String candidateRelativePath, String fallbackFileName) {
        File f = new File("./res/models/" + candidateRelativePath);
        if (f.exists() && f.isFile()) {
            return candidateRelativePath;
        }
        return fallbackFileName;
    }
}
