package RenderEngine;

import Models.RawModel;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

// This loader helps to load the  information from the CPU to the GPU (shaders)
public class Loader {

    private List<Integer> vaos = new ArrayList<Integer>();
    private List<Integer> vbos = new ArrayList<Integer>();
    private List<Integer> textures = new ArrayList<Integer>();


    // Load the vertices, textures and the indices data to the VAO of the GPU.
    public RawModel loadToVAO(float[] positions, float [] textureCoords, int[] indices){
        int vaoID = createVAO();// Create VAO
        bindIndicesBuffer(indices);//Create Index buffers.
        storeDataInAttributeList(0,3,positions );// Create VBO to store position and pass it to opengl
        storeDataInAttributeList(1,2,textureCoords );// Create VBO to store position and pass it to opengl
        unbindVAO();// Unbind the VAO
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);//Unbind the VBO

        return new RawModel(vaoID,indices.length);// Create the Rawmodel object and return /.
    }


    private int createVAO(){
        int vaoID = GL30.glGenVertexArrays();
        vaos.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    public void cleanUp(){
        for(int vao:vaos)
        {
            GL30.glDeleteVertexArrays(vao);
        }
        for(int vbo:vbos)
        {
            GL15.glDeleteBuffers(vbo);
        }

        for(int texture:textures)
        {
            GL11.glDeleteTextures(texture);
        }
    }


    // Tells opengl where to find the data , What coordinate  it uses, and actual data.
    private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float [] data){
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER,buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber,coordinateSize, GL11.GL_FLOAT,false,0,0 );


    }

    // Create index buffers and attach the indices to  it , Bind it and prepare it for use next.
    private void bindIndicesBuffer(int[] indices){
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);// Keep track of the buffers to manage memory later.
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER,vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);// Convert the raw data into proper buffer to fit into the GLFW functions.
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER,buffer,GL15.GL_STATIC_DRAW);

    }

    // A converter to generate int buffers .
    private IntBuffer storeDataInIntBuffer(int[] data){
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();// This makes it ready for use.
        return buffer;
    }

    private void unbindVAO(){
        GL30.glBindVertexArray(0);
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data){
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }


    // Pass the file name and create Texture Buffer  and set defaults.
    public int loadTexture(String filename){

        filename = "res/"+filename;
        int id = GL13.glGenTextures();
        GL13.glBindTexture(GL13.GL_TEXTURE_2D,id);


        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);


        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        // Use STBI library to load the texture data
        ByteBuffer image = stbi_load(filename, width, height, channels, 0);
        if(image == null){
            throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());
        }


        // If channel returns only 3 values.. RGB , else.. RGBA ( Alpha is included)
        if(channels.get(0) == 3){
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width.get(0), height.get(0), 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, image);
        }
        else{
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width.get(0),height.get(0), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }

        stbi_image_free(image);
        textures.add(id);// For Clean up later.
        return id;// Texture VBO id is returned/
    }

    private java.nio.ByteBuffer readFile(String resource) throws IOException{
        File file = new File(resource);

        FileInputStream fis = new FileInputStream(file);
        FileChannel fc = fis.getChannel();

        ByteBuffer buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);

        while(fc.read(buffer) != -1);

        fis.close();
        fc.close();
        buffer.flip();

        return buffer;
    }

}
