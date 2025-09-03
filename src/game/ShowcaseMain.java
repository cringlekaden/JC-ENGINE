package game;

import engine.core.CoreEngine;

public class ShowcaseMain {
    public static void main(String... args) {
        // 1440p window to stress larger textures
        CoreEngine engine = new CoreEngine(2560, 1440, 120, new ShowcaseGame());
        engine.createWindow("JavaCup | Showcase Scene");
        engine.start();
    }
}
