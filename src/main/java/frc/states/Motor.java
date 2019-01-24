package frc.states;

// TODO this was fun and all but probably useless and should be probably be removed... unless we switch over to
//  Pathfinder2 which might use these parameters
public class Motor {
    public static final Motor CIM = new Motor(12.0, 131, 2.41, 5330, 2.7);
    public static final Motor MINI_CIM = new Motor(12.0, 89, 1.41, 5840, 3.0);
    public static final Motor SPARTAN_MOTOR = new Motor(12.0, 126.145, 2.402, 5015.562, 1.170);

    public final double vbat;
    public final double stallCurrent;
    public final double stallTorque;
    public final double freeSpeed;
    public final double freeCurrent;
    public final double motorTorqueConstant;
    public final double resistance;
    public final double motorVoltageConstant;

    private Motor(double vbat, double stallCurrent, double stallTorque, double freeSpeed, double freeCurrent) {
        this.vbat = vbat;
        this.stallCurrent = stallCurrent;
        this.stallTorque = stallTorque;
        this.freeSpeed = freeSpeed;
        this.freeCurrent = freeCurrent;
        this.resistance = vbat / stallCurrent;
        this.motorTorqueConstant = stallTorque / stallCurrent;
        this.motorVoltageConstant = rpmToRadsPerSecond(freeSpeed) / (vbat - (freeCurrent * this.resistance));
    }

    private static double rpmToRadsPerSecond(double rpm) {
        // RPM / 60 = RPS
        // RPS * (2 pi rad / rotation) = rads per second
        // RPM / 60 * (2 * pi) = RPM * PI / 30
        return rpm * Math.PI / 30.0;
    }
}
