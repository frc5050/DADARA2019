package frc.subsystem;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import frc.loops.Loop;
import frc.loops.Looper;
import frc.loops.LooperInterface;
import frc.states.HatchState;
import frc.states.HatchStateMachine;

import static frc.states.HatchStateMachine.PEAK_FORWARD_OUTPUT_STANDARD;
import static frc.states.HatchStateMachine.PEAK_REVERSE_OUTPUT_STANDARD;
import static frc.utils.Constants.*;

public class Hatch2 extends Subsystem {
    private static final int SETTINGS_TIMEOUT = 30;
    private static final int PERIOD_MS = (int) (Looper.PERIOD * 1000);
    private static final double EPSILON = 1E-5;
    private static Hatch2 instance;
    private final WPI_TalonSRX hatch;
    private final DigitalInput upperLimitSwitch;
    private HatchStateMachine hatchStateMachine = new HatchStateMachine(Timer.getFPGATimestamp());
    private HatchState hatchState = new HatchState();
    private HatchState outputState = new HatchState();

    private Hatch2() {
        upperLimitSwitch = new DigitalInput(1);
        hatch = new WPI_TalonSRX(HATCH);

        hatch.configFactoryDefault();

        hatch.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, SETTINGS_TIMEOUT);
        hatch.setInverted(false);
        hatch.setSensorPhase(false);

        hatch.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, PERIOD_MS, SETTINGS_TIMEOUT);
        hatch.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, PERIOD_MS, SETTINGS_TIMEOUT);

        hatch.configNominalOutputReverse(0.0, SETTINGS_TIMEOUT);
        hatch.configNominalOutputForward(0.0, SETTINGS_TIMEOUT);
        hatch.configPeakOutputForward(PEAK_FORWARD_OUTPUT_STANDARD);
        hatch.configPeakOutputReverse(PEAK_REVERSE_OUTPUT_STANDARD);

        hatch.config_kF(0, 0.0); //todo
        hatch.config_kP(0, 2.0); //todo
        hatch.config_kI(0, 0.0); //todo
        hatch.config_kD(0, 0.0); //todo

        hatch.configMotionCruiseVelocity(6000, SETTINGS_TIMEOUT);
        hatch.configMotionAcceleration(6000, SETTINGS_TIMEOUT);

        resetSensorPosition();
    }

    public static Hatch2 getInstance() {
        if (instance == null) {
            instance = new Hatch2();
        }
        return instance;
    }

    @Override
    public void registerEnabledLoops(LooperInterface enabledLooper) {
        Loop loop = new Loop() {
            @Override
            public void onStart(double timestamp) {
                stop();
            }

            @Override
            public void onLoop(double timestamp) {
                synchronized (Hatch2.this) {
                    outputState = hatchStateMachine.update(hatchState, timestamp);
                }
            }

            @Override
            public void onStop(double timestamp) {
                stop();
            }
        };

        enabledLooper.registerLoop(loop);
    }

    public synchronized void resetSensorPosition() {
        hatch.setSelectedSensorPosition(0, 0, SETTINGS_TIMEOUT);
    }

    public synchronized void setOpenLoop(double power) {
        hatchStateMachine.setOpenLoop(power);
    }

    public synchronized void setPosition(double power) {
        hatchStateMachine.setPosition(power);
    }

    @Override
    public synchronized void readPeriodicInputs() {
        hatchState.limitHit = upperLimitSwitch.get();
        hatchState.encoder = hatch.getSelectedSensorPosition(0);
        hatchState.outputCurrent = hatch.getOutputCurrent();
    }

    @Override
    public synchronized void writePeriodicOutputs() {
        if (outputState.resetSensor) {
            resetSensorPosition();
        }
        if (Math.abs(outputState.peakOutputReverse - hatchState.peakOutputReverse) > EPSILON) {
            hatchState.peakOutputReverse = outputState.peakOutputReverse;
            hatch.configPeakOutputReverse(outputState.peakOutputReverse, SETTINGS_TIMEOUT);
        }
        if (Math.abs(outputState.peakOutputForward - hatchState.peakOutputForward) > EPSILON) {
            hatchState.peakOutputForward = outputState.peakOutputForward;
            hatch.configPeakOutputForward(outputState.peakOutputForward, SETTINGS_TIMEOUT);
        }
        hatch.set(outputState.controlMode, outputState.demand);
    }

    @Override
    public void outputTelemetry() {
        HATCH_SHUFFLEBOARD.putBoolean("Limit Hit", hatchState.limitHit);
        HATCH_SHUFFLEBOARD.putBoolean("Hardware Fault", hatchStateMachine.hasHadHardwareFault());
        HATCH_SHUFFLEBOARD.putNumber("Amperage", hatch.getOutputCurrent());
        HATCH_SHUFFLEBOARD.putNumber("Demand", hatchState.demand);
        HATCH_SHUFFLEBOARD.putNumber("Peak Output Forward", hatchState.peakOutputForward);
        HATCH_SHUFFLEBOARD.putNumber("Peak Output Reverse", hatchState.peakOutputReverse);
        HATCH_SHUFFLEBOARD.putString("Control Mode", hatchState.controlMode.toString());
        HATCH_SHUFFLEBOARD.putNumber("Encoder", hatchState.encoder);

    }

    @Override
    public synchronized void stop() {
        setOpenLoop(0.0);
    }
}
