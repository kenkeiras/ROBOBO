package es.udc.fic.robobo.rosWrapper;

import org.ros.address.InetAddressFactory;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;

import java.lang.String;
import java.net.URI;
import java.util.UUID;

import audio_common_msgs.AudioData;
import es.udc.fic.robobo.rosWrapper.managers.listeners.AccelerometerListenerManager;
import es.udc.fic.robobo.rosWrapper.managers.listeners.AmbientTemperatureListenerManager;
import es.udc.fic.robobo.rosWrapper.managers.listeners.AprilTagListenerManager;
import es.udc.fic.robobo.rosWrapper.managers.listeners.AudioListenerManager;
import es.udc.fic.robobo.rosWrapper.managers.listeners.CompressedImageListenerManager;
import es.udc.fic.robobo.rosWrapper.managers.listeners.GravityListenerManager;
import es.udc.fic.robobo.rosWrapper.managers.listeners.GyroscopeListenerManager;
import es.udc.fic.robobo.rosWrapper.managers.listeners.ImuListenerManager;
import es.udc.fic.robobo.rosWrapper.managers.listeners.LightListenerManager;
import es.udc.fic.robobo.rosWrapper.managers.listeners.LinearAccelerationListenerManager;
import es.udc.fic.robobo.rosWrapper.managers.listeners.MagneticFieldListenerManager;
import es.udc.fic.robobo.rosWrapper.managers.listeners.NavSatFixListenerManager;
import es.udc.fic.robobo.rosWrapper.managers.listeners.OdometryListenerManager;
import es.udc.fic.robobo.rosWrapper.managers.listeners.OrientationListenerManager;
import es.udc.fic.robobo.rosWrapper.managers.listeners.PressureListenerManager;
import es.udc.fic.robobo.rosWrapper.managers.listeners.ProximityListenerManager;
import es.udc.fic.robobo.rosWrapper.managers.listeners.RelativeHumidityListenerManager;
import es.udc.fic.robobo.rosWrapper.managers.listeners.RobotSensorListenerManager;
import es.udc.fic.robobo.rosWrapper.managers.listeners.RotationVectorListenerManager;
import es.udc.fic.robobo.rosWrapper.managers.listeners.SpeechRecognitionListenerManager;
import es.udc.fic.robobo.rosWrapper.managers.producers.EnginesProducerManager;
import es.udc.fic.robobo.rosWrapper.managers.producers.InfoProducerManager;
import es.udc.fic.robobo.rosWrapper.managers.producers.TTSProducerManager;
import es.udc.fic.robobo.rosWrapper.utils.Tuple;
import nav_msgs.Odometry;
import sensor_msgs.CompressedImage;
import sensor_msgs.FluidPressure;
import sensor_msgs.Illuminance;
import sensor_msgs.Imu;
import sensor_msgs.MagneticField;
import sensor_msgs.NavSatFix;
import sensor_msgs.Range;
import sensor_msgs.RelativeHumidity;
import sensor_msgs.Temperature;
import udc_robot_control_msgs.AndroidSensor3;
import udc_robot_control_msgs.AprilTag;
import udc_robot_control_msgs.SensorStatus;

public class RoboboController implements NodeMain {

    private final NodeMainExecutor executor;

    // Listeners
    final AccelerometerListenerManager accelerometerListenerManager;
    final AmbientTemperatureListenerManager ambientTemperatureListenerManager;
    final AprilTagListenerManager aprilTagListenerManager;
    final AudioListenerManager audioListenerManager;
    final CompressedImageListenerManager compressedImageListenerManager;
    final GravityListenerManager gravityListenerManager;
    final GyroscopeListenerManager gyroscopeListenerManager;
    final ImuListenerManager imuListenerManager;
    final LightListenerManager lightListenerManager;
    final LinearAccelerationListenerManager linearAccelerationListenerManager;
    final MagneticFieldListenerManager magneticFieldListenerManager;
    final NavSatFixListenerManager navSatFixListenerManager;
    final OdometryListenerManager odometryListenerManager;
    final OrientationListenerManager orientationListenerManager;
    final PressureListenerManager pressureListenerManager;
    final ProximityListenerManager proximityListenerManager;
    final RelativeHumidityListenerManager relativeHumidityListenerManager;
    final RobotSensorListenerManager robotSensorListenerManager;
    final RotationVectorListenerManager rotationVectorListenerManager;
    final SpeechRecognitionListenerManager speechRecognitionListenerManager;

    // Publishers
    final InfoProducerManager infoProducerManager;
    final EnginesProducerManager enginesProducerManager;
    final TTSProducerManager ttsProducerManager;

    public RoboboController(URI masterURI, String robotName) throws ControllerNotFound {

        // Initialize listener managers
        accelerometerListenerManager = new AccelerometerListenerManager(robotName);
        ambientTemperatureListenerManager = new AmbientTemperatureListenerManager(robotName);
        aprilTagListenerManager = new AprilTagListenerManager(robotName);
        audioListenerManager = new AudioListenerManager(robotName);
        compressedImageListenerManager = new CompressedImageListenerManager(robotName);
        gravityListenerManager = new GravityListenerManager(robotName);
        gyroscopeListenerManager = new GyroscopeListenerManager(robotName);
        imuListenerManager = new ImuListenerManager(robotName);
        lightListenerManager = new LightListenerManager(robotName);
        linearAccelerationListenerManager = new LinearAccelerationListenerManager(robotName);
        magneticFieldListenerManager = new MagneticFieldListenerManager(robotName);
        navSatFixListenerManager = new NavSatFixListenerManager(robotName);
        odometryListenerManager = new OdometryListenerManager(robotName);
        orientationListenerManager = new OrientationListenerManager(robotName);
        pressureListenerManager = new PressureListenerManager(robotName);
        proximityListenerManager = new ProximityListenerManager(robotName);
        relativeHumidityListenerManager = new RelativeHumidityListenerManager(robotName);
        robotSensorListenerManager = new RobotSensorListenerManager(robotName);
        rotationVectorListenerManager = new RotationVectorListenerManager(robotName);
        speechRecognitionListenerManager = new SpeechRecognitionListenerManager(robotName);

        // Initialize producer managers
        infoProducerManager = new InfoProducerManager(robotName);
        enginesProducerManager = new EnginesProducerManager(robotName);
        ttsProducerManager = new TTSProducerManager(robotName);

        // Start this node
        executor = DefaultNodeMainExecutor.newDefault();
        String host = InetAddressFactory.newNonLoopback().getHostAddress();
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(host);

        nodeConfiguration.setMasterUri(masterURI);
        executor.execute(this, nodeConfiguration);
    }


    public void stop(){
        executor.shutdownNodeMain(this);
    }

    // ROS functions
    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("ROBOBO_controller_" + UUID.randomUUID().toString().replace("-", "_"));
    }

    @Override
    public void onShutdown(Node node) {}

    @Override
    public void onShutdownComplete(Node node) {}

    @Override
    public void onError(Node node, Throwable throwable) {}

    @Override
    public void onStart(ConnectedNode connectedNode) {
        // Listeners
        accelerometerListenerManager.setConnectedNode(connectedNode);
        ambientTemperatureListenerManager.setConnectedNode(connectedNode);
        aprilTagListenerManager.setConnectedNode(connectedNode);
        audioListenerManager.setConnectedNode(connectedNode);
        compressedImageListenerManager.setConnectedNode(connectedNode);
        gravityListenerManager.setConnectedNode(connectedNode);
        gyroscopeListenerManager.setConnectedNode(connectedNode);
        imuListenerManager.setConnectedNode(connectedNode);
        lightListenerManager.setConnectedNode(connectedNode);
        linearAccelerationListenerManager.setConnectedNode(connectedNode);
        magneticFieldListenerManager.setConnectedNode(connectedNode);
        navSatFixListenerManager.setConnectedNode(connectedNode);
        odometryListenerManager.setConnectedNode(connectedNode);
        orientationListenerManager.setConnectedNode(connectedNode);
        pressureListenerManager.setConnectedNode(connectedNode);
        proximityListenerManager.setConnectedNode(connectedNode);
        relativeHumidityListenerManager.setConnectedNode(connectedNode);
        robotSensorListenerManager.setConnectedNode(connectedNode);
        rotationVectorListenerManager.setConnectedNode(connectedNode);
        speechRecognitionListenerManager.setConnectedNode(connectedNode);

        // Producers
        infoProducerManager.setConnectedNode(connectedNode);
        enginesProducerManager.setConnectedNode(connectedNode);
        ttsProducerManager.setConnectedNode(connectedNode);
    }


    // Publishers
    /**
     * Publish a message to the Info screen.
     */
    public void publishInfoMessage(String htmlData){
        infoProducerManager.publish(htmlData);
    }

    /**
     * Set the engine linear and angular speed.
     */
    public void setEnginesTwist(double linearSpeed, double angularSpeed){
        enginesProducerManager.publish(new Tuple<>(linearSpeed, angularSpeed));
    }

    /**
     * Publish a message to the TextToSpeech system.
     */
    public void publishTextToSpeed(String text){
        ttsProducerManager.publish(text);
    }


    /// Listeners
    // Accelerometer
    public void addAccelerometerHandler(MessageListener<AndroidSensor3> handler){
        accelerometerListenerManager.addHandler(handler);
    }

    public void removeAccelerometerHandler(MessageListener<AndroidSensor3> handler){
        accelerometerListenerManager.removeHandler(handler);
    }

    // Ambient temperature
    public void addAmbientTemperatureHandler(MessageListener<Temperature> handler){
        ambientTemperatureListenerManager.addHandler(handler);
    }

    public void removeAmbientTemperatureHandler(MessageListener<Temperature> handler){
        ambientTemperatureListenerManager.removeHandler(handler);
    }

    // April tags
    public void addAprilTagHandler(MessageListener<AprilTag> handler) {
        aprilTagListenerManager.addHandler(handler);
    }

    public void removeAprilTagHandler(MessageListener<AprilTag> handler){
        aprilTagListenerManager.removeHandler(handler);
    }

    // Audio
    public void addAudioHandler(MessageListener<AudioData> handler){
        audioListenerManager.addHandler(handler);
    }

    public void removeAudioHandler(MessageListener<AudioData> handler){
        audioListenerManager.removeHandler(handler);
    }

    // CompressedImage
    public void addImageHandler(MessageListener<CompressedImage> handler){
        compressedImageListenerManager.addHandler(handler);
    }

    public void removeImageHandler(MessageListener<CompressedImage> handler){
        compressedImageListenerManager.removeHandler(handler);
    }

    // Gravity
    public void addGravityHandler(MessageListener<AndroidSensor3> handler){
        gravityListenerManager.addHandler(handler);
    }

    public void removeGravityHandler(MessageListener<AndroidSensor3> handler){
        gravityListenerManager.removeHandler(handler);
    }

    // Gyroscope
    public void addGyroscopeHandler(MessageListener<AndroidSensor3> handler){
        gyroscopeListenerManager.addHandler(handler);
    }

    public void removeGyroscopeHandler(MessageListener<AndroidSensor3> handler){
        accelerometerListenerManager.removeHandler(handler);
    }

    // IMU
    public void addImuHandler(MessageListener<Imu> handler){
        imuListenerManager.addHandler(handler);
    }

    public void removeImuHandler(MessageListener<Imu> handler){
        imuListenerManager.removeHandler(handler);
    }

    // Light
    public void addLightHandler(MessageListener<Illuminance> handler){
        lightListenerManager.addHandler(handler);
    }

    public void removeLightHandler(MessageListener<Illuminance> handler){
        lightListenerManager.removeHandler(handler);
    }

    // Linear acceleration
    public void addLinearAccelerationHandler(MessageListener<AndroidSensor3> handler){
        linearAccelerationListenerManager.addHandler(handler);
    }

    public void removeLinearAccelerationHandler(MessageListener<AndroidSensor3> handler){
        linearAccelerationListenerManager.removeHandler(handler);
    }

    // Magnetic field
    public void addMagneticFieldHandler(MessageListener<MagneticField> handler){
        magneticFieldListenerManager.addHandler(handler);
    }

    public void removeMagneticFieldHandler(MessageListener<MagneticField> handler){
        magneticFieldListenerManager.removeHandler(handler);
    }

    // Nav Sat Fix
    public void addNavSatFixHandler(MessageListener<NavSatFix> handler){
        navSatFixListenerManager.addHandler(handler);
    }

    public void removeNavSatFixHandler(MessageListener<NavSatFix> handler){
        navSatFixListenerManager.removeHandler(handler);
    }

    // Odometry
    public void addOdometryHandler(MessageListener<Odometry> handler){
        odometryListenerManager.addHandler(handler);
    }

    public void removeOdometryHandler(MessageListener<Odometry> handler){
        odometryListenerManager.removeHandler(handler);
    }

    // Orientation
    public void addOrientationHandler(MessageListener<AndroidSensor3> handler){
        orientationListenerManager.addHandler(handler);
    }

    public void removeOrientationHandler(MessageListener<AndroidSensor3> handler){
        orientationListenerManager.removeHandler(handler);
    }

    // Pressure
    public void addPressureHandler(MessageListener<FluidPressure> handler){
        pressureListenerManager.addHandler(handler);
    }

    public void removePressureHandler(MessageListener<FluidPressure> handler){
        pressureListenerManager.removeHandler(handler);
    }

    // Proximity
    public void addProximityHandler(MessageListener<Range> handler){
        proximityListenerManager.addHandler(handler);
    }

    public void removeProximityHandler(MessageListener<Range> handler){
        proximityListenerManager.removeHandler(handler);
    }

    // Relative humidity
    public void addRelativeHumidityHandler(MessageListener<RelativeHumidity> handler){
        relativeHumidityListenerManager.addHandler(handler);
    }

    public void removeRelativeHumidityHandler(MessageListener<RelativeHumidity> handler){
        relativeHumidityListenerManager.removeHandler(handler);
    }

    // Robot sensor
    public void addRobotSensorHandler(MessageListener<SensorStatus> handler){
        robotSensorListenerManager.addHandler(handler);
    }

    public void removeRobotSensorHandler(MessageListener<SensorStatus> handler){
        robotSensorListenerManager.removeHandler(handler);
    }

    // Rotation vector
    public void addRotationVectorHandler(MessageListener<AndroidSensor3> handler){
        rotationVectorListenerManager.addHandler(handler);
    }

    public void removeRotationVectorHandler(MessageListener<AndroidSensor3> handler){
        rotationVectorListenerManager.removeHandler(handler);
    }

    // Speech recognition
    public void addSpeechRecognitionHandler(MessageListener<std_msgs.String> handler){
        speechRecognitionListenerManager.addHandler(handler);
    }

    public void removeSpeechRecognitionHandler(MessageListener<std_msgs.String> handler){
        speechRecognitionListenerManager.removeHandler(handler);
    }
}
