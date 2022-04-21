package nsu.graphics.thirdlab;

import java.io.*;
import java.util.Properties;

public class PropertyList {
    public int wireframeObjectWindowWidth = 800;
    public int wireframeObjectWindowHeight = 600;
    public int templateWindowWidth = 1000;
    public int templateWindowHeight = 600;
    public int objectColor = -10066177;
    public int splineColor = 500;
    public int pointsColor = -52429;
    private final Properties appProps;

    public PropertyList() {
        appProps = new Properties();
        String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        path = path.substring(0,path.lastIndexOf('/'));
        path+="/app.properties";
        try (InputStream inputStream = new FileInputStream(path)){
            appProps.load(inputStream);
            wireframeObjectWindowWidth = Integer.parseInt(appProps.getProperty("wireframeObjectWindowWidth"));
            wireframeObjectWindowHeight = Integer.parseInt(appProps.getProperty("wireframeObjectWindowHeight"));
            templateWindowWidth = Integer.parseInt(appProps.getProperty("templateWindowWidth"));
            templateWindowHeight = Integer.parseInt(appProps.getProperty("templateWindowHeight"));
            objectColor = Integer.parseInt(appProps.getProperty("objectColor"));
            splineColor = Integer.parseInt(appProps.getProperty("splineColor"));
            pointsColor = Integer.parseInt(appProps.getProperty("pointsColor"));
        } catch (IOException e) {
            try(FileOutputStream fOut = new FileOutputStream(path)){
                appProps.setProperty("wireframeObjectWindowWidth", String.valueOf(wireframeObjectWindowWidth));
                appProps.setProperty("wireframeObjectWindowHeight", String.valueOf(wireframeObjectWindowHeight));
                appProps.setProperty("templateWindowWidth", String.valueOf(templateWindowWidth));
                appProps.setProperty("templateWindowHeight", String.valueOf(templateWindowHeight));
                appProps.setProperty("objectColor", String.valueOf(objectColor));
                appProps.setProperty("splineColor", String.valueOf(splineColor));
                appProps.setProperty("pointsColor", String.valueOf(pointsColor));
                appProps.store(fOut, null);
            }
            catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void store(){
        try {
            String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            path = path.substring(0,path.lastIndexOf('/'));
            path+="/app.properties";
            try(FileOutputStream fOut = new FileOutputStream(path)){
                appProps.store(fOut, null);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setObjectColor(int color){
        appProps.setProperty("objectColor", String.valueOf(color));
        store();
    }

    public void setSplineColor(int color){
        appProps.setProperty("splineColor", String.valueOf(color));
        store();
    }

    public void setPointsColor(int color){
        appProps.setProperty("pointsColor", String.valueOf(color));
        store();
    }
}
