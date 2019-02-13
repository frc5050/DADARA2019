package frc.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PidfConstantsTest {
    @Test
    public void pConstructor() {
        final double[] pValues = new double[]{-2001, 0.0, 20, 3.0 / 7.0};
        for (double p : pValues) {
            final PidfConstants pidfConstants = new PidfConstants(p);
            assertEquals(p, pidfConstants.p, 0);
            assertEquals(0.0, pidfConstants.i, 0);
            assertEquals(0.0, pidfConstants.d, 0);
            assertEquals(0.0, pidfConstants.f, 0);
            assertEquals(0.0, pidfConstants.iZone, 0);
        }
    }

    @Test
    public void pidConstructor() {
        final double[] pValues = new double[]{-2001, 0.0, 20, 3.0 / 7.0};
        final double[] iValues = new double[]{-21293, 0.0, 2450, 9.0 / 61.0};
        final double[] dValues = new double[]{-21589, 0.0, 240, 3.0 / 13.37};
        for (int i = 0; i < pValues.length; i++) {
            final PidfConstants pidfConstants = new PidfConstants(pValues[i], iValues[i], dValues[i]);
            assertEquals(pValues[i], pidfConstants.p, 0);
            assertEquals(iValues[i], pidfConstants.i, 0);
            assertEquals(dValues[i], pidfConstants.d, 0);
            assertEquals(0.0, pidfConstants.f, 0);
            assertEquals(0.0, pidfConstants.iZone, 0);
        }
    }

    @Test
    public void pidfConstructor() {
        final double[] pValues = new double[]{-2001, 0.0, 20, 3.0 / 7.0};
        final double[] iValues = new double[]{-21293, 0.0, 2450, 9.0 / 61.0};
        final double[] dValues = new double[]{-21589, 0.0, 240, 3.0 / 13.37};
        final double[] fValues = new double[]{-21534, 0.0, 243, 17.0 / 13.37};
        for (int i = 0; i < pValues.length; i++) {
            final PidfConstants pidfConstants = new PidfConstants(pValues[i], iValues[i], dValues[i], fValues[i]);
            assertEquals(pValues[i], pidfConstants.p, 0);
            assertEquals(iValues[i], pidfConstants.i, 0);
            assertEquals(dValues[i], pidfConstants.d, 0);
            assertEquals(fValues[i], pidfConstants.f, 0);
            assertEquals(0.0, pidfConstants.iZone, 0);
        }
    }

    @Test
    public void pidfIZoneConstructor() {
        final double[] pValues = new double[]{-2001, 0.0, 20, 3.0 / 7.0};
        final double[] iValues = new double[]{-21293, 0.0, 2450, 9.0 / 61.0};
        final double[] dValues = new double[]{-21589, 0.0, 240, 3.0 / 13.37};
        final double[] fValues = new double[]{-21534, 0.0, 243, 17.0 / 13.37};
        final double[] iZoneValues = new double[]{-25734, 0.0, 237, 19.0 / 23.0};
        for (int i = 0; i < pValues.length; i++) {
            final PidfConstants pidfConstants = new PidfConstants(pValues[i], iValues[i], dValues[i], fValues[i]);
            assertEquals(pValues[i], pidfConstants.p, 0);
            assertEquals(iValues[i], pidfConstants.i, 0);
            assertEquals(dValues[i], pidfConstants.d, 0);
            assertEquals(fValues[i], pidfConstants.f, 0);
            assertEquals(0.0, pidfConstants.iZone, 0);
        }
    }
}
