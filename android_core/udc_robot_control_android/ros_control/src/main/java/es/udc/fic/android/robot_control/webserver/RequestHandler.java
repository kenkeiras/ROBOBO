package es.udc.fic.android.robot_control.webserver;

import android.util.Log;

import java.util.Properties;

public class RequestHandler implements AndroidHTTPD.RequestHandler {
    @Override
    public NanoHTTPDPooled.Response onRequestReceived(String uri,
                                                      String method,
                                                      Properties header,
                                                      Properties parms,
                                                      Properties files) {

        Log.d("UDC_WEBSERVER", "New request");
        return new NanoHTTPDPooled.Response(NanoHTTPDPooled.HTTP_OK,
                                            NanoHTTPDPooled.MIME_PLAINTEXT,
                                            "OK");
    }
}
