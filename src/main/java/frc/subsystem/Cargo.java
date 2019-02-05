package frc.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import frc.loops.Loop;
import frc.loops.LooperInterface;
import frc.states.CargoState;
import frc.states.CargoStateMachine;
import frc.utils.Constants;

import static frc.utils.Constants.CARGO_SHUFFLEBOARD;

// Creates variables for the cargo subsystem and initializes motors
public class Cargo extends Subsystem {
    private static final double MAXIMUM_VOLTAGE = 12.0;
    private static Cargo instance;

    // Hardware
    private final WPI_TalonSRX centerSide;
    private final WPI_TalonSRX rightRear;
    private final WPI_TalonSRX leftRear;
    private final DigitalInput cargoSensor = new DigitalInput(Constants.CARGO_SENSOR);
    private final WPI_TalonSRX intake;
    private final WPI_TalonSRX oudo;

    private CargoState currentState = new CargoState();
    private CargoStateMachine cargoStateMachine = new CargoStateMachine();
    private double intakeTiltPower = 0.0;

    private Cargo() {
        // TODO test voltage compensation
        centerSide = new WPI_TalonSRX(Constants.CARGO_CENTER);
        rightRear = new WPI_TalonSRX(Constants.CARGO_LEFT);
        leftRear = new WPI_TalonSRX(Constants.CARGO_RIGHT);
        intake = new WPI_TalonSRX(Constants.INTAKE);

        oudo = new WPI_TalonSRX(Constants.OUDO);
        oudo.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);
        oudo.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);
        intake.setInverted(true);
//        centerSide.configVoltageCompSaturation(MAXIMUM_VOLTAGE, Constants.CAN_TIMEOUT_MS);
//        rightRear.configVoltageCompSaturation(MAXIMUM_VOLTAGE, Constants.CAN_TIMEOUT_MS);
//        leftRear.configVoltageCompSaturation(MAXIMUM_VOLTAGE, Constants.CAN_TIMEOUT_MS);
//        intake.configVoltageCompSaturation(MAXIMUM_VOLTAGE, Constants.CAN_TIMEOUT_MS);
//
//        centerSide.enableVoltageCompensation(true);
//        rightRear.enableVoltageCompensation(true);
//        leftRear.enableVoltageCompensation(true);
//        intake.enableVoltageCompensation(true);
//
        centerSide.enableVoltageCompensation(false);
        rightRear.enableVoltageCompensation(false);
        leftRear.enableVoltageCompensation(false);
        intake.enableVoltageCompensation(false);

        rightRear.setInverted(true);
    }

    // Creates new cargo instance but not if there is an existing one, to avoid conflicts.
    public synchronized static Cargo getInstance() {
        if (instance == null) {
            instance = new Cargo();
        }
        return instance;
    }

    public synchronized void setDesiredState(CargoState.IntakeState intakeState) {
        cargoStateMachine.setDesiredState(intakeState);
    }

    // Outputs values to shuffleboard
    @Override
    public void outputTelemetry() {
        CARGO_SHUFFLEBOARD.putString("Cargo Intake State", currentState.intakeState.toString());
        CARGO_SHUFFLEBOARD.putNumber("Rear Output", currentState.rearMotorOutput);
        CARGO_SHUFFLEBOARD.putNumber("Left Output", currentState.rightMotorOutput);
        CARGO_SHUFFLEBOARD.putNumber("Right Output", currentState.leftMotorOutput);
        CARGO_SHUFFLEBOARD.putNumber("Intake Output", currentState.intakeOutput);
        CARGO_SHUFFLEBOARD.putBoolean("Cargo In Hold", currentState.ballInHold);
        CARGO_SHUFFLEBOARD.putNumber("Left Rear Current", leftRear.getOutputCurrent());
        CARGO_SHUFFLEBOARD.putNumber("Right Rear Current", rightRear.getOutputCurrent());
        CARGO_SHUFFLEBOARD.putNumber("Center Side Current", centerSide.getOutputCurrent());
        CARGO_SHUFFLEBOARD.putNumber("Intake (Wheels) Current", intake.getOutputCurrent());
        CARGO_SHUFFLEBOARD.putNumber("Intake (Tilt) Current", oudo.getOutputCurrent());
    }

    // Stops the wheels' motors
    @Override
    public synchronized void stop() {
        setDesiredState(CargoState.IntakeState.STOPPED);
    }

    // Sets the modes that the loop uses to function
    @Override
    public void registerEnabledLoops(LooperInterface enabledLooper) {
        Loop loop = new Loop() {
            @Override
            public void onStart(double timestamp) {
                setDesiredState(CargoState.IntakeState.STOPPED);
            }

            @Override
            public void onLoop(double timestamp) {
                synchronized (Cargo.this) {
                    CargoState newState = cargoStateMachine.onUpdate(getCargoState());
                    updateOutputFromState(newState);
                }
            }

            @Override
            public void onStop(double timestamp) {
                synchronized (Cargo.this) {
                    setDesiredState(CargoState.IntakeState.STOPPED);
                    stop();
                }
            }
        };

        enabledLooper.registerLoop(loop);
    }

    // Used with IR sensor to stop when cargo is in hold
    public synchronized boolean cargoInHold() {
        return currentState.ballInHold;
    }

    private synchronized CargoState getCargoState() {
        currentState.ballInHold = !cargoSensor.get();
        return currentState;
    }

    public void intakeTilt(double power) {
        intakeTiltPower = power;
    }

    private synchronized void updateOutputFromState(CargoState state) {
        centerSide.set(ControlMode.PercentOutput, state.rearMotorOutput);
        rightRear.set(ControlMode.PercentOutput, state.rightMotorOutput);
        leftRear.set(ControlMode.PercentOutput, state.leftMotorOutput);
        intake.set(ControlMode.PercentOutput, state.intakeOutput);
        oudo.set(ControlMode.PercentOutput, intakeTiltPower);
        currentState = state;
    }
}