package frc.utils;

import frc.subsystem.DifferentialDriveNoSafety;
import org.junit.Assert;
import org.junit.Test;

public class DifferentialBuiltinVCustom {


    @Test
    public void arcadeEquivalency() {
        DifferentialDriveNoSafety differentialDriveNoSafety = new DifferentialDriveNoSafety();
        for (double x = -1.0; x < 1.0; x += 0.01) {
            for (double y = -1.0; y < 1.0; y += 0.01) {
                final DriveSignal builtInDiff = differentialDriveNoSafety.arcadeDrive(x, y);
                final DriveSignal customDiff = DriveHelper.arcadeToDriveSignal(x, y);
                Assert.assertEquals(builtInDiff.getLeftOutput(), customDiff.getLeftOutput(), Constants.EPSILON_SMALL_DOUBLE);
                Assert.assertEquals(builtInDiff.getRightOutput(), customDiff.getRightOutput(), Constants.EPSILON_SMALL_DOUBLE);
            }
        }
    }
}