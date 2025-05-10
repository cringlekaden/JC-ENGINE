package engine.rendering;

import engine.components.BaseLight;
import engine.components.DirectionalLight;
import engine.components.PointLight;
import engine.components.SpotLight;
import engine.core.Matrix4f;
import engine.core.Transform;
import engine.core.Util;
import engine.core.Vector3f;
import engine.rendering.resources.ShaderResource;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.ref.Cleaner;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

public class Shader implements Runnable {

    private static final Cleaner cleaner = Cleaner.create();
    private static HashMap<String, ShaderResource> loadedShaders = new HashMap<>();
    private ShaderResource resource;
    private String fileName;
    private Cleaner.Cleanable cleanable;
    private Cleaner.Cleanable cleanable2;

    public Shader(String fileName) {
        this.fileName = fileName;
        ShaderResource oldResource = loadedShaders.get(fileName);
        if (oldResource != null) {
            resource = oldResource;
            resource.addReference();
        } else {
            resource = new ShaderResource();
            loadedShaders.put(fileName, resource);
            String vertexShaderText = loadShader(fileName + ".vs");
            String fragmentShaderText = loadShader(fileName + ".fs");
            addVertexShader(vertexShaderText);
            addFragmentShader(fragmentShaderText);
            compileShader();
            addAllUniforms(vertexShaderText);
            addAllUniforms(fragmentShaderText);
        }
        cleanable = cleaner.register(this, resource);
        cleanable2 = cleaner.register(this, this);
    }

    private static String loadShader(String fileName) {
        StringBuilder shaderSource = new StringBuilder();
        BufferedReader shaderReader;
        final String INCLUDE_DIRECTIVE = "#include";
        try {
            shaderReader = new BufferedReader(new FileReader("./res/shaders/" + fileName));
            String line;
            while ((line = shaderReader.readLine()) != null) {
                if (line.startsWith(INCLUDE_DIRECTIVE)) {
                    shaderSource.append(loadShader(line.substring(INCLUDE_DIRECTIVE.length() + 2, line.length() - 1)));
                } else {
                    shaderSource.append(line).append("\n");
                }
            }
            shaderReader.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return shaderSource.toString();
    }

    @Override
    public void run() {
        if (resource.removeReference() && !fileName.isEmpty()) {
            loadedShaders.remove(fileName);
            destroy();
        }
    }

    public void destroy() {
        cleanable.clean();
        cleanable2.clean();
    }

    public void updateUniforms(Transform transform, Material material, RenderingEngine renderingEngine) {
        Matrix4f modelMatrix = transform.getTransformation();
        Matrix4f mvpMatrix = renderingEngine.getMainCamera().getViewProjection().mul(modelMatrix);
        for (int i = 0; i < resource.getUniformNames().size(); i++) {
            String uniformName = resource.getUniformNames().get(i);
            String uniformType = resource.getUniformTypes().get(i);
            if (uniformType.equals("sampler2D")) {
                int samplerSlot = renderingEngine.getSamplerSlot(uniformName);
                material.getTexture(uniformName).bind(samplerSlot);
                setUniform(uniformName, samplerSlot);
            } else if (uniformName.startsWith("T_")) {
                if (uniformName.equals("T_MVP"))
                    setUniform(uniformName, mvpMatrix);
                else if (uniformName.equals("T_model"))
                    setUniform(uniformName, modelMatrix);
                else
                    throw new IllegalArgumentException(uniformName + " is not a valid component of Transform...");
            } else if (uniformName.startsWith("R_")) {
                String unprefixedUniformName = uniformName.substring(2);
                switch (uniformType) {
                    case "vec3" -> setUniform(uniformName, renderingEngine.getVector(unprefixedUniformName));
                    case "float" -> setUniform(uniformName, renderingEngine.getFloat(unprefixedUniformName));
                    case "DirectionalLight" -> setUniform(uniformName, (DirectionalLight) renderingEngine.getActiveLight());
                    case "PointLight" -> setUniform(uniformName, (PointLight) renderingEngine.getActiveLight());
                    case "SpotLight" -> setUniform(uniformName, (SpotLight) renderingEngine.getActiveLight());
                    default -> renderingEngine.updateUniformStruct(transform, material, this, uniformName, uniformType);
                }
            } else if (uniformName.startsWith("C_")) {
                if (uniformName.equals("C_cameraPosition"))
                    setUniform(uniformName, renderingEngine.getMainCamera().getTransform().getTransformedPosition());
                else
                    throw new IllegalArgumentException(uniformName + " is not a valid component of Camera...");
            } else {
                if (uniformType.equals("vec3"))
                    setUniform(uniformName, material.getVector(uniformName));
                else if (uniformType.equals("float"))
                    setUniform(uniformName, material.getFloat(uniformName));
                else
                    throw new IllegalArgumentException(uniformType + " is not a valid type in Material...");
            }
        }
    }

    public void bind() {
        glUseProgram(resource.getProgramID());
    }

    public void setUniform(String uniform, int value) {
        glUniform1i(resource.getUniforms().get(uniform), value);
    }

    public void setUniform(String uniform, float value) {
        glUniform1f(resource.getUniforms().get(uniform), value);
    }

    public void setUniform(String uniform, Vector3f vector) {
        glUniform3f(resource.getUniforms().get(uniform), vector.getX(), vector.getY(), vector.getZ());
    }

    public void setUniform(String uniform, Matrix4f matrix) {
        GL20.glUniformMatrix4fv(resource.getUniforms().get(uniform), true, Util.createFlippedBuffer(matrix));
    }

    public void setUniform(String uniform, BaseLight baseLight) {
        setUniform(uniform + ".color", baseLight.getColor());
        setUniform(uniform + ".intensity", baseLight.getIntensity());
    }

    public void setUniform(String uniform, DirectionalLight directionalLight) {
        setUniform(uniform + ".baseLight", (BaseLight) directionalLight);
        setUniform(uniform + ".direction", directionalLight.getDirection());
    }

    public void setUniform(String uniform, PointLight pointLight) {
        setUniform(uniform + ".baseLight", (BaseLight) pointLight);
        setUniform(uniform + ".atten.constant", pointLight.getAttenuation().getConstant());
        setUniform(uniform + ".atten.linear", pointLight.getAttenuation().getLinear());
        setUniform(uniform + ".atten.exponent", pointLight.getAttenuation().getExponent());
        setUniform(uniform + ".position", pointLight.getTransform().getPosition());
        setUniform(uniform + ".range", pointLight.getRange());
    }

    public void setUniform(String uniform, SpotLight spotLight) {
        setUniform(uniform + ".pointLight", (PointLight) spotLight);
        setUniform(uniform + ".direction", spotLight.getDirection());
        setUniform(uniform + ".cutoff", spotLight.getCutoff());
    }

    private void addVertexShader(String text) {
        addProgram(text, GL_VERTEX_SHADER);
    }

    private void addFragmentShader(String text) {
        addProgram(text, GL_FRAGMENT_SHADER);
    }

    private void addGeometryShader(String text) {
        addProgram(text, GL_GEOMETRY_SHADER);
    }

    private void compileShader() {
        glLinkProgram(resource.getProgramID());
        if (glGetProgrami(resource.getProgramID(), GL_LINK_STATUS) == 0) {
            System.err.println("Shader program link failed...");
            System.err.println(glGetProgramInfoLog(resource.getProgramID(), 1024));
            System.exit(1);
        }
        glValidateProgram(resource.getProgramID());
        if (glGetProgrami(resource.getProgramID(), GL_VALIDATE_STATUS) == 0) {
            System.err.println("Shader program validate failed...");
            System.err.println(glGetProgramInfoLog(resource.getProgramID(), 1024));
            System.exit(1);
        }
    }

    private void addAllUniforms(String shaderText) {
        HashMap<String, ArrayList<GLSLStruct>> structs = findUniformStructs(shaderText);
        final String UNIFORM_KEYWORD = "uniform";
        int uniformStartLocation = shaderText.indexOf(UNIFORM_KEYWORD);
        while (uniformStartLocation != -1) {
            if (!(uniformStartLocation != 0
                    && (Character.isWhitespace(shaderText.charAt(uniformStartLocation - 1)) || shaderText.charAt(uniformStartLocation - 1) == ';')
                    && Character.isWhitespace(shaderText.charAt(uniformStartLocation + UNIFORM_KEYWORD.length())))) {
                uniformStartLocation = shaderText.indexOf(UNIFORM_KEYWORD, uniformStartLocation + UNIFORM_KEYWORD.length());
                continue;
            }
            int begin = uniformStartLocation + UNIFORM_KEYWORD.length() + 1;
            int end = shaderText.indexOf(";", begin);
            String uniformLine = shaderText.substring(begin, end).trim();
            int whiteSpace = uniformLine.indexOf(' ');
            String uniformName = uniformLine.substring(whiteSpace + 1).trim();
            String uniformType = uniformLine.substring(0, whiteSpace).trim();
            resource.getUniformNames().add(uniformName);
            resource.getUniformTypes().add(uniformType);
            addUniform(uniformName, uniformType, structs);
            uniformStartLocation = shaderText.indexOf(UNIFORM_KEYWORD, uniformStartLocation + UNIFORM_KEYWORD.length());
        }
    }

    private void addUniform(String uniformName, String uniformType, HashMap<String, ArrayList<GLSLStruct>> structs) {
        boolean shouldAdd = true;
        ArrayList<GLSLStruct> structMembers = structs.get(uniformType);
        if (structMembers != null) {
            shouldAdd = false;
            for (GLSLStruct struct : structMembers)
                addUniform(uniformName + "." + struct.name, struct.type, structs);
        }
        if (!shouldAdd)
            return;
        int uniformLocation = glGetUniformLocation(resource.getProgramID(), uniformName);
        if (uniformLocation == 0xFFFFFFFF) {
            System.err.println("Error: Could not find uniform: " + uniformName);
            new Exception().printStackTrace();
            System.exit(1);
        }
        resource.getUniforms().put(uniformName, uniformLocation);
    }

    private HashMap<String, ArrayList<GLSLStruct>> findUniformStructs(String shaderText) {
        HashMap<String, ArrayList<GLSLStruct>> result = new HashMap<>();
        final String STRUCT_KEYWORD = "struct";
        int structStartLocation = shaderText.indexOf(STRUCT_KEYWORD);
        while (structStartLocation != -1) {
            if (!(structStartLocation != 0
                    && (Character.isWhitespace(shaderText.charAt(structStartLocation - 1)) || shaderText.charAt(structStartLocation - 1) == ';')
                    && Character.isWhitespace(shaderText.charAt(structStartLocation + STRUCT_KEYWORD.length())))) {
                structStartLocation = shaderText.indexOf(STRUCT_KEYWORD, structStartLocation + STRUCT_KEYWORD.length());
                continue;
            }
            int nameBegin = structStartLocation + STRUCT_KEYWORD.length() + 1;
            int braceBegin = shaderText.indexOf("{", nameBegin);
            int braceEnd = shaderText.indexOf("}", braceBegin);
            String structName = shaderText.substring(nameBegin, braceBegin).trim();
            ArrayList<GLSLStruct> glslStructs = new ArrayList<>();
            int componentSemicolonPos = shaderText.indexOf(";", braceBegin);
            while (componentSemicolonPos != -1 && componentSemicolonPos < braceEnd) {
                int componentNameEnd = componentSemicolonPos + 1;
                while (Character.isWhitespace(shaderText.charAt(componentNameEnd - 1)) || shaderText.charAt(componentNameEnd - 1) == ';')
                    componentNameEnd--;
                int componentNameStart = componentSemicolonPos;
                while (!Character.isWhitespace(shaderText.charAt(componentNameStart - 1)))
                    componentNameStart--;
                int componentTypeEnd = componentNameStart;
                while (Character.isWhitespace(shaderText.charAt(componentTypeEnd - 1)))
                    componentTypeEnd--;
                int componentTypeStart = componentTypeEnd;
                while (!Character.isWhitespace(shaderText.charAt(componentTypeStart - 1)))
                    componentTypeStart--;
                String componentName = shaderText.substring(componentNameStart, componentNameEnd);
                String componentType = shaderText.substring(componentTypeStart, componentTypeEnd);
                GLSLStruct glslStruct = new GLSLStruct();
                glslStruct.name = componentName;
                glslStruct.type = componentType;
                glslStructs.add(glslStruct);
                componentSemicolonPos = shaderText.indexOf(";", componentSemicolonPos + 1);
            }
            result.put(structName, glslStructs);
            structStartLocation = shaderText.indexOf(STRUCT_KEYWORD, structStartLocation + STRUCT_KEYWORD.length());
        }
        return result;
    }

    private void addProgram(String text, int type) {
        int shader = glCreateShader(type);
        if (shader == 0) {
            System.err.println("Shader program add failed pre-compilation...");
            System.exit(1);
        }
        glShaderSource(shader, text);
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            System.err.println("Shader compile failed...");
            System.err.println(glGetShaderInfoLog(shader, 1024));
            System.exit(1);
        }
        glAttachShader(resource.getProgramID(), shader);
    }

    private static class GLSLStruct {
        public String name;
        public String type;
    }
}
