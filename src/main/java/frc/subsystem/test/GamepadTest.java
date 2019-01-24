package frc.subsystem.test;

import frc.robot.GameController;

public class GamepadTest implements SubsystemTest {
    private GameController gameController = GameController.getInstance();

    @Override
    public void periodic(double timestamp) {
        // TODO, do nothing?
    }

    @Override
    public void outputTelemetry() {
        gameController.outputTelemetry();
    }
}
