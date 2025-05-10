package engine.core;

import engine.rendering.RenderingEngine;
import engine.rendering.Window;

public class CoreEngine {

    private final Game game;
    private final int width;
    private final int height;
    private final double frameTime;
    private RenderingEngine renderingEngine;
    private boolean isRunning;

    public CoreEngine(int width, int height, double frameRate, Game game) {
        this.isRunning = false;
        this.game = game;
        this.width = width;
        this.height = height;
        this.frameTime = 1.0 / frameRate;
        game.setEngine(this);
    }

    private void run() {
        isRunning = true;
        int frames = 0;
        double frameCounter = 0;
        game.init();
        double lastFrameTime = Time.getTime();
        double unprocessedTime = 0;
        while (isRunning) {
            boolean render = false;
            double startTime = Time.getTime();
            double passedTime = startTime - lastFrameTime;
            lastFrameTime = startTime;
            unprocessedTime += passedTime;
            frameCounter += passedTime;
            while (unprocessedTime > frameTime) {
                render = true;
                unprocessedTime -= frameTime;
                if (Window.isCloseRequested())
                    stop();
                game.input((float) frameTime);
                InputHandler.getInstance().update();
                game.update((float) frameTime);
                if (frameCounter >= 1.0) {
                    System.out.println(frames + " fps");
                    frames = 0;
                    frameCounter = 0;
                }
            }
            if (render) {
                game.render(renderingEngine);
                Window.render();
                frames++;
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        cleanup();
    }

    public void createWindow(String title) {
        Window.createWindow(width, height, title);
        renderingEngine = new RenderingEngine();
        System.out.println(RenderingEngine.getOpenGLVersion());
    }

    public void start() {
        if (isRunning)
            return;
        run();
    }

    public void stop() {
        if (!isRunning)
            return;
        isRunning = false;
    }

    public RenderingEngine getRenderingEngine() {
        return renderingEngine;
    }

    private void cleanup() {
        Window.closeWindow();
    }
}
