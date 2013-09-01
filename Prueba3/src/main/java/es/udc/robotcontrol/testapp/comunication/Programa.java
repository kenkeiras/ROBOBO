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

import android.util.Log;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Esta clase contiene las utilidades necesarias para modelizar un programa y parsearlo.
 *
 *
 * Created by kerry on 2/06/13.
 */
public class Programa {

    private Vector<Comando> comandos;
    private Hashtable<String, String> parametros;


    public Programa() {
        comandos = new Vector<Comando>();
        parametros = new Hashtable<String, String>();
    }

    public static Programa construir(String[] entrada) {

        Programa salida = new Programa();

        for (int x = 0; x < entrada.length; x++) {
            String candidata = entrada[x];

            if (candidata.startsWith(Constantes.COMANDO)) {
                Comando c = new Comando(candidata);
                salida.comandos.add(c);
            }
            else {
                if(candidata.startsWith(Constantes.PARAMETRO)) {

                    String[] partes = candidata.split("#");
                    candidata = partes[0];
                    partes = candidata.split(" ");
                    String key = partes[1];
                    String value = partes[2];
                    salida.parametros.put(key, value);
                }
                else {
                    Log.w(Constantes.TAG_PROCESO, "Entrada no valida [ " + candidata + " ]");
                }
            }
        }
        return salida;
    }

    public String getReportUrl() {
        return parametros.get(Constantes.PARAM_REPORT_URL);
    }

    public boolean isLimpiarCola() {
        String value = parametros.get(Constantes.PARAM_LIMPIAR_COLA);
        if (value != null) {
            return value.equalsIgnoreCase("TRUE");
        }
        else {
            return false;
        }
    }


    public long writeSleepTime() {
        return sleepTime(Constantes.PARAM_WRITE_SLEEP_TIME);
    }

    public long readSleepTime() {
        return sleepTime(Constantes.PARAM_READ_SLEEP_TIME);
    }

    private long sleepTime(String key) {
        String value = parametros.get(key);
        if (value != null) {
            return Long.parseLong(value);
        }
        else {
            return Constantes.PARAM_DEFAULT_SLEEP_TIME;
        }
    }


    public Vector<Comando> listaComandos() {
        return comandos;
    }

}
