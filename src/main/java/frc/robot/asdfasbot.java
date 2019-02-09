package frc.robot;

import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.*;
import frc.utils.Constants;

public class asdfasbot extends TimedRobot {
    private CANSparkMax motor = new CANSparkMax(Constants.ELEVATOR_NEO, CANSparkMaxLowLevel.MotorType.kBrushless);
    private CANPIDController controller = new CANPIDController(motor);
    private DigitalInput bottomLimit = new DigitalInput(2);
    private boolean bottomLimitPreviouslyHit = false;
    private XboxController xboxController = new XboxController(1);
    @Override
    public void teleopPeriodic(){
        boolean limitHit = bottomLimit.get();
        if(bottomLimitPreviouslyHit != limitHit){
            if(limitHit){
                controller.setOutputRange(-1.0, 0.0);
            } else {
                controller.setOutputRange(-1.0, 1.0);
            }
        }
        motor.set(xboxController.getY(GenericHID.Hand.kRight));
    }
}
