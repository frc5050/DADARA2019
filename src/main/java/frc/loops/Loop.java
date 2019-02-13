package frc.loops;

/**
 * Framework for loops, which run periodically.
 */
public interface Loop {
    /**
     * To be called on the start of the loop.
     *
     * @param timestamp the timestamp, in seconds that the function was called at.
     */
    void onStart(double timestamp);

    /**
     * To be called every iteration of the loop.
     *
     * @param timestamp the timestamp, in seconds that the function was called at.
     */
    void onLoop(double timestamp);

    /**
     * To be called on the stopping of the loop.
     *
     * @param timestamp the timestamp, in seconds that the function was called at.
     */
    void onStop(double timestamp);
}