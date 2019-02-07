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
    private final WPI_TalonSRX intakeTilt;


    private CargoState currentState = new CargoState();
    private CargoStateMachine cargoStateMachine = new CargoStateMachine();
    private double intakeTiltPower = 0.0;

    /**
     * Constructor.
     */
    private Cargo() {
        centerSide = new WPI_TalonSRX(Constants.CARGO_CENTER);
        rightRear = new WPI_TalonSRX(Constants.CARGO_LEFT);
        leftRear = new WPI_TalonSRX(Constants.CARGO_RIGHT);
        intake = new WPI_TalonSRX(Constants.INTAKE);

        intakeTilt = new WPI_TalonSRX(Constants.INTAKE_TILT);
        intakeTilt.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);
        intakeTilt.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);
        intake.setInverted(true);
        // TODO test voltage compensation
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

    /**
     * Returns a static instance of the {@link Cargo} subsystem. If none has been created yet, the instance is created.
     * This enables multiple other subsystems and any other classes to use this class without having to pass an instance
     * or take the risk of trying to instantiate multiple instances of this class, which would result in errors.
     *
     * @return a static instance of the {@link Cargo} subsystem.
     */
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
    }

    /**
     * Stops the cargo system, sets all motors in this subsystem to zero output.
     */
    @Override
    public synchronized void stop() {
        setDesiredState(CargoState.IntakeState.STOPPED);
    }

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
                    currentState = cargoStateMachine.onUpdate(currentState);
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

    /**
     * Returns true when there is a cargo in the holding area.
     *
     * @return true if there is a cargo in the holding area, false if there is not.
     */
    public synchronized boolean cargoInHold() {
        return currentState.ballInHold;
    }

    @Override
    public synchronized void readPeriodicInputs() {
        currentState.ballInHold = !cargoSensor.get();
    }

    @Override
    public synchronized void writePeriodicOutputs() {
        centerSide.set(ControlMode.PercentOutput, currentState.rearMotorOutput);
        rightRear.set(ControlMode.PercentOutput, currentState.rightMotorOutput);
        leftRear.set(ControlMode.PercentOutput, currentState.leftMotorOutput);
        intake.set(ControlMode.PercentOutput, currentState.intakeOutput);
        intakeTilt.set(ControlMode.PercentOutput, intakeTiltPower);
    }

    public void intakeTilt(double power) {
        intakeTiltPower = power;
    }
}