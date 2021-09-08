package Entities;

import ToolSet.Input;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;

public class Camera {
    private Vector3f position = new Vector3f(0,20,0);
    private float pitch = 0;// Tell the up and down movement
    private float yaw = 0; // Tells the left and right movement
    private float roll = 0;



    public  void move() {

        // Since we are moving the world , the values passed are negative of the actual value.

        if (Input.isKeyDown(GLFW.GLFW_KEY_W)){
            position.z -= 0.11f; // Come Near
        }
        if (Input.isKeyDown(GLFW.GLFW_KEY_S)){
            position.z += 0.11f; //Go Far
        }
        if (Input.isKeyDown(GLFW.GLFW_KEY_D)){
            position.x -= 0.11f; //Go right
        }
        if (Input.isKeyDown(GLFW.GLFW_KEY_A)){
            position.x += 0.11f;// Go Left
        }
    }
    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }
}
