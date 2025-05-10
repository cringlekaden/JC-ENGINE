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

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Mesh implements Runnable {

    private static final Cleaner cleaner = Cleaner.create();
    private static HashMap<String, MeshResource> loadedModels = new HashMap<>();
    private Cleaner.Cleanable cleanable;
    private Cleaner.Cleanable cleanable2;

    //TODO: proper vao
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
        glBindBuffer(GL_ARRAY_BUFFER, resource.getVBO());
        GL15.glBufferData(GL_ARRAY_BUFFER, Util.createFlippedBuffer(vertices), GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, resource.getIBO());
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, Util.createFlippedBuffer(indices), GL_STATIC_DRAW);
    }

    public void draw() {
        glBindVertexArray(resource.getVAO());
        glDrawElements(GL_TRIANGLES, resource.getSize(), GL_UNSIGNED_INT, 0);
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

    private void loadMesh(String fileName) {
        OBJModel test = new OBJModel("./res/models/" + fileName);
        IndexedModel model = test.toIndexedModel();
        model.calculateNormals();
        ArrayList<Vertex> vertices = new ArrayList<>();
        for (int i = 0; i < model.getPositions().size(); i++)
            vertices.add(new Vertex(model.getPositions().get(i), model.getTexCoords().get(i), model.getNormals().get(i)));
        Vertex[] vertexData = new Vertex[vertices.size()];
        vertices.toArray(vertexData);
        Integer[] indexData = new Integer[model.getIndices().size()];
        model.getIndices().toArray(indexData);
        addVertices(vertexData, Util.toIntArray(indexData), false);
    }
}
