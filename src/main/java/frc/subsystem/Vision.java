package frc.subsystem;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;

public final class Vision {
    private static final int FPS = 20;
    private static final int IMAGE_WIDTH = 160;
    private static final int IMAGE_HEIGHT = 120;
    private static Vision instance;

    private final UsbCamera hatchCamera, rightCargoCamera, leftCargoCamera;

    private Vision() {
        hatchCamera = CameraServer.getInstance().startAutomaticCapture(0);
        leftCargoCamera = CameraServer.getInstance().startAutomaticCapture(1);
        rightCargoCamera = CameraServer.getInstance().startAutomaticCapture(2);
        configureUsbCamera(hatchCamera);
        configureUsbCamera(leftCargoCamera);
        configureUsbCamera(rightCargoCamera);
    }

    private static void configureUsbCamera(UsbCamera camera) {
        camera.setExposureAuto();
        camera.setWhiteBalanceAuto();
        camera.setFPS(FPS);
        camera.setResolution(IMAGE_WIDTH, IMAGE_HEIGHT);
    }

    public static Vision getInstance() {
        if (instance == null) {
            instance = new Vision();
        }
        return instance;
    }
}