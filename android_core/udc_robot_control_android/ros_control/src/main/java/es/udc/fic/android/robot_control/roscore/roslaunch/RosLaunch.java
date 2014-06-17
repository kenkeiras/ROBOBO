package es.udc.fic.android.robot_control.roscore.roslaunch;

import com.nmote.nanohttp.NanoServer;

import com.nmote.xr.Endpoint;
import com.nmote.xr.HTTPServerEndpoint;
import com.nmote.xr.MethodCall;
import com.nmote.xr.MethodResponse;
import com.nmote.xr.XR;

import java.io.IOException;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.net.URI;
import java.net.URISyntaxException;

/// @TODO Target env['ROS_MASTER_URI']
public class RosLaunch {

    private int port;
    private NanoServer server;


    private String getHostIp() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }


    private String getHostName() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }


    private void registerService(int port) throws IOException {
        String uri = "http://" + getHostIp() + ":" + port + "/";
        String identifier = "host_" + getHostName() + "__" + port;

        Endpoint server;
        try {
             server = XR.client(new URI("http://127.0.0.1:11311/RPC2"));
        }
        catch (URISyntaxException e){
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
        MethodCall call = new MethodCall("setParam",
                                         "/roslaunch",
                                         "/roslaunch/uris/" + identifier,
                                         uri);

        MethodResponse response = server.call(call);
        System.out.println(call + " => " + response);
    }


    private static int getFreePortNumber() throws IOException {
        ServerSocket server = new ServerSocket(0);
        int port = server.getLocalPort();
        server.close();
        return port;
    }


    public RosLaunch() throws IOException {
        port = getFreePortNumber();
        server = new NanoServer(port);
        server.add(XR.server(RosLaunchServer.class));
    }


    public void start() throws IOException {
        server.start();
        registerService(port);
    }

}
