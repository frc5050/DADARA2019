package frc.states;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ElevatorStateMachineTest {
    public static final double EPSILON = 1E-10;
    private static final double FEED_FORWARD_WITH_CARGO = 0.35;
    private static final double FEED_FORWARD_WITHOUT_CARGO = 0.30;
    private static final double BOTTOM_DIST_FROM_GROUND = 0;
    private static final double DT = 0.01;
    private static final double ZEROING_VELOCITY = 0.02;
    private static final double SHAFT_DIAMETER = 1.732 * 0.0254;
    private static final double DISTANCE_PER_MOTOR_REVOLUTION = SHAFT_DIAMETER * Math.PI; // 1.732 * 0.0254 * Math.PI / 1.0
    private static final double ENCODER_OUTPUT_PER_REVOLUTION = 1.0;
    private static final double DISTANCE_PER_ENCODER_TICK = DISTANCE_PER_MOTOR_REVOLUTION / ENCODER_OUTPUT_PER_REVOLUTION;
    private ElevatorStateMachine elevatorStateMachine;

    @Before
    public void setup() {
        elevatorStateMachine = new ElevatorStateMachine();
    }

    private double heightFromGroundToEncoder(double heightFromGround) {
        return (heightFromGround - BOTTOM_DIST_FROM_GROUND) / DISTANCE_PER_ENCODER_TICK;
    }

    @Test
    public void limitZeroing() {
        // TODO add in negative and zero offsets and a negative delta
        final double offsetValue = 100;
        final double deltaFromOffset = 20;

        ElevatorStateMachine.ElevatorState elevatorState = new ElevatorStateMachine.ElevatorState();
        Assert.assertEquals(0.0, elevatorState.encoderFiltered, EPSILON);
        elevatorState.bottomLimitTouched = true;

        elevatorState.encoder = offsetValue;
        elevatorState = elevatorStateMachine.update(elevatorState);
        Assert.assertEquals(0, elevatorState.encoderFiltered, EPSILON);

        elevatorState.bottomLimitTouched = false;
        elevatorState.encoder = offsetValue + deltaFromOffset;
        elevatorState = elevatorStateMachine.update(elevatorState);
        Assert.assertEquals(deltaFromOffset, elevatorState.encoderFiltered, EPSILON);
    }

    @Test
    public void setOpenLoop(){
        final double percentage = 0.7;
        final double percentageNegative = -0.7;
        final double percentageZero = 0.0;
        final double insaneFeedForward = 100000.0;
        ElevatorStateMachine.ElevatorState elevatorState = new ElevatorStateMachine.ElevatorState();

        elevatorStateMachine.setOpenLoop(percentage);
        elevatorState.feedforward = insaneFeedForward;
        elevatorState = elevatorStateMachine.update(elevatorState);
        Assert.assertEquals(percentage, elevatorState.demand, EPSILON);
        Assert.assertEquals(0.0, elevatorState.feedforward, EPSILON);

        elevatorStateMachine.setOpenLoop(percentageNegative);
        elevatorState.feedforward = insaneFeedForward;
        elevatorState = elevatorStateMachine.update(elevatorState);
        Assert.assertEquals(percentageNegative, elevatorState.demand, EPSILON);
        Assert.assertEquals(0.0, elevatorState.feedforward, EPSILON);

        elevatorStateMachine.setOpenLoop(percentageZero);
        elevatorState.feedforward = insaneFeedForward;
        elevatorState = elevatorStateMachine.update(elevatorState);
        Assert.assertEquals(percentageZero, elevatorState.demand, EPSILON);
        Assert.assertEquals(0, elevatorState.feedforward, EPSILON);
    }
}
