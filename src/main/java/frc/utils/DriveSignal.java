package frc.utils;

/**
 * Small data structure to simplify the issuing of demands to the drivebase by encapsulating the left and right powers
 * as well as whether or not the brakes should be enabled on the drivebase motors or not.
 */
public class DriveSignal {
    /**
     * Issues a demand of zero to the motors and disables the brakes.
     */
    public static final DriveSignal NEUTRAL = new DriveSignal(0, 0, false);

    /**
     * Issues a demand of zero to the motors and enables the brakes.
     */
    public static final DriveSignal BRAKE = new DriveSignal(0, 0, true);

    private double leftOutput;
    private double rightOutput;
    private boolean brake;

    /**
     * Constructor. Brakes are enabled by default.
     *
     * @param left  the demand that should be issued to the left motor.
     * @param right the demand that should be issued to the right motor.
     */
    public DriveSignal(double left, double right) {
        this(left, right, true);
    }

    /**
     * Constructor.
     *
     * @param left  the demand that should be issued to the left motor.
     * @param right the demand that should be issued to the right motor.
     * @param brake whether or not to enable brake mode on the motor controllers.
     */
    public DriveSignal(double left, double right, boolean brake) {
        this.leftOutput = left;
        this.rightOutput = right;
        this.brake = brake;
    }

    /**
     * Gets the output to give to the left motor.
     *
     * @return the output to give to the left motor.
     */
    public double getLeftOutput() {
        return leftOutput;
    }

    /**
     * Gets the output to give to the right motor.
     *
     * @return the output to give to the right motor.
     */
    public double getRightOutput() {
        return rightOutput;
    }

    /**
     * Returns true if brake mode should be enabled, false if brake mode should be disabled.
     *
     * @return true if brake mode should be enabled, false if brake mode should be disabled.
     */
    public boolean getBrakeMode() {
        return brake;
    }

    /**
     * Compares the instance to another object and checks if they are equivalent objects or not.
     *
     * @param o the {@link Object} to check equality with.
     * @return true if the object is equivalent in every way to this object.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof DriveSignal) {
            return ((DriveSignal) o).brake == this.brake
                    && ((DriveSignal) o).leftOutput == this.leftOutput
                    && ((DriveSignal) o).rightOutput == this.rightOutput;
        }
        return false;
    }

    /**
     * Generates a hash code for the object. Implemented in order to not break the equals-hashCode relationship.
     *
     * @return a hash code based on the data stored in this instance of the object.
     */
    @Override
    public int hashCode() {
        int result = 0x434;
        int c;
        long tmp;
        c = brake ? 0 : 1;
        result = 37 * result + c;
        tmp = Double.doubleToLongBits(leftOutput);
        c = (int) (tmp ^ (tmp >>> 32));
        result = 37 * result + c;
        tmp = Double.doubleToLongBits(rightOutput);
        c = (int) (tmp ^ (tmp >>> 32));
        result = 37 * result + c;
        return result;
    }
}