/*
Copyright (c) 2010, Felipe Michels Fontoura
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list
      of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this
      list of conditions and the following disclaimer in the documentation and/or
      other materials provided with the distribution.
    * Neither the name of Universidade Tecnológica Federal do Paraná nor the names of its
      contributors may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package comunicacao;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import java.util.concurrent.TimeoutException;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

/**
 * Classe de comunicação pela porta serial.
 * @author Felipe Michels Fontoura
 */
@SuppressWarnings("rawtypes")
public class ComunicadorSerial implements Comunicador {
    /**
     * Fluxo de entrada de dados pela serial.
     */
    private InputStream fluxo_entrada;

    /**
     * Fluxo de saída de dados pela serial.
     */
    private OutputStream fluxo_saida;

    /**
     * Objeto descritor da porta serial.
     */
    private SerialPort porta_serial;

    /**
     * Instancia um novo comunicador serial com baud rate de 57600 por segundo.
     * @param nome_porta Nome da porta serial.
     * @throws NoSuchPortException Se a porta serial desejada não existe.
     * @throws PortInUseException Se a porta serial desejada está em uso.
     * @throws UnsupportedCommOperationException Se a operação desejada não é suportada.
     * @throws IOException Caso não consiga abrir os fluxos de escrita e leitura pela serial.
     */
    public ComunicadorSerial(String nome_porta) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException {
        // obtém o controle da porta serial.
        CommPortIdentifier identificador = CommPortIdentifier.getPortIdentifier(nome_porta);
        porta_serial = (SerialPort) identificador.open("FunIconGUI", 5000);

        // configura a porta serial.
        try {
            porta_serial.setSerialPortParams(
              /*     baud rate: */ 57600,
              /* bits de dados: */ SerialPort.DATABITS_8,
              /*     stop bits: */ SerialPort.STOPBITS_1,
              /*      paridade: */ SerialPort.PARITY_NONE
            );

        } catch (UnsupportedCommOperationException ucoe) {
            porta_serial.close();
            throw ucoe;
        }

        // obtém os fluxos de entrada e saída.
        try {
            fluxo_entrada = porta_serial.getInputStream();
            fluxo_saida = porta_serial.getOutputStream();
        } catch (IOException ioe) {
            porta_serial.close();
            throw ioe;
        }
    }

    /**
     * Obtém um vetor com os nomes das portas seriais disponíveis nesse computador.
     * @return Vetor com os nomes das portas seriais.
     */
    public static String[] listarSeriais() {
        // obtém a enumeração das portas.
        Enumeration portas = CommPortIdentifier.getPortIdentifiers();

        // cria uma lista com o nome das portas.
        List<String> lista_portas = new ArrayList<String>();
        String lista_nomes_ports[] = null;

        // adiciona os elementos da enumeração à lista.
        while (portas.hasMoreElements()) {
            CommPortIdentifier port = (CommPortIdentifier) portas.nextElement();
            if (port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                lista_portas.add(port.getName()); }
        }

        // converte a lista em vetor e retorna.
        lista_nomes_ports = lista_portas.toArray(new String[0]);
        return lista_nomes_ports;
    }

    @Override public byte receberByte(int timeout_ms) throws TimeoutException {
        try {
            if (fluxo_entrada.available() > 0) return (byte) fluxo_entrada.read();
            long ini = System.currentTimeMillis();
            while (System.currentTimeMillis() - ini < timeout_ms) {
                if (fluxo_entrada.available() > 0) return (byte) fluxo_entrada.read();
            }
        } catch (IOException ioe) {};
        throw new TimeoutException(timeout_ms + " ms");
    }

    @Override public void enviarDados(byte[] bytes, int offset, int tamanho) {
        try {
            if (fluxo_saida != null) {
                fluxo_saida.write(bytes, offset, tamanho);
                fluxo_saida.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override public void desconectar() {
        if (porta_serial != null) {
            porta_serial.close();
            fluxo_saida = null;
            fluxo_entrada = null;
        }
    }
}
