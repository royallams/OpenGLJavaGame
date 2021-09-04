package Entities;

import ToolSet.Input;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;

public class Camera {
    private Vector3f position = new Vector3f(0,0,0);
    private float pitch;
    private float yaw;
    private float roll;



    public  void move() {

        if (Input.isKeyDown(GLFW.GLFW_KEY_W)){
            position.z -= 0.02f;
        }
        if (Input.isKeyDown(GLFW.GLFW_KEY_D)){
            position.x += 0.02f;
        }
        if (Input.isKeyDown(GLFW.GLFW_KEY_A)){
            position.x -= 0.02f;
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
