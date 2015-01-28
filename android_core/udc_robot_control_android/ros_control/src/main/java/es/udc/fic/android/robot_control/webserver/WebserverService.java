package es.udc.fic.android.robot_control.webserver;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class WebserverService extends Service {


    private static final String TAG = "UDC_WEBSERVER";
    private final IBinder sBinder = (IBinder) new SimpleBinder();
    private AndroidHTTPD httpd;
    private final int port = 8888;
    private final String wwwroot = "/sdcard/ros";

    public class SimpleBinder extends Binder {
        public WebserverService getService(){
            return WebserverService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return sBinder;
    }


    public void onCreate(){
        Log.i(TAG, "Starting webserver");
        try {
            RobotStateWrapper wrapper = new RobotStateWrapper(this);
            httpd = new AndroidHTTPD(this, port, new File(wwwroot), new RequestHandler(wrapper));
            httpd.startServer();
        } catch(IOException e){
            httpd = null;
            Log.d(TAG, e.getMessage());
        }
    }
}
