package engine.rendering;

import engine.rendering.resources.MappedValues;

import java.util.HashMap;

public class Material extends MappedValues {

    private HashMap<String, Texture> textureHashMap;

    public Material() {
        super();
        this.textureHashMap = new HashMap<>();
        textureHashMap.put("normalMap", new Texture("default_normal.jpg"));
    }

    public Texture getTexture(String name) {
        Texture result = textureHashMap.get(name);
        if (result != null)
            return result;
        return new Texture("defaultTexture.png");
    }

    public void addTexture(String name, Texture texture) {
        textureHashMap.put(name, texture);
    }
}
