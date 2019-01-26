package frc.states;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static frc.loops.Looper.PERIOD;
import static frc.states.ElevatorStateMachine.*;

public class ElevatorStateMachineTest {
    /*

    Tests:
    Zeroes on bottom limit triggered

     */

    public static final double EPSILON = 1E-10;
    private ElevatorStateMachine elevatorStateMachine;
    private ElevatorStateMachine.ElevatorState elevatorState = new ElevatorStateMachine.ElevatorState();

    @Before
    public void setup() {
        elevatorStateMachine = new ElevatorStateMachine();
        elevatorState = new ElevatorStateMachine.ElevatorState();
    }

    @Test
    public void zeroes(){
        final double startingEncoder = 100;
        elevatorStateMachine.setZeroing();
        elevatorState.encoder = startingEncoder;
        elevatorState = elevatorStateMachine.update(elevatorState);
        double lastDistFromGround = elevatorState.heightFromGround;
        double timestamp = 0.0;
        while(timestamp < 10.0){
            lastDistFromGround = elevatorState.heightFromGround;
            elevatorState.encoder = elevatorState.demand;
            elevatorState = elevatorStateMachine.update(elevatorState);
            Assert.assertEquals(ZEROING_VELOCITY * PERIOD, lastDistFromGround - elevatorState.heightFromGround, EPSILON);
            timestamp += PERIOD;
        }
    }

    @Test
    public void limitZeroing() {
        // TODO add in negative and zero offsets and a negative delta
        final double offsetValue = 100;
        final double deltaFromOffset = 20;

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
