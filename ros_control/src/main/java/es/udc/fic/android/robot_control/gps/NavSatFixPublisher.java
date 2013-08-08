/*
 * Copyright (C) 2013 Amancio Díaz Suárez
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package es.udc.fic.android.robot_control.gps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import es.udc.fic.android.robot_control.utils.C;
import es.udc.robotcontrol.utils.Constantes;
import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import sensor_msgs.NavSatFix;
import sensor_msgs.NavSatStatus;


public class NavSatFixPublisher implements NodeMain {
    private LocationManager locationManager;
    private NavSatListener navSatFixListener;
    private Publisher<NavSatFix> publisher;
    private NavSatThread listenerThread;

    private static String QUEUE_NAME = Constantes.TOPIC_NAV_SAT_FIX;

    private Context context;
    private String robotName;


    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(C.DefaultBaseNodeName + "/" + QUEUE_NAME);
    }


    public NavSatFixPublisher(Context ctx, String robotName) {
        super();
        this.robotName = robotName;
        this.context = ctx;
        locationManager = (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
    }


    public String getTopicName() {
        return QUEUE_NAME;
    }

    public boolean isHardwarePresent() {
        PackageManager pm = context.getPackageManager();
        boolean hasGps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        return hasGps;
    }


    @Override
    public void onStart(ConnectedNode connectedNode) {
        try {
            if (isHardwarePresent()) {
                Log.w(C.TAG, "Hardware present for [ " + connectedNode.getName() + " ] topic [ " + getTopicName() + " ] will be created");
                String queueName = robotName + "/" + QUEUE_NAME;
                this.publisher = connectedNode.newPublisher(queueName, NavSatFix._TYPE);
                this.navSatFixListener = new NavSatListener(publisher);
                this.listenerThread = new NavSatThread(this.locationManager, this.navSatFixListener);
                Log.w(C.TAG, "Starting listener thread for [ " + connectedNode.getName() + " ] topic [ " + getTopicName() + " ]");
                this.listenerThread.start();
            }
            else {
                Log.w(C.TAG, "No hardware present for [ " + connectedNode.getName() + " ] No topic [ " + getTopicName() + " ] will be created");
            }
        }
        catch (Exception e) {
            Log.w(C.TAG, "Exception onStart [ " + e.getMessage() + " ] Node [ " + connectedNode + " ]", e);
            if (connectedNode != null) {
                Log.w(C.TAG, "Exception onStart [ " + e.getMessage() + " ] Node [ " + connectedNode.getName() + " ]", e);
                connectedNode .getLog().fatal(e);
            }
            else {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onShutdown(Node node) {
        Log.i(C.TAG, "onShutdown [ " + node.getName() + " ]");
        if(this.listenerThread != null) {
            this.listenerThread.shutdown();

            try {
                this.listenerThread.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onShutdownComplete(Node node) {
        Log.i(C.TAG, "onShutdownComplete [ " + node.getName() + " ]");
    }

    @Override
    public void onError(Node node, Throwable throwable) {
        Log.w(C.TAG, "onError [ " + node.getName() + " ]", throwable);
    }


    private class NavSatThread extends Thread {
        LocationManager locationManager;
        NavSatListener navSatListener;
        private Looper threadLooper;

        private NavSatThread(LocationManager locationManager, NavSatListener navSatListener){
            this.locationManager = locationManager;
            this.navSatListener = navSatListener;
        }

        public void run() {
            Looper.prepare();
            threadLooper = Looper.myLooper();
            this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this.navSatListener);
            this.locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this.navSatListener);
            this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this.navSatListener);
            Looper.loop();
        }

        public void shutdown(){
            this.locationManager.removeUpdates(this.navSatListener);
            if(threadLooper != null){
                threadLooper.quit();
            }
        }
    }


    private class NavSatListener implements LocationListener {

        private Publisher<NavSatFix> publisher;

        private volatile byte currentStatus;

        private NavSatListener(Publisher<NavSatFix> publisher) {
            this.publisher = publisher;
            this.currentStatus = NavSatStatus.STATUS_FIX; // Default to fix until we are told otherwise.
        }

        //	@Override
        public void onLocationChanged(Location location) {

            NavSatFix fix = this.publisher.newMessage();
            fix.getHeader().setStamp(Time.fromMillis(System.currentTimeMillis()));
            fix.getHeader().setFrameId(robotName);

            fix.getStatus().setStatus(currentStatus);
            fix.getStatus().setService(NavSatStatus.SERVICE_GPS);

            fix.setLatitude(location.getLatitude());
            fix.setLongitude(location.getLongitude());
            fix.setAltitude(location.getAltitude());
            fix.setPositionCovarianceType(NavSatFix.COVARIANCE_TYPE_APPROXIMATED);
            double deviation = location.getAccuracy();
            double covariance = deviation*deviation;
            double[] tmpCov = {covariance,0,0, 0,covariance,0, 0,0,covariance};
            fix.setPositionCovariance(tmpCov);
            publisher.publish(fix);
        }

        //	@Override
        public void onProviderDisabled(String provider) {
            Log.w(C.TAG, "Provider [ " + provider + " ] has been disabled");
        }

        //	@Override
        public void onProviderEnabled(String provider) {
            Log.w(C.TAG, "Provider [ " + provider + " ] has been enabled");
        }

        //	@Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.OUT_OF_SERVICE:
                    currentStatus = NavSatStatus.STATUS_NO_FIX;
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    currentStatus = NavSatStatus.STATUS_NO_FIX;
                    break;
                case LocationProvider.AVAILABLE:
                    currentStatus = NavSatStatus.STATUS_FIX;
                    break;
            }
        }
    }
}
