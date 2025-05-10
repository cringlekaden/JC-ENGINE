package engine.rendering.resources;

import engine.core.Vector3f;

import java.util.HashMap;

public abstract class MappedValues {

    private HashMap<String, Vector3f> vectorHashMap;
    private HashMap<String, Float> floatHashMap;

    public MappedValues() {
        vectorHashMap = new HashMap<>();
        floatHashMap = new HashMap<>();
    }

    public Vector3f getVector(String name) {
        Vector3f result = vectorHashMap.get(name);
        if (result != null)
            return result;
        return new Vector3f(0, 0, 0);
    }

    public void addVector(String name, Vector3f vector) {
        vectorHashMap.put(name, vector);
    }

    public float getFloat(String name) {
        Float result = floatHashMap.get(name);
        if (result != null)
            return result;
        return 0;
    }

    public void addFloat(String name, float value) {
        floatHashMap.put(name, value);
    }
}
