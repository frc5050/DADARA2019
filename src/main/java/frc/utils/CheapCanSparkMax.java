package frc.utils;

import com.revrobotics.CANSparkMax;

/**
 * Small wrapper around {@link CANSparkMax} to reduce CAN usage and overhead. Only flushes output when the
 * desired parameters are different than the current parameters.
 */
public class CheapCanSparkMax extends CANSparkMax {
    private double lastSpeed = Double.NaN;

    /**
     * Constructor.
     *
     * @param deviceId the device ID of the Spark MAX, as addressed on the CAN network.
     */
    public CheapCanSparkMax(int deviceId, MotorType motorType) {
        super(deviceId, motorType);
    }

    @Override
    public void set(double speed) {
        if (speed != lastSpeed) {
            super.set(speed);
            lastSpeed = speed;
        }
    }
}
