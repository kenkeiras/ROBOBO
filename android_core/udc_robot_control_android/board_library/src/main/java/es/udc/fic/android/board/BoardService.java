package es.udc.fic.android.board;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.HashSet;
import java.util.Set;

import geometry_msgs.Twist;

/**
 * Android Service used to manage the ROBOBO board Engines and Sensors.
 *
 */
public class BoardService extends Service {

    // Service interface (binding)
    private final IBinder sBinder = (IBinder) new SimpleBinder();
    private RobotState lastState;

    public class SimpleBinder extends Binder {
        public BoardService getService(){
            return BoardService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sBinder;
    }

    // Initialization
    @Override
    public void onCreate(){
        engineManager = new EngineManager(this);
    }

    // Robot state management
    private RobotState robotState = new RobotState();
    RobotState getLastState() {
        engineManager.refresh(robotState);
        return robotState;
    }

    // Engines interface
    private EngineManager engineManager = null;
    public void setEngines(double leftEngine, double rightEngine, double distance) {
        if (engineManager != null){
            engineManager.setEngines(leftEngine, rightEngine, distance);
        }
    }

    public void setTwist(Twist twist) {
        if (engineManager != null) {
            engineManager.setTwist(twist);
        }
    }

    // Connection management
    private BoardControlThread controlThread = null;
    public void connect() throws TransmisionErrorException {
        if (isConnected()){
            disconnect();
        }
        boardConnector.manualConnect(this);
        controlThread = new BoardControlThread(this);
        controlThread.start();
    }

    public void connect(Intent usbIntent) throws TransmisionErrorException {
        if (isConnected()){
            disconnect();
        }
        boardConnector.connect(this, usbIntent);
        controlThread = new BoardControlThread(this);
        controlThread.start();
    }


    public boolean isConnected(){
        return (boardConnector != null) && (boardConnector.isConnected());
    }

    public void disconnect(){
        if (isConnected()){
            boardConnector.disconnect();
            controlThread.interrupt();

            try {
                controlThread.join();
            } catch (InterruptedException e) {}
        }
    }


    // Information transfer on the connection
    private BoardConnector boardConnector = new BoardConnector();
    byte[] readFromConnector() {
        if (boardConnector == null){
            return null;
        }

        return boardConnector.read();
    }


    void writeToConnector(RobotState robotState) {
        if ((boardConnector != null) && (boardConnector.isConnected())){
            boardConnector.write(robotState);
        }
    }

    // Callback methods
    private final Set<SensorInfoHandler> handlers = new HashSet<SensorInfoHandler>();
    public void addCallbackTo(SensorInfoHandler handler){
        handlers.add(handler);
    }

    public void removeCallbackTo(SensorInfoHandler handler){
        handlers.remove(handler);
    }

    void newData(SensorInfo info) {
        for(SensorInfoHandler handler : handlers){
            handler.newSensorInfo(info);
        }
    }
}
