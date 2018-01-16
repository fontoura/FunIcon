/*
Copyright (c) 2010, FunIcon Team
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

import java.util.concurrent.TimeoutException;

/**
 * Descritor de um comunicador genérico.
 * @author Felipe Michels Fontoura
 */
public interface Comunicador {
    /**
     * Envia um vetor de bytes pelo canal. Bloqueia a thread que chamar esse método
     * até o envio estar completo.
     * @param bytes Vetor de bytes a enviar no canal.
     * @param offset Posição inicial a ler no vetor.
     * @param tamanho  Tamanho do vetor de bytes a enviar no canal.
     */
    public void enviarDados(byte[] bytes, int offset, int tamanho);

    /**
     * Recebe um byte do canal. Bloqueia a thread que chamar esse método até
     * o recebimento acontecer ou até um determinado tempo passar.
     * @param timeout_ms Tempo de espera máximo.
     * @return Byte recebido do canal.
     * @throws TimeoutException Caso o tempo de espera máximo passe antes do
     * recebimento do byte.
     */
    public byte receberByte(int timeout_ms) throws TimeoutException;

    /**
     * Desconecta, e libera os recursos físicos, desse comunicador.
     */
    public void desconectar();
}
