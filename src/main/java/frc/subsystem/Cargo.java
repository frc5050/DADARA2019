package frc.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import frc.loops.Loop;
import frc.loops.LooperInterface;
import frc.states.CargoState;
import frc.states.CargoStateMachine;
import frc.utils.Constants;

import static frc.utils.Constants.CARGO_SHUFFLEBOARD;

public class Cargo extends Subsystem {
    private static final double MAXIMUM_VOLTAGE = 12.0;
    private static Cargo instance;

    // Hardware
    private final WPI_TalonSRX centerSide;
    private final WPI_TalonSRX rightRear;
    private final WPI_TalonSRX leftRear;
    private final DigitalInput cargoSensor = new DigitalInput(Constants.CARGO_SENSOR);
    private final WPI_TalonSRX intake;

    private CargoState currentState = new CargoState();
    private CargoStateMachine cargoStateMachine = new CargoStateMachine();

    private Cargo() {
        // TODO test voltage compensation
        centerSide = new WPI_TalonSRX(Constants.CARGO_CENTER);
        rightRear = new WPI_TalonSRX(Constants.CARGO_LEFT);
        leftRear = new WPI_TalonSRX(Constants.CARGO_RIGHT);
        intake = new WPI_TalonSRX(Constants.INTAKE);

        centerSide.configVoltageCompSaturation(MAXIMUM_VOLTAGE, Constants.CAN_TIMEOUT_MS);
        rightRear.configVoltageCompSaturation(MAXIMUM_VOLTAGE, Constants.CAN_TIMEOUT_MS);
        leftRear.configVoltageCompSaturation(MAXIMUM_VOLTAGE, Constants.CAN_TIMEOUT_MS);
        intake.configVoltageCompSaturation(MAXIMUM_VOLTAGE, Constants.CAN_TIMEOUT_MS);

        centerSide.enableVoltageCompensation(true);
        rightRear.enableVoltageCompensation(true);
        leftRear.enableVoltageCompensation(true);
        intake.enableVoltageCompensation(true);

        rightRear.setInverted(true);
    }

    public synchronized static Cargo getInstance() {
        if (instance == null) {
            instance = new Cargo();
        }
        return instance;
    }

    public synchronized void setDesiredState(CargoState.IntakeState intakeState) {
        cargoStateMachine.setDesiredState(intakeState);
    }

    @Override
    public void outputTelemetry() {
        CARGO_SHUFFLEBOARD.putString("Cargo Intake State", currentState.intakeState.toString());
        CARGO_SHUFFLEBOARD.putNumber("Rear Output", currentState.rearMotorOutput);
        CARGO_SHUFFLEBOARD.putNumber("Left Output", currentState.rightMotorOutput);
        CARGO_SHUFFLEBOARD.putNumber("Right Output", currentState.leftMotorOutput);
        CARGO_SHUFFLEBOARD.putNumber("Intake Output", currentState.intakeOutput);
        CARGO_SHUFFLEBOARD.putBoolean("Cargo In Hold", currentState.ballInHold);
    }

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

    public synchronized boolean cargoInHold() {
        return currentState.ballInHold;
    }

    private synchronized CargoState getCargoState() {
        currentState.ballInHold = !cargoSensor.get();
        return currentState;
    }

    private synchronized void updateOutputFromState(CargoState state) {
        centerSide.set(ControlMode.PercentOutput, state.rearMotorOutput);
        rightRear.set(ControlMode.PercentOutput, state.rightMotorOutput);
        leftRear.set(ControlMode.PercentOutput, state.leftMotorOutput);
        intake.set(ControlMode.PercentOutput, state.intakeOutput);
        currentState = state;
    }
}