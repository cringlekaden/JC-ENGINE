package engine.rendering;

import engine.rendering.resources.MappedValues;

import java.util.HashMap;

public class Material extends MappedValues {

    private HashMap<String, Texture> textureHashMap;

    public Material(Texture diffuse, float specularIntensity, float specularExponent, Texture normal,
                    Texture dispMap, float dispMapScale, float dispMapBias) {
        super();
        this.textureHashMap = new HashMap<>();
        addTexture("diffuse", diffuse);
        addFloat("specularIntensity", specularIntensity);
        addFloat("specularExponent", specularExponent);
        addTexture("normalMap", normal);
        addTexture("dispMap", dispMap);
        float baseBias = dispMapScale / 2.0f;
        addFloat("dispMapScale", dispMapScale);
        addFloat("dispMapBias", -baseBias + baseBias * dispMapBias);
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
