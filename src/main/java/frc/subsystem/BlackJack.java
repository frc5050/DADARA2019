package frc.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import frc.utils.Constants;

import static frc.utils.Constants.JACKS_SHUFFLEBOARD;

public class BlackJack extends Subsystem {
    private final WPI_TalonSRX rightRearJack;
    private static BlackJack instance;
    private double demand = 0.0;

    private BlackJack(){
        rightRearJack = new WPI_TalonSRX(Constants.RIGHT_REAR_JACK_LIFT);
        rightRearJack.setSelectedSensorPosition(0, 0, 30);
        rightRearJack.setInverted(false);
        rightRearJack.setSensorPhase(true);

        rightRearJack.config_kP(0, 1, 30);
        rightRearJack.config_kI(0, 0, 30);
        rightRearJack.config_kD(0, 0, 30);
        rightRearJack.config_kF(0, 0, 30);
        rightRearJack.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, 10);
        rightRearJack.setStatusFramePeriod(StatusFrame.Status_10_MotionMagic, 10);
        rightRearJack.configPeakOutputForward(1.0);
        rightRearJack.configPeakOutputReverse(-1.0);
        rightRearJack.configMotionCruiseVelocity(2000);
        rightRearJack.configMotionAcceleration(200);
    }

    public static BlackJack getInstance(){
        if(instance == null){
            instance = new BlackJack();
        }
        return instance;
    }

    public void setOpenLoop(double power){
        demand = power;
        rightRearJack.set(ControlMode.MotionMagic, -8000);
    }

    @Override
    public void outputTelemetry() {
        JACKS_SHUFFLEBOARD.putNumber("Demand", demand);
        JACKS_SHUFFLEBOARD.putNumber("Encoder", rightRearJack.getSelectedSensorPosition(0));
        JACKS_SHUFFLEBOARD.putNumber("Current (Amps)", rightRearJack.getOutputCurrent());
    }

    @Override
    public void stop() {

    }
}
