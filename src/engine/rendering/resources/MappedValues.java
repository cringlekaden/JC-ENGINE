package engine.rendering.resources;

import engine.core.Vector3f;
import engine.rendering.textures.Texture;

import java.util.HashMap;

public abstract class MappedValues {

    private HashMap<String, Texture> textureHashMap;
    private HashMap<String, Vector3f> vectorHashMap;
    private HashMap<String, Float> floatHashMap;

    public MappedValues() {
        textureHashMap = new HashMap<>();
        vectorHashMap = new HashMap<>();
        floatHashMap = new HashMap<>();
    }

    public Texture getTexture(String name) {
        Texture result = textureHashMap.get(name);
        if (result != null)
            return result;
        return new Texture("defaultTexture.png");
    }

    public void setTexture(String name, Texture texture) {
        textureHashMap.put(name, texture);
    }

    public Vector3f getVector(String name) {
        Vector3f result = vectorHashMap.get(name);
        if (result != null)
            return result;
        return new Vector3f(0, 0, 0);
    }

    public void setVector(String name, Vector3f vector) {
        vectorHashMap.put(name, vector);
    }

    public float getFloat(String name) {
        Float result = floatHashMap.get(name);
        if (result != null)
            return result;
        return 0;
    }

    public void setFloat(String name, float value) {
        floatHashMap.put(name, value);
    }
}
