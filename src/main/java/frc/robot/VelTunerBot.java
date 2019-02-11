/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.ControlType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.utils.Constants;

/**
 * This is a demo program showing the use of the RobotDrive class, specifically
 * it contains the code necessary to operate a robot with tank drive.
 */
public class VelTunerBot extends TimedRobot {
    private static final int deviceID = 1;
    public double kP, kI, kD, kIz, kFF, maxRPM;
    private CANSparkMax m_motor;
    private CANPIDController m_pidController;
    private CANEncoder m_encoder;
    private XboxController joystick = new XboxController(1);
    private DigitalInput bottomLimit = new DigitalInput(2);
    private boolean bottomLimitPreviouslyHit = false;

    @Override
    public void robotInit() {
        m_motor = new CANSparkMax(Constants.ELEVATOR_NEO, MotorType.kBrushless);
        m_pidController = m_motor.getPIDController();

        // Encoder object created to display position values
        m_encoder = m_motor.getEncoder();

        // PID coefficients
        kP = 5e-5;
        kI = 1e-6;
        kD = 0;
        kIz = 0;
        kFF = 0;
        maxRPM = 2700;

        // set PID coefficients
        m_pidController.setP(kP);
        m_pidController.setI(kI);
        m_pidController.setD(kD);
        m_pidController.setIZone(kIz);
        m_pidController.setFF(kFF);

        // display PID coefficients on SmartDashboard
        SmartDashboard.putNumber("P Gain", kP);
        SmartDashboard.putNumber("I Gain", kI);
        SmartDashboard.putNumber("D Gain", kD);
        SmartDashboard.putNumber("I Zone", kIz);
        SmartDashboard.putNumber("Feed Forward", kFF);
//        SmartDashboard.putNumber("Max Output", kMaxOutput);
//        SmartDashboard.putNumber("Min Output", kMinOutput);
    }

    @Override
    public void teleopPeriodic() {
        // read PID coefficients from SmartDashboard
        double p = SmartDashboard.getNumber("P Gain", 0);
        double i = SmartDashboard.getNumber("I Gain", 0);
        double d = SmartDashboard.getNumber("D Gain", 0);
        double iz = SmartDashboard.getNumber("I Zone", 0);
        double ff = SmartDashboard.getNumber("Feed Forward", 0);
//        double max = SmartDashboard.getNumber("Max Output", 0);
//        double min = SmartDashboard.getNumber("Min Output", 0);

        // if PID coefficients on SmartDashboard have changed, write new values to controller
        if ((p != kP)) {
            m_pidController.setP(p);
            kP = p;
        }
        if ((i != kI)) {
            m_pidController.setI(i);
            kI = i;
        }
        if ((d != kD)) {
            m_pidController.setD(d);
            kD = d;
        }
        if ((iz != kIz)) {
            m_pidController.setIZone(iz);
            kIz = iz;
        }
        if ((ff != kFF)) {
            m_pidController.setFF(ff);
            kFF = ff;
        }
//        if ((max != kMaxOutput) || (min != kMinOutput)) {
//            m_pidController.setOutputRange(min, max);
//            kMinOutput = min;
//            kMaxOutput = max;
//        }

        boolean limitHit = bottomLimit.get();
//        final double maxOutNew;
//        final double minOutNew;

        if (limitHit) {
            m_pidController.setOutputRange(-1.0, 1.0);
//            maxOutNew = 0.0;
//            minOutNew = -1.0;
        } else {
            m_pidController.setOutputRange(-1.0, 0.0);
//            maxOutNew = 1.0;
//            minOutNew = -1.0;
        }

//        if(minOutNew != minOut || maxOutNew != maxOut) {
//            m_pidController.setOutputRange(minOutNew, maxOutNew);
//            minOut = minOutNew;
//            maxOut = maxOutNew;
//        }

        bottomLimitPreviouslyHit = limitHit;


//        final double setPoint;
//        if(joystick.getBumper(GenericHID.Hand.kRight)){
//            setPoint = maxRPM / 2.0;
//        } else if(joystick.getBumper(GenericHID.Hand.kLeft)){
//            setPoint = -maxRPM / 2.0;
//        } else {
//            setPoint = 0.0;
//        }
        double setPoint = -joystick.getY(GenericHID.Hand.kRight);
//        m_pidController.setReference(setPoint, ControlType.kVelocity);
        m_motor.set(setPoint);
        SmartDashboard.putBoolean("Limit Hit", limitHit);
//        SmartDashboard.putNumber("Max", maxOut);
//        SmartDashboard.putNumber("Min", minOut);
        SmartDashboard.putNumber("SetPoint", setPoint);
        SmartDashboard.putNumber("ProcessVariable", m_encoder.getVelocity());
    }
//    private double minOut = 0;
//    private double maxOut = 0;
}