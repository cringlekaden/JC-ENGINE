package engine.rendering;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Ensures all OpenGL deletions happen on a thread with a current GL context
 * (the render thread). Cleaners enqueue delete actions here; the render loop
 * drains and executes them safely each frame.
 */
public final class GLDisposer {
    private static final ConcurrentLinkedQueue<Runnable> QUEUE = new ConcurrentLinkedQueue<>();

    private GLDisposer() {}

    public static void enqueue(Runnable action) {
        if (action != null) {
            QUEUE.add(action);
        }
    }

    /** Drain pending actions; call from render thread while context is current. */
    public static void drain() {
        Runnable r;
        while ((r = QUEUE.poll()) != null) {
            try {
                r.run();
            } catch (Throwable t) {
                // Swallow to avoid destabilizing render loop; consider logging if needed.
                t.printStackTrace();
            }
        }
    }

    /** Drain everything; call before destroying the context on shutdown. */
    public static void drainAll() {
        drain();
    }
}
