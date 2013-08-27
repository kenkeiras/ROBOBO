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
package es.udc.fic.android.robot_control.robot;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import es.udc.fic.android.robot_control.R;
import es.udc.fic.android.robot_control.UDCAndroidControl;
import es.udc.fic.android.robot_control.utils.C;
import udc_robot_control_java.ActionCommand;


/**
 * Servicio de comunicación con el robot
 * Se encargará de abrir la conexión y lanzar tres hilos separados. Dos para leer y escribir del robot
 * y un tercero para recibir instrucciones en remoto (http get).
 *
 * Created by kerry on 2/06/13.
 */
public class RobotCommController {

    private UDCAndroidControl ctx;
    private EstadoRobot estadoRobot;

    public boolean continuar;

    private long readSleepTime;

    private HiloLector lector;

    private ConectorPlaca conector;

    public RobotCommController(UDCAndroidControl ctx) {
        this.ctx = ctx;
        this.estadoRobot = new EstadoRobot();        
        Log.i(C.ROBOT_TAG, "Servicio robot creado");
        continuar = false;
    }

    public void iniciar(Context ctx, Intent intent) {
        Log.i(C.ROBOT_TAG, "Iniciando controlador de robot");
        try {

            if (continuar) { // YA conectado
                Log.w(C.ROBOT_TAG, "Llamado a INICIAR con un robot ya conectado");
                return;
            }
            continuar = true;
            Log.i(C.ROBOT_TAG, "Creando conector Placa");
            conector = new ConectorPlaca();
            Log.i(C.ROBOT_TAG, "Conector placa creado. Llamando a conectar");
            conector.conectar(ctx, intent);
            Log.i(C.ROBOT_TAG, "La llamada a conectar ha tenido exito");

            Toast.makeText(ctx, R.string.robot_service_started, Toast.LENGTH_SHORT).show();

            Log.i(C.ROBOT_TAG, "Creando hilo lector");
            lector = new HiloLector(this);

            Log.i(C.ROBOT_TAG, "Lanzando hilo lector");
            lector.start();
            Log.i(C.ROBOT_TAG, "Todos los hilos han sido lanzados. iniciar completado.");
        }
        catch (Exception ex) {
            Log.w(C.ROBOT_TAG, "Error iniciando conexion [ " + ex.getMessage() + " ] ", ex);
        }
    }

    public void iniciarManual() {
        try {
            if(continuar) { // Ya conectado
                Log.w(C.ROBOT_TAG, "Llamado a INICIAR MANUAL con un robot ya conectado");
                return;
            }
            Log.i(C.ROBOT_TAG, "Iniciando controlador manualmente.");
            continuar = true;
            if (conector == null) {
                conector = new ConectorPlaca();
            }
            conector.conectarManual(this.ctx);
            Toast.makeText(ctx, R.string.robot_service_manual_started, Toast.LENGTH_SHORT).show();
            lector = new HiloLector(this);
            lector.start();
        }
        catch (Exception ex) {
            Log.w(C.ROBOT_TAG, "Error iniciando conexion [ " + ex.getMessage() + " ] ", ex);
        }
    }


    public void terminar() {
        if (conector != null) {
            conector.desconectar();
        }
        continuar = false;
    }


    public synchronized boolean continuarLector(HiloLector hl) {
        return (continuar && (lector == hl));
    }


    public synchronized  byte[] leer() {
        return conector.leer();
    }

    public long getReadSleepTime() {
        return readSleepTime;
    }

    public void setReadSleepTime(long readSleepTime) {
        this.readSleepTime = readSleepTime;
    }

    public void escribir(ActionCommand comando) {
        try {
            switch (comando.getCommand()) {
                case ActionCommand.CMD_HARD_RESET:
                    terminar();
                    iniciarManual();
                    break;
                case ActionCommand.CMD_RESET:
                    estadoRobot.reset();
                    conector.escribir(estadoRobot);
                    break;
                case ActionCommand.CMD_SET_ENGINES:
                    estadoRobot.setMotores(comando.getEngines());
                    conector.escribir(estadoRobot);
                    break;
                case ActionCommand.CMD_SET_LEDS:
                    estadoRobot.setLeds(comando.getLeds());
                    conector.escribir(estadoRobot);
                    break;
            }
        }
        catch (Exception ex) {
            if (comando != null) {
                Log.e(C.ROBOT_TAG, "Error ejecutando comando [ " + comando.getCommand() + " ]", ex);
            }
            else {
                Log.e(C.ROBOT_TAG, "Error ejecutando comando. El comando es null", ex);
            }
        }
    }

    public void sendToRos(SensorInfo ss) {
        ctx.enviarRos(ss);
    }

}


class HiloLector extends Thread {

    RobotCommController padre;

    public HiloLector (RobotCommController padre) {
        this.padre = padre;
    }
    @Override
    public void run() {
        while (padre.continuarLector(this)) {
            try {
                Log.d(C.ROBOT_TAG, "Leyendo sensores");
                byte[] leido = padre.leer();
                if (leido != null) {
                    Log.i(C.ROBOT_TAG, "Leidos [ " + leido.length + " ] bytes");
                    try {
                        // Parsear la lectura
                        SensorInfo info = new SensorInfo(leido);
                        padre.sendToRos(info);
                    } catch (Exception e) {
                        Log.w("Error recuperando info", e);
                        StringBuilder sb = new StringBuilder();
                        for (int x = 0; x < leido.length; x++) {
                            sb.append("byte [ " + x + " ] = (" + leido[x] + ")");
                        }
                        Log.w(C.ROBOT_TAG, "Leido => " + sb.toString());
                    }
                }
                else {
                    Log.i(C.ROBOT_TAG, "Nada que leer");
                }
                try {
                    sleep(padre.getReadSleepTime());
                }
                catch (InterruptedException ie) {
                    Log.w(C.ROBOT_TAG, "InterruptedException en el lector");
                }
            }
            catch (Exception ex) {
                Log.w(C.ROBOT_TAG, "Exception en hilo lector", ex);
                padre.continuar = false;
            }
            // TODO: Quitar esto en función de cómo funcionen las lecturas (si es que funcionan)
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Log.w(C.ROBOT_TAG, "Error al dormir el hilo lector" + e.getMessage());
                e.printStackTrace();
            }
        }
        Log.i(C.ROBOT_TAG, "Hilo lector terminando");
    }

}
