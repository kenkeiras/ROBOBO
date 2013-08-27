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

package net.adiaz.prueba3;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import net.adiaz.prueba3.comunication.Constantes;
import net.adiaz.prueba3.comunication.RobotCommController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Vector;

public class MainActivity extends Activity {


    private RobotCommController robot;

    LinearLayout imgcontainer;
    EditText textoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        imgcontainer = (LinearLayout) findViewById(R.id.imgContainer);
        textoUrl = (EditText) findViewById(R.id.editText1);

        setOff(imgcontainer);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

    	/* Check to see if it was a USB device attach that caused the app to
    	 * start or if the user opened the program manually.
    	 */
        Intent intent = getIntent();
        String action = intent.getAction();

        if (robot == null) {
            robot = new RobotCommController(this);
        }

        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            robot.iniciar(this, intent);
        }
        else {
            // Ha sido arrancada manualmente

        }

        //Registramos para escuchar los eventos USB
        IntentFilter filter = new IntentFilter();

        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);

        registerReceiver(receiver, filter);

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        if (robot != null) {
            robot.terminar();
        }
    }

    // Encendemos los leds de la pantalla
    public void setOn(View v) {
        int max = imgcontainer.getChildCount();
        for (int x = 0; x < max; x++) {
            encender(x);
        }
    }

    public void setOff(View v) {
        int max = imgcontainer.getChildCount();
        for (int x = 0; x < max; x++) {
            apagar(x);
        }
    }

    public void iniciarManual(View v) {
        if (robot == null) {
            robot = new RobotCommController(this);
        }
        robot.iniciarManual();
    }


    public void apagar(View v) {
        if (robot != null) {
            robot.terminar();
        }
    }


    public void stepLed() {
        int max = imgcontainer.getChildCount();
        for (int x = 0; x < max; x++) {
            if (!encendido(x)) {
                encender(x);
                return;
            }
        }
    }

    protected void encender(int x) {
        ImageView imgv = (ImageView) imgcontainer.getChildAt(x);
        imgv.setImageResource(R.drawable.encendido);
        imgv.setTag(Boolean.TRUE);
    }

    private void apagar(int x) {
        ImageView imgv = (ImageView) imgcontainer.getChildAt(x);
        imgv.setImageResource(R.drawable.apagado);
        imgv.setTag(Boolean.FALSE);
    }

    private boolean encendido(int x) {
        ImageView imgv = (ImageView) imgcontainer.getChildAt(x);
        Boolean tag = (Boolean) imgv.getTag();
        return tag.booleanValue();
    }

    public void onClick(View v) {
        setOff(v);
        Log.i(Constantes.TAG_PROCESO, "Comenzando descarga");
        stepLed();

        // Descargamos el fichero
        String URL = textoUrl.getText().toString();
        new DownloaderTask().execute(URL);

        // Lanzamos el servicio

    }


    /**
     * New BroadcastReceiver object that will handle all of the USB device
     * attach and detach events.
     */
    BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
    		/* Get the information about what action caused this event */
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Log.i(Constantes.TAG_PROCESO, "Dispositivo desconectado");
                // Se ha desconectado un dispositivo.
                if (robot != null) {
                    robot.terminar();
                }
            }

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                // Se ha conectado un dispositivo
                if (robot != null) {
                    Log.i(Constantes.TAG_PROCESO, "Dispositivo conectado");
                    robot.iniciar(context, intent);
                }
            }

        }
    };


    private class DownloaderTask extends AsyncTask<String, Integer, String[]> {
        @Override
        protected String[] doInBackground(String... url) {
            Vector<String> tmp = new Vector<String>();

            try {
                Log.i(Constantes.TAG_DESCARGA, "Descargando [ " + url + " ]");
                URL u = new URL(url[0] + "comando");
                Log.i(Constantes.TAG_DESCARGA, "La URL es valida [ " + u.toExternalForm() + " ]");
                publishProgress(1);

                BufferedReader in = new BufferedReader(new InputStreamReader(u.openStream()));
                String str;
                while ((str = in.readLine()) != null) {
                    Log.i(Constantes.TAG_DESCARGA, "Leido [ " + str + " ]");
                    tmp.add(str);
                }
                in.close();
                publishProgress(2);

            } catch (Exception ex) {
                Log.w(Constantes.TAG_DESCARGA, "Error en descarga");
            }
            String[] res = new String[tmp.size()];
            return tmp.toArray(res);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            stepLed();
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            Log.i(Constantes.TAG_DESCARGA, "Fichero descargado");
            if (robot != null) {
                robot.cargarPrograma(strings);
            }
        }
    }
}