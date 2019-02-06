package frc.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import frc.utils.Constants;
import frc.utils.DriveSignal;

import java.rmi.MarshalledObject;

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
    private static final int LIFT_TOLERANCE = 300;
    private static BlackJack instance;
    private final WPI_TalonSRX rightRearJack;
    private final WPI_TalonSRX leftRearJack;
    private final WPI_TalonSRX frontJack;
    private final WPI_TalonSRX leftRearWheel;
    private final WPI_TalonSRX rightRearWheel;
    private final DigitalInput forwardIrSensor;
    private double demand = 0.0;
    private JackState state = JackState.OPEN_LOOP;
    private HabLevel3ClimbStates habLevel3ClimbStates = HabLevel3ClimbStates.INIT;
    private int frontEncoder = 0;
    private int leftEncoder = 0;
    private int rightEncoder = 0;

    private BlackJack() {
        forwardIrSensor = new DigitalInput(3);
        rightRearJack = new WPI_TalonSRX(Constants.RIGHT_REAR_JACK_LIFT);
        leftRearJack = new WPI_TalonSRX(Constants.LEFT_REAR_JACK_LIFT);
        frontJack = new WPI_TalonSRX(Constants.FRONT_JACK_LIFT);
        leftRearWheel = new WPI_TalonSRX(Constants.LEFT_REAR_JACK_WHEEL);
        rightRearWheel = new WPI_TalonSRX(Constants.RIGHT_REAR_JACK_WHEEL);
        configureTalon(rightRearJack, true, false, 1.0);
        configureTalon(leftRearJack, false, false, 1.0);
        configureTalon(frontJack, true, false, 1.7);

        leftRearWheel.setInverted(true);
    }

    // TODO figure out when to reset the habclimbstate

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

    public void retractFrontJack(){
        if(state != JackState.RETRACT_FRONT_JACK){
            state = JackState.RETRACT_FRONT_JACK;
        }
        lift();
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

    private static final DriveSignal driveBaseRun = new DriveSignal(0.2, 0.2);
    private static final DriveSignal jackWheelsRun = new DriveSignal(0.8, 0.8);
    private Drive drive = Drive.getInstance();

    public void hab3Climb() {
        switch (habLevel3ClimbStates) {
            case INIT:
                habLevel3ClimbStates = HabLevel3ClimbStates.LIFT;
                break;
            case LIFT:
                if (state == JackState.LIFT_ALL) {
                    if (Math.abs(frontEncoder - demand) < LIFT_TOLERANCE &&
                            Math.abs(leftEncoder - demand) < LIFT_TOLERANCE &&
                            Math.abs(rightEncoder - demand) < LIFT_TOLERANCE) {
                        JACKS_SHUFFLEBOARD.putBoolean("ACHIEVEMENT UNLOCKED: MADE IT TO THE TOP", true);
                        drive.setOpenLoop(driveBaseRun);
                        setWheels(jackWheelsRun);
                        if(frontIrAboveGround){
                            habLevel3ClimbStates = HabLevel3ClimbStates.RETRACT_FRONT_JACK;
                        }
                    } else {
                        JACKS_SHUFFLEBOARD.putBoolean("ACHIEVEMENT UNLOCKED: MADE IT TO THE TOP", false);
                    }
                }
                lift();
                break;
            case RETRACT_FRONT_JACK:
                retractFrontJack();
                setWheels(jackWheelsRun);
                if(Math.abs(frontEncoder - 0) < LIFT_TOLERANCE / 1.3){
                    habLevel3ClimbStates = HabLevel3ClimbStates.MOVE_FORWARD_ONCE_ON_HAB;
                }
                break;
            case MOVE_FORWARD_ONCE_ON_HAB:
                break;
        }
    }

    private boolean frontIrAboveGround = false;

    @Override
    public void readPeriodicInputs() {
        frontIrAboveGround = !forwardIrSensor.get();
        frontEncoder = frontJack.getSelectedSensorPosition(0);
        leftEncoder = leftRearJack.getSelectedSensorPosition(0);
        rightEncoder = rightRearJack.getSelectedSensorPosition(0);
    }

    public void setOpenLoop(double power) {
        state = JackState.OPEN_LOOP;
        demand = power;
    }

    public void setWheels(DriveSignal driveSignal) {
        leftRearWheel.set(ControlMode.PercentOutput, driveSignal.getLeftOutput());
        rightRearWheel.set(ControlMode.PercentOutput, driveSignal.getRightOutput());
    }

    @Override
    public void writePeriodicOutputs() {
        if (!state.useMotionMagic()) {
                leftRearJack.set(ControlMode.PercentOutput, demand);
                rightRearJack.set(ControlMode.PercentOutput, demand);
                frontJack.set(ControlMode.PercentOutput, demand);
        } else {
            if(state == JackState.RETRACT_FRONT_JACK){
                frontJack.set(ControlMode.MotionMagic, 0.0);
                leftRearJack.set(ControlMode.PercentOutput, demand);
                rightRearJack.set(ControlMode.PercentOutput, demand);
            } else {
                leftRearJack.set(ControlMode.MotionMagic, demand);
                rightRearJack.set(ControlMode.MotionMagic, demand);
                frontJack.set(ControlMode.MotionMagic, demand);
            }
        }
    }

    @Override
    public void outputTelemetry() {
        JACKS_SHUFFLEBOARD.putString("Hab3 State", habLevel3ClimbStates.toString());
        JACKS_SHUFFLEBOARD.putNumber("Demand", demand);
        JACKS_SHUFFLEBOARD.putBoolean("Front Ir Sensor", frontIrAboveGround);
        JACKS_SHUFFLEBOARD.putNumber("Encoder RRJ", rightRearJack.getSelectedSensorPosition(0));
        JACKS_SHUFFLEBOARD.putNumber("Encoder LRF", leftRearJack.getSelectedSensorPosition(0));
        JACKS_SHUFFLEBOARD.putNumber("Encoder FJ", frontJack.getSelectedSensorPosition(0));
        JACKS_SHUFFLEBOARD.putNumber("Current (Amps)", rightRearJack.getOutputCurrent());
    }

    @Override
    public void stop() {

    }

    private enum HabLevel3ClimbStates {
        INIT,
        LIFT,
        RETRACT_FRONT_JACK,
        MOVE_FORWARD_ONCE_ON_HAB
    }

    private enum JackState {
        LIFT_ALL(true),
        RETRACT_ALL(true),
        OPEN_LOOP(false),
        RETRACT_FRONT_JACK(true);

        private boolean useMotionMagic;

        JackState(boolean useMotionMagic) {
            this.useMotionMagic = useMotionMagic;
        }

        public boolean useMotionMagic() {
            return useMotionMagic;
        }
    }
}
