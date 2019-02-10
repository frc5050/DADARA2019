package frc.subsystem;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;

// TODO
public class Vision {
    private static Vision instance;

    private final UsbCamera hatchCamera, rightCargoCamera, leftCargoCamera;

    private Vision(){
        hatchCamera = CameraServer.getInstance().startAutomaticCapture(0);
        leftCargoCamera = CameraServer.getInstance().startAutomaticCapture(1);
        rightCargoCamera = CameraServer.getInstance().startAutomaticCapture(2);
        configureUsbCamera(hatchCamera);
        configureUsbCamera(leftCargoCamera);
        configureUsbCamera(rightCargoCamera);
    }

    private static void configureUsbCamera(UsbCamera camera){

        camera.setExposureAuto();
        camera.setWhiteBalanceAuto();
        camera.setFPS(20);
        camera.setResolution(160, 120);
    }

    public static Vision getInstance(){
        if(instance == null){
            instance = new Vision();
        }
        return instance;
    }
}