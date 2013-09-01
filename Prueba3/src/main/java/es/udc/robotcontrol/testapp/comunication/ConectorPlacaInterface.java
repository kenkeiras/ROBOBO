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

/**
 * Created by kerry on 6/06/13.
 */
public interface ConectorPlacaInterface {
    public boolean escribir(Comando c);
    public byte[] leer();
    public void conectar(Context ctx, Intent intent) throws TransmisionErrorException;
    public void conectarManual(Context ctx) throws TransmisionErrorException;
    public void desconectar();
}
