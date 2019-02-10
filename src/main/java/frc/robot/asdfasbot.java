package frc.robot;

import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.ControlType;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.inputs.GameController;
import frc.utils.Constants;

public class asdfasbot extends TimedRobot {
    private CANSparkMax motor;
    private CANPIDController controller;
    private DigitalInput bottomLimit;
    private boolean bottomLimitPreviouslyHit = false;
    private GameController xboxController = GameController.getInstance();

    @Override
    public void robotInit(){
        motor = new CANSparkMax(Constants.ELEVATOR_NEO, CANSparkMaxLowLevel.MotorType.kBrushless);
        controller = motor.getPIDController();
        bottomLimit = new DigitalInput(2);
//        xboxController = new XboxController(1);
    }

    @Override
    public void teleopPeriodic(){
        boolean limitHit = bottomLimit.get();
        if(bottomLimitPreviouslyHit != limitHit){
            if(limitHit){
                System.out.println("Changing to limited");
                controller.setOutputRange(-0.02, 0.02);
            } else {
                System.out.println("Changing to unlimited");
                controller.setOutputRange(-1.0, 1.0);
            }
        }
        bottomLimitPreviouslyHit = limitHit;
        double value = xboxController.elevateManual();
        SmartDashboard.putNumber("Output Value", value);
        SmartDashboard.putNumber("Output Min", controller.getOutputMin());
        SmartDashboard.putNumber("Output Actual", motor.getAppliedOutput());
        SmartDashboard.putNumber("Output Max", controller.getOutputMax());
        controller.setReference(value, ControlType.kDutyCycle);
    }
}
