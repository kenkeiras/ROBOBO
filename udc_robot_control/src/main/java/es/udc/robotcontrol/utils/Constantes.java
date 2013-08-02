package es.udc.robotcontrol.utils;

/**
 * Created with IntelliJ IDEA.
 * User: kerry
 * Date: 1/08/13
 * Time: 19:39
 * Esta clase se utiliza para definir utils compartidas entre el robot y la librería de control (nombres de colas y comandos)
 */
public abstract class Constantes {

    // Publisher de audio
    public static final String PUBLISHER_AUDIO = "PUBLISHER_AUDIO";
    // Publisher de vídeo
    public static final String PUBLISHER_VIDEO = "PUBLISHER_VIDEO";
    // Publisher de estado de bateria
    public static final String PUBLISHER_BATERY = "PUBLISHER_BATERY";
    // Publisher de posición (no sólo GPS)
    public static final String PUBLISHER_GPS = "PUBLISHER_GPS";

    // Publisher de Acelerómetro
    public static final String PUBLISHER_ACCEL = "PUBLISHER_ACCEL";
    // Publisher de Gyroscopio
    public static final String PUBLISHER_GYRO = "PUBLISHER_GYRO";
    // Publisher de velocidad angular
    public static final String PUBLISHER_QUAT = "PUBLISHER_QUAT";
    // ACCEL + GYRO + QUAT
    public static final String PUBLISHER_IMU = "PUBLISHER_IMU";
    // Magnetómetro (brújula)
    public static final String PUBLISHER_MAG = "PUBLISHER_MAG";

    // Proximidad
    public static final String PUBLISHER_PROXIMITY = "PUBLISHER_PROXIMITY";
    // Temperatura
    public static final String PUBLISHER_TEMPERATURE = "PUBLISHER_TEMPERATURE";
    // Publisher de Iluminación
    public static final String PUBLISHER_ILLUMINANCE = "PUBLISHER_ILLUMINANCE";
    // Publisher de barómetro
    public static final String PUBLISHER_FLUID_PRESSURE = "PUBLISHER_FLUID_PRESSURE";


    public static final String CMD_START_LISTENER  = "START_LISTENER";
    public static final String CMD_STOP_LISTENER   = "STOP_LISTENER";
    public static final String CMD_SET_LED         = "SET_LET";
    public static final String CMD_SET_ONE_ENGINE  = "SET_ENGINE";
    public static final String CMD_SET_ENGINES     = "SET_ENGINES";

    // Nombres para los nodos
    public static final String NODE_COMMANDS    = "commands";
    public static final String NODE_AUDIO       = "audio";
    public static final String NODE_BATERY      = "batery";
    public static final String NODE_IMAGE       = "image";
    public static final String NODE_CAMERA_INFO = "camerainfo";
    public static final String NODE_NAV_SAT_FIX = "nav_sat_fix";
    public static final String NODE_ACEL        = "acel";
    public static final String NODE_GYRO        = "gyro";
    public static final String NODE_ROTATION    = "rotation";
    public static final String NODE_IMU         = "imu";
    public static final String NODE_MAGNETIC    = "magnetic";
    public static final String NODE_RANGE       = "range";
    public static final String NODE_TEMPERATURE = "temperature";
    public static final String NODE_PRESSURE    = "pressure";
    public static final String NODE_ILLUMINANCE = "illuminance";


    // Nombres para las colas
    public static final String TOPIC_COMMANDS    = "commands";
    public static final String TOPIC_AUDIO       = "audio";
    public static final String TOPIC_BATERY      = "batery";
    public static final String TOPIC_IMAGE       = "image";
    public static final String TOPIC_CAMERA_INFO = "camerainfo";
    public static final String TOPIC_NAV_SAT_FIX = "nav_sat_fix";
    public static final String TOPIC_ACEL        = "acel";
    public static final String TOPIC_GYRO        = "gyro";
    public static final String TOPIC_ROTATION    = "rotation";
    public static final String TOPIC_IMU         = "imu";
    public static final String TOPIC_MAGNETIC    = "magnetic";
    public static final String TOPIC_RANGE       = "range";
    public static final String TOPIC_TEMPERATURE = "temperature";
    public static final String TOPIC_PRESSURE    = "pressure";
    public static final String TOPIC_ILLUMINANCE = "illuminance";

    /*
                AudioData._TYPE,
            BateryStatus._TYPE,
            CompressedImage._TYPE,
            CameraInfo._TYPE,
            NavSatFix._TYPE,
            Imu._TYPE, // acelerometro
            Imu._TYPE, // gyroscopio
            Imu._TYPE, // quat
            Imu._TYPE, // imu
            MagneticField._TYPE,
            Range._TYPE,
            Temperature._TYPE,
            FluidPressure._TYPE,
            Illuminance._TYPE
     */

}
