package Entities;

import ToolSet.Input;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;

public class Camera {

    private float distanceFromPlayer = 50;
    private float angleAroundPlayer = 0;

    private Vector3f position = new Vector3f(100,50,0);
    private float pitch = 50;// Tell the up and down movement
    private float yaw = 0; // Tells the left and right movement
    private float roll = 0;


    private  Player player;

    public Camera(Player player) {
        this.player = player;
    }
    public void move(){
        calculateZoom();
        calculatePitch();
        calculateAngleAroundPlayer();
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance,verticalDistance);
        this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
    }

    private float calculateHorizontalDistance(){
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }

    private float calculateVerticalDistance(){
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
    }

    private void calculateCameraPosition(float horizontalDistance, float verticalDistance){
        float theta = player.getRotY() + angleAroundPlayer;
        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;
        position.y = player.getPosition().y + verticalDistance;

    }

    private void calculateZoom(){
        float zoomLevel = ((float)Input.getDWheel()) * 1.0f;
        distanceFromPlayer -= zoomLevel;

    }

    private  void calculatePitch(){
        if(Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT)){
            float pitchChange = ((float) Input.getDY()) * 0.1f;
            pitch -= pitchChange;
        }
    }


    private  void calculateAngleAroundPlayer(){
        if(Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)){
            float angleChange = ((float) Input.getDX()) * 0.3f;
            angleAroundPlayer -= angleChange;
        }
    }


    //    public  void move() {
//
//        // Since we are moving the world , the values passed are negative of the actual value.
//
//        if (Input.isKeyDown(GLFW.GLFW_KEY_W)){
//            position.z -= 0.71f; // Come Near
//        }
//        if (Input.isKeyDown(GLFW.GLFW_KEY_S)){
//            position.z += 0.71f; //Go Far
//        }
//        if (Input.isKeyDown(GLFW.GLFW_KEY_D)){
//            position.x += 0.71f; //Go right
//        }
//        if (Input.isKeyDown(GLFW.GLFW_KEY_A)){
//            position.x -= 0.71f;// Go Left
//        }
//        if (Input.isKeyDown(GLFW.GLFW_KEY_Z)){
//            position.y -= 0.71f;// Go Left
//        }
//        if (Input.isKeyDown(GLFW.GLFW_KEY_X)){
//            position.y += 0.71f;// Go Left
//        }
//    }
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
