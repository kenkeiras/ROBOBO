package es.udc.robotcontrol.testapp.comunication;

/**
 * Clase para contener valores constantes, cosas coo valores por defecto.
 *
 * Estudiar lo que se cambiará a una posible configuración.
 *
 * Created by kerry on 2/06/13.
 */
public final class Constantes {

    public static int VENDOR_ID = 0x04D8;
    public static int PRODUCT_ID = 0x003F;

    public static int NUMERO_LEDS     = 8;
    public static int NUMERO_SENSORES = 11;


    public static String COMANDO   = "COMANDO";
    public static String ESTADO    = "ESTADO";
    public static String PARAMETRO = "PARAMETRO";
    public static String PARAM_LIMPIAR_COLA = "LIMPIAR_COLA";
    public static String PARAM_READ_SLEEP_TIME  = "READ_SLEEP_TIME";
    public static String PARAM_WRITE_SLEEP_TIME = "WRITE_SLEEP_TIME";
    public static long   PARAM_DEFAULT_SLEEP_TIME = 1000;
    public static String PARAM_REPORT_URL = "REPORT_URL";

    // Log Tags
    public static String TAG_PROCESO  = "PROCESO";
    public static String TAG_DESCARGA = "DESCARGA";
    public static String TAG_SERVICIO = "SERVICIO_ROBOT";
    public static String TAG_CONECTOR = "CONECTOR";
}
