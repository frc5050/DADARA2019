package frc.states;

// TODO this was fun and all but probably useless and should be probably be removed
@Deprecated
public class ElevatorStateMachineDeprecated {
    public static final double INCHES_TO_METERS = 0.0254;
    public static final double MAX_HEIGHT = 68 * INCHES_TO_METERS;
    public static final double MIN_HEIGHT = 0.00;
    public static final double DT = 0.01;
    public static final double ZEROING_VELOCITY = 0.01;
    private static final Motor MOTOR = Motor.MINI_CIM;
    private static final double MASS = 6.0;
    private static final double NUM_MOTORS = 2.0;
    private static final double GEAR_RATIO = 1;
    private static final double PULLEY_RADIUS = 1.732 * INCHES_TO_METERS;
    private static final double MAX_VOLTAGE = 12.0;
    private static final double KP = 30.0;
    private static final double KV = 10.0;
    // public final static Elevator TEST_ELEVATOR = new Elevator(Motor.CIM, 20.0, 2.0, 72.0 / 12.0 * 22.0 / 16.0, 0.25 * 0.0254 * 22.0 / Math.PI / 2.0, 0.50, 0.02);
    public final double accelerationVelocityConstant;
    public final double accelerationVoltageConstant;
    public double goal = 0.0;
    public double filteredGoal = 0.0;
    public double offset = 0.0;
    public double error = 0.0;
    public State state = State.UNINITIALIZED;
    private double previousError = 0.0;

    public ElevatorStateMachineDeprecated() {
        final double kT = MOTOR.motorTorqueConstant * NUM_MOTORS;
        accelerationVelocityConstant = (-kT * GEAR_RATIO * GEAR_RATIO) / (MOTOR.motorVoltageConstant * MOTOR.resistance * PULLEY_RADIUS * PULLEY_RADIUS * MASS);
        accelerationVoltageConstant = GEAR_RATIO * kT / (MOTOR.resistance * PULLEY_RADIUS * MASS);
    }

    public double update(double encoder, boolean limit_triggered, boolean enabled) {
        double position = encoder + offset;
        switch (state) {
            case UNINITIALIZED:
                if (enabled) {
                    state = State.ZEROING;
                    filteredGoal = position;
                }
                break;
            case ZEROING:
                filteredGoal -= ZEROING_VELOCITY * DT;
                if (limit_triggered) {
                    state = State.RUNNING;
                    offset = -encoder;
                    position = 0.0;
                }

                if (!enabled) {
                    state = State.UNINITIALIZED;
                }
                break;
            case RUNNING:
                filteredGoal = goal;
                break;
            case STOPPED:
                break;
        }


        error = filteredGoal - position;
        final double velocity = (error - previousError) / DT;
        previousError = error;
        double voltage = KP * error + KV * velocity;
        return Math.min(MAX_VOLTAGE, Math.max(-MAX_VOLTAGE, voltage));
    }

    public void setGoal(double goal) {
        if (goal > MAX_HEIGHT) {
            goal = MAX_HEIGHT;
        } else if (goal < MIN_HEIGHT) {
            goal = MIN_HEIGHT;
        }
        this.goal = goal;
    }

    public enum State {
        UNINITIALIZED,
        ZEROING,
        RUNNING,
        STOPPED
    }
}
