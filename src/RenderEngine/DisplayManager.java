package RenderEngine;


        import Entities.Camera;
        import Entities.Entity;
        import Entities.Light;
        import Entities.Player;
        import Models.RawModel;
        import Models.TexturedModel;
        import Shaders.StaticShader;
        import Terrains.Terrain;
        import Textures.ModelTexture;
        import Textures.TerrainTexture;
        import Textures.TerrainTexturePack;
        import ToolSet.Input;
        import jdk.nashorn.internal.objects.NativeDate;
        import org.lwjgl.*;
        import org.lwjgl.glfw.*;
        import org.lwjgl.opengl.*;
        import org.lwjgl.system.*;
        import org.lwjgl.util.vector.Vector3f;
        import org.newdawn.slick.tests.TestUtils;

        import java.nio.*;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.Random;

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

    private  static double lastFrameTime;
    private static double delta;

    private  static double lastMouseScrollX, getLastMouseScrollY;



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
        window = glfwCreateWindow(800, 800, "Hello World!", NULL, NULL);
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
        GLFW.glfwSetScrollCallback(window,input.getMouseScrollCallBack());




        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);


        // INTIAL CREATION TIME

        lastFrameTime = getCurrentTime();
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();


        Loader loader = new Loader();
        Light light = new Light(new Vector3f(0,10,0),new Vector3f(1,1,1));

        // Create VAO, VBO, Index buffers, and return the final rawmodel (VAO+numberofIndices)
        RawModel model =  OBJLoader.loadObjModel("tree",loader);
        TexturedModel staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("tree")));
        TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader),new ModelTexture(loader.loadTexture("grassTexture")));
        TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern",loader),new ModelTexture(loader.loadTexture("fern")));
        fern.getTexture().setHasTransparency(true);
        TexturedModel newTree = new TexturedModel(OBJLoader.loadObjModel("lowPolyTree",loader),new ModelTexture(loader.loadTexture("lowPolyTree")));





        //***************** TERRAIN TEXTURE MULTI TEXTURE INPUTS************


        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture,bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));


        Terrain terrain = new Terrain(0,-1,loader, texturePack, blendMap,"heightmap");
        Terrain terrain2 = new Terrain(-1,-1,loader, texturePack, blendMap,"heightmap");


        Input input = new Input();// Creates static call back functions to handle keyboard, mouse and the cursor

        // List of random entities
        List<Entity> entities = new ArrayList<Entity>();
        Random random = new Random();

        for(int i =0; i<400;i++) {

            if(i%2 == 0){
                float x  = random.nextFloat() * 800 -400;
                float z = random.nextFloat() * -600;
                float y = terrain.getHeightOfTerrain(x,z);
                entities.add(new Entity(fern, new Vector3f(x,y,z),0,random.nextFloat()*360, 0 ,random.nextFloat() * 0.1f + 0.6f));
            }
            if(i % 2 == 0){
                float x  = random.nextFloat() * 800 -400;
                float z = random.nextFloat() * -600;
                float y = terrain.getHeightOfTerrain(x,z);
                entities.add(new Entity(newTree, new Vector3f(x,y,z),0,random.nextFloat()*360, 0 ,random.nextFloat() * 0.1f + 0.6f));

                x  = random.nextFloat() * 800 -400;
                z = random.nextFloat() * -600;
                y = terrain.getHeightOfTerrain(x,z);
                entities.add(new Entity(staticModel, new Vector3f(x,y,z),0,random.nextFloat()*360, 0 ,random.nextFloat() * 1 + 4));


            }


//            entities.add(new Entity(staticModel,new Vector3f(random.nextFloat()*800-400,0,random.nextFloat()*-600),0,0,0,3));
//            entities.add(new Entity(grass,new Vector3f(random.nextFloat()*800-400,0,random.nextFloat()*-600),0,0,0,3));
//            entities.add(new Entity(fern,new Vector3f(random.nextFloat()*800-400,0,random.nextFloat()*-600),0,0,0,3));

        }

        RawModel bunnyModel = OBJLoader.loadObjModel("person", loader);
        TexturedModel stanfordBunny = new TexturedModel(bunnyModel, new ModelTexture(loader.loadTexture("playerTexture")));


        Player player = new Player(stanfordBunny, new Vector3f(100,10,-50), 0, 0,0,1);
        player.getModel().getTexture().setReflectivity(0);
        player.getModel().getTexture().setShineDamper(0);


        Camera camera = new Camera(player);




        MasterRenderer masterRenderer = new MasterRenderer();
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            camera.move();
            player.move(terrain);
            masterRenderer.processEntity(player);
            masterRenderer.processTerrain(terrain);
//            masterRenderer.processTerrain(terrain2);
//            masterRenderer.processEntity(entity);
            // First process the entities to its textured Model
            for(Entity single_entity : entities){
                masterRenderer.processEntity(single_entity);
             }

            //Master Rendering Finally
            masterRenderer.render(light,camera);
            UpdateDisplay();


        }

        masterRenderer.cleanUp();
        loader.cleanUp();
        input.destroy();//Cleanup callback buffers after use. This is not a static function.

    }

    public void UpdateDisplay(){
        glfwSwapBuffers(window); // swap the color buffers
        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();

        double currentFrameTime = getCurrentTime();
        delta = (currentFrameTime - lastFrameTime);//in sec
        lastFrameTime = currentFrameTime;

        input.updateMouseScroll();
        input.updateMouseCursorPosition();

    }



    public  static  double getFrameTimeSeconds(){
        return delta;
    }
    public static  IntBuffer getpWidth_() {
        return pWidth_;
    }

    public static  IntBuffer getpHeight_() {
        return pHeight_;
    }

    private static  double getCurrentTime(){
        return GLFW.glfwGetTime();
    }
}