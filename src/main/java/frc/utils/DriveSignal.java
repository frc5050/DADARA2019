package frc.utils;

public class DriveSignal {
    public static final DriveSignal NEUTRAL = new DriveSignal(0, 0, false);
    public static final DriveSignal BRAKE = new DriveSignal(0, 0, true);

    private double leftOutput;
    private double rightOutput;
    private boolean brake;

    public DriveSignal(double left, double right) {
        this(left, right, true);
    }

    public DriveSignal(double left, double right, boolean brake) {
        this.leftOutput = left;
        this.rightOutput = right;
        this.brake = brake;
    }

    public double getLeftOutput() {
        return leftOutput;
    }

    public double getRightOutput() {
        return rightOutput;
    }

    public boolean getBrakeMode() {
        return brake;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DriveSignal) {
            return ((DriveSignal) o).brake == this.brake
                    && ((DriveSignal) o).leftOutput == this.leftOutput
                    && ((DriveSignal) o).rightOutput == this.rightOutput;
        }
        return false;
    }

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