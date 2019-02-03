package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import frc.inputs.GameController;
import frc.loops.Looper;
import frc.subsystem.SubsystemManager;

import java.util.Arrays;

public class ElevatorTestBot39 extends TimedRobot {
    private Looper enabledLooper = new Looper();
    private Looper disabledLooper = new Looper();

    private SubsystemManager subsystemManager = new SubsystemManager(Arrays.asList(Elevator2.getInstance()));
    private Elevator2 elevator = Elevator2.getInstance();
    private GameController gameController = GameController.getInstance();

    @Override
    public void robotInit() {
        subsystemManager.registerEnabledLoops(enabledLooper);
        subsystemManager.registerDisabledLoop(disabledLooper);
    }

    @Override
    public void robotPeriodic() {

    }

    @Override
    public void disabledInit() {
        enabledLooper.stop();
        disabledLooper.start();
    }

    @Override
    public void disabledPeriodic() {

    }


    @Override
    public void autonomousInit() {
    }

    @Override
    public void autonomousPeriodic() {

    }

    @Override
    public void teleopInit() {
        disabledLooper.stop();
        enabledLooper.start();
    }

    @Override
    public void teleopPeriodic() {
        gameController.update();
        if (gameController.setElevatorPositionLowHatch()) {
            elevator.pidToPosition(Elevator2.ElevatorPosition.HATCH_LOW);
        } else if (gameController.setElevatorPositionMidHatch()) {
            elevator.pidToPosition(Elevator2.ElevatorPosition.HATCH_MID);
        } else if (gameController.setElevatorPositionHighHatch()) {
            elevator.pidToPosition(Elevator2.ElevatorPosition.HATCH_HIGH);
        } else if (gameController.setElevatorPositionLowCargo()) {
            elevator.pidToPosition(Elevator2.ElevatorPosition.CARGO_LOW);
        } else if (gameController.setElevatorPositionMidCargo()) {
            elevator.pidToPosition(Elevator2.ElevatorPosition.CARGO_MID);
        } else if (gameController.setElevatorPositionHighCargo()) {
            elevator.pidToPosition(Elevator2.ElevatorPosition.CARGO_HIGH);
        } else {
            elevator.manualMovement(gameController.elevateManual());
        }
        subsystemManager.outputTelemetry();
    }

    @Override
    public void testInit() {
    }

    @Override
    public void testPeriodic() {
    }

}
