package frc.subsystem;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import frc.utils.Constants;

public class TmpElev extends Subsystem {
    private static TmpElev instance;
    private final CANSparkMax left;
    private final CANSparkMax right;

    private TmpElev(){
        left = new CANSparkMax(Constants.LEFT_LIFT_NEO, CANSparkMaxLowLevel.MotorType.kBrushed);
        right = new CANSparkMax(Constants.RIGHT_LIFT_NEO, CANSparkMaxLowLevel.MotorType.kBrushed);
        right.follow(left);
        left.setInverted(true);
        right.setInverted(true);
    }

    public static TmpElev getInstance(){
        if(instance == null){
            instance = new TmpElev();
        }
        return instance;
    }

    public void setOpenLoop(double power){
        left.set(power);
    }

    @Override
    public void outputTelemetry() {

    }

    @Override
    public void stop() {

    }
}
