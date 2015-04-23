package es.udc.fic.android.board;

/**
 * Board constants class.
 */
public class BoardConstants {

    // Logging tag
    final static String TAG = "ROBOBO_BOARD";

    // Movement constants
    public final static double SPEED_CONVERSION = 27.5f; // cm/s at max speed
    public final static double TURN_CONVERSION = 4.608f; // rad/s at max turn (left -1, right +1)

    public final static double DISTANCE_TO_AXIS = 0.045f; // 4,5cm

    // Broadcast keys
    public final static String RIGHT_WHEEL_UPDATE_KEY = "RIGHT_WHEEL_UPDATE";
    public final static String LEFT_WHEEL_UPDATE_KEY = "LEFT_WHEEL_UPDATE";
    public final static String DISTANCE_UPDATE_KEY = "DISTANCE_UPDATE";
    public final static String SET_WHEELS_ACTION = "SET_WHEELS";
}
