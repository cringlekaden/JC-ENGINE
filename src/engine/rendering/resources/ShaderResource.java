package engine.rendering.resources;

import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glCreateProgram;

public class ShaderResource implements Runnable {

    private HashMap<String, Integer> uniforms;
    private ArrayList<String> uniformNames;
    private ArrayList<String> uniformTypes;
    private int programID;
    private int refCount;

    public ShaderResource() {
        this.programID = glCreateProgram();
        if (programID == 0) {
            System.err.println("Shader program creation failed...");
            System.exit(1);
        }
        uniforms = new HashMap<>();
        uniformNames = new ArrayList<>();
        uniformTypes = new ArrayList<>();
    }

    public void addReference() {
        refCount++;
    }

    public boolean removeReference() {
        refCount--;
        return refCount == 0;
    }

    @Override
    public void run() {
        glDeleteBuffers(programID);
    }

    public int getProgramID() {
        return programID;
    }

    public HashMap<String, Integer> getUniforms() {
        return uniforms;
    }

    public ArrayList<String> getUniformNames() {
        return uniformNames;
    }

    public ArrayList<String> getUniformTypes() {
        return uniformTypes;
    }
}
