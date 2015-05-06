package es.udc.fic.android.robot_control.audio;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

import es.udc.fic.android.robot_control.utils.C;


public class SpeechRecognitionService
    extends Service
    implements RecognitionListener {

    private SpeechRecognitionPublisher publisher = null;
    private SpeechRecognizer speechRecognizer = null;
    private Intent serviceIntent = null;
    private final IBinder sBinder = (IBinder) new SimpleBinder();
    private final int SLEEP_TIME = 1500; // In millis

    class SimpleBinder extends Binder {
        SpeechRecognitionService getService() {
            return SpeechRecognitionService.this;
        }
    }

    private void initService(){
        if (serviceIntent == null){
            stopSelf();
            return;
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(this);
        speechRecognizer.startListening(serviceIntent);
        Log.d(C.SPEECH_RECOG_TAG, "Listening started through " + serviceIntent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(C.SPEECH_RECOG_TAG, "onStartCommand");
        serviceIntent = intent;
        initService();
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.d(C.SPEECH_RECOG_TAG, "onBind");
        return sBinder;
    }


    public void setPublisher(SpeechRecognitionPublisher publisher){
        this.publisher = publisher;
    }


    private void waitAndListen(){
        try {
            Thread.sleep(SLEEP_TIME);
        }
        catch (InterruptedException e){}

        speechRecognizer.startListening(serviceIntent);
    }


    /** Gets the first String from the results and passes it to the callback.
     *
     * @param results
     */
    public void onResults(Bundle results) {
        Log.d(C.SPEECH_RECOG_TAG, "onResults");
        ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String speech = data.get(0);
        if (publisher != null){
            publisher.publish(speech);
        }
        waitAndListen();
    }


    /**
     * If error happens just call Service.onError()
     *
     * @param error
     */
    public void onError(int error) {
        Log.d(C.SPEECH_RECOG_TAG, "Error " + error);
        waitAndListen();
    }


    public void onReadyForSpeech(Bundle params) {
        Log.d(C.SPEECH_RECOG_TAG, "onReadyForSpeech (Audio ON)");
    }


    public void onEndOfSpeech() {
        Log.d(C.SPEECH_RECOG_TAG, "onEndOfSpeech");
    }


    public void onBeginningOfSpeech() {
        Log.d(C.SPEECH_RECOG_TAG, "onBeggining");
    }


    public void onPartialResults(Bundle partialResults) {
        Log.d(C.SPEECH_RECOG_TAG, "onPartialResults");
    }


    public void onEvent(int eventType, Bundle params) {
        Log.d(C.SPEECH_RECOG_TAG, "onEvent " + eventType);
    }


    public void onRmsChanged(float rmsdB) {
    }


    public void onBufferReceived(byte[] buffer) {
    }
}
