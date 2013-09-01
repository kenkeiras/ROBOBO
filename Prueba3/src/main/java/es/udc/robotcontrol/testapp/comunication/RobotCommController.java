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
package es.udc.robotcontrol.testapp.comunication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import es.udc.robotcontrol.testapp.R;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Servicio de comunicación con el robot
 * Se encargará de abrir la conexión y lanzar tres hilos separados. Dos para leer y escribir del robot
 * y un tercero para recibir instrucciones en remoto (http get).
 *
 * Created by kerry on 2/06/13.
 */
public class RobotCommController {

    private Context ctx;
    public boolean continuar;

    private ConcurrentLinkedQueue<Comando> comandos;

    private long writeSleepTime;
    private long readSleepTime;
    private String reportUrl;

    private HiloEscritor escritor;
    private HiloLector lector;

    private ConectorPlaca conector;

    public RobotCommController(Context ctx) {
        this.ctx = ctx;
        Log.i(Constantes.TAG_SERVICIO, "Servicio robot creado");
        comandos = new ConcurrentLinkedQueue<Comando>();
        setReadSleepTime(1000);
        setWriteSleepTime(1000);
        Toast.makeText(ctx, R.string.robot_service_created, Toast.LENGTH_SHORT).show();
    }

    public void iniciar(Context ctx, Intent intent) {
        Log.i(Constantes.TAG_SERVICIO, "Iniciando controlador de robot");
        try {

            continuar = true;
            Log.i(Constantes.TAG_SERVICIO, "Creando conector Placa");
            conector = new ConectorPlaca();
            Log.i(Constantes.TAG_SERVICIO, "Conector placa creado. Llamando a conectar");
            conector.conectar(ctx, intent);
            Log.i(Constantes.TAG_SERVICIO, "La llamada a conectar ha tenido exito");

            Toast.makeText(ctx, R.string.robot_service_started, Toast.LENGTH_SHORT).show();

            Log.i(Constantes.TAG_SERVICIO, "Creando hilo escritor");
            escritor = new HiloEscritor(this);
            Log.i(Constantes.TAG_SERVICIO, "Creando hilo lector");
            lector = new HiloLector(this);

            Log.i(Constantes.TAG_SERVICIO, "Lanzando hilo lector");
            lector.start();
            Log.i(Constantes.TAG_SERVICIO, "Lanzando hilo escritor");
            escritor.start();
            Log.i(Constantes.TAG_SERVICIO, "Todos los hilos han sido lanzados. iniciar completado.");
        }
        catch (Exception ex) {
            Log.w(Constantes.TAG_SERVICIO, "Error iniciando conexion [ " + ex.getMessage() + " ] ", ex);
            Toast.makeText(ctx, R.string.robot_service_error_connection, Toast.LENGTH_SHORT).show();

        }
    }

    public void iniciarManual() {
        try {
            Log.i(Constantes.TAG_SERVICIO, "Iniciando controlador manualmente. No habrá conexión");

            continuar = true;

            if (conector == null) {
                conector = new ConectorPlaca();
            }

            conector.conectarManual(this.ctx);
            Toast.makeText(ctx, R.string.robot_service_manual_started, Toast.LENGTH_SHORT).show();

            escritor = new HiloEscritor(this);
            lector = new HiloLector(this);

            lector.start();
            escritor.start();
        }
        catch (Exception ex) {
            Log.w(Constantes.TAG_SERVICIO, "Error iniciando conexion [ " + ex.getMessage() + " ] ", ex);
            String res = ctx.getResources().getString(R.string.robot_service_error_connection);
            String txt = String.format(res, ex.getMessage());
            Toast.makeText(ctx, txt, Toast.LENGTH_SHORT).show();
        }
    }


    public void terminar() {
        if (conector != null) {
            conector.desconectar();
        }
        continuar = false;
    }
    /**
     * Este metodo se utiliza para cargar la lista de comandos a enviar al móvil
     * Hará un parsing de la lista y la enviará a la placa según esté configurado
     *
     * @param list
     */
    public void cargarPrograma(String[] list) {
        Programa p = Programa.construir(list);

        if (p.getReportUrl() != null) {
            setReportUrl(p.getReportUrl());
        }

        // Limpiamos la cola de envios
        if (p.isLimpiarCola()) {
            comandos.clear();
        }

        // Fijamos los tiempos para dormir los hilos trabajadores
        setReadSleepTime(p.readSleepTime());
        setWriteSleepTime(p.writeSleepTime());

        // Añadimos los nuevos comandos
        comandos.addAll(p.listaComandos());
    }


    public synchronized Comando getNextComando() {
        return comandos.poll();
    }

    public synchronized boolean continuarEscritor(HiloEscritor hs) {
        return (continuar && (escritor == hs));
    }

    public synchronized boolean continuarLector(HiloLector hl) {
        return (continuar && (lector == hl));
    }

    public void enviarComando(Comando c) {
        conector.escribir(c);
    }

    public synchronized  byte[] leer() {
        return conector.leer();
    }

    public long getWriteSleepTime() {
        return writeSleepTime;
    }

    public void setWriteSleepTime(long writeSleepTime) {
        this.writeSleepTime = writeSleepTime;
    }

    public long getReadSleepTime() {
        return readSleepTime;
    }

    public void setReadSleepTime(long readSleepTime) {
        this.readSleepTime = readSleepTime;
    }


    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }
}


class HiloEscritor extends Thread {

    RobotCommController padre;

    public HiloEscritor(RobotCommController padre) {
        this.padre = padre;
    }
    @Override
    public void run() {
        while (padre.continuarEscritor(this)) {
            Comando c = padre.getNextComando();
            padre.enviarComando(c);
            try {
                sleep(padre.getWriteSleepTime());
            }
            catch (InterruptedException ie) {
                Log.w(Constantes.TAG_SERVICIO, "InterruptedException en el escritor");
            }
            catch (Exception ex) {
                Log.w(Constantes.TAG_SERVICIO, "Exception en hilo escritor ", ex);
                padre.continuar = false;
            }
        }
        Log.i(Constantes.TAG_SERVICIO, "Hilo escritor terminando");
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
                byte[] leido = padre.leer();
                if (leido != null) {
                    Log.i(Constantes.TAG_SERVICIO, "Leidos [ " + leido.length + " ] bytes");
                    try {
                        // Parsear la lectura
                        SensorInfo info = new SensorInfo(leido);
                        // Publicarla
                        String base = padre.getReportUrl();
                        if (base != null) {
                            base = base + info.toUrlFormat();
                            HttpClient httpClient = new DefaultHttpClient();
                            HttpGet get = new HttpGet(base);
                            get.setHeader("content-type", "application/json");
                                httpClient.execute(get);
                        }
                    } catch (Exception e) {
                        Log.w("Error informando", e);
                        StringBuilder sb = new StringBuilder();
                        for (int x = 0; x < leido.length; x++) {
                            sb.append("byte [ " + x + " ] = (" + leido[x] + ")");
                        }
                        Log.w(Constantes.TAG_SERVICIO, "Leido => " + sb.toString());
                    }
                }
                else {
                    Log.i(Constantes.TAG_SERVICIO, "Nada que leer");
                }
                try {
                    sleep(padre.getReadSleepTime());
                }
                catch (InterruptedException ie) {
                    Log.w(Constantes.TAG_SERVICIO, "InterruptedException en el lector");
                }
            }
            catch (Exception ex) {
                Log.w(Constantes.TAG_SERVICIO, "Exception en hilo lector", ex);
                padre.continuar = false;
            }
        }

        Log.i(Constantes.TAG_SERVICIO, "Hilo lector terminando");
    }

}
