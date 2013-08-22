package es.udc.robot_control.gui.action;

/**
 * Created with IntelliJ IDEA.
 * User: kerry
 * Date: 9/08/13
 * Time: 19:22
 *
 * This class is a data model for the combo box.
 * Represents sensors in the robot
 *
 */
public class SensorModel {

    private int sensorValue;
    private String sensorName;

    public SensorModel(int value, String name) {
        setSensorValue(value);
        setSensorName(name);
    }

    @Override
    public String toString() {
        return getSensorName();
    }

    public int getSensorValue() {
        return sensorValue;
    }

    public void setSensorValue(int sensorValue) {
        this.sensorValue = sensorValue;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }
}
