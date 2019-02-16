package frc.subsystem.test;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;

import static frc.utils.Constants.*;

public class DriveHardwareTest extends TimedRobot {
    private static final double TIME_TO_RUN = 2.0;
    private static final double BREAK_TIME = 0.3;
    private static final double PERCENT_OUTPUT = 0.20;
    private final WPI_TalonSRX leftMaster;
    private final VictorSPX leftSlave;
    private final WPI_TalonSRX rightMaster;
    private final VictorSPX rightSlave;
    private State state = State.INIT;
    private double lastTimestamp = Timer.getFPGATimestamp();

    private DriveHardwareTest() {
        leftMaster = new WPI_TalonSRX(LEFT_DRIVE_1);
        leftSlave = new VictorSPX(LEFT_DRIVE_2);

        rightMaster = new WPI_TalonSRX(RIGHT_DRIVE_1);
        rightSlave = new VictorSPX(RIGHT_DRIVE_2);

        rightMaster.setInverted(true);
        rightSlave.setInverted(true);
    }

    @Override
    public void robotInit() {
    }

    @Override
    public void teleopPeriodic() {
        double timestamp = Timer.getFPGATimestamp();
        stateChanger(timestamp);
        switch (state) {
            case INIT:
                break;
            case LEFT_MASTER_FORWARD:
                leftMaster.set(ControlMode.PercentOutput, getValue(timestamp, true));
                break;
            case LEFT_MASTER_REVERSE:
                leftMaster.set(ControlMode.PercentOutput, getValue(timestamp, false));
                break;
            case LEFT_SLAVE_FORWARD:
                leftSlave.set(ControlMode.PercentOutput, getValue(timestamp, true));
                break;
            case LEFT_SLAVE_REVERSE:
                leftSlave.set(ControlMode.PercentOutput, getValue(timestamp, false));
                break;
            case RIGHT_MASTER_FORWARD:
                rightMaster.set(ControlMode.PercentOutput, getValue(timestamp, true));
                break;
            case RIGHT_MASTER_REVERSE:
                rightMaster.set(ControlMode.PercentOutput, getValue(timestamp, false));
                break;
            case RIGHT_SLAVE_FORWARD:
                leftMaster.set(ControlMode.PercentOutput, getValue(timestamp, true));
                break;
            case RIGHT_SLAVE_REVERSE:
                leftMaster.set(ControlMode.PercentOutput, getValue(timestamp, false));
                break;
            case STOP:
                leftMaster.set(ControlMode.PercentOutput, 0.0);
                leftSlave.set(ControlMode.PercentOutput, 0.0);
                rightMaster.set(ControlMode.PercentOutput, 0.0);
                rightSlave.set(ControlMode.PercentOutput, 0.0);
        }
    }

    private boolean isTimeToChangeState(double timestamp) {
        return timestamp - lastTimestamp > TIME_TO_RUN + BREAK_TIME;
    }

    private double getValue(double timestamp, boolean forward) {
        return (timestamp - lastTimestamp > TIME_TO_RUN) ? 0.0 : (forward ? PERCENT_OUTPUT : -PERCENT_OUTPUT);
    }

    private void stateChanger(double timestamp) {
        if (isTimeToChangeState(timestamp) && state != State.STOP) {
            switch (state) {
                case INIT:
                    state = State.LEFT_MASTER_FORWARD;
                    break;
                case LEFT_MASTER_FORWARD:
                    state = State.LEFT_MASTER_REVERSE;
                    break;
                case LEFT_MASTER_REVERSE:
                    state = State.LEFT_SLAVE_FORWARD;
                    break;
                case LEFT_SLAVE_FORWARD:
                    state = State.LEFT_SLAVE_REVERSE;
                    break;
                case LEFT_SLAVE_REVERSE:
                    state = State.RIGHT_MASTER_FORWARD;
                    break;
                case RIGHT_MASTER_FORWARD:
                    state = State.RIGHT_MASTER_REVERSE;
                    break;
                case RIGHT_MASTER_REVERSE:
                    state = State.RIGHT_SLAVE_FORWARD;
                    break;
                case RIGHT_SLAVE_FORWARD:
                    state = State.RIGHT_SLAVE_REVERSE;
                    break;
                case RIGHT_SLAVE_REVERSE:
                    state = State.STOP;
                    break;
                case STOP:
                    break;
            }
            state = State.STOP;
            lastTimestamp = timestamp;
            System.out.println(state.toString());
        }
    }

    private enum State {
        INIT,
        LEFT_MASTER_FORWARD,
        LEFT_MASTER_REVERSE,
        LEFT_SLAVE_FORWARD,
        LEFT_SLAVE_REVERSE,
        RIGHT_MASTER_FORWARD,
        RIGHT_MASTER_REVERSE,
        RIGHT_SLAVE_FORWARD,
        RIGHT_SLAVE_REVERSE,
        STOP
    }
}
