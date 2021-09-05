package ToolSet;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;


// CConnects with window through call back functions and set the true or false status of the keyboard keys, mouse buttons and the cursor postions.
public class Input {

    private static boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];
    private static boolean[] buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static double mouseX, mouseY;


     private GLFWKeyCallback keyboard;
     private GLFWCursorPosCallback mouseMove;
     private GLFWMouseButtonCallback mouseButtons;

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

    }
}
