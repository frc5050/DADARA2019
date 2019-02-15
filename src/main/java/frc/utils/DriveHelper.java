package frc.utils;

/**
 * Helper functions for taking human inputs from the drivers and converting them to outputs to the robot.
 */
public class DriveHelper {
    public static final double kDefaultQuickStopThreshold = 0.2;
    public static final double kDefaultQuickStopAlpha = 0.1;
    /**
     * The default deadband to use for tank drives.
     */
    static double TANK_DEFAULT_DEADBAND = 0.02;
    /**
     * The default deadband to use for arcade drive.
     */
    static double ARCADE_DEFAULT_DEADBAND = 0.02;
    private double quickStopAccumulator;

    /**
     * Constructor. Private to avoid instantiation.
     */
    public DriveHelper() {

    }

    /**
     * Converts left and right speeds into a {@link DriveSignal}, applies limiting and deadbands as well.
     *
     * @param leftSpeed  the left input speed from the joystick.
     * @param rightSpeed the right input speed from the joystick.
     * @return a new {@link DriveSignal} with the values to output to the motor, after limiting and deadbands are
     * applied.
     */
    public static DriveSignal tankToDriveSignal(double leftSpeed, double rightSpeed) {
        return tankToDriveSignal(leftSpeed, rightSpeed, true);
    }

    /**
     * Converts left and right speeds into a {@link DriveSignal}, applies limiting and deadbands as well.
     *
     * @param leftSpeed    the left input speed from the joystick.
     * @param rightSpeed   the right input speed from the joystick.
     * @param squareInputs true if the inputs should be squared, false if they should be interpreted linearly.
     * @return a new {@link DriveSignal} with the values to output to the motor, after limiting and deadbands are
     * applied.
     */
    public static DriveSignal tankToDriveSignal(double leftSpeed, double rightSpeed, boolean squareInputs) {
        return tankToDriveSignal(leftSpeed, rightSpeed, squareInputs, TANK_DEFAULT_DEADBAND);
    }

    /**
     * Converts left and right speeds into a {@link DriveSignal}, applies limiting and deadbands as well.
     *
     * @param leftSpeed    the left input speed from the joystick.
     * @param rightSpeed   the right input speed from the joystick.
     * @param squareInputs true if the inputs should be squared, false if they should be interpreted linearly.
     * @param deadband     the area at which powers should be rounded to zero and assumed to be zero, either due to input
     *                     noise or the output being unable to properly handle such low outputs.
     * @return a new {@link DriveSignal} with the values to output to the motor, after limiting and deadbands are
     * applied.
     */
    public static DriveSignal tankToDriveSignal(double leftSpeed, double rightSpeed, boolean squareInputs, double deadband) {
        leftSpeed = limit(leftSpeed);
        leftSpeed = applyDeadband(leftSpeed, deadband);
        rightSpeed = limit(rightSpeed);
        rightSpeed = applyDeadband(rightSpeed, deadband);
        if (squareInputs) {
            leftSpeed = Math.copySign(leftSpeed * leftSpeed, leftSpeed);
            rightSpeed = Math.copySign(rightSpeed * rightSpeed, rightSpeed);
        }

        return new DriveSignal(leftSpeed, rightSpeed);
    }

    /**
     * Converts speed and turn values into a {@link DriveSignal}, applies limiting and deadbands as well.
     *
     * @param x         the "speed" input speed from the joystick.
     * @param zRotation the "turn" input speed from the joystick.
     * @return a new {@link DriveSignal} with the values to output to the motor, after limiting and deadbands are
     * applied.
     */
    public static DriveSignal arcadeToDriveSignal(double x, double zRotation) {
        return arcadeToDriveSignal(x, zRotation, true);
    }

    /**
     * Converts speed and turn values into a {@link DriveSignal}, applies limiting and deadbands as well.
     *
     * @param x            the "speed" input speed from the joystick.
     * @param zRotation    the "turn" input speed from the joystick.
     * @param squareInputs true if the inputs should be squared, false if they should be interpreted linearly.
     * @return a new {@link DriveSignal} with the values to output to the motor, after limiting and deadbands are
     * applied.
     */
    public static DriveSignal arcadeToDriveSignal(double x, double zRotation, boolean squareInputs) {
        return arcadeToDriveSignal(x, zRotation, squareInputs, ARCADE_DEFAULT_DEADBAND);
    }

    /**
     * Converts speed and turn values into a {@link DriveSignal}, applies limiting and deadbands as well.
     *
     * @param x            the "speed" input speed from the joystick.
     * @param zRotation    the "turn" input speed from the joystick.
     * @param squareInputs true if the inputs should be squared, false if they should be interpreted linearly.
     * @param deadband     the area at which powers should be rounded to zero and assumed to be zero, either due to input
     *                     noise or the output being unable to properly handle such low outputs.
     * @return a new {@link DriveSignal} with the values to output to the motor, after limiting and deadbands are
     * applied.
     */
    public static DriveSignal arcadeToDriveSignal(double x, double zRotation, boolean squareInputs, double deadband) {
        x = applyDeadband(limit(x), deadband);
        zRotation = applyDeadband(limit(zRotation), deadband);

        if (squareInputs) {
            x = Math.copySign(x * x, x);
            zRotation = Math.copySign(zRotation * zRotation, zRotation);
        }

        double maxInput = Math.copySign(Math.max(Math.abs(x), Math.abs(zRotation)), x);
        double leftMotorPower;
        double rightMotorPower;

        if (x >= 0.0) {
            if (zRotation >= 0.0) {
                leftMotorPower = maxInput;
                rightMotorPower = x - zRotation;
            } else {
                leftMotorPower = x + zRotation;
                rightMotorPower = maxInput;
            }
        } else {
            if (zRotation >= 0.0) {
                leftMotorPower = x + zRotation;
                rightMotorPower = maxInput;
            } else {
                leftMotorPower = maxInput;
                rightMotorPower = x - zRotation;
            }
        }
        return new DriveSignal(leftMotorPower, rightMotorPower);
    }

    /**
     * Coerces the input to be in the range [-1.0, 1.0].
     *
     * @param val the input to limit.
     * @return the value coerced into the range [-1.0, 1.0].
     */
    private static double limit(double val) {
        if (val > 1.0) {
            return 1.0;
        } else if (val < -1.0) {
            return -1.0;
        }
        return val;
    }

    /**
     * Applies the deadband parameter to an input. Preserves an input value of 1.0 still resulting in an output of 1.0
     * while every value less than or equal to the deadband results in an output of 0.
     *
     * @param value    the input value to have the deadband applied to, on the range [-1.0, 1.0].
     * @param deadband the deadband, the minimum magnitude at which an output is considered valid.
     * @return 0 if the magnitude of the value is less than or equal to the deadband, and a value on the range
     * [-1.0, 1.0] if the magnitude of the input is greater than the deadband.
     */
    private static double applyDeadband(double value, double deadband) {
        if (Math.abs(value) > deadband) {
            if (value > 0.0) {
                return (value - deadband) / (1.0 - deadband);
            } else {
                return (value + deadband) / (1.0 - deadband);
            }
        } else {
            return 0.0;
        }
    }

    private static double upperBoundForCurvature(double x){
        x = Math.abs(x);
        if(x > 0.0842){
            return (x * 0.6) * (x * 0.6) + 0.02;
        } else {
            return Math.sin(Math.toRadians(20.0)) * x;
        }
    }

    private static double lowerBoundForCurvature(double x){
        return -upperBoundForCurvature(x);
    }

    public DriveSignal curvatureDrive(double xSpeed, double zRotation) {
        boolean quickTurn = xSpeed <= upperBoundForCurvature(zRotation) && xSpeed >= lowerBoundForCurvature(zRotation);
        return curvatureDrive(xSpeed, zRotation, 0.2, quickTurn);
    }

    public DriveSignal curvatureDrive(double xSpeed, double zRotation, boolean quickTurn) {
        return curvatureDrive(xSpeed, zRotation, 0.2, quickTurn);
    }

    public DriveSignal curvatureDrive(double xSpeed, double zRotation, double deadband, boolean isQuickTurn) {
        xSpeed = limit(xSpeed);
        xSpeed = applyDeadband(xSpeed, deadband);

        zRotation = limit(zRotation);
        zRotation = applyDeadband(zRotation, deadband);

        double angularPower;
        boolean overPower;

        if (isQuickTurn) {
            if (Math.abs(xSpeed) < kDefaultQuickStopThreshold) {
                quickStopAccumulator = (1 - kDefaultQuickStopAlpha) * quickStopAccumulator
                        + kDefaultQuickStopAlpha * limit(zRotation) * 2;
            }
            overPower = true;
            angularPower = zRotation;
        } else {
            overPower = false;
            angularPower = Math.abs(xSpeed) * zRotation - quickStopAccumulator;

            if (quickStopAccumulator > 1) {
                quickStopAccumulator -= 1;
            } else if (quickStopAccumulator < -1) {
                quickStopAccumulator += 1;
            } else {
                quickStopAccumulator = 0.0;
            }
        }

        double leftMotorOutput = xSpeed + angularPower;
        double rightMotorOutput = xSpeed - angularPower;

        // If rotation is overpowered, reduce both outputs to within acceptable range
        if (overPower) {
            if (leftMotorOutput > 1.0) {
                rightMotorOutput -= leftMotorOutput - 1.0;
                leftMotorOutput = 1.0;
            } else if (rightMotorOutput > 1.0) {
                leftMotorOutput -= rightMotorOutput - 1.0;
                rightMotorOutput = 1.0;
            } else if (leftMotorOutput < -1.0) {
                rightMotorOutput -= leftMotorOutput + 1.0;
                leftMotorOutput = -1.0;
            } else if (rightMotorOutput < -1.0) {
                leftMotorOutput -= rightMotorOutput + 1.0;
                rightMotorOutput = -1.0;
            }
        }

        // Normalize the wheel speeds
        double maxMagnitude = Math.max(Math.abs(leftMotorOutput), Math.abs(rightMotorOutput));
        if (maxMagnitude > 1.0) {
            leftMotorOutput /= maxMagnitude;
            rightMotorOutput /= maxMagnitude;
        }

        return new DriveSignal(leftMotorOutput, rightMotorOutput);
    }
}