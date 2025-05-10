package game;

import engine.core.CoreEngine;

public class Main {

    public static void main(String... args) {
        CoreEngine engine = new CoreEngine(1920, 1080, 120, new TestGame());
        engine.createWindow("JavaCup | 0.2.1a");
        engine.start();
    }
}
