package frc.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import frc.utils.Constants;

import static frc.utils.Constants.JACKS_SHUFFLEBOARD;

public class BlackJack extends Subsystem {
    // Motion magic parameters when lifting
    private static final int REAR_MOTION_MAGIC_VELOCITY_LIFT = 2000;
    private static final int REAR_MOTION_MAGIC_ACCELERATION_LIFT = 525;
    private static final int FRONT_MOTION_MAGIC_VELOCITY_LIFT = 3500;
    private static final int FRONT_MOTION_MAGIC_ACCELERATION_LIFT = 600;
    // Motion magic parameters when retracting
    private static final int REAR_MOTION_MAGIC_VELOCITY_RETRACT = 2000;
    private static final int REAR_MOTION_MAGIC_ACCELERATION_RETRACT = 600;
    private static final int FRONT_MOTION_MAGIC_VELOCITY_RETRACT = 3500;
    private static final int FRONT_MOTION_MAGIC_ACCELERATION_RETRACT = 600;
    private static BlackJack instance;
    private final WPI_TalonSRX rightRearJack;
    private final WPI_TalonSRX leftRearJack;
    private final WPI_TalonSRX frontJack;
    private double demand = 0.0;
    private JackState state = JackState.OPEN_LOOP;

    private BlackJack() {
        rightRearJack = new WPI_TalonSRX(Constants.RIGHT_REAR_JACK_LIFT);
        leftRearJack = new WPI_TalonSRX(Constants.LEFT_REAR_JACK_LIFT);
        frontJack = new WPI_TalonSRX(Constants.FRONT_JACK_LIFT);
        configureTalon(rightRearJack, true, false, 1.0);
        configureTalon(leftRearJack, false, false, 1.0);
        configureTalon(frontJack, true, false, 1.7);
    }

    public static BlackJack getInstance() {
        if (instance == null) {
            instance = new BlackJack();
        }
        return instance;
    }

    private void configureTalon(WPI_TalonSRX talon, boolean inverted, boolean sensorPhase, double kp) {
        talon.setSelectedSensorPosition(0, 0, 30);
        talon.setInverted(inverted);
        talon.setSensorPhase(sensorPhase);
        talon.config_kP(0, kp, 30);
        talon.config_kI(0, 0, 30);
        talon.config_kD(0, 0, 30);
        talon.config_kF(0, 0, 30);
        talon.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, 10);
        talon.setStatusFramePeriod(StatusFrame.Status_10_MotionMagic, 10);
        talon.configPeakOutputForward(1.0);
        talon.configPeakOutputReverse(-1.0);
    }

    private void setMotionMagicSpeedParameters(WPI_TalonSRX talon, int velocity, int acceleration) {
        talon.configMotionAcceleration(acceleration);
        talon.configMotionCruiseVelocity(velocity);
    }

    private void reconfigureTalonsLiftMode() {
        setMotionMagicSpeedParameters(frontJack, FRONT_MOTION_MAGIC_VELOCITY_LIFT, FRONT_MOTION_MAGIC_ACCELERATION_LIFT);
        setMotionMagicSpeedParameters(leftRearJack, REAR_MOTION_MAGIC_VELOCITY_LIFT, REAR_MOTION_MAGIC_ACCELERATION_LIFT);
        setMotionMagicSpeedParameters(rightRearJack, REAR_MOTION_MAGIC_VELOCITY_LIFT, REAR_MOTION_MAGIC_ACCELERATION_LIFT);
    }

    private void reconfigureTalonsRetractMode() {
        setMotionMagicSpeedParameters(frontJack, FRONT_MOTION_MAGIC_VELOCITY_RETRACT, FRONT_MOTION_MAGIC_ACCELERATION_RETRACT);
        setMotionMagicSpeedParameters(leftRearJack, REAR_MOTION_MAGIC_VELOCITY_RETRACT, REAR_MOTION_MAGIC_ACCELERATION_RETRACT);
        setMotionMagicSpeedParameters(rightRearJack, REAR_MOTION_MAGIC_VELOCITY_RETRACT, REAR_MOTION_MAGIC_ACCELERATION_RETRACT);
    }

    public void lift() {
        if (state != JackState.LIFT_ALL) {
            if (state.useMotionMagic()) {
                reconfigureTalonsLiftMode();
            }
            state = JackState.LIFT_ALL;
        }
        demand = 19250;
    }

    public void retract() {
        if (state != JackState.RETRACT_ALL) {
            if (state.useMotionMagic()) {
                reconfigureTalonsRetractMode();
            }
            state = JackState.RETRACT_ALL;
        }
        demand = 0;
    }

    public void setOpenLoop(double power) {
        state = JackState.OPEN_LOOP;
        demand = power;
    }

    @Override
    public void writePeriodicOutputs() {
        if (!state.useMotionMagic()) {
            leftRearJack.set(ControlMode.PercentOutput, demand);
            rightRearJack.set(ControlMode.PercentOutput, demand);
            frontJack.set(ControlMode.PercentOutput, demand);
        } else {
            leftRearJack.set(ControlMode.MotionMagic, demand);
            rightRearJack.set(ControlMode.MotionMagic, demand);
            frontJack.set(ControlMode.MotionMagic, demand);
        }
    }

    @Override
    public void outputTelemetry() {
        JACKS_SHUFFLEBOARD.putNumber("Demand", demand);
        JACKS_SHUFFLEBOARD.putNumber("Encoder RRJ", rightRearJack.getSelectedSensorPosition(0));
        JACKS_SHUFFLEBOARD.putNumber("Encoder LRF", leftRearJack.getSelectedSensorPosition(0));
        JACKS_SHUFFLEBOARD.putNumber("Encoder FJ", frontJack.getSelectedSensorPosition(0));
        JACKS_SHUFFLEBOARD.putNumber("Current (Amps)", rightRearJack.getOutputCurrent());
    }

    @Override
    public void stop() {

    }

    private enum JackState {
        LIFT_ALL(true),
        RETRACT_ALL(true),
        OPEN_LOOP(false);

        private boolean useMotionMagic;

        JackState(boolean useMotionMagic) {
            this.useMotionMagic = useMotionMagic;
        }

        public boolean useMotionMagic() {
            return useMotionMagic;
        }
    }
}
