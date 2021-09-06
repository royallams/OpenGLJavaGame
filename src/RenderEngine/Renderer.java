package RenderEngine;

import Entities.Entity;
import Models.RawModel;
import Models.TexturedModel;
import Shaders.StaticShader;
import ToolSet.Maths;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.Display;
import org.lwjgl.util.vector.Matrix4f;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;

    private  Matrix4f projectionMatrix;

    // To make it look like 3d .Projection matrix is important /
    public Renderer(StaticShader shader){
        createProjectionMatrix();
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }


    public void prepare(){// These are for clear the buffers and preparing initial state for rendering
        GL11.glEnable(GL_DEPTH_TEST); // clear the framebuffer
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT| GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,0,0,1);
//        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

    }

    // First already the VIew Matrix is applied, now transformation matrix shall be applied.
    public void render(Entity entity, StaticShader shader){
        TexturedModel model = entity.getModel();
        RawModel rawModel = model.getRawModel();
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID() );
        GL11.glDrawElements(GL11.GL_TRIANGLES,rawModel.getVertexCount(), GL_UNSIGNED_INT,0);//Vertex count is actually an indices count here.
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);//Unbind all buffers after every use.
    }


    // These are the steps to Create projection matrix.
    // The inputs are
    // Window's width, height
    // Field of View , Near plane, Far Plane
    private void  createProjectionMatrix(){
        float aspectRatio = (float) DisplayManager.getpWidth_().get(0)/(float)DisplayManager.getpHeight_().get(0);
        float y_scale = (float) ((1f/Math.tan(Math.toRadians(FOV/2f)))*aspectRatio);
        float x_scale = y_scale/aspectRatio;
        float frustum_length = FAR_PLANE- NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m01 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE+NEAR_PLANE)/ frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2* NEAR_PLANE *FAR_PLANE)/frustum_length);
        projectionMatrix.m33 = 0;

    }
}
