package engine.rendering;

import engine.rendering.resources.MappedValues;
import engine.rendering.textures.Texture;

public class Material extends MappedValues {

    public Material(Texture diffuse, float specularIntensity, float specularExponent) {
        this(diffuse, specularIntensity, specularExponent, new Texture("default_normal.jpg"),
                new Texture("default_disp.png"), 0, 0);
    }

    public Material(Texture diffuse, float specularIntensity, float specularExponent, Texture normal,
                    Texture dispMap, float dispMapScale, float dispMapBias) {
        super();
        setTexture("diffuse", diffuse);
        setFloat("specularIntensity", specularIntensity);
        setFloat("specularExponent", specularExponent);
        setTexture("normalMap", normal);
        setTexture("dispMap", dispMap);
        float baseBias = dispMapScale / 2.0f;
        setFloat("dispMapScale", dispMapScale);
        setFloat("dispMapBias", -baseBias + baseBias * dispMapBias);
    }
}
