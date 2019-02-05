package frc.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import frc.utils.Constants;

import static frc.utils.Constants.JACKS_SHUFFLEBOARD;

public class BlackJack extends Subsystem {
    private final WPI_TalonSRX rightRearJack;
    private final WPI_TalonSRX leftRearJack;
    private final WPI_TalonSRX frontJack;
    private static BlackJack instance;
    private double demand = 0.0;

    private BlackJack(){
        rightRearJack = new WPI_TalonSRX(Constants.RIGHT_REAR_JACK_LIFT);
        leftRearJack = new WPI_TalonSRX(Constants.LEFT_REAR_JACK_LIFT);
        frontJack = new WPI_TalonSRX(Constants.FRONT_JACK_LIFT);
        configureTalon(rightRearJack, true, false, 1.0, 2000, 525);
        configureTalon(leftRearJack, false, false, 1.0, 2000, 525);
        configureTalon(frontJack, true, false, 1.7, 3500, 600);
    }
    
    private void configureTalon(WPI_TalonSRX talon, boolean inverted, boolean sensorPhase, double kp, int maxVelocity, int maxAccel){
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
        talon.configMotionCruiseVelocity(maxVelocity);
        talon.configMotionAcceleration(maxAccel);
    }

    public static BlackJack getInstance(){
        if(instance == null){
            instance = new BlackJack();
        }
        return instance;
    }

    boolean useMotionMagic = false;

    public void lift(){
        useMotionMagic = true;
        demand = 19250;
    }

    public void retract(){
        useMotionMagic = true;
        demand = 0;
    }

    public void setOpenLoop(double power){
        demand = power;
        useMotionMagic = false;
//        rightRearJack.set(ControlMode.MotionMagic, -8000);
    }

    @Override
    public void writePeriodicOutputs(){
        if(!useMotionMagic) {
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
}
