package frc.subsystem;

import frc.loops.LooperInterface;

/**
 * Basic framework for all robot subsystems. Each subsystem can outputs data to Shuffleboard/SmartDashboard,
 * has a stop routine (for after each match), and a routine to zero all sensors.
 * <p>
 * Subsystems should only have one instance since there is no way there could be multiple drivebases. Thus, the
 * constructor on all subsystems should be private and to access a subsystem, a static function to get the instance of
 * the subsystem should be used.
 */
public abstract class Subsystem {

    /**
     * Outputs telemetry to NetworkTables via Shuffleboard or SmartDashboard.
     */
    public abstract void outputTelemetry();

    /**
     * Stops the subsystem.
     */
    public abstract void stop();

    /**
     * Writes values to motors and other outputs periodically to avoid any accidental overloading.
     */
    public void writePeriodicOutputs() {

    }

    /**
     * Reads values from motors and other inputs periodically to avoid any accidental overloading.
     */
    public void readPeriodicInputs() {

    }

    /**
     * Zeros all sensors in the subsystem.
     */
    public void zeroSensors() {

    }

    /**
     * Registers an enabled looper to periodically call any {@link frc.loops.Loop} the subsystem provides to the looper.
     *
     * @param enabledLooper the {@link LooperInterface} to register the subsystem with.
     */
    public void registerEnabledLoops(LooperInterface enabledLooper) {

    }
}