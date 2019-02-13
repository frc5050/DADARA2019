package frc.subsystem.test;

/**
 * General framework for subsystem tests that can be easily called to test full functionality of a given subsystem. All
 * Subsystem tests output data to Shuffleboard/SmartDashboard as well as information about what SHOULD be happening at
 * all times.
 */
public interface SubsystemTest {
    /**
     * To be called periodically, probably by {@link edu.wpi.first.wpilibj.IterativeRobotBase#testPeriodic()}.
     *
     * @param timestamp the FPGA timestamp when the periodic function was called.
     */
    void periodic(double timestamp);

    /**
     * Outputs data to Shuffleboard/SmartDashboard, should include information about what SHOULD be happening at any
     * given point.
     */
    void outputTelemetry();
}
