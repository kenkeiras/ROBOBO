package es.udc.fic.android.robot_control.webserver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.util.Properties;

import es.udc.fic.android.robot_control.R;
import es.udc.fic.android.robot_control.commands.EngineManager;


public class RequestHandler implements AndroidHTTPD.RequestHandler {

    private static final String TAG = "UDC_ROBOT_RequestHandler";
    RobotStateWrapper wrapper;
    Context ctx;

    public RequestHandler(RobotStateWrapper wrapper, Context ctx){
        this.wrapper = wrapper;
        this.ctx = ctx;
    }

    // Responses to sensor queries
    private NanoHTTPDPooled.Response getAccelerationResponse(){
        float[] accelerations = wrapper.getAcceleration();

        return new NanoHTTPDPooled.Response(NanoHTTPDPooled.HTTP_OK,
                NanoHTTPDPooled.MIME_JSON,
                "{\n"
                + "    \"accelerationX\": " + accelerations[0] + ",\n"
                + "    \"accelerationY\": " + accelerations[1] + ",\n"
                + "    \"accelerationZ\": " + accelerations[2] + "\n"
                + "}");
    }


    private NanoHTTPDPooled.Response getBatteryResponse(){
        return new NanoHTTPDPooled.Response(NanoHTTPDPooled.HTTP_OK,
                NanoHTTPDPooled.MIME_JSON,
                "{\"batteryLevel\": " + wrapper.getBatteryLevel() + "}");
    }


    private NanoHTTPDPooled.Response getCompressedImageResponse(){
        byte[] compressedImage = wrapper.getLastCompressedImage();
        if (compressedImage == null){
            return responseNotFound();
        }

        return new NanoHTTPDPooled.Response(NanoHTTPDPooled.HTTP_OK,
                NanoHTTPDPooled.MIME_JPEG, new ByteArrayInputStream(compressedImage));
    }


    private NanoHTTPDPooled.Response getGravityResponse() {
        float[] gravity = wrapper.getGravity();

        return new NanoHTTPDPooled.Response(NanoHTTPDPooled.HTTP_OK,
                NanoHTTPDPooled.MIME_JSON,
                "{\n"
                + "    \"gravityX\": " + gravity[0] + ",\n"
                + "    \"gravityY\": " + gravity[1] + ",\n"
                + "    \"gravityZ\": " + gravity[2] + "\n"
                + "}");
    }


    private NanoHTTPDPooled.Response getGyroscopeResponse() {
        float[] gyroscope = wrapper.getGyroscope();

        return new NanoHTTPDPooled.Response(NanoHTTPDPooled.HTTP_OK,
                NanoHTTPDPooled.MIME_JSON,
                "{\n"
                + "    \"gyroscopeX\": " + gyroscope[0] + ",\n"
                + "    \"gyroscopeY\": " + gyroscope[1] + ",\n"
                + "    \"gyroscopeZ\": " + gyroscope[2] + "\n"
                + "}");
    }


    private NanoHTTPDPooled.Response getIrSensorsResponse() {
        int[] irSensors = wrapper.getIrSensors();
        int len = irSensors.length;
        StringBuilder sensorsJsonBuilder = new StringBuilder();

        for (int i = 0; i < len; i++){
            sensorsJsonBuilder.append("    \"irSensor" + i + "\": " + irSensors[i] + ",\n");
        }

        String sensorsJson = sensorsJsonBuilder.toString();
        sensorsJson = sensorsJson.substring(0, sensorsJson.length() - 2); // Remove the last “,” to make it standard

        return new NanoHTTPDPooled.Response(NanoHTTPDPooled.HTTP_OK,
                NanoHTTPDPooled.MIME_JSON,
                "{\n" + sensorsJson + "\n}");
    }


    private NanoHTTPDPooled.Response getLightResponse(){
        return new NanoHTTPDPooled.Response(NanoHTTPDPooled.HTTP_OK,
                NanoHTTPDPooled.MIME_JSON,
                "{\"light\": " + wrapper.getLight() + "}");
    }


    private NanoHTTPDPooled.Response getMagneticFieldResponse() {
        float[] magneticField = wrapper.getMagneticField();

        return new NanoHTTPDPooled.Response(NanoHTTPDPooled.HTTP_OK,
                NanoHTTPDPooled.MIME_JSON,
                "{\n"
                + "    \"magneticFieldX\": " + magneticField[0] + ",\n"
                + "    \"magneticFieldY\": " + magneticField[1] + ",\n"
                + "    \"magneticFieldZ\": " + magneticField[2] + "\n"
                + "}");
    }


    private NanoHTTPDPooled.Response getPressureResponse(){
        return new NanoHTTPDPooled.Response(NanoHTTPDPooled.HTTP_OK,
                NanoHTTPDPooled.MIME_JSON,
                "{\"pressure\": " + wrapper.getPressure() + "}");
    }


    private NanoHTTPDPooled.Response getProximityResponse(){
        return new NanoHTTPDPooled.Response(NanoHTTPDPooled.HTTP_OK,
                NanoHTTPDPooled.MIME_JSON,
                "{\"proximity\": " + wrapper.getProximity() + "}");
    }


    private NanoHTTPDPooled.Response getTemperatureResponse(){
        return new NanoHTTPDPooled.Response(NanoHTTPDPooled.HTTP_OK,
                NanoHTTPDPooled.MIME_JSON,
                "{\"temperature\": " + wrapper.getTemperature() + "}");
    }


    private NanoHTTPDPooled.Response getWheelsResponse() {
        double[] wheels = wrapper.getWheels();

        return new NanoHTTPDPooled.Response(NanoHTTPDPooled.HTTP_OK,
                NanoHTTPDPooled.MIME_JSON,
                "{\n"
                + "    \"leftWheel\": " + wheels[0] + ",\n"
                + "    \"rightWheel\": " + wheels[1] + "\n"
                + "}");
    }


    private NanoHTTPDPooled.Response updatedEnginesResponse(boolean updated) {
        if (updated){
            return new NanoHTTPDPooled.Response(NanoHTTPDPooled.HTTP_OK,
                    NanoHTTPDPooled.MIME_PLAINTEXT, "Wheel speeds updated");
        }
        else {
            return new NanoHTTPDPooled.Response(NanoHTTPDPooled.HTTP_BADREQUEST,
                    NanoHTTPDPooled.MIME_PLAINTEXT, "Wheel speeds not found in parameters");
        }
    }


    private NanoHTTPDPooled.Response handleSensorRequest(String uri) {
        if (uri.equals("/sensors/battery")){
            return getBatteryResponse();
        }
        else if (uri.equals("/sensors/accelerometer")){
            return getAccelerationResponse();
        }
        else if (uri.equals("/sensors/compressedImage")){
            return getCompressedImageResponse();
        }
        else if (uri.equals("/sensors/gyroscope")){
            return getGyroscopeResponse();
        }
        else if (uri.equals("/sensors/gravity")){
            return getGravityResponse();
        }
        else if (uri.equals("/sensors/ir")){
            return getIrSensorsResponse();
        }
        else if (uri.equals("/sensors/light")){
            return getLightResponse();
        }
        else if (uri.equals("/sensors/magneticField")){
            return getMagneticFieldResponse();
        }
        else if (uri.equals("/sensors/pressure")){
            return getPressureResponse();
        }
        else if (uri.equals("/sensors/proximity")){
            return getProximityResponse();
        }
        else if (uri.equals("/sensors/temperature")){
            return getTemperatureResponse();
        }
        else {
            return responseNotFound();
        }
    }


    private NanoHTTPDPooled.Response handleActuatorRequest(String uri, String method, Properties params) {
        if (method.equals("GET")) {
            if (uri.equals("/actuators/wheels")) {
                return getWheelsResponse();
            }
        }
        else if (method.equals("POST")){
            if (uri.equals("/actuators/wheels")) {
                Intent i = new Intent(EngineManager.SET_WHEELS_ACTION);
                boolean withExtra = false;
                if (params.containsKey("leftWheel")){
                    try {
                        i.putExtra(EngineManager.LEFT_WHEEL_UPDATE_KEY,
                                Double.parseDouble(params.getProperty("leftWheel")));
                        withExtra = true;
                    }
                    catch (NumberFormatException nfe){
                        Log.e(TAG, nfe.getMessage());
                    }
                }
                if (params.containsKey("rightWheel")){
                    try {
                        i.putExtra(EngineManager.RIGHT_WHEEL_UPDATE_KEY,
                                Double.parseDouble(params.getProperty("rightWheel")));
                        withExtra = true;
                    }
                    catch (NumberFormatException nfe){
                        Log.e(TAG, nfe.getMessage());
                    }
                }
                if (withExtra){
                    ctx.sendBroadcast(i);
                }

                return updatedEnginesResponse(withExtra);
            }
        }

        return responseNotFound();
    }


    // General query management
    @Override
    public NanoHTTPDPooled.Response onRequestReceived(String uri,
                                                      String method,
                                                      Properties header,
                                                      Properties params,
                                                      Properties files) {

        if (uri.equals("/")) {
            return new NanoHTTPDPooled.Response(NanoHTTPDPooled.HTTP_OK,
                    NanoHTTPDPooled.MIME_HTML,
                    ctx.getResources().openRawResource(R.raw.dashboardhtml));
        }
        else if (uri.equals("/dash.css")) {
            return new NanoHTTPDPooled.Response(NanoHTTPDPooled.HTTP_OK,
                    NanoHTTPDPooled.MIME_CSS,
                    ctx.getResources().openRawResource(R.raw.dashboardcss));
        }
        else if (uri.equals("/dash.js")) {
            return new NanoHTTPDPooled.Response(NanoHTTPDPooled.HTTP_OK,
                    NanoHTTPDPooled.MIME_JAVASCRIPT,
                    ctx.getResources().openRawResource(R.raw.dashboardjs));
        }
        else if (uri.equals("/jquery.js")) {
            return new NanoHTTPDPooled.Response(NanoHTTPDPooled.HTTP_OK,
                    NanoHTTPDPooled.MIME_JAVASCRIPT,
                    ctx.getResources().openRawResource(R.raw.jquery));
        }
        else if (uri.startsWith("/sensors/")){
            return handleSensorRequest(uri);
        }
        else if (uri.startsWith("/actuators/")){
            return handleActuatorRequest(uri, method, params);
        }
        else {
            return responseNotFound();
        }
    }


    private NanoHTTPDPooled.Response responseNotFound(){
        return new NanoHTTPDPooled.Response(NanoHTTPDPooled.HTTP_NOTFOUND,
                NanoHTTPDPooled.MIME_PLAINTEXT,
                "Not found, better luck next time :/");
    }
}
