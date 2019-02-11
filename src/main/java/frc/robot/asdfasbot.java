package frc.robot;

import com.revrobotics.*;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.inputs.GameController;
import frc.subsystem.Elevator;
import frc.utils.Constants;

public class asdfasbot extends TimedRobot {
    private CANSparkMax motor;
    private CANPIDController controller;
    private DigitalInput bottomLimit;
    private boolean bottomLimitPreviouslyHit = false;
    private GameController xboxController = GameController.getInstance();
    private CANEncoder encoder;
    private double lastVelocity = 0.0;
    private double lastTimestamp = Timer.getFPGATimestamp();
    private double offset = 0;

    @Override
    public void robotInit() {
        motor = new CANSparkMax(Constants.ELEVATOR_NEO, CANSparkMaxLowLevel.MotorType.kBrushless);
        controller = motor.getPIDController();
        encoder = motor.getEncoder();
        controller.setFF(0.0);
        controller.setD(0.0);
        controller.setP(3E-5);
        controller.setI(1E-6);
        controller.setIZone(0.0);
        controller.setOutputRange(-1.0, 1.0);
        bottomLimit = new DigitalInput(2);
    }

    @Override
    public void disabledInit(){
        xboxController.disabled();
    }

    @Override
    public void disabledPeriodic(){
        xboxController.disabledPeriodic();
    }

    @Override
    public void teleopPeriodic() {
        boolean limitHit = bottomLimit.get();
        double value;
        double currentEncoderValue = encoder.getPosition() + offset;
        final double desiredEncoder = -92.0;
        final double maxRpm = 1500;
        if(xboxController.useHatchOpenLoop()){
            System.out.println("Cint");
            double encError = desiredEncoder - currentEncoderValue;
            double errorMeters = -(encError / Elevator.TOTAL_DELTA_ENCODER_VALUE) * Elevator.TOTAL_DELTA_HEIGHT;
            value = 5 * errorMeters * maxRpm;
            value = Math.abs(value) > maxRpm ? Math.copySign(maxRpm, value) : value;
            SmartDashboard.putNumber("Error (m)", errorMeters);
            SmartDashboard.putNumber("Val", value);
        } else {
             value = xboxController.elevateManual() * maxRpm;
        }
        if (limitHit) {
            value = value > 0.0 ? 0.0 : value;
        }
        if (bottomLimitPreviouslyHit != limitHit) {
            if (limitHit) {
                offset = currentEncoderValue;
                System.out.println("Changing to limited");
                System.out.println(controller.setOutputRange(-1.0, 1.0));
            } else {
                System.out.println("Changing to unlimited");
                System.out.println(controller.setOutputRange(-1.0, 1.0).toString());
            }
        }
        bottomLimitPreviouslyHit = limitHit;
        controller.setReference(value, ControlType.kVelocity);
        SmartDashboard.putNumber("Output Value", value);
        SmartDashboard.putNumber("Output Min", controller.getOutputMin());
        SmartDashboard.putNumber("Output Actual", motor.getAppliedOutput());
        SmartDashboard.putNumber("Output Max", controller.getOutputMax());
        SmartDashboard.putNumber("Elevator/Encoder", encoder.getPosition());
    }
}
