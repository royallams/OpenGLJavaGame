package ToolSet;

import org.lwjgl.assimp.AIVector2D;
import org.lwjgl.glfw.*;
import org.lwjgl.util.vector.Vector2f;


// Connects with window through call back functions and set the true or false status of the keyboard keys, mouse buttons and the cursor postions.
public class Input {

    private static boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];
    private static boolean[] buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static double mouseX = 0, mouseY = 0;
    private static double previousMouseX = 0, previousMouseY  = 0;

    private static double dmouseX = 0   , dmouseY = 0;

    private static double xScrollPosition  , yScrollPosition = 0 ;
    private static double dxScrollPosition  = 0, dyScrollPosition = 0;


     private GLFWKeyCallback keyboard;
     private GLFWCursorPosCallback mouseMove;
     private GLFWMouseButtonCallback mouseButtons;
    private GLFWScrollCallback mouseScroll;


    public static boolean isKeyDown(int key){
        return keys[key];
    }

    public static boolean isButtonDown(int button){
        return buttons[button];
    }


    public  void destroy(){
        keyboard.free();
        mouseButtons.free();
        mouseMove.free();
    }
    public GLFWKeyCallback getKeyboardCallback() {
        return keyboard;
    }

    public GLFWCursorPosCallback getMouseMoveCallBack() {
        return mouseMove;
    }

    public GLFWMouseButtonCallback getMouseButtonsCallBack() {
        return mouseButtons;
    }
    public GLFWScrollCallback getMouseScrollCallBack() {
        return mouseScroll;
    }
    public Input(){
        keyboard = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                keys[key]= (action != GLFW.GLFW_RELEASE);
            }
        };



        mouseMove = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                mouseX = xpos;
                mouseY = ypos;
            }
        };

        mouseButtons = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                buttons[button] = (action!= GLFW.GLFW_RELEASE);
            }
        };

        mouseScroll = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                xScrollPosition = xoffset;
                yScrollPosition = yoffset;
            }
        };


    }

    public void updateMouseScroll(){


        dxScrollPosition = xScrollPosition;
        dyScrollPosition = yScrollPosition;

        xScrollPosition = 0;
        yScrollPosition = 0;
    }

    public void updateMouseCursorPosition(){


        dmouseX = mouseX- previousMouseX;
        dmouseY = mouseY- previousMouseY;

        previousMouseX = mouseX;
        previousMouseY = mouseY;

    }


    public static double getDWheel(){
        return dyScrollPosition;
    }

    public static double getDY(){
        return dmouseY;
    }

    public static double getDX(){
        return dmouseX;
    }



}
