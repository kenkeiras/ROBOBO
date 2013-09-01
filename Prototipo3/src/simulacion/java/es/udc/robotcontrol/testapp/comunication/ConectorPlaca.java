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

/**
 * Esta clase simula la lectura y escritura del conector de la placa.
 *
 * Created by kerry on 4/06/13.
 */
public class ConectorPlaca implements ConectorPlacaInterface {

    public boolean escribir(Comando c) {
        if (c != null) {
            Log.i(Constantes.TAG_CONECTOR, "Enviando Comando [ " + c.toString() + " ]");
            // TODO: Enviar esto por el cable
            byte[] m = c.mensaje();
            StringBuffer sb = new StringBuffer();
            for (int x = 0; x < m.length; x++) {
                sb.append(String.valueOf(m[x]));
                sb.append(" ");
            }
            Log.i(Constantes.TAG_CONECTOR, "En el cable [ " + sb.toString() + " ]");
        }
        else {
            Log.i(Constantes.TAG_CONECTOR, "Nada que enviar");
        }
        return true;
    }

    public byte[] leer() {
        byte[] salida = new byte[22];
        salida[0] = (byte) 0x81;
        byte checksum = 0;
        for (int x = 1; x < salida.length - 1; x += 2) {
            salida[x] = 0;
            salida[x+1] = 1;
            checksum += 1;
        }
        salida[salida.length -1] = checksum;
        return salida;
    }

    @Override
    public void conectar(Context ctx, Intent intent) {
        Log.i(Constantes.TAG_CONECTOR, "Conectando a intent [ " + intent.getAction() + " ]. Modo - Simulado");
    }

    @Override
    public void conectarManual(Context ctx) throws TransmisionErrorException {
        Log.i(Constantes.TAG_CONECTOR, "Conectando Manualmente. Modo - Simulado");
    }

    @Override
    public void desconectar() {
        Log.i(Constantes.TAG_CONECTOR, "Desconectando. Modo - Simulado");
    }

}
