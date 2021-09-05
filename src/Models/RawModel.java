package Models;

public class RawModel {

    private int vaoID;// This Id is where our model is stored in VAO
    private int vertexCount; // How many vertexs we are providning , this is importent for opengl to process later.

    public RawModel(int vaoID, int vertexCount){// RawModel simply stores the VAO id and the number of vertices.
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
    }

    public int getVaoID() {
        return vaoID;
    }

    public int getVertexCount() {
        return vertexCount;
    }
}
