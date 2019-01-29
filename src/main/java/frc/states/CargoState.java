package frc.states;

public class CargoState {
    // Inputs
    public IntakeState intakeState = IntakeState.STOPPED;
    public boolean ballInHold = false;
    // Outputs
    public double rearMotorOutput;
    public double rightMotorOutput;
    public double leftMotorOutput;
    public double intakeOutput;

    // What state to set the intake to
    public enum IntakeState {
        INTAKE(true),
        INTAKE_LEFT(true),
        INTAKE_RIGHT(true),
        OUTTAKE_FRONT(false),
        OUTTAKE_LEFT(false),
        OUTTAKE_RIGHT(false),
        STOPPED(false);

        private boolean stopOnSensor;

        IntakeState(boolean stopOnSensor){
            this.stopOnSensor = stopOnSensor;
        }

        public boolean getStopOnSensor(){
            return stopOnSensor;
        }
    }
}
