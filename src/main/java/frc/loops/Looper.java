package frc.loops;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.ArrayList;
import java.util.List;

import static frc.utils.Constants.LOOPER_SHUFFLEBOARD;

public class Looper implements LooperInterface {
    public static final double PERIOD = 0.01;
    private final Notifier notifier;
    private final List<Loop> loops;
    private final Object runningLock = new Object();
    private boolean running;
    private double timestamp = 0.0;
    private double dt = 0.0;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            synchronized (runningLock) {
                if (running) {
                    double now = Timer.getFPGATimestamp();
                    for (Loop loop : loops) {
                        loop.onLoop(now);
                    }
                    dt = now - timestamp;
                    timestamp = now;
                }
            }
        }
    };

    public Looper() {
        notifier = new Notifier(runnable);
        running = false;
        loops = new ArrayList<>();
    }

    public synchronized void registerLoop(Loop loop) {
        synchronized (runningLock) {
            loops.add(loop);
        }
    }

    public synchronized void start() {
        if (!running) {
            synchronized (runningLock) {
                timestamp = Timer.getFPGATimestamp();
                for (Loop loop : loops) {
                    loop.onStart(timestamp);
                }
                running = true;
            }
            notifier.startPeriodic(PERIOD);
        }
    }

    public synchronized void stop() {
        if (running) {
            notifier.stop();
            synchronized (runningLock) {
                running = false;
                timestamp = Timer.getFPGATimestamp();
                for (Loop loop : loops) {
                    loop.onStop(timestamp);
                }
            }
        }
    }

    public void outputTelemetry() {
        LOOPER_SHUFFLEBOARD.putNumber("Looper dt", dt * 100);
    }
}