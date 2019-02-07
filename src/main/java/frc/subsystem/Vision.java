package frc.subsystem;

import edu.wpi.first.cameraserver.CameraServer;

// TODO
public class Vision {
    private static Vision instance;

    private Vision(){
        CameraServer.getInstance().startAutomaticCapture(0);
        CameraServer.getInstance().startAutomaticCapture(1);
    }

    public static Vision getInstance(){
        if(instance == null){
            instance = new Vision();
        }
        return instance;
    }
}