package RenderEngine;


        import Entities.Camera;
        import Entities.Entity;
        import Models.RawModel;
        import Models.TexturedModel;
        import Shaders.StaticShader;
        import Textures.ModelTexture;
        import ToolSet.Input;
        import org.lwjgl.*;
        import org.lwjgl.glfw.*;
        import org.lwjgl.opengl.*;
        import org.lwjgl.system.*;
        import org.lwjgl.util.vector.Vector3f;

        import java.nio.*;

        import static org.lwjgl.glfw.Callbacks.*;
        import static org.lwjgl.glfw.GLFW.*;
        import static org.lwjgl.opengl.GL11.*;
        import static org.lwjgl.system.MemoryStack.*;
        import static org.lwjgl.system.MemoryUtil.*;

public class DisplayManager {

    // The window handle
    private long window;
    static IntBuffer pWidth_;
    static IntBuffer pHeight_;

    static Input input;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }


    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Input
        input = new Input();


        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        // Create the window
        window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });


        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            pWidth_ = pWidth;
            pHeight_ = pHeight;

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);


        GLFW.glfwSetKeyCallback(window,input.getKeyboardCallback());
        GLFW.glfwSetCursorPosCallback(window,input.getMouseMoveCallBack());
        GLFW.glfwSetMouseButtonCallback(window,input.getMouseButtonsCallBack());




        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
//        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);


        Loader loader = new Loader();
        StaticShader shader = new StaticShader();
        Renderer renderer = new Renderer(shader);

        float[] vertices = {
                -0.5f,0.5f,-0.5f,
                -0.5f,-0.5f,-0.5f,
                0.5f,-0.5f,-0.5f,
                0.5f,0.5f,-0.5f,

                -0.5f,0.5f,0.5f,
                -0.5f,-0.5f,0.5f,
                0.5f,-0.5f,0.5f,
                0.5f,0.5f,0.5f,

                0.5f,0.5f,-0.5f,
                0.5f,-0.5f,-0.5f,
                0.5f,-0.5f,0.5f,
                0.5f,0.5f,0.5f,

                -0.5f,0.5f,-0.5f,
                -0.5f,-0.5f,-0.5f,
                -0.5f,-0.5f,0.5f,
                -0.5f,0.5f,0.5f,

                -0.5f,0.5f,0.5f,
                -0.5f,0.5f,-0.5f,
                0.5f,0.5f,-0.5f,
                0.5f,0.5f,0.5f,

                -0.5f,-0.5f,0.5f,
                -0.5f,-0.5f,-0.5f,
                0.5f,-0.5f,-0.5f,
                0.5f,-0.5f,0.5f

        };

        float[] textureCoords = {

                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0


        };

        int[] indices = {
                0, 1, 3,
                3, 1, 2,
                4, 5, 7,
                7, 5, 6,
                8, 9, 11,
                11, 9, 10,
                12, 13, 15,
                15, 13, 14,
                16, 17, 19,
                19, 17, 18,
                20, 21, 23,
                23,21,22
        };

        RawModel model = loader.loadToVAO(vertices, textureCoords,indices);// Create VAO, VBO, Index buffers, and return the final rawmodel (VAO+numberofIndices)
        ModelTexture texture = new ModelTexture(loader.loadTexture("res/image1.jpeg"));
        TexturedModel texturedModel = new TexturedModel(model,texture);// SImply holds the information of the model and the texture data (VAO and the VBO ids)
        Entity entity = new Entity(texturedModel, new Vector3f(0,0,-5),0,0,0,1);// Textured Model with its initial position, translate, rotate , scale value.
        Camera camera = new Camera();
        Input input = new Input();// Creates static call back functions to handle keyboard, mouse and the cursor


        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
//            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            entity.increaseRotation(1,1,0);
            camera.move();
            entity.increasePosition(0,0,-0.002f);
//            entity.increaseRotation(0,0,-0.002f);
            renderer.prepare();
            shader.start();// Use the shader program
            shader.loadViewMatrix(camera);// This takes the camera , camera information is the View, through that it creates a view matrix and loads it to the GPU. uniform
            renderer.render(entity, shader);
            shader.stop();// Dont use the shader program
            glfwSwapBuffers(window); // swap the color buffers



            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }

        shader.cleanUp();
        loader.cleanUp();
        input.destroy();//Cleanup callback buffers after use. This is not a static function.

    }

    public static  IntBuffer getpWidth_() {
        return pWidth_;
    }

    public static  IntBuffer getpHeight_() {
        return pHeight_;
    }
}