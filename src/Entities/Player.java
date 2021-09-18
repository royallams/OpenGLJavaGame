package Entities;

import Models.TexturedModel;
import RenderEngine.DisplayManager;
import ToolSet.Input;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import javax.swing.plaf.basic.BasicSplitPaneUI;

public class Player extends  Entity {

    private  static  final float RUN_SPEED = 20;
    private  static  final float TURN_SPEED = 160;
    private  static final  float GRAVITY = -50;
    private  static final  float JUMP_POWER = 30;

    private static  float TERRAIN_HEIGHT = 0;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private  float upwardsSpeed = 0;


    boolean isInAir = false;

    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
    }

    public void move(){
        checkInputs();
        super.increaseRotation(0, (float)(currentTurnSpeed* DisplayManager.getFrameTimeSeconds()),0);
        float distance = (float)(currentSpeed * DisplayManager.getFrameTimeSeconds());
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
        super.increasePosition(dx, 0, dz);
        upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
        super.increasePosition(0, (float)(upwardsSpeed * DisplayManager.getFrameTimeSeconds()),0);
        if(super.getPosition().y< TERRAIN_HEIGHT){
            upwardsSpeed = 0;
            isInAir = false;
            super.getPosition().y = TERRAIN_HEIGHT;
        }


    }

    private void jump(){
        if(!isInAir){
            this.upwardsSpeed = JUMP_POWER;
            isInAir = true;
        }

    }

    private  void checkInputs(){
        if(Input.isKeyDown(GLFW.GLFW_KEY_W)){
            this.currentSpeed = RUN_SPEED;
        }else if(Input.isKeyDown(GLFW.GLFW_KEY_S)){
            this.currentSpeed = - RUN_SPEED;
        }else {
            this.currentSpeed = 0;
        }

        if(Input.isKeyDown(GLFW.GLFW_KEY_D)){
            this.currentTurnSpeed = -TURN_SPEED;
        }else if(Input.isKeyDown(GLFW.GLFW_KEY_A)){
            this.currentTurnSpeed = TURN_SPEED;
        }else {
            this.currentTurnSpeed = 0;
        }


        if(Input.isKeyDown(GLFW.GLFW_KEY_SPACE)){
            jump();
        }

    }
}

