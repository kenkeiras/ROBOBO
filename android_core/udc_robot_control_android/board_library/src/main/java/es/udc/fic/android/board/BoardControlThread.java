package es.udc.fic.android.board;


import android.util.Log;

/**
 * Keeps account of the updates from and to the ROBOBO board.
 *
 */
public class BoardControlThread extends Thread {

    private static final long SLEEP_TIME = 5000; // Miliseconds between each write/read cycle
    private final BoardService boardService;

    public BoardControlThread(BoardService boardService) {
        this.boardService = boardService;
    }


    @Override
    public void run() {
        try {
            while (!interrupted()) {
                // Commands have to be written for sensor data to be sent back
                // thats why they have to be sent on every iteration
                writeData();
                readData();
                sleep(SLEEP_TIME);
            }
        }
        catch (InterruptedException e){
            // Let the thread end
        }
    }


    void writeData(){
        RobotState robotState = boardService.getLastState();
        boardService.writeToConnector(robotState);
    }



    void readData(){
        Log.d(BoardConstants.TAG, "Reading sensors");
        byte[] read = boardService.readFromConnector();
        if (read != null) {
            Log.i(BoardConstants.TAG, "Read [ " + read.length + " ] bytes");
            try {
                // Parse the read data
                SensorInfo info = new SensorInfo(read);
                boardService.newData(info);

            } catch (Exception e) {
                Log.w(BoardConstants.TAG, "Error retrieving data", e);
                StringBuilder sb = new StringBuilder();
                for (int x = 0; x < read.length; x++) {
                    sb.append("byte [ " + x + " ] = (" + read[x] + ")");
                }
                Log.w(BoardConstants.TAG, "Read => " + sb.toString());
            }
        }
        else {
            Log.i(BoardConstants.TAG, "Nothing read");
        }
    }
}
