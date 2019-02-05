package frc.utils;

// TODO we can do better than this for conversions, this does not exactly produce smooth driving results, just simple ones
public class DriveHelper {
    // TODO experimentally determine these? Make them zero?
    private static double TANK_DEFAULT_DEADBAND = 0.02;
    private static double ARCADE_DEFAULT_DEADBAND = 0.02;

    private DriveHelper() {

    }
    // Sets tank-drive settings to the motor signals 
    public static DriveSignal tankToDriveSignal(double leftSpeed, double rightSpeed) {
        return tankToDriveSignal(leftSpeed, rightSpeed, true);
    }

    public static DriveSignal tankToDriveSignal(double leftSpeed, double rightSpeed, boolean squareInputs) {
        return tankToDriveSignal(leftSpeed, rightSpeed, squareInputs, TANK_DEFAULT_DEADBAND);
    }
    // Applies speed and limits to motor signals on the drivebase
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
    // Sets values/speeds/limits to the motor signals to produce arcade drive
    public static DriveSignal arcadeToDriveSignal(double x, double zRotation) {
        return arcadeToDriveSignal(x, zRotation, true);
    }

    public static DriveSignal arcadeToDriveSignal(double x, double zRotation, boolean squareInputs) {
        return arcadeToDriveSignal(x, zRotation, squareInputs, ARCADE_DEFAULT_DEADBAND);
    }

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

    private static double limit(double val) {
        if (val > 1.0) {
            return 1.0;
        } else if (val < -1.0) {
            return -1.0;
        }
        return val;
    }
    // Sets power limits on the motors
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
}