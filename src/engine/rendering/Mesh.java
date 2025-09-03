package engine.rendering;

import engine.core.Util;
import engine.core.Vector3f;
import engine.rendering.models.IndexedModel;
import engine.rendering.models.OBJModel;
import engine.rendering.resources.MeshResource;
import org.lwjgl.opengl.GL15;

import java.lang.ref.Cleaner;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Mesh implements Runnable {

    private static final Cleaner cleaner = Cleaner.create();
    private static HashMap<String, MeshResource> loadedModels = new HashMap<>();
    private Cleaner.Cleanable cleanable;
    private Cleaner.Cleanable cleanable2;

    private MeshResource resource;
    private String fileName;

    public Mesh(String fileName) {
        this.fileName = fileName;
        MeshResource oldResource = loadedModels.get(fileName);
        if (oldResource != null) {
            resource = oldResource;
            resource.addReference();
        } else {
            loadMesh(fileName);
            loadedModels.put(fileName, resource);
        }
    }

    public Mesh(Vertex[] vertices, int[] indices) {
        this(vertices, indices, false);
    }

    public Mesh(Vertex[] vertices, int[] indices, boolean calculateNormals) {
        fileName = "";
        addVertices(vertices, indices, calculateNormals);
    }

    @Override
    public void run() {
        if (resource.removeReference() && !fileName.isEmpty()) {
            loadedModels.remove(fileName);
            destroy();
        }
    }

    private void addVertices(Vertex[] vertices, int[] indices, boolean calculateNormals) {
        if (calculateNormals)
            calculateNormals(vertices, indices);
        resource = new MeshResource(indices.length);
        cleanable = cleaner.register(this, resource);
        cleanable2 = cleaner.register(this, this);
        // Bind VAO for buffer uploads (ELEMENT_ARRAY_BUFFER is VAO state)
        glBindVertexArray(resource.getVAO());
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, resource.getVBO());
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, Util.createFlippedBuffer(vertices), GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, resource.getIBO());
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, Util.createFlippedBuffer(indices), GL15.GL_STATIC_DRAW);
        // Unbind VAO; GL_ARRAY_BUFFER binding is global, so optionally unbind it
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void draw() {
        glBindVertexArray(resource.getVAO());
        org.lwjgl.opengl.GL11.glDrawElements(org.lwjgl.opengl.GL11.GL_TRIANGLES, resource.getSize(), org.lwjgl.opengl.GL11.GL_UNSIGNED_INT, 0L);
    }

    public void destroy() {
        cleanable.clean();
        cleanable2.clean();
    }

    private void calculateNormals(Vertex[] vertices, int[] indices) {
        for (int i = 0; i < indices.length; i += 3) {
            int i0 = indices[i];
            int i1 = indices[i + 1];
            int i2 = indices[i + 2];
            Vector3f v1 = vertices[i1].getPosition().sub(vertices[i0].getPosition());
            Vector3f v2 = vertices[i2].getPosition().sub(vertices[i0].getPosition());
            Vector3f normal = v1.cross(v2).normalized();
            vertices[i0].setNormal(vertices[i0].getNormal().add(normal));
            vertices[i1].setNormal(vertices[i1].getNormal().add(normal));
            vertices[i2].setNormal(vertices[i2].getNormal().add(normal));
        }
        for (int i = 0; i < vertices.length; i++) {
            vertices[i].setNormal(vertices[i].getNormal().normalized());
        }
    }

    private Mesh loadMesh(String fileName) {
        String[] splitArray = fileName.split("\\.");
        String ext = splitArray[splitArray.length - 1];
        if(!ext.equals("obj")) {
            System.err.println("Error: '" + ext + "' file format not supported for mesh data.");
            new Exception().printStackTrace();
            System.exit(1);
        }
        OBJModel test = new OBJModel("./res/models/" + fileName);
        IndexedModel model = test.toIndexedModel();
        //model.calculateNormals();
        ArrayList<Vertex> vertices = new ArrayList<>();
        for (int i = 0; i < model.getPositions().size(); i++)
            vertices.add(new Vertex(model.getPositions().get(i), model.getTexCoords().get(i), model.getNormals().get(i), model.getTangents().get(i)));
        Vertex[] vertexData = new Vertex[vertices.size()];
        vertices.toArray(vertexData);
        Integer[] indexData = new Integer[model.getIndices().size()];
        model.getIndices().toArray(indexData);
        addVertices(vertexData, Util.toIntArray(indexData), false);
        return this;
    }
}
