package frc.loops;

/**
 * General framework for Loopers, which take {@link Loop}s and execute groups of them.
 */
public interface LooperInterface {
    void registerLoop(Loop loop);
}